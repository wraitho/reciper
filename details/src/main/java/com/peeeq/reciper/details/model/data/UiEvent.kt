package com.peeeq.reciper.details.model.data

sealed class UiEvent
data class InitEvent(val recipeId: String) : UiEvent()
class FavouriteEvent : UiEvent()