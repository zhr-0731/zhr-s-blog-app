package com.zhr.blog.data.model

data class Article(
    val title: String,
    val link: String,
    val description: String,
    val pubDate: String,
    val formattedDate: String = ""
)