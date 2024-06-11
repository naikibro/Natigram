package com.example.natigram

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.ScrollView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.natigram.data.model.articles.ArticleDao
import com.example.natigram.data.model.articles.ArticleDataResponse
import com.example.natigram.databinding.ActivityHomeBinding
import com.example.natigram.ui.articles.ArticleFragment
import com.example.natigram.ui.articles.CreateArticleActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var progressBar: ProgressBar
    private lateinit var homeScrollView: ScrollView
    private lateinit var articleDao: ArticleDao
    private var userId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userId = intent.getStringExtra("USER_ID")

        progressBar = findViewById(R.id.progressBar)
        homeScrollView = findViewById(R.id.home_scrollview)
        articleDao = ArticleDao(this)

        setupToolbar()
        setupBottomNavigation()
        displayStoredArticles()

        // Uncomment to insert example data
        //insertExampleData()
    }

    override fun onResume() {
        super.onResume()
        displayStoredArticles()
    }

    private fun setupToolbar() {
        val toolbar: Toolbar = binding.toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    private fun setupBottomNavigation() {
        val bottomNavigationView: BottomNavigationView = binding.bottomNavigation
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    // Handle home action
                    true
                }
                R.id.navigation_search -> {
                    // Handle search action
                    true
                }
                R.id.navigation_add -> {
                    val intent = Intent(this, CreateArticleActivity::class.java).apply {
                        putExtra("USER_ID", userId)
                    }
                    startActivity(intent)
                    true
                }
                R.id.navigation_profile -> {
                    // Handle profile action
                    true
                }
                else -> false
            }
        }
    }

    private fun insertExampleData() {
        articleDao.createArticle("1", 1, "Article 1", "Hello world!")
        articleDao.createArticle("2", 2, "Article 2", "How are you?")
        articleDao.createArticle("3", 3, "Article 3", "I'm fine thank you!")
    }

    private fun displayStoredArticles() {
        homeScrollView.visibility = View.GONE
        progressBar.visibility = View.VISIBLE

        val articles: List<ArticleDataResponse> = articleDao.getAllArticles()
        if (articles.isNotEmpty()) {
            displayArticles(articles)
        } else {
            Toast.makeText(this, "No articles found", Toast.LENGTH_SHORT).show()
        }

        progressBar.visibility = View.GONE
        homeScrollView.visibility = View.VISIBLE
    }

    private fun displayArticles(articles: List<ArticleDataResponse>) {
        // Clear any existing fragments
        supportFragmentManager.fragments.forEach { fragment ->
            supportFragmentManager.beginTransaction().remove(fragment).commit()
        }

        for (article in articles) {
            val uniqueImageUrl = "https://picsum.photos/200?random=${article.id}"
            val fragment = ArticleFragment.newInstance(
                userId = article.userId.toString(),
                id = article.id.toString(),
                title = article.title,
                body = article.body,
                image = uniqueImageUrl,
                currentUserId = userId!!
            )
            addFragmentToLayout(fragment)
        }
    }

    private fun addFragmentToLayout(fragment: Fragment) {
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        transaction.add(R.id.article_container, fragment) // Use a container view to add fragments
        transaction.commit()
    }
}
