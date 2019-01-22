package com.khinthirisoe.popularmoviesappstage2.ui.movies.overview.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.khinthirisoe.popularmoviesappstage2.R
import com.khinthirisoe.popularmoviesappstage2.data.db.repository.MovieRepository
import com.khinthirisoe.popularmoviesappstage2.data.pref.AppPreferencesHelper
import com.khinthirisoe.popularmoviesappstage2.ui.movies.overview.MovieContract
import com.khinthirisoe.popularmoviesappstage2.ui.movies.overview.model.Genre
import com.khinthirisoe.popularmoviesappstage2.ui.movies.overview.model.MovieResult
import org.koin.android.ext.android.inject

class MovieFragment : Fragment(), MovieContract.View, SwipeRefreshLayout.OnRefreshListener {

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

    private fun loadGenreList() {
        presenter.fetchGenres(preferenceHelper.apiKey)
        preferenceHelper.firstOpen = false
    }

    private fun initView(view: View) {
        mProgressBar = view.findViewById(R.id.progress_bar)
        mRecyclerView = view.findViewById(R.id.recycler_movies)
        mSwipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout)
        mConnectionLayout = view.findViewById(R.id.connection_layout)

        mGridLayoutManager = GridLayoutManager(context, 2)
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
        } else {
            return
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
        repository.saveGenreList(context!!, lists)
    }

    override fun showMoviesList(lists: ArrayList<MovieResult>) {
        if (pageNumber == 1) {
            mAdapter = MovieAdapter(lists)
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
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
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
            loadMoviesList()
        } else if (sortedType == "favourite") {
            val list = repository.loadMoviesList(context!!)
            mAdapter = MovieAdapter(list)
            mRecyclerView.adapter = mAdapter
        } else {
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_movie, container, false)

        presenter.onAttachView(this)

        initView(view)

//        setUpLoadMoreListener()

        val firstLoad = preferenceHelper.firstOpen
        if (firstLoad) {
            loadGenreList()
        }

        return view
    }

    companion object {
        @JvmStatic
        fun newInstance(): MovieFragment {
            val movieFragment = MovieFragment()
            return movieFragment
        }
    }
}
