package co.llanox.alacartaexpress.admin.presentation.listorders

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AbsListView
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import co.llanox.alacartaexpress.admin.ACEApplication
import co.llanox.alacartaexpress.admin.R
import co.llanox.alacartaexpress.admin.adapters.OrderAdapter
import co.llanox.alacartaexpress.admin.messages.MessagesHandler
import co.llanox.alacartaexpress.admin.presentation.LoginActivity
import co.llanox.alacartaexpress.admin.presentation.OrderDetailActivity
import co.llanox.alacartaexpress.admin.presentation.OrderDetailFragment
import co.llanox.alacartaexpress.mobile.Constants
import co.llanox.alacartaexpress.mobile.data.BackendException
import co.llanox.alacartaexpress.mobile.model.Order
import com.google.android.material.snackbar.Snackbar


class OrderListActivity : AppCompatActivity(), OnItemClickListener, OrderListView {

    private var mTwoPane = false
    private var mListView: AbsListView? = null
    private var presenter: OrderListPresenter? = null


    private var mAdapter: ArrayAdapter<*>? = null
    override fun onResume() {
        super.onResume()
        presenter?.attachView(this)
        presenter?.init()
        presenter?.startRealTime()
    }

    override fun onStop() {
        super.onStop()
        presenter?.stopRealTime()
        presenter?.detachView()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_list)
        val applicationSessionHelper = (this.application as ACEApplication).applicationSession
        presenter = MessagesHandler?.let { OrderListPresenter(it, applicationSessionHelper) }
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        if (toolbar != null) {
            toolbar.title = this.resources.getString(R.string.title_fragment_placed_orders)
        }
        mListView = findViewById<View>(R.id.order_list) as ListView
        mListView?.onItemClickListener = this
        if (findViewById<View?>(R.id.order_detail_container) != null) {
            mTwoPane = true
        }
    }

    private fun refreshPlacedOrders() {
        presenter?.refreshPlacedOrders()
    }

    override fun onItemClick(adapterView: AdapterView<*>?, view: View, position: Int, l: Long) {
        if (mTwoPane) {
            val arguments = Bundle()
            arguments.putString(Constants.ORDER_ID, view.getTag(R.id.ORDER_ID).toString())
            val fragment = OrderDetailFragment()
            fragment.arguments = arguments
            supportFragmentManager.beginTransaction()
                .replace(R.id.order_detail_container, fragment)
                .commit()
        } else {
            val intent = Intent(this@OrderListActivity, OrderDetailActivity::class.java)
            intent.putExtra(Constants.ORDER_ID, view.getTag(R.id.ORDER_ID).toString())
            this@OrderListActivity.startActivity(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.refresh_item -> {
                refreshPlacedOrders()
                return true
            }
            R.id.logout_item -> {
                logOutUser()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun logOutUser() {
        presenter?.logOut()
    }

    override fun goToLogin() {
        Intent(this@OrderListActivity, LoginActivity::class.java).apply {
            startActivity(this)
            finish()
        }

    }

    override fun renderOrders(data: List<Order>) {
        mAdapter = OrderAdapter(this, android.R.layout.simple_list_item_1, data)
        mListView?.adapter = mAdapter
        mListView?.let { Snackbar.make(it, R.string.txt_placed_orders_updated, Snackbar.LENGTH_LONG).show() }
    }

    override fun onErrorFetchingOrders(error: Any) {
        mListView?.let { Snackbar.make(it, R.string.txt_err_updating_placed_orders, Snackbar.LENGTH_LONG).setAction(R.string.txt_try_again) { refreshPlacedOrders() }.show() }
    }

    override fun showBackendError(error: BackendException?) {
        mListView?.let { Snackbar.make(it, getString(R.string.err_general_error, error?.errorCode), Snackbar.LENGTH_LONG).show() }
    }
}
