package cn.acewill.mobile.pos.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.acewill.mobile.pos.R;
import cn.acewill.mobile.pos.base.adapter.BaseAdapter;
import cn.acewill.mobile.pos.model.order.OrderSingleReason;


/**
 * Created by aqw on 2016/9/20.
 */
public class OrderSingleReasonAdp extends BaseAdapter {

    private int current_select = -1;

    public OrderSingleReasonAdp(Context context) {
        super(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        final OrderSingleReason orderSingleReason = (OrderSingleReason) getItem(position);
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_reason, null);
            holder.type_ll = (LinearLayout)convertView.findViewById(R.id.type_ll);
            holder.type_name = (TextView)convertView.findViewById(R.id.type_name);

            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        if(current_select==position){
            holder.type_ll.setSelected(true);
        }else {
            holder.type_ll.setSelected(false);
        }

        holder.type_name.setText(orderSingleReason.reason);

        return convertView;
    }

    class ViewHolder {
        LinearLayout type_ll;
        TextView type_name;
    }

    public void setCurrent_select(int positiion){
        if(current_select == positiion){
            return;
        }

        this.current_select = positiion;
        this.notifyDataSetChanged();
    }

    public int getSelectId(){
        if(current_select==-1){
            return -1;
        }
        OrderSingleReason reason = (OrderSingleReason) getItem(current_select);
        return reason.id;
    }
}
