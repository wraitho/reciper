package com.peeeq.reciper.details.model

import android.arch.core.executor.testing.InstantTaskExecutorRule
import com.peeeq.reciper.details.RecipeProvider
import com.peeeq.reciper.details.RxJavaTestRule
import com.peeeq.reciper.details.api.GetRecipeApi
import io.reactivex.Observable
import org.amshove.kluent.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule

class RecipeDetailsViewModelTest {

    @Rule @JvmField var mockitoRule: MockitoRule = MockitoJUnit.rule()
    @Rule @JvmField var mInstantExecutorRule = InstantTaskExecutorRule()
    @Rule @JvmField var mRxJava2TestRule = RxJavaTestRule()

    @Mock private lateinit var getRecipeApi: GetRecipeApi
    @Mock private lateinit var recipeSaver: RecipeSaver

    private lateinit var recipeDetailsViewModel: RecipeDetailsViewModel

    @Before
    fun setUp() {
        recipeDetailsViewModel = RecipeDetailsViewModel(getRecipeApi, recipeSaver)
    }

    @Test
    fun getRecipe() {
        // GIVEN (although the statements starts with 'when' it's actually just a setup which is 'given'
        When calling getRecipeApi.getRecipe(RecipeProvider.RECIPE_ID) itReturns Observable.just(RecipeProvider.TEST_RECIPE)

        // WHEN
        recipeDetailsViewModel.getRecipes(RecipeProvider.RECIPE_ID)

        // THEN
        Verify on getRecipeApi that getRecipeApi.getRecipe(RecipeProvider.RECIPE_ID) was called
    }
}