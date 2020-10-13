package co.llanox.alacartaexpress.mobile.model

import java.io.Serializable
import java.math.BigDecimal
import java.util.Date

/**
 * Created by jgabrielgutierrez on 15-09-11.
 */
class Order(    var id: String? = null,
                var code: String? = null,
                var status: String? = null,
                var deliveryAddress: String? = null,
                var additionalRequests: String? = null,
                var buyerName: String? = null,
                var buyerId: String? = null,
                var orderDetails: Collection<OrderDetail>? = null,
                var deliveryStartedAt: Date? = null,
                var deliveryEndedAt: Date? = null,
                var createdAt: Date? = null,
                var scheduledDeliveryDate: Date? = null,
                var deliveryBoy: User? = null,
                var client: User? = null,
                var cashPayment: BigDecimal? = null,
                var deliveryCost: BigDecimal? = null,
                var store: Store? = null,
                var customer: Customer? = null
) : Serializable {

    companion object {
        const val NAME = "Order"
        fun toJson(mOrder: Order): String {
            return "client:" + mOrder.customer?.email + ",order:" + mOrder.id + ",state:" + mOrder.status
        }
    }
}