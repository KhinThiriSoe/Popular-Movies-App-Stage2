package com.khinthirisoe.popularmoviesappstage2.ui.movies.overview.model

import com.khinthirisoe.popularmoviesappstage2.data.network.ApiHelper
import com.khinthirisoe.popularmoviesappstage2.ui.movies.overview.presenter.MoviePresenter
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class MovieInteractor(private val mApiService: ApiHelper) {

    fun loadMoviesList(sortedType: String, page: Int, key: String, listener: MoviePresenter.OnMoviesListFetchListener) {
        mApiService.getSortedMoviesList(sortedType, page, key)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<MovieInfo> {
                override fun onComplete() {
                }

                override fun onSubscribe(d: Disposable) {
                    listener.onListsFetchDisposable(d)
                }

                override fun onNext(t: MovieInfo) {
                    listener.onMoviesListFetched(t.results)
                }

                override fun onError(e: Throwable) {
                    listener.onListsFetchFailed()
                }

            })
    }

    fun fetchGenres(key: String, listener: MoviePresenter.OnGenresListFetchListener) {
        mApiService.getGenresList(key)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<Genres> {
                override fun onComplete() {
                }

                override fun onSubscribe(d: Disposable) {
                    listener.onListsFetchDisposable(d)
                }

                override fun onNext(t: Genres) {
                    listener.onGenresListFetched(t.genres)
                }

                override fun onError(e: Throwable) {
                    listener.onListsFetchFailed()
                }

            })
    }
}