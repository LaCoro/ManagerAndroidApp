package co.llanox.alacartaexpress.mobile.data

/**
 * Created by jgabrielgutierrez on 15-09-02.
 */
interface RetrievedDataListener<E> {
    fun onRetrievedData(data: List<E>)
    fun onError(error: Throwable)
}