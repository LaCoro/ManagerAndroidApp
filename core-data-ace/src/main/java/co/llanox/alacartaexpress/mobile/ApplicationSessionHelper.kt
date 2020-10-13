package co.llanox.alacartaexpress.mobile

import android.app.Application
import android.content.Context
import android.content.SharedPreferences

class ApplicationSessionHelper private constructor(private val preferences: SharedPreferences) {
    fun getString(tag: String?, defaultValue: String?): String? {
        return preferences.getString(tag, defaultValue)
    }

    fun getString(tag: String?): String? {
        return getString(tag, null)
    }

    fun getBoolean(tag: String?): Boolean {
        return preferences.getBoolean(tag, false)
    }

    fun putString(tag: String?, value: String?) {
        val editor = preferences.edit()
        editor.putString(tag, value)
        editor.apply()
    }

    fun putBoolean(tag: String?, value: Boolean, ctx: Context?) {
        val editor = preferences.edit()
        editor.putBoolean(tag, value)
        editor.apply()
    }

    private fun clearSessionPreferences() {
        val editor = preferences.edit()
        editor.clear()
        editor.apply()
    }

    fun putArrayString(tag: String?, vararg params: String?) {
        val sb = StringBuilder()
        for (value in params) {
            sb.append(value).append(ARRAY_STRING_SEPARATOR)
        }
        val editor = preferences.edit()
        editor.putString(tag, sb.toString())
        editor.apply()
    }

    fun getArrayString(tag: String?): Array<String> {
        val arrayStrings = getString(tag)
        return arrayStrings!!.split(ARRAY_STRING_SEPARATOR).toTypedArray()
    }

    fun resetApp() {
        clearSessionPreferences()
    }

    fun clearStringPreference(key: String?) {
        val editor = preferences.edit()
        editor.putString(key, null)
        editor.apply()
    }

    companion object {
        private var seetingsHelper: ApplicationSessionHelper? = null
        @JvmStatic
        fun getInstance(app: Application): ApplicationSessionHelper? {
            if (seetingsHelper == null) {
                val preferences = app.getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE)
                seetingsHelper = ApplicationSessionHelper(preferences)
                return seetingsHelper
            }
            return seetingsHelper
        }

        private const val ARRAY_STRING_SEPARATOR = ";"
        private const val SHARED_PREFERENCES = "co.llanox.alacartaexpress.mobile"
    }
}