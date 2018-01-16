package com.peeeq.reciper.recipeslist.di

import android.content.Context
import com.peeeq.reciper.recipeslist.api.GetRecipesModule
import com.peeeq.reciper.recipeslist.ui.RecipesListActivity
import com.peeeq.reciper.network.NetworkModule
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [(NetworkModule::class), (GetRecipesModule::class)])
interface RecipesListComponent {

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun context(context: Context): Builder

        fun build(): RecipesListComponent
    }

    fun inject(recipesListActivity: RecipesListActivity)
}