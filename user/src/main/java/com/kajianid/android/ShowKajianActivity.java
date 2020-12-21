package com.kajianid.android;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class ShowKajianActivity extends AppCompatActivity {

    public static final String EXTRA_KAJIAN_ID = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_kajian);
    }
}