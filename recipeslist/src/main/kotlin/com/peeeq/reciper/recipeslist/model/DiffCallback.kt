package com.peeeq.reciper.recipeslist.model

import android.support.v7.util.DiffUtil
import com.peeeq.reciper.commons.data.Recipe

class DiffCallback(private val oldRecipies: List<Recipe>, private val newRecipies: List<Recipe>) : DiffUtil.Callback() {

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
            oldRecipies[oldItemPosition].recipeId == newRecipies[newItemPosition].recipeId

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
            oldRecipies[oldItemPosition] == newRecipies[newItemPosition]

    override fun getOldListSize(): Int = oldRecipies.size

    override fun getNewListSize(): Int = newRecipies.size
}