package cn.acewill.mobile.pos.ui.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cn.acewill.mobile.pos.R;
import cn.acewill.mobile.pos.base.adapter.BaseAdapter;
import cn.acewill.mobile.pos.model.Receipt;
import cn.acewill.mobile.pos.model.user.User;
import cn.acewill.mobile.pos.model.user.UserRet;

/**
 * Created by DHH on 2016/6/17.
 */
public class UserNameAdp<T> extends BaseAdapter {
    private List<Receipt> receiptList;
    public UserNameAdp(Context context) {
        super(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        final User user = (User) getItem(position);
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.lv_item_username, null);
            holder.tv_context = (TextView) convertView.findViewById(R.id.tv_context);
            holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        UserRet userRet = user.getUserRet();
        holder.tv_context.setText(userRet.getUsername());
        if(!TextUtils.isEmpty(userRet.getRealname()))
        {
            holder.tv_name.setText("("+userRet.getRealname()+")");
        }
        return convertView;
    }

    public void setReceipt(List<Receipt> receiptList){
        if (receiptList != null){
            this.receiptList = receiptList;
            this.notifyDataSetChanged();
        }
    }

    private boolean isHaveReceipt(Receipt receipt)
    {
        int size = receiptList.size();
        for (int i = 0; i < size; i++) {
            Receipt selectReceipt = receiptList.get(i);
            if(selectReceipt.getId() == receipt.getId())
            {
                return true;
            }
        }
        return false;
    }




    class ViewHolder {
        TextView tv_context;
        TextView tv_name;
        ImageView iv_select;
    }

}
