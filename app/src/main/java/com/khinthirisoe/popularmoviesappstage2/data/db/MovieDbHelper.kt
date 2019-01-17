package com.khinthirisoe.popularmoviesappstage2.data.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.khinthirisoe.popularmoviesappstage2.data.db.MovieContract.Genres
import com.khinthirisoe.popularmoviesappstage2.data.db.MovieContract.Movies
import com.khinthirisoe.popularmoviesappstage2.data.db.MovieContract.Reviews
import com.khinthirisoe.popularmoviesappstage2.data.db.MovieContract.Trailers
import com.khinthirisoe.popularmoviesappstage2.utils.AppConstants


class MoviesDbHelper(context: Context) :
    SQLiteOpenHelper(
        context,
        DATABASE_NAME, null,
        DATABASE_VERSION
    ) {

    companion object {

        val DATABASE_VERSION = 8
        val DATABASE_NAME = AppConstants.DB_NAME
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(createTable(Movies.TABLE_NAME, Movies.COLUMNS))
        db.execSQL(createTable(Genres.TABLE_NAME, Genres.COLUMNS))
        db.execSQL(createTable(Trailers.TABLE_NAME, Trailers.COLUMNS))
        db.execSQL(createTable(Reviews.TABLE_NAME, Reviews.COLUMNS))
    }

    private fun createTable(tableName: String?, columns: Array<Array<String>>?): String {
        if (tableName == null || columns == null || columns.isEmpty()) {
            throw IllegalArgumentException("Invalid parameters for creating table " + tableName!!)
        } else {
            val stringBuilder = StringBuilder("CREATE TABLE ")

            stringBuilder.append(tableName)
            stringBuilder.append(" (")
            var n = 0
            val i = columns.size
            while (n < i) {
                if (n > 0) {
                    stringBuilder.append(", ")
                }
                stringBuilder.append(columns[n][0]).append(' ').append(columns[n][1])
                n++
            }
            return stringBuilder.append(");").toString()
        }
    }

    override fun onUpgrade(db: SQLiteDatabase, i: Int, i1: Int) {
        val drop = "DROP TABLE IF EXISTS "
        db.execSQL(drop + Movies.TABLE_NAME)
        db.execSQL(drop + Genres.TABLE_NAME)
        db.execSQL(drop + Trailers.TABLE_NAME)
        db.execSQL(drop + Reviews.TABLE_NAME)
        onCreate(db)
    }
}