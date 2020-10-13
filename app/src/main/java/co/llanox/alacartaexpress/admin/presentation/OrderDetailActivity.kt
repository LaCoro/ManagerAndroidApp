package co.llanox.alacartaexpress.admin.presentation

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import co.llanox.alacartaexpress.admin.R
import co.llanox.alacartaexpress.admin.adapters.ViewPagerAdapter
import co.llanox.alacartaexpress.admin.presentation.listorders.OrderListActivity
import co.llanox.alacartaexpress.mobile.Constants
import co.llanox.alacartaexpress.mobile.data.OrderData
import co.llanox.alacartaexpress.mobile.data.OrderDataImpl
import co.llanox.alacartaexpress.mobile.data.OrderDetailData
import co.llanox.alacartaexpress.mobile.data.OrderDetailDataImpl
import co.llanox.alacartaexpress.mobile.data.RetrievedDataListener
import co.llanox.alacartaexpress.mobile.model.Order
import co.llanox.alacartaexpress.mobile.model.OrderDetail
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout

class OrderDetailActivity : AppCompatActivity() {
    private var viewPager: ViewPager? = null
    private var viewPagerAdapter: ViewPagerAdapter? = null
    private var orderData: OrderData? = null
    private var orderDetailData: OrderDetailData? = null
    private val retrievedOrdersListener: RetrievedDataListener<Order> = object : RetrievedDataListener<Order> {
        override fun onRetrievedData(data: List<Order>) {
            val order = data[0]
            orderDetailData?.asyncFindOrderDetailByOrderId(order.id, object : RetrievedDataListener<OrderDetail> {
                override fun onRetrievedData(data: List<OrderDetail>) {
                    val arguments = Bundle()
                    arguments.putSerializable(Constants.ORDER_OBJECT_KEY, order)
                    val orderDetails = OrderDetail.convertToHashMap(data)
                    arguments.putSerializable(Constants.ORDER_DETAILS_OBJECT_KEY, orderDetails)
                    val orderDetailsFragment = PlacedOrderDetailsFragment()
                    orderDetailsFragment.arguments = arguments
                    val infoFragment = OrderInfoFragment()
                    infoFragment.arguments = arguments
                    loadOrderFragments(orderDetailsFragment, infoFragment)
                }

                override fun onError(error: Throwable) {
                    Log.e(TAG, "Error fetching order details", error)
                    viewPager?.let { Snackbar.make(it, R.string.txt_err_retrieving_order_details, Snackbar.LENGTH_LONG).show() }
                }
            })
        }

        override fun onError(error: Throwable) {
            viewPager?.let { Snackbar.make(it, R.string.txt_err_retrieving_order_info, Snackbar.LENGTH_LONG).show() }
        }
    }

    private fun loadOrderFragments(orderDetailsFragment: PlacedOrderDetailsFragment, infoFragment: OrderInfoFragment) {
        viewPagerAdapter = ViewPagerAdapter(supportFragmentManager)
        addFragmentToViewPager(infoFragment, this@OrderDetailActivity.getString(R.string.lb_tab_order_info))
        addFragmentToViewPager(orderDetailsFragment, this@OrderDetailActivity.getString(R.string.lb_tab_order_details))
        viewPager?.adapter = viewPagerAdapter
        val tabLayout = findViewById<TabLayout>(R.id.tabs)
        tabLayout.setupWithViewPager(viewPager)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_detail)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        orderData = OrderDataImpl()
        orderDetailData = OrderDetailDataImpl()


        // Show the Up button in the action bar.
        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
        viewPager = findViewById(R.id.viewpager)
        if (savedInstanceState == null) {
            val currentOrderId = intent.getStringExtra(Constants.ORDER_ID)
            if (currentOrderId != null) {
                fetchOrderInfo(currentOrderId, retrievedOrdersListener)
            }
        }
    }


    private fun fetchOrderInfo(orderId: String, retrievedOrdersListener: RetrievedDataListener<Order>) {
        orderData?.asyncFindByOrderId(orderId, retrievedOrdersListener)
    }

    private fun addFragmentToViewPager(fragment: Fragment, title: String) {
        if (viewPager == null || viewPagerAdapter == null) return
        viewPagerAdapter?.addFragment(fragment, title)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            navigateUpTo(Intent(this, OrderListActivity::class.java))
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        private val TAG = OrderDetailActivity::class.java.canonicalName
    }
}
