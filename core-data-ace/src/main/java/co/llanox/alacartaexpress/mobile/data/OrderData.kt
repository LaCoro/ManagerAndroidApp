package co.llanox.alacartaexpress.mobile.data

import co.llanox.alacartaexpress.mobile.model.Order

/**
 * Created by llanox on 21/02/16.
 */
interface OrderData : ObjectData<Order> {
    fun asyncFindByState(listener: RetrievedDataListener<Order>, state: String)
    fun asyncFindByOrderId(orderId: String, retrievedOrdersListener: RetrievedDataListener<Order>)
    fun asyncFindByStoreId(storeId: String, retrievedOrdersListener: RetrievedDataListener<Order>)
    fun asyncFindByCity(cityName: String, retrievedOrdersListener: RetrievedDataListener<Order>)
    fun asyncUpdateOrder(mOrder: Order, callbackUpdateOrder: SentDataListener<Order>)
}