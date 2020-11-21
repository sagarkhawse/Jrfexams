package com.virtualmind.jrfexams.activities;

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
import android.view.ViewGroup;
import android.widget.Button;

import com.bumptech.glide.Glide;
import com.gocashfree.cashfreesdk.CFPaymentService;
import com.google.firebase.FirebaseApp;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.onesignal.OneSignal;
import com.virtualmind.jrfexams.BuildConfig;
import com.virtualmind.jrfexams.Payment;
import com.virtualmind.jrfexams.R;
import com.virtualmind.jrfexams.adapters.DatesAdapter;
import com.virtualmind.jrfexams.bottomsheet.FreeExamsBottomSheet;
import com.virtualmind.jrfexams.common.Common;
import com.virtualmind.jrfexams.databinding.ActivityMainBinding;
import com.virtualmind.jrfexams.models.AppData;
import com.virtualmind.jrfexams.models.Date;
import com.virtualmind.jrfexams.models.User;
import com.virtualmind.jrfexams.retrofit.RetrofitApi;
import com.virtualmind.jrfexams.utils.Animations;
import com.virtualmind.jrfexams.utils.Constants;
import com.virtualmind.jrfexams.utils.Functions;
import com.virtualmind.jrfexams.utils.Helper;
import com.virtualmind.jrfexams.utils.SharedPreferenceUtil;
import com.virtualmind.jrfexams.utils.Variables;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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

    SharedPreferenceUtil sharedPreferenceUtil;
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

        sharedPreferenceUtil = new SharedPreferenceUtil(activity);
        userData = Helper.getLoggedInUser(sharedPreferenceUtil);
        if (userData == null) {
            startActivity(new Intent(activity, LogRegActivity.class));
            finish();
        } else {
            customerEmail = userData.email;
            customerPhone = userData.phone;


            binding.recyclerview.setHasFixedSize(true);
            binding.recyclerview.setLayoutManager(new LinearLayoutManager(activity));


            Animations.bounce(activity, binding.cvDemoExam);
            Animations.bounce(activity, binding.cvSubscription);


            Glide.with(activity).load(Variables.BASE_URL + userData.profile_pic).placeholder(R.drawable.ic_baseline_person_24).into(binding.userDp);

            binding.userDp.setOnClickListener(view13 -> startActivity(new Intent(activity, ProfileActivity.class)));

            initAppData();
            initDates();

//
            Button crashButton = new Button(this);
            crashButton.setText("Crash!");
            crashButton.setOnClickListener(view14 -> {
                throw new RuntimeException("Test Crash"); // Force a crash
            });

            addContentView(crashButton, new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
//
        }

        binding.menu.setOnClickListener(this::showMenuOptions);
        binding.cvSubscription.setOnClickListener(view1 -> initPaymentScreen());
        binding.cvDemoExam.setOnClickListener(view12 -> {
//            Intent intent = new Intent(activity, MCQActivity.class);
//            intent.putExtra("date_id", 0);
//            startActivity(intent);
            FreeExamsBottomSheet bottomSheet = new FreeExamsBottomSheet();
            bottomSheet.show(getSupportFragmentManager(), "freeexambottomsheet");
        });

        FirebaseApp.initializeApp(this);
        Glide.with(activity).asBitmap().load(R.drawable.logo_main).override(600, 200).into(binding.logo);


        //admin
   // binding.admin.setOnClickListener(view14 -> startActivity(new Intent(activity,AdminActivity.class)));
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
                        binding.sub.setText(getString(R.string.rupee) + " " + orderAmount + " " + getString(R.string.subscription));
                        binding.progressbarSub.setVisibility(View.GONE);
                        binding.cvSubscription.setEnabled(true);

                        if (dat.isactive != null) {
                            if (dat.isactive.equals("1")) {
                                subscriptionActive();
                                //check for subscription expiry , if date is more than 30 days ago
                                if (Functions.getDaysBetweenDates(dat.date, Functions.getCurrentDateTime()) > 30) {
                                    //make subscription changes to expired
                                    subscribingUserApi("date", Constants.INACTIVATE);
                                } else {
                                    binding.sub.setText(Functions.dateFormat(dat.date));
                                }

                            }


                            if (dat.device_id!=null){
                                if (!dat.device_id.equals(Functions.getDeviceId(activity))){
                                    Functions.ShowToast(activity, "Unauthorized access");
                                    log_out();
                                }
                            }

                        }


                    }

                    @Override
                    public void onFailure(@NonNull Call<AppData> call, @NonNull Throwable t) {
                        binding.cvSubscription.setEnabled(false);
                    }
                });
    }

    private void log_out() {
        Helper.setLoggedInUser(sharedPreferenceUtil,null);
        startActivity(new Intent(activity, LogRegActivity.class));
        finish();
    }
    private void initDates() {
        //1 for paid exams
        mService.show_all_test(1)
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
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.logout:
                       log_out();
                        finish();
                        return true;
                    case R.id.contact_us:
                       startActivity(new Intent(activity, ContactUsActivity.class));
                        return true;

                    case R.id.privacy_policy:
                        Intent intent4 = new Intent(activity, PrivacyPolicyActivity.class);
                        intent4.putExtra("work","privacy_policy");
                        startActivity(intent4);
                        return true;

                    case R.id.about_us:
                        Intent intent1 = new Intent(activity, PrivacyPolicyActivity.class);
                        intent1.putExtra("work","about_us");
                        startActivity(intent1);
                        return true;

                    case R.id.term_and_condition:
                        Intent intent2 = new Intent(activity, PrivacyPolicyActivity.class);
                        intent2.putExtra("work","term_and_condition");
                        startActivity(intent2);
                        return true;

                    case R.id.refund_policy:
                        Intent intent3 = new Intent(activity, PrivacyPolicyActivity.class);
                        intent3.putExtra("work","refund_policy");
                        startActivity(intent3);
                        return true;

                    case R.id.share:
                        Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_SEND);
                        sendIntent.putExtra(Intent.EXTRA_TEXT,
                                "I found a great app in play store.CSIR-NET,DBT-JRF,GATE and other life science exams will be easy for you with daily revision check it out : https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID);
                        sendIntent.setType("text/plain");
                        startActivity(sendIntent);
                        return true;

                    case R.id.rate:
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID));
                        startActivity(intent);
                        return true;

                    case R.id.profile:
                        startActivity(new Intent(activity, ProfileActivity.class));
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
                subscribingUserApi(date, Constants.ACTIVATE);
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


    /**
     * Api calls for subscriptions
     *
     * @param date
     * @param action
     */
    private void subscribingUserApi(String date, String action) {
        pd.setMessage("please wait...");
        pd.show();
        mService.subscription(userData.user_id, date, action)
                .enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                        assert response.body() != null;
                        if (response.body().code.equals(Constants.SUCCESS)) {

                            if (action.equals(Constants.ACTIVATE)) {
                                Functions.ShowToast(activity, "User Subscription Activated Successfully");
                                subscriptionActive();
                            } else {
                                subscriptionInActive();
                            }

                        } else {
                            Functions.ShowToast(activity, "" + response.body().error_msg);
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


    /**
     * Subscription on / off views methods
     */
    private void subscriptionActive() {
        Variables.isUserSubscribed = true;
        binding.subscriptionTitle.setText(getString(R.string.activated));

        binding.cvSubscription.setBackgroundColor(getResources().getColor(R.color.colorGreen));
        binding.cvSubscription.setEnabled(false);

        binding.subActive.setText(getString(R.string.subscription_active));
        binding.subActive.setTextColor(getResources().getColor(R.color.colorGreen));

    }

    private void subscriptionInActive() {
        Variables.isUserSubscribed = false;
        binding.subscriptionTitle.setText(getString(R.string.subscription_title));


        binding.cvSubscription.setBackgroundColor(getResources().getColor(R.color.colorBlack));
        binding.cvSubscription.setEnabled(false);

        binding.subActive.setText(getString(R.string.subscription_inactive));
        binding.subActive.setTextColor(getResources().getColor(R.color.colorBlack));

    }


}