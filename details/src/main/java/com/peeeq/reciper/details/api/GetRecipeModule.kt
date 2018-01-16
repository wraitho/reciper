package com.peeeq.reciper.details.api

import dagger.Module
import dagger.Provides
import retrofit2.Retrofit

@Module
class GetRecipeModule {
    @Provides
    fun bindsApiClient(getRecipesService: GetRecipeService): GetRecipeApi = GetRecipeApiClient(getRecipesService)

    @Provides
    fun providesService(retrofit: Retrofit): GetRecipeService = retrofit.create(GetRecipeService::class.java)
}
