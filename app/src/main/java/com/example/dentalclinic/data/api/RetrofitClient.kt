package com.example.dentalclinic.data.api

import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    const val BASE_URL = "http://smartcare.tryasp.net/"

    // In-memory cookie storage
    private val cookieJar = object : CookieJar {
        private val cookieStore = mutableMapOf<String, List<Cookie>>()

        override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
            cookieStore[url.host] = cookies
        }

        override fun loadForRequest(url: HttpUrl): List<Cookie> {
            return cookieStore[url.host] ?: listOf()
        }
    }

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val httpClient = OkHttpClient.Builder()
        .addInterceptor(AuthInterceptor())
        .addInterceptor(logging)
        .cookieJar(cookieJar)
        .build()

    val service: SmartCareService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SmartCareService::class.java)
    }

    /**
     * Converts a relative API path to an absolute, URL-encoded URL.
     * Handles spaces and special characters.
     */
    fun getImageUrl(relativePath: String?): String? {
        if (relativePath.isNullOrBlank()) return null
        
        // If it's already an absolute URL, just return it
        if (relativePath.startsWith("http")) return relativePath
        
        // Remove leading slash if exists to prevent double slashes
        val cleanPath = if (relativePath.startsWith("/")) relativePath.substring(1) else relativePath
        
        // Properly encode the path to handle spaces and special characters
        // We split by / to encode segments individually to keep the path structure
        val encodedPath = cleanPath.split("/").joinToString("/") { segment ->
            java.net.URLEncoder.encode(segment, "UTF-8").replace("+", "%20")
        }
        
        return BASE_URL + encodedPath
    }
}
