package com.khinthirisoe.popularmoviesappstage2.ui.movies.details

import com.khinthirisoe.popularmoviesappstage2.ui.movies.details.model.Movies
import com.khinthirisoe.popularmoviesappstage2.ui.movies.details.model.ReviewsResult
import com.khinthirisoe.popularmoviesappstage2.ui.movies.details.model.TrailersResult

interface DetailsContract {

    interface View{

        fun showMovieDetail(detail: Movies)

        fun showTrailersList(lists: ArrayList<TrailersResult>)

        fun showReviewsList(lists: ArrayList<ReviewsResult>)

        fun showMessage(message: String)

        fun showProgress()

        fun hideProgress()
    }

    interface Presenter{

        fun onAttachView(view: View)

        fun onDetachView()

        fun loadList(movieId: Int, key: String)
    }
}