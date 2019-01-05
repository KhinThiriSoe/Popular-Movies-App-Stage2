package com.khinthirisoe.popularmoviesappstage2.ui.detail.model

import com.khinthirisoe.popularmoviesappstage2.ui.detail.presenter.DetailPresenter
import com.khinthirisoe.popularmoviesappstage2.ui.main.model.MovieApiService
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Function3
import io.reactivex.schedulers.Schedulers


class DetailRepository(private val mApiService: MovieApiService) {

    fun loadList(movieId: Int, key: String, listener: DetailPresenter.OnListFetchListener) {

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