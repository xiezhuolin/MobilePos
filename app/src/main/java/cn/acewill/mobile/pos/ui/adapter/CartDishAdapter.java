package cn.acewill.mobile.pos.ui.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.math.BigDecimal;
import java.util.List;

import cn.acewill.mobile.pos.R;
import cn.acewill.mobile.pos.base.adapter.BaseAdapter;
import cn.acewill.mobile.pos.model.dish.Cart;
import cn.acewill.mobile.pos.model.dish.Dish;
import cn.acewill.mobile.pos.model.dish.Option;
import cn.acewill.mobile.pos.utils.DishMenuUtil;
import cn.acewill.mobile.pos.utils.ToolsUtils;
import cn.acewill.mobile.pos.utils.UserAction;
import cn.acewill.mobile.pos.widget.ScrolListView;

/**
 * 购物车列表
 * Created by aqw on 2016/8/24.
 */
public class CartDishAdapter<T> extends BaseAdapter {
    private List<Dish> datas;
    private int[] clickPoint = new int[2];

    public CartDishAdapter(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    public Dish getItem(int position) {
        return datas != null ? datas.get(position) : null;
    }


    @Override
    public int getCount() {
        return datas != null ? datas.size() : 0;
    }

    public void setDataInfo(List<Dish> dataList) {
        //        if (dataList != null) {
        this.datas = dataList;
        this.notifyDataSetChanged();
        //        }
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        final Dish dish = datas.get(position);
//        final Dish dish = (Dish) getItem(position);
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_cart_dish, null);
            holder.lv_dish_package_item = (ScrolListView)convertView.findViewById(R.id.lv_dish_package_item);
            holder.dish_name = (TextView) convertView.findViewById(R.id.dish_name);
            holder.dish_line = (View) convertView.findViewById(R.id.dish_line);
            holder.dish_money = (TextView) convertView.findViewById(R.id.dish_money);
            holder.tv_note = (TextView) convertView.findViewById(R.id.tv_note);
            holder.activi_money = (TextView) convertView.findViewById(R.id.activi_money);
            holder.dish_reduce = (ImageView) convertView.findViewById(R.id.dish_reduce);
            holder.dish_select_count = (TextView) convertView.findViewById(R.id.dish_select_count);
            holder.dish_plus = (ImageView) convertView.findViewById(R.id.dish_plus);
            holder.meal_type = (ImageView) convertView.findViewById(R.id.meal_type);
            holder.tv_disCount = (TextView) convertView.findViewById(R.id.tv_disCount);
            holder.tv_option = (TextView) convertView.findViewById(R.id.tv_option);
            holder.lin_convertView = (LinearLayout) convertView.findViewById(R.id.lin_convertView);
            holder.lin_note = (LinearLayout) convertView.findViewById(R.id.lin_note);
            holder.rel_meal = (RelativeLayout) convertView.findViewById(R.id.rel_meal);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        //套餐子项
        if (dish.subItemList != null && dish.subItemList.size() > 0) {
            holder.rel_meal.setVisibility(View.VISIBLE);
            DishPackageItemAdp dishPackageItemAdp = new DishPackageItemAdp(context);
            dishPackageItemAdp.setDish(dish);
            dishPackageItemAdp.setData(dish.subItemList);
            holder.lv_dish_package_item.setAdapter(dishPackageItemAdp);
        } else {
            holder.lv_dish_package_item.setVisibility(View.GONE);
            holder.rel_meal.setVisibility(View.GONE);
        }
        if(dish.isMealShow())
        {
            holder.lv_dish_package_item.setVisibility(View.VISIBLE);
            holder.meal_type.setImageResource(R.drawable.reduce);
        }
        else{
            holder.lv_dish_package_item.setVisibility(View.GONE);
            if(dish.subItemList != null && dish.subItemList.size() > 0)
            {
                holder.meal_type.setImageResource(R.drawable.icon_add_selector);
            }
        }

        //最后一行隐藏下划线
        if(position == getCount()-1){
            holder.dish_line.setVisibility(View.GONE);
        }else {
            holder.dish_line.setVisibility(View.VISIBLE);
        }
        holder.dish_name.setText(dish.getDishName());

        BigDecimal price = (dish.getDishRealCost()).multiply(new BigDecimal(dish.quantity)).setScale(3, BigDecimal.ROUND_DOWN);
        String money = String.format("%.2f ", price);
        holder.dish_money.setText("￥"+money);
        holder.activi_money.setText("￥"+ToolsUtils.returnXMLStr("price_prefer")+": "+dish.activeMoney().setScale(2, BigDecimal.ROUND_DOWN).toString());
        holder.dish_select_count.setText(dish.quantity+"");

        holder.dish_reduce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserAction.log("购物车 减菜："+dish.getDishName(),context);
                Cart.getInstance().reduceItem(position);
            }
        });

        holder.dish_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    holder.dish_plus.getLocationOnScreen(clickPoint);
                }catch (Exception e){
                    e.printStackTrace();
                }
                UserAction.log("购物车 加菜："+dish.getDishName(),context);

                Cart.getInstance().addItem(position);
            }
        });

        holder.rel_meal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(dish != null)
                {
                    if(dish.isMealShow())
                    {
                        dish.setMealShow(false);
                    }
                    else{
                        dish.setMealShow(true);
                    }
                    notifyDataSetChanged();
                }
            }
        });

        if (dish.optionList != null && dish.optionList.size() > 0) {
            holder.tv_option.setVisibility(View.VISIBLE);
            holder.tv_option.setText(optionTextShow(dish));
        } else {
            holder.tv_option.setVisibility(View.GONE);
        }
        if (dish.getMarketList() != null && dish.getMarketList().size() > 0 || dish.getTempMarketList() != null && dish.getTempMarketList().size() > 0) {
            holder.tv_disCount.setVisibility(View.VISIBLE);
            holder.tv_disCount.setText(ToolsUtils.getDisCountStr(dish.getMarketList(), dish.getTempMarketList(),dish));
        } else {
            holder.tv_disCount.setVisibility(View.GONE);
        }
        if(!TextUtils.isEmpty(dish.getComment()))
        {
            holder.tv_note.setVisibility(View.VISIBLE);
            holder.lin_note.setVisibility(View.GONE);
            holder.tv_note.setText(ToolsUtils.returnXMLStr("sth_note2")+": "+dish.getComment());
        }
        else{
            holder.tv_note.setVisibility(View.GONE);
            holder.lin_note.setVisibility(View.VISIBLE);
        }

        holder.lin_convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Dish dish = (Dish) getItem(position);
                if (dish != null) {
                    DishMenuUtil.setDishDisCountDialog(context, dish, position);
                }
            }
        });

        holder.lin_note.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dish != null) {
                    DishMenuUtil.setDishNote(context, dish, position);
                }
            }
        });
        holder.tv_note.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dish != null) {
                    DishMenuUtil.setDishNote(context, dish, position);
                }
            }
        });

        return convertView;
    }


    private String optionTextShow(Dish mDishModel) {
        StringBuffer sb = new StringBuffer();
        for (Option option : mDishModel.optionList) {
            if (option.getPrice().compareTo(new BigDecimal("0")) == 0) {
                sb.append("+ " +option.name + "、");
            } else {
                sb.append("+ " +option.name + " (" + option.getPrice() + ")、");
            }
        }
        return sb.toString();
    }




    class ViewHolder {
        ScrolListView lv_dish_package_item;
        TextView dish_name;
        TextView dish_money;
        ImageView dish_reduce;
        TextView dish_select_count;
        TextView activi_money;
        TextView tv_note;
        ImageView dish_plus;
        ImageView meal_type;
        View dish_line;
        TextView tv_option;
        TextView tv_disCount;
        LinearLayout lin_convertView;
        LinearLayout lin_note;
        RelativeLayout rel_meal;
    }

    /**
     * 获取点击位置坐标
     */
    public int[] getClickPoint(){
        return clickPoint;
    }
}
