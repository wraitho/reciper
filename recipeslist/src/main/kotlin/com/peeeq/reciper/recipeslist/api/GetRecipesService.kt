package com.peeeq.reciper.recipeslist.api

import com.peeeq.reciper.recipeslist.api.data.RecipesResponse
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface GetRecipesService {
    companion object {
        const val ENDPOINT= "search"
        const val INGREDIENTS = "q" // search can be based on ingredients. multiple ingredients can be separated by comas
        const val SORT = "sort"     // 2 available sorting mechanism: ranking ( r ) and trending ( t )
        const val PAGE = "page"     // pagination (30 recipes per page is fix)
    }

    @GET(ENDPOINT)
    fun getRecipes(@Query(INGREDIENTS) ingredients: String = "",
                   @Query(SORT) sort: String = "r",
                   @Query(PAGE) page: Int = 0): Observable<RecipesResponse>
}
