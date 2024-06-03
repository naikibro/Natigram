package com.example.natigram

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.natigram.data.model.ArticleDataResponse
import com.example.natigram.databinding.ActivityHomeBinding
import com.example.natigram.fetch.ApiInterface
import com.example.natigram.fetch.RetrofitInstance
import com.example.natigram.ui.articles.ArticleFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.widget.Toast

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var apiInterface: ApiInterface

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
            }

            override fun onFailure(call: Call<List<ArticleDataResponse>>, t: Throwable) {
                t.printStackTrace()
                Toast.makeText(this@HomeActivity, "An error occurred: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun displayArticles(articles: List<ArticleDataResponse>) {
        for (article in articles) {
            val fragment = ArticleFragment.newInstance(
                article.userId.toString(),
                article.id.toString(),
                article.title,
                article.body,
                "" // Assuming there's no image URL provided
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
