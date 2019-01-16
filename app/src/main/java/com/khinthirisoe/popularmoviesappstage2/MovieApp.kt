package com.khinthirisoe.popularmoviesappstage2

import android.app.Application
import com.khinthirisoe.popularmoviesappstage2.di.movieAppModules
import org.koin.android.ext.android.startKoin

class MovieApp : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin(this, movieAppModules)
    }
}

