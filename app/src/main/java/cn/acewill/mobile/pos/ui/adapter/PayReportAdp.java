package cn.acewill.mobile.pos.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.math.BigDecimal;

import cn.acewill.mobile.pos.R;
import cn.acewill.mobile.pos.base.adapter.BaseAdapter;
import cn.acewill.mobile.pos.model.OrderItemReportData;

/**
 * 支付报表统计
 * Created by aqw on 2017/1/9.
 */
public class PayReportAdp extends BaseAdapter {

    public PayReportAdp(Context context) {
        super(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;

        OrderItemReportData.ItemSalesData itemSalesData = (OrderItemReportData.ItemSalesData) getItem(position);
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_report_pay, null);
            holder.bg_ll = (LinearLayout)convertView.findViewById(R.id.bg_ll);
            holder.pay_name = (TextView) convertView.findViewById(R.id.pay_name);
            holder.num = (TextView) convertView.findViewById(R.id.num);
            holder.money = (TextView) convertView.findViewById(R.id.money);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.pay_name.setText(itemSalesData.name);
        holder.num.setText(itemSalesData.itemCounts+"");
        holder.money.setText("￥"+itemSalesData.total.setScale(2, BigDecimal.ROUND_DOWN));

        if(position%2==0){
            holder.bg_ll.setBackgroundResource(R.color.layout_gray);
        }else {
            holder.bg_ll.setBackgroundResource(R.color.transform);
        }

        return convertView;
    }

    class ViewHolder {
        LinearLayout bg_ll;
        TextView pay_name;
        TextView num;
        TextView money;
    }
}
