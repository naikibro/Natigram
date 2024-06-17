// ArticleDatabaseHelper.kt
package com.example.natigram.data.model.articles

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class ArticleDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_ENTRIES)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL(SQL_DELETE_ENTRIES)
        onCreate(db)
    }

    companion object {
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "Articles.db"

        private const val SQL_CREATE_ENTRIES =
            "CREATE TABLE articles (" +
                    "userId INTEGER," +
                    "id INTEGER PRIMARY KEY," +
                    "title TEXT," +
                    "body TEXT)"

        private const val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS articles"

        fun deleteArticle(articleId: String) {
            Log.d("DA", articleId)
        }
    }
}
