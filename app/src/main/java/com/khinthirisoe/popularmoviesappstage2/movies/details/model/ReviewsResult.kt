package com.khinthirisoe.popularmoviesappstage2.movies.details.model

import com.google.gson.annotations.SerializedName

data class ReviewsResult(
    @SerializedName("author") val author: String,
    @SerializedName("content") val content: String,
    @SerializedName("id") val id: String,
    @SerializedName("url") val url: String
)