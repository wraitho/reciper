package com.peeeq.reciper.commons.data

import com.squareup.moshi.Json
import java.io.Serializable

data class Recipe(@Json(name = "publisher") val publisher: String,
                  @Json(name = "title") val title: String,
                  @Json(name = "recipe_id") val recipeId: String,
                  @Json(name = "social_rank") val rank: Double,
                  @Json(name = "ingredients") val ingredients: Array<String>?,
                  @Json(name = "image_url") val imageUrl: String,
                  @Json(name = "source_url") val sourceUrl: String,
                  @Json(name = "f2f_url") val f2fUrl: String,
                  var favorite: Boolean = false) : Serializable {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Recipe

        if (recipeId != other.recipeId) return false

        return true
    }

    override fun hashCode(): Int {
        return recipeId.hashCode()
    }
}
