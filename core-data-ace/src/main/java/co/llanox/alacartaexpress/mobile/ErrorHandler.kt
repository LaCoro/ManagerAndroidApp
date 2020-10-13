package co.llanox.alacartaexpress.mobile

import android.content.Context

/**
 * Created by jgabrielgutierrez on 15-10-17.
 */
interface ErrorHandler {
    fun onError(error: Throwable)
    fun showHumanReadableError(error: Throwable, ctx: Context): Int
    fun updateCurrentUserID()
    fun addInfoToLogger(key: String, value: String)
}