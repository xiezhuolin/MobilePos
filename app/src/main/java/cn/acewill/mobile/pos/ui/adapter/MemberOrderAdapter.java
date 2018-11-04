package cn.acewill.mobile.pos.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.acewill.mobile.pos.R;
import cn.acewill.mobile.pos.interfices.RefrushLisener;
import cn.acewill.mobile.pos.model.wsh.Account;
import cn.acewill.mobile.pos.utils.ToolsUtils;


/**
 * 當日訂單
 * Created by aqw on 2016/8/16.
 */
public class MemberOrderAdapter extends RecyclerView.Adapter {

    public Context context;
    public List<Account> dataList = new ArrayList<>();
    public LayoutInflater inflater;
    private RefrushLisener refrushLisener;


    public static final int UP_LOAD_TYPE = 0;//上拉加载
    public static final int DOWN_LOAD_TYPE = 1;//下拉刷新
    public int load_type = 0;//加载类型

    public static final int LOAD_MORE = 0;//加载更多
    public static final int LOADING = 1;//正在加载
    public static final int NO_MORE = 2;//没有数据了
    public int load_more_status = 0;

    private static final int TYPE_ITEM = 0;//普通Item
    private static final int TYPE_FOOTER = 1;//底部footview


    public MemberOrderAdapter(Context context, List<Account> memberList, RefrushLisener refrushLisener){
        this.context = context;
        this.dataList = memberList;
        inflater = LayoutInflater.from(context);
        this.refrushLisener = refrushLisener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == TYPE_ITEM){
            View view = inflater.inflate(R.layout.lv_item_member_order,parent,false);
            ItemViewHolder itemViewHolder = new ItemViewHolder(view);
            return itemViewHolder;
        }else if(viewType == TYPE_FOOTER){
            View foot_view = inflater.inflate(R.layout.foot_view,parent,false);
            FootViewHolder footViewHolder = new FootViewHolder(foot_view);
            return footViewHolder;
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if(holder instanceof ItemViewHolder){
            ItemViewHolder itemViewHolder = (ItemViewHolder)holder;
            Account account = dataList.get(position);
            itemViewHolder.card_type.setText(ToolsUtils.logicCardType(account));
            itemViewHolder.member_numbs.setText(account.getUno());
            itemViewHolder.card_holder.setText(account.getName());
            itemViewHolder.card_sex.setText(ToolsUtils.logicUserCardGender(account));
            itemViewHolder.card_open_time.setText(account.getRegistered());
            itemViewHolder.member_phone.setText(ToolsUtils.replacePhone(account.getPhone()));
            itemViewHolder.member_level.setText(account.getGradeName());

            holder.itemView.setTag(position);
        }else if(holder instanceof FootViewHolder){
            FootViewHolder footViewHolder = (FootViewHolder)holder;

            switch (load_more_status){
                case LOAD_MORE:
                    footViewHolder.load_icon.setVisibility(View.GONE);
                    footViewHolder.load_more_tv.setText(ToolsUtils.returnXMLStr("pull_up_to_load_more"));
                    break;
                case LOADING:
                    footViewHolder.load_icon.setVisibility(View.VISIBLE);
                    footViewHolder.load_more_tv.setText(ToolsUtils.returnXMLStr("loading"));
                    break;
                case NO_MORE:
                    footViewHolder.load_icon.setVisibility(View.GONE);
                    footViewHolder.load_more_tv.setText("");
                    break;
            }
        }

    }

    @Override
    public int getItemViewType(int position) {
        if(position + 1 == getItemCount()){
            return TYPE_FOOTER;
        }else {
            return TYPE_ITEM;
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size()+1;
    }

    class ItemViewHolder extends RecyclerView.ViewHolder{
        private TextView card_type;
        private TextView member_numbs;
        private TextView card_holder;
        private TextView card_sex;
        private TextView card_open_time;
        private TextView member_phone;
        private TextView member_level;

        public ItemViewHolder(View view){
            super(view);
            card_type = (TextView) view.findViewById(R.id.card_type);
            member_numbs = (TextView)view.findViewById(R.id.member_numbs);
            card_holder = (TextView)view.findViewById(R.id.card_holder);
            card_sex = (TextView)view.findViewById(R.id.card_sex);
            card_open_time = (TextView)view.findViewById(R.id.card_open_time);
            member_phone = (TextView)view.findViewById(R.id.member_phone);
            member_level = (TextView)view.findViewById(R.id.member_level);

        }
    }

    class FootViewHolder extends RecyclerView.ViewHolder{
        private TextView load_more_tv;
        private ProgressBar load_icon;

        public FootViewHolder(View itemView) {
            super(itemView);
            load_more_tv = (TextView)itemView.findViewById(R.id.load_more_tv);
            load_icon = (ProgressBar)itemView.findViewById(R.id.load_icon);
        }
    }

    /**
     * 更新数据
     * @param memberList
     */
    public void setData(List<Account> memberList){
        if(memberList!=null&&memberList.size()>0){
            switch (load_type){
                case UP_LOAD_TYPE://上拉加载
                    dataList.addAll(memberList);
                    break;
                case DOWN_LOAD_TYPE://下拉更新
                    this.dataList = memberList;
                    break;
            }
        }
        else
        {
            this.dataList.clear();
        }
        this.notifyDataSetChanged();
    }

    /**
     * status
     * 0:加载更多；1:加载中；2:没有数据了；3:上拉刷新
     * @param status
     */
    public void changeMoreStatus(int status){
        load_more_status = status;
        this.notifyDataSetChanged();
    }

    public void setLoadType(int type){
        load_type = type;
    }

}
