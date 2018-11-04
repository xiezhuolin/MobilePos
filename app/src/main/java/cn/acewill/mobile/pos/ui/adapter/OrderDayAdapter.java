package cn.acewill.mobile.pos.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import cn.acewill.mobile.pos.R;
import cn.acewill.mobile.pos.common.StoreInfor;
import cn.acewill.mobile.pos.model.order.Order;
import cn.acewill.mobile.pos.ui.activity.OrderInfoAty;
import cn.acewill.mobile.pos.utils.TimeUtil;
import cn.acewill.mobile.pos.utils.ToolsUtils;
import cn.acewill.mobile.pos.utils.UserAction;


/**
 * 當日訂單
 * Created by aqw on 2016/8/16.
 */
public class OrderDayAdapter extends RecyclerView.Adapter {

    private boolean netOrder;
    public Context context;
    public List<Order> dataList ;
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


    public OrderDayAdapter(Context context, List<Order> dataList, RefrushLisener refrushLisener, boolean netOrder){
        this.context = context;
        this.dataList = dataList;
        inflater = LayoutInflater.from(context);
        this.refrushLisener = refrushLisener;
        this.netOrder = netOrder;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == TYPE_ITEM){
            View view = inflater.inflate(R.layout.lv_item_order_day,parent,false);
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
            Order orderInfo = dataList.get(position);
            itemViewHolder.order_num.setText(orderInfo.getId()+"");
            String source = orderInfo.getSource();
            if (!TextUtils.isEmpty(source)) {
                if (source.equals("2")) {
                    source = "微信点餐";
                }
            } else {
                source = "未知来源";
            }
            String callNumber = "";
            if (ToolsUtils.logicIsTable()) {
                callNumber = (TextUtils.isEmpty(orderInfo.getTableNames()) ? "0" : orderInfo.getTableNames());
            } else {
                if (StoreInfor.cardNumberMode) {
                    callNumber = (TextUtils.isEmpty(orderInfo.getTableNames()) ? "0" : orderInfo.getTableNames());
                } else {
                    callNumber = (TextUtils.isEmpty(orderInfo.getCallNumber()) ? "0" : orderInfo.getCallNumber());
                }
            }
            itemViewHolder.order_tableName.setText(callNumber);



            itemViewHolder.creat_time.setText(TimeUtil.getStringTime(orderInfo.getCreatedAt()));
            String orderType = orderInfo.getOrderType();
            String typeStrs = "";
            if("EAT_IN".equals(orderType)){
                typeStrs = "堂食";
            }else if("TAKE_OUT".equals(orderType)){
                typeStrs = "外带";
            }else if("SALE_OUT".equals(orderType)){
                typeStrs = "外卖";
            }
            itemViewHolder.creat_type.setText(typeStrs);

            itemViewHolder.pay_btn.setText(orderInfo.getCost()+"￥");

//            final String paymentstate = orderInfo.getPaymentStatus().toString();
//            if("NOT_PAYED".equals(paymentstate)){//未支付：显示修改与支付
//                itemViewHolder.pay_btn.setText("未支付");
//                itemViewHolder.pay_btn.setTextColor(context.getResources().getColor(R.color.bbutton_danger_pressed));
//            }else if("PAYED".equals(paymentstate)){//已支付
//                itemViewHolder.pay_btn.setText("已支付");
//                itemViewHolder.pay_btn.setTextColor(context.getResources().getColor(R.color.bbutton_info_order_blue));
//            }else if("REFUND".equals(paymentstate)){//已退单
//                itemViewHolder.pay_btn.setText("已退单");
//                itemViewHolder.pay_btn.setTextColor(context.getResources().getColor(R.color.dishitem_count_font_gray));
//            }else if("FAILED_TO_QUERY_STATUS".equals(paymentstate)){//查询支付结果超时，手动点击下单
//                itemViewHolder.pay_btn.setText("支付超时");
//                itemViewHolder.pay_btn.setTextColor(context.getResources().getColor(R.color.bbutton_danger_pressed));
//            }else if("CANCELED".equals(paymentstate)){//订单取消
//                itemViewHolder.pay_btn.setText("订单取消");
//                itemViewHolder.pay_btn.setTextColor(context.getResources().getColor(R.color.dishitem_count_font_gray));
//            }else if("DUPLICATED".equals(paymentstate)){//重复订单
//                itemViewHolder.pay_btn.setText("重复订单");
//                itemViewHolder.pay_btn.setTextColor(context.getResources().getColor(R.color.dishitem_count_font_gray));
//            }

            itemViewHolder.order_ll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {//订单详情
                    UserAction.log("查看订单详情:"+dataList.get(position).getId(),context);
                    Intent intent = new Intent(context,OrderInfoAty.class);
                    intent.putExtra("orderId",dataList.get(position).getId()+"");
                    context.startActivity(intent);
//                    if(netOrder){
//                        intent.putExtra("order",orderInfo);
//                    }
//                    if(context instanceof Activity){
//                        ((Activity) context).startActivityForResult(intent,0);
//                    }else{
//                        context.startActivity(intent);
//                    }
                }
            });
            if(netOrder){
                itemViewHolder.ll_do.setVisibility(View.VISIBLE);
                itemViewHolder.dialog_cancle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
//                        rejectOrder(orderInfo);
                    }
                });
                itemViewHolder.dialog_ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
//                        confirmOrder(orderInfo);
                    }
                });
            }else{
                itemViewHolder.ll_do.setVisibility(View.GONE);
            }


            holder.itemView.setTag(position);
        }else if(holder instanceof FootViewHolder){
            FootViewHolder footViewHolder = (FootViewHolder)holder;

            switch (load_more_status){
                case LOAD_MORE:
                    footViewHolder.load_icon.setVisibility(View.GONE);
                    footViewHolder.load_more_tv.setText("上拉加载更多");
                    break;
                case LOADING:
                    footViewHolder.load_icon.setVisibility(View.VISIBLE);
                    footViewHolder.load_more_tv.setText("正在加载");
                    break;
                case NO_MORE:
                    footViewHolder.load_icon.setVisibility(View.GONE);
                    footViewHolder.load_more_tv.setText("");
                    break;
            }
        }

    }

//    private void confirmOrder(Order order){
//        final ProgressDialogF progressDialogF = new ProgressDialogF(context);
//        progressDialogF.setMessage("");
//        progressDialogF.show();
//        SyncNetOrderThread.checkDishCount(order, new SyncNetOrderThread.OrderCallback() {
//            @Override
//            public void onSuccess(Order order) {
//                Toast.makeText(context,"接单成功",Toast.LENGTH_SHORT).show();
//                dataList.remove(order);
//                notifyDataSetChanged();
//                progressDialogF.disLoading();
//
////                PrintLogic printLogic = new PrintLogic(context, DishDataController.handleOrder(order), CommonMsg.PRINT_NET_ORDER_KIT);
////                if(!TextUtils.isEmpty(order.customerAddress)){
////                    printLogic.printCheckOutOrKit();
////                }
////                printLogic.printKit();
//            }
//
//            @Override
//            public void onError(String errMsg) {
//                Toast.makeText(context,"接单失败:"+errMsg,Toast.LENGTH_SHORT).show();
//                progressDialogF.disLoading();
//            }
//        });
//    }
//
//    private void rejectOrder(Order order){
//        final ProgressDialogF progressDialogF = new ProgressDialogF(context);
//        progressDialogF.setMessage("");
//        progressDialogF.show();
//        SyncNetOrderThread.rejectOrder(order, "", new SyncNetOrderThread.OrderCallback() {
//            @Override
//            public void onSuccess(Order order) {
//                Toast.makeText(context,"拒单成功",Toast.LENGTH_SHORT).show();
//                dataList.remove(order);
//                notifyDataSetChanged();
//                progressDialogF.disLoading();
//            }
//
//            @Override
//            public void onError(String errMsg) {
//                Toast.makeText(context,"拒单失败:"+errMsg,Toast.LENGTH_SHORT).show();
//                progressDialogF.disLoading();
//            }
//        });
//    }

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
        private View dialog_ok;
        private View dialog_cancle;
        private View ll_do;
        private LinearLayout order_ll;
        private TextView order_num;
        private TextView order_tableName;
        private TextView creat_time;
        private TextView edit_btn;
        private TextView pay_btn;
        private TextView creat_type;

        public ItemViewHolder(View view){
            super(view);
            order_ll = (LinearLayout)view.findViewById(R.id.order_ll);
            order_num = (TextView) view.findViewById(R.id.order_num);
            order_tableName = (TextView) view.findViewById(R.id.order_tableName);
            creat_time = (TextView)view.findViewById(R.id.creat_time);
            edit_btn = (TextView)view.findViewById(R.id.edit_btn);
            pay_btn = (TextView)view.findViewById(R.id.pay_btn);
            creat_type = (TextView)view.findViewById(R.id.creat_type);
            dialog_cancle = view.findViewById(R.id.dialog_cancle);
            dialog_ok = view.findViewById(R.id.dialog_ok);
            ll_do = view.findViewById(R.id.ll_do);
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
    public void setData(List<Order> orders){
        if(orders!=null&&orders.size()>0){
            switch (load_type){
                case UP_LOAD_TYPE://上拉加载
                    dataList.addAll(orders);
                    break;
                case DOWN_LOAD_TYPE://下拉更新
                    this.dataList = orders;
                    break;
            }
            this.notifyDataSetChanged();
        }
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



    public void setNetOrder(boolean netOrder){
        this.netOrder = netOrder;
    }


    public interface RefrushLisener{
        public void refrush();
    }


}
