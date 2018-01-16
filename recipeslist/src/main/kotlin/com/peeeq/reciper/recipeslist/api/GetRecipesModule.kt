package com.peeeq.reciper.recipeslist.api

import dagger.Module
import dagger.Provides
import retrofit2.Retrofit

@Module
class GetRecipesModule {
    @Provides
    fun bindsApiClient(getRecipesService: GetRecipesService): GetRecipesApi = GetRecipesApiClient(getRecipesService)

    @Provides
    fun providesService(retrofit: Retrofit): GetRecipesService = retrofit.create(GetRecipesService::class.java)
}
