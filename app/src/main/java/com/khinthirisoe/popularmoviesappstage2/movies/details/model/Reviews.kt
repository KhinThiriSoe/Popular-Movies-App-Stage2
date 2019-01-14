package com.khinthirisoe.popularmoviesappstage2.movies.details.model

import com.google.gson.annotations.SerializedName

data class Reviews(
    @SerializedName("id") val id: Int,
    @SerializedName("page") val page: Int,
    @SerializedName("results") val results: ArrayList<ReviewsResult>,
    @SerializedName("total_pages") val totalPages: Int,
    @SerializedName("total_results") val totalResults: Int
)