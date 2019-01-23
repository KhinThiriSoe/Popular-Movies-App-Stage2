package com.khinthirisoe.popularmoviesappstage2.ui.movies.overview.view

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.FrameLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.khinthirisoe.popularmoviesappstage2.R
import com.khinthirisoe.popularmoviesappstage2.ui.movies.details.view.DetailsActivity
import com.khinthirisoe.popularmoviesappstage2.ui.movies.details.view.DetailsFragment
import com.khinthirisoe.popularmoviesappstage2.ui.movies.overview.model.MovieResult
import com.khinthirisoe.popularmoviesappstage2.ui.settings.SettingsActivity


class MovieActivity : AppCompatActivity(), MovieFragment.OnFragmentInteractionListener {

    private var movieFragment: MovieFragment = MovieFragment()
    private var fragmentContainer: FrameLayout? = null
    private var listDetailFragment: DetailsFragment? = null

    companion object {
        val INTENT_LIST_KEY = "list"
        val LIST_DETAIL_REQUEST_CODE = 123
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        movieFragment =
                supportFragmentManager.findFragmentById(R.id.list_selection_fragment) as MovieFragment
        fragmentContainer = findViewById(R.id.fragment_container)
        movieFragment.preferenceHelper.isLargeScreen = fragmentContainer != null
    }

    override fun onStart() {
        super.onStart()

        val sortedType = movieFragment.preferenceHelper.sortedType
        if (sortedType == "1" || sortedType == "popularity.desc" || sortedType == "") {
            supportActionBar?.title = "Popular Movies"
        } else if (sortedType == "favourite") {
            supportActionBar?.title = "Favourite Movies"
        } else {
            supportActionBar?.title = "Top Rated Movies"
        }

    }

    override fun onListItemClicked(list: MovieResult) {
        showListDetail(list)
    }

    private fun showListDetail(list: MovieResult) {
        if (!movieFragment.preferenceHelper.isLargeScreen) {
            val listDetailIntent = Intent(this, DetailsActivity::class.java)
            listDetailIntent.putExtra(INTENT_LIST_KEY, list)
            startActivityForResult(listDetailIntent, LIST_DETAIL_REQUEST_CODE)
        } else {
            listDetailFragment = DetailsFragment.newInstance(list)
            supportFragmentManager.beginTransaction()
                .replace(
                    R.id.fragment_container,
                    listDetailFragment!!,
                    "Detail Fragment"
                )
                .addToBackStack(null)
                .commit()
        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_setting, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        val id = item!!.itemId
        if (id == R.id.action_settings) {
            if (movieFragment.preferenceHelper.isLargeScreen) {
                settingsAlertDialog()
            } else {
                startActivity(Intent(this, SettingsActivity::class.java))
            }
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    private fun settingsAlertDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Choose Sort Type")

        val type = resources.getStringArray(R.array.listArray)
        builder.setItems(type) { dialog, which ->
            when (which) {
                0 -> {
                    movieFragment.preferenceHelper.sortedType = "popularity.desc"
                    reload()
                }
                1 -> {
                    movieFragment.preferenceHelper.sortedType = "top_rated"
                    reload()
                }
                2 -> {
                    movieFragment.preferenceHelper.sortedType = "favourite"
                    reload()
                }
            }
        }
        val dialog = builder.create()
        dialog.show()
    }

    private fun reload() {
        finish()
        overridePendingTransition(0, 0)
        startActivity(intent)
        overridePendingTransition(0, 0)
    }
}
