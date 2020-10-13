package co.llanox.alacartaexpress.mobile.data

/**
 * Created by jgabrielgutierrez on 15-07-22.
 */
interface ObjectData<T> {
    fun asyncFindAll(listener: RetrievedDataListener<T>, vararg params: String)
    fun asyncSave(listener: SentDataListener<T>, model: T, vararg params: Any)
}