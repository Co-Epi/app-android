package org.coepi.android.system

import android.content.SharedPreferences
import com.google.gson.Gson

enum class PreferencesKey {
    SEEN_ONBOARDING,
    LAST_COMPLETED_REPORTS_INTERVAL
}

interface Preferences {
    fun getString(key: PreferencesKey): String?
    fun putString(key: PreferencesKey, value: String?)

    fun getBoolean(key: PreferencesKey): Boolean
    fun putBoolean(key: PreferencesKey, value: Boolean?)

    fun getLong(key: PreferencesKey): Long?
    fun putLong(key: PreferencesKey, value: Long?)

    fun <T> putObject(key: PreferencesKey, model: T?)
    fun <T> getObject(key: PreferencesKey, clazz: Class<T>): T?
}

class PreferencesImpl(
    private val sharedPreferences: SharedPreferences,
    private val gson: Gson
): Preferences {

    override fun getBoolean(key: PreferencesKey): Boolean =
        sharedPreferences.getBoolean(key.toString(), false)

    override fun putBoolean(key: PreferencesKey, value: Boolean?) {
        putOrClear(key, value) {
            sharedPreferences.edit().putBoolean(key.toString(), it).apply()
        }
    }

    override fun getLong(key: PreferencesKey): Long? =
        if (sharedPreferences.contains(key.toString())) {
            sharedPreferences.getLong(key.toString(), -1)
        } else {
            null
        }

    override fun putLong(key: PreferencesKey, value: Long?) {
        putOrClear(key, value) {
            sharedPreferences.edit().putLong(key.toString(), it).apply()
        }
    }

    override fun getString(key: PreferencesKey): String? =
        sharedPreferences.getString(key.toString(), null)

    override fun putString(key: PreferencesKey, value: String?) {
        putOrClear(key, value) {
            sharedPreferences.edit().putString(key.toString(), it).apply()
        }
    }

    override fun <T> putObject(key: PreferencesKey, model: T?) {
        putString(key, model?.let { gson.toJson(it) } )
    }

    override fun <T> getObject(key: PreferencesKey, clazz: Class<T>): T? =
        getString(key)?.let {
            gson.fromJson(it, clazz)
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
