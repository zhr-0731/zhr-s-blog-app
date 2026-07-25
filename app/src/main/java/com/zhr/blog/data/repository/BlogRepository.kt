package com.zhr.blog.data.repository

import com.zhr.blog.data.model.Article
import kotlinx.coroutines.flow.Flow

interface BlogRepository {
    suspend fun getArticles(): Flow<List<Article>>
    suspend fun fetchArticles(): List<Article>
    suspend fun getArticleContent(url: String): String
}