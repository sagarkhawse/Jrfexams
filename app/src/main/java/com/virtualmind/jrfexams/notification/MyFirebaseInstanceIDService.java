package com.virtualmind.jrfexams.notification;


import android.util.Log;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;



public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseIIDService";

    @Override
    public void onTokenRefresh() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);
        storeToken(refreshedToken);
}

    private void storeToken(String token) {  Toast.makeText(this, ""+token, Toast.LENGTH_SHORT).show();
        //saving the token on shared preferences
//        SPref.getInstance(getApplicationContext()).saveDeviceToken(token);
    }

}
