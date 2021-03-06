package com.khinthirisoe.popularmoviesappstage2.di

import com.khinthirisoe.popularmoviesappstage2.data.db.repository.DetailsRepository
import com.khinthirisoe.popularmoviesappstage2.data.db.repository.MovieRepository
import com.khinthirisoe.popularmoviesappstage2.data.network.MovieApiService.apiService
import com.khinthirisoe.popularmoviesappstage2.data.pref.AppPreferencesHelper
import com.khinthirisoe.popularmoviesappstage2.ui.movies.details.DetailsContract
import com.khinthirisoe.popularmoviesappstage2.ui.movies.details.model.DetailsInteractor
import com.khinthirisoe.popularmoviesappstage2.ui.movies.details.presenter.DetailsPresenter
import com.khinthirisoe.popularmoviesappstage2.ui.movies.details.view.DetailsFragment
import com.khinthirisoe.popularmoviesappstage2.ui.movies.overview.MovieContract
import com.khinthirisoe.popularmoviesappstage2.ui.movies.overview.model.MovieInteractor
import com.khinthirisoe.popularmoviesappstage2.ui.movies.overview.presenter.MoviePresenter
import com.khinthirisoe.popularmoviesappstage2.ui.movies.overview.view.MovieFragment
import org.koin.dsl.module.module

/**
 * Koin main module
 */
val MovieAppModule = module {

    factory { MovieFragment() }
    factory { MoviePresenter(get()) as MovieContract.Presenter }
    single { MovieInteractor(get()) }

    factory { DetailsFragment() }
    factory { DetailsPresenter(get()) as DetailsContract.Presenter }
    single { DetailsInteractor(get()) }

    single { (apiService()) }
    single { AppPreferencesHelper(get()) }

    single { MovieRepository() }
    single { DetailsRepository() }
}

val movieAppModules = listOf(MovieAppModule)