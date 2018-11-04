package cn.acewill.mobile.pos.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.acewill.mobile.pos.R;
import cn.acewill.mobile.pos.base.fragment.BaseFragment;
import cn.acewill.mobile.pos.common.StoreInfor;
import cn.acewill.mobile.pos.config.MyApplication;
import cn.acewill.mobile.pos.config.Store;
import cn.acewill.mobile.pos.exception.PosServiceException;
import cn.acewill.mobile.pos.interfices.DialogOkCallBack;
import cn.acewill.mobile.pos.model.order.Order;
import cn.acewill.mobile.pos.model.user.UserData;
import cn.acewill.mobile.pos.presenter.OrderPresenter;
import cn.acewill.mobile.pos.ui.DialogView;
import cn.acewill.mobile.pos.ui.adapter.OrderDayAdapter;
import cn.acewill.mobile.pos.utils.ToolsUtils;
import cn.acewill.mobile.pos.widget.CommonEditText;

/**
 * 当日订单
 * Created by Administrator on 2016/8/19.
 */
public class OrderDayFragment extends BaseFragment implements DialogView, SwipeRefreshLayout.OnRefreshListener, OrderDayAdapter.RefrushLisener {
    @BindView(R.id.order_lv)
    RecyclerView orderLv;
    @BindView(R.id.order_srl)
    SwipeRefreshLayout orderSrl;
    @BindView(R.id.more_set)
    LinearLayout moreSet;
//    @BindView(R.id.tv_order_count)
//    TextView tv_order_count;
    @BindView(R.id.tv_number)
    TextView tvNumber;
//    @BindView(R.id.tv_net_order)
//    View tv_net_order;
    @BindView( R.id.search_cotent )
    CommonEditText edMemberNumber;
    @BindView( R.id.search_clear )
    LinearLayout searchClear;

    private OrderPresenter orderPresenter;
    private OrderDayAdapter adapter;
    private List<Order> orderList = new ArrayList<Order>();
    private int lastVisibleItem = 0;
    private Store store;
    private Integer workshiftId;
    private UserData mUserData;
    private DialogOkCallBack callBack;
    private List<Order> historyOrderList = new CopyOnWriteArrayList<>();

    private int page = 0;
    private int limit = 800;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_day, container, false);
        ButterKnife.bind(this, view);
        initView();
        return view;
    }

    private void initView() {
        store = store.getInstance(aty);
//        if (store.getReceiveNetOrder()) {
//            tv_net_order.setVisibility(View.VISIBLE);
//        }
//        else{
//            tv_net_order.setVisibility(View.GONE);
//        }

        orderPresenter = new OrderPresenter(this);
        orderSrl.setOnRefreshListener(this);
        orderSrl.setColorSchemeResources(R.color.green, R.color.blue, R.color.yellow_bg);
        adapter = new OrderDayAdapter(getActivity(), orderList, this, false);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(OrientationHelper.VERTICAL);
        orderLv.setLayoutManager(linearLayoutManager);
        mUserData = UserData.getInstance(getActivity());

        String numberType = "";
        if (ToolsUtils.logicIsTable()) {
            numberType = ToolsUtils.returnXMLStr("table_number");
        } else {
            if (StoreInfor.cardNumberMode) {
                numberType = ToolsUtils.returnXMLStr("card_number");
            } else {
                numberType = ToolsUtils.returnXMLStr("call_number");
            }
        }
        tvNumber.setText(numberType.replace(":",""));

        orderLv.setAdapter(adapter);

        orderLv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                if (lastVisibleItem + 1 == adapter.getItemCount() && adapter.load_more_status == adapter.LOAD_MORE && dy > 0) {
                    adapter.setLoadType(adapter.UP_LOAD_TYPE);
                    adapter.changeMoreStatus(adapter.LOADING);
                    orderPresenter.getAllOrders(++page, limit);
                }
            }
        });

        //edtext的控件内容监听
        edMemberNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    searchClear.setVisibility(View.VISIBLE);
                    List<Order> orders = null;
                    if (ToolsUtils.isNumeric(s.toString())) {
                        orders = getOrderInfoByOrderId(historyOrderList, s.toString());
                        adapter.setData(orders);
                        adapter.changeMoreStatus(adapter.NO_MORE);
                        orderSrl.setRefreshing(false);
                    }
                } else {
                    searchClear.setVisibility(View.GONE);
                    setOrderData(historyOrderList);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private List<Order> getOrderInfoByOrderId(List<Order> orderList, String orderId) {
        List<Order> orderS = new CopyOnWriteArrayList<>();
        if (orderList != null && orderList.size() > 0) {
            for (Order order : orderList) {
                if ((order.getId() + "").contains(orderId) || (order.getTableNames() + "").contains(orderId) || (order.getCallNumber() + "").contains(orderId)) {
                    orderS.add(order);
                }
            }
        }
        return orderS;
    }


    //设置回调
    public void setCallBack(DialogOkCallBack callBack) {
        this.callBack = callBack;
    }


    @Override
    public void onResume() {
        super.onResume();
        initData();
        orderLv.scrollToPosition(0);
    }

    //获取第一页数据
    public void initData() {
        adapter.setLoadType(adapter.DOWN_LOAD_TYPE);
        page = 0;
        orderPresenter.getAllOrders(page, limit);
    }

    @Override
    public void onRefresh() {
        initData();
    }


    @Override
    public void refrush() {
        initData();
    }

    @Override
    public void showDialog() {

    }

    @Override
    public void dissDialog() {

    }

    @Override
    public void showError(PosServiceException e) {
        orderSrl.setRefreshing(false);
        Log.e("获取订单列表","获取订单列表失败=="+e.getMessage());
        MyApplication.getInstance().ShowToast("获取订单列表失败,"+e.getMessage());
    }

    @Override
    public <T> void callBackData(T t) {
        List<Order> orders = (List<Order>) t;
        setOrderData(orders);
    }

    private void setOrderData(List<Order> orderList) {
        historyOrderList = ToolsUtils.cloneTo(orderList);
        if (orderList != null && orderList.size() > 0) {
            adapter.setData(orderList);
            if (orderList.size() < limit) {
                adapter.changeMoreStatus(adapter.NO_MORE);
            } else {
                adapter.changeMoreStatus(adapter.LOAD_MORE);
            }
        } else {
            adapter.changeMoreStatus(adapter.NO_MORE);
        }
        orderSrl.setRefreshing(false);
    }



    @OnClick({R.id.more_set, R.id.search_clear})
    public void onClick(View view) {
        switch (view.getId()) {
            //更多
            case R.id.more_set:
                callBack.onOk();
                break;
            //网络订单
//            case R.id.tv_net_order:
//                ToolsUtils.writeUserOperationRecords("网络订单");
//                startActivity(new Intent(mContext, NetOrderAty.class));
//                break;
            case R.id.search_clear:
                ToolsUtils.hideInputManager(aty,searchClear);
                edMemberNumber.setText("");
                setOrderData(historyOrderList);
                break;
        }

    }

}
