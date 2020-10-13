package co.llanox.alacartaexpress.mobile.data

import com.parse.ParseException

class BackendException(parseException: ParseException) : Exception(parseException) {
    val errorCode: Int = parseException.code
}