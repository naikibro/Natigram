package com.example.natigram

import FirebaseArticleRepository
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.ScrollView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
import com.example.natigram.data.model.articles.ArticleDao
import com.example.natigram.data.model.articles.ArticleDataResponse
import com.example.natigram.databinding.ActivityHomeBinding
import com.example.natigram.ui.articles.ArticleFragment
import com.example.natigram.ui.articles.CreateArticleActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.example.natigram.data.listeners.ArticleDeleteListener

class HomeActivity : AppCompatActivity(), ArticleDeleteListener {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var progressBar: ProgressBar
    private lateinit var homeScrollView: ScrollView
    private lateinit var articleDao: ArticleDao
    private lateinit var firebaseRepository: FirebaseArticleRepository
    private var userId: String? = null
    private var articles: MutableList<ArticleDataResponse> = mutableListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userId = intent.getStringExtra("USER_ID")

        if (userId == null) {
            Toast.makeText(this, "User ID is missing", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        progressBar = findViewById(R.id.progressBar)
        homeScrollView = findViewById(R.id.home_scrollview)
        articleDao = ArticleDao(this)
        firebaseRepository = FirebaseArticleRepository(this)
        setupToolbar()
        setupBottomNavigation()
        //setupArticlesListener()
        hideSystemBars()
    }

    override fun onResume() {
        super.onResume()
        syncArticlesWithFirebase()
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
                R.id.navigation_home -> true
                R.id.navigation_search -> true
                R.id.navigation_add -> {
                    val intent = Intent(this, CreateArticleActivity::class.java).apply {
                        putExtra("USER_ID", userId)
                    }
                    startActivity(intent)
                    true
                }
                R.id.navigation_profile -> true
                else -> false
            }
        }
    }

    private fun syncArticlesWithFirebase() {

        articles = articleDao.flushAll();
        Log.d("SYNC", articles.toString())
        firebaseRepository.fetchArticles(
            onSuccess = { a ->
                articles = a.toMutableList()
                articleDao.insertArticles(articles)
                displayStoredArticles()
            },
            onFailure = { exception ->
                Log.d("Articles", "fetch from firebase has failed: $exception")
            }
        )
    }

    private fun displayStoredArticles() {
        homeScrollView.visibility = View.GONE
        progressBar.visibility = View.VISIBLE

        supportFragmentManager.fragments.forEach { fragment ->
            supportFragmentManager.beginTransaction().remove(fragment).commit()
        }

        val articles: List<ArticleDataResponse> = articleDao.getAllArticles()
        if (articles.isNotEmpty()) {
            displayArticles(articles)
        }

        progressBar.visibility = View.GONE
        homeScrollView.visibility = View.VISIBLE
    }

    private fun displayArticles(articles: List<ArticleDataResponse>) {
        supportFragmentManager.fragments.forEach { fragment ->
            supportFragmentManager.beginTransaction().remove(fragment).commit()
        }

        for (article in articles) {
            val uniqueImageUrl = "https://picsum.photos/200?random=${article.id}${article.userId}"
            val fragment = ArticleFragment.newInstance(
                userId = article.userId,
                id = article.id,
                title = article.title,
                body = article.body,
                image = uniqueImageUrl,
                currentUserId = userId!!
            )
            addFragmentToLayout(fragment)
        }
    }

    private fun hideSystemBars() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.hide(WindowInsetsCompat.Type.systemBars())
        controller.hide(WindowInsetsCompat.Type.statusBars())
        controller.hide(WindowInsetsCompat.Type.navigationBars())
        controller.hide(WindowInsetsCompat.Type.displayCutout())
        controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }

    private fun addFragmentToLayout(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.add(R.id.article_container, fragment)
        transaction.commit()
    }

    override fun onArticleDeleted() {
        displayStoredArticles()
    }
}
