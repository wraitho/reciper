package com.peeeq.reciper.details.api

import com.peeeq.reciper.commons.data.Recipe
import com.peeeq.reciper.details.RecipeProvider
import com.peeeq.reciper.network.RxErrorHandlingCallAdapterFactory
import com.squareup.moshi.KotlinJsonAdapterFactory
import com.squareup.moshi.Moshi
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test

import retrofit2.Retrofit
import okhttp3.mockwebserver.MockResponse
import retrofit2.converter.moshi.MoshiConverterFactory
import java.io.File


class GetRecipeApiClientTest {

    private val mockWebServer = MockWebServer()
    private lateinit var retrofit: Retrofit
    private lateinit var getRecipeApiClient: GetRecipeApiClient

    @Before
    fun setUp() {
        retrofit = Retrofit.Builder()
                .baseUrl(mockWebServer.url("").toString())
                .addCallAdapterFactory(RxErrorHandlingCallAdapterFactory.create())
                .addConverterFactory(MoshiConverterFactory.create(Moshi.Builder().add(KotlinJsonAdapterFactory()).build()))
                .build()
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun getRecipe() {
        // GIVEN
        val recipeId = "35120"
        val expectedJson = createResponseJsonFromFile()
        val expectedRecipe = RecipeProvider.TEST_RECIPE
        setupClientAndServer(expectedJson)

        // WHEN
        val testObserver = getRecipeApiClient.getRecipe(recipeId).test()

        // THEN
        testObserver.assertComplete()
        testObserver.assertValue(expectedRecipe)

    }

    private fun setupClientAndServer(expectedJson: String) {
        mockWebServer.enqueue(MockResponse().setBody(expectedJson))
        val getRecipeService = retrofit.create(GetRecipeService::class.java)
        getRecipeApiClient = GetRecipeApiClient(getRecipeService)
    }

    private fun createResponseJsonFromFile(): String {
        val jsonBuilder = StringBuilder()
        File("details/src/test/res/raw/recipe.json").forEachLine { jsonBuilder.append(it) }
        return jsonBuilder.toString()
    }

}