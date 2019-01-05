package com.khinthirisoe.popularmoviesappstage2.ui.detail.presenter

import com.khinthirisoe.popularmoviesappstage2.ui.detail.DetailContract
import com.khinthirisoe.popularmoviesappstage2.ui.detail.model.DetailRepository
import com.khinthirisoe.popularmoviesappstage2.ui.detail.model.MovieInfo
import com.khinthirisoe.popularmoviesappstage2.ui.detail.model.ReviewsResult
import com.khinthirisoe.popularmoviesappstage2.ui.detail.model.VideoResult
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import org.koin.standalone.KoinComponent

class DetailPresenter constructor(var interactor: DetailRepository) : DetailContract.Presenter, KoinComponent {
    var view: DetailContract.View? = null
    var videoList: ArrayList<VideoResult>? = null
    var reviewsList: ArrayList<ReviewsResult>? = null
    var movie: MovieInfo? = null
    var compositeDisposable: CompositeDisposable = CompositeDisposable()

    override fun loadList(movieId: Int, key: String) {
        if (view != null) view?.showProgress()
        interactor.loadList(movieId, key, object :
            DetailPresenter.OnListFetchListener {
            override fun onMovieDetailListFetched(movie: MovieInfo) {
                this@DetailPresenter.movie = movie
                if (view != null) {
                    view?.showMovieDetail(movie)
                    view?.hideProgress()
                }
            }

            override fun onReviewsListFetched(lists: ArrayList<ReviewsResult>) {
                this@DetailPresenter.reviewsList = lists
                if (view != null) {
                    view?.showReviewsList(lists)
                    view?.hideProgress()
                }
            }

            override fun onVideoListFetched(lists: ArrayList<VideoResult>) {
                this@DetailPresenter.videoList = lists
                if (view != null) {
                    view?.showVideoList(lists)
                    view?.hideProgress()
                }
            }

            override fun onListsFetchDisposable(d: Disposable) {
                compositeDisposable.add(d)
            }

            override fun onListsFetchFailed() {
                if (view != null) {
                    view?.showMessage("Fetch failed")
                    view?.hideProgress()
                }

            }
        })
    }

    override fun onAttachView(view: DetailContract.View) {
        this.view = view
    }

    override fun onDetachView() {
        compositeDisposable.clear()
        this.view = null
    }

    interface OnListFetchListener {

        fun onMovieDetailListFetched(movie: MovieInfo)

        fun onReviewsListFetched(lists: ArrayList<ReviewsResult>)

        fun onVideoListFetched(lists: ArrayList<VideoResult>)

        fun onListsFetchDisposable(d: Disposable)

        fun onListsFetchFailed()
    }
}