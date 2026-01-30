package com.ecolix.atschool.util

import android.content.Context
import android.content.SharedPreferences

actual class Settings actual constructor() {
    private val prefs: SharedPreferences by lazy {
        // This is a bit of a hack but avoids passing Context everywhere in commonMain
        // In a real app, you'd inject this via Koin
        com.ecolix.atschool.util.AndroidContextProvider.context
            .getSharedPreferences("atschool_prefs", Context.MODE_PRIVATE)
    }

    actual fun getString(key: String): String? = prefs.getString(key, null)
    actual fun putString(key: String, value: String?) {
        prefs.edit().putString(key, value).apply()
    }
    actual fun getBoolean(key: String, defaultValue: Boolean): Boolean = prefs.getBoolean(key, defaultValue)
    actual fun putBoolean(key: String, value: Boolean) {
        prefs.edit().putBoolean(key, value).apply()
    }
}
