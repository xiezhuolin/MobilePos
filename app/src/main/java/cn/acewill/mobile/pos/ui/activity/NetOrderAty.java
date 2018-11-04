//package cn.acewill.mobile.pos.ui.activity;
//
//import android.os.Bundle;
//import android.support.v4.widget.SwipeRefreshLayout;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.OrientationHelper;
//import android.support.v7.widget.RecyclerView;
//import android.util.Log;
//import android.view.View;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import butterknife.BindView;
//import butterknife.ButterKnife;
//import butterknife.OnClick;
//import cn.acewill.mobile.pos.R;
//import cn.acewill.mobile.pos.base.activity.BaseActivity;
//import cn.acewill.mobile.pos.common.StoreInfor;
//import cn.acewill.mobile.pos.config.MyApplication;
//import cn.acewill.mobile.pos.config.Store;
//import cn.acewill.mobile.pos.interfices.DialogOkCallBack;
//import cn.acewill.mobile.pos.interfices.NetOrderinfoCallBack;
//import cn.acewill.mobile.pos.model.order.Order;
//import cn.acewill.mobile.pos.model.user.UserData;
//import cn.acewill.mobile.pos.service.NetOrderService;
//import cn.acewill.mobile.pos.ui.adapter.NetOrderAdapter;
//import cn.acewill.mobile.pos.utils.ToolsUtils;
//
//import static cn.acewill.mobile.pos.common.NetOrderController.netOrderList;
//
///**
// * 网上订单
// * Created by Administrator on 2016/8/19.
// * <p>
// * <p>
// * 本来是从OrderDayFragment 进入到这个页面的 2018 07 11
// */
//public class NetOrderAty extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener,
//		NetOrderAdapter.RefrushLisener, NetOrderinfoCallBack {
//	@BindView(R.id.title_left)
//	LinearLayout       titleLeft;
//	@BindView(R.id.right_left_ll)
//	LinearLayout       rightLeftll;
//	@BindView(R.id.order_lv)
//	RecyclerView       orderLv;
//	@BindView(R.id.order_srl)
//	SwipeRefreshLayout orderSrl;
//	@BindView(R.id.tv_number)
//	TextView           tvNumber;
//
//	private NetOrderAdapter adapter;
//	private List<Order> orderList       = new ArrayList<Order>();
//	private int         lastVisibleItem = 0;
//	private Store store;
//	private String TAG = "NetOrderAcy";
//	private UserData         mUserData;
//	private DialogOkCallBack callBack;
//	private NetOrderService  netOrderService;
//
//	private int page  = 0;
//	private int limit = 800;
//
//	@Override
//	public void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.aty_net_order);
//		ButterKnife.bind(this);
//		myApplication.addPage(NetOrderAty.this);
//		initView();
//	}
//
//
//	private void initView() {
//		store = store.getInstance(context);
//		orderSrl.setOnRefreshListener(this);
//		netOrderService = MyApplication.getInstance().getNetOrderService();
//		if (netOrderService != null) {
//			netOrderService.setNetOrderinfoCallBack(this);
//		}
//		orderSrl.setColorSchemeResources(R.color.green, R.color.blue, R.color.yellow_bg);
//		adapter = new NetOrderAdapter(context, orderList, this);
//		final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
//		linearLayoutManager.setOrientation(OrientationHelper.VERTICAL);
//		orderLv.setLayoutManager(linearLayoutManager);
//		mUserData = UserData.getInstance(context);
//
//		String numberType = "";
//		if (ToolsUtils.logicIsTable()) {
//			numberType = ToolsUtils.returnXMLStr("table_number");
//		} else {
//			if (StoreInfor.cardNumberMode) {
//				numberType = ToolsUtils.returnXMLStr("card_number");
//			} else {
//				numberType = ToolsUtils.returnXMLStr("call_number");
//			}
//		}
//		tvNumber.setText(numberType.replace(":", ""));
//
//		orderLv.setAdapter(adapter);
//
//		orderLv.addOnScrollListener(new RecyclerView.OnScrollListener() {
//			@Override
//			public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//				super.onScrollStateChanged(recyclerView, newState);
//			}
//
//			@Override
//			public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//				super.onScrolled(recyclerView, dx, dy);
//				lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
//				if (lastVisibleItem + 1 == adapter
//						.getItemCount() && adapter.load_more_status == adapter.LOAD_MORE && dy > 0) {
//					adapter.setLoadType(adapter.UP_LOAD_TYPE);
//					adapter.changeMoreStatus(adapter.LOADING);
//				}
//			}
//		});
//	}
//
//
//	@Override
//	public void onResume() {
//		super.onResume();
//		initData();
//		orderLv.scrollToPosition(0);
//	}
//
//	//获取第一页数据
//	public void initData() {
//		//        adapter.setLoadType(adapter.DOWN_LOAD_TYPE);
//		adapter.changeMoreStatus(adapter.NO_MORE);
//		orderSrl.setRefreshing(false);
//	}
//
//	@Override
//	public void onRefresh() {
//		initData();
//	}
//
//
//	@Override
//	public void refrush() {
//		initData();
//	}
//
//	private void setNetOrderData(List<Order> orderList) {
//		netOrderList = ToolsUtils.cloneTo(orderList);
//		if (orderList != null && orderList.size() > 0) {
//			adapter.setData(orderList);
//			if (orderList.size() < limit) {
//				adapter.changeMoreStatus(adapter.NO_MORE);
//			} else {
//				adapter.changeMoreStatus(adapter.LOAD_MORE);
//			}
//		} else {
//			adapter.changeMoreStatus(adapter.NO_MORE);
//		}
//		orderSrl.setRefreshing(false);
//	}
//
//	@Override
//	public void getNetOrderInfoList(List<Order> orderList) {
//		if (!ToolsUtils.isList(orderList)) {
//			adapter.setLoadType(adapter.DOWN_LOAD_TYPE);
//			setNetOrderData(orderList);
//		} else {
//			adapter.setData(null);
//			adapter.changeMoreStatus(adapter.NO_MORE);
//			orderSrl.setRefreshing(false);
//			Log.i("没有网络订单信息", ToolsUtils.getPrinterSth(orderList));
//			Log.i(TAG, "后台轮训网上订单信息条数=====0");
//		}
//	}
//
//	@Override
//	public void printState(boolean isPrint) {
//
//	}
//
//	@OnClick({R.id.title_left})
//	public void onClick(View view) {
//		switch (view.getId()) {
//			//退出
//			case R.id.title_left:
//				finish();
//				break;
//		}
//	}
//}
