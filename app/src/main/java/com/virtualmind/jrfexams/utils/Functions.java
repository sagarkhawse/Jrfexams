package com.virtualmind.jrfexams.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.provider.Settings;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class Functions {

    public static void ShowToast(Context context, String toast) {
        Toast.makeText(context, "" + toast, Toast.LENGTH_SHORT).show();
    }

    public static String getAppVersion(Context context) {
        PackageInfo packageInfo = null;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        assert packageInfo != null;
        return packageInfo.versionName;
    }


    public static void copyFile(File sourceFile, File destFile) throws IOException {
        if (!destFile.getParentFile().exists())
            destFile.getParentFile().mkdirs();

        if (!destFile.exists()) {
            destFile.createNewFile();
        }

        FileChannel source = null;
        FileChannel destination = null;

        try {
            source = new FileInputStream(sourceFile).getChannel();
            destination = new FileOutputStream(destFile).getChannel();
            destination.transferFrom(source, 0, source.size());
        } finally {
            if (source != null) {
                source.close();
            }
            if (destination != null) {
                destination.close();
            }
        }
    }


    public static long getDurationInt(String filePath) {
        MediaMetadataRetriever metaRetriever_int = new MediaMetadataRetriever();
        metaRetriever_int.setDataSource(filePath);
        String songDuration = metaRetriever_int.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        assert songDuration != null;
        long duration = Long.parseLong(songDuration);
        //int time = (int) (duration % 60000) / 1000;
        // close object
        long time = (int) duration / 1000;
        metaRetriever_int.release();
        return time;
    }
public static int getFileSize(String file_path){
        File file = new File(file_path);
    return (int) file.length() / (1024 * 1024);
    }


    public static void make_directory(String path) {
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdir();
        }
    }

//    @NonNull
//    public static RequestBody createPartFromString(String descriptionString) {
//        return RequestBody.create(
//                MultipartBody.FORM, descriptionString);
//    }
//
//    @NonNull
//    public static MultipartBody.Part prepareFilePart(Context context, String partName, String file_path) {
//        File file;
//        // use the FileUtils to get the actual file by uri
//        try {
//            String extension = file_path.substring(file_path.lastIndexOf("."));
//            if (extension.equals(".mp4")){
//                file = new File(file_path);
//            }else{
//                file = FileUtils.getFile(context,Uri.parse(file_path));
//            }
//
//        }catch(StringIndexOutOfBoundsException e){
//            e.printStackTrace();
//            file = FileUtils.getFile(context,Uri.parse(file_path));
//        }
//
//
//
//        // create RequestBody instance from file
//        RequestBody requestFile =
//                RequestBody.create(
//                        MediaType.parse("*/*"),
//                        file
//                );
//
//        // MultipartBody.Part is used to send also the actual file name
//        return MultipartBody.Part.createFormData(partName, file.getName(), requestFile);
//    }

    public static Dialog determinant_dialog;
    public static ProgressBar determinant_progress;

//    public static void Show_determinent_loader(Context context, boolean outside_touch, boolean cancleable) {
//
//        determinant_dialog = new Dialog(context);
//        determinant_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        determinant_dialog.setContentView(R.layout.item_determinant_progress_layout);
//        determinant_dialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(context,R.drawable.bg_forgot_pass));
//
//        determinant_progress=determinant_dialog.findViewById(R.id.pbar);
//
//        if(!outside_touch)
//            determinant_dialog.setCanceledOnTouchOutside(false);
//
//        if(!cancleable)
//            determinant_dialog.setCancelable(false);
//
//        determinant_dialog.show();
//
//    }
//
//    public static void Show_loading_progress(int progress){
//        if(determinant_progress!=null ){
//            determinant_progress.setProgress(progress);
//
//        }
//    }
//
//
//    public static void cancel_determinent_loader(){
//        if(determinant_dialog!=null){
//            determinant_progress=null;
//            determinant_dialog.cancel();
//        }
//    }






    public static String GetSuffix(String value) {
        try {

            int count=Integer.parseInt(value);
            if (count < 1000) return "" + count;
            int exp = (int) (Math.log(count) / Math.log(1000));
            return String.format("%.1f %c",
                    count / Math.pow(1000, exp),
                    "kMGTPE".charAt(exp-1));
        }catch (Exception e){
            return value;
        }

    }



    public static int getTimeDifference(String startDate) {
        Date diff = new Date(Date.parse(getCurrentDateTime()) - Date.parse(startDate));
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        calendar.setTime(diff);
        int day=calendar.get(Calendar.DAY_OF_MONTH);
        int hours = calendar.get(Calendar.HOUR_OF_DAY);
        return day;
    }


    public static String getCurrentDateTime(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }


    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";  //or use "M/d/yyyy"

    public static long getDaysBetweenDates(String start, String end) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.ENGLISH);
        Date startDate, endDate;
        long numberOfDays = 0;
        try {
            startDate = dateFormat.parse(start);
            endDate = dateFormat.parse(end);
            numberOfDays = getUnitBetweenDates(startDate, endDate, TimeUnit.DAYS);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return numberOfDays;
    }

    private static long getUnitBetweenDates(Date startDate, Date endDate, TimeUnit unit) {
        long timeDiff = endDate.getTime() - startDate.getTime();
        return unit.convert(timeDiff, TimeUnit.MILLISECONDS);
    }


    @NonNull
    public static RequestBody createPartFromString(String descriptionString) {
        return RequestBody.create(
                okhttp3.MultipartBody.FORM, descriptionString);
    }

    @NonNull
    public static MultipartBody.Part prepareFilePart(Context context, String partName, String file_path) {
        File file;
        // use the FileUtils to get the actual file by uri
        file = new File(file_path);
//              file= FileUtils.getFile(context, Uri.parse(file_path));


        // create RequestBody instance from file
        RequestBody requestFile =
                RequestBody.create(
                        MediaType.parse("*/*"),
                        file
                );

        // MultipartBody.Part is used to send also the actual file name
        return MultipartBody.Part.createFormData(partName, file.getName(), requestFile);
    }


    public static void startSupportChat(Activity context) {

        try {
            String headerReceiver = "hello ";// Replace with your message.
            String bodyMessageFormal = "";// Replace with your message.
            String whatsappContain = headerReceiver + bodyMessageFormal;
            String trimToNumner = "+918249660580"; //10 digit number
            Intent intent = new Intent ( Intent.ACTION_VIEW );
            intent.setData ( Uri.parse ( "https://wa.me/" + trimToNumner + "/?text=" + whatsappContain) );
            context.startActivity ( intent );
        } catch (Exception e) {
            e.printStackTrace ();
        }

    }
    public static void startEmailIntent(Activity context) {

        try {
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                    "mailto","jrfexams@gmail.com", null));
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject");
            emailIntent.putExtra(Intent.EXTRA_TEXT, "Body");
            context.startActivity(Intent.createChooser(emailIntent, "Send email..."));
        } catch (Exception e) {
            e.printStackTrace ();
        }

    }



    public static String dateFormat(String date){

        Calendar current_cal = Calendar.getInstance();

        Calendar date_cal=Calendar.getInstance();

        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date d = null;
        try {
            d = f.parse(date);
            date_cal.setTime(d);
        } catch (ParseException e) {
            e.printStackTrace();
        }


        long difference=(current_cal.getTimeInMillis()- date_cal.getTimeInMillis())/1000;

        if(difference<86400)
        {
            if(current_cal.get(Calendar.DAY_OF_YEAR)-date_cal.get(Calendar.DAY_OF_YEAR)==0) {

                SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
                assert d != null;
                return sdf.format(d);
            }
            else
                return "yesterday";
        }

        else if(difference<172800){
            return "yesterday";
        }

        else
            return (difference/86400)+" day ago";

    }

    @SuppressLint("HardwareIds")
    public static String getDeviceId(Context context) {
        return Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);

    }

}
