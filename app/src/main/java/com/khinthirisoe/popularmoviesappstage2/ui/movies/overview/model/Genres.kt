package com.khinthirisoe.popularmoviesappstage2.ui.movies.overview.model

import com.google.gson.annotations.SerializedName

data class Genres(
    @SerializedName("genres")
    var genres: ArrayList<Genre>
)