package cn.acewill.mobile.pos.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import cn.acewill.mobile.pos.R;
import cn.acewill.mobile.pos.base.adapter.BaseAdapter;
import cn.acewill.mobile.pos.config.MyApplication;
import cn.acewill.mobile.pos.model.dish.Dish;
import cn.acewill.mobile.pos.model.dish.Option;
import cn.acewill.mobile.pos.model.order.OrderItem;
import cn.acewill.mobile.pos.utils.Constant;
import cn.acewill.mobile.pos.utils.ToolsUtils;


/**
 * Created by DHH on 2016/6/17.
 */
public class RushItemAdp<T> extends BaseAdapter {
    private List<OrderItem> orderItemList;
    private int sectionsStyle;

    public RushItemAdp(Context context, int sectionsStyle) {
        super(context);
        this.sectionsStyle = sectionsStyle;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        final OrderItem item = (OrderItem) getItem(position);
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.lv_item_rush_dish, null);
            holder.tv_dishName = (TextView) convertView.findViewById(R.id.tv_dishName);
            holder.tv_count = (TextView) convertView.findViewById(R.id.tv_count);
            holder.tv_money = (TextView) convertView.findViewById(R.id.tv_money);
            holder.tv_info = (TextView) convertView.findViewById(R.id.tv_info);
            holder.btn_minus = (Button) convertView.findViewById(R.id.btn_minus);
            holder.btn_add = (Button) convertView.findViewById(R.id.btn_add);
            holder.img_select = (ImageView) convertView.findViewById(R.id.img_select);
            holder.rel_selectCount = (RelativeLayout) convertView.findViewById(R.id.rel_selectCount);
            holder.rel_convertView = (RelativeLayout) convertView.findViewById(R.id.rel_convertView);
            holder.tv_selectCount = (TextView) convertView.findViewById(R.id.tv_selectCount);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tv_dishName.setText((position + 1) + "." + item.getDishName());

        holder.tv_count.setText(item.quantity + "/份");

        String money = String.format("%.2f ", item.getCost());
        holder.tv_money.setText("¥ " + money);

        if (sectionsStyle == Constant.TABLE_REFUN) {
            holder.tv_money.setVisibility(View.GONE);
            holder.rel_selectCount.setVisibility(View.VISIBLE);
        } else if (sectionsStyle == Constant.TABLE_RUSH) {
            holder.tv_money.setVisibility(View.VISIBLE);
            holder.rel_selectCount.setVisibility(View.GONE);
        }

        if (item.subItemList != null && item.subItemList.size() > 0 || item.optionList != null && item.optionList.size() > 0) {
            int count = 1;
            holder.tv_info.setVisibility(View.VISIBLE);
            StringBuffer sb = new StringBuffer();
            if (item.subItemList != null && item.subItemList.size() > 0) {
                for (Dish.Package packge : item.subItemList) {
                    sb.append(count + "." + packge.getDishName() + "\n");
                    count++;
                }
            }
            if (item.optionList != null && item.optionList.size() > 0) {
                for (Option option : item.optionList) {
                    String name = "";
                    double price = option.getPrice().doubleValue();
                    if (price > 0) {
                        name = option.name + "(" + option.getPrice() + "元)";
                    } else {
                        name = option.getName();
                    }
                    sb.append(count + "." + name + "\n");
                    count++;
                }
            }
            holder.tv_info.setText(sb.toString());
        } else {
            holder.tv_info.setVisibility(View.GONE);
        }

        final int[] dishMaxRefund = {item.quantity};//最大退菜份数
        final int[] initRefund = {1};//初始退菜份数
        final ViewHolder finalHolder = holder;
        holder.tv_selectCount.setText(initRefund[0] + "");
        item.current_refund_select = initRefund[0];
        holder.btn_minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (initRefund[0] > 1) {
                    initRefund[0]--;
                    item.current_refund_select = initRefund[0];
                    finalHolder.tv_selectCount.setText(initRefund[0] + "");
                    ToolsUtils.writeUserOperationRecords("菜品(" + item.getDishName() + ")退菜份数-1");
                }
            }
        });

        holder.btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (initRefund[0] + 1 > dishMaxRefund[0]) {
                    MyApplication.getInstance().ShowToast("超出可选份额!");
                } else {
                    initRefund[0]++;
                    item.current_refund_select = initRefund[0];
                    finalHolder.tv_selectCount.setText(initRefund[0] + "");
                    ToolsUtils.writeUserOperationRecords("菜品(" + item.getDishName() + ")退菜份数+1");
                }
            }
        });
        if (item.isSelectItem) {
            holder.img_select.setBackgroundResource(R.drawable.ad_select);
        } else {
            holder.img_select.setBackgroundResource(R.drawable.ad_unselect);
        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (item != null) {
                    if (item.isSelectItem) {
                        item.isSelectItem = false;
                        finalHolder.img_select.setBackgroundResource(R.drawable.ad_unselect);
                    } else {
                        item.isSelectItem = true;
                        finalHolder.img_select.setBackgroundResource(R.drawable.ad_select);
                    }
                }
            }
        });
        return convertView;
    }

    public void setOrderItemList(List<OrderItem> checkDishCount) {
        if (checkDishCount != null && checkDishCount.size() > 0) {
            if (this.orderItemList != null && this.orderItemList.size() > 0) {
                this.orderItemList.clear();
            }
            this.orderItemList = checkDishCount;
            this.notifyDataSetChanged();
        }
    }


    class ViewHolder {
        TextView tv_dishName;
        TextView tv_count;
        TextView tv_money;
        TextView tv_info;
        Button btn_minus;
        Button btn_add;
        ImageView img_select;
        RelativeLayout rel_selectCount;
        TextView tv_selectCount;
        RelativeLayout rel_convertView;
    }

}
