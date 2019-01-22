package com.khinthirisoe.popularmoviesappstage2.ui.movies.overview.view

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.khinthirisoe.popularmoviesappstage2.R
import com.khinthirisoe.popularmoviesappstage2.data.pref.AppPreferencesHelper
import com.khinthirisoe.popularmoviesappstage2.ui.settings.SettingsActivity
import org.koin.android.ext.android.inject

class MovieActivity : AppCompatActivity() {

    val preferenceHelper: AppPreferencesHelper by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


    }

    override fun onStart() {
        super.onStart()

        val sortedType = preferenceHelper.sortedType
        if (sortedType == "1" || sortedType == "popularity.desc" || sortedType == "") {
            supportActionBar?.title = "Popular Movies"
        } else if (sortedType == "favourite") {
            supportActionBar?.title = "Favourite Movies"
        } else {
            supportActionBar?.title = "Top Rated Movies"
        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_setting, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        val id = item!!.itemId
        if (id == R.id.action_settings) {
            startActivity(Intent(this, SettingsActivity::class.java))
            return true
        }

        return super.onOptionsItemSelected(item)
    }



}
