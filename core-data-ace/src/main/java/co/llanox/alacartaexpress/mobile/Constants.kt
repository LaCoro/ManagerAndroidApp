package co.llanox.alacartaexpress.mobile

/**
 * Created by jgabrielgutierrez on 15-08-05.
 */
object Constants {
    const val MAXIMUM_PRODUCTS_BY_QUERY = 500
    const val SERVER_URL = "https://parseapi.back4app.com/"

    // STG
    const val APPLICATION_ID = "***REMOVED***"
    const val CLIENT_KEY = "***REMOVED***"

    // PROD
    // public static final String APPLICATION_ID = "***REMOVED***";
    // public static final String CLIENT_KEY = "***REMOVED***";
    const val ORDER_ID = "_order_id"
    const val ORDER_OBJECT_KEY = "__order_obj"
    const val ORDER_DETAILS_OBJECT_KEY = "__order_details_obj"
    const val TOKEN_DEVICE_KEY = "token_device"

    object ORDER_STATUS {
        const val PLACED = "ORDER_PLACED"
        const val IN_PROGRESS = "ORDER_IN_PROGRESS"
        const val COMPLETED = "ORDER_COMPLETED"
        const val CANCELED = "ORDER_CANCELED"

        @JvmField
        @Deprecated("")
        val REQUESTED = "ORDER_REQUESTED"
    }

    object PubNubSettings {
        // STG
        const val PUBLISH_KEY = "***REMOVED***"
        const val SUBSCRIBE_KEY = "***REMOVED***"

        // PROD
        // public static final String PUBLISH_KEY = "***REMOVED***" ;
        // public static final String SUBSCRIBE_KEY = "***REMOVED***" ;
        const val DEFAULT_CHANNEL = "el_papa_de_los_papas_channel"
        const val UPDATE_ACTION_MSG = "update_order_list"
    }
}