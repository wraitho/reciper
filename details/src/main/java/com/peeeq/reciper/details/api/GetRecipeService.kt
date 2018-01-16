package com.peeeq.reciper.details.api

import com.peeeq.reciper.details.api.data.RecipeResponse
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface GetRecipeService {
    companion object {
        const val ENDPOINT= "get"
        const val RECIPE_ID = "rId"
    }

    @GET(ENDPOINT)
    fun getRecipes(@Query(RECIPE_ID) recipeId: String): Observable<RecipeResponse>
}
