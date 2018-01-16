package com.peeeq.reciper.details.model

import com.peeeq.reciper.commons.data.Recipe

interface RecipeSaver {
    fun saveRecipe(recipe: Recipe)
    fun isRecipeSaved(recipe: Recipe): Boolean
    fun removeRecipe(recipe: Recipe)
}