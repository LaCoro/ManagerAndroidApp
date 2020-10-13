package co.llanox.alacartaexpress.mobile.model

import java.io.Serializable
import java.util.HashMap

/**
 * Created by jgabrielgutierrez on 15-08-12.
 */
data class OrderDetail( var id: String? = null,
                        var productId: String? = null,
                        var quantity : Int = 0,
                        var product: Product? = null) : Serializable {


    companion object {
        const val NAME = "OrderDetail"
        fun convertToHashMap(data: List<OrderDetail>): HashMap<String?, OrderDetail> {
            val orderDetailHashMap = HashMap<String?, OrderDetail>()
            for (orderDetail in data) {
                orderDetailHashMap[orderDetail.id] = orderDetail
            }
            return orderDetailHashMap
        }
    }
}