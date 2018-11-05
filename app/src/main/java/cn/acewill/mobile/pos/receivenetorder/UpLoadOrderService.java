package cn.acewill.mobile.pos.receivenetorder;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cn.acewill.mobile.pos.R;
import cn.acewill.mobile.pos.common.StoreInfor;
import cn.acewill.mobile.pos.config.MyApplication;
import cn.acewill.mobile.pos.config.Store;
import cn.acewill.mobile.pos.exception.PosServiceException;
import cn.acewill.mobile.pos.model.dish.Cart;
import cn.acewill.mobile.pos.model.dish.Dish;
import cn.acewill.mobile.pos.model.event.PosEvent;
import cn.acewill.mobile.pos.model.order.Order;
import cn.acewill.mobile.pos.service.OrderService;
import cn.acewill.mobile.pos.service.ResultCallback;
import cn.acewill.mobile.pos.utils.Constant;
import cn.acewill.mobile.pos.utils.ToolsUtils;

import static java.lang.Thread.currentThread;
import static java.lang.Thread.sleep;

/**
 * Author：Anch
 * Date：2017/5/19 15:58
 * Desc：
 */
public class UpLoadOrderService extends Service {

	private static final String   TAG                   = "UpLoadOrderService";
	private static final int      uploadOrderTag        = 100;
	private static final int      delayTime             = 1 * 1000;
	private              Runnable upLoadOrderRunnable   = new Runnable() {


		@Override
		public void run() {
			handler.sendEmptyMessage(uploadOrderTag);
			handler.postDelayed(upLoadOrderRunnable, delayTime);
		}
	};
	private              Runnable syncNetOrdersRunnable = new Runnable() {
		@Override
		public void run() {
			getNetOrderInfo();
		}
	};


	private int     currentTime = -1;
	private int     queryTime   = 8;
	public  Handler handler     = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
				case uploadOrderTag:
					if (currentTime > 0) {
						currentTime--;
					} else if (currentTime == 0) {
						Log.i(TAG, "开始获取网络订单。");
						currentTime--;
						handler.removeCallbacks(upLoadOrderRunnable);
						getNetOrderInfo();
					}
					break;
			}

		}
	};

	private List<Order> uploadList = new CopyOnWriteArrayList<Order>();

	private void getNetOrderInfo() {
		Log.i(TAG, currentThread().getName() + ">getNetOrderInfo ");
		excuteCount = 0;
		uploadList.clear();
		if (Store.getInstance(this).getReceiveNetOrder()) {
			try {
				OrderService orderService = OrderService.getInstance();
				final Store  store        = Store.getInstance(MyApplication.getInstance());
				orderService
						.syncNetOrders(store.getPreordertime(), new ResultCallback<List<Order>>() {
							@Override
							public void onResult(List<Order> result) {
								List<Order> orderWaiMaiList      = new CopyOnWriteArrayList<Order>();
								boolean     isWaiMaiOrderReceive = true;
								if (!ToolsUtils.isList(result)) {
									Log.i(TAG, currentThread()
											.getName() + ">后台轮训网上订单信息条数=====" + result.size());
									for (Order order : result) {
										Log.i(TAG, currentThread().getName() + ">网络订单号=====" + order
												.getId());
									}
									if (!store.isWaiMaiOrderReceive()) {
										isWaiMaiOrderReceive = false;
										for (Order order : result) {
											if (order.getOrderType().equals("SALE_OUT")) {
												orderWaiMaiList.add(order);
											} else {
												uploadList.add(order);
											}
										}
									} else {
										uploadList = ToolsUtils.cloneTo(result);
									}
									if (store.getAutoMaticNetOrder()) {
										autoMaticNetOrder(uploadList);
									} else {
										if (isWaiMaiOrderReceive) {
											EventBus.getDefault()
													.post(new PosEvent(Constant.EventState.PUT_NET_ORDER, uploadList));
										} else {
											EventBus.getDefault()
													.post(new PosEvent(Constant.EventState.PUT_NET_ORDER, orderWaiMaiList));
										}
									}
								} else {
									Log.i(TAG, "startTimer1");
									Log.i(TAG, Thread.currentThread()
											.getId() + ">后台轮训网上订单信息条数=====0");
									startTimer(0);
								}
							}

							@Override
							public void onError(PosServiceException e) {
								Log.i(TAG, "startTimer2");
								startTimer(0);
								Log.i(TAG, "同步订单时后台或者网络异常造成失败1");
							}
						});
			} catch (PosServiceException e) {
				Log.i(TAG, "startTimer3");
				startTimer(0);
				e.printStackTrace();
				Log.i(TAG, "同步订单时后台或者网络异常造成失败2");
			}
		}
	}

	//	private void startUpLoadOrder() {
	//
	//		//		Cursor cursor = DataSupport.findBySQL("select * from orderreq where upload =0");
	//		uploadlist.clear();
	//		excutePosition = 0;
	//		uploadlist = DataSupport.where("upload=?", 0 + "").find(OrderReq.class);
	//
	//		for (int i = 0; i < uploadlist.size(); i++) {
	//			OrderReq req = uploadlist.get(i);
	//			List<DishModel> models = DataSupport
	//					.where("orderreq_id=?", req.getId() + "")
	//					.find(DishModel.class);
	//			List<Payment> payments = DataSupport
	//					.where("orderreq_id=?", req.getId() + "").find(Payment.class);
	//			req.setItemList(models);
	//			req.setPaymentList(payments);
	//		}
	//
	//
	//
	//	}

	private void autoMaticNetOrder(final List<Order> orderList) {
		Log.e(TAG, currentThread().getName() + "upload.>");//执行上传操作，完成后，再次开启计时
		if (orderList != null && orderList.size() == 0) {
			Log.i(TAG, "startTimer4");
			startTimer(0);
		}


		for (int i = 0; i < orderList.size(); i++) {
			final int finalI = i;
			fixedThreadPool.execute(new Runnable() {
				@Override
				public void run() {
					final int index = finalI;
					try {
						sleep(2000);
					} catch (InterruptedException e) {
						Log.i(TAG, "startTimer12");
						startTimer(0);
						e.printStackTrace();
					}
					//并发的时候可能会出现问题
					Order orderInfo;
					synchronized (UpLoadOrderService.class) {
						orderInfo = ToolsUtils.cloneTo(orderList.get(index));
					}
					//订单对象不能为空 并且订单没有被门店接收过
					if (orderInfo != null) {
						ToolsUtils.writeUserOperationRecords("正在处理订单的网络订单号" + orderInfo.getId());
						String source = orderInfo.getSource();
						if (!TextUtils.isEmpty(source)) {
							if (source.equals("2")) {
								source = "微信堂食";
							}
						} else {
							source = "未知来源";
						}
						orderInfo.setSource(source);
						checkDishCount(orderInfo);
					}

				}
			});
		}
	}

	/**
	 * 检测菜品库存并下单
	 *
	 * @param order
	 */
	public void checkDishCount(Order order) {
		getOrderId(null, order);
	}

	/**
	 * 支付完成之后要去获取一个订单号
	 */

	public void getOrderId(final List<Dish> dishs, final Order order) {
		Log.i(TAG, currentThread().getName() + "getOrderId ");
		try {
			OrderService orderService = OrderService.getInstance();
			orderService.getNextOrderId(new ResultCallback<Long>() {
				@Override
				public void onResult(Long result) {
					Log.i(TAG, Thread.currentThread().getId() + ">网络订单和平台订单号分别是>" + order
							.getId() + "-------" + result);
					if (result > 0) {
						order.setId(result);
						createOrder(dishs, order);
					} else {
						Log.i(TAG, "startTimer11");
						startTimer(0);
					}
				}

				@Override
				public void onError(PosServiceException e) {
					Log.i(TAG, "获取订单Id失败>" + e.getMessage());
					Log.i(TAG, "startTimer5");
					startTimer(0);
					EventBus.getDefault()
							.post(new PosEvent(Constant.EventState.ERR_CREATE_ORDERID_FILURE));
					//                    MyApplication.getInstance().ShowToast(ToolsUtils.returnXMLStr("get_order_id_failure"));
				}
			});
		} catch (PosServiceException e) {
			Log.i(TAG, "startTimer6");
			startTimer(0);
			e.printStackTrace();
		}
	}

	private int excuteCount;

	private synchronized void createOrder(List<Dish> dishs, final Order netOrder) {
		try {
			OrderService orderService = OrderService.getInstance();
			Order        order        = null;
			if (dishs != null) {
				order = Cart.getInstance().getNetOrderItem(dishs, netOrder);
			} else if (netOrder != null) {
				order = ToolsUtils.cloneTo(netOrder);
			}
			if (order != null) {
				final long orderId = order.getId();
				order.setRefNetOrderId(orderId);
				order.setThirdPlatformOrderId(netOrder.getOuterOrderid());
				order.setThirdPlatformOrderIdView(netOrder.getOuterOrderIdView());
				order.setThirdPlatfromOrderIdDaySeq(netOrder.getOuterOrderIdDaySeq());
				final Order finalOrder = ToolsUtils.cloneTo(order);
				orderService.createOrder(order, new ResultCallback<Order>() {
					@Override
					public void onResult(final Order result) {
						excuteCount++;
						Log.i(TAG, Thread.currentThread().getId() + ">createOrder Result");
						if (excuteCount == uploadList.size()) {
							Log.i(TAG, "startTimer7");
							startTimer(0);
						}
						if (result != null) {
							orderPrint(result, orderId);
						} else {
							MyApplication.getInstance()
									.ShowToast(ToolsUtils.orderErrTips(finalOrder, orderId));
							Log.i(TAG, "网上开台下单失败" + "orderId ==" + orderId + "===" + "订单状态获取失败,或者门店已接收该订单!");
						}
					}

					@Override
					public void onError(PosServiceException e) {
						excuteCount++;
						if (excuteCount == uploadList.size()) {
							Log.i(TAG, "startTimer8");
							startTimer(0);
						}
						Log.i(TAG, "网上开台下单失败>" + "orderId ==" + orderId + "===" + e.getMessage());
						MyApplication.getInstance()
								.ShowToast(ToolsUtils.orderErrTips(finalOrder, orderId));
						//refrushUi(position,netOrder.getId());
					}
				});
			}
		} catch (PosServiceException e) {
			excuteCount++;
			if (excuteCount == uploadList.size()) {
				Log.i(TAG, "startTimer9");
				startTimer(0);
			}
			e.printStackTrace();
			Log.i(TAG, "网上开台下单失败" + "===" + e.getMessage());
		}
	}

	private void orderPrint(final Order result, long orderId) {
		Log.i(TAG, Thread.currentThread()
				.getId() + ">打印>netOrderId ==" + orderId + "trueOrderId>" + result
				.getId() + "===" + ToolsUtils.getPrinterSth(result));
		if (!TextUtils.isEmpty(StoreInfor.storeMode) && StoreInfor.storeMode.equals("TABLE")) {
			EventBus.getDefault().post(new PosEvent(Constant.EventState.SELECT_FRAGMTNT_TABLE));
		}
		//		System.out.println("==checkDishCount==" + orderId);

		EventBus.getDefault().post(new PosEvent(Constant.EventState.PRINTER_ORDER, result));

		//		new Handler().postDelayed(new Runnable() {
		//			public void run() {
		EventBus.getDefault()
				.post(new PosEvent(Constant.EventState.PRINTER_KITCHEN_ORDER, result));
		//			}
		//		}, 3000);

		//		new Handler().postDelayed(new Runnable() {
		//			public void run() {
		EventBus.getDefault()
				.post(new PosEvent(Constant.EventState.PRINT_CHECKOUT, result));
		//			}
		//		}, 3000);
		//conFirmNetOrder(position ,result);
		MyApplication.getInstance().ShowToast("网络订单编号 : " + orderId + "接单成功!");

		//		refrushUi(orderId);
		EventBus.getDefault().post(new PosEvent(Constant.EventState.SEND_INFO_KDS, result, result
				.getTableNames()));//kds下单
	}

	//	private void refrushUi(long netOrderId) {
	//		Order copyList = NetOrderController.getNetOrderMap()
	//				.get(netOrderId);//根据订单ID去map列表里面查询出order对象
	//		if (copyList != null && copyList.getId() == (int) netOrderId) {
	//			NetOrderController.getNetOrderList().remove(copyList);
	//		}
	//	}
	/*
  下单 ，调用后台的接口
	*/

	public class MyBinder extends Binder {
		public UpLoadOrderService getService() {
			return UpLoadOrderService.this;
		}
	}

	//通过binder实现调用者client与Service之间的通信
	private MyBinder binder = new MyBinder();
	private ExecutorService fixedThreadPool;

	@Override
	public void onCreate() {
		super.onCreate();
		Log.i(TAG, Thread.currentThread().getId() + ">onCreate");

		fixedThreadPool = Executors.newSingleThreadExecutor();
		Notification notifation = new Notification.Builder(this)
				.setContentTitle("接单服务")
				.setContentText("正在运行中...")
				.setSmallIcon(R.mipmap.pos_icon)
				.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.pos_icon))
				.build();
		NotificationManager manger = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		manger.notify(0, notifation);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i(TAG, "startTimer10");
		//		startTimer(0);
		return START_NOT_STICKY;
	}


	public void startTimer(int time) {
		Log.i(TAG, "first > startTimer");
		currentTime = queryTime;
		handler.postDelayed(upLoadOrderRunnable, time);//1 秒执行一次
	}

	/**
	 * 检测打印机连接的状态
	 *
	 * @param
	 * @throws IOException
	 */


	@Override
	public IBinder onBind(Intent intent) {
		Log.i(TAG, "TestService -> onBind, Thread: " + currentThread().getName());
		return binder;
	}

	@Override
	public boolean onUnbind(Intent intent) {
		Log.i(TAG, "TestService -> onUnbind, from:" + intent.getStringExtra("from"));
		return false;
	}

	@Override
	public void onDestroy() {
		Log.i(TAG, "TestService -> onDestroy, Thread: " + currentThread().getName());
		super.onDestroy();
	}

}
