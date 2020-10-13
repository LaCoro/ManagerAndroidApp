package co.llanox.alacartaexpress.mobile.data

import co.llanox.alacartaexpress.mobile.LaCoroErrorHandler
import co.llanox.alacartaexpress.mobile.ErrorHandler
import co.llanox.alacartaexpress.mobile.data.CustomerData.parseCustomer
import co.llanox.alacartaexpress.mobile.model.Order
import co.llanox.alacartaexpress.mobile.model.User
import com.parse.FindCallback
import com.parse.GetCallback
import com.parse.ParseException
import com.parse.ParseObject
import com.parse.ParseQuery
import com.parse.SaveCallback
import java.math.BigDecimal
import java.util.ArrayList

/**
 * Created by jgabrielgutierrez on 15-07-22.
 */
class OrderDataImpl : OrderData {
    private val errorHandler: ErrorHandler = LaCoroErrorHandler
    override fun asyncFindAll(listener: RetrievedDataListener<Order>, vararg params: String) {
        val query = ParseQuery.getQuery<ParseObject>(Order.NAME)
        query.addDescendingOrder("createdAt")
        findOrders(listener, query)
    }
    override fun asyncSave(listener: SentDataListener<Order>, model: Order, vararg params: Any) {
        val (id) = params[0] as User
        val order = ParseObject("Order")
        order.put("code", model.code)
        order.put("store", ParseObject.createWithoutData("Store", model.store!!.id))
        order.put("deliveryAddress", model.deliveryAddress)
        order.put("cashPayment", model.cashPayment)
        order.put("additionalRequests", model.additionalRequests)
        order.put("deliveryCost", model.deliveryCost)
        order.put("buyerName", model.buyerName)
        order.put("buyerId", model.buyerId)
        order.put("scheduledDeliveryDate", model.scheduledDeliveryDate)
        order.put("customer", ParseObject.createWithoutData("_User", id))
        order.saveInBackground { e ->
            if (e == null) {
                val query = ParseQuery.getQuery<ParseObject>("Order")
                query.whereEqualTo("code", model.code)
                query.findInBackground { list, e ->
                    if (e == null) {
                        model.id = list[0].objectId
                        asyncSaveOrderDetails(listener, model, list[0])
                    } else {
                        errorHandler?.onError(e)
                        listener.onError(e)
                    }
                }
            } else {
                errorHandler?.onError(e)
                //ACEUtil.logError(e);
                listener.onError(e)
            }
        }
    }

    private fun asyncSaveOrderDetails(listener: SentDataListener<Order>, model: Order, order: ParseObject) {
        val orderDetails: MutableList<ParseObject> = ArrayList(model.orderDetails!!.size)
        var orderDetail: ParseObject? = null
        for ((_, _, quantity, product) in model.orderDetails!!) {
            orderDetail = ParseObject("OrderDetail")
            orderDetail.put("quantity", quantity)
            orderDetail.put("product", ParseObject.createWithoutData("Item", product!!.id))
            orderDetail.put("order", order)
            orderDetails.add(orderDetail)
        }
        ParseObject.saveAllInBackground(orderDetails) { e ->
            if (e == null) {
                listener.onResponse(model)
            } else {
                errorHandler!!.onError(e)
                listener.onError(e)
            }
        }
    }

    override fun asyncFindByState(listener: RetrievedDataListener<Order>, state: String) {
        val query = ParseQuery.getQuery<ParseObject>(Order.NAME)
        query.whereEqualTo("status", state)
        query.addDescendingOrder("createdAt")
        findOrders(listener, query)
    }

    private fun findOrders(listener: RetrievedDataListener<Order>, query: ParseQuery<ParseObject>) {
        query.findInBackground(FindCallback { objects, e ->
            if (e != null) {
                errorHandler?.onError(e)
                listener.onError(e)
                return@FindCallback
            }
            val orders: MutableList<Order> = ArrayList()
            var order: Order
            for (parseOrder in objects) {
                order = try {
                    parseOrder(parseOrder, null)
                } catch (error: ParseException) {
                    errorHandler?.onError(error)
                    listener.onError(error)
                    return@FindCallback
                }
                orders.add(order)
            }
            listener.onRetrievedData(orders.toList())
        })
    }

    @Throws(ParseException::class)
    private fun parseOrder(orderObject: ParseObject, relationalDataParsers: List<RelationalDataParser<Order>>?): Order {
        val order= Order()
        order.id = orderObject.objectId
        order.deliveryAddress = orderObject.getString("deliveryAddress")
        order.buyerName = orderObject.getString("buyerName")
        order.buyerId = orderObject.getString("buyerId")
        order.status = orderObject.getString("status")
        order.scheduledDeliveryDate = orderObject.getDate("scheduledDeliveryDate")
        order.createdAt = orderObject.createdAt
        order.additionalRequests = orderObject.getString("additionalRequests")
        order.cashPayment = BigDecimal(orderObject.getInt("cashPayment"))
        if (relationalDataParsers != null) for (parser in relationalDataParsers) {
            parser.fetch(order, orderObject)
        }
        return order
    }

    override fun asyncFindByOrderId(orderId: String, listener: RetrievedDataListener<Order>) {
        val query = ParseQuery.getQuery<ParseObject>(Order.NAME)
        query.whereEqualTo("objectId", orderId)
        query.findInBackground(FindCallback { objects, e ->
            if (e != null) {
                errorHandler?.onError(e)
                listener.onError(e)
                return@FindCallback
            }
            val orders: MutableList<Order> = ArrayList()
            var order: Order
            val parsers: MutableList<RelationalDataParser<Order>> = ArrayList()
            parsers.add(OrderStoreFetcher())
            parsers.add(OrderCustomerFetcher())
            for (`object` in objects) {
                order = try {
                    parseOrder(`object`, parsers)
                } catch (error: ParseException) {
                    errorHandler?.onError(error)
                    listener.onError(error)
                    return@FindCallback
                }
                orders.add(order)
            }
            listener.onRetrievedData(orders)
        })
    }

    override fun asyncFindByStoreId(storeId: String, listener: RetrievedDataListener<Order>) {
        val query = ParseQuery.getQuery<ParseObject>(Order.NAME)
        val innerQuery = ParseQuery.getQuery<ParseObject>(StoreData.STORE_OBJECT_NAME)
        innerQuery.whereEqualTo("objectId", storeId)
        query.whereMatchesQuery("store", innerQuery)
        query.addDescendingOrder("createdAt")
        findOrders(listener, query)
    }

    override fun asyncFindByCity(cityName: String, listener: RetrievedDataListener<Order>) {
        val query = ParseQuery.getQuery<ParseObject>(Order.NAME)
        val innerQuery = ParseQuery.getQuery<ParseObject>(StoreData.STORE_OBJECT_NAME)
        innerQuery.whereEqualTo("city", cityName)
        query.whereMatchesQuery("store", innerQuery)
        query.addDescendingOrder("createdAt")
        findOrders(listener, query)
    }

    override fun asyncUpdateOrder(mOrder: Order, callbackUpdateOrder: SentDataListener<Order>) {
        val query = ParseQuery.getQuery<ParseObject>(Order.NAME)
        query.whereEqualTo("objectId", mOrder.id)
        query.getInBackground(mOrder.id, GetCallback { order, e ->
            if (e != null) {
                errorHandler!!.onError(e)
                callbackUpdateOrder.onError(e)
                return@GetCallback
            }
            order.put("status", mOrder.status)
            order.saveInBackground(SaveCallback { e ->
                if (e != null) {
                    errorHandler!!.onError(e)
                    callbackUpdateOrder.onError(e)
                    return@SaveCallback
                }
                callbackUpdateOrder.onResponse(mOrder)
            })
        })
    }

    internal inner class OrderStoreFetcher : RelationalDataParser<Order> {
        @Throws(ParseException::class)
        override fun fetch(order: Order?, `object`: ParseObject?) {
            val parseObject = `object`?.getParseObject("store")?.fetchIfNeeded<ParseObject>()
            val store = StoreData.parseStore(parseObject)
            order?.store = store
        }
    }

    internal inner class OrderCustomerFetcher : RelationalDataParser<Order> {
        @Throws(ParseException::class)
        override fun fetch(order: Order, `object`: ParseObject) {
            val parseUser = `object`.getParseUser("customer").fetchIfNeeded()
            val customer = parseCustomer(parseUser)
            order.customer = customer
        }
    }

}
