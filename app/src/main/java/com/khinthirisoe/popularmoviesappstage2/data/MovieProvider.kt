package com.khinthirisoe.popularmoviesappstage2.data

import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.database.sqlite.SQLiteQueryBuilder
import android.net.Uri
import androidx.annotation.NonNull
import com.khinthirisoe.popularmoviesappstage2.data.MovieContract.CONTENT_AUTHORITY
import com.khinthirisoe.popularmoviesappstage2.data.MovieContract.Genres
import com.khinthirisoe.popularmoviesappstage2.data.MovieContract.Movies
import com.khinthirisoe.popularmoviesappstage2.data.MovieContract.Reviews
import com.khinthirisoe.popularmoviesappstage2.data.MovieContract.Trailers


class MovieProvider : ContentProvider() {

    private var mDbHelper: MoviesDbHelper? = null

    companion object {
        val URI_MOVIES = 1
        val URI_GENRES = 2
        val URI_TRAILERS = 3
        val URI_REVIEWS = 4

        val URI_MOVIE_ID = 5
        val URI_TRAILER_ID = 6
        val URI_REVIEW_ID = 7

        val URI_MATCHER = UriMatcher(UriMatcher.NO_MATCH)

        init {

            URI_MATCHER.addURI(
                CONTENT_AUTHORITY,
                Movies.TABLE_NAME, URI_MOVIES
            )
            URI_MATCHER.addURI(
                CONTENT_AUTHORITY,
                Movies.TABLE_NAME + "/#", URI_MOVIE_ID
            )
            URI_MATCHER.addURI(
                CONTENT_AUTHORITY,
                Genres.TABLE_NAME, URI_GENRES
            )
            URI_MATCHER.addURI(
                CONTENT_AUTHORITY,
                Reviews.TABLE_NAME, URI_REVIEWS
            )
            URI_MATCHER.addURI(
                CONTENT_AUTHORITY,
                Reviews.TABLE_NAME + "/#", URI_REVIEW_ID
            )
            URI_MATCHER.addURI(
                CONTENT_AUTHORITY,
                Trailers.TABLE_NAME, URI_TRAILERS
            )
            URI_MATCHER.addURI(
                CONTENT_AUTHORITY,
                Trailers.TABLE_NAME + "/#", URI_TRAILER_ID
            )
        }
    }

    override fun onCreate(): Boolean {
        mDbHelper = MoviesDbHelper(context)
        return true
    }

    override fun query(
        @NonNull uri: Uri, projection: Array<String>?,
        selection: String?,
        selectionArgs: Array<String>?,
        sortOrder: String?
    ): Cursor? {
        val queryBuilder = SQLiteQueryBuilder()
        val mDatabase = mDbHelper!!.readableDatabase
        val matchCode = URI_MATCHER.match(uri)

        val joinTable =
            Movies.TABLE_NAME + " JOIN " + Trailers.TABLE_NAME + " ON " + Movies.TABLE_NAME + "." + Movies.COL_ID + " = " + Trailers.TABLE_NAME + "." + Trailers.COL_MOVIE_ID +
                    " JOIN " + Movies.TABLE_NAME + " ON " + Reviews.TABLE_NAME + "." + Movies.COL_ID + " = " + Reviews.TABLE_NAME + "." + Reviews.COL_MOVIE_ID

        val movieTable =
            Movies.TABLE_NAME + " JOIN " + Genres.TABLE_NAME + " ON " + Movies.TABLE_NAME + "." + Movies.COL_GENRE_ID + " = " + Genres.TABLE_NAME + "." + Genres.COL_ID
        val trailerTable =
            Movies.TABLE_NAME + " JOIN " + Trailers.TABLE_NAME + " ON " + Movies.TABLE_NAME + "." + Movies.COL_ID + " = " + Trailers.TABLE_NAME + "." + Trailers.COL_MOVIE_ID
        val reviewTable =
            Movies.TABLE_NAME + " JOIN " + Reviews.TABLE_NAME + " ON " + Movies.TABLE_NAME + "." + Movies.COL_ID + " = " + Reviews.TABLE_NAME + "." + Reviews.COL_MOVIE_ID
        when (matchCode) {
            URI_MOVIES -> {
                queryBuilder.tables = movieTable
            }

            URI_GENRES -> {
                queryBuilder.tables = Genres.TABLE_NAME
            }

            URI_TRAILERS -> {
                queryBuilder.tables = trailerTable
            }

            URI_REVIEWS -> {
                queryBuilder.tables = reviewTable
            }

            else -> throw IllegalArgumentException("Illegal query. Match code=$matchCode; uri=$uri")
        }

        val cursor = queryBuilder.query(mDatabase, projection, selection, selectionArgs, null, null, sortOrder)
        if (context != null)
            cursor.setNotificationUri(context!!.contentResolver, uri)
        return cursor
    }

    override fun bulkInsert(uri: Uri, values: Array<ContentValues>): Int {
        val db = mDbHelper?.writableDatabase
        when (URI_MATCHER.match(uri)) {
            URI_GENRES -> {
                db?.beginTransaction()
                var rowsInserted = 0
                try {
                    for (value in values) {

                        val _id = db?.insert(Genres.TABLE_NAME, null, value)
                        if (_id != (-1).toLong()) {
                            rowsInserted++
                        }
                    }
                    db?.setTransactionSuccessful()
                } finally {
                    db?.endTransaction()
                }
                if (rowsInserted > 0) {
                    context!!.contentResolver.notifyChange(uri, null)
                }
                return rowsInserted
            }
            URI_MOVIES -> {
                db?.beginTransaction()
                var rowsInserted = 0
                try {
                    for (value in values) {

                        val _id = db?.insert(Movies.TABLE_NAME, null, value)
                        if (_id != (-1).toLong()) {
                            rowsInserted++
                        }
                    }
                    db?.setTransactionSuccessful()
                } finally {
                    db?.endTransaction()
                }
                if (rowsInserted > 0) {
                    context!!.contentResolver.notifyChange(uri, null)
                }
                return rowsInserted
            }
            URI_TRAILERS -> {
                db?.beginTransaction()
                var rowsInserted = 0
                try {
                    for (value in values) {

                        val _id = db?.insert(Trailers.TABLE_NAME, null, value)
                        if (_id != (-1).toLong()) {
                            rowsInserted++
                        }
                    }
                    db?.setTransactionSuccessful()
                } finally {
                    db?.endTransaction()
                }
                if (rowsInserted > 0) {
                    context!!.contentResolver.notifyChange(uri, null)
                }
                return rowsInserted
            }
            URI_REVIEWS -> {
                db?.beginTransaction()
                var rowsInserted = 0
                try {
                    for (value in values) {

                        val _id = db?.insert(Reviews.TABLE_NAME, null, value)
                        if (_id != (-1).toLong()) {
                            rowsInserted++
                        }
                    }
                    db?.setTransactionSuccessful()
                } finally {
                    db?.endTransaction()
                }
                if (rowsInserted > 0) {
                    context!!.contentResolver.notifyChange(uri, null)
                }
                return rowsInserted
            }
            else -> return super.bulkInsert(uri, values)
        }
    }

    override fun insert(@NonNull uri: Uri, contentValues: ContentValues?): Uri? {
        val db = mDbHelper?.writableDatabase

        when (URI_MATCHER.match(uri)) {
            URI_MOVIES -> {
                val _id = db?.insert(Movies.TABLE_NAME, null, contentValues)
                if (_id != (-1).toLong()) {
                    context!!.contentResolver.notifyChange(uri, null)
                }

                return Movies.buildTodoUriWithId(_id!!)
            }

            URI_TRAILERS -> {
                val _id = db?.insert(Trailers.TABLE_NAME, null, contentValues)
                if (_id != (-1).toLong()) {
                    context!!.contentResolver.notifyChange(uri, null)
                }

                return Trailers.buildTodoUriWithId(_id!!)
            }

            URI_REVIEWS -> {
                val _id = db?.insert(Reviews.TABLE_NAME, null, contentValues)
                if (_id != (-1).toLong()) {
                    context!!.contentResolver.notifyChange(uri, null)
                }

                return Reviews.buildTodoUriWithId(_id!!)
            }

            else -> return null
        }
    }

    override fun delete(@NonNull uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        val db = mDbHelper?.writableDatabase
        val numDeleted: Int
        when (URI_MATCHER.match(uri)) {
            /* URI_MOVIES -> {
                 numDeleted = db!!.delete(
                     MoviesPersistenceContract.Movies.TABLE_NAME, selection, selectionArgs
                 )
                 db.execSQL(
                     "DELETE FROM movies WHERE movie_id = '" +
                             MoviesPersistenceContract.Movies.COL_CODE + "'")
             }*/
            URI_MOVIES -> {
                numDeleted = db!!.delete(
                    Movies.TABLE_NAME, selection, selectionArgs
                )
                db.execSQL(
                    "DELETE FROM movies WHERE movie_id = '" +
                            Movies.COL_ID + "'"
                )
            }
            URI_TRAILERS -> {
                /*numDeleted = db!!.delete(
                    Trailers.TABLE_NAME,
                    Trailers.COL_MOVIE_ID + " = ?",
                    arrayOf((ContentUris.parseId(uri)).toString())
                )
                // reset _ID
                db.execSQL(
                    ("DELETE FROM trailers WHERE movie_id = '" +
                            Trailers.TABLE_NAME + "'")
                )*/
                numDeleted = db!!.delete(
                    Trailers.TABLE_NAME, selection, selectionArgs
                )
                db.execSQL(
                    "DELETE FROM trailers WHERE movie_id = '" +
                            Trailers.COL_MOVIE_ID + "'"
                )
            }
            URI_REVIEWS -> {
                numDeleted = db!!.delete(
                    Reviews.TABLE_NAME, selection, selectionArgs
                )
                db.execSQL(
                    "DELETE FROM reviews WHERE movie_id = '" +
                            Reviews.COL_MOVIE_ID + "'"
                )
            }
            else -> throw UnsupportedOperationException("Unknown uri: $uri")
        }

        context.contentResolver.notifyChange(uri, null)
        return numDeleted
    }

    override fun update(@NonNull uri: Uri, contentValues: ContentValues?, s: String?, strings: Array<String>?): Int {
        return 0
    }

    override fun getType(@NonNull uri: Uri): String? {
        val matchCode = URI_MATCHER.match(uri)

        return when (matchCode) {
            URI_MOVIES -> Movies.CONTENT_TYPE
            URI_MOVIE_ID -> Movies.ITEM_TYPE
            URI_GENRES -> Genres.CONTENT_TYPE
            URI_TRAILERS -> Trailers.CONTENT_TYPE
            URI_TRAILER_ID -> Trailers.ITEM_TYPE
            URI_REVIEWS -> Reviews.CONTENT_TYPE
            URI_REVIEW_ID -> Reviews.ITEM_TYPE
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
    }
}