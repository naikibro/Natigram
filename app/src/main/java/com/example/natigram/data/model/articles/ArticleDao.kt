package com.example.natigram.data.model.articles

import android.content.ContentValues
import android.content.Context
import android.database.Cursor

class ArticleDao(context: Context) {

    private val dbHelper = ArticleDatabaseHelper(context)
    private val db = dbHelper.writableDatabase

    fun createArticle(userId: Int, id: Int, title: String, body: String) {
        val values = ContentValues().apply {
            put("userId", userId)
            put("id", id)
            put("title", title)
            put("body", body)
        }
        db.insert("articles", null, values)
    }

    fun getAllArticles(): List<ArticleDataResponse> {
        val articles = mutableListOf<ArticleDataResponse>()
        val cursor: Cursor = db.query("articles", null, null, null, null, null, null)
        with(cursor) {
            while (moveToNext()) {
                val userId = getInt(getColumnIndexOrThrow("userId"))
                val id = getInt(getColumnIndexOrThrow("id"))
                val title = getString(getColumnIndexOrThrow("title"))
                val body = getString(getColumnIndexOrThrow("body"))
                articles.add(ArticleDataResponse(userId, id, title, body))
            }
        }
        cursor.close()
        return articles
    }

    fun getArticleById(articleId: Int): ArticleDataResponse? {
        val cursor: Cursor = db.query(
            "articles", null, "id = ?", arrayOf(articleId.toString()),
            null, null, null
        )
        var article: ArticleDataResponse? = null
        with(cursor) {
            if (moveToFirst()) {
                val userId = getInt(getColumnIndexOrThrow("userId"))
                val id = getInt(getColumnIndexOrThrow("id"))
                val title = getString(getColumnIndexOrThrow("title"))
                val body = getString(getColumnIndexOrThrow("body"))
                article = ArticleDataResponse(userId, id, title, body)
            }
        }
        cursor.close()
        return article
    }

    fun updateArticle(id: Int, title: String, body: String) {
        val values = ContentValues().apply {
            put("title", title)
            put("body", body)
        }
        db.update("articles", values, "id = ?", arrayOf(id.toString()))
    }

    fun deleteArticle(id: Int) {
        db.delete("articles", "id = ?", arrayOf(id.toString()))
    }
}
