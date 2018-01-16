package com.peeeq.reciper.details.api

import com.peeeq.reciper.commons.data.Recipe
import io.reactivex.Observable
import javax.inject.Inject

class GetRecipeApiClient @Inject constructor(private val getRecipesService: GetRecipeService) : GetRecipeApi {
    override fun getRecipe(recipeId: String) : Observable<Recipe> {
        return getRecipesService.getRecipes(recipeId).map { it.recipe }
    }
}
