package com.khinthirisoe.popularmoviesappstage2.ui.movies.details.view

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.*
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.khinthirisoe.popularmoviesappstage2.GlideApp
import com.khinthirisoe.popularmoviesappstage2.data.db.repository.DetailsRepository
import com.khinthirisoe.popularmoviesappstage2.data.network.ApiEndPoint
import com.khinthirisoe.popularmoviesappstage2.data.pref.AppPreferencesHelper
import com.khinthirisoe.popularmoviesappstage2.ui.movies.details.DetailsContract
import com.khinthirisoe.popularmoviesappstage2.ui.movies.details.model.ReviewsResult
import com.khinthirisoe.popularmoviesappstage2.ui.movies.details.model.TrailersResult
import com.khinthirisoe.popularmoviesappstage2.ui.movies.overview.model.MovieResult
import com.khinthirisoe.popularmoviesappstage2.ui.movies.overview.view.MovieActivity
import org.koin.android.ext.android.inject


class DetailsFragment : Fragment(), DetailsContract.View {

    val presenter: DetailsContract.Presenter by inject()
    val repository: DetailsRepository by inject()
    val preferencesHelper: AppPreferencesHelper by inject()

    private var animationUp: Animation? = null
    private var animationDown: Animation? = null
    private lateinit var mRootLayout: LinearLayout
    private lateinit var mTitle: TextView
    private lateinit var mBackPoster: ImageView
    private lateinit var mPoster: ImageView
    private lateinit var mOverviewTitle: TextView
    private lateinit var mOverview: TextView
    private lateinit var mAverageVote: TextView
    private lateinit var mGenre: TextView
    private lateinit var mReleaseDate: TextView
    private lateinit var mReviewTitle: TextView
    private lateinit var mTrailerAdapter: TrailersAdapter
    private lateinit var mReviewsAdapter: ReviewsAdapter
    private lateinit var mRecyclerVideo: RecyclerView
    private lateinit var mRecyclerReview: RecyclerView
    private lateinit var mProgressBar: ProgressBar
    private lateinit var mFab: FloatingActionButton
    private lateinit var mEmptyReview: TextView
    private lateinit var mEmptyTrailer: TextView

    private var detail: com.khinthirisoe.popularmoviesappstage2.ui.movies.details.model.Details? = null
    private var review: ArrayList<ReviewsResult>? = ArrayList()
    private var trailer: ArrayList<TrailersResult>? = ArrayList()

    private var showingFirst: Boolean = true
    private var movieId: Int? = null
    private var sortedType: String? = null
    private var menu: Menu? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        presenter.onAttachView(this)

        sortedType = preferencesHelper.sortedType
    }

    private fun setUpToolbar(view: View) {
        val mToolbar = view.findViewById<Toolbar>(com.khinthirisoe.popularmoviesappstage2.R.id.toolbar)
        (activity as AppCompatActivity).setSupportActionBar(mToolbar)

        (activity as AppCompatActivity).supportActionBar?.setHomeButtonEnabled(true)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val mAppBarLayout = view.findViewById<AppBarLayout>(com.khinthirisoe.popularmoviesappstage2.R.id.app_bar)
        mAppBarLayout.addOnOffsetChangedListener(object : AppBarLayout.OnOffsetChangedListener {
            var isShow = false
            var scrollRange = -1

            override fun onOffsetChanged(appBarLayout: AppBarLayout, verticalOffset: Int) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.totalScrollRange
                }
                if (scrollRange + verticalOffset == 0) {
                    isShow = true
                    showOption(com.khinthirisoe.popularmoviesappstage2.R.id.action_save)
                } else if (isShow) {
                    isShow = false
                    hideOption(com.khinthirisoe.popularmoviesappstage2.R.id.action_save)
                }
            }
        })
    }

    private fun initView(view: View) {
        mRootLayout = view.findViewById(com.khinthirisoe.popularmoviesappstage2.R.id.root_layout)
        mTitle = view.findViewById(com.khinthirisoe.popularmoviesappstage2.R.id.txt_title)
        mBackPoster = view.findViewById(com.khinthirisoe.popularmoviesappstage2.R.id.img_backposter)
        mPoster = view.findViewById(com.khinthirisoe.popularmoviesappstage2.R.id.img_poster)
        mOverviewTitle = view.findViewById(com.khinthirisoe.popularmoviesappstage2.R.id.txt_title_overview)
        mOverview = view.findViewById(com.khinthirisoe.popularmoviesappstage2.R.id.txt_overview)
        mAverageVote = view.findViewById(com.khinthirisoe.popularmoviesappstage2.R.id.txt_average_vote)
        mGenre = view.findViewById(com.khinthirisoe.popularmoviesappstage2.R.id.txt_genre)
        mReleaseDate = view.findViewById(com.khinthirisoe.popularmoviesappstage2.R.id.txt_release_date)
        mReviewTitle = view.findViewById(com.khinthirisoe.popularmoviesappstage2.R.id.txt_title_reviews)
        mRecyclerReview = view.findViewById(com.khinthirisoe.popularmoviesappstage2.R.id.recycler_reviews)
        mRecyclerVideo = view.findViewById(com.khinthirisoe.popularmoviesappstage2.R.id.recycler_trailers)
        mProgressBar = view.findViewById(com.khinthirisoe.popularmoviesappstage2.R.id.progress_bar)
        mFab = view.findViewById(com.khinthirisoe.popularmoviesappstage2.R.id.fab)
        mEmptyReview = view.findViewById(com.khinthirisoe.popularmoviesappstage2.R.id.no_reviews)
        mEmptyTrailer = view.findViewById(com.khinthirisoe.popularmoviesappstage2.R.id.no_trailers)

        mRecyclerReview.layoutManager = LinearLayoutManager(context!!)
        val layoutManager = LinearLayoutManager(context!!, LinearLayoutManager.HORIZONTAL, false)
        mRecyclerVideo.layoutManager = layoutManager

        animationUp = AnimationUtils.loadAnimation(context!!, com.khinthirisoe.popularmoviesappstage2.R.anim.slide_up)
        animationDown =
                AnimationUtils.loadAnimation(context!!, com.khinthirisoe.popularmoviesappstage2.R.anim.slide_down)
    }

    private fun checkIntent() {
        val movieResult = arguments!!.getParcelable<MovieResult>(MovieActivity.INTENT_LIST_KEY)
        if (movieResult != null) {
            movieId = movieResult.id
            if (sortedType == "favourite") {
                setUpUI(movieResult)
                loadDataFromDb()
            } else {
                presenter.loadList(movieId!!, preferencesHelper.apiKey)
            }
        }
    }

    private fun setUpUI(detail: MovieResult) {
        mFab.visibility = View.VISIBLE
        mFab.setImageResource(com.khinthirisoe.popularmoviesappstage2.R.drawable.ic_favorite)

        mFab.setOnClickListener {
            mFab.setImageResource(com.khinthirisoe.popularmoviesappstage2.R.drawable.ic_favorite_border)

            repository.removeMovieDetail(context!!, movieId.toString())
            repository.removeReview(context!!, movieId.toString())
            repository.removeTrailer(context!!, movieId.toString())

        }
        mRootLayout.visibility = View.VISIBLE

        mTitle.text = detail.title

        if (detail.backdropPath.isEmpty()) {
            GlideApp.with(this)
                .load(com.khinthirisoe.popularmoviesappstage2.R.drawable.ic_movie)
                .placeholder(com.khinthirisoe.popularmoviesappstage2.R.drawable.ic_movie)
                .into(mBackPoster)

        } else {
            GlideApp.with(this)
                .load(ApiEndPoint.POSTER_PATH + detail.backdropPath)
                .placeholder(com.khinthirisoe.popularmoviesappstage2.R.drawable.ic_movie)
                .into(mBackPoster)
        }


        GlideApp.with(this)
            .load(ApiEndPoint.POSTER_PATH + detail.posterPath)
            .placeholder(com.khinthirisoe.popularmoviesappstage2.R.drawable.ic_movie)
            .into(mPoster)

        mAverageVote.text = detail.voteAverage.toString()

        mReleaseDate.text = detail.releaseDate

        mOverview.text = detail.overview
    }

    private fun loadDataFromDb() {
        loadReviews()
        loadTrailers()
        loadGenres()
    }

    private fun loadGenres() {

        val results = repository.loadGenres(context!!, movieId.toString())

        var name: String = ""
        for (item in results) {
            name += "$item, "
        }
        mGenre.text = name.substring(0, name.length - 2)
    }

    private fun loadTrailers() {

        val list = repository.loadTrailers(context!!, movieId.toString())

        if (list.size == 0) {
            mRecyclerVideo.visibility = View.GONE
            mEmptyTrailer.visibility = View.VISIBLE
        }

        mTrailerAdapter = TrailersAdapter(list)
        mRecyclerVideo.adapter = mTrailerAdapter
    }

    private fun loadReviews() {

        val list = repository.loadReviews(context!!, movieId.toString())

        if (list.size == 0) {
            mRecyclerReview.visibility = View.GONE
            mEmptyReview.visibility = View.VISIBLE
            mReviewTitle.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
        }

        mReviewTitle.setOnClickListener {
            if (mRecyclerReview.isShown) {
                mReviewTitle.setCompoundDrawablesWithIntrinsicBounds(
                    0, //left
                    0, //top
                    com.khinthirisoe.popularmoviesappstage2.R.drawable.ic_down_arrow, //right
                    0
                )
                mRecyclerReview.startAnimation(animationUp)
                mRecyclerReview.visibility = View.GONE
                mEmptyReview.visibility = View.GONE
            } else {
                mReviewTitle.setCompoundDrawablesWithIntrinsicBounds(
                    0, //left
                    0, //top
                    com.khinthirisoe.popularmoviesappstage2.R.drawable.ic_up_arrow, //right
                    0
                )
                mRecyclerReview.visibility = View.VISIBLE
                mEmptyReview.visibility = View.GONE
                mRecyclerReview.startAnimation(animationDown)
                mReviewsAdapter = ReviewsAdapter(list)
                mRecyclerReview.adapter = mReviewsAdapter
            }
        }
    }

    private fun setUpListener() {
        mFab.setOnClickListener {

            showingFirst = if (showingFirst) {
                mFab.setImageResource(com.khinthirisoe.popularmoviesappstage2.R.drawable.ic_favorite)

                repository.saveMovieDetail(context!!, detail!!, movieId.toString())
                repository.saveReview(context!!, review, movieId.toString())
                repository.saveTrailer(context!!, trailer, movieId.toString())

                false
            } else {
                mFab.setImageResource(com.khinthirisoe.popularmoviesappstage2.R.drawable.ic_favorite_border)
                repository.removeMovieDetail(context!!, movieId.toString())
                repository.removeReview(context!!, movieId.toString())
                repository.removeTrailer(context!!, movieId.toString())
                true
            }
        }
    }

    @SuppressLint("RestrictedApi")
    override fun showMovieDetail(detail: com.khinthirisoe.popularmoviesappstage2.ui.movies.details.model.Details) {
        this.detail = detail

        mFab.visibility = View.VISIBLE

        mRootLayout.visibility = View.VISIBLE

        mTitle.text = detail.title

        GlideApp.with(this)
            .load(ApiEndPoint.POSTER_PATH + detail.backdropPath)
            .placeholder(com.khinthirisoe.popularmoviesappstage2.R.drawable.ic_movie)
            .into(mBackPoster)

        GlideApp.with(this)
            .load(ApiEndPoint.POSTER_PATH + detail.posterPath)
            .placeholder(com.khinthirisoe.popularmoviesappstage2.R.drawable.ic_movie)
            .into(mPoster)

        mAverageVote.text = detail.voteAverage.toString()
        var name: String = ""
        for (item in detail.genres) {
            name += item.name + ", "
        }
        mGenre.text = name.substring(0, name.length - 2)
        mReleaseDate.text = detail.releaseDate

        mOverview.text = detail.overview

    }

    override fun showReviewsList(lists: ArrayList<ReviewsResult>) {

        review = lists

        if (lists.size == 0) {
            mRecyclerReview.visibility = View.GONE
            mEmptyReview.visibility = View.VISIBLE
            mReviewTitle.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
        }

        mReviewTitle.setOnClickListener {
            if (mRecyclerReview.isShown) {
                mReviewTitle.setCompoundDrawablesWithIntrinsicBounds(
                    0, //left
                    0, //top
                    com.khinthirisoe.popularmoviesappstage2.R.drawable.ic_down_arrow, //right
                    0
                )
                mRecyclerReview.startAnimation(animationUp)
                mRecyclerReview.visibility = View.GONE
                mEmptyReview.visibility = View.GONE
            } else {
                mReviewTitle.setCompoundDrawablesWithIntrinsicBounds(
                    0, //left
                    0, //top
                    com.khinthirisoe.popularmoviesappstage2.R.drawable.ic_up_arrow, //right
                    0
                )
                mRecyclerReview.visibility = View.VISIBLE
                mEmptyReview.visibility = View.GONE
                mRecyclerReview.startAnimation(animationDown)
                mReviewsAdapter = ReviewsAdapter(lists)
                mRecyclerReview.adapter = mReviewsAdapter
            }
        }
    }

    override fun showTrailersList(lists: ArrayList<TrailersResult>) {

        trailer = lists

        if (lists.size == 0) {
            mRecyclerVideo.visibility = View.GONE
            mEmptyTrailer.visibility = View.VISIBLE
        }

        mTrailerAdapter = TrailersAdapter(lists)
        mRecyclerVideo.adapter = mTrailerAdapter
    }

    override fun showMessage(message: String) {
        Toast.makeText(context!!, message, Toast.LENGTH_LONG).show()
    }

    override fun showProgress() {
        mProgressBar.visibility = View.VISIBLE
    }

    override fun hideProgress() {
        mProgressBar.visibility = View.GONE
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(com.khinthirisoe.popularmoviesappstage2.R.layout.fragment_details, container, false)

        initView(view)


        if (!preferencesHelper.isLargeScreen) {
            setUpToolbar(view)
        }

        setUpListener()

        checkIntent()

        return view
    }

    companion object {
        fun newInstance(list: MovieResult): DetailsFragment {
            val fragment = DetailsFragment()
            val args = Bundle()
            args.putParcelable(MovieActivity.INTENT_LIST_KEY, list)
            fragment.arguments = args
            return fragment
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        this.menu = menu
        inflater?.inflate(com.khinthirisoe.popularmoviesappstage2.R.menu.menu_detail, menu)
        hideOption(com.khinthirisoe.popularmoviesappstage2.R.id.action_save)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == com.khinthirisoe.popularmoviesappstage2.R.id.action_save) {
            val menu = menu?.getItem(0)
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    private fun hideOption(id: Int) {
        val item = menu?.findItem(id)
        item?.isVisible = false
    }

    private fun showOption(id: Int) {
        val item = menu?.findItem(id)
        item?.icon = mFab.drawable
        item?.isVisible = true
    }

    override fun onStop() {
        super.onStop()
        presenter.onDetachView()
    }
}
