package cn.acewill.mobile.pos.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import cn.acewill.mobile.pos.R;
import cn.acewill.mobile.pos.base.adapter.BaseAdapter;
import cn.acewill.mobile.pos.model.NetOrderRea;


/**
 * Created by DHH on 2016/6/17.
 */
public class DialogListAdp<T> extends BaseAdapter {
    public DialogListAdp(Context context) {
        super(context);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_dialog_list, null);
            holder.tv_content = (TextView) convertView.findViewById(R.id.tv_content);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        NetOrderRea reason = (NetOrderRea)dataList.get(position);
        holder.tv_content.setText(reason.refuseReason);

        return convertView;
    }
    class ViewHolder {
        TextView tv_content;
    }

}
