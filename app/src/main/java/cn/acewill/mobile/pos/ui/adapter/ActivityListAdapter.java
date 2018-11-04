package cn.acewill.mobile.pos.ui.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.math.BigDecimal;

import cn.acewill.mobile.pos.R;
import cn.acewill.mobile.pos.base.adapter.BaseAdapter;
import cn.acewill.mobile.pos.model.order.MarketingActivity;


/**营销活动
 * Created by aqw on 2016/10/13.
 */
public class ActivityListAdapter extends BaseAdapter {
    private int current_select = -1;
    private String allMoney;//总金额

    public ActivityListAdapter(Context context) {
        super(context);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        final MarketingActivity activitys = (MarketingActivity) getItem(position);
        if (convertView == null) {
            holder = new ViewHolder();

            convertView = LayoutInflater.from(context).inflate(R.layout.item_active, null);
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
        holder.type_name.setText(activitys.getDiscountName());

        if(!TextUtils.isEmpty(allMoney))
        {
            if (activitys.getDiscountType() == 1) {
                BigDecimal reduceMoney = activitys.getDiscountAmount();
                if (reduceMoney.compareTo(new BigDecimal(allMoney)) == 1)//如果折扣金额大于订单金额
                {
                    holder.type_ll.setBackgroundResource(R.color.light_gray_btn);
                }
                else{
                    holder.type_ll.setBackgroundResource(R.color.white);
                }
            }
            else{
                holder.type_ll.setBackgroundResource(R.color.white);
            }
        }
        else{
            holder.type_ll.setBackgroundResource(R.color.white);
        }

//        if(!TextUtils.isEmpty(allMoney)){
//            if (activitys.getDiscountInfo() != null && activitys.getDiscountInfo().size() > 0) {
//                DiscountInfoObject discountInfo = activitys.getDiscountInfo().get(0);
//                if (discountInfo.getDiscountType() == 0) {//折扣类型int, 0:满减， 1:折扣
//                    if (new BigDecimal(allMoney).compareTo(discountInfo.getSupportMinPrice()) != -1) {//符合满减条件
//                        holder.type_ll.setBackgroundResource(R.color.transparent);
//                    }else{//不符合
//                        holder.type_ll.setBackgroundResource(R.drawable.layout_gray);
//                    }
//                }else{//折扣判断后台设置的不超过最大值和不低于最小值
//
//                    holder.type_ll.setBackgroundResource(R.color.transparent);
//                    BigDecimal max = discountInfo.getSupportMaxPrice();
//                    BigDecimal min = discountInfo.getUnsupportMinPrice();
//                    if(max!=null){//判断是否超过设定的最大金额
//                        if (new BigDecimal(allMoney).compareTo(max) == 1) {
//                            holder.type_ll.setBackgroundResource(R.drawable.layout_gray);
//                        }
//                    }
//                    if(min!=null){
//                        if (new BigDecimal(allMoney).compareTo(min) == -1) {
//                            holder.type_ll.setBackgroundResource(R.drawable.layout_gray);
//                        }
//                    }
//
//                }
//            }else{
//                holder.type_ll.setBackgroundResource(R.drawable.layout_gray);
//            }
//        }

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

    public int getCurrent_select(){
        return current_select;
    }


    public void setAllMoney(String allMoney) {
        this.allMoney = allMoney;
        this.notifyDataSetChanged();
    }
}
