package co.llanox.alacartaexpress.admin.presentation.listorders

import co.llanox.alacartaexpress.admin.mvp.BaseView
import co.llanox.alacartaexpress.mobile.model.Order

interface OrderListView : BaseView {
    fun goToLogin()
    fun renderOrders(data: List<Order>)
    fun onErrorFetchingOrders(error: Any)
}