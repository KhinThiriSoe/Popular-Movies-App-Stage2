package com.khinthirisoe.popularmoviesappstage2.ui.movies.details

import com.khinthirisoe.popularmoviesappstage2.core.base.BasePresenter
import com.khinthirisoe.popularmoviesappstage2.core.base.BaseView
import com.khinthirisoe.popularmoviesappstage2.ui.movies.details.model.Movies
import com.khinthirisoe.popularmoviesappstage2.ui.movies.details.model.ReviewsResult
import com.khinthirisoe.popularmoviesappstage2.ui.movies.details.model.TrailersResult

interface DetailsContract {

    interface View : BaseView<Presenter> {

        fun showMovieDetail(detail: Movies)

        fun showTrailersList(lists: ArrayList<TrailersResult>)

        fun showReviewsList(lists: ArrayList<ReviewsResult>)

        fun showMessage(message: String)

        fun showProgress()

        fun hideProgress()
    }

    interface Presenter : BasePresenter<View> {

        fun onAttachView(view: View)

        fun onDetachView()

        fun loadList(movieId: Int, key: String)
    }
}