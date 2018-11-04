package cn.acewill.mobile.pos.ui.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.math.BigDecimal;
import java.util.List;

import cn.acewill.mobile.pos.R;
import cn.acewill.mobile.pos.base.adapter.BaseAdapter;
import cn.acewill.mobile.pos.common.RetreatDishController;
import cn.acewill.mobile.pos.config.MyApplication;
import cn.acewill.mobile.pos.model.dish.Dish;
import cn.acewill.mobile.pos.model.dish.Option;
import cn.acewill.mobile.pos.model.order.OrderItem;


/**
 * 订单详情
 * Created by aqw on 2016/9/7.
 */
public class OrderInforNewAdp extends BaseAdapter {
    public OrderInforNewAdp(Context context) {
        super(context);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder ;
        final OrderItem orderItem = (OrderItem) getItem(position);
        final OrderItem tempItem = RetreatDishController.getTempItemList().get(position);
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_order_infor_new, null);
            holder.dish_name = (TextView)convertView.findViewById(R.id.dish_name);
            holder.dish_count = (TextView)convertView.findViewById(R.id.dish_count);
            holder.dish_price = (TextView)convertView.findViewById(R.id.dish_price);
            holder.dish_sub = (TextView)convertView.findViewById(R.id.dish_sub);
            holder.tv_count = (TextView)convertView.findViewById(R.id.tv_count);
            holder.btn_add = (Button) convertView.findViewById(R.id.btn_add);
            holder.btn_minus = (Button) convertView.findViewById(R.id.btn_minus);
            holder.rel_do = (RelativeLayout) convertView.findViewById(R.id.rel_do);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }
        if(orderItem.subItemList != null && orderItem.subItemList.size() >0)// 套餐菜品
        {
            holder.dish_sub.setVisibility(View.VISIBLE);
            BigDecimal money = orderItem.getCost();
            holder.dish_name.setText(orderItem.getDishName());
            holder.dish_count.setText(money.toString()+"*"+orderItem.getQuantity());
            BigDecimal b2 = new BigDecimal(orderItem.getQuantity()+"");
            BigDecimal subPrice = b2.multiply(orderItem.getCost()==null?new BigDecimal(0):orderItem.getCost()).setScale(2, BigDecimal.ROUND_DOWN);
            holder.dish_price.setText(subPrice+"");

            List<Dish.Package> subItemList = orderItem.getSubItemList();
            StringBuffer sb = new StringBuffer();
            String space = "";
            for (int a = 0; a < subItemList.size(); a++) {
                if(a != subItemList.size()-1)
                {
                    space = "\n";
                }
                String skuStrs = "";
                skuStrs = printDishOption(subItemList.get(a).optionList);
//                if(!TextUtils.isEmpty(subItemList.get(a).skuStr))
//                {
//                    skuStrs = subItemList.get(a).skuStr;
//                }
                sb.append((a+1)+"."+subItemList.get(a).getDishName()+"     "+subItemList.get(a).quantity+"/份 "+ skuStrs+space);
            }
            holder.dish_sub.setText(sb.toString());
        }
        else
        {
            holder.dish_sub.setVisibility(View.GONE);
            holder.dish_name.setText(orderItem.getDishName());
            holder.dish_count.setText(orderItem.getPrice()+"*"+orderItem.getQuantity());
            BigDecimal b1 = new BigDecimal(orderItem.getQuantity()+"");
            BigDecimal dishPrice = b1.multiply(orderItem.getCost()==null?new BigDecimal(0):orderItem.getCost()).setScale(2, BigDecimal.ROUND_DOWN);
            holder.dish_price.setText(dishPrice+"");
            holder.tv_count.setText(tempItem.rejectedQuantity+"");

            String skuStrs = printDishOption(orderItem.optionList);
            if(!TextUtils.isEmpty(skuStrs))
            {
                holder.dish_sub.setVisibility(View.VISIBLE);
                holder.dish_sub.setText(skuStrs);
            }
        }

        //加
        holder.btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectQuantity = ++tempItem.rejectedQuantity;
                //System.out.println(selectQuantity+"=====++++===="+tempItem.quantity);
                if(selectQuantity < tempItem.quantity  || selectQuantity == tempItem.quantity)
                {
                    holder.tv_count.setText(selectQuantity+"");
                    RetreatDishController.addItem(position,selectQuantity);
                }
                else
                {
                    --tempItem.rejectedQuantity;
                    MyApplication.getInstance().ShowToast("超出可选份额!");
                    holder.tv_count.setText(tempItem.rejectedQuantity+"");
                }
            }
        });

        //减
        holder.btn_minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectQuantity = --tempItem.rejectedQuantity;
                //                System.out.println(selectQuantity+"=====----===="+tempItem.quantity);
                if(selectQuantity >0 && selectQuantity != 0)
                {
                    holder.tv_count.setText(selectQuantity+"");
                    RetreatDishController.minusItem(position,selectQuantity);
                }
                else
                {
                    tempItem.rejectedQuantity = 0;
                    holder.tv_count.setText(tempItem.rejectedQuantity+"");
                }
            }
        });
        if(isRetreat == false)
        {
            holder.btn_minus.setVisibility(View.GONE);
            holder.btn_add.setVisibility(View.GONE);
        }
        else
        {
            holder.btn_minus.setVisibility(View.VISIBLE);
            holder.btn_add.setVisibility(View.VISIBLE);
        }
        return convertView;
    }

    class ViewHolder {
        TextView dish_name;
        TextView dish_count;
        TextView dish_price;
        TextView dish_sub;
        TextView tv_count;
        Button btn_add;
        Button btn_minus;
        RelativeLayout rel_do;
    }
    private boolean isRetreat = true;
    public void isRetreatDish(boolean isRetreat)
    {
        this.isRetreat = isRetreat;
    }

    public interface Retreat{
        //isOneItem为true时，说明此订单只有一个菜品并且数量为1，这时候调用退菜就会调用退单接口
        public void onRetreat(OrderItem orderItem, boolean isOneItem);
    }

    private String printDishOption(List<Option> optionList) {
        StringBuffer sb = new StringBuffer();
        if (optionList != null && optionList.size() > 0) {
                if (optionList != null && optionList.size() > 0) {
                    for (Option option : optionList) {
                        if (option.getPrice().compareTo(new BigDecimal("0")) == 0) {
                            sb.append(option.name + "、");
                        } else {
                            sb.append(option.name + "(" + option.getPrice() + "元)、");
                        }
                    }
                }
            }
        return sb.toString();
    }
}
