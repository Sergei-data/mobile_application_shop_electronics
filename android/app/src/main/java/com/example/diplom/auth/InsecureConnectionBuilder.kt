package com.example.diplom.auth

import android.net.Uri
import net.openid.appauth.connectivity.ConnectionBuilder
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

object InsecureConnectionBuilder : ConnectionBuilder {
    private const val CONNECTION_TIMEOUT_MS = 15_000
    private const val READ_TIMEOUT_MS = 10_000

    @Throws(IOException::class)
    override fun openConnection(uri: Uri): HttpURLConnection {
        val connection = URL(uri.toString()).openConnection() as HttpURLConnection
        connection.connectTimeout = CONNECTION_TIMEOUT_MS
        connection.readTimeout = READ_TIMEOUT_MS
        connection.instanceFollowRedirects = false
        return connection
    }
}