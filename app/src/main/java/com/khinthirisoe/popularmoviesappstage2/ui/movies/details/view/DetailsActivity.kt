package com.khinthirisoe.popularmoviesappstage2.ui.movies.details.view

import android.annotation.SuppressLint
import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.khinthirisoe.popularmoviesappstage2.GlideApp
import com.khinthirisoe.popularmoviesappstage2.R
import com.khinthirisoe.popularmoviesappstage2.core.config.ApiUrl
import com.khinthirisoe.popularmoviesappstage2.data.MovieContract.Genres
import com.khinthirisoe.popularmoviesappstage2.data.MovieContract.Movies
import com.khinthirisoe.popularmoviesappstage2.data.MovieContract.Reviews
import com.khinthirisoe.popularmoviesappstage2.data.MovieContract.Trailers
import com.khinthirisoe.popularmoviesappstage2.ui.movies.details.DetailsContract
import com.khinthirisoe.popularmoviesappstage2.ui.movies.details.model.ReviewsResult
import com.khinthirisoe.popularmoviesappstage2.ui.movies.details.model.TrailersResult
import com.khinthirisoe.popularmoviesappstage2.ui.movies.overview.model.Genre
import com.khinthirisoe.popularmoviesappstage2.ui.movies.overview.model.MovieResult
import com.khinthirisoe.popularmoviesappstage2.utils.PrefUtils
import org.koin.android.ext.android.inject

class DetailsActivity : AppCompatActivity(), DetailsContract.View {

    val presenter: DetailsContract.Presenter by inject()

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
    private var menu: Menu? = null

    private var detail: com.khinthirisoe.popularmoviesappstage2.ui.movies.details.model.Movies? = null
    private var review: ArrayList<ReviewsResult>? = ArrayList()
    private var trailer: ArrayList<TrailersResult>? = ArrayList()

    private var showingFirst: Boolean = true
    private var movieId: Int? = null
    private var sortedType: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        presenter.onAttachView(this)

        initView()

        setUpToolbar()

        setUpListener()

        sortedType = PrefUtils.getSortedTypeKey(this) ?: "1"

        checkIntent()

    }

    private fun initView() {
        mRootLayout = findViewById(R.id.root_layout)
        mTitle = findViewById(R.id.txt_title)
        mBackPoster = findViewById(R.id.img_backposter)
        mPoster = findViewById(R.id.img_poster)
        mOverviewTitle = findViewById(R.id.txt_title_overview)
        mOverview = findViewById(R.id.txt_overview)
        mAverageVote = findViewById(R.id.txt_average_vote)
        mGenre = findViewById(R.id.txt_genre)
        mReleaseDate = findViewById(R.id.txt_release_date)
        mReviewTitle = findViewById(R.id.txt_title_reviews)
        mRecyclerReview = findViewById(R.id.recycler_reviews)
        mRecyclerVideo = findViewById(R.id.recycler_trailers)
        mProgressBar = findViewById(R.id.progress_bar)
        mFab = findViewById(R.id.fab)

        mRecyclerReview.layoutManager = LinearLayoutManager(this)
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        mRecyclerVideo.layoutManager = layoutManager

        animationUp = AnimationUtils.loadAnimation(this, R.anim.slide_up)
        animationDown = AnimationUtils.loadAnimation(this, R.anim.slide_down)
    }

    private fun checkIntent() {
        if (intent.hasExtra("data")) {
            val movieResult = intent.getParcelableExtra<MovieResult>("data")
            movieId = movieResult.id
            if (sortedType == "favourite") {
                setUpUI(movieResult)
                loadDataFromDb()
            } else {
                presenter.loadList(movieId!!, PrefUtils.getApiKey(this))
            }
        }
    }

    private fun setUpUI(detail: MovieResult) {
        mFab.visibility = View.VISIBLE
        mFab.setImageResource(R.drawable.ic_favorite)

        mFab.setOnClickListener {
            mFab.setImageResource(R.drawable.ic_favorite_border)
            removeMovieDetail()
            removeReview()
            removeTrailer()
        }
        mRootLayout.visibility = View.VISIBLE

        mTitle.text = detail.title

        GlideApp.with(this)
            .load(ApiUrl.POSTER_PATH + detail.backdropPath)
            .placeholder(R.drawable.ic_movie)
            .into(mBackPoster)

        GlideApp.with(this)
            .load(ApiUrl.POSTER_PATH + detail.posterPath)
            .placeholder(R.drawable.ic_movie)
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

        val cursor = contentResolver.query(
            Movies.CONTENT_URI, null,
            "movie_id=?",
            arrayOf(movieId.toString()), null
        )

        when {
            cursor == null -> {
                // error - log some message
            }
            cursor.count < 1 -> // nothing to show  - log some message
                Log.d("message", "cursor.getCount() < 1")
            else -> {
                val list = arrayListOf<String>()
                cursor.moveToFirst()
                Log.d("message", "count ${cursor.count} ")
                do {
                    val id = cursor.getString(cursor.getColumnIndex("genre_id"))

                    Log.d("message", "genre_id $id ")
                    list.add(id)
                } while (cursor.moveToNext())

                val generCursor = contentResolver.query(Genres.CONTENT_URI, null, null, null, null)
                val genreName = arrayListOf<Genre>()
                when {
                    generCursor == null -> {
                        // error - log some message
                    }
                    generCursor.count < 1 -> // nothing to show  - log some message
                        Log.d("message", "cursor.getCount() < 1")
                    else -> {
                        generCursor.moveToFirst()
                        Log.d("message", "cursor count " + generCursor.count)
                        do {
                            val id = generCursor.getInt(generCursor.getColumnIndex("genre_id"))
                            val name = generCursor.getString(generCursor.getColumnIndex("genre_name"))
                            val result = Genre(id, name)
                            genreName.add(result)
                        } while (generCursor.moveToNext())
                    }

                }
                generCursor.close()
                val results = arrayListOf<String>()

                for (i in list) {
                    for (item in 0 until genreName.size) {
                        if (i == genreName[item].id.toString()) {
                            results.add(genreName[item].name)
                        }
                    }
                }


                var name: String = ""
                for (item in results) {
                    name += "$item, "
                }
                mGenre.text = name.substring(0, name.length - 2)

            }
        }
        cursor.close()
    }

    private fun loadTrailers() {

        val cursor = contentResolver.query(
            Trailers.CONTENT_URI, null,
            "movie_id=?",
            arrayOf(movieId.toString()), null
        )

        val list = ArrayList<TrailersResult>()
        when {
            cursor == null -> {
// error - log some message
            }
            cursor.count < 1 -> // nothing to show  - log some message
                Log.d("message", "cursor.getCount() < 1")
            else -> {
                cursor.moveToFirst()
                do {

                    val id = cursor.getString(cursor.getColumnIndex("trailer_id"))
                    val key = cursor.getString(cursor.getColumnIndex("trailer_key"))

                    Log.d("message", "id $id key $key")

                    val result = TrailersResult(id, "", "", key, "", "", 0, "")

                    list.add(result)
                } while (cursor.moveToNext())
            }
        }
        cursor.close()

        if (list.size == 0) mRecyclerVideo.visibility = View.GONE

        mTrailerAdapter = TrailersAdapter(this, list)
        mRecyclerVideo.adapter = mTrailerAdapter
    }

    private fun loadReviews() {
        val cursor = contentResolver.query(
            Reviews.CONTENT_URI, null,
            "movie_id=?",
            arrayOf(movieId.toString()), null
        )

        val list = ArrayList<ReviewsResult>()
        when {
            cursor == null -> {
// error - log some message
            }
            cursor.count < 1 -> // nothing to show  - log some message
                Log.d("message", "cursor.getCount() < 1")
            else -> {
                cursor.moveToFirst()
                do {

                    val id = cursor.getString(cursor.getColumnIndex("review_id"))
                    val author = cursor.getString(cursor.getColumnIndex("author"))
                    val content = cursor.getString(cursor.getColumnIndex("content"))

                    Log.d("message", "id $id author $author content $content")

                    val result = ReviewsResult(author, content, id, "")

                    list.add(result)
                } while (cursor.moveToNext())
            }
        }
        cursor.close()

        if (list.size == 0) mRecyclerReview.visibility = View.GONE

        mReviewTitle.setOnClickListener {
            if (mRecyclerReview.isShown) {
                mReviewTitle.setCompoundDrawablesWithIntrinsicBounds(
                    0, //left
                    0, //top
                    R.drawable.ic_down_arrow, //right
                    0
                )
                mRecyclerReview.startAnimation(animationUp)
                mRecyclerReview.visibility = View.GONE
            } else {
                mReviewTitle.setCompoundDrawablesWithIntrinsicBounds(
                    0, //left
                    0, //top
                    R.drawable.ic_up_arrow, //right
                    0
                )
                mRecyclerReview.visibility = View.VISIBLE
                mRecyclerReview.startAnimation(animationDown)
                mReviewsAdapter = ReviewsAdapter(this, list)
                mRecyclerReview.adapter = mReviewsAdapter
            }
        }
    }

    private fun setUpToolbar() {
        val mToolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(mToolbar)

        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val mAppBarLayout = findViewById<AppBarLayout>(R.id.app_bar)
        mAppBarLayout.addOnOffsetChangedListener(object : AppBarLayout.OnOffsetChangedListener {
            var isShow = false
            var scrollRange = -1

            override fun onOffsetChanged(appBarLayout: AppBarLayout, verticalOffset: Int) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.totalScrollRange
                }
                if (scrollRange + verticalOffset == 0) {
                    isShow = true
                    showOption(R.id.action_save)
                } else if (isShow) {
                    isShow = false
                    hideOption(R.id.action_save)
                }
            }
        })
    }

    private fun setUpListener() {
        mFab.setOnClickListener {

            showingFirst = if (showingFirst) {
                mFab.setImageResource(R.drawable.ic_favorite)
                saveMovieDetail()
                saveReview()
                saveTrailer()
                false
            } else {
                mFab.setImageResource(R.drawable.ic_favorite_border)
                removeMovieDetail()
                removeReview()
                removeTrailer()
                true
            }
        }
    }

    private fun removeTrailer() {
        contentResolver.delete(
            Trailers.CONTENT_URI,
            Trailers.COL_MOVIE_ID + "=?",
            arrayOf(movieId.toString())
        )
    }

    private fun removeReview() {
        contentResolver.delete(
            Reviews.CONTENT_URI,
            Reviews.COL_MOVIE_ID + "=?",
            arrayOf(movieId.toString())
        )

    }

    private fun removeMovieDetail() {
        contentResolver.delete(
            Movies.CONTENT_URI,
            Movies.COL_ID + "=?",
            arrayOf(movieId.toString())
        )

    }

    private fun saveTrailer() {

        if (trailer!!.size > 0) {
            val contentValues = ArrayList<ContentValues>(trailer!!.size)
            for (result in trailer!!) {
                val values = ContentValues(3)
                values.put(Trailers.COL_ID, result.id)
                values.put(Trailers.COL_KEY, result.key)
                values.put(Trailers.COL_MOVIE_ID, movieId)
                contentValues.add(values)
            }
            val id = contentResolver.bulkInsert(Trailers.CONTENT_URI, contentValues.toTypedArray())
            Log.d("message", "trailer bulk insert " + id.toString())
        }

        val cursor = contentResolver.query(Trailers.CONTENT_URI, null, null, null, null)

        when {
            cursor == null -> {
// error - log some message
            }
            cursor.count < 1 -> // nothing to show  - log some message
                Log.d("message", "trailer cursor.getCount() < 1")
            else -> while (cursor.moveToNext()) {
                Log.d("message", cursor.getString(0) + "-" + cursor.getString(1))
            }
        }
        cursor.close()
    }

    private fun saveReview() {

        if (review!!.size > 0) {
            val contentValues = ArrayList<ContentValues>(review!!.size)
            for (result in review!!) {
                val values = ContentValues(4)
                values.put(Reviews.COL_ID, result.id)
                values.put(Reviews.COL_AUTHOR, result.author)
                values.put(Reviews.COL_CONTENT, result.content)
                values.put(Reviews.COL_MOVIE_ID, movieId)
                contentValues.add(values)
            }
            val id = contentResolver.bulkInsert(Reviews.CONTENT_URI, contentValues.toTypedArray())
            Log.d("message", "review bulk insert " + id.toString())
        }

        val cursor = contentResolver.query(Reviews.CONTENT_URI, null, null, null, null)

        when {
            cursor == null -> {
// error - log some message
            }
            cursor.count < 1 -> // nothing to show  - log some message
                Log.d("message", "review cursor.getCount() < 1")
            else -> while (cursor.moveToNext()) {
                Log.d("message", cursor.getString(0) + "-" + cursor.getString(1))
            }
        }
        cursor.close()
    }

    private fun saveMovieDetail() {

        if (detail!!.genres.isNotEmpty()) {
            val contentValues = ArrayList<ContentValues>(detail!!.genres.size)
            for (result in detail!!.genres) {
                val values = ContentValues(8)
                values.put(Movies.COL_ID, movieId)
                values.put(Movies.COL_NAME, detail?.title)
                values.put(Movies.COL_BACK_PATH, detail?.backdropPath)
                values.put(Movies.COL_POSTER, detail?.posterPath)
                values.put(Movies.COL_AVERAGE_VOTE, detail?.voteAverage)
                values.put(Movies.COL_RELEASE_DATE, detail?.releaseDate)
                values.put(Movies.COL_OVERVIEW, detail?.overview)
                values.put(Movies.COL_GENRE_ID, result.id)

                contentValues.add(values)
            }
            val id = contentResolver.bulkInsert(Movies.CONTENT_URI, contentValues.toTypedArray())
            Log.d("message", "detail bulk insert " + id.toString())
        }

        val cursor = contentResolver.query(Movies.CONTENT_URI, null, null, null, null)

        when {
            cursor == null -> {
// error - log some message
            }
            cursor.count < 1 -> // nothing to show  - log some message
                Log.d("message", "detail cursor.getCount() < 1")
            else -> while (cursor.moveToNext()) {
                Log.d("message", cursor.getString(0) + "-" + cursor.getString(1))
            }
        }
        cursor.close()
    }

    @SuppressLint("RestrictedApi")
    override fun showMovieDetail(detail: com.khinthirisoe.popularmoviesappstage2.ui.movies.details.model.Movies) {
        this.detail = detail

        mFab.visibility = View.VISIBLE

        mRootLayout.visibility = View.VISIBLE

        mTitle.text = detail.title

        GlideApp.with(this)
            .load(ApiUrl.POSTER_PATH + detail.backdropPath)
            .placeholder(R.drawable.ic_movie)
            .into(mBackPoster)

        GlideApp.with(this)
            .load(ApiUrl.POSTER_PATH + detail.posterPath)
            .placeholder(R.drawable.ic_movie)
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

        if (lists.size == 0) mRecyclerReview.visibility = View.GONE

        mReviewTitle.setOnClickListener {
            if (mRecyclerReview.isShown) {
                mReviewTitle.setCompoundDrawablesWithIntrinsicBounds(
                    0, //left
                    0, //top
                    R.drawable.ic_down_arrow, //right
                    0
                )
                mRecyclerReview.startAnimation(animationUp)
                mRecyclerReview.visibility = View.GONE
            } else {
                mReviewTitle.setCompoundDrawablesWithIntrinsicBounds(
                    0, //left
                    0, //top
                    R.drawable.ic_up_arrow, //right
                    0
                )
                mRecyclerReview.visibility = View.VISIBLE
                mRecyclerReview.startAnimation(animationDown)
                mReviewsAdapter = ReviewsAdapter(this, lists)
                mRecyclerReview.adapter = mReviewsAdapter
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun showTrailersList(lists: ArrayList<TrailersResult>) {

        trailer = lists

        if (lists.size == 0) mRecyclerVideo.visibility = View.GONE

        mTrailerAdapter = TrailersAdapter(this, lists)
        mRecyclerVideo.adapter = mTrailerAdapter
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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        this.menu = menu
// Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_detail, menu)
        hideOption(R.id.action_save)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
// Handle action bar item clicks here. The action bar will
// automatically handle clicks on the Home/Up button, so long
// as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId

        if (id == R.id.action_save) {
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
}
