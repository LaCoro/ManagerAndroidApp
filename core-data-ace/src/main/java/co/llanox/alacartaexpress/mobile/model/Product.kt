package co.llanox.alacartaexpress.mobile.model

import java.io.Serializable
import java.util.ArrayList

/**
 * Created by jgabrielgutierrez on 15-07-22.
 */
data class Product(
    var id: String,
    var price: Double,
    var name: String,
    var description: String,
    var parentId: String? = null,
    var position: Int,
    var imageId: String? = null,
    var categoryProducts: MutableList<Product> = mutableListOf(),
    ) : Serializable {

    override fun equals(other: Any?): Boolean {
        val productId = (other as Product?)?.id ?: return false
        return productId.equals(id, ignoreCase = true)
    }

}