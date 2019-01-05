package com.khinthirisoe.popularmoviesappstage2.utils

import android.content.Context
import android.content.SharedPreferences

object PrefUtils {
    /* Default Pref */
    fun getDefaultSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences("APP_PREF", Context.MODE_PRIVATE)
    }

    fun getApiKey(context: Context): String {
        return getDefaultSharedPreferences(context).getString("API_KEY", "f620efedee20f579541e84617932d567")
    }

    fun getFirstLoadDataKey(context: Context): Boolean? {
        return getDefaultSharedPreferences(context).getBoolean("FIRST_LOAD_DATA_KEY", true)
    }

    fun putFirstLoadDataKey(context: Context, value: Boolean) {
        getDefaultSharedPreferences(context).edit().putBoolean("FIRST_LOAD_DATA_KEY", value).apply()
    }

}