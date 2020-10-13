package co.llanox.alacartaexpress.mobile.data

import co.llanox.alacartaexpress.mobile.model.OrderDetail

/**
 * Created by juangabrielgutierrez on 3/22/16.
 */
interface OrderDetailData : ObjectData<OrderDetail?> {
    fun asyncFindOrderDetailByOrderId(id: String?, listener: RetrievedDataListener<OrderDetail>)
}
