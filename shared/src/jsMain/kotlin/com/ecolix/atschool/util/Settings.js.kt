package com.ecolix.atschool.util

import kotlinx.browser.localStorage

actual class Settings actual constructor() {
    actual fun getString(key: String): String? = localStorage.getItem(key)
    actual fun putString(key: String, value: String?) {
        if (value == null) localStorage.removeItem(key)
        else localStorage.setItem(key, value)
    }
    actual fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        return localStorage.getItem(key)?.toBoolean() ?: defaultValue
    }
    actual fun putBoolean(key: String, value: Boolean) {
        localStorage.setItem(key, value.toString())
    }
}
