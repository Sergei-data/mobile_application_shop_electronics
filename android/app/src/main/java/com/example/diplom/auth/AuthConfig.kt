package com.example.diplom.auth

import android.net.Uri

object AuthConfig {
    const val CLIENT_ID = "shop-android"
    const val SCOPE = "openid profile email"

    const val ALLOW_INSECURE_HTTP_FOR_DEV = true

    val ISSUER_URI: Uri = Uri.parse("http://10.0.2.2:8080/realms/shop")
    val REDIRECT_URI: Uri = Uri.parse("com.example.diplom:/oauth2redirect")
}