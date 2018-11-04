package cn.acewill.mobile.pos.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import cn.acewill.mobile.pos.R;
import cn.acewill.mobile.pos.base.adapter.BaseAdapter;
import cn.acewill.mobile.pos.model.WorkShiftReport;

import static cn.acewill.mobile.pos.R.id.tv_dishName;


/**
 * Created by DHH on 2016/6/17.
 */
public class CategorySalesDailyAdp<T> extends BaseAdapter {
    public CategorySalesDailyAdp(Context context) {
        super(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        final WorkShiftReport.ItemCategorySalesDataList.ItemSalesDataList itemSalesDataList = (WorkShiftReport.ItemCategorySalesDataList.ItemSalesDataList) getItem(position);
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_lv_category_sales, null);
            holder.tv_dishName = (TextView) convertView.findViewById(tv_dishName);
            holder.tv_count = (TextView) convertView.findViewById(R.id.tv_count);
            holder.tv_price = (TextView) convertView.findViewById(R.id.tv_price);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tv_dishName.setText(itemSalesDataList.getName());
        holder.tv_count.setText(itemSalesDataList.getItemCounts()+"");
        holder.tv_price.setText(itemSalesDataList.getTotal()+" ￥");

        return convertView;
    }
    class ViewHolder {
        TextView tv_dishName;
        TextView tv_count;
        TextView tv_price;
    }

}
