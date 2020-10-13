package co.llanox.alacartaexpress.admin.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;

import co.llanox.alacartaexpress.mobile.ACEUtil;
import co.llanox.alacartaexpress.admin.R;
import co.llanox.alacartaexpress.mobile.model.OrderDetail;

/**
 * Created by jgabrielgutierrez on 15-07-22.
 */
public class OrderDetailAdapter extends ArrayAdapter<OrderDetail> {


    private Resources resources;


    public OrderDetailAdapter(Context context, int resource, Collection<OrderDetail> objects) {
        super(context, resource, new ArrayList<>(objects));
        this.resources = context.getResources();

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.order_detail_item, parent, false);
        }

        TextView tv = (TextView) convertView.findViewById(R.id.tv_order_id);
        @SuppressLint("StringFormatMatches")
        String nameAndQty = resources.getString(R.string.txt_product_name, this.getItem(position).getProduct().getName(), this.getItem(position).getQuantity());
        tv.setText(nameAndQty);


        tv = (TextView) convertView.findViewById(R.id.tv_product_price);
        String price = resources.getString(R.string.txt_product_price, getItem(position).getProduct().getPrice());
        tv.setText(price);


        return convertView;
    }
}
