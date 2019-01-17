package com.khinthirisoe.popularmoviesappstage2.data.db.repository

import android.content.ContentValues
import android.content.Context
import android.util.Log
import com.khinthirisoe.popularmoviesappstage2.data.db.MovieContract.Genres
import com.khinthirisoe.popularmoviesappstage2.data.db.MovieContract.Movies
import com.khinthirisoe.popularmoviesappstage2.ui.movies.overview.model.Genre
import com.khinthirisoe.popularmoviesappstage2.ui.movies.overview.model.MovieResult

class MovieRepository {

    fun saveGenreList(context: Context, lists: ArrayList<Genre>) {
        val contentValues = ArrayList<ContentValues>(lists.size)
        for (genre in lists) {
            val values = ContentValues(2)
            values.put(Genres.COL_ID, genre.id)
            values.put(Genres.COL_NAME, genre.name)
            contentValues.add(values)
        }
        val id = context.contentResolver.bulkInsert(Genres.CONTENT_URI, contentValues.toTypedArray())
        Log.d("message", "bulk insert " + id.toString())

    }

    fun loadMoviesList(context: Context) : ArrayList<MovieResult>{
        val cursor = context.contentResolver.query(Movies.CONTENT_URI, null, null, null, null)

        val modifiedList = arrayListOf<MovieResult>()

        when {
            cursor == null -> {
                // error - log some message
            }
            cursor.count < 1 -> // nothing to show  - log some message
            {
                Log.d("message", "detail cursor.getCount() < 1")

            }
            else -> {

                val list = ArrayList<MovieResult>()
                cursor.moveToFirst()
                do {
                    for (i in 0 until cursor.columnCount) {

                        val id = cursor.getString(cursor.getColumnIndex(Movies.COL_ID))
                        val name = cursor.getString(cursor.getColumnIndex(Movies.COL_NAME))
                        val back_path = cursor.getString(cursor.getColumnIndex(Movies.COL_BACK_PATH))
                        val poster = cursor.getString(cursor.getColumnIndex(Movies.COL_POSTER))
                        val average_vote = cursor.getString(cursor.getColumnIndex(Movies.COL_AVERAGE_VOTE))
                        val releaseDate = cursor.getString(cursor.getColumnIndex(Movies.COL_RELEASE_DATE))
                        val overview = cursor.getString(cursor.getColumnIndex(Movies.COL_OVERVIEW))

                        val result = MovieResult(
                            0, id.toInt(), false, average_vote.toDouble(), name, 0.0, poster, "", name,
                            arrayListOf(), back_path, false, overview, releaseDate
                        )

                        list.add(result)
                    }
                } while (cursor.moveToNext())


                for (i in list) {
                    if (!modifiedList.contains(i)) {
                        modifiedList.add(i)
                    }
                }
            }

        }
        cursor.close()
        return modifiedList
    }
}