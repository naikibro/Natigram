package com.example.natigram.fetch

import com.example.natigram.data.model.ArticleDataResponse
import retrofit2.Call
import retrofit2.http.GET

interface ApiInterface {
    @GET("posts")
    fun getExampleData(): Call<List<ArticleDataResponse>>
}
