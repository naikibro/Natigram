package com.example.natigram.ui.articles

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.natigram.HomeActivity
import com.example.natigram.R
import com.example.natigram.data.model.articles.ArticleDao
import com.example.natigram.data.model.articles.ArticleDataResponse

class CreateArticleActivity : AppCompatActivity() {

    private lateinit var articleDao: ArticleDao
    private var userId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_article)

        articleDao = ArticleDao(this)
        userId = intent.getStringExtra("USER_ID")

        Log.d("CreateArticleActivity", "Received userId: $userId")

        val bodyEditText: EditText = findViewById(R.id.editTextBody)
        val createButton: Button = findViewById(R.id.buttonCreatePost)

        createButton.setOnClickListener {
            val title = "a user post"
            val body = bodyEditText.text.toString()

            if (title.isNotEmpty() && body.isNotEmpty() && userId != null) {
                val newId = generateNewId()
                articleDao.createArticle(userId!!, newId, title, body)
                Toast.makeText(this, "Post created successfully!", Toast.LENGTH_SHORT).show()
                redirectToHome()
            } else {
                Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun redirectToHome() {
        val intent = Intent(this, HomeActivity::class.java).apply {
            putExtra("USER_ID", userId)
        }
        startActivity(intent)
        finish()
    }

    private fun generateNewId(): Int {
        val articles: List<ArticleDataResponse> = articleDao.getAllArticles()
        return if (articles.isEmpty()) {
            1
        } else {
            articles.maxOf { it.id } + 1
        }
    }
}
