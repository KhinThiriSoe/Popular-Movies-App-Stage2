package com.khinthirisoe.popularmoviesappstage2.movies.overview.model

import com.google.gson.annotations.SerializedName

data class Movies(
    @SerializedName("page") val page: Int,
    @SerializedName("total_results") val totalResults: Int,
    @SerializedName("total_pages") val totalPages: Int,
    @SerializedName("results") val results: ArrayList<MovieResult>
)