package com.peeeq.reciper.recipeslist.model

import android.support.v7.util.DiffUtil
import com.peeeq.reciper.commons.data.Recipe
import io.reactivex.Observable
import javax.inject.Inject

/**
 * Util class that helps calculating the differnces between old and new recipes.
 *
 * This could be also interfaced out normally, but for simplicity I keep it as is now.
 *
 */
class RecipesDiffUtil @Inject constructor() {

    fun calculateDiff(oldRecipes: List<Recipe>, newRecipes: List<Recipe>): Observable<DiffUtil.DiffResult> {
        return Observable.fromCallable { DiffUtil.calculateDiff(DiffCallback(oldRecipes, newRecipes)) }
    }
}