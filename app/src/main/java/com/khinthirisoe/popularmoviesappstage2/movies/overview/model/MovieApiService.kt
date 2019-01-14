package com.khinthirisoe.popularmoviesappstage2.movies.overview.model

import com.khinthirisoe.popularmoviesappstage2.core.config.ApiUrl
import com.khinthirisoe.popularmoviesappstage2.movies.details.model.MovieInfo
import com.khinthirisoe.popularmoviesappstage2.movies.details.model.Reviews
import com.khinthirisoe.popularmoviesappstage2.movies.details.model.Videos
import io.reactivex.Observable
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MovieApiService {

    @GET(ApiUrl.GET_GENRES)
    fun getGenresList(
        @Query("api_key") api_key: String
    ): Single<Genres>

    @GET(ApiUrl.DISCOVER_MOVIES)
    fun getSortedMoviesList(
        @Query("sort_by") sort_by: String,
        @Query("page") page: Int,
        @Query("api_key") api_key: String
    ): Observable<Movies>

    @GET(ApiUrl.MOVIE_DETAILS)
    fun getMovieDetail(
        @Path("movie_id") movie_id: Int,
        @Query("api_key") api_key: String
    ): Observable<MovieInfo>

    @GET(ApiUrl.GET_VIDEO)
    fun getVideoList(
        @Path("movie_id") movie_id: Int,
        @Query("api_key") api_key: String
    ): Observable<Videos>

    @GET(ApiUrl.GET_REVIEWS)
    fun getReviewsList(
        @Path("movie_id") movie_id: Int,
        @Query("api_key") api_key: String
    ): Observable<Reviews>
}