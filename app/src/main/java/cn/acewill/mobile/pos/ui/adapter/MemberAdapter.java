package cn.acewill.mobile.pos.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.acewill.mobile.pos.R;
import cn.acewill.mobile.pos.base.adapter.BaseAdapter;
import cn.acewill.mobile.pos.model.wsh.Account;
import cn.acewill.mobile.pos.utils.ToolsUtils;


/**会员卡列表
 * Created by aqw on 2016/10/13.
 */
public class MemberAdapter extends BaseAdapter {
    private int current_select = -1;

    public MemberAdapter(Context context) {
        super(context);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        final Account account = (Account) getItem(position);
        if (convertView == null) {
            holder = new ViewHolder();

            convertView = LayoutInflater.from(context).inflate(R.layout.item_active, null);
            holder.type_ll = (LinearLayout)convertView.findViewById(R.id.type_ll);
            holder.type_name = (TextView)convertView.findViewById(R.id.type_name);
            holder.type_name.setTextSize(15);
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
        holder.type_name.setText(ToolsUtils.logicCardType(account) +"  "+ account.getuActualNo() +"  "+account.getName()+""+ToolsUtils.logicUserCardGender(account)
        +"\n" +"开卡时间:" + account.getRegistered());
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
}
