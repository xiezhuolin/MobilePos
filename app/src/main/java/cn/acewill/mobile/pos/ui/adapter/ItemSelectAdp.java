package cn.acewill.mobile.pos.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import cn.acewill.mobile.pos.R;
import cn.acewill.mobile.pos.base.adapter.BaseAdapter;
import cn.acewill.mobile.pos.model.MainSelect;
import cn.acewill.mobile.pos.model.Receipt;

/**
 * Created by DHH on 2016/6/17.
 */
public class ItemSelectAdp<T> extends BaseAdapter {
    private List<Receipt> receiptList;
    public ItemSelectAdp(Context context) {
        super(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        final MainSelect mainSelect = (MainSelect) getItem(position);
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.lv_item_select, null);
            holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tv_name.setText(mainSelect.getSelectName());
        return convertView;
    }


    class ViewHolder {
        TextView tv_name;
    }

}
