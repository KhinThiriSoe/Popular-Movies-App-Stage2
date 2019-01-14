package com.khinthirisoe.popularmoviesappstage2.movies.details

import com.khinthirisoe.popularmoviesappstage2.core.base.BasePresenter
import com.khinthirisoe.popularmoviesappstage2.core.base.BaseView
import com.khinthirisoe.popularmoviesappstage2.movies.details.model.MovieInfo
import com.khinthirisoe.popularmoviesappstage2.movies.details.model.ReviewsResult
import com.khinthirisoe.popularmoviesappstage2.movies.details.model.VideoResult

interface DetailsContract {

    interface View : BaseView<Presenter> {

        fun showMovieDetail(detail: MovieInfo)

        fun showVideoList(lists: ArrayList<VideoResult>)

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