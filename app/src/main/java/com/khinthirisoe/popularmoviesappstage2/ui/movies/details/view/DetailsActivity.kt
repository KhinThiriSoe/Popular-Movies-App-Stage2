package com.khinthirisoe.popularmoviesappstage2.ui.movies.details.view

import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.khinthirisoe.popularmoviesappstage2.R
import com.khinthirisoe.popularmoviesappstage2.ui.movies.overview.model.MovieResult
import com.khinthirisoe.popularmoviesappstage2.ui.movies.overview.view.MovieActivity

class DetailsActivity : AppCompatActivity(){

    private var menu: Menu? = null
    private var detailsFragment: DetailsFragment? = null
    private lateinit var mFab: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        val list = intent.getParcelableExtra<MovieResult>(MovieActivity.INTENT_LIST_KEY)
        title = list.title

        detailsFragment = DetailsFragment.newInstance(list)
        supportFragmentManager.beginTransaction()
            .add(R.id.fragment_container, detailsFragment!!, "Detail Fragment")
            .commit()

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
