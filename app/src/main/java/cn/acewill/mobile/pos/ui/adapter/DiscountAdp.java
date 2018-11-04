package cn.acewill.mobile.pos.ui.adapter;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.TextView;

import java.math.BigDecimal;

import cn.acewill.mobile.pos.R;
import cn.acewill.mobile.pos.base.adapter.BaseAdapter;
import cn.acewill.mobile.pos.config.MyApplication;
import cn.acewill.mobile.pos.model.dish.Dish;
import cn.acewill.mobile.pos.model.dish.DishDiscount;
import cn.acewill.mobile.pos.utils.ToolsUtils;


/**
 * Created by DHH on 2016/6/17.
 */
public class DiscountAdp<T> extends BaseAdapter {
    private int width;
    private int height;

    public DiscountAdp(Context context) {
        super(context);
        int sc_width = MyApplication.getInstance().getScreenWidth();
        width = sc_width / 3 + 10;
        height = width / 3 + 60;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final DishDiscount discount = (DishDiscount) getItem(position);
        final TextView textView = new TextView(context);

        textView.setTextSize(16);
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(0xff000000);
        AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(width, height);
        textView.setPadding(5, 5, 5, 5);
        textView.setLayoutParams(layoutParams);
        //        if(discount.isEnable())
        //        {
        BigDecimal discountPrice = new BigDecimal(discount.discountPrice + "").setScale(2, BigDecimal.ROUND_DOWN);
        BigDecimal tempMoney = new BigDecimal("0.00");
        if(discount.getDiscountType() == 1)
        {
            tempMoney = discount.getReductiMoney().negate();
        }
        else{
            tempMoney = discountPrice;
        }
        textView.setText(tempMoney + "￥/" + discount.name);
        if (current_select == position) {
            ToolsUtils.writeUserOperationRecords("选择了(" + tempMoney + "￥/" + discount.name + ")折扣");
            textView.setBackgroundResource(R.drawable.border_green);
        } else {
            textView.setBackgroundResource(R.drawable.selector_item_cooker_xb);
        }
        //        }
        //        else{
        //            MyApplication.getInstance().ShowToast("减免金额大于菜品金额!");
        //        }


        //        //原价
        //        if(position == 0)
        //        {
        //
        //            textView.setText(discount.getDiscountStr()+"/￥"+new BigDecimal(dish.getDishCost().multiply(new BigDecimal(1+"")).intValue()));
        //        }
        //        //5折
        //         if(position == 1)
        //        {
        //            textView.setText(discount.getDiscountStr()+"/￥"+new BigDecimal(dish.getDishCost().multiply(new BigDecimal(0.5+"")).intValue()));
        //        }
        //        //8折
        //         if(position == 2)
        //        {
        //            textView.setText(discount.getDiscountStr()+"/￥"+new BigDecimal(dish.getDishCost().multiply(new BigDecimal(0.8+"")).intValue()));
        //        }
        //
        //        if (option.getPrice().compareTo(new BigDecimal("0")) == 0) {
        //            textView.setText(option.name);
        //        } else {
        //            textView.setText(option.name + " 加" + option.getPrice() + "元");
        //        }
        //        textView.setTextSize(16);
        //        textView.setId(option.getId());
        //        textView.setGravity(Gravity.CENTER);
        //        textView.setTextColor(0xff000000);
        //        AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(
        //                width, height);
        //        textView.setPadding(5, 5, 5, 5);
        //        textView.setLayoutParams(layoutParams);
        //        textView.setBackgroundResource(R.drawable.selector_item_cooker_xb);
        //
        //        if (option.required) {
        //            option.setSelect(true);
        //            Cart.addOption(option);
        //            textView.setBackgroundResource(R.drawable.border_green);
        //        } else {
        //            option.setSelect(false);
        ////            Cart.deleteOption(option);
        //            textView.setBackgroundResource(R.drawable.border_gray1_xb);
        //        }
        //        if(isSelect)
        //        {
        //            if(optionList != null && optionList.size() >0)
        //            {
        //                for (Option optionSelect :optionList)
        //                {
        //                    if(optionSelect.getId() == option.getId())
        //                    {
        //                        option.setSelect(true);
        //                        Cart.addOption(option);
        //                        textView.setBackgroundResource(R.drawable.border_green);
        //                    }
        //                }
        //            }
        //        }
        //
        //        textView.setOnClickListener(new View.OnClickListener() {
        //
        //            @Override
        //            public void onClick(View v) {
        //                isSelect = false;
        //                if (!option.required) {
        //                    if (option.getSelect()) {
        //                        option.setSelect(false);
        //                        Cart.deleteOption(option);
        //                        textView.setBackgroundResource(R.drawable.border_gray1_xb);
        //                    } else {
        //                        option.setSelect(true);
        //                        Cart.addOption(option);
        //                        textView.setBackgroundResource(R.drawable.border_green);
        //                    }
        //                }
        //            }
        //        });

        return textView;
    }

    private Dish dish;
    private int current_select = 0;

    public void setDish(Dish dish) {
        if (dish != null) {
            this.dish = dish;
        }
    }

    public void setCurrent_select(int current_select) {
        this.current_select = current_select;
    }

    //    private ArrayList<Discount> discountsList = new ArrayList<>();
    //    public void setDisCountList(ArrayList<Discount> discountsList)
    //    {
    //        if(discountsList != null && discountsList.size() >0)
    //        {
    //            this.discountsList = discountsList;
    //        }
    //    }

}
