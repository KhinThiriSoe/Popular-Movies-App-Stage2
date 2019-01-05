package com.khinthirisoe.popularmoviesappstage2.ui.main.presenter

import com.khinthirisoe.popularmoviesappstage2.ui.main.MainContract
import com.khinthirisoe.popularmoviesappstage2.ui.main.model.Genre
import com.khinthirisoe.popularmoviesappstage2.ui.main.model.MainRepository
import com.khinthirisoe.popularmoviesappstage2.ui.main.model.MovieResult
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import org.koin.standalone.KoinComponent

class MainPresenter constructor(var interactor: MainRepository) : MainContract.Presenter, KoinComponent {
    var view: MainContract.View? = null
    var moviesList: ArrayList<MovieResult>? = null
    var genresList: ArrayList<Genre>? = null
    var compositeDisposable: CompositeDisposable = CompositeDisposable()

    override fun loadMoviesList(sortedType: String, page: Int, key: String) {
        if (view != null) view?.showProgress()
        interactor.loadMoviesList(sortedType, page, key, object :
            OnMoviesListFetchListener {
            override fun onMoviesListFetched(lists: ArrayList<MovieResult>) {
                this@MainPresenter.moviesList = lists
                if (view != null) {
                    view?.showMoviesList(lists)
                    view?.hideProgress()
                }
            }

            override fun onListsFetchFailed() {
                if (view != null) {
                    view?.showMessage("Fetch failed")
                    view?.hideProgress()
                }
            }

            override fun onListsFetchDisposable(d: Disposable) {
                compositeDisposable.add(d)
            }
        })
    }

    override fun fetchGenres(key: String) {
        if (view != null) view?.showProgress()
        interactor.fetchGenres(key, object :
            OnGenresListFetchListener {
            override fun onGenresListFetched(lists: ArrayList<Genre>) {
                this@MainPresenter.genresList = lists
                if (view != null) {
                    view?.saveGenreList(lists)
                    view?.hideProgress()
                }
            }

            override fun onListsFetchFailed() {
                if (view != null) {
                    view?.showMessage("Fetch failed")
                    view?.hideProgress()
                }
            }

            override fun onListsFetchDisposable(d: Disposable) {
                compositeDisposable.add(d)
            }
        })
    }

    override fun onAttachView(view: MainContract.View) {
        this.view = view
    }

    override fun onDetachView() {
        compositeDisposable.clear()
        this.view = null
    }

    interface OnMoviesListFetchListener {
        fun onMoviesListFetched(lists: ArrayList<MovieResult>)

        fun onListsFetchFailed()

        fun onListsFetchDisposable(d: Disposable)

    }

    interface OnGenresListFetchListener {
        fun onGenresListFetched(lists: ArrayList<Genre>)

        fun onListsFetchFailed()

        fun onListsFetchDisposable(d: Disposable)

    }
}