package com.khinthirisoe.popularmoviesappstage2.di

import com.google.gson.Gson
import com.khinthirisoe.popularmoviesappstage2.core.config.ApiUrl
import com.khinthirisoe.popularmoviesappstage2.ui.movies.details.DetailsContract
import com.khinthirisoe.popularmoviesappstage2.ui.movies.details.model.DetailsRepository
import com.khinthirisoe.popularmoviesappstage2.ui.movies.details.presenter.DetailsPresenter
import com.khinthirisoe.popularmoviesappstage2.ui.movies.details.view.DetailsActivity
import com.khinthirisoe.popularmoviesappstage2.ui.movies.overview.MovieContract
import com.khinthirisoe.popularmoviesappstage2.ui.movies.overview.model.MovieApiService
import com.khinthirisoe.popularmoviesappstage2.ui.movies.overview.model.MovieRepository
import com.khinthirisoe.popularmoviesappstage2.ui.movies.overview.presenter.MoviePresenter
import com.khinthirisoe.popularmoviesappstage2.ui.movies.overview.view.MovieActivity
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Koin main module
 */
val MovieAppModule = module {

    factory { MovieActivity() }
    factory { MoviePresenter(get()) as MovieContract.Presenter }
    single { MovieRepository(get()) }

    factory { DetailsActivity() }
    factory { DetailsPresenter(get()) as DetailsContract.Presenter }
    single { DetailsRepository(get()) }

    single { (apiService()) }
}

fun apiService(): MovieApiService {

    val interceptor = HttpLoggingInterceptor()
    interceptor.level = HttpLoggingInterceptor.Level.BODY

    val client = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val original = chain.request()
            val requestBuilder = original.newBuilder()
                .addHeader("Content-Type", "application/json")
            val request = requestBuilder.build()
            chain.proceed(request)
        }
    client.connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .addInterceptor(interceptor)

    return Retrofit.Builder().baseUrl(ApiUrl.BASE_URL + ApiUrl.VERSION)
        .client(client.build())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .addConverterFactory(GsonConverterFactory.create(Gson()))
        .build()
        .create(MovieApiService::class.java)
}

val movieAppModules = listOf(MovieAppModule)