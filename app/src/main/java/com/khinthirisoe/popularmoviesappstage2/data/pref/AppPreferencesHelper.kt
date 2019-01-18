/*
 * Copyright (C) 2017 MINDORKS NEXTGEN PRIVATE LIMITED
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://mindorks.com/license/apache-v2
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.khinthirisoe.popularmoviesappstage2.data.pref

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.khinthirisoe.popularmoviesappstage2.utils.AppConstants

class AppPreferencesHelper constructor(context: Context) : PreferencesHelper {

    private val mPrefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    override val apiKey: String
        get() = mPrefs.getString(PREF_KEY_API_KEY, AppConstants.API_KEY)

    override var firstOpen: Boolean
        get() = mPrefs.getBoolean(PREF_KEY_FIRST_OPEN, true)
        set(value) {
            mPrefs.edit().putBoolean(PREF_KEY_FIRST_OPEN, value).apply()
        }

    override var sortedType: String
        get() = mPrefs.getString(PREF_KEY_SORTED_TYPE, "")
        set(value) {
            mPrefs.edit().putString(PREF_KEY_SORTED_TYPE, value).apply()
        }

    companion object {
        private val PREF_KEY_API_KEY = "API_KEY"
        private val PREF_KEY_FIRST_OPEN = "FIRST_OPEN"
        private val PREF_KEY_SORTED_TYPE = "sortedType"
    }
}
