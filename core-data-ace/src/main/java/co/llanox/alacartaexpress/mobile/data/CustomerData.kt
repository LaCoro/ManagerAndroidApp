package co.llanox.alacartaexpress.mobile.data

import co.llanox.alacartaexpress.mobile.model.Customer
import com.parse.ParseObject

/**
 * Created by llanox on 3/21/16.
 */
object CustomerData {
    @JvmStatic
    fun parseCustomer(o: ParseObject): Customer {
        val customer = Customer()
        customer.id = o.objectId
        customer.birthday = o.getDate("birthday")
        customer.city = o.getString("city")
        customer.phone = o.getString("phone")
        customer.fullName = o.getString("fullname")
        customer.username = o.getString("username")
        customer.email = o.getString("email")
        return customer
    }
}