package com.peeeq.reciper.recipeslist.ui

import android.support.v4.view.ViewCompat
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import com.peeeq.reciper.commons.data.Recipe
import com.peeeq.reciper.commons.helper.GlideApp
import com.peeeq.reciper.R

class RecipesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bind(recipe: Recipe) {
        GlideApp.with(itemView)
                .load(recipe.imageUrl)
                .centerCrop()
                .into(itemView.findViewById(R.id.listItemImage))
        itemView.findViewById<TextView>(R.id.listItemText).text = recipe.title
        ViewCompat.setElevation(itemView, itemView.context.resources.getDimension(R.dimen.material_toolbar_elevation_default))
    }
}