package com.densitect.rssreader.core

class Settings(val defaultFeedUrls: Set<String>) {
    fun isDefault(url: String): Boolean {
        return defaultFeedUrls.contains(url)
    }
}