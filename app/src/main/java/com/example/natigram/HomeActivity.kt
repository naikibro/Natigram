package com.example.natigram

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.natigram.data.model.articles.ArticleDataResponse
import com.example.natigram.databinding.ActivityHomeBinding
import com.example.natigram.fetch.ApiInterface
import com.example.natigram.fetch.RetrofitInstance
import com.example.natigram.ui.articles.ArticleFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.widget.ProgressBar
import android.widget.ScrollView
import android.widget.Toast

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var apiInterface: ApiInterface
    private lateinit var progressBar: ProgressBar
    private lateinit var homeScrollView: ScrollView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        progressBar = findViewById(R.id.progressBar)
        homeScrollView = findViewById(R.id.home_scrollview)

        setupToolbar()
        setupBottomNavigation()
        getApiInterface()
        getExampleData()
    }

    private fun setupToolbar() {
        val toolbar: Toolbar = binding.toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    private fun setupBottomNavigation() {
        val bottomNavigationView: BottomNavigationView = binding.bottomNavigation
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
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
                    // Handle add action
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

    private fun getApiInterface() {
        apiInterface = RetrofitInstance.getInstance().create(ApiInterface::class.java)
    }

    private fun getExampleData() {
        homeScrollView.visibility = View.GONE
        progressBar.visibility = View.VISIBLE
        val call = apiInterface.getExampleData()
        call.enqueue(object : Callback<List<ArticleDataResponse>> {
            override fun onResponse(call: Call<List<ArticleDataResponse>>, response: Response<List<ArticleDataResponse>>) {

                if (response.isSuccessful && response.body() != null) {
                    val exampleData = response.body()
                    exampleData?.let {
                        displayArticles(it.take(50)) // Limit to 10 articles
                    }
                } else {
                    Toast.makeText(this@HomeActivity, "Failed to fetch data", Toast.LENGTH_SHORT).show()
                }
                progressBar.visibility = View.GONE
                homeScrollView.visibility = View.VISIBLE
            }

            override fun onFailure(call: Call<List<ArticleDataResponse>>, t: Throwable) {
                progressBar.visibility = View.GONE
                homeScrollView.visibility = View.VISIBLE
                t.printStackTrace()
                Toast.makeText(this@HomeActivity, "An error occurred: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun displayArticles(articles: List<ArticleDataResponse>) {
        for (article in articles) {
            val uniqueImageUrl = "https://picsum.photos/200?random=${article.id}"
            val fragment = ArticleFragment.newInstance(
                article.userId.toString(),
                article.id.toString(),
                article.title,
                article.body,
                uniqueImageUrl
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
