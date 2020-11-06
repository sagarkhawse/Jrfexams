package com.virtualmind.jrfexams.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.virtualmind.jrfexams.R;
import com.virtualmind.jrfexams.common.Common;
import com.virtualmind.jrfexams.databinding.ActivityProfileBinding;
import com.virtualmind.jrfexams.models.User;
import com.virtualmind.jrfexams.retrofit.RetrofitApi;
import com.virtualmind.jrfexams.utils.Constants;
import com.virtualmind.jrfexams.utils.FileUtils;
import com.virtualmind.jrfexams.utils.Functions;
import com.virtualmind.jrfexams.utils.Helper;
import com.virtualmind.jrfexams.utils.SharedPreferenceUtil;
import com.virtualmind.jrfexams.utils.Variables;

import java.io.File;
import java.util.Objects;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {
    private static final String TAG = "ProfileActivityTest";
    private static final int IMAGE_FILE_PICKER = 1;
    private static final int READ_EXTERNAL_STORAGE_REQUEST_CODE = 2;
    private ActivityProfileBinding binding;
    private Activity activity;

    //Retrofit api
    RetrofitApi mService;

    private String profile_pic;
    private int user_id;
    private User userData;
    private SharedPreferenceUtil sharedPreferenceUtil;
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setSupportActionBar(binding.toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.profile));
        View view = binding.getRoot();
        setContentView(view);
        activity = this;
        mService = Common.getAPI();
        sharedPreferenceUtil = new SharedPreferenceUtil(activity);
        userData = Helper.getLoggedInUser(sharedPreferenceUtil);
        assert userData != null;
        user_id = userData.user_id;


        binding.dpFrame.setOnClickListener(view1 -> openImageChooser());
        binding.saveAndContinue.setOnClickListener(view12 -> updateUserData());
        pd = new ProgressDialog(activity);
        initProfileData();

    }

    private void initProfileData() {
        try {
            Log.d(TAG, "initProfileData: user dp " + Variables.BASE_URL + userData.profile_pic);
            if (userData.profile_pic!=null){
                binding.progressBar.setVisibility(View.VISIBLE);
            }
            Glide.with(activity).load(Variables.BASE_URL + userData.profile_pic).placeholder(R.drawable.ic_baseline_person_24).listener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    binding.progressBar.setVisibility(View.GONE);
                    Functions.ShowToast(activity, "failed to load Profile pic");
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    binding.progressBar.setVisibility(View.GONE);
                    return false;
                }
            }).into(binding.profilePic);
            binding.name.setText(userData.full_name);
            binding.phoneNumber.setText(userData.phone);
            binding.email.setText(userData.email);
            binding.city.setText(userData.city);

            if (userData.gender.equals("Male")) {
                binding.genderSpinner.setSelection(1);
            } else if (userData.gender.equals("Female")) {
                binding.genderSpinner.setSelection(2);
            } else {
                binding.genderSpinner.setSelection(0);
            }
        } catch (NullPointerException e) {
            Log.d(TAG, "initProfileData: " + e.getMessage());
        }


    }


    private void updateUserDp() {
        MultipartBody.Part user_dp = null;
        RequestBody userid = null;
        if (profile_pic != null) {
            user_dp = Functions.prepareFilePart(activity, "file", profile_pic);
            userid = Functions.createPartFromString(String.valueOf(user_id));
        } else {
            Functions.ShowToast(activity, "no file selected");
        }
    mService.update_user_dp(user_dp, userid)
              .enqueue(new Callback<User>() {
                  @Override
                  public void onResponse(Call<User> call, Response<User> response) {
                      if (response.body() != null){
                          if (response.body().code.equals(Constants.SUCCESS)) {
                              Functions.ShowToast(activity, "Profile pic updated");
                              if (response.body().res != null) {
                                  Helper.saveProfileMe(sharedPreferenceUtil, response.body().res.get(0));
                              }
                          } else {
                              Functions.ShowToast(activity, "" + response.body().error_msg);
                          }
                      }

                  }

                  @Override
                  public void onFailure(Call<User> call, Throwable t) {
                      Log.d(TAG, "onFailure: "+t.getMessage());
                  }
              });
                  }

    private void updateUserData() {
        pd.setCancelable(false);
        pd.setTitle("Updating Profile");
        pd.setMessage("please wait...");
        pd.show();

        pd.dismiss();
        mService.update_user_data(user_id, binding.name.getText().toString(), binding.email.getText().toString(),
                binding.genderSpinner.getSelectedItem().toString(), binding.city.getText().toString())
                .enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> user) {
                        if (user.body()!=null){
                            if (user.body().code.equals(Constants.SUCCESS)) {
                                Log.d(TAG, "onSuccess: profile data updated");
                                Functions.ShowToast(activity, "Profile updated successfully");
                                if (user.body().res != null) {
                                    Helper.saveProfileMe(sharedPreferenceUtil, user.body().res.get(0));
                                }
                            } else {
                                Functions.ShowToast(activity, "" + user.body().error_msg);
                            }
                        }

                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
                        Log.d(TAG, "onFailure: "+t.getMessage());
                    }
                });

    }


    //On permission requested
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == READ_EXTERNAL_STORAGE_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                openImageChooser();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_FILE_PICKER && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            Uri selectedImageUri = data.getData();

            // MEDIA GALLERY
            profile_pic = FileUtils.getPath(activity, selectedImageUri);

            try {
                assert profile_pic != null;
                Uri uri = Uri.fromFile(new File(profile_pic));
                Glide.with(this).load(uri).into(binding.profilePic);
                updateUserDp();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void openImageChooser() {
        Log.d(TAG, "openImageChooser: opening image");
        if (FileUtils.doesUserHavePermission(activity)) {
            Intent intent = new Intent(Intent.ACTION_PICK);
            Log.d(TAG, "openImageChooser: ssssss");
            intent.setType("image/*");
            startActivityForResult(Intent.createChooser(intent, "Select Profile Picture"), IMAGE_FILE_PICKER);
        } else {
            Log.d(TAG, "openImageChooser: asdasd");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Log.d(TAG, "openImageChooser: request perem");
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_EXTERNAL_STORAGE_REQUEST_CODE);
            }
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}