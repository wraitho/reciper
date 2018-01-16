package com.peeeq.reciper.recipeslist.api

import com.peeeq.reciper.commons.data.Recipe
import io.reactivex.Observable

interface GetRecipesApi {
    fun getRecipes(ingredients: String, sort: String, page: Int) : Observable<List<Recipe>>
}
