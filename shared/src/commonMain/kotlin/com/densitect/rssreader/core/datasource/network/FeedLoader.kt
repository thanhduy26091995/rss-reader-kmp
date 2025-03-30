package com.densitect.rssreader.core.datasource.network

import com.densitect.rssreader.core.entity.Feed
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText

internal class FeedLoader(private val httpClient: HttpClient, private val feedParser: FeedParser) {
    suspend fun getFeed(url: String, isDefault: Boolean): Feed {
        val xml = httpClient.get(url).bodyAsText()
        return feedParser.parse(url, xml, isDefault)
    }
}