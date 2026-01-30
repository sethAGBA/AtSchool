package com.ecolix.atschool.util

import platform.Foundation.NSUserDefaults

actual class Settings actual constructor() {
    private val delegate = NSUserDefaults.standardUserDefaults

    actual fun getString(key: String): String? = delegate.stringForKey(key)
    actual fun putString(key: String, value: String?) {
        if (value == null) delegate.removeObjectForKey(key)
        else delegate.setObject(value, key)
    }
    actual fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        // NSUserDefaults returns false if key doesn't exist, but we want to honor defaultValue
        return if (delegate.objectForKey(key) != null) {
            delegate.boolForKey(key)
        } else {
            defaultValue
        }
    }
    actual fun putBoolean(key: String, value: Boolean) {
        delegate.setBool(value, key)
    }
}
