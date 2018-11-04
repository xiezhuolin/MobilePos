package cn.acewill.mobile.pos.ui.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import cn.acewill.mobile.pos.R;
import cn.acewill.mobile.pos.base.adapter.BaseAdapter;
import cn.acewill.mobile.pos.config.MyApplication;
import cn.acewill.mobile.pos.model.dish.Dish;
import cn.acewill.mobile.pos.model.dish.DishCount;
import cn.acewill.mobile.pos.model.event.PosEvent;
import cn.acewill.mobile.pos.utils.Constant;

import static cn.acewill.mobile.pos.R.id.lin_bg;


/**
 * 菜品列表
 * Created by DHH on 2016/6/17.
 */
public class DishCountAdp<T> extends BaseAdapter {
    private List<Dish> datas;
    private Context context;
    private int[] clickPoint = new int[2];

    public DishCountAdp(Context context) {
        super(context);
        this.context = context;
    }

    public void setDataInfo(List<Dish> dataList) {
        if (dataList != null) {
            this.datas = dataList;
            this.notifyDataSetChanged();
        }
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
            holder.dish_img = (ImageView) convertView.findViewById(R.id.dish_img);
            holder.dish_name = (TextView) convertView.findViewById(R.id.dish_name);
            holder.dish_dishCount = (TextView) convertView.findViewById(R.id.dish_dishCount);
            holder.dish_price = (TextView) convertView.findViewById(R.id.dish_price);
            holder.dish_reduce_ll = (LinearLayout) convertView.findViewById(R.id.dish_reduce_ll);
            holder.lin_bg = (LinearLayout) convertView.findViewById(lin_bg);
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
        String dishUnit = dish.getDishUnit();
        dishUnit = TextUtils.isEmpty(dishUnit) ? "份" : dishUnit;
        holder.dish_price.setText(money + "/" + dishUnit);


        if (dishCountList != null && dishCountList.size() > 0) {
            for (DishCount dishCount : dishCountList) {
                if (dishCount.dishid == dish.getDishId()) {
                    if (dishCount.count <= 0) {
                        holder.lin_bg.setEnabled(false);
                        holder.dish_dishCount.setVisibility(View.VISIBLE);
                        holder.dish_dishCount.setText("已估清");
                        holder.dish_dishCount.setTextColor(ContextCompat.getColor(context, R.color.bbutton_danger_disabled_edge));
                    } else {
                        holder.lin_bg.setEnabled(true);
                        holder.dish_dishCount.setText("剩余:" + dishCount.count);
                        holder.dish_dishCount.setTextColor(ContextCompat.getColor(context, R.color.login_gray));
                        holder.dish_dishCount.setVisibility(View.VISIBLE);
                    }
                    break;
                }
            }
        } else {
            if (dish.dishCount <= 0) {
                holder.lin_bg.setEnabled(false);
                holder.dish_dishCount.setTextColor(ContextCompat.getColor(context, R.color.bbutton_danger_disabled_edge));
                holder.dish_dishCount.setText("已估清");
                holder.dish_dishCount.setVisibility(View.VISIBLE);
            } else {
                holder.lin_bg.setEnabled(true);
                holder.dish_dishCount.setVisibility(View.VISIBLE);
                holder.dish_dishCount.setText("剩余:" + dish.dishCount);
                holder.dish_dishCount.setTextColor(ContextCompat.getColor(context, R.color.login_gray));
            }
        }


        // 绑定tag
        holder.dish_name.setTag(position);
        //  绑定当前的item，也就是convertview
        holder.dish_price.setTag(convertView);
        return convertView;
    }

    class ViewHolder {
        ImageView dish_img;
        TextView dish_name;
        TextView dish_dishCount;
        TextView dish_price;
        LinearLayout dish_reduce_ll;
        LinearLayout lin_bg;
        ImageView dish_reduce;
        TextView dish_select_count;
        ImageView dish_plus;
        LinearLayout item_view;
    }

    private List<DishCount> dishCountList = new ArrayList<>();

    public void setDishCount(List<DishCount> dishCountList) {
        if (dishCountList != null && dishCountList.size() > 0) {
            this.dishCountList = dishCountList;
            this.notifyDataSetChanged();
        }
    }

}
