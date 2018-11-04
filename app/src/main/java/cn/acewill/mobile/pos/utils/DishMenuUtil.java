package cn.acewill.mobile.pos.utils;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.TextView;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import cn.acewill.mobile.pos.R;
import cn.acewill.mobile.pos.common.DishOptionController;
import cn.acewill.mobile.pos.common.MarketDataController;
import cn.acewill.mobile.pos.common.StoreInfor;
import cn.acewill.mobile.pos.config.MyApplication;
import cn.acewill.mobile.pos.model.dish.Cart;
import cn.acewill.mobile.pos.model.dish.Dish;
import cn.acewill.mobile.pos.model.dish.DishDiscount;
import cn.acewill.mobile.pos.ui.adapter.DiscountAdp;
import cn.acewill.mobile.pos.ui.adapter.OptionCategoryPackageAdp;
import cn.acewill.mobile.pos.widget.ScrolGridView;
import cn.acewill.mobile.pos.widget.ScrolListView;

/**
 * Created by DHH on 2017/12/12.
 */

public class DishMenuUtil {

    public static Dialog setDishPackageSkuDialog(Context context, final Dish dish, final Dish.Package packagItem, final int position) {
        final Dialog dialog = DialogUtil.createDialog(context, R.layout.dialog_dish_sku, 9, 8);
        TextView dialog_ok = (TextView) dialog.findViewById(R.id.dialog_ok);
        TextView dialog_cancle = (TextView) dialog.findViewById(R.id.dialog_cancle);
        TextView retreat_title = (TextView) dialog.findViewById(R.id.retreat_title);
        final ScrolListView lv_option = (ScrolListView) dialog.findViewById(R.id.lv_option);

        retreat_title.setText(packagItem.getDishName());
        if (packagItem.optionCategoryList != null && packagItem.optionCategoryList.size() > 0) {
            OptionCategoryPackageAdp optionCategoryAdp = new OptionCategoryPackageAdp(context, dish, packagItem);
            optionCategoryAdp.setOptionList(packagItem.optionList);
            optionCategoryAdp.setData(packagItem.optionCategoryList);
            lv_option.setAdapter(optionCategoryAdp);
        }
        dialog_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToolsUtils.writeUserOperationRecords("菜品SKU取消按钮");
                dialog.dismiss();
            }
        });

        dialog_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToolsUtils.writeUserOperationRecords("菜品SKU确定按钮");
                //遍历选中的定制项
                if (!DishOptionController.checkSelectOption(dish, packagItem)) {
                    return;
                }
                packagItem.optionList = DishOptionController.getDishOption(dish, packagItem);
                Cart.getInstance().selectDishPackage(position, packagItem);
                dialog.dismiss();
            }
        });
        return dialog;
    }

    /**
     * 设置菜品折扣
     *
     * @param context
     * @param dish
     * @param position
     * @return
     */
    public static Dialog setDishDisCountDialog(Context context, final Dish dish, final int position) {
        final Dialog dialog = DialogUtil.createDialog(context, R.layout.dialog_dish_discount, 9, 8);
        TextView dialog_ok = (TextView) dialog.findViewById(R.id.dialog_ok);
        TextView dialog_cancle = (TextView) dialog.findViewById(R.id.dialog_cancle);
        TextView retreat_title = (TextView) dialog.findViewById(R.id.retreat_title);
        final ScrolGridView gv_discount = (ScrolGridView) dialog.findViewById(R.id.gv_discount);

        retreat_title.setText(dish.getDishName());
        final int[] current_selectDish = {dish.current_select};
        final List<DishDiscount> dishDisCountList = MarketDataController.getDishDiscount(dish, StoreInfor.marketingActivities);
        ArrayList<DishDiscount> newDishDiscountList = new ArrayList<>();
        dish.setDishDiscount(dishDisCountList);
        if (dish.dishDiscount != null && dish.dishDiscount.size() > 0) {
            final DiscountAdp discountAdp = new DiscountAdp(context);
            if (newDishDiscountList != null && newDishDiscountList.size() > 0) {
                newDishDiscountList.clear();
            }
            ArrayList<DishDiscount> oldDishDiscount = dish.getDishDiscount();
            DishDiscount tempDiscount = oldDishDiscount.get(0);

            if (dish.isSelectDisCount == false) {
                String price = (dish.getDishPrice().setScale(2, BigDecimal.ROUND_DOWN)).toString();
                DishDiscount dishDiscount = new DishDiscount(tempDiscount, price);
                newDishDiscountList.add(dishDiscount);
                for (DishDiscount count : oldDishDiscount) {
                    newDishDiscountList.add(count);
                }
            } else {
                newDishDiscountList = oldDishDiscount;
            }
            discountAdp.setCurrent_select(dish.current_select);
            discountAdp.setData(newDishDiscountList);
            gv_discount.setAdapter(discountAdp);
            dish.dishDiscount = newDishDiscountList;
            //            dish.isSelectDisCount = true;
            gv_discount.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    DishDiscount discount = (DishDiscount) discountAdp.getItem(position);
                    if (discount != null) {
                        if (discount.isEnable()) {
                            current_selectDish[0] = position;
                            discountAdp.setCurrent_select(position);
                            discountAdp.notifyDataSetChanged();
                        } else {
                            MyApplication.getInstance().ShowToast("减免金额大于菜品金额!");
                        }
                    }
                }
            });
        }
        dialog_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToolsUtils.writeUserOperationRecords("菜品折扣取消按钮");
                dialog.dismiss();
            }
        });

        final ArrayList<DishDiscount> finalNewDishDiscountList = newDishDiscountList;
        dialog_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToolsUtils.writeUserOperationRecords("菜品折扣确定按钮");
                Cart.getInstance().selectCount(position, dish.quantity, -10, current_selectDish[0], -10, finalNewDishDiscountList);
                dialog.dismiss();
            }
        });
        return dialog;
    }


    /**
     * 添加菜品备注
     *
     * @param context
     * @param dish
     * @param position
     * @return
     */
    public static Dialog setDishNote(Context context, final Dish dish, final int position) {
        final Dialog dialog = DialogUtil.createDialog(context, R.layout.dialog_dish_add_note, 9, 4);
        boolean isHaveNote = false; //菜品是否已经存在备注
        TextView dialog_ok = (TextView) dialog.findViewById(R.id.dialog_ok);
        TextView dialog_cancle = (TextView) dialog.findViewById(R.id.dialog_cancle);
        TextView retreat_title = (TextView) dialog.findViewById(R.id.retreat_title);
        final EditText ed_dishNote = (EditText) dialog.findViewById(R.id.ed_dishNote);

        retreat_title.setText(dish.getDishName());
        if(!TextUtils.isEmpty(dish.getComment()))
        {
            ed_dishNote.setText(dish.getComment());
            isHaveNote = true;
        }

        dialog_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToolsUtils.writeUserOperationRecords("菜品备注取消按钮");
                dialog.dismiss();
            }
        });

        final boolean finalIsHaveNote = isHaveNote;
        dialog_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToolsUtils.writeUserOperationRecords("菜品备注确定按钮");

                String note = ed_dishNote.getText().toString().trim();
                if (TextUtils.isEmpty(note) && !finalIsHaveNote) {
                    MyApplication.getInstance().ShowToast(ToolsUtils.returnXMLStr("standby_dish_comment_hine")+  "!");
                    return;
                }
                dish.setComment(note);
                Cart.notifyContentChange();
                dialog.dismiss();
            }
        });
        return dialog;
    }

}
