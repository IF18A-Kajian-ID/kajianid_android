package com.kajianid.android.databases

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.kajianid.android.databases.DatabaseContract.KajianColumns.Companion.ADDRESS
import com.kajianid.android.databases.DatabaseContract.KajianColumns.Companion.CONTENT
import com.kajianid.android.databases.DatabaseContract.KajianColumns.Companion.DATE_ANNOUNCE
import com.kajianid.android.databases.DatabaseContract.KajianColumns.Companion.DATE_DUE
import com.kajianid.android.databases.DatabaseContract.KajianColumns.Companion.DESCRIPTION
import com.kajianid.android.databases.DatabaseContract.KajianColumns.Companion.HAS_IMG
import com.kajianid.android.databases.DatabaseContract.KajianColumns.Companion.ID
import com.kajianid.android.databases.DatabaseContract.KajianColumns.Companion.IMGURL
import com.kajianid.android.databases.DatabaseContract.KajianColumns.Companion.IMG_RESOURCE
import com.kajianid.android.databases.DatabaseContract.KajianColumns.Companion.KAJIAN_TITLE
import com.kajianid.android.databases.DatabaseContract.KajianColumns.Companion.MOSQUE_NAME
import com.kajianid.android.databases.DatabaseContract.KajianColumns.Companion.PLACE
import com.kajianid.android.databases.DatabaseContract.KajianColumns.Companion.POST_DATE
import com.kajianid.android.databases.DatabaseContract.KajianColumns.Companion.TABLE_NAME_ARTICLES
import com.kajianid.android.databases.DatabaseContract.KajianColumns.Companion.TABLE_NAME_KAJIAN
import com.kajianid.android.databases.DatabaseContract.KajianColumns.Companion.USTADZ_NAME
import com.kajianid.android.databases.DatabaseContract.KajianColumns.Companion.YOUTUBE_LINK

internal class DatabaseHelper (context: Context):
        SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION){
    companion object {
        private const val DATABASE_NAME = "dbShoumHome"
        private const val DATABASE_VERSION = 1
        private val SQL_CREATE_TABLE_ARTIKEL = """
            CREATE TABLE $TABLE_NAME_ARTICLES(
            $ID varchar(5) not null primary key,
            $KAJIAN_TITLE text,
            $POST_DATE Text,
            $CONTENT Text,
            $HAS_IMG Int,
            $USTADZ_NAME varchar(15),
            $IMGURL Text)
        """.trimIndent()

        private val SQL_CREATE_NAME_KAJIAN = """
            CREATE TABLE $TABLE_NAME_KAJIAN(
            $ID varchar(5) not null primary key,
            $KAJIAN_TITLE Text,
            $USTADZ_NAME varchar(15),
            $MOSQUE_NAME varchar(30),
            $ADDRESS Text,
            $PLACE varchar(30),
            $YOUTUBE_LINK Text,
            $DESCRIPTION Text,
            $IMG_RESOURCE Text,
            $DATE_ANNOUNCE Text,
            $DATE_DUE Text)
        """.trimIndent()
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(SQL_CREATE_TABLE_ARTIKEL)
        db?.execSQL(SQL_CREATE_NAME_KAJIAN)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        onCreate(db)
    }
}