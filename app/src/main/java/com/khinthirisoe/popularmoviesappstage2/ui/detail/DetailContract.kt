package com.khinthirisoe.popularmoviesappstage2.ui.detail

import com.khinthirisoe.popularmoviesappstage2.ui.base.BasePresenter
import com.khinthirisoe.popularmoviesappstage2.ui.base.BaseView
import com.khinthirisoe.popularmoviesappstage2.ui.detail.model.MovieInfo
import com.khinthirisoe.popularmoviesappstage2.ui.detail.model.ReviewsResult
import com.khinthirisoe.popularmoviesappstage2.ui.detail.model.VideoResult

interface DetailContract {

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