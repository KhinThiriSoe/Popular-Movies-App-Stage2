package com.khinthirisoe.popularmoviesappstage2.di

import com.khinthirisoe.popularmoviesappstage2.data.db.repository.DetailsRepository
import com.khinthirisoe.popularmoviesappstage2.data.db.repository.MovieRepository
import com.khinthirisoe.popularmoviesappstage2.data.network.MovieApiService.apiService
import com.khinthirisoe.popularmoviesappstage2.data.pref.AppPreferencesHelper
import com.khinthirisoe.popularmoviesappstage2.ui.movies.details.DetailsContract
import com.khinthirisoe.popularmoviesappstage2.ui.movies.details.model.DetailsInteractor
import com.khinthirisoe.popularmoviesappstage2.ui.movies.details.presenter.DetailsPresenter
import com.khinthirisoe.popularmoviesappstage2.ui.movies.details.view.DetailsActivity
import com.khinthirisoe.popularmoviesappstage2.ui.movies.details.view.ReviewsAdapter
import com.khinthirisoe.popularmoviesappstage2.ui.movies.details.view.TrailersAdapter
import com.khinthirisoe.popularmoviesappstage2.ui.movies.overview.MovieContract
import com.khinthirisoe.popularmoviesappstage2.ui.movies.overview.model.MovieInteractor
import com.khinthirisoe.popularmoviesappstage2.ui.movies.overview.presenter.MoviePresenter
import com.khinthirisoe.popularmoviesappstage2.ui.movies.overview.view.MovieActivity
import com.khinthirisoe.popularmoviesappstage2.ui.movies.overview.view.MovieAdapter
import org.koin.dsl.module.module

/**
 * Koin main module
 */
val MovieAppModule = module {

    factory { MovieActivity() }
    factory { MoviePresenter(get()) as MovieContract.Presenter }
    single { MovieInteractor(get()) }

    factory { DetailsActivity() }
    factory { DetailsPresenter(get()) as DetailsContract.Presenter }
    single { DetailsInteractor(get()) }

    single { (apiService()) }
    single { AppPreferencesHelper(get()) }

    single { MovieRepository() }
    single { DetailsRepository() }

    factory { MovieAdapter(get()) }
    factory { TrailersAdapter(get()) }
    factory { ReviewsAdapter(get()) }
}

val movieAppModules = listOf(MovieAppModule)