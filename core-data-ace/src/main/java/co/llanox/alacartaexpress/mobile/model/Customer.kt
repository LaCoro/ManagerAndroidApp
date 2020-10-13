package co.llanox.alacartaexpress.mobile.model

import java.io.Serializable
import java.util.Date

/**
 * Created by llanox on 3/21/16.
 */
data class Customer(var id: String? = null,
                    var phone: String? = null,
                    var fullName: String? = null,
                    var username: String? = null,
                    var email: String? = null,
                    var city: String? = null,
                    var birthday: Date? = null) : Serializable