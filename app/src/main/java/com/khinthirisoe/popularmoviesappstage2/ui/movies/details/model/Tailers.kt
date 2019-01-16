package com.khinthirisoe.popularmoviesappstage2.ui.movies.details.model

import com.google.gson.annotations.SerializedName

data class Tailers(
    @SerializedName("id") val id: Int,
    @SerializedName("results") val results: ArrayList<TrailersResult>
)