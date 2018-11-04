package cn.acewill.mobile.pos.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.math.BigDecimal;
import java.util.List;

import cn.acewill.mobile.pos.R;
import cn.acewill.mobile.pos.base.adapter.BaseAdapter;
import cn.acewill.mobile.pos.model.dish.Dish;
import cn.acewill.mobile.pos.model.dish.Option;
import cn.acewill.mobile.pos.model.dish.OptionCategory;
import cn.acewill.mobile.pos.utils.DishMenuUtil;


/**
 * Created by DHH on 2016/6/17.
 */
public class DishPackageItemAdp<T> extends BaseAdapter {
    private Dish dish;
    public DishPackageItemAdp(Context context) {
        super(context);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        final Dish.Package dishPackage = (Dish.Package)getItem(position);
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.lv_dish_package_item, null);
            holder.rel_conver = (RelativeLayout) convertView.findViewById(R.id.rel_conver);
            holder.tv_dishName = (TextView) convertView.findViewById(R.id.tv_dishName);
            holder.tv_count = (TextView) convertView.findViewById(R.id.tv_count);
            holder.tv_money = (TextView) convertView.findViewById(R.id.tv_money);
            holder.tv_option = (TextView) convertView.findViewById(R.id.tv_option);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tv_dishName.setText((position + 1) + "." + dishPackage.getDishName());
        if(dishPackage.extraCost>0)
        {
            holder.tv_money.setVisibility(View.VISIBLE);
            holder.tv_money.setText("¥ " + dishPackage.extraCost);
        }
        else
        {
            holder.tv_money.setVisibility(View.GONE);
        }
        holder.tv_count.setText(dishPackage.quantity + "份");

        if (dishPackage.optionList != null && dishPackage.optionList.size() > 0) {
            holder.tv_option.setVisibility(View.VISIBLE);
            holder.tv_option.setText("定制项:" + optionTextShow(dishPackage));
        } else {
            holder.tv_option.setVisibility(View.GONE);
        }

        holder.rel_conver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<OptionCategory> optionCategoryList = dishPackage.getOptionCategoryList();
                if(optionCategoryList != null && optionCategoryList.size() >0)
                {
                    if(dish != null)
                    {
                        DishMenuUtil.setDishPackageSkuDialog(context,dish,dishPackage,position);
                    }
                }
            }
        });
        return convertView;
    }

    private String optionTextShow(Dish mDishModel) {
        StringBuffer sb = new StringBuffer();
        for (Option option : mDishModel.optionList) {
            if (option.getPrice().compareTo(new BigDecimal("0")) == 0) {
                sb.append(option.name + "、");
            } else {
                sb.append(option.name + " (" + option.getPrice() + ")、");
            }
        }
        return sb.toString();
    }
    public void setDish(Dish dish)
    {
        if(dish != null)
        {
            this.dish = dish;
        }
    }


    class ViewHolder {
        RelativeLayout rel_conver;
        TextView tv_dishName;
        TextView tv_count;
        TextView tv_money;
        TextView tv_option;
    }

}
