package com.khinthirisoe.popularmoviesappstage2.ui.main.view

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.khinthirisoe.popularmoviesappstage2.R
import com.khinthirisoe.popularmoviesappstage2.data.MoviesPersistenceContract.Genres
import com.khinthirisoe.popularmoviesappstage2.ui.base.BaseActivity
import com.khinthirisoe.popularmoviesappstage2.ui.settings.SettingsActivity
import com.khinthirisoe.popularmoviesappstage2.ui.main.MainContract
import com.khinthirisoe.popularmoviesappstage2.ui.main.model.Genre
import com.khinthirisoe.popularmoviesappstage2.ui.main.model.MovieResult
import com.khinthirisoe.popularmoviesappstage2.utils.PrefUtils
import io.reactivex.disposables.Disposable
import org.koin.android.ext.android.inject


class MainActivity : BaseActivity(), MainContract.View, SwipeRefreshLayout.OnRefreshListener {

    val presenter: MainContract.Presenter by inject()

    private var mAdapter: MovieAdapter? = null
    private lateinit var mProgressBar: ProgressBar
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mSwipeRefreshLayout: SwipeRefreshLayout
    private lateinit var mConnectionLayout: LinearLayout
    private lateinit var sortedType: String

    private var mConnectivityDisposable: Disposable? = null
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

        val firstLoad = PrefUtils.getFirstLoadDataKey(this)
        if (firstLoad!!) {
            loadGenreList()
        }

        setUpLoadMoreListener()
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

        mGridLayoutManager = GridLayoutManager(this@MainActivity, 2)
        mRecyclerView.layoutManager = mGridLayoutManager
        mRecyclerView.setHasFixedSize(true)

        mSwipeRefreshLayout.setOnRefreshListener(this)

    }

    private fun setUpLoadMoreListener() {
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

    private fun loadMoviesList() {
        presenter.loadMoviesList(sortedType, pageNumber, PrefUtils.getApiKey(this@MainActivity))

        /* mConnectivityDisposable = ReactiveNetwork
             .observeNetworkConnectivity(applicationContext)
             .subscribeOn(Schedulers.io())
             .observeOn(AndroidSchedulers.mainThread())
             .subscribe({ connectivity ->
                 if (connectivity.available()) {
                     mConnectionLayout.visibility = View.GONE
                     presenter.loadMoviesList(sortedType, pageNumber, PrefUtils.getApiKey(this@MainActivity))
                 } else {
                     mSwipeRefreshLayout.isRefreshing = false
                     mConnectionLayout.visibility = View.VISIBLE
                     Toast.makeText(this, R.string.no_internet, Toast.LENGTH_SHORT)
                         .show()
                 }
             }, { throwable ->
                 val message =
                     if (throwable.message == null) resources.getString(R.string.fetch_failed) else throwable.message
                 Log.d(TAG, message)
             })*/

    }

    override fun onRefresh() {
        loadMoviesList()
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

        /* val cursor = contentResolver.query(Genres.CONTENT_URI, null, null, null, null)

         when {
             cursor == null -> {
                 // error - log some message
             }
             cursor.count < 1 -> // nothing to show  - log some message
                 Log.d("message", "cursor.getCount() < 1")
             else -> while (cursor.moveToNext()) {
                 Log.d("message", cursor.getString(0) + "-" + cursor.getString(1))
             }
         }
         cursor.close()*/

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
        if (sortedType == "popularity.desc") {
            supportActionBar?.title = "Popular Movies"
        } else {
            supportActionBar?.title = "Top Rated Movies"
        }

        loadMoviesList()
    }

    private fun loadPreference(): String {
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val sortedType = prefs.getString("sortedType", "") ?: "popularity.desc"
        return sortedType
    }

    override fun onPause() {
        super.onPause()
        safelyDispose(mConnectivityDisposable)
    }

    private fun safelyDispose(disposable: Disposable?) {
        if (disposable != null && !disposable.isDisposed) {
            disposable.dispose()
        }
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
