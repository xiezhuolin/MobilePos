package cn.acewill.mobile.pos.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.acewill.mobile.pos.R;
import cn.acewill.mobile.pos.common.PowerController;
import cn.acewill.mobile.pos.config.MyApplication;
import cn.acewill.mobile.pos.exception.PosServiceException;
import cn.acewill.mobile.pos.interfices.PermissionCallback;
import cn.acewill.mobile.pos.model.order.CardRecord;
import cn.acewill.mobile.pos.service.OrderService;
import cn.acewill.mobile.pos.service.ResultCallback;
import cn.acewill.mobile.pos.utils.ToolsUtils;


/**
 * 當日訂單
 * Created by aqw on 2016/8/16.
 */
public class CardRecordsAdapter extends RecyclerView.Adapter {

    public Context context;
    public List<CardRecord> dataList = new ArrayList<>();
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


    public CardRecordsAdapter(Context context, List<CardRecord> dataList, RefrushLisener refrushLisener){
        this.context = context;
        this.dataList = dataList;
        inflater = LayoutInflater.from(context);
        this.refrushLisener = refrushLisener;
    }

    public CardRecord getItem(int position) {
        if(dataList != null && dataList.size() >0)
        {
            if(dataList.get(position) != null)
            {
                return dataList.get(position);
            }
        }
        return null;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == TYPE_ITEM){
            View view = inflater.inflate(R.layout.lv_item_card_records_list,parent,false);
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
            final CardRecord cardRecord = dataList.get(position);
            itemViewHolder.tv_time.setText(cardRecord.getDate());
            itemViewHolder.tv_orderId.setText(cardRecord.getOrderid()+"");
            itemViewHolder.tv_customer.setText(cardRecord.getName());
            itemViewHolder.tv_contact.setText(cardRecord.getContact());
            itemViewHolder.tv_money.setText(cardRecord.getPrice().toString());

            itemViewHolder.tv_cancle_order.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //判断是否有退菜权限
                    PowerController.isLogicPower(context, PowerController.REFUND_DISH, new PermissionCallback() {
                        @Override
                        public void havePermission() {
                            chargeOffs(cardRecord.getOrderid());
                        }

                        @Override
                        public void withOutPermission() {

                        }
                    });

                }
            });
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

    /**
     * 挂单
     */
    private void chargeOffs(long orderId)
    {
        try {
            OrderService orderService = OrderService.getInstance();
            orderService.chargeOffs(orderId, false, new ResultCallback<Integer>() {
                @Override
                public void onResult(Integer result) {
                    if(result == 0)
                    {
                        refrushLisener.refrush();
                    }
                    else{
                        MyApplication.getInstance().ShowToast(ToolsUtils.returnXMLStr("cancle_order_error") +"!");
                        Log.e("销单失败", "");
                    }
                }

                @Override
                public void onError(PosServiceException e) {
                    MyApplication.getInstance().ShowToast(ToolsUtils.returnXMLStr("cancle_order_error") + e.getMessage()+ "!");
                    Log.e("销单失败", e.getMessage());
                }
            });
        } catch (PosServiceException e) {
            e.printStackTrace();
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
        private TextView tv_time;
        private TextView tv_orderId;
        private TextView tv_customer;
        private TextView tv_contact;
        private TextView tv_money;
        private TextView tv_cancle_order;

        public ItemViewHolder(View view){
            super(view);
            tv_time = (TextView) view.findViewById(R.id.tv_time);
            tv_customer = (TextView) view.findViewById(R.id.tv_customer);
            tv_contact = (TextView) view.findViewById(R.id.tv_contact);
            tv_money = (TextView) view.findViewById(R.id.tv_money);
            tv_orderId = (TextView)view.findViewById(R.id.tv_orderId);
            tv_cancle_order = (TextView)view.findViewById(R.id.cancle_order);
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
     * @param orders
     */
    public void setData(List<CardRecord> orders){
        if(orders!=null&&orders.size()>0){
            switch (load_type){
                case UP_LOAD_TYPE://上拉加载
                    dataList.addAll(orders);
                    break;
                case DOWN_LOAD_TYPE://下拉更新
                    this.dataList = orders;
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

    public interface RefrushLisener{
        public void refrush();
    }


}
