package com.peeeq.reciper.recipeslist.model.data

import android.support.v7.util.DiffUtil
import com.peeeq.reciper.commons.data.Recipe

data class UiState(val recipes: List<Recipe>, val diffResult: DiffUtil.DiffResult)