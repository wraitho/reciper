package com.peeeq.reciper.details.api

import com.peeeq.reciper.commons.data.Recipe
import io.reactivex.Observable

interface GetRecipeApi {
    fun getRecipe(recipeId: String) : Observable<Recipe>
}
