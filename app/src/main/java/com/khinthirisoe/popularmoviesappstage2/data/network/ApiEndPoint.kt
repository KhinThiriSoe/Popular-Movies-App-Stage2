/*
 * Copyright (C) 2017 MINDORKS NEXTGEN PRIVATE LIMITED
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://mindorks.com/license/apache-v2
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.khinthirisoe.popularmoviesappstage2.data.network

object ApiEndPoint {

    const val BASE_URL = "https://api.themoviedb.org/"
    const val VERSION = "3/"
    const val GET_GENRES = "genre/movie/list"
    const val DISCOVER_MOVIES = "discover/movie"
    const val MOVIE_DETAILS = "movie/{movie_id}"
    const val POSTER_PATH = "http://image.tmdb.org/t/p/w500/"
    const val GET_VIDEO = "movie/{movie_id}/videos"
    const val GET_REVIEWS = "movie/{movie_id}/reviews"

}
