package com.peeeq.reciper.recipeslist.api.data

import com.peeeq.reciper.commons.data.Recipe

data class RecipesResponse(val count: Int, val recipes: List<Recipe>)