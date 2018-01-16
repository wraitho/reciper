package com.peeeq.reciper.recipeslist.model


import android.arch.lifecycle.*
import android.support.v7.util.DiffUtil
import android.util.Log
import com.peeeq.reciper.commons.data.*
import com.peeeq.reciper.commons.helper.SingleLiveEvent
import com.peeeq.reciper.recipeslist.api.GetRecipesApi
import com.peeeq.reciper.network.RetrofitException
import com.peeeq.reciper.recipeslist.model.data.InputState
import com.peeeq.reciper.recipeslist.model.data.UiState
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class RecipesListViewModel(private val getRecipesApi: GetRecipesApi, private val recipesDiffUtil: RecipesDiffUtil) : ViewModel() {

    val inputState: SingleLiveEvent<InputState> = SingleLiveEvent()
    val uiState: MutableLiveData<Result<UiState>> = MutableLiveData()
    private val recipes: MutableList<Recipe> = mutableListOf()

    //var lastInputState: InputState = InputState("", SortType.RANKING, -1)

    // usecase1: load first time recipes - without any keyword, 1st page
    // usecase2: load more - paging
    // usecase3: search by ingredients - 1st page
    // usecase4: load more with keyword
    // usecase5: change sorting method ? feature might be coming later.

    // concept: store keyword/sort in livedata - observed on that we reduce the number of usescases to 2

    // was trying to achieve the same with livedata's switchmap, but somehow it is not triggered. (code at the bottom)
    fun init(lifecycleOwner: LifecycleOwner) {
        inputState.observe(lifecycleOwner, Observer { inputState ->
            Log.d("RecipesListViewModel", "InputState pushed, page: ${inputState!!.page}")

            //if (inputState == lastInputState) {
                getRecipesApi.getRecipes(inputState.searchTerm, inputState.sortType.value, inputState.page)
                        .flatMap { createRestaurantDiff(it) }
                        .map { createUiState(inputState, it) }
                        .map { Result.success(it) }
                        .startWith(Result.loading())
                        .onErrorReturn { handleError(it) }
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            Log.d("RecipesListViewModel", "Recipes size: ${recipes.size}")
                            //lastInputState = inputState
                            uiState.postValue(it)
                        }, {t -> t.printStackTrace()})
            //}
        })
    }

    private fun createRestaurantDiff(newRecipes: List<Recipe>) : Observable<Pair<List<Recipe>, DiffUtil.DiffResult>> =
        recipesDiffUtil.calculateDiff(this.recipes, newRecipes).map { Pair(newRecipes, it) }

    private fun createUiState(inputState: InputState, result: Pair<List<Recipe>, DiffUtil.DiffResult>) : UiState {
        if (inputState.page == 0) {
            this.recipes.clear()
        }
        this.recipes.addAll(result.first)
        return UiState(this.recipes, result.second)
    }

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

    class Factory @Inject constructor(private val getRecipesApi: GetRecipesApi,
                                      private val recipesDiffUtil: RecipesDiffUtil) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return RecipesListViewModel(getRecipesApi, recipesDiffUtil) as T
        }
    }
}


// 2 Extension function to make transformations work nicer with kotlin
// fun <T, R> LiveData<T>.map(function: (T) -> R): LiveData<R> = Transformations.map(this, function)
// fun <T, R> LiveData<T>.switchMap(function: (T) -> LiveData<R>): LiveData<R> = Transformations.switchMap(this, function)



//    init {
//        inputState.switchMap({
//            Log.d("Recipes", "switchMap")
//            val input = it
//            LiveDataReactiveStreams.fromPublisher<List<Recipe>> {
//                getRecipesApi.getRecipes(input.searchTerm, input.sortType.value, input.page)
//                        .map { UiState(it) }
//                        .map { Result.success(it) }
//                        .startWith(Result.loading())
//                        .onErrorReturn { handleError(it) }
//                        .subscribeOn(Schedulers.io())
//                        .observeOn(AndroidSchedulers.mainThread())
//                        .subscribe({ uiState.postValue(it) })
//            }
//        })
//    }

