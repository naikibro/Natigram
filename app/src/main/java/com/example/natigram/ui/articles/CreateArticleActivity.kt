package com.example.natigram.ui.articles

import FirebaseArticleRepository
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.natigram.HomeActivity
import com.example.natigram.R
import com.example.natigram.data.model.articles.ArticleDao
import com.example.natigram.data.model.articles.ArticleDataResponse
import com.example.natigram.data.repository.ArticleRepository
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore


class CreateArticleActivity : AppCompatActivity() {

    private lateinit var articleRepository: ArticleRepository
    private var userId: String? = null
    private lateinit var articleDao: ArticleDao
    private val db = Firebase.firestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_article)

        articleRepository = FirebaseArticleRepository(this)
        userId = intent.getStringExtra("USER_ID")
        articleDao = ArticleDao(this)

        hideSystemBars()
        Log.d("CreateArticleActivity", "Received userId: $userId")

        val bodyEditText: EditText = findViewById(R.id.editTextBody)
        val createButton: Button = findViewById(R.id.buttonCreatePost)

        createButton.setOnClickListener {
            val title = "a user post"
            val body = bodyEditText.text.toString()

            if (title.isNotEmpty() && body.isNotEmpty() && userId != null) {
                val newId = generateNewId().toString()
                val article = ArticleDataResponse(
                    id = newId,
                    userId = userId!!,
                    title = title,
                    body = body
                )
                Log.d("LuiaKartier", article.toString())
                Log.d("ARTICLEREPO", articleRepository.toString())

                val tag = "Article creation"
                db.collection("articles")
                    .add(article)
                    .addOnSuccessListener {
                        Log.d(tag, "success creating article $article")
                        Toast.makeText(this, "Article sucessfully created !", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, HomeActivity::class.java).apply {
                            putExtra("USER_ID", userId)
                        }
                        startActivity(intent)
                    }
                    .addOnFailureListener {
                        Log.d(tag, "failure creating article $article")
                        Toast.makeText(this, "failed to create article", Toast.LENGTH_SHORT)
                            .show()
                    }
            } else {
                Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun generateNewId(): Comparable<*> {
        val articles: List<ArticleDataResponse> = articleDao.getAllArticles()
        return if (articles.isEmpty()) {
            1
        } else {
            articles.maxOf { it.id } + 1
        }
    }

    private fun hideSystemBars() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.hide(WindowInsetsCompat.Type.systemBars())
        controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }
}
