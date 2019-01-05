package com.khinthirisoe.popularmoviesappstage2.ui.main

import com.khinthirisoe.popularmoviesappstage2.ui.base.BasePresenter
import com.khinthirisoe.popularmoviesappstage2.ui.base.BaseView
import com.khinthirisoe.popularmoviesappstage2.ui.main.model.Genre
import com.khinthirisoe.popularmoviesappstage2.ui.main.model.MovieResult

interface MainContract {

    interface View : BaseView<Presenter> {
        fun showMoviesList(lists: ArrayList<MovieResult>)

        fun saveGenreList(lists: ArrayList<Genre>)

        fun showMessage(message: String)

        fun showProgress()

        fun hideProgress()
    }

    interface Presenter : BasePresenter<View> {

        fun onAttachView(view: View)

        fun onDetachView()

        fun loadMoviesList(sortedType: String, page: Int, key: String)

        fun fetchGenres(key: String)
    }
}
