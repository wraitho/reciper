//package com.peeeq.reciper.network
//
//import android.content.Context
//import dagger.BindsInstance
//import dagger.Component
//import retrofit2.Retrofit
//import javax.inject.Singleton
//
//@Singleton
//@Component(modules = arrayOf(NetworkModule::class))
//interface NetworkComponent {
//
//    @Component.Builder
//    interface Builder {
//        @BindsInstance
//        fun context(context: Context): Builder
//
//        fun build(): NetworkComponent
//    }
//
//    // Only Retrofit is exposed to higher level components
//    fun provideRetrofit(): Retrofit
//}