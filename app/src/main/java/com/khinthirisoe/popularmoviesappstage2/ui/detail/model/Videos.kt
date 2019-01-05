package com.khinthirisoe.popularmoviesappstage2.ui.detail.model

import com.google.gson.annotations.SerializedName

data class Videos(
    @SerializedName("id") val id: Int,
    @SerializedName("results") val results: ArrayList<VideoResult>
)