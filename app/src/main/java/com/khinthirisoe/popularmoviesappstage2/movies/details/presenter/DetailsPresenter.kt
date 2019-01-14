package com.khinthirisoe.popularmoviesappstage2.movies.details.presenter

import com.khinthirisoe.popularmoviesappstage2.movies.details.DetailsContract
import com.khinthirisoe.popularmoviesappstage2.movies.details.model.DetailsRepository
import com.khinthirisoe.popularmoviesappstage2.movies.details.model.MovieInfo
import com.khinthirisoe.popularmoviesappstage2.movies.details.model.ReviewsResult
import com.khinthirisoe.popularmoviesappstage2.movies.details.model.VideoResult
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import org.koin.standalone.KoinComponent

class DetailsPresenter constructor(var interactor: DetailsRepository) : DetailsContract.Presenter, KoinComponent {
    var view: DetailsContract.View? = null
    var videoList: ArrayList<VideoResult>? = null
    var reviewsList: ArrayList<ReviewsResult>? = null
    var movie: MovieInfo? = null
    var compositeDisposable: CompositeDisposable = CompositeDisposable()

    override fun loadList(movieId: Int, key: String) {
        if (view != null) view?.showProgress()
        interactor.loadList(movieId, key, object :
            DetailsPresenter.OnListFetchListener {
            override fun onMovieDetailListFetched(movie: MovieInfo) {
                this@DetailsPresenter.movie = movie
                if (view != null) {
                    view?.showMovieDetail(movie)
                    view?.hideProgress()
                }
            }

            override fun onReviewsListFetched(lists: ArrayList<ReviewsResult>) {
                this@DetailsPresenter.reviewsList = lists
                if (view != null) {
                    view?.showReviewsList(lists)
                    view?.hideProgress()
                }
            }

            override fun onVideoListFetched(lists: ArrayList<VideoResult>) {
                this@DetailsPresenter.videoList = lists
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

    override fun onAttachView(view: DetailsContract.View) {
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