package co.llanox.alacartaexpress.admin.messages

import android.content.Context
import android.util.Log
import co.llanox.alacartaexpress.admin.R
import co.llanox.alacartaexpress.mobile.LaCoroErrorHandler
import com.pubnub.api.PNConfiguration
import com.pubnub.api.PubNub
import com.pubnub.api.callbacks.PNCallback
import com.pubnub.api.callbacks.SubscribeCallback
import com.pubnub.api.enums.PNPushType
import com.pubnub.api.models.consumer.PNPublishResult
import com.pubnub.api.models.consumer.PNStatus
import com.pubnub.api.models.consumer.pubsub.PNMessageResult
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult
import com.pubnub.api.models.consumer.push.PNPushAddChannelResult
import java.util.ArrayList
import java.util.Arrays

object MessagesHandler: RealTimeMessages {
    private lateinit var pubnub: PubNub
    private val errorHandler = LaCoroErrorHandler
    private var mRealTimeMessagesListeners = arrayListOf<RealTimeMessagesListener>()

    fun init(context: Context){
        mRealTimeMessagesListeners = ArrayList()
        val pnConfiguration = PNConfiguration()
        pnConfiguration.subscribeKey = context.getString(R.string.pubnub_subscribe_key)
        pnConfiguration.publishKey = context.getString(R.string.pubnub_publish_key)
        pnConfiguration.isSecure = true
        pubnub = PubNub(pnConfiguration)

    }


    private val messageCallback: SubscribeCallback = object : SubscribeCallback() {
        override fun status(pubnub: PubNub, status: PNStatus) {
            if (!status.isError) {
                Log.v(TAG, "result messageCallback(" + pubnub.removeAllPushNotificationsFromDeviceWithPushToken() + ")")
            } else {
                Log.v(TAG, "error(" + status.errorData.information + ")")
                errorHandler?.onError(status.errorData.throwable)
            }
        }

        override fun message(pubnub: PubNub, message: PNMessageResult) {
            for (listener in mRealTimeMessagesListeners) {
                listener.onArrivedMessage(message)
            }
        }

        override fun presence(pubnub: PubNub, presence: PNPresenceEventResult) {}
    }

    override fun subscribe(channel: String) {
        pubnub.addListener(messageCallback)
        pubnub.subscribe().channels(Arrays.asList(channel)).execute()
    }

    override fun unsubscribe(channel: String) {
        pubnub.unsubscribe().channels(Arrays.asList(channel)).execute()
    }

    override fun unsubscribeAllChannels() {
        val subscribedChannels = pubnub.subscribedChannels
        pubnub.unsubscribe().channels(subscribedChannels).execute()
        pubnub.removeAllPushNotificationsFromDeviceWithPushToken()
        pubnub.removePushNotificationsFromChannels()
    }

    override fun addRealTimeMessagesListener(listener: RealTimeMessagesListener) {
        mRealTimeMessagesListeners.add(listener)
    }

    override fun removeRealTimeMessagesListener(listener: RealTimeMessagesListener) {
        mRealTimeMessagesListeners.remove(listener)
    }

    override fun publish(channel: String, updateActionMsg: String) {
        pubnub.publish().channel(channel).message(updateActionMsg).async(object : PNCallback<PNPublishResult>() {
            override fun onResponse(result: PNPublishResult, status: PNStatus) {
                if (!status.isError) {
                    Log.v(TAG, "publish: result($result) channel :$channel")
                } else {
                    Log.v(TAG, "publish: error(" + status.errorData.information + ")")
                    errorHandler?.onError(status.errorData.throwable)
                }
            }
        })
    }

    override fun registerDeviceToken(token: String?) {
        val channels = pubnub.subscribedChannels
        val builder = pubnub.addPushNotificationsOnChannels()
        builder.pushType(PNPushType.GCM)
        builder.channels(channels)
        if (token == null) return
        builder.deviceId(token).async(object : PNCallback<PNPushAddChannelResult>() {
            override fun onResponse(result: PNPushAddChannelResult, status: PNStatus) {
                if (!status.isError) {
                    Log.v(TAG, "result($result channels $channels )")
                } else {
                    Log.v(TAG, "error(" + status.errorData.information + ")")
                    errorHandler?.onError(status.errorData.throwable)
                }
            }
        })
    }

    val TAG = MessagesHandler::class.java.simpleName

}
