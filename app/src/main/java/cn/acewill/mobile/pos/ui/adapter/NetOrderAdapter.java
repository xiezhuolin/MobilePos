//package cn.acewill.mobile.pos.ui.adapter;
//
//import android.content.Context;
//import android.content.Intent;
//import android.os.Bundle;
//import android.os.Handler;
//import android.support.v7.widget.RecyclerView;
//import android.text.TextUtils;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.LinearLayout;
//import android.widget.ProgressBar;
//import android.widget.TextView;
//
//import org.greenrobot.eventbus.EventBus;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.concurrent.CopyOnWriteArrayList;
//
//import cn.acewill.mobile.pos.R;
//import cn.acewill.mobile.pos.common.NetOrderController;
//import cn.acewill.mobile.pos.common.RetreatDishController;
//import cn.acewill.mobile.pos.common.StoreInfor;
//import cn.acewill.mobile.pos.config.MyApplication;
//import cn.acewill.mobile.pos.config.Store;
//import cn.acewill.mobile.pos.exception.PosServiceException;
//import cn.acewill.mobile.pos.interfices.DialogEtCallback;
//import cn.acewill.mobile.pos.interfices.DishCheckCallback;
//import cn.acewill.mobile.pos.model.NetOrderRea;
//import cn.acewill.mobile.pos.model.dish.Cart;
//import cn.acewill.mobile.pos.model.dish.Dish;
//import cn.acewill.mobile.pos.model.event.PosEvent;
//import cn.acewill.mobile.pos.model.order.Order;
//import cn.acewill.mobile.pos.model.order.OrderItem;
//import cn.acewill.mobile.pos.model.payment.Payment;
//import cn.acewill.mobile.pos.service.OrderService;
//import cn.acewill.mobile.pos.service.ResultCallback;
//import cn.acewill.mobile.pos.ui.activity.NetOrderAty;
//import cn.acewill.mobile.pos.ui.activity.NetOrderInfoAty;
//import cn.acewill.mobile.pos.utils.CheckOutUtil;
//import cn.acewill.mobile.pos.utils.Constant;
//import cn.acewill.mobile.pos.utils.DialogUtil;
//import cn.acewill.mobile.pos.utils.TimeUtil;
//import cn.acewill.mobile.pos.utils.ToolsUtils;
//import cn.acewill.mobile.pos.utils.UserAction;
//
//import static cn.acewill.mobile.pos.utils.DialogUtil.refreshDish;
//
//
///**
// * 當日訂單
// * Created by aqw on 2016/8/16.
// */
//public class NetOrderAdapter extends RecyclerView.Adapter {
//
//	public  Context         context;
//	public  List<Order>     dataList;
//	public  LayoutInflater  inflater;
//	private RefrushLisener  refrushLisener;
//
//
//	public static final int UP_LOAD_TYPE   = 0;//上拉加载
//	public static final int DOWN_LOAD_TYPE = 1;//下拉刷新
//	public              int load_type      = 0;//加载类型
//
//	public static final int LOAD_MORE        = 0;//加载更多
//	public static final int LOADING          = 1;//正在加载
//	public static final int NO_MORE          = 2;//没有数据了
//	public              int load_more_status = 0;
//
//	private static final int TYPE_ITEM   = 0;//普通Item
//	private static final int TYPE_FOOTER = 1;//底部footview
//	private MyApplication myApplication;
//	private Cart          cart;
//	private Store         store;
//	private NetOrderAty   aty;
//
//
//	public NetOrderAdapter(Context context, List<Order> dataList, RefrushLisener refrushLisener) {
//		this.context = context;
//		this.dataList = dataList;
//		inflater = LayoutInflater.from(context);
//		this.refrushLisener = refrushLisener;
//		cart = Cart.getInstance();
//		store = Store.getInstance(context);
//		myApplication = MyApplication.getInstance();
//		aty = (NetOrderAty) context;
//	}
//
//	@Override
//	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//		if (viewType == TYPE_ITEM) {
//			View           view           = inflater
//					.inflate(R.layout.lv_item_net_order, parent, false);
//			ItemViewHolder itemViewHolder = new ItemViewHolder(view);
//			return itemViewHolder;
//		} else if (viewType == TYPE_FOOTER) {
//			View           foot_view      = inflater.inflate(R.layout.foot_view, parent, false);
//			FootViewHolder footViewHolder = new FootViewHolder(foot_view);
//			return footViewHolder;
//		}
//		return null;
//	}
//
//	@Override
//	public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
//		if (holder instanceof ItemViewHolder) {
//			final ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
//			final Order          orderInfo      = dataList.get(position);
//			itemViewHolder.order_num.setText(orderInfo.getId() + "");
//			String source = orderInfo.getSource();
//			if (!TextUtils.isEmpty(source)) {
//				if (source.equals("2")) {
//					source = "微信点餐";
//				}
//			} else {
//				source = "未知来源";
//			}
//			String callNumber = "";
//			if (ToolsUtils.logicIsTable()) {
//				callNumber = (TextUtils.isEmpty(orderInfo.getTableNames()) ? "0" : orderInfo
//						.getTableNames());
//			} else {
//				if (StoreInfor.cardNumberMode) {
//					callNumber = (TextUtils.isEmpty(orderInfo.getTableNames()) ? "0" : orderInfo
//							.getTableNames());
//				} else {
//					callNumber = (TextUtils.isEmpty(orderInfo.getCallNumber()) ? "0" : orderInfo
//							.getCallNumber());
//				}
//			}
//			itemViewHolder.order_tableName.setText("未接收");
//
//
//			itemViewHolder.creat_time.setText(TimeUtil.getStringTime(orderInfo.getCreatedAt()));
//			String orderType = orderInfo.getOrderType();
//			String typeStrs  = "";
//			if ("EAT_IN".equals(orderType)) {
//				typeStrs = "堂食";
//			} else if ("TAKE_OUT".equals(orderType)) {
//				typeStrs = "外带";
//			} else if ("SALE_OUT".equals(orderType)) {
//				typeStrs = "外卖";
//			}
//			itemViewHolder.creat_type.setText(typeStrs);
//
//			showOrderInfo(itemViewHolder.edit_btn, orderInfo);
//
//			//接收
//			itemViewHolder.pay_btn.setOnClickListener(new View.OnClickListener() {
//				@Override
//				public void onClick(View v) {
//					if (orderInfo != null) {
//						receiveOrder(orderInfo);
//					} else {
//						myApplication.ShowToast(ToolsUtils
//								.returnXMLStr("order_has_been_received_by_the_store_or_has_been_rejected"));
//					}
//				}
//			});
//			//拒接
//			itemViewHolder.edit_btn.setOnClickListener(new View.OnClickListener() {
//				@Override
//				public void onClick(View v) {
//					if (orderInfo != null) {
//						refuseOrder(itemViewHolder.edit_btn, orderInfo);
//					} else {
//						myApplication.ShowToast(ToolsUtils
//								.returnXMLStr("order_has_been_received_by_the_store_or_has_been_rejected"));
//					}
//				}
//			});
//
//
//			itemViewHolder.order_ll.setOnClickListener(new View.OnClickListener() {
//				@Override
//				public void onClick(View v) {//订单详情
//					UserAction.log("查看订单详情:" + dataList.get(position).getId(), context);
//					Intent intent = new Intent(context, NetOrderInfoAty.class);
//					Bundle bundle = new Bundle();
//					bundle.putSerializable("order", dataList.get(position));
//					intent.putExtras(bundle);
//					context.startActivity(intent);
//				}
//			});
//			if (orderInfo != null) {
//				itemViewHolder.lin_do.setVisibility(View.VISIBLE);
//			} else {
//				itemViewHolder.lin_do.setVisibility(View.INVISIBLE);
//			}
//
//			holder.itemView.setTag(position);
//		} else if (holder instanceof FootViewHolder) {
//			FootViewHolder footViewHolder = (FootViewHolder) holder;
//
//			switch (load_more_status) {
//				case LOAD_MORE:
//					footViewHolder.load_icon.setVisibility(View.GONE);
//					footViewHolder.load_more_tv.setText("上拉加载更多");
//					break;
//				case LOADING:
//					footViewHolder.load_icon.setVisibility(View.VISIBLE);
//					footViewHolder.load_more_tv.setText("正在加载");
//					break;
//				case NO_MORE:
//					footViewHolder.load_icon.setVisibility(View.GONE);
//					footViewHolder.load_more_tv.setText("");
//					break;
//			}
//		}
//
//	}
//
//	@Override
//	public int getItemViewType(int position) {
//		if (position + 1 == getItemCount()) {
//			return TYPE_FOOTER;
//		} else {
//			return TYPE_ITEM;
//		}
//	}
//
//	@Override
//	public int getItemCount() {
//		return dataList.size() + 1;
//	}
//
//	class ItemViewHolder extends RecyclerView.ViewHolder {
//		private LinearLayout lin_do;
//		private LinearLayout order_ll;
//		private TextView     order_num;
//		private TextView     order_tableName;
//		private TextView     creat_time;
//		private TextView     edit_btn;
//		private TextView     pay_btn;
//		private TextView     creat_type;
//
//		public ItemViewHolder(View view) {
//			super(view);
//			order_ll = (LinearLayout) view.findViewById(R.id.order_ll);
//			order_num = (TextView) view.findViewById(R.id.order_num);
//			order_tableName = (TextView) view.findViewById(R.id.order_tableName);
//			creat_time = (TextView) view.findViewById(R.id.creat_time);
//			edit_btn = (TextView) view.findViewById(R.id.edit_btn);
//			pay_btn = (TextView) view.findViewById(R.id.pay_btn);
//			creat_type = (TextView) view.findViewById(R.id.creat_type);
//			lin_do = (LinearLayout) view.findViewById(R.id.lin_do);
//		}
//	}
//
//	class FootViewHolder extends RecyclerView.ViewHolder {
//		private TextView    load_more_tv;
//		private ProgressBar load_icon;
//
//		public FootViewHolder(View itemView) {
//			super(itemView);
//			load_more_tv = (TextView) itemView.findViewById(R.id.load_more_tv);
//			load_icon = (ProgressBar) itemView.findViewById(R.id.load_icon);
//		}
//	}
//
//	/**
//	 * 更新数据
//	 *
//	 * @param orders
//	 */
//	public void setData(List<Order> orders) {
//		if (orders != null && orders.size() > 0) {
//			switch (load_type) {
//				case UP_LOAD_TYPE://上拉加载
//					dataList.addAll(orders);
//					break;
//				case DOWN_LOAD_TYPE://下拉更新
//					this.dataList = orders;
//					break;
//			}
//		} else {
//			this.dataList = new ArrayList<>();
//		}
//		this.notifyDataSetChanged();
//	}
//
//	/**
//	 * status
//	 * 0:加载更多；1:加载中；2:没有数据了；3:上拉刷新
//	 *
//	 * @param status
//	 */
//	public void changeMoreStatus(int status) {
//		load_more_status = status;
//		this.notifyDataSetChanged();
//	}
//
//	public void setLoadType(int type) {
//		load_type = type;
//	}
//
//
//	public interface RefrushLisener {
//		public void refrush();
//	}
//
//	/**
//	 * 接收网上订单
//	 *
//	 * @param orderInfo
//	 */
//	private void receiveOrder(Order orderInfo) {
//		if (orderInfo != null && !netOrderService.isReceiveNetOrder(orderInfo.getId())) {
//			ToolsUtils.writeUserOperationRecords("接受网上订单==>订单Id==" + orderInfo.getId());
//			String source = orderInfo.getSource();
//			if (!TextUtils.isEmpty(source)) {
//				if (source.equals("2")) {
//					source = "微信堂食";
//				}
//			} else {
//				source = "未知来源";
//			}
//			orderInfo.setSource(source);
//			checkDishCount(orderInfo);
//		} else {
//			MyApplication.getInstance().ShowToast(ToolsUtils
//					.returnXMLStr("order_has_been_received_by_the_store_or_has_been_rejected"));
//		}
//	}
//
//	/**
//	 * 检测菜品库存并下单
//	 *
//	 * @param order
//	 */
//	public void checkDishCount(final Order order) {
//		//换台单
//		if (order.getOperation_type() == 2) {
//			conFirmNetOrder(order);
//		} else {
//			CheckOutUtil     checkOutUtil = new CheckOutUtil(context, new Payment(1, "1"));
//			final List<Dish> dishs        = new CopyOnWriteArrayList<>();
//			for (OrderItem orderItem : order.getItemList()) {
//				Dish dish = new Dish(orderItem);
//				dishs.add(dish);
//			}
//			checkOutUtil.getDishStock(dishs, new DishCheckCallback() {
//				@Override
//				public void haveStock() {
//					getOrderId(null, order);
//				}
//
//				@Override
//				public void noStock(List dataList) {
//					refreshDish(dataList, dishs);
//				}
//			});
//		}
//	}
//
//	/**
//	 * 确认接收网上的订单
//	 *
//	 * @param resultOrder
//	 */
//	private void conFirmNetOrder(final Order resultOrder) {
//		aty.showProgress();
//		final String netOrderId   = String.valueOf(resultOrder.getId());
//		final String orderId      = String.valueOf(resultOrder.getRefOrderId());
//		final long   orderIdL     = resultOrder.getId();
//		OrderService orderService = null;
//		try {
//			orderService = OrderService.getInstance();
//		} catch (PosServiceException e) {
//			e.printStackTrace();
//		}
//		orderService.confirmNetOrder(netOrderId, orderId, resultOrder
//				.getCallNumber(), new ResultCallback<Integer>() {
//			@Override
//			public void onResult(Integer result) {
//				aty.dissmiss();
//				if (result == 0)//表示成功接单
//				{
//					netOrderService.modifyOrderType(Long.parseLong(netOrderId), 1);
//					refrushUi(resultOrder.getId());
//					MyApplication.getInstance().ShowToast(ToolsUtils
//							.returnXMLStr("net_order_number") + orderId + ToolsUtils
//							.returnXMLStr("synchronization_is_success"));
//					if (resultOrder.isInformKds())//是否通知KDS打印
//					{
//						EventBus.getDefault()
//								.post(new PosEvent(Constant.EventState.SEND_INFO_KDS_CHANGE_TABLE, resultOrder
//										.getRefOrderId(), resultOrder.getChangeTableName()));
//					}
//					if (resultOrder.isInformKitchen())//是否通知厨房打印
//					{
//						getOrderInfo(resultOrder);
//					}
//					sleep();
//				}
//			}
//
//			@Override
//			public void onError(PosServiceException e) {
//				aty.dissmiss();
//				MyApplication.getInstance()
//						.ShowToast(ToolsUtils.returnXMLStr("synchronization_is_failure") + "," + e
//								.getMessage());
//				Log.i("网上订单确认同步失败", "orderId ==" + orderId + "===" + e.getMessage());
//			}
//		});
//	}
//
//	public void getOrderId(final List<Dish> dishs, final Order order) {
//		try {
//			OrderService orderService = OrderService.getInstance();
//			orderService.getNextOrderId(new ResultCallback<Long>() {//接收网络订单并下单
//				@Override
//				public void onResult(Long result) {
//					if (result > 0) {
//						//                        PosInfo posInfo = PosInfo.getInstance();
//						//                        posInfo.setOrderId(result);
//						createOrder(dishs, order, result);
//					}
//				}
//
//				@Override
//				public void onError(PosServiceException e) {
//					Log.i("获取订单Id失败", e.getMessage());
//					EventBus.getDefault()
//							.post(new PosEvent(Constant.EventState.ERR_CREATE_ORDERID_FILURE));
//					//                    MyApplication.getInstance().ShowToast(ToolsUtils.returnXMLStr("get_order_id_failure"));
//				}
//			});
//		} catch (PosServiceException e) {
//			e.printStackTrace();
//		}
//	}
//
//	private void createOrder(List<Dish> dishs, final Order netOrder, Long result) {
//		try {
//			aty.showProgress();
//			OrderService orderService = OrderService.getInstance();
//			Order        order        = null;
//			if (dishs != null) {
//				order = cart.getNetOrderItem(dishs, netOrder);
//			} else if (netOrder != null) {
//				order = ToolsUtils.cloneTo(netOrder);
//			}
//			if (order != null) {
//				final long orderId = order.getId();
//				order.setRefNetOrderId(orderId);
//				order.setThirdPlatformOrderId(netOrder.getOuterOrderid());
//				order.setThirdPlatformOrderIdView(netOrder.getOuterOrderIdView());
//				order.setThirdPlatfromOrderIdDaySeq(netOrder.getOuterOrderIdDaySeq());
//				order.setId(result);
//				final Order finalOrder = ToolsUtils.cloneTo(order);
//				orderService.createOrder(order, new ResultCallback<Order>() {
//					@Override
//					public void onResult(final Order result) {
//						aty.dissmiss();
//						if (result != null) {
//							if (netOrderService != null) {
//								netOrderService.modifyOrderType(orderId, 1);//修改为已接收
//								netOrderService.setStopSyncNetOrder(false);//停止轮训网上订单
//							}
//
//							if (store.isCreateOrderJyj()) {
//								result.setJyjOrder(true);//将下单状态改为向JYJ下单
//								createOrderJyj(result, orderId);
//							} else {
//								orderPrint(result, orderId);
//							}
//
//						} else {
//							MyApplication.getInstance()
//									.ShowToast(ToolsUtils.orderErrTips(finalOrder, orderId));
//						}
//					}
//
//					@Override
//					public void onError(PosServiceException e) {
//						Log.i("网上开台下单失败", "orderId ==" + orderId + "===" + e.getMessage());
//						myApplication.ShowToast(ToolsUtils.orderErrTips(finalOrder, orderId));
//						//                        refrushUi(position,netOrder.getId());
//						aty.dissmiss();
//					}
//				});
//			}
//		} catch (PosServiceException e) {
//			e.printStackTrace();
//			Log.i("网上开台下单失败", "===" + e.getMessage());
//		}
//	}
//
//	private void createOrderJyj(final Order order, final long orderId) {
//		aty.showProgress();
//		final Order newOrder = ToolsUtils.cloneTo(order);
//		if (newOrder != null) {
//			try {
//				OrderService orderService = OrderService.getJyjOrderService();
//				orderService.createOrder(newOrder, new ResultCallback<Order>() {
//					@Override
//					public void onResult(Order result) {
//						aty.dissmiss();
//						if (result != null) {
//							orderPrint(result, orderId);
//						} else {
//							Log.e("JYJ下单", "JYJ下单失败:" + "null");
//							MyApplication.getInstance().ShowToast(ToolsUtils
//									.returnXMLStr("orders_failed") + "," + ToolsUtils
//									.returnXMLStr("sync_jyj_order_err"));
//							order.setJyjPrintErrMessage(ToolsUtils
//									.returnXMLStr("jyj_order_error_message"));
//							orderPrint(order, orderId);
//						}
//					}
//
//					@Override
//					public void onError(PosServiceException e) {
//						aty.dissmiss();
//						Log.e("JYJ下单", "JYJ下单失败:" + e.getMessage());
//						MyApplication.getInstance().ShowToast(ToolsUtils
//								.returnXMLStr("orders_failed") + "," + ToolsUtils
//								.returnXMLStr("sync_jyj_order_err") + "," + e.getMessage());
//						order.setJyjPrintErrMessage(ToolsUtils
//								.returnXMLStr("jyj_order_error_message"));
//						orderPrint(order, orderId);
//					}
//				});
//			} catch (PosServiceException e) {
//				e.printStackTrace();
//			}
//
//		}
//	}
//
//	private void orderPrint(final Order result, long orderId) {
//		Log.i("网上开台下单成功", "orderId ==" + orderId + "===" + ToolsUtils.getPrinterSth(result));
//		if (!TextUtils.isEmpty(StoreInfor.storeMode) && StoreInfor.storeMode.equals("TABLE")) {
//			EventBus.getDefault().post(new PosEvent(Constant.EventState.SELECT_FRAGMTNT_TABLE));
//		}
//		System.out.println("==checkDishCount==" + orderId);
//
//		EventBus.getDefault()
//				.post(new PosEvent(Constant.EventState.PRINTER_ORDER, ToolsUtils.cloneTo(result)));
//
//		new Handler().postDelayed(new Runnable() {
//			public void run() {
//				EventBus.getDefault()
//						.post(new PosEvent(Constant.EventState.PRINTER_KITCHEN_ORDER, ToolsUtils
//								.cloneTo(result)));
//			}
//		}, 3000);
//
//		new Handler().postDelayed(new Runnable() {
//			public void run() {
//				EventBus.getDefault()
//						.post(new PosEvent(Constant.EventState.PRINT_CHECKOUT, ToolsUtils
//								.cloneTo(result)));
//			}
//		}, 3000);
//
//		//conFirmNetOrder(position ,result);
//		MyApplication.getInstance()
//				.ShowToast(ToolsUtils.returnXMLStr("net_order_number") + orderId + ToolsUtils
//						.returnXMLStr("get_order_success"));
//
//		refrushUi(orderId);
//		EventBus.getDefault().post(new PosEvent(Constant.EventState.SEND_INFO_KDS, ToolsUtils
//				.cloneTo(result), result.getTableNames()));//kds下单
//	}
//
//	private void refrushUi(long netOrderId) {
//		Order copyList = NetOrderController.getNetOrderMap()
//				.get(netOrderId);//根据订单ID去map列表里面查询出order对象
//		if (copyList != null && copyList.getId() == (int) netOrderId) {
//			NetOrderController.getNetOrderList().remove(copyList);
//		}
//		notifyDataSetChanged();
//		EventBus.getDefault()
//				.post(new PosEvent(Constant.EventState.PUT_NET_ORDER, NetOrderController
//						.getNetOrderList()));
//	}
//
//	/**
//	 * 根据订单Id获得订单详情
//	 */
//	private void getOrderInfo(final Order order) {
//		try {
//			myApplication.ShowToast(ToolsUtils.returnXMLStr("getting_order_details_please_wait"));
//			OrderService orderService = OrderService.getInstance();
//			orderService.getOrderInfoById(order.getRefOrderId() + "", new ResultCallback<Order>() {
//				@Override
//				public void onResult(final Order result) {
//					if (result != null && result.getItemList() != null && result.getItemList()
//							.size() > 0) {
//						new Handler().postDelayed(new Runnable() {
//							public void run() {
//								order.setItemList(result.getItemList());
//								order.setOrderType(result.getOrderType());
//								order.setId(result.getId());
//								EventBus.getDefault()
//										.post(new PosEvent(Constant.EventState.PRINTER_KITCHEN_ORDER, order));
//							}
//						}, 3000);
//					}
//				}
//
//				@Override
//				public void onError(PosServiceException e) {
//					Log.i("订单详情获取失败!", e.getMessage());
//					myApplication.ShowToast(e.getMessage());
//				}
//			});
//		} catch (PosServiceException e) {
//			e.printStackTrace();
//			Log.i("订单详情获取失败!", e.getMessage());
//		}
//	}
//
//	private void refuseOrder(TextView view, final Order orderInfo) {
//		ToolsUtils.writeUserOperationRecords("拒绝网上订单==>订单Id==" + orderInfo.getId());
//		if (view.getVisibility() == View.GONE) {
//			rejuctNetOrder(orderInfo.getId(), ToolsUtils.returnXMLStr("order_info_error"));
//		} else {
//			List<NetOrderRea> reasonItem = new ArrayList<NetOrderRea>();
//			reasonItem.add(new NetOrderRea(ToolsUtils.returnXMLStr("weather_reason")));
//			reasonItem.add(new NetOrderRea(ToolsUtils.returnXMLStr("order_info_error")));
//			reasonItem.add(new NetOrderRea(ToolsUtils.returnXMLStr("dish_is_sell_out")));
//			reasonItem.add(new NetOrderRea(ToolsUtils.returnXMLStr("will_close")));
//			reasonItem.add(new NetOrderRea(ToolsUtils.returnXMLStr("no")));
//			DialogUtil.listDialog(aty, ToolsUtils
//					.returnXMLStr("reject_reason"), reasonItem, new DialogEtCallback() {
//				@Override
//				public void onConfirm(String sth) {
//					rejuctNetOrder(orderInfo.getId(), sth);
//				}
//
//				@Override
//				public void onCancle() {
//
//				}
//			});
//		}
//	}
//
//	/**
//	 * 拒绝网上订单
//	 */
//	private void rejuctNetOrder(final long orderId, final String rejuctStr) {
//		aty.showProgress();
//		try {
//			final OrderService orderService = OrderService.getInstance();
//			orderService.rejectNetOrder(orderId, rejuctStr, new ResultCallback<Integer>() {
//				@Override
//				public void onResult(Integer result) {
//					aty.dissmiss();
//					if (result == 0)//表示成功拒绝
//					{
//						MyApplication.getInstance().ShowToast("已成功拒接,网上订单号:" + orderId + "!");
//						notifyDataSetChanged();
//					}
//				}
//
//				@Override
//				public void onError(PosServiceException e) {
//					aty.dissmiss();
//					MyApplication.getInstance().ShowToast(e.getMessage());
//				}
//			});
//		} catch (PosServiceException e) {
//			e.printStackTrace();
//		}
//	}
//
//	/**
//	 * 显示订单信息
//	 *
//	 * @param order
//	 */
//	private void showOrderInfo(TextView view, Order order) {
//		if (order != null) {
//			if (order.getOperation_type() != 1) {
//				view.setText(ToolsUtils.returnXMLStr("refuse"));
//			} else {
//				view.setText(ToolsUtils.returnXMLStr("agree_change_table"));
//			}
//
//			//如果是桌台模式
//			if (ToolsUtils.logicIsTable()) {
//				//判断该订单是否含有tableid,没有的话隐藏接受按钮
//				if (order.preorderTime > 0) {
//					view.setVisibility(View.GONE);
//				} else {
//					view.setVisibility(View.VISIBLE);
//				}
//			}
//
//			if (order.getItemList() != null && order.getItemList().size() > 0) {
//				RetreatDishController.setItemList(order.getItemList());
//				RetreatDishController.setTempItemList(order.getItemList());
//			}
//		}
//	}
//
//	/**
//	 * 休息两秒
//	 */
//	private void sleep() {
//		try {
//			Thread.sleep(3000);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//	}
//
//}
