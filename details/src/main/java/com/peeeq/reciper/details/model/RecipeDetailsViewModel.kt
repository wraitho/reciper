package com.peeeq.reciper.details.model

import android.arch.lifecycle.*
import android.support.annotation.VisibleForTesting
import com.peeeq.reciper.commons.data.*
import com.peeeq.reciper.commons.helper.SingleLiveEvent
import com.peeeq.reciper.details.api.GetRecipeApi
import com.peeeq.reciper.details.model.data.FavouriteEvent
import com.peeeq.reciper.details.model.data.InitEvent
import com.peeeq.reciper.details.model.data.UiEvent
import com.peeeq.reciper.details.model.data.UiState
import com.peeeq.reciper.network.RetrofitException
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class RecipeDetailsViewModel(private val recipeApi: GetRecipeApi, private val recipeSaver: RecipeSaver): ViewModel() {

    val inputState: SingleLiveEvent<UiEvent> = SingleLiveEvent()
    val outputState: MutableLiveData<Result<UiState>> = MutableLiveData()

    fun init(lifecycleOwner: LifecycleOwner) {
        inputState.observe(lifecycleOwner, Observer {
            when (it) {
                is InitEvent -> getRecipes(it.recipeId)
                is FavouriteEvent -> handleFavoriteRecipeEvent()
            }
        })
    }

    @VisibleForTesting
    fun getRecipes(recipeId: String) {
        recipeApi.getRecipe(recipeId)
                .map { setFavoriteFlag(it) }
                .map { UiState(it) }
                .map { Result.success(it) }
                .startWith(Result.loading())
                .onErrorReturn { handleError(it) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    outputState.postValue(it)
                }, {
                    it.printStackTrace()
                })
    }

    @VisibleForTesting
    fun setFavoriteFlag(recipe: Recipe): Recipe {
        recipe.favorite = recipeSaver.isRecipeSaved(recipe)
        return recipe
    }

    fun handleFavoriteRecipeEvent() {
        val recipe = outputState.value!!.data!!.recipe
        if (recipe.favorite) {
            recipeSaver.removeRecipe(recipe)
        } else {
            recipeSaver.saveRecipe(recipe)
        }
        outputState.postValue(Result.success(UiState(recipe)))
    }

    @VisibleForTesting
    private fun handleError(error: Throwable): Result<UiState> {
        return if (error is RetrofitException) {
            when (error.kind) {
                RetrofitException.Kind.NETWORK -> Result.failure(NetworkError(error))
                RetrofitException.Kind.HTTP -> Result.failure(ServerError(error))
                RetrofitException.Kind.UNEXPECTED -> Result.failure(UnknownError(error))
                else -> Result.failure(UnknownError(error))
            }
        } else {
            Result.failure(UnknownError(error))
        }
    }

    class Factory @Inject constructor(private val getRecipeApi: GetRecipeApi, private val recipeSaver: RecipeSaver) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return RecipeDetailsViewModel(getRecipeApi, recipeSaver) as T
        }
    }
}