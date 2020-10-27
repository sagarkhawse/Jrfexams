package com.skteam.jrfexams.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;

import com.skteam.jrfexams.R;
import com.skteam.jrfexams.adapters.QuizAdapter;
import com.skteam.jrfexams.common.Common;
import com.skteam.jrfexams.databinding.ActivityMCQBinding;
import com.skteam.jrfexams.interfaces.IQuizListener;
import com.skteam.jrfexams.models.Quiz;
import com.skteam.jrfexams.retrofit.RetrofitApi;
import com.skteam.jrfexams.utils.Constants;
import com.skteam.jrfexams.utils.Functions;
import com.skteam.jrfexams.utils.Variables;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MCQActivity extends AppCompatActivity implements IQuizListener {
    private static final String TAG = "MCQActivityTest";
    private Activity activity;
    private ActivityMCQBinding binding;
    private RetrofitApi mService;

    private List<Quiz> list;
    private QuizAdapter adapter;

    //summary detail
    private int total_attempted = 0;
    private int correct_answer = 0;
    private int incorrect_answer = 0;

    //timer
    private CountDownTimer mCountDownTimer;
    private long mStartTimeInMillis;
    private long mTimeLeftInMillis;
    private long mEndTime;

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMCQBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        activity = this;
        mService = Common.getAPI();
        list = new ArrayList<>();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            initQuiz(bundle.getInt("date_id"));
        }
        binding.ivQuestions.setImageDrawable(getDrawable(R.drawable.ic_select_question));
        binding.tvQues.setTextColor(getResources().getColor(R.color.colorRed));
        binding.rlQue.setOnClickListener(view1 -> {
            binding.ivQuestions.setImageDrawable(getDrawable(R.drawable.ic_select_question));
            binding.tvQues.setTextColor(getResources().getColor(R.color.colorRed));
            binding.ivSummary.setImageDrawable(getDrawable(R.drawable.ic_unselect_summary));
            binding.tvSummary.setTextColor(Color.parseColor("#000000"));
            binding.mlSummary.setVisibility(View.GONE);
            binding.recyclerview.setVisibility(View.VISIBLE);
//            binding.btnResetResponse.setVisibility(View.GONE);
        });
        binding.rlSummary.setOnClickListener(view12 -> {
            binding.ivSummary.setImageDrawable(getDrawable(R.drawable.ic_select_summary));
            binding.tvSummary.setTextColor(getResources().getColor(R.color.colorRed));
            binding.ivQuestions.setImageDrawable(getDrawable(R.drawable.ic_unselect_question));
            binding.tvQues.setTextColor(Color.parseColor("#000000"));
            binding.mlSummary.setVisibility(View.VISIBLE);
            binding.recyclerview.setVisibility(View.GONE);
//            binding.btnResetResponse.setVisibility(View.VISIBLE);


            //set summary data
            binding.tvTotalQue.setText(String.valueOf(Variables.quizQuestionsList.size()));
            binding.tvAttempted.setText(String.valueOf(total_attempted));
            binding.tvCorrectAnswer.setText(String.valueOf(correct_answer));
            binding.tvInCorrectAnswer.setText(String.valueOf(incorrect_answer));

        });

        binding.ivBack.setOnClickListener(view13 -> onBackPressed());
    }


    private void initQuiz(int date_id) {
        list.clear();
        mService.quiz(date_id)
                .enqueue(new Callback<Quiz>() {
                    @Override
                    public void onResponse(@NonNull Call<Quiz> call, @NonNull Response<Quiz> response) {
                        if (response.body() != null) {
                            if (response.body().code.equals(Constants.SUCCESS)) {
                                list = response.body().res;
                                setRecyclerView();
                                Variables.quizQuestionsList = list;
                            } else {
                                Functions.ShowToast(activity, "" + response.body().error_msg);
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<Quiz> call, @NonNull Throwable t) {
                        Log.d(TAG, "onFailure: " + t.getMessage());
                    }
                });
    }


    private void setRecyclerView() {
        binding.recyclerview.setHasFixedSize(true);
        binding.recyclerview.setLayoutManager(new LinearLayoutManager(activity));
        adapter = new QuizAdapter(activity, list,this);
        binding.recyclerview.setAdapter(adapter);
        binding.progressBar.setVisibility(View.GONE);
        mTimeLeftInMillis = 3000000;
        startTimer();
    }



    /**
     * Quiz listener methods
     */
    @Override
    public void onAnswerAttempted() {
        total_attempted++;
    }

    @Override
    public void onCorrectAnswer() {
        correct_answer++;
    }

    @Override
    public void onInCorrectAnswer() {
        incorrect_answer++;
    }


    /**
     * Timer methods
     */
    //TIME Text
    private void startTimer() {
        mEndTime = System.currentTimeMillis() + mTimeLeftInMillis;
        mCountDownTimer = new CountDownTimer(mTimeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMillis = millisUntilFinished;
                updateCountDownText();
            }

            @Override
            public void onFinish() {

            }
        }.start();
    }

    @SuppressLint("SetTextI18n")
    private void updateCountDownText() {
        int minutes = (int) ((mTimeLeftInMillis / 1000) % 3600) / 60;
        int seconds = (int) (mTimeLeftInMillis / 1000) % 60;
        String timeLeftFormatted;

        timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);

        binding.time.setText(timeLeftFormatted);
    }


}