package com.ecolix.atschool.util

import java.util.prefs.Preferences

actual class Settings actual constructor() {
    private val prefs = Preferences.userRoot().node("com.ecolix.atschool")

    actual fun getString(key: String): String? = prefs.get(key, null)
    actual fun putString(key: String, value: String?) {
        if (value == null) prefs.remove(key)
        else prefs.put(key, value)
    }
    actual fun getBoolean(key: String, defaultValue: Boolean): Boolean = prefs.getBoolean(key, defaultValue)
    actual fun putBoolean(key: String, value: Boolean) {
        prefs.putBoolean(key, value)
    }
}
