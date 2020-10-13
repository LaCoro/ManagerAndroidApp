package co.llanox.alacartaexpress.admin.messages

/**
 * Created by llanox on 5/7/16.
 */
interface RealTimeMessages {
    fun subscribe(channel: String)
    fun unsubscribe(channel: String)
    fun addRealTimeMessagesListener(listener: RealTimeMessagesListener)
    fun removeRealTimeMessagesListener(listener: RealTimeMessagesListener)
    fun publish(defaultChannel: String, updateActionMsg: String)
    fun unsubscribeAllChannels()
    fun registerDeviceToken(token: String?)
}