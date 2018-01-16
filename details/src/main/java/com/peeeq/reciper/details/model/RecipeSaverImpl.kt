package com.peeeq.reciper.details.model

import android.content.SharedPreferences
import android.util.Log
import com.peeeq.reciper.commons.data.Recipe
import javax.inject.Inject

/**
 * Now this class will only save the recipe id into the shared preferences. If it's saved then it's a favourite.
 *
 * This solution will be replaced later by saving the recipt to a database and the user can take a look at his/her
 * fav recipes even in offline mode.
 *
 */
class RecipeSaverImpl @Inject constructor(private val sharedPreferences: SharedPreferences) : RecipeSaver {

    private val savedRecipeIds: MutableSet<String> by lazy { sharedPreferences.getStringSet(PREF_KEY_FAV_RECIPES, hashSetOf()) }

    companion object {
        @JvmStatic val PREF_KEY_FAV_RECIPES = "FavoriteRecipes"
    }

    override fun saveRecipe(recipe: Recipe) {
        savedRecipeIds.add(recipe.recipeId)
        recipe.favorite = true
        val savedSuccessfully = sharedPreferences.edit().putStringSet(PREF_KEY_FAV_RECIPES, savedRecipeIds).commit()
        Log.d(javaClass.simpleName, "Saving ${recipe.title} ${ if (savedSuccessfully) "was successful" else "wasn't successful"}")
    }

    override fun isRecipeSaved(recipe: Recipe): Boolean = savedRecipeIds.contains(recipe.recipeId)

    override fun removeRecipe(recipe: Recipe) {
        if (savedRecipeIds.contains(recipe.recipeId)) {
            savedRecipeIds.remove(recipe.recipeId)
            recipe.favorite = false
            val removedSuccessfully = sharedPreferences.edit().putStringSet(PREF_KEY_FAV_RECIPES, savedRecipeIds).commit()
            Log.d(javaClass.simpleName, "Removing ${recipe.title} ${ if (removedSuccessfully) "was successful" else "wasn't successful"}")
        }

    }
}
