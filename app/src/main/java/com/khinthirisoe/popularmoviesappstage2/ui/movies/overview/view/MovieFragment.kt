package com.khinthirisoe.popularmoviesappstage2.ui.movies.overview.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.flexbox.AlignItems
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.khinthirisoe.popularmoviesappstage2.data.db.repository.MovieRepository
import com.khinthirisoe.popularmoviesappstage2.data.pref.AppPreferencesHelper
import com.khinthirisoe.popularmoviesappstage2.ui.movies.overview.MovieContract
import com.khinthirisoe.popularmoviesappstage2.ui.movies.overview.model.Genre
import com.khinthirisoe.popularmoviesappstage2.ui.movies.overview.model.MovieResult
import org.koin.android.ext.android.inject


class MovieFragment : Fragment(), MovieAdapter.MovieListRecyclerViewClickListener,
    MovieContract.View, SwipeRefreshLayout.OnRefreshListener {

    val presenter: MovieContract.Presenter by inject()
    val repository: MovieRepository by inject()
    val preferenceHelper: AppPreferencesHelper by inject()

    private var mAdapter: MovieAdapter? = null
    private lateinit var mProgressBar: ProgressBar
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mSwipeRefreshLayout: SwipeRefreshLayout
    private lateinit var mConnectionLayout: LinearLayout
    private lateinit var sortedType: String

    private var listener: OnFragmentInteractionListener? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(com.khinthirisoe.popularmoviesappstage2.R.layout.fragment_movie, container, false)

        presenter.onAttachView(this)

        initView(view)

        val firstLoad = preferenceHelper.firstOpen
        if (firstLoad) {
            loadGenreList()
        }

        return view
    }

    private fun loadGenreList() {
        presenter.fetchGenres(preferenceHelper.apiKey)
        preferenceHelper.firstOpen = false
    }

    private fun initView(view: View) {
        mProgressBar = view.findViewById(com.khinthirisoe.popularmoviesappstage2.R.id.progress_bar)
        mRecyclerView = view.findViewById(com.khinthirisoe.popularmoviesappstage2.R.id.recycler_movies)
        mSwipeRefreshLayout = view.findViewById(com.khinthirisoe.popularmoviesappstage2.R.id.swipeRefreshLayout)
        mConnectionLayout = view.findViewById(com.khinthirisoe.popularmoviesappstage2.R.id.connection_layout)

        val layoutManager = FlexboxLayoutManager(context)
        layoutManager.flexDirection = FlexDirection.ROW
        layoutManager.justifyContent = JustifyContent.CENTER
        layoutManager.alignItems = AlignItems.STRETCH
        mRecyclerView.layoutManager = layoutManager

        mSwipeRefreshLayout.setOnRefreshListener(this)

    }

    private fun loadMoviesList() {
        presenter.loadMoviesList(sortedType, 1, preferenceHelper.apiKey)
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
        mAdapter = MovieAdapter(lists, this)
        mRecyclerView.adapter = mAdapter
        mSwipeRefreshLayout.isRefreshing = false
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    interface OnFragmentInteractionListener {
        fun onListItemClicked(list: MovieResult)
    }

    companion object {
        @JvmStatic
        fun newInstance(): MovieFragment {
            val listSelectionFragment = MovieFragment()
            return listSelectionFragment
        }
    }

    override fun listItemClick(list: MovieResult) {
        listener?.onListItemClicked(list)
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

        sortedType = loadPreference()
        if (sortedType == "1" || sortedType == "popularity.desc" || sortedType == "") {
            loadMoviesList()
        } else if (sortedType == "favourite") {
            val list = repository.loadMoviesList(context!!)
            mAdapter = MovieAdapter(list, this)
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
}
