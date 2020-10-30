package com.skteam.jrfexams.utils;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Environment;

import com.skteam.jrfexams.models.Quiz;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Variables {

    private final static String DOMAIN = "https://androappdev.xyz/";
    private final static String PATH = "JrfExams/";
    public final static String BASE_URL = DOMAIN + PATH;
    public final static String api = "index.php?p=";

    public final static String my_shared_pref = "my_shared_pref";
    public static SharedPreferences sharedPreferences;
    public static SharedPreferences.Editor editor;
    public final static String user_name = "user_name";
    public final static String email = "email";
    public final static String phone = "phone";
    public final static String user_id = "user_id";

    public static final String root= Environment.getExternalStorageDirectory().toString();
    public static final String hide_folder_name = "/.HidedSocialMedia/";
    public static final String video_output = "videoOutput.mp4";

    public static final String app_hided_folder =root+hide_folder_name;
    public static final String app_showing_folder =root+"/SocialMedia/";
    public static final String draft_app_folder= app_hided_folder +"Draft/";
    public static String output_frontcamera= app_hided_folder + "output_frontcamera.mp4";
    public static String outputfile= app_hided_folder + "output.mp4";
    public static String outputfile2= app_hided_folder + video_output;
    public static String output_filter_file= app_hided_folder + "output-filtered.mp4";
    public static String gallery_trimed_video= app_hided_folder + "gallery_trimed_video.mp4";
    public static String trimmed_video = app_hided_folder + "trimmed_video.mp4";

    public static int max_video_length=60000;
    public static int recording_duration=60000;
    public static int min_time_recording=15000;

    public static boolean isMultipleImgSelected =  false;
    public static Map<Integer, Bitmap> map = new HashMap<>();
    public static Map<Integer,String> videoMap = new HashMap<>();
    public static int countSelectedPost = 0;

    //inserted data in quizQuestionsList from quiz list adapter
    public static List<Quiz> quizQuestionsList = new ArrayList<>();


    public static boolean isUserSubscribed = false;

}
