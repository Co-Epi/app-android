package org.coepi.android.components

import org.coepi.android.system.Preferences
import org.coepi.android.system.PreferencesKey

class PreferencesReturningObject(private val obj: Any?):
    Preferences {
    override fun getString(key: PreferencesKey): String? = null
    override fun putString(key: PreferencesKey, value: String?) {}
    override fun getBoolean(key: PreferencesKey): Boolean = false
    override fun putBoolean(key: PreferencesKey, value: Boolean?) {}
    override fun getLong(key: PreferencesKey): Long? = null
    override fun putLong(key: PreferencesKey, value: Long?) {}
    override fun <T> putObject(key: PreferencesKey, model: T?) {}
    @Suppress("UNCHECKED_CAST")
    override fun <T> getObject(key: PreferencesKey, clazz: Class<T>): T? = obj as T?
}
