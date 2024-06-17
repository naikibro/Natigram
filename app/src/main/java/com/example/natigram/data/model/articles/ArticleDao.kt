package com.example.natigram.data.model.articles

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class ArticleDao(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "articles.db"
        const val DATABASE_VERSION = 3
        const val TABLE_NAME = "articles"
        const val COLUMN_ID = "id"
        const val COLUMN_TITLE = "title"
        const val COLUMN_BODY = "body"
        const val COLUMN_USER_ID = "userId"

        const val SQL_CREATE_TABLE = "CREATE TABLE IF NOT EXISTS $TABLE_NAME (" +
                "$COLUMN_USER_ID TEXT, " +
                "$COLUMN_ID TEXT PRIMARY KEY, " +
                "$COLUMN_TITLE TEXT, " +
                "$COLUMN_BODY TEXT) "
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 3) {
            try {
                db.execSQL("ALTER TABLE $TABLE_NAME RENAME TO temp_$TABLE_NAME;")
                db.execSQL(SQL_CREATE_TABLE)
                db.execSQL("INSERT INTO $TABLE_NAME ($COLUMN_ID, $COLUMN_TITLE, $COLUMN_BODY, $COLUMN_USER_ID) " +
                        "SELECT CAST($COLUMN_ID AS TEXT), $COLUMN_TITLE, $COLUMN_BODY, $COLUMN_USER_ID FROM temp_$TABLE_NAME;")
                db.execSQL("DROP TABLE temp_$TABLE_NAME;")
            } catch (e: Exception) {
                Log.e("ArticleDao", "Error during database upgrade", e)
            }
        }
    }

    fun insertArticles(articles: List<ArticleDataResponse>) {
        val db = writableDatabase
        db.beginTransaction()
        try {
            for (article in articles) {
                val values = ContentValues().apply {
                    put(COLUMN_USER_ID, article.userId)
                    put(COLUMN_ID, article.id)
                    put(COLUMN_TITLE, article.title)
                    put(COLUMN_BODY, article.body)
                }
                db.insertWithOnConflict(TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE)
            }
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }

    fun getAllArticles(): List<ArticleDataResponse> {
        val db = readableDatabase
        val cursor = db.query(TABLE_NAME, null, null, null, null, null, null)
        val articles = mutableListOf<ArticleDataResponse>()
        with(cursor) {
            while (moveToNext()) {
                val id = getString(getColumnIndexOrThrow(COLUMN_ID))
                val title = getString(getColumnIndexOrThrow(COLUMN_TITLE))
                val body = getString(getColumnIndexOrThrow(COLUMN_BODY))
                val userId = getString(getColumnIndexOrThrow(COLUMN_USER_ID))
                articles.add(ArticleDataResponse(userId, id, title, body))
            }
        }
        cursor.close()
        return articles
    }

    fun deleteArticle(articleId: String) {
        val db = writableDatabase
        db.beginTransaction()
        try {
            val rowsDeleted = db.delete(TABLE_NAME, "$COLUMN_ID = ?", arrayOf(articleId))
            Log.d("ArticleDao", "Deleted $rowsDeleted row(s) with ID $articleId")
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }


    fun flushAll(): MutableList<ArticleDataResponse> {
        val db = writableDatabase
        val articles = getAllArticles()

        db.beginTransaction()
        try {
            for (article in articles) {
                Log.d("DA", article.toString());
                db.delete(TABLE_NAME, "$COLUMN_ID = ?", arrayOf(article.id))
            }
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }

        return mutableListOf();
    }
}
