package com.khinthirisoe.popularmoviesappstage2.ui.movies.overview.view

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.khinthirisoe.popularmoviesappstage2.R
import com.khinthirisoe.popularmoviesappstage2.data.db.repository.MovieRepository
import com.khinthirisoe.popularmoviesappstage2.data.pref.AppPreferencesHelper
import com.khinthirisoe.popularmoviesappstage2.ui.movies.overview.MovieContract
import com.khinthirisoe.popularmoviesappstage2.ui.movies.overview.model.Genre
import com.khinthirisoe.popularmoviesappstage2.ui.movies.overview.model.MovieResult
import com.khinthirisoe.popularmoviesappstage2.ui.settings.SettingsActivity
import org.koin.android.ext.android.inject


class MovieActivity : AppCompatActivity(), MovieContract.View, SwipeRefreshLayout.OnRefreshListener {

    val presenter: MovieContract.Presenter by inject()
    val repository: MovieRepository by inject()
    val preferenceHelper: AppPreferencesHelper by inject()

    private var mAdapter: MovieAdapter? = null
    private lateinit var mProgressBar: ProgressBar
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mSwipeRefreshLayout: SwipeRefreshLayout
    private lateinit var mConnectionLayout: LinearLayout
    private lateinit var sortedType: String

    private var isLoading = false
    private var pageNumber = 1
    private var visibleItemCount = 1
    private var lastVisibleItem: Int = 0
    private var totalItemCount: Int = 0
    private var mGridLayoutManager: GridLayoutManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        presenter.onAttachView(this)

        initView()

//        setUpLoadMoreListener()

        val firstLoad = preferenceHelper.firstOpen
        if (firstLoad) {
            loadGenreList()
        }
    }

    private fun loadGenreList() {
        presenter.fetchGenres(preferenceHelper.apiKey)
        preferenceHelper.firstOpen = false
    }

    private fun initView() {
        mProgressBar = findViewById(R.id.progress_bar)
        mRecyclerView = findViewById(R.id.recycler_movies)
        mSwipeRefreshLayout = findViewById(R.id.swipeRefreshLayout)
        mConnectionLayout = findViewById(R.id.connection_layout)

        mGridLayoutManager = GridLayoutManager(this@MovieActivity, 2)
        mRecyclerView.layoutManager = mGridLayoutManager
        mRecyclerView.setHasFixedSize(true)

        mSwipeRefreshLayout.setOnRefreshListener(this)

    }

    private fun setUpLoadMoreListener() {
        if (sortedType != "favourite") {
            mRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    visibleItemCount = mGridLayoutManager!!.childCount
                    totalItemCount = mGridLayoutManager!!.itemCount
                    lastVisibleItem = mGridLayoutManager!!.findFirstVisibleItemPosition()

                    if (visibleItemCount + lastVisibleItem >= totalItemCount && !isLoading) {
                        pageNumber++
                        loadMoviesList()
                        isLoading = true
                    }
                }
            })
        }
    }

    private fun loadMoviesList() {
        presenter.loadMoviesList(sortedType, pageNumber, preferenceHelper.apiKey)
    }

    override fun onRefresh() {
        if (sortedType != "favourite") {
            loadMoviesList()
        }
    }

    override fun saveGenreList(lists: ArrayList<Genre>) {
        repository.saveGenreList(this, lists)
    }

    override fun showMoviesList(lists: ArrayList<MovieResult>) {
        if (pageNumber == 1) {
            mAdapter = MovieAdapter(this, lists)
            mRecyclerView.adapter = mAdapter
            mSwipeRefreshLayout.isRefreshing = false
        } else {
            mAdapter!!.addItems(lists)
            mRecyclerView.adapter = mAdapter
            mAdapter!!.notifyDataSetChanged()
            mSwipeRefreshLayout.isRefreshing = false
            isLoading = false
            mGridLayoutManager?.scrollToPosition(mAdapter!!.itemCount - lists.size)
        }
    }

    override fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    override fun showProgress() {
        mProgressBar.visibility = View.VISIBLE
    }

    override fun hideProgress() {
        mProgressBar.visibility = View.GONE
    }

    override fun onStart() {
        super.onStart()
        pageNumber = 1

        sortedType = loadPreference()
        if (sortedType == "1" || sortedType == "popularity.desc" || sortedType == "") {
            supportActionBar?.title = "Popular Movies"
            loadMoviesList()
        } else if (sortedType == "favourite") {
            supportActionBar?.title = "Favourite Movies"
            val list = repository.loadMoviesList(this)
            mAdapter = MovieAdapter(this, list)
            mRecyclerView.adapter = mAdapter
        } else {
            supportActionBar?.title = "Top Rated Movies"
            loadMoviesList()
        }

    }

    private fun loadPreference(): String {
        sortedType = preferenceHelper.sortedType
        if (sortedType == "1" || sortedType == "") {
            sortedType = "popularity.desc"
        }
        return sortedType
    }

    override fun onDestroy() {
        presenter.onDetachView()
        super.onDestroy()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_setting, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        val id = item!!.itemId
        if (id == R.id.action_settings) {
            startActivity(Intent(this, SettingsActivity::class.java))
            return true
        }

        return super.onOptionsItemSelected(item)
    }

}
