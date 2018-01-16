package com.peeeq.reciper.details.di

import android.content.Context
import android.content.SharedPreferences
import com.peeeq.reciper.details.model.RecipeSaver
import com.peeeq.reciper.details.model.RecipeSaverImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
abstract class RecipeDetailsModule {

    @Binds
    abstract fun bindsRecipeSaver(recipeSaverImpl: RecipeSaverImpl): RecipeSaver

    @Module
    companion object {
        @Singleton
        @Provides
        @JvmStatic
        fun providesSharedPreferences(context: Context): SharedPreferences =
                context.getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
    }

}
