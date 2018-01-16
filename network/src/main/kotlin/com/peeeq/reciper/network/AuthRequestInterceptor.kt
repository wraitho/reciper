package com.peeeq.reciper.network

import okhttp3.Interceptor
import okhttp3.Response

class AuthRequestInterceptor(private val apiKey: String) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()

        val url = original.url().newBuilder()
                .addQueryParameter("key", apiKey)
                .build()

        val requestBuilder = original.newBuilder()
                .url(url)

        val request = requestBuilder.build()
        return chain.proceed(request)
    }

}
