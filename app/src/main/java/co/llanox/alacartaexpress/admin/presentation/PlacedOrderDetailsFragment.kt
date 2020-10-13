package co.llanox.alacartaexpress.admin.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.AdapterView
import android.widget.Button
import android.widget.ListAdapter
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import co.llanox.alacartaexpress.admin.R
import co.llanox.alacartaexpress.admin.adapters.OrderDetailAdapter
import co.llanox.alacartaexpress.admin.messages.MessagesHandler
import co.llanox.alacartaexpress.admin.messages.RealTimeMessages
import co.llanox.alacartaexpress.mobile.ACEErrorHandler
import co.llanox.alacartaexpress.mobile.Constants
import co.llanox.alacartaexpress.mobile.ErrorHandler
import co.llanox.alacartaexpress.mobile.data.OrderData
import co.llanox.alacartaexpress.mobile.data.OrderDataImpl
import co.llanox.alacartaexpress.mobile.data.SentDataListener
import co.llanox.alacartaexpress.mobile.model.Order
import co.llanox.alacartaexpress.mobile.model.OrderDetail
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import java.math.BigDecimal
import java.util.HashMap

class PlacedOrderDetailsFragment
/**
 * Mandatory empty constructor for the fragment manager to instantiate the
 * fragment (e.g. upon screen orientation changes).
 */
    : Fragment() {
    private var mContentActivity: AppCompatActivity? = null
    private var mFooterPanel: View? = null
    private var mOrderDetails: HashMap<String, OrderDetail>? = HashMap()
    private var mDeliveryCost: BigDecimal? = null
    private var mRequests: TextView? = null
    private var mOrder: Order? = null
    private var mOrderData: OrderData? = null
    private var mBtnChangeOrderStatus: Button? = null
    private var mBtnCancelOrder: Button? = null
    private var realTimeMessages: RealTimeMessages? = null
    private var errorHandler: ErrorHandler? = null

    /**
     * The fragment's ListView/GridView.
     */
    private var mListView: AbsListView? = null

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private var mAdapter: ListAdapter? = null
    private val changeOrderStatusListener = View.OnClickListener {
        if (mOrder?.status == Constants.ORDER_STATUS.PLACED) {
            mOrder?.status = Constants.ORDER_STATUS.IN_PROGRESS
        } else if (mOrder?.status == Constants.ORDER_STATUS.IN_PROGRESS) {
            mOrder?.status = Constants.ORDER_STATUS.COMPLETED
        }
        mOrder?.let { order -> mOrderData?.asyncUpdateOrder(order, callbackUpdateOrder) }
    }
    private val cancelOrderListener = View.OnClickListener {
        mOrder?.status = Constants.ORDER_STATUS.CANCELED
        mOrder?.let { order -> mOrderData?.asyncUpdateOrder(order, callbackCanceledOrder) }
    }
    private val callbackCanceledOrder: SentDataListener<Order> = object : SentDataListener<Order> {
        override fun onResponse(response: Order) {
            Snackbar.make(mBtnChangeOrderStatus!!, R.string.txt_order_canceled, Snackbar.LENGTH_LONG).show()
            disableButtons()
            realTimeMessages?.publish("store_" + response.store?.id, Gson().toJson(response))
        }

        override fun onError(error: Throwable) {
            errorHandler?.showHumanReadableError(error, this@PlacedOrderDetailsFragment.requireContext())
        }
    }
    private val callbackUpdateOrder: SentDataListener<Order> = object : SentDataListener<Order> {
        override fun onResponse(response: Order) {
            response.status?.let { mBtnChangeOrderStatus?.let { it1 -> Snackbar.make(it1, it, Snackbar.LENGTH_LONG).show() } }
            updateOrderStatusActionButtons(response)
            realTimeMessages?.publish("store_" + response.store?.id, Gson().toJson(response))
        }

        override fun onError(error: Throwable) {
            errorHandler?.showHumanReadableError(error, this@PlacedOrderDetailsFragment.requireContext())
        }
    }

    private fun updateOrderStatusActionButtons(order: Order?) {
        when (order?.status) {
            Constants.ORDER_STATUS.PLACED -> {
                mBtnChangeOrderStatus?.text = Constants.ORDER_STATUS.IN_PROGRESS
            }
            Constants.ORDER_STATUS.IN_PROGRESS -> {
                mBtnChangeOrderStatus?.text = Constants.ORDER_STATUS.COMPLETED
            }
            Constants.ORDER_STATUS.COMPLETED -> {
                disableButtons()
            }
            Constants.ORDER_STATUS.CANCELED -> {
                disableButtons()
            }
        }
    }

    private fun disableButtons() {
        mBtnCancelOrder?.isEnabled = false
        mBtnCancelOrder?.background = this.resources.getDrawable(R.drawable.canceled_order_rounded_text_view,null)
        mBtnChangeOrderStatus?.isEnabled = false
        mBtnChangeOrderStatus?.background = this.resources.getDrawable(R.drawable.canceled_order_rounded_text_view, null)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        errorHandler = ACEErrorHandler.instance
        mContentActivity = this.activity as AppCompatActivity?
        realTimeMessages = MessagesHandler.instance
        if (arguments != null) {
            mOrderDetails = arguments?.getSerializable(Constants.ORDER_DETAILS_OBJECT_KEY) as HashMap<String, OrderDetail>
            mOrder = arguments?.getSerializable(Constants.ORDER_OBJECT_KEY) as Order?
            mDeliveryCost = mOrder?.store?.deliveryCost
        }
        val details: MutableCollection<OrderDetail>? = mOrderDetails?.values
        mAdapter = OrderDetailAdapter(activity, android.R.layout.simple_list_item_1, details)
        mOrderData = OrderDataImpl()
    }

    private fun updateTotal() {
        var productsQuantity = 0
        var totalPrice = 0.0
        mOrderDetails?.forEach { _, orderDetail ->
            val price = orderDetail.product?.price
            var quantity = orderDetail.quantity
             price?.times(quantity)?:0.let {
                 totalPrice += it.toDouble()
             }
            productsQuantity += quantity
        }

        mFooterPanel?.isVisible = true
        var tv = mFooterPanel?.findViewById<TextView>(R.id.tv_order_cost)
        tv?.text = mContentActivity!!.resources.getString(R.string.lb_price, totalPrice)
        tv = mFooterPanel?.findViewById(R.id.tv_delivery_cost)
        tv?.text = mContentActivity!!.resources.getString(R.string.lb_price, mDeliveryCost)
        val total = mDeliveryCost?.add(BigDecimal(totalPrice))
        tv = mFooterPanel?.findViewById(R.id.tv_total)
        tv?.text = mContentActivity!!.resources.getString(R.string.lb_price, total)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_view_order, container, false)
        mFooterPanel = view.findViewById(R.id.footer)
        updateTotal()


        // Set the adapter
        mListView = view.findViewById(android.R.id.list)
        (mListView as AdapterView<ListAdapter?>?)!!.setAdapter(mAdapter)
        val toolbar = mContentActivity!!.supportActionBar
        if (toolbar != null) {
            toolbar.setTitle(mContentActivity!!.resources.getString(R.string.title_fragment_view_order))
            toolbar.setDisplayHomeAsUpEnabled(true)
            toolbar.setHomeButtonEnabled(true)
        }
        mRequests = mFooterPanel?.findViewById(R.id.tv_order_requests)
        mRequests?.text = mOrder?.additionalRequests
        mBtnChangeOrderStatus = mFooterPanel?.findViewById(R.id.btn_request_order)
        mBtnCancelOrder = mFooterPanel?.findViewById(R.id.btn_cancel_order)
        updateOrderStatusActionButtons(mOrder)
        if (Constants.ORDER_STATUS.PLACED.equals(mOrder?.status, ignoreCase = true) ||
            Constants.ORDER_STATUS.IN_PROGRESS.equals(mOrder?.status, ignoreCase = true)) {
            mBtnChangeOrderStatus?.setOnClickListener(changeOrderStatusListener)
            mBtnCancelOrder?.setOnClickListener(cancelOrderListener)
        }
        return view
    }


    companion object {
        fun newInstance(params: Bundle?): PlacedOrderDetailsFragment {
            val fragment = PlacedOrderDetailsFragment()
            if (params == null || params.size() == 0) return fragment
            fragment.arguments = params
            return fragment
        }
    }
}