package com.example.natigram.data.model.articles

data class ArticleDataResponse(
    val userId: String,
    val id: Int,
    val title: String,
    val body: String
)
