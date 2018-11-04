package cn.acewill.mobile.pos.ui.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import cn.acewill.mobile.pos.R;
import cn.acewill.mobile.pos.base.adapter.BaseAdapter;
import cn.acewill.mobile.pos.interfices.WSHListener;
import cn.acewill.mobile.pos.model.wsh.WshAccountCoupon;

/**
 * 选择优惠券
 * Created by aqw on 2016/10/19.
 */
public class CouponAdapter extends BaseAdapter {
    private WSHListener callback;

    private int[] selectP;
    private int[] selectCount;//每个代金券选择的张数

    private BigDecimal money = new BigDecimal(0);//消费金额，用于判断优惠券是否有满多少才能使用的限制

    public CouponAdapter(Context context, BigDecimal money, WSHListener callback) {
        super(context);
        this.money = money;
        this.callback = callback;
    }

    public void chandDataSize(int count)
    {
        selectP = new int[count];
        for (int i : selectP) {
            selectP[i] = 0;
        }
        selectCount = new int[count];
        for (int i = 0; i < selectCount.length; i++) {
            selectCount[i] = 1;
        }
//        Log.e("selectCount"+position,""+selectCount[position]);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        final WshAccountCoupon coupon = (WshAccountCoupon)getItem(position);
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_coupons, null);
            holder.coupon_item = (LinearLayout)convertView.findViewById(R.id.coupon_item);
            holder.coupon_name = (TextView)convertView.findViewById(R.id.coupon_name);
            holder.coupon_count_ll = (LinearLayout)convertView.findViewById(R.id.coupon_count_ll);
            holder.reduce_coupon = (ImageView)convertView.findViewById(R.id.reduce_coupon);
            holder.add_coupon = (ImageView)convertView.findViewById(R.id.add_coupon);
            holder.coupon_count = (TextView)convertView.findViewById(R.id.coupon_count);


            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }


        if(selectP[position]==1){
            holder.coupon_item.setSelected(true);
            holder.coupon_count_ll.setVisibility(View.VISIBLE);
        }else {
            holder.coupon_item.setSelected(false);
            holder.coupon_count_ll.setVisibility(View.GONE);
        }

        //未满足条件，禁止选择优惠券
        if(coupon.getEnable_amount()!=null&&coupon.getEnable_amount().compareTo(BigDecimal.ZERO)==1&&coupon.getEnable_amount().compareTo(money)==1){
            holder.coupon_item.setEnabled(false);
            holder.coupon_item.setBackgroundResource(R.drawable.layout_gray);
        }else {
            holder.coupon_item.setEnabled(true);
            holder.coupon_item.setBackgroundResource(R.drawable.layout_selector_gray);
        }

        holder.coupon_name.setText(coupon.getTitle()+"("+coupon.getCoupon_ids().size()+"张)");
        holder.coupon_count.setText(selectCount[position]+"");

        holder.coupon_item.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                if(holder.coupon_item.isSelected()){
                    holder.coupon_item.setSelected(false);
                    selectP[position] = 0;

                }else {
                    holder.coupon_item.setSelected(true);
                    selectP[position] = 1;
                }
                notifyDataSetChanged();
                callback.refrush(getSelectCoupon());
            }
        });

        //减少使用张数
        holder.reduce_coupon.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                int count = selectCount[position];
                if(count>1){
                    selectCount[position] = --count;
                    holder.coupon_count.setText(count+"");
                }
                callback.refrush(getSelectCoupon());
            }
        });

        //增加使用张数
        holder.add_coupon.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                int count = selectCount[position];
                if(count<coupon.getMax_use()&&count<coupon.getCoupon_ids().size()){
                    selectCount[position] = ++count;
                    holder.coupon_count.setText(count+"");
                }
                callback.refrush(getSelectCoupon());
            }
        });


        return convertView;
    }

    class ViewHolder {
        LinearLayout coupon_item;
        TextView coupon_name;
        LinearLayout coupon_count_ll;
        ImageView reduce_coupon;
        ImageView add_coupon;
        TextView coupon_count;
    }

    //获取选中的优惠券
    public List<WshAccountCoupon> getSelectCoupon(){

        if(selectP==null){
            return null;
        }
        List<WshAccountCoupon> coupons = new ArrayList<>();
        for (int i = 0; i < selectP.length; i++) {
            Log.e("selectP"+i+":",selectP[i]+"");
            if (selectP[i]==1){
                WshAccountCoupon coupon = (WshAccountCoupon)getItem(i);
                coupon.setSelectCount(selectCount[i]);
                Log.i("selectCount",""+selectCount[i]);
                coupons.add(coupon);
            }
        }
        return coupons;
    }
}
