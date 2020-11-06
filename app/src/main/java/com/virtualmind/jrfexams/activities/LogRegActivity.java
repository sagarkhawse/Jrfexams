package com.virtualmind.jrfexams.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;

import com.virtualmind.jrfexams.R;
import com.virtualmind.jrfexams.common.Common;
import com.virtualmind.jrfexams.databinding.ActivityLogRegBinding;
import com.virtualmind.jrfexams.models.User;
import com.virtualmind.jrfexams.retrofit.RetrofitApi;
import com.virtualmind.jrfexams.utils.Constants;
import com.virtualmind.jrfexams.utils.Functions;
import com.virtualmind.jrfexams.utils.Helper;
import com.virtualmind.jrfexams.utils.SharedPreferenceUtil;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LogRegActivity extends AppCompatActivity {
    private static final String TAG = "LogRegActivityTest";
    private Activity activity;
    private ActivityLogRegBinding binding;
    private RetrofitApi mService;
    private String type = "login";

    private String full_name, email, phone, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLogRegBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        activity = this;
        mService = Common.getAPI();

        initActions();
        createPhoneLoginForm();
    }

    private void initActions() {
        binding.bottomTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (type.equals("login")) {
                    createPhoneSignUpForm();
                } else {
                    createPhoneLoginForm();
                }
            }
        });
        binding.btnContinue.setOnClickListener(view -> {
            if (isFormFilled()) {
                if (type.equals("login")){
                    checkUser(binding.phone.getText().toString().trim());
                }else{
                    sign_up_log_in_api();
                }

            }
        });

        binding.txtBelowContinueBtn.setOnClickListener(view -> {

                if (type.equals("login")){
                   Functions.startSupportChat(activity);
                }else{
                    startActivity(new Intent(activity,PrivacyPolicyActivity.class));
                }


        });
    }



    private void openMainActivity() {
        hideProgressBar();
        Intent intent = new Intent(activity, MainActivity.class);
        startActivity(intent);
        finish();
    }

private void hideProgressBar(){
    binding.progressBar.setVisibility(View.GONE);
    binding.btnContinue.setVisibility(View.VISIBLE);
}
    private void createPhoneSignUpForm() {
        type = "sign_up";
        // setting text views for sign up with email
        binding.heading.setText(getString(R.string.create_account));
        binding.txtBelowContinueBtn.setText(getString(R.string.terms_of_use_and_privacy_policy));
        binding.bottomTxt.setText(getString(R.string.already_have_an_ac));
        binding.btnContinue.setText(getString(R.string.sign_up));

        //set visibility
        binding.username.setVisibility(View.VISIBLE);
        binding.email.setVisibility(View.VISIBLE);
        binding.phone.setVisibility(View.VISIBLE);
        binding.btnContinue.setVisibility(View.VISIBLE);
        binding.progressBar.setVisibility(View.GONE);
    }

    private void createPhoneLoginForm() {
        type = "login";
        // setting text views for login with email
        binding.heading.setText(getString(R.string.welcome_back));
        binding.txtBelowContinueBtn.setText(getString(R.string.forgot_password));
        binding.bottomTxt.setText(getString(R.string.dont_have_ac));
        binding.btnContinue.setText(getString(R.string.log_in));


        //set visibility
        binding.username.setVisibility(View.GONE);
        binding.email.setVisibility(View.GONE);
        binding.password.setVisibility(View.VISIBLE);
        binding.phone.setVisibility(View.VISIBLE);
        binding.btnContinue.setVisibility(View.VISIBLE);
        binding.progressBar.setVisibility(View.GONE);

    }

    private boolean isFormFilled() {
        full_name = binding.username.getText().toString().trim();
        phone = binding.phone.getText().toString().trim();
        email = binding.email.getText().toString().trim();
        password = binding.password.getText().toString().trim();


        if (TextUtils.isEmpty(binding.phone.getText().toString().trim())) {
            binding.phone.setError("phone number required");
            binding.phone.requestFocus();
        } else if (type.equals("sign_up") && TextUtils.isEmpty(full_name)) {
            binding.username.setError("full name required");
            binding.username.requestFocus();
        }else if (binding.phone.length()!=10) {
            binding.phone.setError("enter 10 digit phone number");
            binding.phone.requestFocus();
        } else if (type.equals("sign_up") && TextUtils.isEmpty(email)) {
            binding.email.setError("email address required");
            binding.email.requestFocus();
        } else if (type.equals("sign_up") && !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.email.setError("invalid email address");
            binding.email.requestFocus();
        } else if (TextUtils.isEmpty(password)) {
            binding.password.setError("password required");
            binding.password.requestFocus();
        } else if (password.length() < 6) {
            binding.password.setError("please enter more than 6 character password");
            binding.password.requestFocus();
        } else {
            return true;
        }

        return false;
    }

    private void checkUser(String phone) {
        ProgressDialog pd = new ProgressDialog(activity);
        pd.setMessage("please wait...");
        pd.show();
        mService.check_user(phone)
                .enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                        if (response.body() != null) {
                            User data = response.body();
                            if (data.code.equals(Constants.SUCCESS)) {
                         sign_up_log_in_api();
                            } else {
                                Functions.ShowToast(activity, data.error_msg);
                            }
                            pd.dismiss();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                        Functions.ShowToast(activity, t.getMessage());
                        pd.dismiss();

                    }
                });
    }
    private void sign_up_log_in_api() {
        if (type.equals("login")) {
            full_name = email = "null";
        }
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.btnContinue.setVisibility(View.GONE);

        Log.d(TAG, "sign_up_log_in_api: " + full_name + " " +email + " "+ phone + " "+ password+ " ");

        mService.sign_up(full_name, email, phone, password,Functions.getDeviceId(activity))
                .enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                        if (response.body() != null) {
                            if (response.body().code.equals(Constants.SUCCESS)) {
                                //login or sign up success

                                SharedPreferenceUtil sharedPreferenceUtil = new SharedPreferenceUtil(activity);
                                Helper.setLoggedInUser(sharedPreferenceUtil,response.body().res.get(0));
                                openMainActivity();
                            } else {
                                hideProgressBar();
                                Functions.ShowToast(activity, "" + response.body().error_msg);
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                        Log.d(TAG, "onFailure: " + t.getMessage());
                        hideProgressBar();
                    }
                });
    }
}