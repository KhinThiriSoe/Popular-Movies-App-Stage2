package com.khinthirisoe.popularmoviesappstage2.ui.movies.details.model

import com.khinthirisoe.popularmoviesappstage2.data.network.ApiHelper
import com.khinthirisoe.popularmoviesappstage2.ui.movies.details.presenter.DetailsPresenter
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Function3
import io.reactivex.schedulers.Schedulers


class DetailsInteractor(private val mApiService: ApiHelper) {

    fun loadList(movieId: Int, key: String, listener: DetailsPresenter.OnListFetchListener) {

        val reviews = mApiService.getReviewsList(movieId, key)
        val trailers = mApiService.getTailersList(movieId, key)
        val movies = mApiService.getMoviesDetail(movieId, key)

        Observable.zip(
            reviews,
            trailers,
            movies,
            Function3<Reviews, Trailers, Details, ZipResponse>(::ZipResponse)
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<ZipResponse> {
                override fun onComplete() {
                }

                override fun onSubscribe(d: Disposable) {
                    listener.onListsFetchDisposable(d)
                }

                override fun onNext(t: ZipResponse) {
                    listener.onReviewsListFetched(t.reviews.results)
                    listener.onTrailersListFetched(t.videos.results)
                    listener.onMovieDetailListFetched(t.movieInfo)
                }

                override fun onError(e: Throwable) {
                    listener.onListsFetchFailed()
                }

            })
    }

}