package com.khinthirisoe.popularmoviesappstage2.data.db.repository

import android.content.ContentValues
import android.content.Context
import android.util.Log
import com.khinthirisoe.popularmoviesappstage2.data.db.MovieContract.Genres
import com.khinthirisoe.popularmoviesappstage2.data.db.MovieContract.Movies
import com.khinthirisoe.popularmoviesappstage2.data.db.MovieContract.Reviews
import com.khinthirisoe.popularmoviesappstage2.data.db.MovieContract.Trailers
import com.khinthirisoe.popularmoviesappstage2.ui.movies.details.model.Details
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

    fun saveMovieDetail(context: Context, detail: Details, movieId: String) {

        if (detail.genres.isNotEmpty()) {
            val contentValues = ArrayList<ContentValues>(detail.genres.size)
            for (result in detail.genres) {
                val values = ContentValues(8)
                values.put(Movies.COL_ID, movieId)
                values.put(Movies.COL_NAME, detail.title)
                values.put(Movies.COL_BACK_PATH, detail.backdropPath)
                values.put(Movies.COL_POSTER, detail.posterPath)
                values.put(Movies.COL_AVERAGE_VOTE, detail.voteAverage)
                values.put(Movies.COL_RELEASE_DATE, detail.releaseDate)
                values.put(Movies.COL_OVERVIEW, detail.overview)
                values.put(Movies.COL_GENRE_ID, result.id)

                contentValues.add(values)
            }
            val id = context.contentResolver.bulkInsert(Movies.CONTENT_URI, contentValues.toTypedArray())
            Log.d("message", "detail bulk insert " + id.toString())
        }
    }

    fun saveReview(context: Context, reviewsResult: ArrayList<ReviewsResult>?, movieId: String) {

        if (reviewsResult!!.size > 0) {
            val contentValues = ArrayList<ContentValues>(reviewsResult.size)
            for (result in reviewsResult) {
                val values = ContentValues(4)
                values.put(Reviews.COL_ID, result.id)
                values.put(Reviews.COL_AUTHOR, result.author)
                values.put(Reviews.COL_CONTENT, result.content)
                values.put(Reviews.COL_MOVIE_ID, movieId)
                contentValues.add(values)
            }
            val id = context.contentResolver.bulkInsert(Reviews.CONTENT_URI, contentValues.toTypedArray())
            Log.d("message", "review bulk insert " + id.toString())
        }

        val cursor = context.contentResolver.query(Reviews.CONTENT_URI, null, null, null, null)

        when {
            cursor == null -> {
                // error - log some message
            }
            cursor.count < 1 -> // nothing to show  - log some message
                Log.d("message", "review cursor.getCount() < 1")
            else -> while (cursor.moveToNext()) {
                Log.d("message", cursor.getString(0) + "-" + cursor.getString(1))
            }
        }
        cursor.close()
    }

    fun saveTrailer(context: Context, trailersResult: ArrayList<TrailersResult>?, movieId: String) {

        if (trailersResult!!.size > 0) {
            val contentValues = ArrayList<ContentValues>(trailersResult.size)
            for (result in trailersResult) {
                val values = ContentValues(3)
                values.put(Trailers.COL_ID, result.id)
                values.put(Trailers.COL_KEY, result.key)
                values.put(Trailers.COL_MOVIE_ID, movieId)
                contentValues.add(values)
            }
            val id = context.contentResolver.bulkInsert(Trailers.CONTENT_URI, contentValues.toTypedArray())
            Log.d("message", "trailer bulk insert " + id.toString())
        }

        val cursor = context.contentResolver.query(Trailers.CONTENT_URI, null, null, null, null)

        when {
            cursor == null -> {
                // error - log some message
            }
            cursor.count < 1 -> // nothing to show  - log some message
                Log.d("message", "trailer cursor.getCount() < 1")
            else -> while (cursor.moveToNext()) {
                Log.d("message", cursor.getString(0) + "-" + cursor.getString(1))
            }
        }
        cursor.close()
    }

    fun removeTrailer(context: Context, movieId: String) {
        context.contentResolver.delete(
            Trailers.CONTENT_URI,
            Trailers.COL_MOVIE_ID + "=?",
            arrayOf(movieId.toString())
        )
    }

    fun removeReview(context: Context, movieId: String) {
        context.contentResolver.delete(
            Reviews.CONTENT_URI,
            Reviews.COL_MOVIE_ID + "=?",
            arrayOf(movieId.toString())
        )

    }

    fun removeMovieDetail(context: Context, movieId: String) {
        context.contentResolver.delete(
            Movies.CONTENT_URI,
            Movies.COL_ID + "=?",
            arrayOf(movieId.toString())
        )

    }
}