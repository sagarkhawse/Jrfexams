package com.virtualmind.jrfexams.adapters;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.virtualmind.jrfexams.R;
import com.virtualmind.jrfexams.common.Common;
import com.virtualmind.jrfexams.databinding.ItemQuizBinding;
import com.virtualmind.jrfexams.interfaces.IQuizListener;
import com.virtualmind.jrfexams.models.Quiz;
import com.virtualmind.jrfexams.retrofit.RetrofitApi;
import com.virtualmind.jrfexams.utils.Animations;
import com.virtualmind.jrfexams.utils.Constants;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QuizAdapter extends RecyclerView.Adapter<QuizAdapter.DataViewHolder> {
    String checkAns = "";
    String selectedOption = "";
    int solvedPosition = -1;


    private Context context;
    private List<Quiz> list;
    private IQuizListener listener;



    public QuizAdapter(Context context, List<Quiz> list, IQuizListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }


    @NonNull
    @Override
    public DataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemQuizBinding binding = ItemQuizBinding.inflate(LayoutInflater.from(context), parent, false);
        return new DataViewHolder(binding);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull DataViewHolder holder, int position) {
        Quiz data = list.get(position);


        holder.binding.tvQue.setText(data.question);
        holder.binding.tvOpt1.setText("(A) " + data.option1);
        holder.binding.tvOpt2.setText("(B) " + data.option2);
        holder.binding.tvOpt3.setText("(C) " + data.option3);
        holder.binding.tvOpt4.setText("(D) " + data.option4);
        holder.binding.tvNumbering.setText(String.valueOf(position + 1));


        holder.binding.llOptA.setOnClickListener(view -> {
            if (position != solvedPosition) {
                selectedOption = holder.binding.tvA.getText().toString();
                setBackground(holder.binding.llOptA);
                unsetBackground(holder.binding.llOptB);
                unsetBackground(holder.binding.llOptC);
                unsetBackground(holder.binding.llOptD);
                selectedView(holder.binding.tv1);
                unSelectedView(holder.binding.tv2);
                unSelectedView(holder.binding.tv3);
                unSelectedView(holder.binding.tv4);
                submitAnswer(data,holder,position);

            }


        });
        holder.binding.llOptB.setOnClickListener(view -> {
            if (position != solvedPosition) {

                selectedOption = holder.binding.tvB.getText().toString();
                unsetBackground(holder.binding.llOptA);
                setBackground(holder.binding.llOptB);
                unsetBackground(holder.binding.llOptC);
                unsetBackground(holder.binding.llOptD);
                unSelectedView(holder.binding.tv1);
                selectedView(holder.binding.tv2);
                unSelectedView(holder.binding.tv3);
                unSelectedView(holder.binding.tv4);
                submitAnswer(data,holder,position);
            }
        });
        holder.binding.llOptC.setOnClickListener(view -> {
            if (position != solvedPosition) {

                selectedOption = holder.binding.tvC.getText().toString();
                unsetBackground(holder.binding.llOptA);
                unsetBackground(holder.binding.llOptB);
                setBackground(holder.binding.llOptC);
                unsetBackground(holder.binding.llOptD);
                unSelectedView(holder.binding.tv1);
                unSelectedView(holder.binding.tv2);
                selectedView(holder.binding.tv3);
                unSelectedView(holder.binding.tv4);
                submitAnswer(data,holder,position);
            }
        });
        holder.binding.llOptD.setOnClickListener(view -> {
            if (position != solvedPosition) {

                selectedOption = holder.binding.tvD.getText().toString();
                unsetBackground(holder.binding.llOptA);
                unsetBackground(holder.binding.llOptB);
                unsetBackground(holder.binding.llOptC);
                setBackground(holder.binding.llOptD);
                unSelectedView(holder.binding.tv1);
                unSelectedView(holder.binding.tv2);
                unSelectedView(holder.binding.tv3);
                selectedView(holder.binding.tv4);
                submitAnswer(data,holder,position);

            }

        });


//        for admin  only
//        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View view) {
//             MaterialAlertDialogBuilder builder =   new MaterialAlertDialogBuilder(context)
//                        .setMessage("delete karna hai kya bhai isko?")
//                        .setPositiveButton("Ha bhai krde", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
//
//                                add_api(data.id);
//                                dialogInterface.dismiss();
//
//                            }
//                        })
//                        .setNegativeButton("Nahi yr maat kr", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
//                                dialogInterface.dismiss();
//                            }
//                        });
//                Dialog dialog = builder.create();
//                dialog.show();
//                return false;
//            }
//        });


    }

    private void submitAnswer(Quiz data, DataViewHolder holder, int position){
        checkAns = data.answer; // answer contain correct Answer a, b, c or d
        // checkAns a, b, c or d = selectedOption A. B, C or D

        if (selectedOption.equalsIgnoreCase(checkAns)) {
            correctAnswerSelected(selectedOption, holder);
            listener.onCorrectAnswer();

        } else {
            wrongAnswerSelected(selectedOption, holder);
            listener.onInCorrectAnswer();

        }
        solvedPosition = position;
        listener.onAnswerAttempted();
    }

    private void wrongAnswerSelected(String selectedOption, DataViewHolder holder) {
        holder.binding.btnSubmit.setVisibility(View.GONE);

        //setting you missed green color bg correct answer
        if (holder.binding.tvA.getText().toString().equalsIgnoreCase(checkAns)) {
            setCorrectAns(holder.binding.tv1);
            setCorrectAnsBg(holder.binding.llOptA);
            holder.binding.tvStatus.setText(context.getString(R.string.you_missed));
            holder.binding.tvStatus.setVisibility(View.VISIBLE);
        } else if (holder.binding.tvB.getText().toString().equalsIgnoreCase(checkAns)) {
            setCorrectAns(holder.binding.tv2);
            setCorrectAnsBg(holder.binding.llOptB);
            holder.binding.tvStatus2.setText(context.getString(R.string.you_missed));
            holder.binding.tvStatus2.setVisibility(View.VISIBLE);
        } else if (holder.binding.tvC.getText().toString().equalsIgnoreCase(checkAns)) {
            setCorrectAns(holder.binding.tv3);
            setCorrectAnsBg(holder.binding.llOptC);
            holder.binding.tvStatus3.setText(context.getString(R.string.you_missed));
            holder.binding.tvStatus3.setVisibility(View.VISIBLE);

        } else if (holder.binding.tvD.getText().toString().equalsIgnoreCase(checkAns)) {
            setCorrectAns(holder.binding.tv4);
            setCorrectAnsBg(holder.binding.llOptD);
            holder.binding.tvStatus4.setText(context.getString(R.string.you_missed));
            holder.binding.tvStatus4.setVisibility(View.VISIBLE);
        }


        //setting red color bg wrong answer you marked
        if (selectedOption.equalsIgnoreCase(holder.binding.tvA.getText().toString())) {
            setInCorrectAns(holder.binding.tv1);
            setInCorrectAnsBg(holder.binding.llOptA);
            holder.binding.tvStatus.setText(context.getString(R.string.you_marked));
            holder.binding.tvStatus.setVisibility(View.VISIBLE);
        } else if (selectedOption.equalsIgnoreCase(holder.binding.tvB.getText().toString())) {
            setInCorrectAns(holder.binding.tv2);
            setInCorrectAnsBg(holder.binding.llOptB);
            holder.binding.tvStatus2.setText(context.getString(R.string.you_marked));
            holder.binding.tvStatus2.setVisibility(View.VISIBLE);
        } else if (selectedOption.equalsIgnoreCase(holder.binding.tvC.getText().toString())) {
            setInCorrectAns(holder.binding.tv3);
            setInCorrectAnsBg(holder.binding.llOptC);
            holder.binding.tvStatus3.setText(context.getString(R.string.you_marked));
            holder.binding.tvStatus3.setVisibility(View.VISIBLE);
        } else if (selectedOption.equalsIgnoreCase(holder.binding.tvD.getText().toString())) {
            setInCorrectAns(holder.binding.tv4);
            setInCorrectAnsBg(holder.binding.llOptD);
            holder.binding.tvStatus4.setText(context.getString(R.string.you_marked));
            holder.binding.tvStatus4.setVisibility(View.VISIBLE);
        }
    }

    private void correctAnswerSelected(String selectedAnswer, DataViewHolder holder) {
        holder.binding.btnSubmit.setVisibility(View.GONE);

        if (holder.binding.tvA.getText().toString().equalsIgnoreCase(selectedAnswer)) {
            setCorrectAns(holder.binding.tv1);
            setCorrectAnsBg(holder.binding.llOptA);
            holder.binding.tvStatus.setText(context.getString(R.string.well_done));
            holder.binding.tvStatus.setVisibility(View.VISIBLE);
        } else if (holder.binding.tvB.getText().toString().equalsIgnoreCase(selectedAnswer)) {
            setCorrectAns(holder.binding.tv2);
            setCorrectAnsBg(holder.binding.llOptB);
            holder.binding.tvStatus2.setText(context.getString(R.string.well_done));
            holder.binding.tvStatus2.setVisibility(View.VISIBLE);
        } else if (holder.binding.tvC.getText().toString().equalsIgnoreCase(selectedAnswer)) {
            setCorrectAns(holder.binding.tv3);
            setCorrectAnsBg(holder.binding.llOptC);
            holder.binding.tvStatus3.setText(context.getString(R.string.well_done));
            holder.binding.tvStatus3.setVisibility(View.VISIBLE);

        } else if (holder.binding.tvD.getText().toString().equalsIgnoreCase(selectedAnswer)) {
            setCorrectAns(holder.binding.tv4);
            setCorrectAnsBg(holder.binding.llOptD);
            holder.binding.tvStatus4.setText(context.getString(R.string.well_done));
            holder.binding.tvStatus4.setVisibility(View.VISIBLE);
        }

    }


    public void setCorrectAnsBg(View view) {
        view.setBackgroundColor(Color.parseColor("#93FA69"));
        Animations.bounce(context, view);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public void setCorrectAns(View view) {
        view.setBackgroundDrawable(context.getDrawable(R.drawable.textview_opt_round_correct));
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public void setInCorrectAnsBg(View view) {
        view.setBackgroundColor(Color.parseColor("#FEB7AD"));
        Animations.shake(context, view);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public void setInCorrectAns(View view) {
        view.setBackgroundDrawable(context.getDrawable(R.drawable.textview_opt_round_incorrect));

    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public void selectedView(View view) {
        view.setBackgroundDrawable(context.getDrawable(R.drawable.textview_opt_round_selected));
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public void unSelectedView(View view) {
        view.setBackgroundDrawable(context.getDrawable(R.drawable.textview_opt_round));
    }

    public void hideButton(MaterialButton button) {
        button.setVisibility(View.GONE);
    }

    public void viewButton(MaterialButton button) {
        button.setVisibility(View.VISIBLE);
    }

    public void setBackground(View view) {
        view.setBackgroundColor(Color.parseColor("#B8CEF0"));
    }

    public void unsetBackground(View view) {
        view.setBackgroundColor(Color.parseColor("#FFFFFF"));
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    static class DataViewHolder extends RecyclerView.ViewHolder {
        ItemQuizBinding binding;

        public DataViewHolder(@NonNull ItemQuizBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }















    private void add_api(int id) {
        ProgressDialog pd = new ProgressDialog(context);
        pd.setTitle("Deleting Question");
        pd.setMessage("please wait...");
        pd.setCancelable(false);
        pd.show();
        RetrofitApi mService = Common.getAPI();
        mService.admin_add_question("delete",id,0,"","","","","","")
                .enqueue(new Callback<Quiz>() {
                    @Override
                    public void onResponse(Call<Quiz> call, Response<Quiz> response) {
                        if (response.body().code.equals(Constants.SUCCESS)){
                            Toast.makeText(context, "question deleted success", Toast.LENGTH_SHORT).show();
                        }
                        pd.dismiss();
                    }

                    @Override
                    public void onFailure(Call<Quiz> call, Throwable t) {
                        pd.dismiss();
                        Toast.makeText(context, ""+t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
