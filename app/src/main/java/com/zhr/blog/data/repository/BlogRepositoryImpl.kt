package com.zhr.blog.data.repository

import com.zhr.blog.data.model.Article
import com.zhr.blog.data.model.RssFeed
import com.zhr.blog.data.network.BlogApiService
import com.zhr.blog.utils.DateUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.simpleframework.xml.core.Persister
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BlogRepositoryImpl @Inject constructor(
    private val apiService: BlogApiService
) : BlogRepository {

    private val _cachedArticles = MutableStateFlow<List<Article>>(emptyList())

    override suspend fun getArticles(): Flow<List<Article>> {
        if (_cachedArticles.value.isEmpty()) {
            fetchArticles()
        }
        return _cachedArticles.asStateFlow()
    }

    override suspend fun fetchArticles(): List<Article> {
        val response = apiService.getRssFeed()
        if (!response.isSuccessful) return _cachedArticles.value
        val xml = response.body()?.string() ?: return _cachedArticles.value
        val feed = Persister().read(RssFeed::class.java, xml)
        val items = feed.channel?.items ?: emptyList()
        val articles = items.map {
            Article(
                title = it.title,
                link = it.link,
                description = it.description,
                pubDate = it.pubDate,
                formattedDate = DateUtils.formatRssDate(it.pubDate)
            )
        }
        _cachedArticles.value = articles
        return articles
    }

    override suspend fun getArticleContent(url: String): String {
        val response = apiService.getPostContent(url)
        return if (response.isSuccessful) response.body()?.string() ?: "" else ""
    }
}