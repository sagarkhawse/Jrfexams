package com.skteam.jrfexams.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.gocashfree.cashfreesdk.CFPaymentService;
import com.skteam.jrfexams.BuildConfig;
import com.skteam.jrfexams.Payment;
import com.skteam.jrfexams.R;
import com.skteam.jrfexams.adapters.DatesAdapter;
import com.skteam.jrfexams.common.Common;
import com.skteam.jrfexams.databinding.ActivityLogRegBinding;
import com.skteam.jrfexams.databinding.ActivityMainBinding;
import com.skteam.jrfexams.models.AppData;
import com.skteam.jrfexams.models.Date;
import com.skteam.jrfexams.models.User;
import com.skteam.jrfexams.retrofit.RetrofitApi;
import com.skteam.jrfexams.utils.Animations;
import com.skteam.jrfexams.utils.Constants;
import com.skteam.jrfexams.utils.Functions;
import com.skteam.jrfexams.utils.Helper;
import com.skteam.jrfexams.utils.SharedPreferenceUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivityTest";
    private Activity activity;
    private ActivityMainBinding binding;
    private RetrofitApi mService;


    //Cash free Payment
    private String token;
    private String appId = "81771947506bc5ff6f72dfbb117718";
    private String orderId = String.valueOf(UUID.randomUUID());
    private int orderAmount;
    private String customerPhone = "7385663427";
    private String customerEmail = "android@gmail.com";


    private ProgressDialog pd;
    private User userData;
    private String date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        activity = this;
        mService = Common.getAPI();
        pd = new ProgressDialog(activity);

        SharedPreferenceUtil sharedPreferenceUtil = new SharedPreferenceUtil(activity);
        userData = Helper.getLoggedInUser(sharedPreferenceUtil);
        if (userData == null) {
            startActivity(new Intent(activity, LogRegActivity.class));
            finish();
        } else {
            customerEmail = userData.email;
            customerPhone = userData.phone;

            Toast.makeText(activity, "" + userData.user_id, Toast.LENGTH_SHORT).show();


            binding.recyclerview.setHasFixedSize(true);
            binding.recyclerview.setLayoutManager(new LinearLayoutManager(activity));


            Animations.bounce(activity, binding.cvDemoExam);
            Animations.bounce(activity, binding.cvSubscription);

            initAppData();
            initDates();

        }

        binding.menu.setOnClickListener(this::showMenuOptions);
        binding.cvSubscription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initPaymentScreen();
            }
        });


    }

    /**
     * fetching data api calls
     */
    private void initAppData() {
        mService.get_app_data(userData.user_id)
                .enqueue(new Callback<AppData>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onResponse(@NonNull Call<AppData> call, @NonNull Response<AppData> response) {
                        //set subscription views
                        assert response.body() != null;
                        AppData dat = response.body().res.get(0);
                        orderAmount = Integer.parseInt(dat.subscription_amount);
                        binding.sub.setText(orderAmount + getString(R.string.subscription));
                        binding.progressbarSub.setVisibility(View.GONE);
                        binding.cvSubscription.setEnabled(true);

                        if (dat.isactive != null) {
                            if (dat.isactive.equals("1")) {
                                subscriptionActive();
                                binding.sub.setText(Functions.timeDiff(dat.date));
                            }
                        }

                    }

                    @Override
                    public void onFailure(@NonNull Call<AppData> call, @NonNull Throwable t) {
                        binding.cvSubscription.setEnabled(false);
                    }
                });
    }

    private void initDates() {
        mService.show_all_dates()
                .enqueue(new Callback<Date>() {
                    @Override
                    public void onResponse(@NonNull Call<Date> call, @NonNull Response<Date> response) {

                        if (response.body() != null) {
                            if (response.body().code.equals(Constants.SUCCESS)) {
                                DatesAdapter adapter = new DatesAdapter(activity, response.body().res);
                                binding.recyclerview.setAdapter(adapter);
                                binding.progressBar.setVisibility(View.GONE);
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<Date> call, @NonNull Throwable t) {
                        Log.d(TAG, "onFailure: " + t.getMessage());
                    }
                });
    }


    @SuppressLint("RestrictedApi")
    private void showMenuOptions(View view) {
        PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
        popupMenu.inflate(R.menu.menu);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.privacy_policy:
                        startActivity(new Intent(activity, PrivacyPolicyActivity.class));
                        return true;

                    case R.id.share:
                        Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_SEND);
                        sendIntent.putExtra(Intent.EXTRA_TEXT,
                                "Hey check out my app at: https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID);
                        sendIntent.setType("text/plain");
                        startActivity(sendIntent);
                        return true;

                    case R.id.rate:
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID));
                        startActivity(intent);
                        return true;

                    default:
                        return false;
                }

            }
        });

        @SuppressLint("RestrictedApi") MenuPopupHelper menuHelper = new MenuPopupHelper(view.getContext(), (MenuBuilder) popupMenu.getMenu(), view);
        menuHelper.setForceShowIcon(true);
        menuHelper.show();
    }


    /**
     * Cash free Payment methods
     */
    private void initPaymentScreen() {
        pd.setTitle("Preparing for subscription");
        pd.setMessage("please wait...");
        pd.setCancelable(false);
        pd.show();
        mService.generateCFToken(orderId,
                orderAmount,
                "INR")
                .enqueue(new Callback<Payment>() {
                    @Override
                    public void onResponse(@NonNull Call<Payment> call, @NonNull Response<Payment> response) {
                        if (response.body() != null) {
                            if (!response.body().isError()) {
                                token = response.body().getCftoken();
                                triggerPayment();
                            } else {
                                Log.d(TAG, "onResponse: " + response.body().getMessage());
                                Log.d(TAG, "onResponse: " + response.body().getStatus());
                                Log.d(TAG, "onResponse: " + response.body().getCftoken());
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<Payment> call, @NonNull Throwable t) {
                        Log.d(TAG, "onFailure: " + t.getLocalizedMessage());
                        Log.d(TAG, "onFailure: " + t.getStackTrace());
                    }
                });
    }

    private void triggerPayment() {
        pd.dismiss();
        Log.d(TAG, "triggerPayment: " + orderAmount);
        Map<String, String> map = new HashMap<>();
        map.put("appId", appId);
        map.put("orderId", orderId);
        map.put("orderAmount", String.valueOf(orderAmount));
        map.put("customerPhone", customerPhone);
        map.put("customerEmail", customerEmail);
        Log.d(TAG, "triggerPayment: " + token);
        CFPaymentService cfPaymentService = CFPaymentService.getCFPaymentServiceInstance();
        cfPaymentService.setOrientation(0);
        try {
            cfPaymentService.doPayment(activity, map, token, "PROD");
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Same request code for all payment APIs.
//        Log.d(TAG, "ReqCode : " + CFPaymentService.REQ_CODE);
        Log.d(TAG, "API Response : ");
        //Prints all extras. Replace with app logic.
        if (data != null) {
            Bundle bundle = data.getExtras();
            if (bundle != null)
//                for (String key : bundle.keySet()) {
//                    if (bundle.getString(key) != null) {
//                        Log.d(TAG, key + " : " + bundle.getString(key));
//                    }
//                }
                date = bundle.getString("txTime");
            if (bundle.getString("txStatus").equals("SUCCESS")) {
                subscribingUserApi();
            } else {
                Functions.ShowToast(activity, bundle.getString("txStatus"));
            }

            transaction_details(bundle.getString("paymentMode"),
                    bundle.getString("orderId"),
                    date,
                    bundle.getString("referenceId"),
                    bundle.getString("type"),
                    bundle.getString("txMsg"),
                    bundle.getString("signature"),
                    bundle.getString("orderAmount"),
                    bundle.getString("txStatus")
            );
        }
    }


    private void subscribingUserApi() {
        pd.setMessage("please wait...");
        pd.show();
        mService.subscription(userData.user_id, date, "activate")
                .enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                        assert response.body() != null;
                        if (response.body().code.equals(Constants.SUCCESS)) {
                            Functions.ShowToast(activity, "User Subscription Activated Successfully");
                            subscriptionActive();
                        } else {
                            Functions.ShowToast(activity, "failed to activate subscription");
                        }
                        pd.dismiss();
                    }

                    @Override
                    public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                        Functions.ShowToast(activity, "failed to subscribe");
                        pd.dismiss();

                    }
                });
    }

    private void subscriptionActive() {
        binding.subscriptionTitle.setText(getString(R.string.activated));

        binding.cvSubscription.setBackgroundColor(getResources().getColor(R.color.colorGreen));
        binding.cvSubscription.setEnabled(false);

        binding.subActive.setText(getString(R.string.subscription_active));
        binding.subActive.setTextColor(getResources().getColor(R.color.colorGreen));

    }

    private void transaction_details(String payment_mode, String order_id, String time, String reference_id, String type, String message,
                                     String signature, String amount, String status) {
        mService.transaction(order_id, amount, customerPhone, customerEmail, status, time, payment_mode, type, reference_id, signature, message)
                .enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                        Log.d(TAG, "onResponse: transaction detail sent to server");
                    }

                    @Override
                    public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                        Log.d(TAG, "onFailure: error " + t.getMessage());
                    }
                });
    }
}