package co.llanox.alacartaexpress.admin.presentation

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import co.llanox.alacartaexpress.admin.R
import co.llanox.alacartaexpress.admin.adapters.OrderAdapter
import co.llanox.alacartaexpress.admin.presentation.PlacedOrdersFragment
import co.llanox.alacartaexpress.mobile.ACEErrorHandler.Companion.instance
import co.llanox.alacartaexpress.mobile.ErrorHandler
import co.llanox.alacartaexpress.mobile.data.OrderData
import co.llanox.alacartaexpress.mobile.data.OrderDataImpl
import co.llanox.alacartaexpress.mobile.data.RetrievedDataListener
import co.llanox.alacartaexpress.mobile.model.Order

class PlacedOrdersFragment: Fragment(), RetrievedDataListener<Order> {
    private var mOrderData: OrderData? = null
    private var mContentActivity: AppCompatActivity? = null
    private var errorHandler: ErrorHandler? = null

    /**
     * The fragment's ListView/GridView.
     */
    private var mListView: AbsListView? = null

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private var mAdapter: ArrayAdapter<*>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        errorHandler = instance
        mOrderData = OrderDataImpl()
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_placed_orders, container, false)
        mListView = view.findViewById(android.R.id.list)
        val toolbar = mContentActivity!!.supportActionBar
        if (toolbar != null) {
            toolbar.title = mContentActivity!!.resources.getString(R.string.title_fragment_placed_orders)
            toolbar.setHomeButtonEnabled(true)
            toolbar.setDisplayShowHomeEnabled(true)
            toolbar.setDisplayShowTitleEnabled(true)
        }
        return view
    }

    override fun onResume() {
        super.onResume()
        refreshPlacedOrders()
    }

    override fun onRetrievedData(data: List<Order>) {
        mAdapter = OrderAdapter(this.activity, android.R.layout.simple_list_item_1, data)
        mListView?.adapter = mAdapter
    }

    override fun onError(error: Throwable) {
        errorHandler!!.showHumanReadableError(error, this@PlacedOrdersFragment.activity!!)
    }

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        mContentActivity = try {
            activity as AppCompatActivity
        } catch (e: ClassCastException) {
            throw ClassCastException(activity.toString()
                + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
    }

    override fun onPrepareOptionsMenu(menu: Menu) {}
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.toolbar_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.refresh_item -> {
                refreshPlacedOrders()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun refreshPlacedOrders() {
        Toast.makeText(this.activity, "Updating Order List", Toast.LENGTH_LONG).show()
        mOrderData?.asyncFindByState(this, "")
    }

    companion object {
        fun newInstance(params: Bundle?): PlacedOrdersFragment {
            val fragment = PlacedOrdersFragment()
            if (params == null || params.size() == 0) return fragment
            fragment.arguments = params
            return fragment
        }
    }
}