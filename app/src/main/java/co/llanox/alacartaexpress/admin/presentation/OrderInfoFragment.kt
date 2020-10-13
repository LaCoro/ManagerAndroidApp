package co.llanox.alacartaexpress.admin.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import co.llanox.alacartaexpress.admin.R
import co.llanox.alacartaexpress.mobile.Constants
import co.llanox.alacartaexpress.mobile.model.Order
import java.text.DateFormat


class OrderInfoFragment

    : Fragment() {
    private var mOrder: Order? = null
    private var mDateFormat: DateFormat? = null
    private var mTimeFormat: DateFormat? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments!!.containsKey(Constants.ORDER_OBJECT_KEY)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            mOrder = arguments!!.getSerializable(Constants.ORDER_OBJECT_KEY) as Order?
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.order_detail, container, false)
        mDateFormat = android.text.format.DateFormat.getDateFormat(activity)
        mTimeFormat = android.text.format.DateFormat.getTimeFormat(activity)
        val createdAt = mOrder?.createdAt
        val scheduleDate = mOrder?.scheduledDeliveryDate
        val tvOrderId = rootView.findViewById<TextView>(R.id.tv_order_id)
        tvOrderId.text = mOrder?.id
        val tvDeliveryAddress = rootView.findViewById<TextView>(R.id.tv_delivery_address)
        tvDeliveryAddress.text = mOrder?.deliveryAddress
        val tvBuyerInfo = rootView.findViewById<TextView>(R.id.tv_order_buyer_info)
        val buyerInfo = getString(R.string.txt_buyer_info_format, mOrder?.buyerId, mOrder?.buyerName)
        tvBuyerInfo.text = buyerInfo
        val tvOrderCustomer = rootView.findViewById<TextView>(R.id.tv_order_customer_info)
        tvOrderCustomer.text = mOrder?.customer?.fullName
        val tvOrderCustomerPhone = rootView.findViewById<TextView>(R.id.tv_order_customer_phone)
        tvOrderCustomerPhone.text = mOrder?.customer?.phone
        val tvOrderCreatedAt = rootView.findViewById<TextView>(R.id.tv_order_created_at)
        val sCreatedAt = mDateFormat?.format(createdAt) + " " + mTimeFormat?.format(createdAt)
        tvOrderCreatedAt.text = sCreatedAt
        val tvScheduledTimeOrder = rootView.findViewById<TextView>(R.id.tv_scheduled_time_order)
        //        String sScheduleDate = mDateFormat.format(scheduleDate)+" "+mTimeFormat.format(scheduleDate);

//        tvScheduledTimeOrder.setText(sScheduleDate);
        val tvStoreName = rootView.findViewById<TextView>(R.id.tv_store_name)
        tvStoreName.text = mOrder?.store?.name
        val tvStoreAddress = rootView.findViewById<TextView>(R.id.tv_store_address)
        tvStoreAddress.text = mOrder?.store?.address
        val tvStorePhone = rootView.findViewById<TextView>(R.id.tv_store_phone)
        tvStorePhone.text = mOrder?.store?.phone
        val tvStoreCity = rootView.findViewById<TextView>(R.id.tv_store_city)
        tvStoreCity.text = mOrder?.store?.city
        val tvCashAmount = rootView.findViewById<TextView>(R.id.tv_cash_amount)
        tvCashAmount.text = mOrder?.cashPayment?.toPlainString()
        return rootView
    }

}