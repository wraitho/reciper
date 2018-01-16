package com.peeeq.reciper.recipeslist.api

import com.peeeq.reciper.commons.data.Recipe
import io.reactivex.Observable
import javax.inject.Inject

class GetRecipesApiClient @Inject constructor(private val getRecipesService: GetRecipesService) : GetRecipesApi {
    override fun getRecipes(ingredients: String, sort: String, page: Int) : Observable<List<Recipe>> {
        return getRecipesService.getRecipes(ingredients, sort, page).map { it.recipes }
    }
}
