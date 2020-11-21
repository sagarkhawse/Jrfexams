package com.virtualmind.jrfexams.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.virtualmind.jrfexams.R;
import com.virtualmind.jrfexams.utils.Functions;

public class ContactUsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);

        findViewById(R.id.email).setOnClickListener(view -> Functions.startEmailIntent(this));
        findViewById(R.id.whatsapp).setOnClickListener(view -> Functions.startSupportChat(this));
    }
}