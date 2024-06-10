package com.example.natigram.data.model.articles
class ArticleDataSource {

    companion object {
        val articles = listOf(
            ArticleDataResponse(1, 1, "article 1", "Hello world!"),
            ArticleDataResponse(2, 2, "article 2", "How are you ?"),
            ArticleDataResponse(3, 3, "article 3", "Im fine thank you !")
        )
    }


}
