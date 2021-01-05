package com.kajianid.android.databases

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import com.kajianid.android.databases.DatabaseContract.KajianColumns.Companion.ID
import com.kajianid.android.databases.DatabaseContract.KajianColumns.Companion.TABLE_NAME_KAJIAN
import kotlin.jvm.Throws

class DbKajianHelper (context: Context) {
    companion object {
        private const val DATABASE_TABLE = TABLE_NAME_KAJIAN
        private lateinit var databaseHelper: DatabaseHelper
        private var INSTANCE: DbKajianHelper? = null
        private lateinit var database: SQLiteDatabase


        fun getInstance(context: Context): DbKajianHelper = INSTANCE
                ?: synchronized(this) {
            INSTANCE
                    ?: DbKajianHelper(context)
        }

    }

    init {
        databaseHelper = DatabaseHelper(context)
    }

    @Throws(SQLiteException::class)
    fun open() {
        database = databaseHelper.writableDatabase
    }

    fun close() {
        databaseHelper.close()
        if (database.isOpen) database.close()
    }

    fun queryAll(): Cursor {
        return database.query(TABLE_NAME_KAJIAN,
                null,
                null,
                null,
                null,
                null,
                null)
    }

    fun queryById(id: String): Cursor {
        return database.query(
                TABLE_NAME_KAJIAN,
                null,
                "$ID = ?",
                arrayOf(id),
                null,
                null,
                null,
                null)
    }

    fun insert(values: ContentValues?): Long {
        return database.insert(DATABASE_TABLE, null, values)
    }

    fun update(id: String, values: ContentValues?): Int {
        return database.update(DATABASE_TABLE, values, "$ID = ?", arrayOf(id))
    }

    fun deleteById(id: String): Int {
        return database.delete(DATABASE_TABLE, "$ID = '$id'", null)
    }
}