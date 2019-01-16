package com.khinthirisoe.popularmoviesappstage2.ui.movies.overview.model

import com.khinthirisoe.popularmoviesappstage2.ui.movies.overview.presenter.MoviePresenter
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class MovieRepository(private val mApiService: MovieApiService) {

    fun loadMoviesList(sortedType: String, page: Int, key: String, listener: MoviePresenter.OnMoviesListFetchListener) {
        mApiService.getSortedMoviesList(sortedType, page, key)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : SingleObserver<MovieInfo> {
                override fun onSuccess(t: MovieInfo) {
                    listener.onMoviesListFetched(t.results)
                }

                override fun onSubscribe(d: Disposable) {
                    listener.onListsFetchDisposable(d)
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
            .subscribe(object : SingleObserver<Genres> {
                override fun onSuccess(t: Genres) {
                    listener.onGenresListFetched(t.genres)
                }

                override fun onSubscribe(d: Disposable) {
                    listener.onListsFetchDisposable(d)
                }

                override fun onError(e: Throwable) {
                    listener.onListsFetchFailed()
                }

            })
    }
}