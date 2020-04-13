package org.coepi.android.system

import android.content.SharedPreferences

enum class PreferencesKey {
    SEEN_ONBOARDING,
    LAST_CEN_KEYS_FETCH_TIMESTAMP // Unix time
}

class Preferences(private val sharedPreferences: SharedPreferences) {

    fun getBoolean(key: PreferencesKey): Boolean =
        sharedPreferences.getBoolean(key.toString(), false)

    fun putBoolean(key: PreferencesKey, value: Boolean?) {
        putOrClear(key, value) {
            sharedPreferences.edit().putBoolean(key.toString(), it).apply()
        }
    }

    fun getLong(key: PreferencesKey): Long? =
        if (sharedPreferences.contains(key.toString())) {
            sharedPreferences.getLong(key.toString(), -1)
        } else {
            null
        }

    fun putLong(key: PreferencesKey, value: Long?) {
        putOrClear(key, value) {
            sharedPreferences.edit().putLong(key.toString(), it).apply()
        }
    }

    private fun <T> putOrClear(key: PreferencesKey, obj: T?, put: (T) -> Unit) {
        if (obj != null) {
            put(obj)
        } else {
            clear(key)
        }
    }

    private fun clear(key: PreferencesKey) {
        sharedPreferences.edit().remove(key.toString()).apply()
    }
}
