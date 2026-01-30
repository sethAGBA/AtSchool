package com.ecolix.atschool.util

expect class Settings() {
    fun getString(key: String): String?
    fun putString(key: String, value: String?)
    fun getBoolean(key: String, defaultValue: Boolean): Boolean
    fun putBoolean(key: String, value: Boolean)
}
