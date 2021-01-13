package com.kajianid.android.databases

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.kajianid.android.databases.DatabaseContract.ArticleColums.Companion.TABLE_NAME_ARTICLES
import com.kajianid.android.databases.DatabaseContract.KajianColumns.Companion.TABLE_NAME_KAJIAN

class DatabaseHelper (context: Context):
        SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION){
    companion object {
        private const val DATABASE_NAME = "dbShoumHome"
        private const val DATABASE_VERSION = 1
        private val SQL_CREATE_NAME_KAJIAN = """
            CREATE TABLE $TABLE_NAME_KAJIAN(
            ${DatabaseContract.KajianColumns.ID} varchar(5) not null primary key,
            ${DatabaseContract.KajianColumns.KAJIAN_TITLE} Text,
            ${DatabaseContract.KajianColumns.USTADZ_NAME} varchar(15),
            ${DatabaseContract.KajianColumns.MOSQUE_NAME} varchar(30),
            ${DatabaseContract.KajianColumns.ADDRESS} Text,
            ${DatabaseContract.KajianColumns.PLACE} varchar(30),
            ${DatabaseContract.KajianColumns.YOUTUBE_LINK} Text,
            ${DatabaseContract.KajianColumns.DESCRIPTION} Text,
            ${DatabaseContract.KajianColumns.IMG_RESOURCE} Text,
            ${DatabaseContract.KajianColumns.DATE_ANNOUNCE} Text,
            ${DatabaseContract.KajianColumns.DATE_DUE} Text)
        """.trimIndent()
    }

    private val SQL_CREATE_TABLE_ARTIKEL = """
            CREATE TABLE $TABLE_NAME_ARTICLES(
            ${DatabaseContract.ArticleColums.ID} varchar(5) not null primary key,
            ${DatabaseContract.ArticleColums.TITLE} text,
            ${DatabaseContract.ArticleColums.POST_DATE} Text,
            ${DatabaseContract.ArticleColums.CONTENT} Text,
            ${DatabaseContract.ArticleColums.HAS_IMG} Int,
            ${DatabaseContract.ArticleColums.USTADZ_NAME} varchar(15),
            ${DatabaseContract.ArticleColums.IMGURL} Text)
        """.trimIndent()

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(SQL_CREATE_NAME_KAJIAN)
        db?.execSQL(SQL_CREATE_TABLE_ARTIKEL)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        onCreate(db)
    }

}
