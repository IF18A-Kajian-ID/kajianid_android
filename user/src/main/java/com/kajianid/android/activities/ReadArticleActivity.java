package com.kajianid.android.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.kajianid.android.R;

public class ReadArticleActivity extends AppCompatActivity {

    public static final String EXTRA_ARTICLE_ID = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_article);
    }
}