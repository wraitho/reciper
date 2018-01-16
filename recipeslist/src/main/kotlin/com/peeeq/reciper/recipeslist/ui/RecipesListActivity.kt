package com.peeeq.reciper.recipeslist.ui

import android.app.SearchManager
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SearchView
import android.support.v7.widget.SearchView.SearchAutoComplete
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import com.jakewharton.rxbinding2.support.v7.widget.queryTextChangeEvents
import com.peeeq.reciper.R
import com.peeeq.reciper.commons.data.*
import com.peeeq.reciper.commons.dispatcher.Dispatcher
import com.peeeq.reciper.commons.helper.hide
import com.peeeq.reciper.commons.helper.show
import com.peeeq.reciper.recipeslist.di.DaggerRecipesListComponent
import com.peeeq.reciper.recipeslist.di.RecipesListComponent
import com.peeeq.reciper.recipeslist.model.RecipesListViewModel
import com.peeeq.reciper.recipeslist.model.data.InputState
import com.peeeq.reciper.recipeslist.model.data.SortType
import com.peeeq.reciper.recipeslist.model.data.UiState
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.include_content_list.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject


class RecipesListActivity : AppCompatActivity() {

    companion object {
        @JvmStatic val TAG = "RecipesListActivity"
    }

    private val recipeSelected = PublishSubject.create<Pair<Recipe, View>>()
    private val recipesListAdapter = RecipesListAdapter(recipeSelected = recipeSelected)

    private lateinit var recipeSelectorDisposable: Disposable
    private lateinit var recipesListComponent: RecipesListComponent
    private lateinit var viewModel: RecipesListViewModel
    private lateinit var searchView: SearchView

    @Inject lateinit var viewModelFactory: RecipesListViewModel.Factory
    @Inject lateinit var dispatcher: Dispatcher

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        setupToolbar()

        createFeatureComponent()

        recipesList.layoutManager = LinearLayoutManager(this)
        recipesList.adapter = recipesListAdapter
        recipesListAdapter.loadMoreListener.subscribe({
            if (recipesListAdapter.itemCount != 1) { // first occasion when only loading is visible
                viewModel.inputState.postValue(InputState("", SortType.RANKING, it))
            }
        })

        initialiseViewModel(savedInstanceState)

        observeViewModelChanges()

        handleIntent(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        searchView = menu?.findItem(R.id.search)?.actionView as SearchView

        styleSearchView(searchView)
        setupSearchView(searchView)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onNewIntent(intent: Intent?) {
        setIntent(intent)
        handleIntent(intent)
        super.onNewIntent(intent)
    }

    override fun onResume() {
        recipeSelectorDisposable = recipeSelected.subscribe({
            Log.d(TAG, "Recipe clicked: ${it.first.title}")
            dispatcher.goToDetails(this, it.first.recipeId)//, it.second)
        }, {it.printStackTrace()})
        super.onResume()
    }

    override fun onPause() {
        recipeSelectorDisposable.dispose()
        super.onPause()
    }

    private fun handleIntent(intent: Intent?) {
        if (Intent.ACTION_SEARCH == intent?.action) {
            val query = intent.getStringExtra(SearchManager.QUERY)
            Log.d(TAG, "Searched: $query")
        }
    }

    private fun setupSearchView(searchView: SearchView) {
        val searchManager: SearchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))

        searchView.queryTextChangeEvents()
                .skipInitialValue()
                .debounce(500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .map { it.queryText().toString() }
                .subscribe {
                    Log.d(TAG, "Searched from RX: $it")
                    viewModel.inputState.postValue(InputState(it, SortType.RANKING, 0))
                }
    }

    private fun styleSearchView(searchView: SearchView) {
        val searchAutoComplete = searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text) as SearchAutoComplete
        searchAutoComplete.setHintTextColor(Color.WHITE)
        searchAutoComplete.setTextColor(Color.WHITE)
        searchAutoComplete.hint = getString(R.string.menu_search_hint)

        val searchClear = searchView.findViewById(android.support.v7.appcompat.R.id.search_close_btn) as ImageView
        searchClear.setImageResource(R.drawable.ic_close)
    }

    private fun observeViewModelChanges() {
        viewModel.uiState.observe(this, Observer {
            Log.d(TAG, "uiState changed")
            when (it?.status) {
                is Success -> showList(it.data!!)
                is Failure -> showError(it.error!!)
                is Loading -> showLoading()
            }
        })
    }

    private fun initialiseViewModel(savedInstanceState: Bundle?) {
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(RecipesListViewModel::class.java)
        viewModel.init(this)

        if (savedInstanceState == null) {
            viewModel.inputState.postValue(InputState("", SortType.RANKING, 0))
        }
    }

    private fun createFeatureComponent() {
        recipesListComponent = DaggerRecipesListComponent.builder()
                .context(this)
                .build()
        recipesListComponent.inject(this)
    }

    private fun showLoading() {
        if (recipesListAdapter.itemCount == 0) {
            errorView.hide()
            recipesList.hide()
            loadingIndicator.show()
        }
    }

    private fun showError(error: Fail) {
        loadingIndicator.hide()
        recipesList.hide()

        //errorView.setFailure("Something went wrong: ${error.throwable.message}", Consumer {
        errorView.setFailure(error, Consumer {
            Toast.makeText(this, "This feature is coming soon!", Toast.LENGTH_SHORT).show()
        })
        errorView.show()
    }

    private fun setupToolbar() {
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        val supportActionBar = supportActionBar

        if (supportActionBar != null) {
            supportActionBar.setHomeButtonEnabled(true)
            supportActionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back)
            supportActionBar.title = getString(R.string.app_name)
        }
    }

    private fun showList(uiState: UiState) {
        recipesListAdapter.addItems(uiState)
        loadingIndicator.hide()
        errorView.hide()
        recipesList.show()
    }
}
