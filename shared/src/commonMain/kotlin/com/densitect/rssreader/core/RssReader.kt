package com.densitect.rssreader.core

import com.densitect.rssreader.core.datasource.network.FeedLoader
import com.densitect.rssreader.core.datasource.storage.FeedStorage
import com.densitect.rssreader.core.entity.Feed
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

class RssReader internal constructor(
    private val feedLoader: FeedLoader,
    private val feedStorage: FeedStorage,
    private val settings: Settings = Settings(setOf("https://blog.jetbrains.com/kotlin/feed/")),
) {
    suspend fun getAllFeeds(forceUpdate: Boolean = true): List<Feed> {
        var feeds = feedStorage.getAllFeeds()

        if (forceUpdate || feeds.isEmpty()) {
            val feedUrls =
                if (feeds.isEmpty()) settings.defaultFeedUrls else feeds.map { it.sourceUrl }
            feeds = feedUrls.mapAsync { url ->
                val feed = feedLoader.getFeed(url, settings.isDefault(url))
                feedStorage.saveFeed(feed)
                feed
            }
        }

        return feeds
    }

    suspend fun addFeed(url: String) {
        val feed = feedLoader.getFeed(url, settings.isDefault(url))
        feedStorage.saveFeed(feed)
    }

    suspend fun deleteFeed(url: String) {
        feedStorage.deleteFeed(url)
    }

    private suspend fun <A, B> Iterable<A>.mapAsync(f: suspend (A) -> B): List<B> {
        return coroutineScope {
            map {
                async {
                    f(it)
                }
            }
        }.awaitAll()
    }

    companion object
}