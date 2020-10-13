package co.llanox.alacartaexpress.admin.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import androidx.core.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


import java.util.Date;
import java.util.List;

import co.llanox.alacartaexpress.mobile.ACEUtil;
import co.llanox.alacartaexpress.mobile.Constants;
import co.llanox.alacartaexpress.admin.R;
import co.llanox.alacartaexpress.mobile.model.Order;

/**
 * Created by jgabrielgutierrez on 15-07-22.
 */
public class OrderAdapter extends ArrayAdapter<Order> {

    private ACEUtil mACEUtil;
    private Context mContext;
    private java.text.DateFormat mDateFormat;
    private java.text.DateFormat mTimeFormat;


    public OrderAdapter(Context context, int resource, List<Order> objects) {
        super(context, resource, objects);
        mACEUtil = new ACEUtil();
        this.mContext = context;
        mDateFormat = android.text.format.DateFormat.getDateFormat(context);
        mTimeFormat = android.text.format.DateFormat.getTimeFormat(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;

        // Check if an existing view is being reused, otherwise inflate the view
        if (v == null) {
            v = LayoutInflater.from(getContext()).inflate(R.layout.order_item, parent, false);
            ItemViewHolder itemViewHolder = new ItemViewHolder();

            itemViewHolder.tvOrderId = (TextView) v.findViewById(R.id.tv_order_id);
            itemViewHolder.tvCreatedAt = (TextView) v.findViewById(R.id.tv_order_date);
            itemViewHolder.tvBuyerInfo = (TextView) v.findViewById(R.id.tv_order_buyer_info);
            itemViewHolder.tvOrderState= (TextView) v.findViewById(R.id.tv_order_state);

            v.setTag(R.id.ITEM_VIEW_TYPE, itemViewHolder);

        }

        ItemViewHolder holder = (ItemViewHolder) v.getTag(R.id.ITEM_VIEW_TYPE);

        Order order = getItem(position);
        v.setTag(R.id.ORDER_ID,order.getId() );
        v.setTag(R.id.DELIVERY_COST_ID, getItem(position).getDeliveryCost());

        holder.tvOrderId.setText(mContext.getString(R.string.txt_order_id, " " + order.getId()));
        holder.tvBuyerInfo.setText(order.getBuyerId() + "--" +order.getBuyerName());
        Date createdAt = order.getCreatedAt();

        holder.tvCreatedAt.setText(" "+mDateFormat.format(createdAt)+" "+mTimeFormat.format(createdAt));

        setOrderState(order,holder.tvOrderState);



        return v;
    }

    private void setOrderState(Order order, TextView tvOrderState) {
        int padding = (int) mContext.getResources().getDimension(R.dimen.padding_labels);
        tvOrderState.setPadding(padding, padding, padding, padding);


        String state = order.getStatus();

        if(Constants.ORDER_STATUS.CANCELED.equalsIgnoreCase(state)){
            tvOrderState.setText(mContext.getString(R.string.txt_order_canceled_state));
            Drawable drawable =  ContextCompat.getDrawable(mContext, R.drawable.canceled_order_rounded_text_view);
            tvOrderState.setBackground(drawable);
            return;
        }

        if(Constants.ORDER_STATUS.REQUESTED.equalsIgnoreCase(state)){
            tvOrderState.setText(mContext.getString(R.string.txt_order_requested_state));
            Drawable drawable =  ContextCompat.getDrawable(mContext, R.drawable.requested_order_rounded_text_view);
            tvOrderState.setBackground(drawable);
            return;
        }

        if(Constants.ORDER_STATUS.PLACED.equalsIgnoreCase(state)){
            tvOrderState.setText(mContext.getString(R.string.txt_order_placed_state));
            Drawable drawable =  ContextCompat.getDrawable(mContext, R.drawable.placed_order_rounded_text_view);
            tvOrderState.setBackground(drawable);
            return;
        }

    }

    @Override
    public boolean isEnabled(int position) {
        return true;
    }

    public static class ItemViewHolder {

        TextView tvOrderId;
        TextView tvCreatedAt;
        TextView tvBuyerInfo;
        TextView tvOrderState;


    }
}
