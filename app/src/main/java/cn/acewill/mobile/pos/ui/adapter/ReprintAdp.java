package cn.acewill.mobile.pos.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.acewill.mobile.pos.R;
import cn.acewill.mobile.pos.base.adapter.BaseAdapter;
import cn.acewill.mobile.pos.common.ReprintController;
import cn.acewill.mobile.pos.printer.Printer;


/**
 * Created by DHH on 2016/6/17.
 */
public class ReprintAdp<T> extends BaseAdapter {

    public ReprintAdp(Context context) {
        super(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        final Printer printer = (Printer) ReprintController.getRePrinterList().get(position);
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_lv_reprint, null);
            holder.tv_context = (TextView) convertView.findViewById(R.id.tv_context);
            holder.rel_ll = (RelativeLayout) convertView.findViewById(R.id.rel_ll);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tv_context.setText(printer.getDeviceName());
        if (printer.isSelect) {
            holder.rel_ll.setBackgroundResource(R.drawable.border_green);
        } else {
            holder.rel_ll.setBackgroundResource(R.drawable.border_gray1_xb);
        }
        return convertView;
    }

    class ViewHolder {
        TextView tv_context;
        RelativeLayout rel_ll;
    }

}
