package co.llanox.alacartaexpress.mobile.data

import co.llanox.alacartaexpress.mobile.Constants
import co.llanox.alacartaexpress.mobile.ErrorHandler
import co.llanox.alacartaexpress.mobile.LaCoroErrorHandler
import co.llanox.alacartaexpress.mobile.model.Product
import com.parse.ParseObject
import com.parse.ParseQuery
import java.util.ArrayList

/**
 * Created by jgabrielgutierrez on 15-07-23.
 */
class ProductData : ObjectData<Product> {
    private var errorHandler: ErrorHandler = LaCoroErrorHandler


    override fun asyncFindAll(listener: RetrievedDataListener<Product>, vararg params: String) {
        val storeId = params[0]
        val innerQuery = ParseQuery.getQuery<ParseObject>("Store")
        innerQuery.whereEqualTo("objectId", storeId)
        val query = ParseQuery.getQuery<ParseObject>("Item")
        query.whereMatchesQuery("store", innerQuery)
        query.addAscendingOrder("position")
        query.limit = Constants.MAXIMUM_PRODUCTS_BY_QUERY
        query.findInBackground { objects, e ->
            if (e == null) {
                var product: Product?
                val products: MutableList<Product> = ArrayList(objects.size)
                for (`object` in objects) {
                    product = parseProduct(`object`)
                    var index = products.size - 1
                    index = if (index < 0) 0 else index
                    if (index >= 0) {
                        products[index].categoryProducts.add(product)
                    } else {
                        products.add(product)
                    }
                }
                listener.onRetrievedData(products.toList())
            } else {
                errorHandler?.onError(e)
                listener.onError(e)
            }
        }
    }

    override fun asyncSave(listener: SentDataListener<Product>, model: Product, vararg params: Any) {}

    companion object {
        @JvmStatic
        fun parseProduct(productObject: ParseObject): Product {
            var parentId: String? = null
            if (productObject.containsKey("parent")) {
                val parent = productObject["parent"] as ParseObject
                parentId = parent.objectId
            }
            val product: Product
            product = Product(
                productObject.objectId,
                productObject.getDouble("price"),
                productObject.getString("name"),
                productObject.getString("description"),
                parentId,
                productObject.getInt("position")
            )
            return product
        }
    }
}
