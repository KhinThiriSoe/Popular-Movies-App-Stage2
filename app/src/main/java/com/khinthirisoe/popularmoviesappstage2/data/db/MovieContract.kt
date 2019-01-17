package com.khinthirisoe.popularmoviesappstage2.data.db

import android.content.ContentResolver
import android.net.Uri
import android.provider.BaseColumns


object MovieContract {

    val CONTENT_AUTHORITY = "com.khinthirisoe.popularmoviesappstage2"
    val BASE_CONTENT_URI = Uri.parse("content://$CONTENT_AUTHORITY")

    val REAL = "REAL"
    val TYPE_TEXT = "TEXT"
    val PRIMARY_KEY = " PRIMARY KEY"
    val NOT_NULL = " NOT NULL"
    val UNIQUE = " UNIQUE"
    val TYPE_PRIMARY_KEY_TEXT_UNIQUE = TYPE_TEXT + PRIMARY_KEY + NOT_NULL + UNIQUE
    val TYPE_TEXT_NOT_NULL = TYPE_TEXT + NOT_NULL

    class Movies : BaseColumns {
        companion object {

            val TABLE_NAME = "movies"

            val COL_ID = "movie_id"
            val COL_NAME = "movie_name"
            val COL_BACK_PATH = "back_path"
            val COL_POSTER = "poster"
            val COL_AVERAGE_VOTE = "average_vote"
            val COL_RELEASE_DATE = "release_date"
            val COL_OVERVIEW = "overview"
            val COL_GENRE_ID = "genre_id"

            val FOREIGN_KEY = "FOREIGN KEY"
            val REFERENCES = " REFERENCES "

            val COLUMNS = arrayOf(
                arrayOf(
                    COL_ID,
                    TYPE_TEXT_NOT_NULL
                ),
                arrayOf(
                    COL_NAME,
                    TYPE_TEXT_NOT_NULL
                ),
                arrayOf(
                    COL_BACK_PATH,
                    TYPE_TEXT
                ),
                arrayOf(
                    COL_POSTER,
                    TYPE_TEXT
                ),
                arrayOf(
                    COL_AVERAGE_VOTE,
                    REAL
                ),
                arrayOf(
                    COL_RELEASE_DATE,
                    TYPE_TEXT
                ),
                arrayOf(
                    COL_OVERVIEW,
                    TYPE_TEXT
                ),
                arrayOf(
                    COL_GENRE_ID,
                    TYPE_TEXT
                ),
                arrayOf(
                    FOREIGN_KEY,
                    String.format(
                        DbConstant.CURLY_BRACE,
                        COL_GENRE_ID
                    ) + REFERENCES + Genres.TABLE_NAME + String.format(
                        DbConstant.CURLY_BRACE,
                        Genres.COL_ID
                    )
                )
            )

            val CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY + DbConstant.SEPARATOR + TABLE_NAME)
            val CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + DbConstant.SEPARATOR + TABLE_NAME
            val ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + DbConstant.SEPARATOR + TABLE_NAME

            fun buildTodoUriWithId(id: Long): Uri {
                return BASE_CONTENT_URI.buildUpon()
                    .appendPath(java.lang.Long.toString(id))
                    .build()
            }
        }
    }


    class Genres : BaseColumns {
        companion object {

            val TABLE_NAME = "genres"

            val COL_ID = "genre_id"
            val COL_NAME = "genre_name"

            val COLUMNS = arrayOf(
                arrayOf(
                    COL_ID,
                    TYPE_PRIMARY_KEY_TEXT_UNIQUE
                ),
                arrayOf(
                    COL_NAME,
                    TYPE_TEXT
                )
            )

            val CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY + DbConstant.SEPARATOR + TABLE_NAME)
            val CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + DbConstant.SEPARATOR + TABLE_NAME
            val ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + DbConstant.SEPARATOR + TABLE_NAME

            fun buildTodoUriWithId(id: Long): Uri {
                return BASE_CONTENT_URI.buildUpon()
                    .appendPath(java.lang.Long.toString(id))
                    .build()
            }
        }
    }


    class Trailers : BaseColumns {
        companion object {

            val TABLE_NAME = "trailers"

            val COL_ID = "trailer_id"
            val COL_KEY = "trailer_key"
            val COL_MOVIE_ID = "movie_id"

            val FOREIGN_KEY = "FOREIGN KEY"
            val REFERENCES = " REFERENCES "

            val COLUMNS = arrayOf(
                arrayOf(
                    COL_ID,
                    TYPE_PRIMARY_KEY_TEXT_UNIQUE
                ),
                arrayOf(
                    COL_KEY,
                    TYPE_TEXT
                ),
                arrayOf(
                    COL_MOVIE_ID,
                    TYPE_TEXT
                ),
                arrayOf(
                    FOREIGN_KEY,
                    String.format(
                        DbConstant.CURLY_BRACE,
                        COL_MOVIE_ID
                    ) + REFERENCES + Movies.TABLE_NAME + String.format(
                        DbConstant.CURLY_BRACE,
                        Movies.COL_ID
                    )
                )
            )

            val CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY + DbConstant.SEPARATOR + TABLE_NAME)
            val CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + DbConstant.SEPARATOR + TABLE_NAME
            val ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + DbConstant.SEPARATOR + TABLE_NAME

            fun buildTodoUriWithId(id: Long): Uri {
                return BASE_CONTENT_URI.buildUpon()
                    .appendPath(java.lang.Long.toString(id))
                    .build()
            }
        }
    }

    class Reviews : BaseColumns {
        companion object {

            val TABLE_NAME = "reviews"

            val COL_ID = "review_id"
            val COL_AUTHOR = "author"
            val COL_CONTENT = "content"
            val COL_MOVIE_ID = "movie_id"

            val FOREIGN_KEY = "FOREIGN KEY"
            val REFERENCES = " REFERENCES "

            val COLUMNS = arrayOf(
                arrayOf(
                    COL_ID,
                    TYPE_PRIMARY_KEY_TEXT_UNIQUE
                ),
                arrayOf(
                    COL_AUTHOR,
                    TYPE_TEXT
                ),
                arrayOf(
                    COL_CONTENT,
                    TYPE_TEXT
                ),
                arrayOf(
                    COL_MOVIE_ID,
                    TYPE_TEXT
                ),
                arrayOf(
                    FOREIGN_KEY,
                    String.format(
                        DbConstant.CURLY_BRACE,
                        COL_MOVIE_ID
                    ) + REFERENCES + Movies.TABLE_NAME + String.format(
                        DbConstant.CURLY_BRACE,
                        Movies.COL_ID
                    )
                )
            )

            val CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY + DbConstant.SEPARATOR + TABLE_NAME)
            val CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + DbConstant.SEPARATOR + TABLE_NAME
            val ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + DbConstant.SEPARATOR + TABLE_NAME

            fun buildTodoUriWithId(id: Long): Uri {
                return BASE_CONTENT_URI.buildUpon()
                    .appendPath(java.lang.Long.toString(id))
                    .build()
            }
        }
    }
}