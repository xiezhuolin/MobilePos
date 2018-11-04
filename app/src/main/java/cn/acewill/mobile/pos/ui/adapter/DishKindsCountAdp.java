package cn.acewill.mobile.pos.ui.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.acewill.mobile.pos.R;
import cn.acewill.mobile.pos.base.adapter.BaseAdapter;
import cn.acewill.mobile.pos.model.dish.DishType;
import cn.acewill.mobile.pos.utils.ToolsUtils;


/**
 * 菜品分类
 * Created by DHH on 2016/6/17.
 */
public class DishKindsCountAdp<T> extends BaseAdapter {
    private Context context;
    public List<T> dataList;
    private static int position = 0;//初始化点击分类下标
    //    private static int last = -1;
    private int positionPage = 0;
    private int VIEW_COUNT = 8;
    public Resources res;
    private SparseArray<Integer> order_dish_mp = new SparseArray<>();
    private ArrayList<HashMap<String, DishType>> listItem = new ArrayList<>();
    private int isSelect = -1;
    private int isClick = -1;
    private boolean isInit = true;
    private int widch;
    public DishKindsCountAdp(Context context) {
        super(context);
        this.context = context;
        res = context.getResources();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        final DishType dishType = (DishType) getItem(position);
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_dish_kinds, null);
            holder.tv_dishKinds = (TextView) convertView.findViewById(R.id.tv_dishKinds);
            holder.rel_count = (RelativeLayout) convertView.findViewById(R.id.rel_count);
            holder.dish_kind_line = (View) convertView.findViewById(R.id.dish_kind_line);
            holder.ll_dish_kind =  convertView.findViewById(R.id.ll_dish_kind);

            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tv_dishKinds.setText(dishType.getName());
        if(this.position == position)
        {
            holder.tv_dishKinds.setTextColor(res.getColor(R.color.white));
            holder.tv_dishKinds.setTextSize(22);
            holder.rel_count.setBackgroundColor(ToolsUtils.getStrColor2Int(dishType.getKindColor()));
            holder.dish_kind_line.setVisibility(View.GONE);
        }
        else{
            holder.tv_dishKinds.setTextColor(res.getColor(R.color.black));
            holder.tv_dishKinds.setTextSize(18);
            holder.rel_count.setBackgroundColor(ToolsUtils.getStrColor2Int("ffffff"));
            holder.dish_kind_line.setVisibility(View.VISIBLE);
        }
        return convertView;
    }

    class ViewHolder {
        TextView tv_dishKinds;
        RelativeLayout rel_count;
        View dish_kind_line;
        View ll_dish_kind;
    }

    public void setSelect(int position) {
        if (this.position != position) {
            this.position = position;
            notifyDataSetChanged();
        }
    }


    public void setMap(SparseArray<Integer>  order_dish_mp) {
        this.order_dish_mp = order_dish_mp;
        this.notifyDataSetChanged();
    }

}
