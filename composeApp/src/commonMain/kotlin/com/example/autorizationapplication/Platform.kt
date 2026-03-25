package com.example.autorizationapplication

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform