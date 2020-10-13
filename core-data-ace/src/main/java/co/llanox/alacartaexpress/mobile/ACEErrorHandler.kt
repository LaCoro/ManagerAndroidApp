package co.llanox.alacartaexpress.mobile

import android.content.Context
import android.widget.Toast
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.mobile.llanox.acedatacore.R
import com.parse.ParseException

/**
 * Created by jgabrielgutierrez on 15-10-17.
 */
class ACEErrorHandler private constructor() : ErrorHandler {

    override fun showHumanReadableError(error: Throwable, ctx: Context): Int {
        if (error is ParseException) {
            val code = error.code
            if (ParseException.EMAIL_TAKEN == code || ParseException.USERNAME_TAKEN == code) {
                Toast.makeText(ctx, ctx.getString(R.string.backend_err_email_already_taken), Toast.LENGTH_LONG).show()
                return ParseException.EMAIL_TAKEN
            }
            Toast.makeText(ctx, ctx.getString(R.string.err_general_error, code), Toast.LENGTH_LONG).show()
        }
        return -1
    }

    override fun updateCurrentUserID() {
        val crashlytics = FirebaseCrashlytics.getInstance()
        crashlytics.setUserId(ACEUtil.getCurrentUserID())
    }

    override fun addInfoToLogger(key: String, value: String) {
        val crashlytics = FirebaseCrashlytics.getInstance()
        crashlytics.setCustomKey(key,value)
    }

    override fun onError(error: Throwable) {
        val crashlytics = FirebaseCrashlytics.getInstance()
        crashlytics.recordException(error)
    }

    companion object {
        @JvmStatic
        val instance: ACEErrorHandler? = null
            get() = field ?: ACEErrorHandler()
    }
}
