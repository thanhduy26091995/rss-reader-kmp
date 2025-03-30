package com.densitect.rssreader

import android.content.Context
import com.densitect.rssreader.core.RssReader
import com.densitect.rssreader.core.datasource.network.FeedLoader
import com.densitect.rssreader.core.datasource.storage.FeedStorage
import com.russhwolf.settings.SharedPreferencesSettings
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import kotlinx.serialization.json.Json

fun RssReader.Companion.create(ctx: Context, withLog: Boolean = true): RssReader {
    return RssReader(
        feedLoader = FeedLoader(
            httpClient = AndroidHTTPClient(withLog),
            feedParser = AndroidFeedParser()
        ),
        feedStorage = FeedStorage(
            settings = SharedPreferencesSettings(
                ctx.getSharedPreferences(
                    "rss_reader",
                    Context.MODE_PRIVATE
                )
            ),
            json = Json {
                ignoreUnknownKeys = true
                isLenient = true
                encodeDefaults = false
            }
        )
    ).also {
        if (withLog) {
            Napier.base(DebugAntilog())
        }
    }
}