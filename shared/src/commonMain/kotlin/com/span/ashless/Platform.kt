package com.span.ashless

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
