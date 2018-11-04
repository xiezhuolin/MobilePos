package cn.acewill.mobile.pos.ui.adapter;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.TextView;

import java.math.BigDecimal;
import java.util.List;

import cn.acewill.mobile.pos.R;
import cn.acewill.mobile.pos.base.adapter.BaseAdapter;
import cn.acewill.mobile.pos.common.DishOptionController;
import cn.acewill.mobile.pos.config.MyApplication;
import cn.acewill.mobile.pos.model.dish.Dish;
import cn.acewill.mobile.pos.model.dish.Option;
import cn.acewill.mobile.pos.model.dish.OptionCategory;


/**
 * Created by DHH on 2016/6/17.
 */
public class OptionPackageAdp<T> extends BaseAdapter {
    private int width;
    private int height;

    private Dish dish;
    private Dish.Package packagItem;
    private List<OptionCategory> optionCategoryList;
    public OptionPackageAdp(Context context, Dish dish , Dish.Package packagItem,List<OptionCategory> optionCategoryList) {
        super(context);
        int sc_width = MyApplication.getInstance().getScreenWidth();
        width = sc_width / 4 + 10;
        height = width / 3 + 60;
        this.dish = dish;
        this.packagItem = packagItem;
        this.optionCategoryList = optionCategoryList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Option option = (Option) getItem(position);
        final TextView textView = new TextView(context);
        if (option.getPrice().compareTo(new BigDecimal("0")) == 0) {
            textView.setText(option.name);
        } else {
            textView.setText(option.name + "(" + option.getPrice() + "元)");
        }
        textView.setTextSize(16);
        textView.setId(option.getId());
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(0xff000000);
        AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(
                width, height);
        textView.setPadding(5, 5, 5, 5);
        textView.setLayoutParams(layoutParams);
        textView.setBackgroundResource(R.drawable.selector_item_cooker_xb);

        if (option.required) {
            option.setSelect(true);
            textView.setBackgroundResource(R.drawable.border_green);
            if(!DishOptionController.isDishHaveOption(dish,packagItem,option))
            {
                DishOptionController.addDishOption(dish,packagItem,optionCategoryList,option,true);
            }
        } else {
            option.setSelect(false);
            textView.setBackgroundResource(R.drawable.border_gray1_xb);
        }
        if(isSelect)
        {
            if(optionList != null && optionList.size() >0)
            {
                for (Option optionSelect :optionList)
                {
                    if(optionSelect.getId() == option.getId())
                    {
                        option.setSelect(true);
                        textView.setBackgroundResource(R.drawable.border_green);
                        if(!DishOptionController.isDishHaveOption(dish,packagItem,option))
                        {
                            DishOptionController.addDishOption(dish,packagItem,optionCategoryList,option,true);
                        }
                    }
                }
            }
        }

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isSelect = false;
                if (!option.required) {
                    if (option.getSelect()) {
                        if(DishOptionController.addDishOption(dish,packagItem,optionCategoryList,option,false))
                        {
                            option.setSelect(false);
                            textView.setBackgroundResource(R.drawable.border_gray1_xb);
                        }
                    } else {
                        if(DishOptionController.addDishOption(dish,packagItem,optionCategoryList,option,false))
                        {
                            option.setSelect(true);
                            textView.setBackgroundResource(R.drawable.border_green);
                        }
                    }
                }
            }
        });

        return textView;
    }
    private boolean isSelect = true;
    private List<Option> optionList;
    public void setOptionList(List<Option> optionList)
    {
        if(optionList != null && optionList.size() >0)
        {
            this.optionList = optionList;
        }
    }

}
