package com.skteam.jrfexams.retrofit;



import com.skteam.jrfexams.Payment;
import com.skteam.jrfexams.models.AppData;
import com.skteam.jrfexams.models.Date;
import com.skteam.jrfexams.models.Quiz;
import com.skteam.jrfexams.models.User;
import com.skteam.jrfexams.utils.Variables;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;

public interface RetrofitApi {

    @FormUrlEncoded
    @POST(Variables.api + "check_user")
    Call<User> check_user(@Field("phone") String phone);

    @FormUrlEncoded
    @POST(Variables.api+"sign_up")
    Call<User> sign_up(@Field("full_name") String full_name,
                       @Field("email") String email,
                       @Field("phone") String phone,
                       @Field("password") String password);

    @GET(Variables.api+"show_all_dates")
    Call<Date> show_all_dates();

    @FormUrlEncoded
    @POST(Variables.api+"get_app_data")
    Call<AppData> get_app_data(@Field("user_id") int user_id);

    @FormUrlEncoded
    @POST(Variables.api+"show_quiz")
    Call<Quiz> quiz(@Field("date_id") int date_id);


    //============================================PAYMENT CASH FREE
    @FormUrlEncoded
    @POST(Variables.api+"access_token")
    Call<Payment> generateCFToken(@Field("Order_Id") String order_id,
                                  @Field("Order_Amount") int order_amount,
                                  @Field("Order_Currency") String order_currency);



    @FormUrlEncoded
    @POST(Variables.api+"subscription")
    Call<User> subscription(@Field("user_id") int user_id,
                                  @Field("date") String date,
                                  @Field("action") String action);

    @FormUrlEncoded
    @POST(Variables.api+"transaction")
    Call<User> transaction(@Field("order_id") String order_id,
                                  @Field("order_amount") String order_amount,
                              @Field("customer_phone") String customer_phone,
                              @Field("customer_email") String customer_email,
                              @Field("status") String status,
                                  @Field("date") String date,
                           @Field("payment_mode") String payment_mode,
                           @Field("type") String type,
                           @Field("reference_id") String reference_id,
                           @Field("signature") String signature,
                           @Field("message") String message);

    @Multipart
    @POST(Variables.api + "update_user_dp")
    Call<User> update_user_dp(@Part MultipartBody.Part profile_pic, @Part("user_id") RequestBody user_id);

    @FormUrlEncoded
    @POST(Variables.api + "update_user_data")
    Call<User> update_user_data(@Field("user_id") int user_id,
                                  @Field("name") String name,
                                  @Field("email") String email,
                                  @Field("gender") String gender,
                                  @Field("city") String city);
}
