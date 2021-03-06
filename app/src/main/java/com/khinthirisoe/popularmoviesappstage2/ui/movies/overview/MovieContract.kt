package com.khinthirisoe.popularmoviesappstage2.ui.movies.overview

import com.khinthirisoe.popularmoviesappstage2.ui.movies.overview.model.Genre
import com.khinthirisoe.popularmoviesappstage2.ui.movies.overview.model.MovieResult

interface MovieContract {

    interface View {
        fun showMoviesList(lists: ArrayList<MovieResult>)

        fun saveGenreList(lists: ArrayList<Genre>)

        fun showMessage(message: String)

        fun showProgress()

        fun hideProgress()
    }

    interface Presenter {

        fun onAttachView(view: View)

        fun onDetachView()

        fun loadMoviesList(sortedType: String, page: Int, key: String)

        fun fetchGenres(key: String)
    }
}
