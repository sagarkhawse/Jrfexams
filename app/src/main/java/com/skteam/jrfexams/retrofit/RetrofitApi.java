package com.skteam.jrfexams.retrofit;



import com.skteam.jrfexams.Payment;
import com.skteam.jrfexams.models.AppData;
import com.skteam.jrfexams.models.Date;
import com.skteam.jrfexams.models.Quiz;
import com.skteam.jrfexams.models.User;
import com.skteam.jrfexams.utils.Variables;

import java.util.Map;

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

//
//    @FormUrlEncoded
//    @POST("get_video_list_category_wise.php")
//    Call<VideoResponse> getVideoListCategoryWise(@Field("Category_Name") String category_name);
//
//    @GET("get_category_list.php")
//    Call<VideoResponse> getCategoryList();
//
//    @FormUrlEncoded
//    @POST("search_video.php")
//    Call<VideoResponse> getVideoSearchResult(@Field("Search_Query") String category_name);
//
//
//    @FormUrlEncoded
//    @POST("check_user.php")
//    Call<User> checkUserExist(@Field("Email") String email);
//

//
//
//    @FormUrlEncoded
//    @POST("on_video_liked.php")
//    Call<Video> onVideoLiked(@Field("Video_Id") int video_id,
//                             @Field("Email") String email);

}
