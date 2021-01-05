package com.kajianid.android

import androidx.appcompat.app.AppCompatActivity
import com.kajianid.android.databinding.ActivityReadArticleBinding

class ReadArticleSQLActivity: AppCompatActivity{
    private lateinit var article: Article
    private var downloaded = false
    private lateinit var dbArticleHelper: DbArticleHelper
    private lateinit var binding: ActivityReadArticleBinding

    companion object {
        const val EXTRA_PARCEL_ARTICLES = "extra_parcel_articles"
    }

}