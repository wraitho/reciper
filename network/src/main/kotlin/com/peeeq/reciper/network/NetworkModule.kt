package com.peeeq.reciper.network

import android.content.Context
import com.squareup.moshi.KotlinJsonAdapterFactory
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import io.reactivex.schedulers.Schedulers
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * This Dagger module provides everything that is network related and can be reused in different feature and technical modules
 * The module does not provide anything that is feature related.
 *
 * It deals with an API_KEY that can be stored in a better way but for the sake of simplicity I just added now here.
 */

@Module
class NetworkModule {

    companion object {
        @JvmStatic private val TIMEOUT = 5000L
        @JvmStatic private val API_URL = "http://food2fork.com/api/"
        @JvmStatic private val API_KEY = "5014d3360b6b6b045d77a417dcc52834"
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit =
            Retrofit.Builder()
                    .baseUrl(API_URL)
                    .client(okHttpClient)
                    .addConverterFactory(MoshiConverterFactory
                            .create(Moshi.Builder().add(KotlinJsonAdapterFactory()).build()))
                    .addCallAdapterFactory(RxErrorHandlingCallAdapterFactory.createWithScheduler(Schedulers.io()))
                    .build()

    @Provides
    fun provideOkHttpClient(authRequestInterceptor: AuthRequestInterceptor): OkHttpClient {
        val builder = OkHttpClient.Builder()
                .addInterceptor(authRequestInterceptor)

        builder.connectTimeout(TIMEOUT, TimeUnit.MILLISECONDS)
                .readTimeout(TIMEOUT, TimeUnit.MILLISECONDS)
                .writeTimeout(TIMEOUT, TimeUnit.MILLISECONDS)

        if (BuildConfig.DEBUG) {
            builder.addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
        }

        return builder.build()
    }

    @Provides
    fun providesCache(context: Context): Cache = CacheWrapper.getInstance(context).cache

    @Provides
    fun provideAuthRequestInterceptor(): AuthRequestInterceptor {

        if (API_KEY.isEmpty()) {
            throw ApiKeyNotSetException()
        }

        return AuthRequestInterceptor(API_KEY)
    }
}
