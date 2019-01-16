package com.khinthirisoe.popularmoviesappstage2.ui.movies.overview.view

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import com.khinthirisoe.popularmoviesappstage2.data.MovieContract.Genres
import com.khinthirisoe.popularmoviesappstage2.data.MovieContract.Movies
import com.khinthirisoe.popularmoviesappstage2.ui.movies.overview.MovieContract
import com.khinthirisoe.popularmoviesappstage2.ui.movies.overview.model.Genre
import com.khinthirisoe.popularmoviesappstage2.ui.movies.overview.model.MovieResult
import com.khinthirisoe.popularmoviesappstage2.ui.settings.SettingsActivity
import com.khinthirisoe.popularmoviesappstage2.utils.PrefUtils
import org.koin.android.ext.android.inject


class MovieActivity : AppCompatActivity(), MovieContract.View, SwipeRefreshLayout.OnRefreshListener {

    val presenter: MovieContract.Presenter by inject()

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

        sortedType = PrefUtils.getSortedTypeKey(this) ?: "1"

        initView()

        setUpLoadMoreListener()

        val firstLoad = PrefUtils.getFirstLoadDataKey(this)
        if (firstLoad!!) {
            loadGenreList()
        }
    }

    private fun loadGenreList() {
        PrefUtils.putFirstLoadDataKey(this, false)
        presenter.fetchGenres(PrefUtils.getApiKey(this))

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
        presenter.loadMoviesList(sortedType, pageNumber, PrefUtils.getApiKey(this@MovieActivity))
    }

    override fun onRefresh() {
        if (sortedType != "favourite") {
            loadMoviesList()
        }
    }

    override fun saveGenreList(lists: ArrayList<Genre>) {
        val contentValues = ArrayList<ContentValues>(lists.size)
        for (genre in lists) {
            val values = ContentValues(2)
            values.put(Genres.COL_ID, genre.id)
            values.put(Genres.COL_NAME, genre.name)
            contentValues.add(values)
        }
        val id = contentResolver.bulkInsert(Genres.CONTENT_URI, contentValues.toTypedArray())
        Log.d("message", "bulk insert " + id.toString())

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
            loadMoviesListFromDb()
        } else {
            supportActionBar?.title = "Top Rated Movies"
            loadMoviesList()
        }

    }

    private fun loadMoviesListFromDb() {
        val cursor = contentResolver.query(Movies.CONTENT_URI, null, null, null, null)

        when {
            cursor == null -> {
                // error - log some message
            }
            cursor.count < 1 -> // nothing to show  - log some message
            {
                mAdapter = MovieAdapter(this, null)
                mRecyclerView.adapter = mAdapter
                Log.d("message", "detail cursor.getCount() < 1")

            }
            else -> {

                val list = ArrayList<MovieResult>()
                cursor.moveToFirst()
                do {
                    for (i in 0 until cursor.columnCount) {

                        val id = cursor.getString(cursor.getColumnIndex("movie_id"))
                        val name = cursor.getString(cursor.getColumnIndex("movie_name"))
                        val back_path = cursor.getString(cursor.getColumnIndex("back_path"))
                        val poster = cursor.getString(cursor.getColumnIndex("poster"))
                        val average_vote = cursor.getString(cursor.getColumnIndex("average_vote"))
                        val releaseDate = cursor.getString(cursor.getColumnIndex("release_date"))
                        val overview = cursor.getString(cursor.getColumnIndex("overview"))

                        val result = MovieResult(
                            0, id.toInt(), false, average_vote.toDouble(), name, 0.0, poster, "", name,
                            arrayListOf(), back_path, false, overview, releaseDate
                        )

                        list.add(result)
                    }
                } while (cursor.moveToNext())

                val modifiedList = arrayListOf<MovieResult>()
                for (i in list) {
                    if (!modifiedList.contains(i)) {
                        modifiedList.add(i)
                    }
                }

                mAdapter = MovieAdapter(this, modifiedList)
                mRecyclerView.adapter = mAdapter
            }

        }
        cursor.close()
    }

    private fun loadPreference(): String {
        return PrefUtils.getSortedTypeKey(this) ?: "1"
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
