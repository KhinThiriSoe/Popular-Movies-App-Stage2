package com.khinthirisoe.popularmoviesappstage2.ui.main.model

import com.khinthirisoe.popularmoviesappstage2.ui.main.presenter.MainPresenter
import io.reactivex.Observer
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class MainRepository(private val mApiService: MovieApiService) {

    fun loadMoviesList(sortedType: String, page: Int, key: String, listener: MainPresenter.OnMoviesListFetchListener) {
        mApiService.getSortedMoviesList(sortedType, page, key)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<Movies> {
                override fun onComplete() {
                }

                override fun onSubscribe(d: Disposable) {
                    listener.onListsFetchDisposable(d)
                }

                override fun onNext(t: Movies) {
                    listener.onMoviesListFetched(t.results)
                }

                override fun onError(e: Throwable) {
                    listener.onListsFetchFailed()
                }

            })
    }

    fun fetchGenres(key: String, listener: MainPresenter.OnGenresListFetchListener) {
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