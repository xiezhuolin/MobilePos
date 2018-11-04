package cn.acewill.mobile.pos.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import cn.acewill.mobile.pos.R;
import cn.acewill.mobile.pos.base.adapter.BaseAdapter;
import cn.acewill.mobile.pos.model.WorkShiftReport;
import cn.acewill.mobile.pos.widget.ScrolListView;


/**
 * Created by DHH on 2016/6/17.
 */
public class ShowReportAdp<T> extends BaseAdapter {
    public ShowReportAdp(Context context) {
        super(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        final WorkShiftReport.ItemCategorySalesDataList itemCategorySalesDataList = (WorkShiftReport.ItemCategorySalesDataList) getItem(position);
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_lv_show_report, null);
            holder.tv_categorySales_title = (TextView) convertView.findViewById(R.id.tv_categorySales_title);
            holder.lv_categorySales = (ScrolListView) convertView.findViewById(R.id.lv_categorySales);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tv_categorySales_title.setText(itemCategorySalesDataList.getName());
        CategorySalesDailyAdp categorySalesAdp = new CategorySalesDailyAdp(context);
        categorySalesAdp.setData(itemCategorySalesDataList.getItemSalesDataList());
        holder.lv_categorySales.setAdapter(categorySalesAdp);
        return convertView;
    }
    class ViewHolder {
        TextView tv_categorySales_title;
        ScrolListView lv_categorySales;
    }

}
