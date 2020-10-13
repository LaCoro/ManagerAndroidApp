package co.llanox.alacartaexpress.mobile.data

/**
 * Created by jgabrielgutierrez on 15-09-30.
 */
interface SentDataListener<R> {
    fun onResponse(response: R)
    fun onError(error: Throwable)
}