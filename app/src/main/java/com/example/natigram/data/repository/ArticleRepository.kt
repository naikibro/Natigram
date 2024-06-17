// ArticleRepository.kt
package com.example.natigram.data.repository

import com.example.natigram.data.model.articles.ArticleDataResponse


interface ArticleRepository {
    fun saveArticle(article: ArticleDataResponse, onSuccess: () -> Unit, onFailure: (Exception) -> Unit)

    fun flushAllArticles()
    fun flushArticle(article: ArticleDataResponse)
    fun flushArticle(articleId: String)
    // Other data operations like fetching articles can be added here
    fun flushArticle(articleId: String, userId: String)
}
