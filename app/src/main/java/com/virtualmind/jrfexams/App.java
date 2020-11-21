package com.virtualmind.jrfexams;

import android.app.Application;
import android.util.Log;

import com.onesignal.OneSignal;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        try {
            // Logging set to help debug issues, remove before releasing your app.
            OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);

            // OneSignal Initialization
            OneSignal.startInit(this)
                    .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                    .unsubscribeWhenNotificationsAreDisabled(true)
                    .init();
        }catch (Exception exception){
            Log.e("ONESIGNAL",exception.getMessage() );
        }
    }
}