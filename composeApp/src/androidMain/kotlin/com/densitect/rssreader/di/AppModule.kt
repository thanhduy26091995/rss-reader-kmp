package com.densitect.rssreader.di

import com.densitect.rssreader.app.FeedStore
import com.densitect.rssreader.core.RssReader
import com.densitect.rssreader.create
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val modules = module {
    single { RssReader.create(ctx = androidContext(), withLog = true) }
    single { FeedStore(rssReader = get()) }
}