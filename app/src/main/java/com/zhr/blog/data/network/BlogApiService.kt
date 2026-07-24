package com.zhr.blog.data.network

import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Url

interface BlogApiService {
    @GET("/rss.xml")
    suspend fun getRssFeed(): retrofit2.Response<ResponseBody>

    @GET
    suspend fun getPostContent(@Url url: String): retrofit2.Response<ResponseBody>
}