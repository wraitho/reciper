package com.peeeq.reciper.details.di

import android.content.Context
import com.peeeq.reciper.details.api.GetRecipeModule
import com.peeeq.reciper.details.ui.DetailsActivity
import com.peeeq.reciper.network.NetworkModule
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [(NetworkModule::class), (GetRecipeModule::class), (RecipeDetailsModule::class)])
interface RecipeDetailsComponent {
    @Component.Builder
    interface Builder {
        @BindsInstance
        fun context(context: Context): Builder

        fun build(): RecipeDetailsComponent
    }

    fun inject(detailsActivity: DetailsActivity)
}