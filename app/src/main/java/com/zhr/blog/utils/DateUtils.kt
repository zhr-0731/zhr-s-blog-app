package com.zhr.blog.utils

import java.text.SimpleDateFormat
import java.util.Locale

object DateUtils {
    private val RSS_FORMAT = SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US)
    private val DISPLAY_FORMAT = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())

    fun formatRssDate(rssDate: String?): String {
        if (rssDate.isNullOrEmpty()) return ""
        return try {
            val date = RSS_FORMAT.parse(rssDate)
            date?.let { DISPLAY_FORMAT.format(it) } ?: rssDate
        } catch (_: Exception) {
            rssDate
        }
    }
}