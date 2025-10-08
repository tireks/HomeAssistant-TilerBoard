package com.tirexmurina.tilerboard.source.remote

import android.content.Context
import android.content.SharedPreferences

class UrlDataStore(context: Context) {
    companion object {
        private const val PREFS_NAME = "prefs_url"
        private const val LOCAL_URL = "local_url"
    }

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun getBaseUrl(): String? {
        return sharedPreferences.getString(LOCAL_URL, null)
    }

    fun setBaseUrl(url: String) {
        sharedPreferences.edit().putString(LOCAL_URL, url).apply()
    }

    fun isBaseUrlSaved(): Boolean {
        val url = getBaseUrl()
        return !url.isNullOrEmpty()
    }

    fun clearBaseUrl() {
        sharedPreferences.edit().remove(LOCAL_URL).apply()
    }
}