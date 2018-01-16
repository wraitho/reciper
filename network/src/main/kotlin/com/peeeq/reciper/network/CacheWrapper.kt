package com.peeeq.reciper.network

import android.content.Context
import com.peeeq.reciper.commons.helper.SingletonHolder
import okhttp3.Cache
import java.io.File

class CacheWrapper private constructor(context: Context){

    val cache: Cache

    init {
        val appCacheDirectory = if (context.externalCacheDir != null)
            context.externalCacheDir
        else
            context.cacheDir

        val httpCacheDirectory = File(appCacheDirectory.absolutePath, "HttpCache")
        cache = Cache(httpCacheDirectory, (10 * 1024 * 1024).toLong())
    }

    companion object : SingletonHolder<CacheWrapper, Context>(::CacheWrapper)

}