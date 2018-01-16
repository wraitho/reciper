package com.peeeq.reciper.recipeslist.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.peeeq.reciper.commons.data.Recipe
import com.peeeq.reciper.recipeslist.model.data.UiState
import com.peeeq.reciper.R
import com.peeeq.reciper.widgets.PaginatingRecyclerViewAdapter
import io.reactivex.subjects.PublishSubject

class RecipesListAdapter(private val items: MutableList<Recipe> = mutableListOf(), private val recipeSelected: PublishSubject<Pair<Recipe, View>>) : PaginatingRecyclerViewAdapter<RecipesViewHolder, LoadingViewHolder>() {

    override val realItemCount: Int
        get() = items.size

    override fun onCreateFooterViewHolder(parent: ViewGroup): LoadingViewHolder =
            LoadingViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_item_loading, parent, false))


    override fun onCreateNormalViewHolder(parent: ViewGroup): RecipesViewHolder =
            RecipesViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_item_recipe, parent, false))

    override fun onBindFooterView(holder: LoadingViewHolder) {
        // nothing to do ?
    }

    override fun onBindNormalView(holder: RecipesViewHolder, position: Int) {
        holder.bind(items[position])
        holder.itemView.setOnClickListener({ recipeSelected.onNext(Pair(items[position], holder.itemView.findViewById(R.id.listItemImage))) })
    }

    fun addItems(uiState: UiState) {
        if (items.isEmpty()) {
            this.items.addAll(uiState.recipes)
            notifyDataSetChanged()
        } else {
            this.items.clear()
            this.items.addAll(uiState.recipes)
            uiState.diffResult.dispatchUpdatesTo(this)
        }
        onDataReady(true)
    }

}