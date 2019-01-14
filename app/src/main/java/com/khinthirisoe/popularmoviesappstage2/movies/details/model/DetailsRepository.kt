package com.khinthirisoe.popularmoviesappstage2.movies.details.model

import com.khinthirisoe.popularmoviesappstage2.movies.details.presenter.DetailsPresenter
import com.khinthirisoe.popularmoviesappstage2.movies.overview.model.MovieApiService
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Function3
import io.reactivex.schedulers.Schedulers


class DetailsRepository(private val mApiService: MovieApiService) {

    fun loadList(movieId: Int, key: String, listener: DetailsPresenter.OnListFetchListener) {

        val observable1 = mApiService.getReviewsList(movieId, key)
        val observable2 = mApiService.getVideoList(movieId, key)
        val observable3 = mApiService.getMovieDetail(movieId, key)

        Observable.zip(
            observable1,
            observable2,
            observable3,
            Function3<Reviews, Videos, MovieInfo, ZipResponse>(::ZipResponse)
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
                    listener.onVideoListFetched(t.videos.results)
                    listener.onMovieDetailListFetched(t.movieInfo)
                }

                override fun onError(e: Throwable) {
                    listener.onListsFetchFailed()
                }

            })
    }

}