package com.peeeq.reciper.details.ui

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.content.res.ColorStateList
import android.net.Uri
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.Toast
import com.jakewharton.rxbinding2.view.clicks
import com.peeeq.reciper.commons.data.*
import com.peeeq.reciper.commons.dispatcher.Dispatcher
import com.peeeq.reciper.commons.helper.GlideApp
import com.peeeq.reciper.commons.helper.hide
import com.peeeq.reciper.commons.helper.show
import com.peeeq.reciper.details.R
import com.peeeq.reciper.details.di.DaggerRecipeDetailsComponent
import com.peeeq.reciper.details.di.RecipeDetailsComponent
import com.peeeq.reciper.details.model.RecipeDetailsViewModel
import com.peeeq.reciper.details.model.data.FavouriteEvent
import com.peeeq.reciper.details.model.data.InitEvent
import kotlinx.android.synthetic.main.activity_details.*
import kotlinx.android.synthetic.main.cardview_directions.*
import kotlinx.android.synthetic.main.cardview_ingredients.*
import kotlinx.android.synthetic.main.cardview_published_rated.*
import javax.inject.Inject

class DetailsActivity : AppCompatActivity() {

    private lateinit var component: RecipeDetailsComponent
    private lateinit var viewModel: RecipeDetailsViewModel

    @Inject lateinit var viewModelFactory: RecipeDetailsViewModel.Factory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)
        setupToolbar()
        createFeatureComponent()
        setupViewModel(savedInstanceState)
    }

    private fun getRecipeIdFromIntent(): String {
        val unparsedUriString: String = intent.data.toString()
        val uri = Uri.parse(unparsedUriString.replace("%20", " ").replace("%26", "&"))
        return uri.getQueryParameter(Dispatcher.QUERY_PARAM_RECIPE_ID)
    }

    private fun setupViewModel(savedInstanceState: Bundle?) {
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(RecipeDetailsViewModel::class.java)
        viewModel.init(this)

        if (savedInstanceState == null) {
            viewModel.inputState.postValue(InitEvent(getRecipeIdFromIntent()))
        }

        viewModel.outputState.observe(this, Observer {
            when (it?.status) {
                is Success -> showRecipe(it.data!!.recipe)
                is Failure -> showError(it.error!!)
                is Loading -> showLoading()
            }
        })
    }

    private fun showLoading() {
        content.hide()
        loadingIndicator.show()
    }

    private fun showRecipe(recipe: Recipe) {
        supportActionBar?.title = recipe.title
        toolbarLayout.title = recipe.title

        GlideApp.with(this)
                .load(recipe.imageUrl)
                .centerCrop()
                .into(detailsRecipeImage)

        publishedByView.text = recipe.publisher
        ratingView.text = String.format(getString(R.string.pattern_rate), recipe.rank)
        ingredientsView.text = recipe.ingredients!!.joinToString("\n\n")

        // todo unsubscribing
        directionsView.clicks().subscribe {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(recipe.sourceUrl)
            startActivity(intent)
        }

        if (recipe.favorite) {
            fab.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.link_blue))
        } else {
            fab.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.primary_color))
        }

        fab.clicks().subscribe({ viewModel.inputState.postValue(FavouriteEvent()) })

        loadingIndicator.hide()
        content.show()
    }

    private fun showError(failure: Fail) {
        Toast.makeText(this, failure.throwable.message, Toast.LENGTH_SHORT).show()
    }

    private fun createFeatureComponent() {
        component = DaggerRecipeDetailsComponent.builder()
                .context(this)
                .build()
        component.inject(this)
    }

    private fun setupToolbar() {
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        val supportActionBar = supportActionBar

        if (supportActionBar != null) {
            supportActionBar.setHomeButtonEnabled(true)
            supportActionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back)
            supportActionBar.title = ""
        }
    }
}
