package com.khinthirisoe.popularmoviesappstage2.ui.movies.details.model

import com.google.gson.annotations.SerializedName

data class Trailers(
    @SerializedName("id") val id: Int,
    @SerializedName("results") val results: ArrayList<TrailersResult>
)