package cn.acewill.mobile.pos.ui.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.greenrobot.eventbus.EventBus;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import cn.acewill.mobile.pos.R;
import cn.acewill.mobile.pos.base.adapter.BaseAdapter;
import cn.acewill.mobile.pos.common.DishOptionController;
import cn.acewill.mobile.pos.model.dish.Cart;
import cn.acewill.mobile.pos.model.dish.Dish;
import cn.acewill.mobile.pos.model.dish.DishCount;
import cn.acewill.mobile.pos.model.dish.OptionCategory;
import cn.acewill.mobile.pos.model.event.PosEvent;
import cn.acewill.mobile.pos.service.PosInfo;
import cn.acewill.mobile.pos.ui.activity.PackagerAty;
import cn.acewill.mobile.pos.utils.Constant;
import cn.acewill.mobile.pos.utils.TimeUtil;
import cn.acewill.mobile.pos.utils.ToolsUtils;
import cn.acewill.mobile.pos.utils.UserAction;
import cn.acewill.mobile.pos.widget.ScrolListView;


/**
 * 菜品列表
 * Created by DHH on 2016/6/17.
 */
public class DishAdp<T> extends BaseAdapter {
    private List<Dish> datas;
    private Context context;
    private PosInfo posInfo;
    private int[] clickPoint = new int[2];

    public DishAdp(Context context) {
        super(context);
        this.context = context;
        posInfo = PosInfo.getInstance();
    }

    public void setDataInfo(List<Dish> dataList) {
        //        if (dataList != null) {
        this.datas = dataList;
        this.notifyDataSetChanged();
        //        }
    }


    @Override
    public Dish getItem(int position) {
        return datas != null ? datas.get(position) : null;
    }

    @Override
    public int getCount() {
        if (datas == null) {
            EventBus.getDefault().post(new PosEvent(Constant.EventState.CURRENT_TIME_DISH_NULL));
        }
        return datas != null ? datas.size() : 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        final Dish dish = datas.get(position);
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_dishs, null);
            holder.lin_bg = (LinearLayout) convertView.findViewById(R.id.lin_bg);
            holder.dish_img = (ImageView) convertView.findViewById(R.id.dish_img);
            holder.dish_name = (TextView) convertView.findViewById(R.id.dish_name);
            holder.dish_dishCount = (TextView) convertView.findViewById(R.id.dish_dishCount);
            holder.dish_price = (TextView) convertView.findViewById(R.id.dish_price);
            holder.dish_memberprice = (TextView) convertView.findViewById(R.id.dish_memberprice);
            holder.dish_reduce_ll = (LinearLayout) convertView.findViewById(R.id.dish_reduce_ll);
            holder.dish_reduce = (ImageView) convertView.findViewById(R.id.dish_reduce);
            holder.dish_select_count = (TextView) convertView.findViewById(R.id.dish_select_count);
            holder.dish_plus = (ImageView) convertView.findViewById(R.id.dish_plus);
            holder.item_view = (LinearLayout) convertView.findViewById(R.id.item_view);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        holder.dish_name.setText(dish.getDishName());
        String money = String.format("%.2f ", dish.getPrice());
        String memberPrice = String.format("%.2f ", dish.getMemberPrice());
        String dishUnit = dish.getDishUnit();
        dishUnit = TextUtils.isEmpty(dishUnit) ? "份" : dishUnit;
        holder.dish_price.setText(money + "/" + dishUnit);
        holder.dish_memberprice.setText(memberPrice + "/" + dishUnit);
        Glide.with(context).load(dish.getImageName())
                .placeholder(R.drawable.test)
                .error(R.drawable.test)
                .into(holder.dish_img);
            if (dishCountList != null && dishCountList.size() > 0) {
                for (DishCount dishCount : dishCountList) {
                    if (dishCount.dishid == dish.getDishId()) {
                        if (dishCount.count <= 0) {
                            holder.lin_bg.setEnabled(false);
                            holder.dish_dishCount.setVisibility(View.VISIBLE);
                            holder.dish_dishCount.setText("已估清");
                            holder.dish_plus.setVisibility(View.GONE);
                            holder.dish_dishCount.setTextColor(ContextCompat.getColor(context, R.color.bbutton_danger_disabled_edge));
                        } else {
                            holder.lin_bg.setEnabled(true);
                            holder.dish_plus.setVisibility(View.VISIBLE);
                            if (dishCount.count < 20) {
                                holder.dish_dishCount.setVisibility(View.VISIBLE);
                                holder.dish_dishCount.setText("剩余:" + dishCount.count);
                                holder.dish_dishCount.setTextColor(ContextCompat.getColor(context, R.color.login_gray));
                            } else {
                                holder.dish_dishCount.setVisibility(View.INVISIBLE);
                            }
                        }
                        break;
                    }
                }
            } else {
                if (dish.dishCount <= 0) {
                    holder.lin_bg.setEnabled(false);
                    holder.dish_dishCount.setVisibility(View.VISIBLE);
                    holder.dish_dishCount.setText("已估清");
                    holder.dish_plus.setVisibility(View.GONE);
                    holder.dish_dishCount.setTextColor(ContextCompat.getColor(context, R.color.bbutton_danger_disabled_edge));
                } else {
                    holder.lin_bg.setEnabled(true);
                    holder.dish_plus.setVisibility(View.VISIBLE);
                    if (dish.dishCount < 20) {
                        holder.dish_dishCount.setVisibility(View.VISIBLE);
                        holder.dish_dishCount.setText("剩余:" + dish.dishCount);
                        holder.dish_dishCount.setTextColor(ContextCompat.getColor(context, R.color.login_gray));
                    } else {
                        holder.dish_dishCount.setVisibility(View.INVISIBLE);
                    }
                }
            }

        int num = Cart.getInstance().findDishByDid(dish.getDishId());
        if (num > 0) {
            holder.dish_select_count.setText(num + "");
            holder.dish_plus.setVisibility(View.VISIBLE);
            holder.dish_reduce_ll.setVisibility(View.VISIBLE);
        } else {
            holder.dish_select_count.setText("");
            holder.dish_reduce_ll.setVisibility(View.GONE);
            holder.dish_plus.setVisibility(View.GONE);
        }
        posInfo.setClickDish(true);

        ////        int num = dish.getQuantity();
        //        if (dish.isPackage() || dish.haveCooker() || dish.haveTaste() || dish.haveOptionCategory()) {
        //            if (num > 0) {
        //                holder.dish_select_count.setText(num + "");
        //            } else {
        //                holder.dish_select_count.setText("");
        //            }
        //            holder.dish_reduce_ll.setVisibility(View.GONE);
        //            holder.dish_plus.setVisibility(View.GONE);
        //        } else {
        //            if (num > 0) {
        //                holder.dish_select_count.setText(num + "");
        //                holder.dish_plus.setVisibility(View.VISIBLE);
        //                holder.dish_reduce_ll.setVisibility(View.VISIBLE);
        //            } else {
        //                holder.dish_select_count.setText("");
        //                holder.dish_plus.setVisibility(View.GONE);
        //                holder.dish_reduce_ll.setVisibility(View.GONE);
        //            }
        //        }


        //减一份
        holder.dish_reduce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int count = Cart.getInstance().reduceItem(dish);
                UserAction.log("减一份菜品:" + dish.getDishName() + "; count:" + count, context);
                if (count > 0) {
                    holder.dish_select_count.setText(count + "");
                    holder.dish_plus.setVisibility(View.VISIBLE);
                    holder.dish_reduce_ll.setVisibility(View.VISIBLE);
                } else {
                    holder.dish_select_count.setText("");
                    holder.dish_plus.setVisibility(View.GONE);
                    holder.dish_reduce_ll.setVisibility(View.GONE);
                }

            }
        });

        //加一份
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserAction.log("加一份菜品:" + dish.getDishName(), context);
                try {
                    holder.dish_plus.getLocationOnScreen(clickPoint);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                addDish(holder.item_view, dish);
//                if(dish.isPackage() || dish.haveCooker() || dish.haveTaste() || dish.haveOptionCategory() || dish.haveMarkList())
//                {
//                    addDish(holder.item_view, dish);
//                }
//                else{
//                    int count = Cart.getInstance().addItem(dish);
//                    if (count > 0) {
//                        holder.dish_select_count.setText(count + "");
//                        holder.dish_plus.setVisibility(View.VISIBLE);
//                        holder.dish_reduce_ll.setVisibility(View.VISIBLE);
//                    } else {
//                        holder.dish_select_count.setText("");
//                        holder.dish_plus.setVisibility(View.GONE);
//                        holder.dish_reduce_ll.setVisibility(View.GONE);
//                    }
//                }
            }
        });


        // 绑定tag
        holder.dish_name.setTag(position);
        //  绑定当前的item，也就是convertview
        holder.dish_price.setTag(convertView);

        holder.lin_bg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(posInfo.isClickDish())
                {
                    addDish(holder.item_view, dish);
                }
            }
        });
        return convertView;
    }

    class ViewHolder {
        ImageView dish_img;
        TextView dish_name;
        TextView dish_dishCount;
        TextView dish_price;
        TextView dish_memberprice;
        LinearLayout dish_reduce_ll;
        ImageView dish_reduce;
        TextView dish_select_count;
        ImageView dish_plus;
        LinearLayout item_view;
        LinearLayout lin_bg;
    }

    private List<DishCount> dishCountList = new ArrayList<>();

    public void setDishCount(List<DishCount> dishCountList) {
        if (dishCountList != null && dishCountList.size() > 0) {
            this.dishCountList = dishCountList;
            this.notifyDataSetChanged();
        }
    }

    /**
     * 获取点击位置坐标
     */
    public int[] getClickPoint() {
        return clickPoint;
    }


    public void addDish(LinearLayout countView, Dish mDishModel) {
        ToolsUtils.writeUserOperationRecords("点击了(" + mDishModel.getDishName() + ")菜品");
        try {
            countView.getLocationOnScreen(clickPoint);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Integer dishOptionId = TimeUtil.getStringData();
        mDishModel.setDishOptionId(dishOptionId);
        if (mDishModel.isPackage()) {
            if (mDishModel.isShowPackageItemsFlag()) {
                Intent intent = new Intent(context, PackagerAty.class);
                intent.putExtra("dish", (Serializable) mDishModel);
                context.startActivity(intent);
                return;
            } else {
                Cart.getInstance().addItem(mDishModel);
            }
        } else if (!mDishModel.isOptionRequired()) {
            Cart.getInstance().addItem(mDishModel);
        }
        else if(mDishModel.haveMarkList())
        {
            Cart.getInstance().addItem(mDishModel);
        }
        else {
//            Cart.getInstance().addItem(mDishModel);
            List<OptionCategory> optionCategoryList = mDishModel.getOptionCategoryList();
            int count = 0;
            for (OptionCategory optionCategory : optionCategoryList) {
                count += optionCategory.getMinimalOptions();
            }

            if (count == 0) {
                Cart.getInstance().addItem(mDishModel);
            } else {
                Dialog dishDialog = createDishDialog(mDishModel);
                if (dishDialog != null && !dishDialog.isShowing()) {
                    dishDialog.show();
                }
            }
        }
    }


    private Dialog dialog;
    private ScrolListView lv_option;
    private OptionCategoryAdp optionCategoryAdp;
    private TextView tv_dishName_title;
    private ImageView img_close;
    private EditText ed_note;
    private Button btn_cancle, btn_ok;

    public Dialog createDishDialog(final Dish mDishModel) {
        if (dialog != null && dialog.isShowing()) {
            return null;
        }
        dialog = new Dialog(context, R.style.loading_dialog);
        dialog.setContentView(R.layout.dialog_add_dish);
        Window dialogWindow = dialog.getWindow();
        WindowManager m = ((Activity) context).getWindowManager();
        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
        WindowManager.LayoutParams p = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        p.width = (int) (d.getWidth() * 0.9); // 高度设置为屏幕的0.5
        p.height = (int) (d.getHeight() * 0.8);
        dialogWindow.setAttributes(p);
        lv_option = (ScrolListView) dialog.findViewById(R.id.lv_option);
        ed_note = (EditText) dialog.findViewById(R.id.ed_note);
        btn_cancle = (Button) dialog.findViewById(R.id.btn_cancle);
        btn_ok = (Button) dialog.findViewById(R.id.btn_ok);
        tv_dishName_title = (TextView) dialog.findViewById(R.id.tv_dishName_title);
        img_close = (ImageView) dialog.findViewById(R.id.img_close);
        if (mDishModel.quantity == 0) {
            mDishModel.quantity = 1;
        } else {
        }
        if (mDishModel.haveOptionCategory()) {
            optionCategoryAdp = new OptionCategoryAdp(context, mDishModel);
            optionCategoryAdp.setData(mDishModel.optionCategoryList);
            lv_option.setAdapter(optionCategoryAdp);
        }
        img_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        tv_dishName_title.setText(mDishModel.getDishName());
        btn_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String note = ed_note.getText().toString().trim();
                if (!TextUtils.isEmpty(note)) {
                    mDishModel.comment = note;
                }
                //遍历选中的定制项
                if (!DishOptionController.checkSelectOption(mDishModel, null)) {
                    return;
                }
                mDishModel.optionList = DishOptionController.getDishOption(mDishModel, null);
                //遍历选中的定制项
                Cart.getInstance().addItem(mDishModel);
                dialog.cancel();
            }
        });
        return dialog;
    }

}
