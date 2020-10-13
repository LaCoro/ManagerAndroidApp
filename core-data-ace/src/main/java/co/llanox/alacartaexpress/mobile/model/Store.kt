package co.llanox.alacartaexpress.mobile.model

import java.io.Serializable
import java.math.BigDecimal
import java.util.ArrayList

/**
 * Created by jgabrielgutierrez on 15-07-22.
 */
data class Store(var id: String, var name: String, var description: String, var address: String, var imageId: String,
                 var tags: ArrayList<String>, var openTime: String, var closeTime: String, var isOpen: Boolean,
                 var deliveryCost: BigDecimal, var phone: String, var city: String) : Serializable {


    override fun toString(): String {
        return "$name $tags"
    }

    companion object {
        const val NAME = "Store"
    }

    object StoreStates {
        const val OPENED = "opened"
        const val CLOSED = "closed"
    }

}

