package com.densitect.rssreader

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform