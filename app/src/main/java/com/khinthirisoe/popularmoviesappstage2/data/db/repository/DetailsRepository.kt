package com.khinthirisoe.popularmoviesappstage2.data.db.repository

import android.content.Context
import android.util.Log
import com.khinthirisoe.popularmoviesappstage2.data.db.MovieContract.Genres
import com.khinthirisoe.popularmoviesappstage2.data.db.MovieContract.Movies
import com.khinthirisoe.popularmoviesappstage2.data.db.MovieContract.Reviews
import com.khinthirisoe.popularmoviesappstage2.data.db.MovieContract.Trailers
import com.khinthirisoe.popularmoviesappstage2.ui.movies.details.model.ReviewsResult
import com.khinthirisoe.popularmoviesappstage2.ui.movies.details.model.TrailersResult
import com.khinthirisoe.popularmoviesappstage2.ui.movies.overview.model.Genre

class DetailsRepository {

    fun loadReviews(context: Context, movieId: String): ArrayList<ReviewsResult> {
        val cursor = context.contentResolver.query(
            Reviews.CONTENT_URI, null,
            "movie_id=?",
            arrayOf(movieId), null
        )

        val list = ArrayList<ReviewsResult>()
        when {
            cursor == null -> {
                // error - log some message
            }
            cursor.count < 1 -> // nothing to show  - log some message
                Log.d("message", "cursor.getCount() < 1")
            else -> {
                cursor.moveToFirst()
                do {

                    val id = cursor.getString(cursor.getColumnIndex("review_id"))
                    val author = cursor.getString(cursor.getColumnIndex("author"))
                    val content = cursor.getString(cursor.getColumnIndex("content"))

                    Log.d("message", "id $id author $author content $content")

                    val result = ReviewsResult(author, content, id, "")

                    list.add(result)
                } while (cursor.moveToNext())
            }
        }
        cursor.close()
        return list
    }

    fun loadTrailers(context: Context, movieId: String): ArrayList<TrailersResult> {

        val cursor = context.contentResolver.query(
            Trailers.CONTENT_URI, null,
            "movie_id=?",
            arrayOf(movieId), null
        )

        val list = ArrayList<TrailersResult>()
        when {
            cursor == null -> {
// error - log some message
            }
            cursor.count < 1 -> // nothing to show  - log some message
                Log.d("message", "cursor.getCount() < 1")
            else -> {
                cursor.moveToFirst()
                do {

                    val id = cursor.getString(cursor.getColumnIndex("trailer_id"))
                    val key = cursor.getString(cursor.getColumnIndex("trailer_key"))

                    Log.d("message", "id $id key $key")

                    val result = TrailersResult(id, "", "", key, "", "", 0, "")

                    list.add(result)
                } while (cursor.moveToNext())
            }
        }
        cursor.close()

        return list
    }

    fun loadGenres(context: Context, movieId: String): ArrayList<String> {

        val list = arrayListOf<String>()
        val results = arrayListOf<String>()

        val cursor = context.contentResolver.query(
            Movies.CONTENT_URI, null,
            "movie_id=?",
            arrayOf(movieId.toString()), null
        )

        when {
            cursor == null -> {
                // error - log some message
            }
            cursor.count < 1 -> // nothing to show  - log some message
                Log.d("message", "cursor.getCount() < 1")
            else -> {

                cursor.moveToFirst()
                Log.d("message", "count ${cursor.count} ")
                do {
                    val id = cursor.getString(cursor.getColumnIndex("genre_id"))

                    Log.d("message", "genre_id $id ")
                    list.add(id)
                } while (cursor.moveToNext())

                val generCursor = context.contentResolver.query(Genres.CONTENT_URI, null, null, null, null)
                val genreName = arrayListOf<Genre>()
                when {
                    generCursor == null -> {
                        // error - log some message
                    }
                    generCursor.count < 1 -> // nothing to show  - log some message
                        Log.d("message", "cursor.getCount() < 1")
                    else -> {
                        generCursor.moveToFirst()
                        Log.d("message", "cursor count " + generCursor.count)
                        do {
                            val id = generCursor.getInt(generCursor.getColumnIndex("genre_id"))
                            val name = generCursor.getString(generCursor.getColumnIndex("genre_name"))
                            val result = Genre(id, name)
                            genreName.add(result)
                        } while (generCursor.moveToNext())
                    }

                }
                generCursor.close()

                for (i in list) {
                    for (item in 0 until genreName.size) {
                        if (i == genreName[item].id.toString()) {
                            results.add(genreName[item].name)
                        }
                    }
                }

            }
        }
        cursor.close()
        return results
    }
}