package com.virtualmind.jrfexams.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.virtualmind.jrfexams.common.Common;
import com.virtualmind.jrfexams.databinding.ActivityAdminBinding;
import com.virtualmind.jrfexams.databinding.ActivityMCQBinding;
import com.virtualmind.jrfexams.models.Date;
import com.virtualmind.jrfexams.models.Quiz;
import com.virtualmind.jrfexams.retrofit.RetrofitApi;
import com.virtualmind.jrfexams.utils.Constants;
import com.virtualmind.jrfexams.utils.Functions;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminActivity extends AppCompatActivity {
    private static final String TAG = "AdminActivityTest";
    private Activity activity;
    private ActivityAdminBinding binding;
    private RetrofitApi mService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        activity = this;
        mService = Common.getAPI();


        binding.submit.setOnClickListener(view12 -> {


            if (TextUtils.isEmpty(binding.etQuestion.getText().toString()) ||
                    TextUtils.isEmpty(binding.etOption1.getText().toString())||
                    TextUtils.isEmpty(binding.etOption2.getText().toString())||
                    TextUtils.isEmpty(binding.etOption3.getText().toString())||
                    TextUtils.isEmpty(binding.etOption4.getText().toString())){
                Functions.ShowToast(activity,"Aise nahi chalta bhai sab field fill krna padta hai");
            }else
            if (binding.spinnerAnswer.getSelectedItem().toString().equals("select correct answer")){
                Functions.ShowToast(activity,"Select correct answer");
            }else {

                add_api();
            }

        });



        binding.addDate.setOnClickListener(view1 -> {
            if (TextUtils.isEmpty(binding.etDate.getText().toString())){
                Functions.ShowToast(activity,"Date to dalo yr ");
            }else {
                add_date_api();
            }
        });

    }

    private void add_date_api() {
        ProgressDialog pd = new ProgressDialog(this);
        pd.setTitle("Adding Date");
        pd.setMessage("please wait...");
        pd.setCancelable(false);
        pd.show();
    mService.admin_add_date(0,binding.etDate.getText().toString(),"add")
            .enqueue(new Callback<Date>() {
                @Override
                public void onResponse(Call<Date> call, Response<Date> response) {
                    if (response.body().code.equals(Constants.SUCCESS)){
                        Functions.ShowToast(activity,"date added successfully");
                    }else {
                        Functions.ShowToast(activity,response.body().error_msg);
                    }
                    pd.dismiss();

                }

                @Override
                public void onFailure(Call<Date> call, Throwable t) {
                    pd.dismiss();
Functions.ShowToast(activity,""+t.getMessage());
                }
            });
    }

    private void add_api() {
        ProgressDialog pd = new ProgressDialog(this);
        pd.setTitle("Adding Question");
        pd.setMessage("please wait...");
        pd.setCancelable(false);
        pd.show();

        Bundle bundle = getIntent().getExtras();
    mService.admin_add_question("add",bundle.getInt("id",0),bundle.getInt("date_id"),
            binding.etQuestion.getText().toString(),
            binding.etOption1.getText().toString(),
            binding.etOption2.getText().toString(),
            binding.etOption3.getText().toString(),
            binding.etOption4.getText().toString(),
            binding.spinnerAnswer.getSelectedItem().toString())
            .enqueue(new Callback<Quiz>() {
                @Override
                public void onResponse(Call<Quiz> call, Response<Quiz> response) {
                    if (response.body().code.equals(Constants.SUCCESS)){
                        Toast.makeText(activity, "question added success", Toast.LENGTH_SHORT).show();

                        binding.etQuestion.setText("");
                        binding.etOption1.setText("");
                        binding.etOption2.setText("");
                        binding.etOption4.setText("");
                        binding.etOption3.setText("");
                        binding.spinnerAnswer.setSelection(0);
                    }
                    pd.dismiss();
                }

                @Override
                public void onFailure(Call<Quiz> call, Throwable t) {
                    pd.dismiss();
                    Toast.makeText(activity, ""+t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
    }
}