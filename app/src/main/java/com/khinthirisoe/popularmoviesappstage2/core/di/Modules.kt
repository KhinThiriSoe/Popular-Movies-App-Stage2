package com.khinthirisoe.popularmoviesappstage2.core.di

import com.google.gson.Gson
import com.khinthirisoe.popularmoviesappstage2.core.config.ApiUrl
import com.khinthirisoe.popularmoviesappstage2.ui.detail.DetailContract
import com.khinthirisoe.popularmoviesappstage2.ui.detail.model.DetailRepository
import com.khinthirisoe.popularmoviesappstage2.ui.detail.presenter.DetailPresenter
import com.khinthirisoe.popularmoviesappstage2.ui.detail.view.DetailActivity
import com.khinthirisoe.popularmoviesappstage2.ui.main.MainContract
import com.khinthirisoe.popularmoviesappstage2.ui.main.model.MainRepository
import com.khinthirisoe.popularmoviesappstage2.ui.main.model.MovieApiService
import com.khinthirisoe.popularmoviesappstage2.ui.main.presenter.MainPresenter
import com.khinthirisoe.popularmoviesappstage2.ui.main.view.MainActivity
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

    factory { MainActivity() }
    factory { MainPresenter(get()) as MainContract.Presenter }
    single { MainRepository(get()) }

    factory { DetailActivity() }
    factory { DetailPresenter(get()) as DetailContract.Presenter }
    single { DetailRepository(get()) }

    single { (ApiService()) }
}

fun ApiService(): MovieApiService {

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