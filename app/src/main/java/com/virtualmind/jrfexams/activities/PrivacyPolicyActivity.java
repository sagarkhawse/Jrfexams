package com.virtualmind.jrfexams.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

import com.virtualmind.jrfexams.R;


public class PrivacyPolicyActivity extends AppCompatActivity {

    private String url;
    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);



        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            String work = bundle.getString("work", "privacy_policy");

            String privacy_policy = "http://androappdev.xyz/JrfExams/privacy_policy.html";
            String refund_policy = "http://androappdev.xyz/JrfExams/refund_policy.html";
            String term_and_condition = "http://androappdev.xyz/JrfExams/term_and_condition.html";
            String about_us = "http://androappdev.xyz/JrfExams/about_us.html";
            switch (work) {
                case "refund_policy":
                    url = refund_policy;
                    break;
                case "about_us":
                    url = about_us;
                    break;
                case "term_and_condition":
                    url = term_and_condition;
                    break;
                default:
                    url = privacy_policy;
                    break;
            }


        }



        WebView webView = findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(url);
        webView.setWebViewClient(new WebViewClient());
    }
}
