package co.llanox.alacartaexpress.mobile.model

import java.io.Serializable
import java.util.Date

/**
 * Created by jgabrielgutierrez on 15-09-11.
 */
data class User ( var id: String? = null,
                  var fullname: String? = null,
                  var username: String? = null,
                  var phone: String? = null,
                  var email: String? = null,
                  var city: String? = null,
                  var managedStoreId: String? = null,
                  var birthday: Date? = null,
                  var token: String? = null ) : Serializable

