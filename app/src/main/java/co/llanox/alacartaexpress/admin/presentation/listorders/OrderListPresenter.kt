package co.llanox.alacartaexpress.admin.presentation.listorders

import android.util.Log
import co.llanox.alacartaexpress.admin.messages.RealTimeMessages
import co.llanox.alacartaexpress.admin.messages.RealTimeMessagesListener
import co.llanox.alacartaexpress.admin.mvp.BasePresenter
import co.llanox.alacartaexpress.mobile.ApplicationSessionHelper
import co.llanox.alacartaexpress.mobile.Constants
import co.llanox.alacartaexpress.mobile.data.BackendException
import co.llanox.alacartaexpress.mobile.data.OrderData
import co.llanox.alacartaexpress.mobile.data.OrderDataImpl
import co.llanox.alacartaexpress.mobile.data.RetrievedDataListener
import co.llanox.alacartaexpress.mobile.data.SentDataListener
import co.llanox.alacartaexpress.mobile.data.UserAuthentication
import co.llanox.alacartaexpress.mobile.data.UserAuthenticationImpl
import co.llanox.alacartaexpress.mobile.data.UserData
import co.llanox.alacartaexpress.mobile.model.Order
import co.llanox.alacartaexpress.mobile.model.Role
import co.llanox.alacartaexpress.mobile.model.User
import com.parse.ParseException

class OrderListPresenter(private val realTimeMessages: RealTimeMessages, applicationSessionHelper: ApplicationSessionHelper) : BasePresenter<OrderListView?>() {
    private val userAuthentication: UserAuthentication = UserAuthenticationImpl()
    private val orderData: OrderData
    private val userData: UserData
    private val applicationSessionHelper: ApplicationSessionHelper
    private val realTimeMessagesListener = RealTimeMessagesListener {  refreshPlacedOrders() }
    private val retrievedOrdersListener: RetrievedDataListener<Order> = object : RetrievedDataListener<Order> {
        override fun onRetrievedData(data: List<Order>) {
           view?.renderOrders(data)
        }

        override fun onError(error: Throwable) {
            view?.onErrorFetchingOrders(error)
        }
    }

    init {
        orderData = OrderDataImpl()
        userData = UserData()
        this.applicationSessionHelper = applicationSessionHelper
    }

    fun logOut() {
        userAuthentication.logOut(object : SentDataListener<String> {
            override fun onResponse(response: String) {
                applicationSessionHelper.resetApp()
                view?.goToLogin()
                realTimeMessages.unsubscribeAllChannels()
            }

            override fun onError(error: Throwable) {
                if (error is BackendException) {
                    view?.showBackendError(error)
                    return
                }
            }
        })
    }

    fun refreshPlacedOrders() {
        if (view == null){
            return
        }

        var currentUser: User? = null
        try {
            currentUser = userData.currentUser
        } catch (e: ParseException) {
            Log.e(TAG, "error fetching current user", e)
            logOut()
        }
        if (userAuthentication.hasRole(Role.STORE_MANAGER)) {
            currentUser?.managedStoreId?.let { orderData.asyncFindByStoreId(it, retrievedOrdersListener) }
            return
        }
        if (userAuthentication.hasRole(Role.CITY_MANAGER)) {
            currentUser?.city?.let { orderData.asyncFindByCity(it, retrievedOrdersListener) }
            return
        }
        if (userAuthentication.hasRole(Role.ADMINISTRATOR)) {
            orderData.asyncFindAll(retrievedOrdersListener)
            return
        }
        logOut()
    }

    public override fun init() {
        registerTokenForMessaging()
        refreshPlacedOrders()
    }

    private fun registerTokenForMessaging() {
        var currentUser: User? = null
        try {
            currentUser = userData.currentUser
        } catch (e: ParseException) {
            Log.e(TAG, "error fetching current user", e)
            logOut()
        }
        val tokenDevice = applicationSessionHelper.getString(Constants.TOKEN_DEVICE_KEY)
        if (userAuthentication.hasRole(Role.STORE_MANAGER)) {
            currentUser?.managedStoreId?.let { realTimeMessages.subscribe(it) }
            realTimeMessages.registerDeviceToken(tokenDevice)
            return
        }
        if (userAuthentication.hasRole(Role.CITY_MANAGER)) {
            currentUser?.city?.let { realTimeMessages.subscribe(it) }
            realTimeMessages.registerDeviceToken(tokenDevice)
            return
        }
        if (userAuthentication.hasRole(Role.ADMINISTRATOR)) {
            realTimeMessages.subscribe(Constants.PubNubSettings.DEFAULT_CHANNEL)
            realTimeMessages.registerDeviceToken(tokenDevice)
            return
        }
        logOut()
    }

    fun startRealTime() {
        realTimeMessages.addRealTimeMessagesListener(realTimeMessagesListener)
    }

    fun stopRealTime() {
        realTimeMessages.removeRealTimeMessagesListener(realTimeMessagesListener)
    }


    companion object {
        val TAG = OrderListPresenter::class.java.simpleName
    }
}
