package com.khinthirisoe.popularmoviesappstage2.ui.movies.overview.presenter

import com.khinthirisoe.popularmoviesappstage2.ui.movies.overview.MovieContract
import com.khinthirisoe.popularmoviesappstage2.ui.movies.overview.model.Genre
import com.khinthirisoe.popularmoviesappstage2.ui.movies.overview.model.MovieRepository
import com.khinthirisoe.popularmoviesappstage2.ui.movies.overview.model.MovieResult
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import org.koin.standalone.KoinComponent

class MoviePresenter constructor(var interactor: MovieRepository) : MovieContract.Presenter, KoinComponent {
    var view: MovieContract.View? = null
    var moviesList: ArrayList<MovieResult>? = null
    var genresList: ArrayList<Genre>? = null
    var compositeDisposable: CompositeDisposable = CompositeDisposable()

    override fun loadMoviesList(sortedType: String, page: Int, key: String) {
        if (view != null) view?.showProgress()
        interactor.loadMoviesList(sortedType, page, key, object :
            OnMoviesListFetchListener {
            override fun onMoviesListFetched(lists: ArrayList<MovieResult>) {
                this@MoviePresenter.moviesList = lists
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
                this@MoviePresenter.genresList = lists
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

    override fun onAttachView(view: MovieContract.View) {
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