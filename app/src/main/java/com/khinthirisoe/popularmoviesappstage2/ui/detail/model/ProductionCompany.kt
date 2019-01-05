package com.khinthirisoe.popularmoviesappstage2.ui.detail.model

import com.google.gson.annotations.SerializedName

data class ProductionCompany(
    @SerializedName("id") val id: Int,
    @SerializedName("logo_path") val logoPath: String,
    @SerializedName("name") val name: String,
    @SerializedName("origin_country") val originCountry: String
)