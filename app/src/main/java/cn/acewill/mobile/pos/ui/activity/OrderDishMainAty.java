package cn.acewill.mobile.pos.ui.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.PowerManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.acewill.paylibrary.alipay.config.AlipayConfig;
import com.acewill.paylibrary.tencent.WXPay;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.acewill.mobile.pos.R;
import cn.acewill.mobile.pos.base.activity.BaseActivity;
import cn.acewill.mobile.pos.common.DishDataController;
import cn.acewill.mobile.pos.common.DishOptionController;
import cn.acewill.mobile.pos.common.MainEvenBusController;
import cn.acewill.mobile.pos.common.MarketDataController;
import cn.acewill.mobile.pos.common.NetOrderController;
import cn.acewill.mobile.pos.common.PosSinUsbScreenController;
import cn.acewill.mobile.pos.common.PowerController;
import cn.acewill.mobile.pos.common.PrinterDataController;
import cn.acewill.mobile.pos.common.StoreInfor;
import cn.acewill.mobile.pos.config.MyApplication;
import cn.acewill.mobile.pos.config.Store;
import cn.acewill.mobile.pos.exception.PosServiceException;
import cn.acewill.mobile.pos.interfices.DialogOkCallBack;
import cn.acewill.mobile.pos.model.Definition;
import cn.acewill.mobile.pos.model.Discount;
import cn.acewill.mobile.pos.model.KDS;
import cn.acewill.mobile.pos.model.KitchenStall;
import cn.acewill.mobile.pos.model.Market;
import cn.acewill.mobile.pos.model.TerminalInfo;
import cn.acewill.mobile.pos.model.TerminalVersion;
import cn.acewill.mobile.pos.model.WorkShift;
import cn.acewill.mobile.pos.model.WorkShiftNewReport;
import cn.acewill.mobile.pos.model.WorkShiftReport;
import cn.acewill.mobile.pos.model.dish.Cart;
import cn.acewill.mobile.pos.model.event.PosEvent;
import cn.acewill.mobile.pos.model.order.MarketingActivity;
import cn.acewill.mobile.pos.model.order.Order;
import cn.acewill.mobile.pos.model.payment.Payment;
import cn.acewill.mobile.pos.model.user.UserData;
import cn.acewill.mobile.pos.printer.Printer;
import cn.acewill.mobile.pos.printer.PrinterTemplates;
import cn.acewill.mobile.pos.printer.usb.GpUsbPrinter;
import cn.acewill.mobile.pos.printer.usb.UsbPrinter;
import cn.acewill.mobile.pos.receivenetorder.UpLoadOrderService;
import cn.acewill.mobile.pos.service.DialogCallback;
import cn.acewill.mobile.pos.service.DishService;
import cn.acewill.mobile.pos.service.OrderService;
import cn.acewill.mobile.pos.service.PosInfo;
import cn.acewill.mobile.pos.service.ResultCallback;
import cn.acewill.mobile.pos.service.StoreBusinessService;
import cn.acewill.mobile.pos.service.SystemService;
import cn.acewill.mobile.pos.service.retrofit.response.PosResponse;
import cn.acewill.mobile.pos.ui.adapter.DefinitionAdp;
import cn.acewill.mobile.pos.ui.fragment.OrderDayFragment;
import cn.acewill.mobile.pos.ui.fragment.ReportFragment;
import cn.acewill.mobile.pos.ui.fragment.RightMenuFragment;
import cn.acewill.mobile.pos.utils.Constant;
import cn.acewill.mobile.pos.utils.DialogUtil;
import cn.acewill.mobile.pos.utils.DownUtlis;
import cn.acewill.mobile.pos.utils.TimeUtil;
import cn.acewill.mobile.pos.utils.ToolsUtils;
import cn.acewill.mobile.pos.widget.CommonEditText;
import cn.acewill.mobile.pos.widget.ProgressDialogF;
import cn.acewill.mobile.pos.widget.ScrolGridView;

/**
 * Created by DHH on 2016/6/12.
 */
public class OrderDishMainAty extends BaseActivity {

	@BindView(R.id.main_order_day)
	LinearLayout mainOrderDay;
	@BindView(R.id.main_sys_set)
	LinearLayout mainSysSet;
	@BindView(R.id.radioGroup)
	LinearLayout radioGroup;
	@BindView(R.id.line_view)
	View         lineView;
	@BindView(R.id.main_create_order)
	ImageView    mainCreateOrder;
	@BindView(R.id.content)
	FrameLayout  content;
	@BindView(R.id.right_menu_ll)
	FrameLayout  rightMenuLl;
	@BindView(R.id.drawer_layout)
	DrawerLayout drawerLayout;

	private OrderDayFragment orderDayFragment;
	private ReportFragment   reportFragment;
	private Integer          workshiftId;
	private UserData         mUserData;
	private Handler          timerHandler;
	private Runnable         timerRunnable;

	private Intent intent;

	private DishService           dishService;
	private GpUsbPrinter          usbPrinter;
	private Handler               handler;
	private PowerManager.WakeLock wakeLock;
	private Store                 store;
	private PosInfo               posInfo;
	private ProgressDialogF       progressDialog;
	private SystemService         systemService;
	private int checkOutPrintCounts = 1;//结账单打印张数
	private int guestReceiptCounts  = 1;//客用小票打印张数
	//    private TimerTaskController timerTaskController;
	private PosSinUsbScreenController posSinUsbScreenController;


	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.i("onDestroy", "======================================");
		try {
			isShowNoti = false;
			//关闭轮训获取网上订单的开关
			MyApplication.getInstance().setConFirmNetOrder(false);
			EventBus.getDefault().unregister(this);
			stopService();
			if (usbPrinter != null) {
				usbPrinter.unbindService();
			}
			if (timer != null) {
				timer.cancel();
			}
			DishDataController.cleanDishDate();
			DishDataController.cleanDishMarkMap();
			NetOrderController.cleanNetOrderData();
			PrinterDataController.cleanPrinterData();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//监听back键,弹出退出提示dialog
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			DialogUtil.ordinaryDialog(context, ToolsUtils.returnXMLStr("exit"), ToolsUtils
					.returnXMLStr("whether_to_exit"), new DialogCallback() {
				@Override
				public void onConfirm() {
					myApplication.exit();
				}

				@Override
				public void onCancle() {

				}
			});
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ButterKnife.bind(this);
		MyApplication.getInstance().setContext(context);
		myApplication.addPage(OrderDishMainAty.this);
		EventBus.getDefault().register(this);
		mUserData = UserData.getInstance(context);
		posSinUsbScreenController = PosSinUsbScreenController.getInstance();
		initService();
		loadData();
		getWorkShiftDefinition();
		initPosData();
	}

	/**
	 * 获取班次的接口
	 */
	private void getWorkShiftDefinition() {
		try {
			StoreBusinessService storeBusinessService = StoreBusinessService.getInstance();
			storeBusinessService.getWorkShiftDefinition(new ResultCallback<List<Definition>>() {
				@Override
				public void onResult(List<Definition> result) {
					if (result != null && result.size() > 0) {
						PosInfo posInfo = PosInfo.getInstance();
						posInfo.setDefinitionList(result);
						crateExchangeWork(result);
						checkWorkShift(mUserData.getUserName());
						System.out.println(mUserData.getUserName() + " ===username");
					} else {
						errorReturnLogin();
						showToast(ToolsUtils.returnXMLStr("not_workshift_not_use_pos"));
						Log.i("无法找到门店对应的班次定义,无法使用收银系统!", "");
					}
				}

				@Override
				public void onError(PosServiceException e) {
					errorReturnLogin();
					showToast(ToolsUtils.returnXMLStr("not_workshift_not_use_pos"));
					Log.i("无法找到门店对应的班次定义,无法使用收银系统!", "");
				}
			});
		} catch (PosServiceException e) {
			e.printStackTrace();
			errorReturnLogin();
			showToast(ToolsUtils.returnXMLStr("not_workshift_not_use_pos"));
			Log.i("无法找到门店对应的班次定义,无法使用收银系统!", "");
		}
	}

	private Dialog         dialog;
	private TextView       title;
	private ScrolGridView  gv_definition;
	private LinearLayout   lin_select_work;
	private CommonEditText ed_standby_money;
	private TextView       negativeButton, positiveButton;
	private        int selectDefinitionIndex = 0;//选中的班次 默认为0 默认选中第一个
	private static int definitionId          = -1;//班次定义的id，
	private String        definitionName;
	private DefinitionAdp definitionAdp;

	/**
	 * 初始化开班dialog
	 *
	 * @return
	 */
	private Dialog crateExchangeWork(List<Definition> result) {
		dialog = DialogUtil
				.getDialog(context, R.layout.dialog_ordinary, 9, LinearLayout.LayoutParams.WRAP_CONTENT);
		dialog.setContentView(R.layout.dialog_work_shift);
		title = (TextView) dialog.findViewById(R.id.print_title);
		lin_select_work = (LinearLayout) dialog.findViewById(R.id.lin_select_work);
		gv_definition = (ScrolGridView) dialog.findViewById(R.id.gv_definition);
		LinearLayout print_close_ll = (LinearLayout) dialog.findViewById(R.id.print_close_ll);
		LinearLayout lin_money      = (LinearLayout) dialog.findViewById(R.id.lin_money);
		ed_standby_money = (CommonEditText) dialog.findViewById(R.id.ed_standby_money);
		negativeButton = (TextView) dialog.findViewById(R.id.print_ok);
		positiveButton = (TextView) dialog.findViewById(R.id.print_cancle);
		dialog.setCancelable(false);
		lin_money.setVisibility(View.VISIBLE);
		definitionId = result.get(selectDefinitionIndex).getId();//设置默认的班次Id
		definitionName = result.get(selectDefinitionIndex).getName();//设置默认的班次名称
		definitionAdp = new DefinitionAdp(context);
		definitionAdp.setData(result);
		definitionAdp.setPosition(selectDefinitionIndex);
		gv_definition.setAdapter(definitionAdp);
		print_close_ll.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ToolsUtils.writeUserOperationRecords("关闭开班窗口");
				dialog.dismiss();
				errorReturnLogin();
				showToast(ToolsUtils.returnXMLStr("not_setting_not_use_pos"));
			}
		});

		/**
		 * 取消
		 */
		positiveButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ToolsUtils.writeUserOperationRecords("取消开班");
				dialog.dismiss();
				errorReturnLogin();
				showToast(ToolsUtils.returnXMLStr("not_setting_not_use_pos"));
			}
		});
		return dialog;
	}

	/**
	 * 判断是否要交班
	 *
	 * @param userName
	 */
	private void checkWorkShift(String userName) {
		try {
			StoreBusinessService storeBusinessService = StoreBusinessService.getInstance();
			final PosInfo        posInfo              = PosInfo.getInstance();
			storeBusinessService.getOpenWorkShift(userName, posInfo
					.getTerminalId(), new ResultCallback<WorkShift>() {
				@Override
				public void onResult(WorkShift result) {
					if (result != null) {
						workshiftId = result.getDefinitionId();
						Integer workShiftId = Integer.valueOf(String.valueOf(result.getId()));
						Integer definitionId = Integer
								.valueOf(String.valueOf(result.getDefinitionId()));
						posInfo.setDefinition(getWorkShiftInfo(definitionId));
						posInfo.setWorkShiftId(workShiftId);
						posInfo.setWorkShiftName(result.getDefinitionName());
						showOrderDishMain();

						showMainActivity();
						Log.e("workshiftId", workShiftId + "");
					} else {
						if (dialog != null) {
							startWorkShift();
							dialog.show();
						} else {
							errorReturnLogin();
							showToast(ToolsUtils.returnXMLStr("not_workshift_not_use_pos"));
							Log.i("获取门店对应的班次信息失败", "");
						}
					}
				}

				@Override
				public void onError(PosServiceException e) {
					e.printStackTrace();
					errorReturnLogin();
					showToast(ToolsUtils.returnXMLStr("not_workshift_not_use_pos"));
					Log.i("获取门店对应的班次信息失败", e.getMessage());
				}
			});
		} catch (PosServiceException e) {
			e.printStackTrace();
			errorReturnLogin();
			showToast(ToolsUtils.returnXMLStr("not_workshift_not_use_pos"));
			Log.i("获取门店对应的班次信息失败", e.getMessage());
		}
	}

	/**
	 * 是否开班的按钮事件监听
	 */
	private void startWorkShift() {
		lin_select_work.setVisibility(View.VISIBLE);
		title.setText(ToolsUtils.returnXMLStr("open_classes"));

		gv_definition.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				selectDefinitionIndex = position;
				Definition definition = (Definition) definitionAdp.getItem(position);
				definitionAdp.setPosition(position);
				if (definition != null) {
					definitionId = definition.getId();
					definitionName = definition.getName();
				}
			}
		});
		/**
		 * 确定
		 */
		negativeButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				ToolsUtils.writeUserOperationRecords("确定开班按钮");
				dialog.dismiss();
				String standbyMoney = ed_standby_money.getText().toString().trim();
				if (TextUtils.isEmpty(standbyMoney)) {
					standbyMoney = "0.0";
				}
				try {
					UserData             mUserData            = UserData.getInstance(context);
					StoreBusinessService storeBusinessService = StoreBusinessService.getInstance();
					final PosInfo        posinfo              = PosInfo.getInstance();
					WorkShift            workShift            = new WorkShift();
					workShift.setDefinitionId(definitionId);
					workShift.setDefinitionName(definitionName);
					workShift.setUserId(1);
					workShift.setTerminalName(posinfo.getTerminalName());
					workShift.setUserName(mUserData.getUserName());
					workShift.setSpareCash(new BigDecimal(standbyMoney));
					workShift.setStartTime(System.currentTimeMillis());
					workShift.setTerminalId(Integer.valueOf(posinfo.getTerminalId()));

					storeBusinessService.startWorkShift(workShift, new ResultCallback<WorkShift>() {
						@Override
						public void onResult(WorkShift result) {
							Integer workShiftId = Integer.valueOf(String.valueOf(result.getId()));
							Integer definitionId = Integer
									.valueOf(String.valueOf(result.getDefinitionId()));
							posinfo.setDefinition(getWorkShiftInfo(definitionId));
							posinfo.setWorkShiftId(workShiftId);
							posinfo.setWorkShiftName(result.getDefinitionName());

							showMainActivity();
						}

						@Override
						public void onError(PosServiceException e) {
							getWorkShiftDefinition();
							myApplication.ShowToast(ToolsUtils
									.returnXMLStr("open_classes_error_try_again") + e.getMessage());
							Log.i("开班失败", e.getMessage());
						}
					});
				} catch (PosServiceException e) {
					e.printStackTrace();
					Log.i("开班失败", e.getMessage());
				}
			}
		});
	}

	/**
	 * 显示点菜界面
	 */
	private void showOrderDishMain() {
		UserData mUserData = UserData.getInstance(context);
		mUserData.setWorkShifts(true);
	}

	/**
	 * 得到当前班次信息对象
	 *
	 * @param definitionId
	 * @return
	 */
	private Definition getWorkShiftInfo(Integer definitionId) {
		PosInfo          posInfo        = PosInfo.getInstance();
		List<Definition> definitionList = posInfo.getDefinitionList();
		for (Definition definition : definitionList) {
			if (definition.getId() == definitionId) {
				return definition;
			}
		}
		return null;
	}

	/**
	 * 当出错时,退回到LoginAty重新登录
	 */
	private void errorReturnLogin() {
		myApplication.soundPool.stop(1);
		myApplication.clean();
		Intent orderIntent = new Intent(OrderDishMainAty.this, LoginAty.class);
		orderIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
		startActivity(orderIntent);
	}


	boolean isShowNoti = false;


	private void loadData() {
		intent = new Intent();
		posInfo = PosInfo.getInstance();
		mUserData = UserData.getInstance(context);
		store = Store.getInstance(context);
		progressDialog = new ProgressDialogF(this);
		String brandNameStr = "";
		if (StoreInfor.terminalInfo != null) {
			TerminalInfo terminalInfo = StoreInfor.terminalInfo;
			String brandName = TextUtils.isEmpty(terminalInfo.brandName) ? ToolsUtils
					.returnXMLStr("wisdom_cash_register") : terminalInfo.brandName;
			String storeName = TextUtils.isEmpty(terminalInfo.sname) ? ToolsUtils
					.returnXMLStr("acewill_cloud_pos") : terminalInfo.sname;
			if (TextUtils.isEmpty(terminalInfo.sname)) {
				//                tvMainTitle.setText(brandName);
				brandNameStr = brandName;
			}
			brandNameStr = brandName + "-" + storeName;
			//            tvMainTitle.setText(brandName + "-" + storeName);
		}
		posInfo.setBrandName(brandNameStr.trim());
		try {
			systemService = SystemService.getInstance();
			dishService = DishService.getInstance();
		} catch (PosServiceException e) {
			e.printStackTrace();
		}

		//        //初始化网络订单角标提示
		//        badeView = new BadgeView(this, tvNetorder);
		//        badeView.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
		//        badeView.setTextColor(Color.WHITE);
		//        badeView.setBadgeBackgroundColor(Color.RED);
		//        badeView.setTextSize(12);
		//        badeView.hide();

		//        mainPopWindow = new MainPopWindow(context, PowerController.getSelectPopList());
		//        mainPopWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
		//            @Override
		//            public void onDismiss() {
		//                backgroundAlpha(1f);
		//            }
		//        });

		if (StoreInfor.printConfiguration != null && StoreInfor.printConfiguration
				.getCheckoutReceiptCounts() >= 0) {
			checkOutPrintCounts = StoreInfor.printConfiguration.getCheckoutReceiptCounts();
			posInfo.setCheckoutReceiptCounts(checkOutPrintCounts);
		}
		if (StoreInfor.printConfiguration != null && StoreInfor.printConfiguration
				.getGuestReceiptCounts() >= 0) {
			guestReceiptCounts = StoreInfor.printConfiguration.getGuestReceiptCounts();
			posInfo.setGuestReceiptCounts(guestReceiptCounts);
		}
		if (StoreInfor.marketingActivities != null && StoreInfor.marketingActivities.size() > 0) {
			StoreInfor.marketingActivities.clear();
		}

	}

	/**
	 * 获取商家LOGO图片
	 */
	private void getLogoPath() {
		try {
			SystemService systemService = SystemService.getInstance();
			systemService.getLogoPath(new ResultCallback() {
				@Override
				public void onResult(Object result) {
					String path = (String) result;
					if (!TextUtils.isEmpty(path)) {
						PosInfo posInfo = PosInfo.getInstance();
						posInfo.setLogoPath(path);
					}
				}

				@Override
				public void onError(PosServiceException e) {
					showToast(ToolsUtils.returnXMLStr("get_logo_failure") + "!" + e.getMessage());
				}
			});
		} catch (PosServiceException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取手动全单营销活动 手动
	 */
	private void getMarketList() {
		try {
			DishService dishService = DishService.getInstance();
			dishService.getMarketingActivityList(new ResultCallback<List<MarketingActivity>>() {
				@Override
				public void onResult(List<MarketingActivity> result) {
					if (result != null && result.size() > 0) {
						StoreInfor.marketingActivities = result;
					} else {
						Log.i("获取营销活动为空", "");
					}
				}

				@Override
				public void onError(PosServiceException e) {
					e.printStackTrace();
					Log.i("获取营销活动失败", e.getMessage());
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
			Log.i("获取营销活动失败", e.getMessage());
		}
	}

	/**
	 * 获取门店营销活动列表 自动
	 */
	private void getStoreMarket() {
		try {
			SystemService systemService = SystemService.getInstance();
			systemService.getStoreMarket(new ResultCallback<List<Market>>() {
				@Override
				public void onResult(List<Market> result) {
					if (result != null) {
						MarketDataController.setMarketList(result);
					}
				}

				@Override
				public void onError(PosServiceException e) {
					Log.i("营销活动获取失败,", e.getMessage());
				}
			});
		} catch (PosServiceException e) {
			e.printStackTrace();
		}
	}

	private void showMainActivity() {
		initView();
	}

	/**
	 * 初始化POS的数据
	 */
	private void initPosData() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				getLogoPath();
				getMarketList();
				getStoreMarket();
				getPrinterList();
				getKDSList();
				getKitchenStalls();
				getAllTemplates();
				getOrderDiscountTypes();
				getPayType();
			}
		}).start();
	}
	/**
	 * 获取菜品分类数据
	 */
	//	private void getKindInfo() {
	//		progressDialog.showLoading("");
	//		DishService dishService = null;
	//		try {
	//			dishService = DishService.getInstance();
	//		} catch (PosServiceException e) {
	//			e.printStackTrace();
	//			return;
	//		}
	//		dishService.getKindDataInfo(new ResultCallback<List<DishType>>() {
	//			@Override
	//			public void onResult(List<DishType> result) {
	//				progressDialog.disLoading();
	//				if (result != null && result.size() > 0) {
	//					DishDataController.dishKindList = result;
	//					getDishInfo();
	//				} else {
	//					showToast(ToolsUtils.returnXMLStr("get_dish_kind_is_null"));
	//					Log.i("获取菜品分类为空", "");
	//				}
	//			}
	//
	//			@Override
	//			public void onError(PosServiceException e) {
	//				showToast(ToolsUtils.returnXMLStr("get_dish_kind_error") + "," + e.getMessage());
	//				Log.i("获取菜品分类为空", e.getMessage());
	//			}
	//		});
	//	}

	/**
	 * 得到菜品数据  dishList
	 */
	//	private void getDishInfo() {
	//		DishService dishService = null;
	//		try {
	//			dishService = DishService.getInstance();
	//		} catch (PosServiceException e) {
	//			e.printStackTrace();
	//			return;
	//		}
	//		dishService.getDishList(new ResultCallback<List<Menu>>() {
	//			@Override
	//			public void onResult(List<Menu> result) {
	//				progressDialog.disLoading();
	//				if (result != null && result.size() > 0) {
	//					DishDataController.setDishData(result);
	//				}
	//			}
	//
	//			@Override
	//			public void onError(PosServiceException e) {
	//				showToast(e.getMessage());
	//				Log.i("获取菜品为空", e.getMessage());
	//				progressDialog.disLoading();
	//			}
	//		});
	//	}

	/**
	 * 获取全单的折扣信息列表
	 */
	private void getOrderDiscountTypes() {
		try {
			SystemService systemService = SystemService.getInstance();
			systemService.getOrderDiscountTypes(new ResultCallback<List<Discount>>() {
				@Override
				public void onResult(List<Discount> result) {
					if (result != null && result.size() > 0) {
						MarketDataController.setDisCountList(result);
					}
				}

				@Override
				public void onError(PosServiceException e) {
					Log.i("获取全单的折扣信息列表失败,", e.getMessage());
				}
			});
		} catch (PosServiceException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取全部打印模板
	 */
	private void getAllTemplates() {
		try {
			SystemService systemService = SystemService.getInstance();
			systemService.getAllTemplates(new ResultCallback<List<PrinterTemplates>>() {
				@Override
				public void onResult(List<PrinterTemplates> result) {
					if (result != null && result.size() > 0) {
						PrinterDataController.setPrinterTemplatesList(result);
					}
				}

				@Override
				public void onError(PosServiceException e) {
					Log.i("获取打印方案失败失败,", e.getMessage());
				}
			});
		} catch (PosServiceException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取门店档口列表信息
	 */
	private void getKitchenStalls() {
		try {
			SystemService systemService = SystemService.getInstance();
			systemService.getKitchenStalls(new ResultCallback<List<KitchenStall>>() {
				@Override
				public void onResult(List<KitchenStall> result) {
					if (result != null) {
						Log.i("打印机档口列表", ToolsUtils.getPrinterSth(result));
						PrinterDataController.setKitchenStallList(result);
						//映射菜品与菜品档口数据
						PrinterDataController.handleKitchenStall();
					}
				}

				@Override
				public void onError(PosServiceException e) {
					Log.i("打印机档口列表获取失败,", e.getMessage());
				}
			});
		} catch (PosServiceException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取门店打印机列表
	 */
	private void getPrinterList() {
		try {
			PrinterDataController.cleanPrinterData();
			SystemService systemService = SystemService.getInstance();
			systemService.getPrinterList(new ResultCallback<List<Printer>>() {
				@Override
				public void onResult(List<Printer> result) {
					if (result != null) {
						Integer cashPrinterId      = store.getCashPrinterId();
						Integer secondartPrinterId = store.getSecondaryPrinterId();
						Integer takeoutPrinterId   = store.getTakeoutPrinterId();
						PrinterDataController
								.setPrinterList(result, cashPrinterId, secondartPrinterId, takeoutPrinterId);
					}
				}

				@Override
				public void onError(PosServiceException e) {
					Log.i("打印机列表获取失败,", e.getMessage());
				}
			});
		} catch (PosServiceException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取KDS列表
	 */
	private void getKDSList() {
		try {
			SystemService systemService = SystemService.getInstance();
			systemService.getKDSList(new ResultCallback<List<KDS>>() {
				@Override
				public void onResult(List<KDS> result) {
					if (result != null) {
						PrinterDataController.setKdsList(result);
					}
				}

				@Override
				public void onError(PosServiceException e) {
					Log.i("打印机列表获取失败,", e.getMessage());
				}
			});
		} catch (PosServiceException e) {
			e.printStackTrace();
		}
	}

	private void initView() {
		changeTab(0);
		mainOrderDay.setSelected(true);
		RightMenuFragment rightMenuFragment = new RightMenuFragment();
		getFragmentManager().beginTransaction().replace(R.id.right_menu_ll, rightMenuFragment)
				.commit();

		//侧滑栏开始先禁止滑动
		drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
		drawerLayout.setDrawerListener(new DrawerLayout.DrawerListener() {
			@Override
			public void onDrawerSlide(View drawerView, float slideOffset) {
			}

			@Override
			public void onDrawerOpened(View drawerView) {
				drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
			}

			@Override
			public void onDrawerClosed(View drawerView) {
				drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
			}

			@Override
			public void onDrawerStateChanged(int newState) {
			}
		});
	}

	private void changeTab(int tab) {
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

		switch (tab) {
			case 0:
				if (orderDayFragment == null) {
					orderDayFragment = new OrderDayFragment();
					orderDayFragment.setCallBack(new DialogOkCallBack() {
						@Override
						public void onOk() {
							openMenu();
						}
					});
					ft.add(R.id.content, orderDayFragment);
				} else {
					ft.show(orderDayFragment);
				}
				if (reportFragment != null) {
					ft.hide(reportFragment);
				}
				break;
			case 1:
				if (reportFragment == null) {
					reportFragment = new ReportFragment();
					reportFragment.setCallBack(new DialogOkCallBack() {
						@Override
						public void onOk() {
							openMenu();
						}
					});
					ft.add(R.id.content, reportFragment);
				} else {
					ft.show(reportFragment);
				}
				if (orderDayFragment != null) {
					ft.hide(orderDayFragment);
				}
				break;
		}
		ft.commit();
	}

	//打开右侧侧滑菜单
	public void openMenu() {
		drawerLayout.openDrawer(rightMenuLl);
	}

	//关闭右侧侧滑菜单
	public void closeMenu() {
		drawerLayout.closeDrawers();
	}

	@OnClick({R.id.main_order_day, R.id.main_sys_set, R.id.main_create_order})
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.main_order_day://当日订单
				ToolsUtils.writeUserOperationRecords("当日订单");
				mainOrderDay.setSelected(true);
				mainSysSet.setSelected(false);
				changeTab(0);
				break;
			case R.id.main_sys_set://系统设置
				ToolsUtils.writeUserOperationRecords("系统设置");
				mainOrderDay.setSelected(false);
				mainSysSet.setSelected(true);
				changeTab(1);
				break;
			case R.id.main_create_order://创建订单
				if (posInfo != null) {
					posInfo.setAccountMember(null);
				}
				//                if(isHaveMemberPay)
				//                {
				//                    DialogUtil.memberVerDialog(context, new DialogCallBack() {
				//                        @Override
				//                        public void onOk() {
				//                            ToolsUtils.writeUserOperationRecords("有会员 == 》创建订单");
				//                            jumpCreateOrder();
				//                        }
				//
				//                        @Override
				//                        public void onCancle() {
				//                            ToolsUtils.writeUserOperationRecords("非会员 == 》创建订单");
				//                            jumpCreateOrder();
				//                        }
				//                    });
				//                }
				//                else{
				jumpCreateOrder();
				//                }

				break;
		}
	}

	private void jumpCreateOrder() {
		Intent intent = new Intent(context, CreateOrderAty.class);
		startActivity(intent);
	}


	/**
	 * 跳转到Login界面
	 */
	private void jumpLogin() {
		ToolsUtils.writeUserOperationRecords("跳转到login界面");
		UserData mUserData = UserData.getInstance(context);
		mUserData.setUserName("");
		mUserData.setPwd("");
		mUserData.setSaveStated(false);
		errorReturnLogin();
	}

	/**
	 * 获取班次交接报表数据
	 *
	 * @param workShiftId 班次Id
	 */
	private void workShiftReport(Integer workShiftId, String endWorkAmount, final boolean isJumpLogin) {
		try {
			SystemService systemService = SystemService.getInstance();
			if (TextUtils.isEmpty(endWorkAmount)) {
				endWorkAmount = "0";
			}
			systemService
					.workShiftReport(workShiftId, endWorkAmount, new ResultCallback<WorkShiftNewReport>() {
						@Override
						public void onResult(final WorkShiftNewReport result) {
							Log.i("获取交接班报表数据==>>", ToolsUtils.getPrinterSth(result));
							printWorkShift(result, isJumpLogin);
						}

						@Override
						public void onError(PosServiceException e) {
							if (isJumpLogin) {
								posInfo.getInstance().setWorkShiftId(null);
							}
							showToast(ToolsUtils
									.returnXMLStr("get_workshift_report_data_error") + "," + e
									.getMessage());
							Log.i("获取交接班报表数据失败", e.getMessage());
						}
					});
		} catch (PosServiceException e) {
			e.printStackTrace();
			if (isJumpLogin) {
				posInfo.getInstance().setWorkShiftId(null);
			}
			Log.i("获取交接班报表数据失败", e.getMessage());
		}
	}

	/**
	 * 交接班打印
	 *
	 * @param result
	 */
	private WorkShiftNewReport workShiftReport;

	/**
	 * 打印交接班(日结)记录
	 *
	 * @param result
	 * @param isJumpLogin
	 */
	private void printWorkShift(final WorkShiftNewReport result, final boolean isJumpLogin) {
		if (result != null) {
			if (isJumpLogin) {
				workShiftReport = result;
				posInfo.getInstance().setWorkShiftId(null);
			}
			if (PrinterDataController.getInstance().getReceiptPrinter() == null) {
				PrinterDataController.getInstance()
						.printWorkShift(result, PowerController.SHIFT_WORK);
			} else {
				new Thread(new Runnable() {
					@Override
					public void run() {
						PrinterDataController.getInstance()
								.printWorkShift(result, PowerController.SHIFT_WORK);
					}
				}).start();//启动打印线程
			}
			if (isJumpLogin) {
				jumpLogin();
			}
		}
	}

	/**
	 * 打印日结小票
	 *
	 * @param result
	 */
	WorkShiftReport dailyReport;

	private void printDailyReport(final WorkShiftReport result) {
		if (result != null) {
			dailyReport = result;
			showToast("正在打印日结信息,请稍等...");
			if (PrinterDataController.getInstance().getReceiptPrinter() == null) {
				PrinterDataController.getInstance().printWorkShift(result, PowerController.DAILY);
			} else {
				new Thread(new Runnable() {
					@Override
					public void run() {
						PrinterDataController.getInstance()
								.printWorkShift(result, PowerController.DAILY);
					}
				}).start();//启动打印线程
			}
			jumpLogin();
		}
	}

	private Dialog         dialogEnd;
	private TextView       titleEnd;
	private TextView       tv_standby_money;
	private CommonEditText ed_standby_moneyEnd;
	private LinearLayout   ed_lin_select_work;
	private TextView       negativeButtonEnd, positiveButtonEnd;

	/**
	 * 初始化交班dialog
	 *
	 * @return
	 */
	private Dialog createEndWorkShift() {
		dialogEnd = DialogUtil
				.getDialogShow(context, R.layout.dialog_ordinary, 0.9f, 0.25f, false, true);
		dialogEnd.setContentView(R.layout.dialog_work_shift);
		titleEnd = (TextView) dialogEnd.findViewById(R.id.print_title);
		ed_standby_moneyEnd = (CommonEditText) dialogEnd.findViewById(R.id.ed_standby_money);
		tv_standby_money = (TextView) dialogEnd.findViewById(R.id.tv_standby_money);
		negativeButtonEnd = (TextView) dialogEnd.findViewById(R.id.print_ok);
		positiveButtonEnd = (TextView) dialogEnd.findViewById(R.id.print_cancle);
		LinearLayout print_close_ll = (LinearLayout) dialogEnd
				.findViewById(R.id.print_close_ll);
		LinearLayout ed_lin_select_work = (LinearLayout) dialogEnd
				.findViewById(R.id.lin_select_work);
		LinearLayout lin_money = (LinearLayout) dialogEnd.findViewById(R.id.lin_money);
		ed_lin_select_work.setVisibility(View.GONE);
		lin_money.setVisibility(View.VISIBLE);
		print_close_ll.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ToolsUtils.writeUserOperationRecords("关闭交班窗口");
				dialogEnd.dismiss();
			}
		});

		/**
		 * 取消
		 */
		positiveButtonEnd.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ToolsUtils.writeUserOperationRecords("取消交班窗口");
				dialogEnd.dismiss();
			}
		});
		return dialogEnd;
	}

	/**
	 * 交班
	 */
	private void workShift() {
		if (dialogEnd == null) {
			createEndWorkShift();
		}
		PrinterDataController.getInstance().openCashBox();
		endWorkShift();
		dialogEnd.show();
	}

	/**
	 * 交班
	 */
	private void endWorkShift() {
		titleEnd.setText(ToolsUtils.returnXMLStr("shift"));
		lin_select_work.setVisibility(View.GONE);
		ed_standby_moneyEnd.setHint(ToolsUtils.returnXMLStr("please_input_cash_box_money"));
		tv_standby_money.setText(ToolsUtils.returnXMLStr("cash_box_money"));

		/**
		 * 确定
		 */
		negativeButtonEnd.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ToolsUtils.writeUserOperationRecords(ToolsUtils.returnXMLStr("confirm_shift_menu"));
				dialogEnd.dismiss();
				String standbyMoney = ed_standby_moneyEnd.getText().toString().trim();
				if (TextUtils.isEmpty(standbyMoney)) {
					standbyMoney = "0.0";
				}
				try {
					StoreBusinessService storeBusinessService = StoreBusinessService.getInstance();
					WorkShift            workShift            = new WorkShift();
					workShift.setCashRevenue(new BigDecimal(standbyMoney));
					workShift.setEndTime(System.currentTimeMillis());
					PosInfo      posInfo           = PosInfo.getInstance();
					final String finalStandbyMoney = standbyMoney;
					storeBusinessService.endWorkShift(new Long(posInfo
							.getWorkShiftId()), workShift, new ResultCallback<Integer>() {
						@Override
						public void onResult(Integer result) {
							showToast(ToolsUtils.returnXMLStr("shift_success"));
							PosInfo posInfo = PosInfo.getInstance();
							workShiftReport(posInfo.getWorkShiftId(), finalStandbyMoney, true);
						}

						@Override
						public void onError(PosServiceException e) {
							myApplication.ShowToast(ToolsUtils
									.returnXMLStr("shift_failure_try_again") + e.getMessage());
							Log.i("交班失败", e.getMessage());
						}
					});
				} catch (PosServiceException e) {
					e.printStackTrace();
					Log.i("交班失败", e.getMessage());
				}
			}
		});
	}

	/**
	 * 检查更新
	 */
	private void checkUpdate() {
		final ProgressDialogF progressDialogF = new ProgressDialogF(this);
		progressDialogF.showLoading("");
		systemService.getTerminalVersions(new ResultCallback<TerminalVersion>() {
			@Override
			public void onResult(TerminalVersion result) {
				progressDialogF.disLoading();
				if (result != null) {
					if (Integer.valueOf(result.getVersion()) > MyApplication.getInstance()
							.getVersionCode()) {
						//下载文件
						DownUtlis.getInstance(context)
								.upDataDialog(result.getDescription(), result.getFilename());
					} else {
						MyApplication.getInstance()
								.ShowToast(ToolsUtils.returnXMLStr("is_the_latest_version"));
					}
				} else {
					MyApplication.getInstance()
							.ShowToast(ToolsUtils.returnXMLStr("get_version_info_failure"));
				}
			}

			@Override
			public void onError(PosServiceException e) {
				progressDialogF.disLoading();
				showToast(ToolsUtils.returnXMLStr("get_version_info_failure") + "!" + e
						.getMessage());
				Log.i("获取版本信息失败", e.getMessage());
			}
		});
	}

	/**
	 * 日结
	 */
	private void daySettlement() {
		dailyReport();
	}

	/**
	 * 获取门店日结信息
	 */
	private void dailyReport() {
		try {
			SystemService systemService = SystemService.getInstance();
			systemService.dailyReport(new ResultCallback<WorkShiftReport>() {
				@Override
				public void onResult(final WorkShiftReport result) {
					Log.i("获取门店日结信息", ToolsUtils.getPrinterSth(result));
					if (result != null) {
						Intent intent = new Intent();
						intent.setClass(OrderDishMainAty.this, ShowReportAty.class);
						intent.putExtra("WorkShiftReport", (Serializable) result);
						intent.putExtra("printType", PowerController.DAILY);
						startActivity(intent);
						//printDailyReport(result);
					} else {
						showToast(ToolsUtils.returnXMLStr("get_daily_data_error"));
						Log.i("获取门店日结信息为空", "");
					}
				}

				@Override
				public void onError(PosServiceException e) {
					showToast(ToolsUtils.returnXMLStr("get_daily_data_error") + "!" + e
							.getMessage());
					Log.i("获取门店日结信息失败", e.getMessage());
				}
			});
		} catch (PosServiceException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 解绑门店
	 */
	private void unBindStore() {
		DialogUtil.ordinaryDialog(context, ToolsUtils.returnXMLStr("unbind"), ToolsUtils
				.returnXMLStr("are_you_sure_unbind"), new DialogCallback() {
			@Override
			public void onConfirm() {
				String userName    = mUserData.getUserName();
				String userPwd     = mUserData.getPwd();
				String terminalMac = posInfo.getTerminalMac();
				try {
					StoreBusinessService storeBusinessService = StoreBusinessService.getInstance();
					storeBusinessService
							.unbindStore(userName, userPwd, terminalMac, new ResultCallback<Integer>() {
								@Override
								public void onResult(Integer result) {
									if (result == 0) {
										showToast(ToolsUtils.returnXMLStr("unbind_success"));
										store.setDeviceName("");
										StoreInfor.terminalInfo = null;
										jumpLogin();
									}
								}

								@Override
								public void onError(PosServiceException e) {
									showToast(ToolsUtils.returnXMLStr("unbind_failure") + e
											.getMessage());
									Log.i("解绑门店信息失败", e.getMessage());
								}
							});
				} catch (PosServiceException e) {
					e.printStackTrace();
					showToast(ToolsUtils.returnXMLStr("unbind_failure") + e.getMessage());
					Log.i("解绑门店信息失败", e.getMessage());
				}
			}

			@Override
			public void onCancle() {

			}
		});
	}

	/**
	 * 注销
	 */
	private void logOut() {
		ToolsUtils.writeUserOperationRecords("注销");
		DialogUtil.ordinaryDialog(context, ToolsUtils.returnXMLStr("sth_home_logout"), posInfo
				.getRealname() + "," + ToolsUtils
				.returnXMLStr("whether_you_want_to_exit"), new DialogCallback() {
			@Override
			public void onConfirm() {
				jumpLogin();
			}

			@Override
			public void onCancle() {

			}
		});
	}

	private final Timer timer = new Timer();//轮询打印机和KDS的连接情况
	private TimerTask task;
	private int delayedTime = 500;//延迟3秒
	private int cycleTime   = 3 * 1000;//周期循环时间

	private void SyncPrinterAndKds() {
		task = new TimerTask() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				PrinterDataController.getPrinterListState();
				PrinterDataController.getKdsListState();
			}
		};
		timer.schedule(task, delayedTime, cycleTime);
	}

	private void bindUsbPrint(boolean cashPrinterType) {
		if (cashPrinterType) {
			UsbPrinter.requestUsbPrinter(this);
			posSinUsbScreenController.init();
		} else {
			List<String> printerNameList = GpUsbPrinter.listGpUsbPrinterList(context);
			if (printerNameList.size() > 0) {
				System.out.println("===gp label printer found " + printerNameList.get(0));
				GpUsbPrinter usbPrinter = new GpUsbPrinter(context, printerNameList.get(0));
				usbPrinter.init();
				PrinterDataController.setUsbPrinter(usbPrinter);
			} else {
				System.out.println("===gp label printer not found ");
			}
		}
	}

	/**
	 * 确认打印日结报表
	 */
	private void confirmDailyPrintReport(WorkShiftReport dailyReport) {
		endDailyBusiness(dailyReport);
	}

	/**
	 * 日结打印
	 *
	 * @param workShiftReport
	 */
	private void endDailyBusiness(final WorkShiftReport workShiftReport) {
		try {
			OrderService orderService = OrderService.getInstance();
			orderService.endDailyBusiness(new ResultCallback<PosResponse>() {
				@Override
				public void onResult(PosResponse result) {
					if (result != null) {
						showToast(ToolsUtils.returnXMLStr("daily_success"));
						printDailyReport(workShiftReport);
						//                        dailyReport();
					}
				}

				@Override
				public void onError(PosServiceException e) {
					showToast(ToolsUtils.returnXMLStr("daily_failure") + "," + e.getMessage());
					Log.i("日结失败", e.getMessage());
				}
			});
		} catch (PosServiceException e) {
			e.printStackTrace();
			showToast(ToolsUtils.returnXMLStr("daily_failure") + "," + e.getMessage());
			Log.i("日结失败", e.getMessage());
		}
	}

	/**
	 * evenbus回调处理
	 *
	 * @param event
	 */
	private AlertDialog alertDialog;//Token错误的时候弹出的dialog

	@Subscribe
	public void PosEventCallBack(PosEvent event) {
		switch (event.getAction()) {
			//确认收银打印机是否是possin品牌 true是  false不是
			case Constant.EventState.CASH_PRINTER_USB:
				bindUsbPrint(event.isCashPrinterType());
				break;
			//测试KDS打印情况
			case Constant.TestPrinterState.TEST_PRINT_KDS:
				MainEvenBusController.testPrintKds(event.getKds());
				break;
			//测试打印机打印情况
			case Constant.TestPrinterState.TEST_PRINT:
				MainEvenBusController.testPrint(event.getPrinter());
				break;
			//切换用户
			case Constant.EventState.LOGOUT:
				logOut();
				break;
			//交班
			case PowerController.SHIFT_WORK:
				workShift();
				break;
			//检测更新
			case PowerController.UPDATE:
				ToolsUtils.writeUserOperationRecords("版本更新");
				checkUpdate();
				break;
			//确认打印日结报表
			case Constant.EventState.PRINT_CONFIRM_DALIY:
				WorkShiftReport workShiftReports = event.getWorkShiftReport();
				if (workShiftReports != null) {
					confirmDailyPrintReport(workShiftReports);
				}
				break;
			//日结
			case PowerController.DAILY:
				ToolsUtils.writeUserOperationRecords("日结");
				daySettlement();
				break;
			//解绑终端
			case PowerController.UNBIND_DEVICE:
				ToolsUtils.writeUserOperationRecords("解绑终端");
				unBindStore();
				break;
			//关于云POS
			case PowerController.ABOUT_CLOUDPOS:
				ToolsUtils.writeUserOperationRecords("关于云POS");
				DialogUtil.aboutCloudPos(context);
				break;
			//跳转到login界面
			case Constant.JUMP_LOGIN:
				jumpLogin();
				break;
			//发送数据到KDS 换台
			case Constant.EventState.SEND_INFO_KDS_CHANGE_TABLE:
				MainEvenBusController
						.kdsChangeOrderTable(event.getRefOrderId(), event.getTableName());
				break;
			//kds下单打印
			case Constant.EventState.SEND_INFO_KDS:
				MainEvenBusController.kdsCreatOrder(event.getOrder(), event.getTableName());
				break;
			//kds退单打印
			case Constant.EventState.SEND_INFO_KDS_REFUND_ORDER:
				MainEvenBusController.kdsDeleteOrder(event.getOrderId());
				break;
			//kds退菜打印
			case Constant.EventState.SEND_INFO_KDS_REFUND_DISH:
				MainEvenBusController.kdsDeleteDish(event.getOiList(), event.getOrderId());
				break;
			//当前时段没有菜品档案
			case Constant.EventState.CURRENT_TIME_DISH_NULL:
				showToast(ToolsUtils.returnXMLStr("current_dish_get_is_null"));
				break;
			//通过交接班历史打印交接班报表
			case Constant.EventState.PRINT_WORKE_SHIFT_HISTORY:
				workShiftReport(event.getPosition(), "", false);
				break;
			//下单打印标识
			case Constant.EventState.PRINTER_ORDER:
				ToolsUtils.writeUserOperationRecords("TIME===>下单打印客用单==" + TimeUtil
						.getStringTimeLong(System.currentTimeMillis()));
				DishOptionController.cleanCartDishMap();//下单后清除菜品的定制项缓存
				posInfo.setTableNumber("");//下单成功后将保存的餐牌信息清空
				posInfo.setCustomerAmount(1);//下单成功后将默认的顾客人数设置为1
				MainEvenBusController.printGuestOrder(store, event
						.getOrder(), guestReceiptCounts, checkOutPrintCounts);
				posInfo.setAddWaiMaiMoney(false);//订单添加过外卖配送费逻辑字段
				posInfo.setCustomer(null);//下单成功后将设置的外卖顾客信息设置为空
				posInfo.setAccountMember(null);//下单成功后将保存的微生活会员对象清空,以免影响下一次客户点餐
				posInfo.setComment("");//下单成功后将保存的订单备注信息清空
				posInfo.setBizId("");
				posInfo.setWshDealPreview(null);
				break;
			//打印结账单
			case Constant.EventState.PRINT_CHECKOUT:
				ToolsUtils.writeUserOperationRecords("TIME===>下单打印结账单==" + TimeUtil
						.getStringTimeLong(System.currentTimeMillis()));
				MainEvenBusController.printCheckOutOrder(store, event.getOrder(), event
						.getPosition(), checkOutPrintCounts);
				break;
			//打印厨房小票
			case Constant.EventState.PRINTER_KITCHEN_ORDER:
				ToolsUtils.writeUserOperationRecords("TIME===>下单打印厨房小票==" + TimeUtil
						.getStringTimeLong(System.currentTimeMillis()));
				MainEvenBusController.printKitChenOrder(event.getOrder());
				break;
			//催菜(补打)厨房
			case Constant.EventState.PRINTER_RUSH_DISH:
				MainEvenBusController.printKitChenRushDishOrder(event.getOrder());
				break;
			//厨房退菜打印
			case Constant.EventState.PRINTER_RETREAT_DISH:
				MainEvenBusController.printKitChenRetreatDishOrder(event.getOrder());
				break;
			//客用退单厨房打印
			case Constant.EventState.PRINTER_RETREAT_KITCHEN_ORDER:
				MainEvenBusController.printGuestKitchenRetreatOrder(event.getOrder());
				break;
			//客票退菜打印
			case Constant.EventState.PRINTER_RETREAT_DISH_GUEST:
				MainEvenBusController.printGuestRetreatDish(store, event
						.getOrder(), guestReceiptCounts, checkOutPrintCounts);
				break;
			//客用退单打印
			case Constant.EventState.PRINTER_RETREAT_ORDER:
				MainEvenBusController.printGuestRetreatOrder(store, event
						.getOrder(), guestReceiptCounts, checkOutPrintCounts);
				break;
			//补打下单标识
			case Constant.EventState.PRINTER_EXTRA_RECEIPT:
				MainEvenBusController.printExtraReceipt(store, event
						.getOrder(), guestReceiptCounts, checkOutPrintCounts);
				break;
			//请求超时
			case Constant.EventState.SERVER_REQUEST_TIMEOUT:
				showToast("请求超时!");
				break;
			//Token过期的标识
			case Constant.EventState.TOKEN_TIME_OUT:
				Looper.prepare();
				if (alertDialog == null) {
					alertDialog = DialogUtil
							.LoginErrorDialog(context, ToolsUtils.returnXMLStr("wrong"), ToolsUtils
									.returnXMLStr("token_abnormal_is_re_register"), new DialogCallback() {
								@Override
								public void onConfirm() {
									errorReturnLogin();
									alertDialog = null;
								}

								@Override
								public void onCancle() {
									errorReturnLogin();
									alertDialog = null;
								}
							});
				}
				Looper.loop();
				break;
			case Constant.EventState.ERR_PRINT_CASH:
				String errMessage = event.getErrMessage();
				final int cashActionId = event.getCashActionId();
				final Order order = event.getOrder();
				final int position = event.getPosition();
				Looper.prepare();
				if (alertDialog == null) {
					alertDialog = DialogUtil.LoginErrorDialog(context, errMessage, ToolsUtils
							.returnXMLStr("after_pressing_the_error"), new DialogCallback() {
						@Override
						public void onConfirm() {
							alertDialog = null;
							//交班打印出错
							if (cashActionId == PowerController.SHIFT_WORK && workShiftReport != null) {
								printWorkShift(workShiftReport, true);
							}
							//日结打印出错
							else if (cashActionId == PowerController.DAILY && dailyReport != null) {
								printDailyReport(dailyReport);
							}
							//客用打印机打印出错
							else if (cashActionId == Constant.EventState.PRINTER_ORDER) {
								MainEvenBusController
										.printGuestOrder(store, order, guestReceiptCounts, checkOutPrintCounts);
							}
							//预结小票或者是结账小票
							else if (cashActionId == Constant.EventState.ORDER_TYPE_ADVANCE || cashActionId == Constant.EventState.PRINT_CHECKOUT) {
								MainEvenBusController
										.printCheckOutOrder(store, order, position, checkOutPrintCounts);
							}
							//客票退菜打印
							else if (cashActionId == Constant.EventState.PRINTER_RETREAT_DISH_GUEST) {
								MainEvenBusController
										.printGuestRetreatDish(store, order, guestReceiptCounts, checkOutPrintCounts);
							}
							//客票退单打印
							else if (cashActionId == Constant.EventState.PRINTER_RETREAT_ORDER) {
								MainEvenBusController
										.printGuestRetreatOrder(store, order, guestReceiptCounts, checkOutPrintCounts);
							}
						}

						@Override
						public void onCancle() {
							alertDialog = null;
						}
					});
				}
				Looper.loop();
				break;
			//打印订单小票出错
			case Constant.EventState.ERR_PRINT_ORDER:
				Intent intentwel = new Intent(this, ErrPrinteAty.class);
				intentwel.putExtra("source", Constant.EventState.ERR_PRINT_ORDER);
				Bundle errBundle = new Bundle();
				errBundle.putSerializable("order", event.getOrder());
				errBundle.putSerializable("printer", event.getPrinter());
				intentwel.putExtras(errBundle);
				startActivity(intentwel);
				break;
			//打印厨房小票出错
			case Constant.EventState.ERR_PRINT_KITCHEN_ORDER:
				Intent intentKit = new Intent(this, ErrPrinteAty.class);
				intentKit.putExtra("source", Constant.EventState.ERR_PRINT_KITCHEN_ORDER);
				intentKit.putExtra("errStr", event.getTableName());
				//                Bundle errKitBundle = new Bundle();
				//                errKitBundle.putSerializable("order", event.getOrder());
				//                errKitBundle.putSerializable("oi", event.getOi());
				//                errKitBundle.putSerializable("dishPackage", event.getDishPackageItem());
				//                errKitBundle.putSerializable("printer", event.getPrinter());
				//                intentKit.putExtras(errKitBundle);
				startActivity(intentKit);
				break;
			//打印厨房总单小票出错
			case Constant.EventState.ERR_PRINT_KITCHEN_SUMMARY_ORDER:
				Intent intentKitAll = new Intent(this, ErrPrinteAty.class);
				intentKitAll
						.putExtra("source", Constant.EventState.ERR_PRINT_KITCHEN_SUMMARY_ORDER);
				intentKitAll.putParcelableArrayListExtra("oiList", (ArrayList) event.getOiList());
				Bundle errKitAllBundle = new Bundle();
				errKitAllBundle.putSerializable("order", event.getOrder());
				errKitAllBundle.putSerializable("printer", event.getPrinter());
				intentKitAll.putExtras(errKitAllBundle);
				startActivity(intentKitAll);
				break;
			//清空购物车
			case Constant.EventState.CLEAN_CART:
				PosInfo posInfo = PosInfo.getInstance();
				posInfo.setCustomer(null);
				Cart.cleanDishList();
				DishDataController.cleanDishMarkMap();
				break;
			//生成订单ID失败的错误提示标签
			case Constant.EventState.ERR_CREATE_ORDERID_FILURE:
				Intent tipsOrdIdFailure = new Intent(this, ErrTipsAty.class);
				tipsOrdIdFailure.putExtra("source", Constant.EventState.ERR_CREATE_ORDERID_FILURE);
				startActivity(tipsOrdIdFailure);
				break;
			//获取线上支付状态失败提示标签
			case Constant.EventState.ERR_GET_ONLINE_PAY_STATE_FAILURE:
				Intent tipsOnlinePay = new Intent(this, ErrTipsAty.class);
				tipsOnlinePay
						.putExtra("source", Constant.EventState.ERR_GET_ONLINE_PAY_STATE_FAILURE);
				startActivity(tipsOnlinePay);
				break;
			//获取威富通支付状态失败提示标签
			case Constant.EventState.ERR_GET_WFT_PAY_STATE:
				Intent tipsWFTPay = new Intent(this, ErrTipsAty.class);
				tipsWFTPay.putExtra("source", Constant.EventState.ERR_GET_WFT_PAY_STATE);
				startActivity(tipsWFTPay);
				break;
			//获取支付状态失败提示标签
			case Constant.EventState.ERR_GET_PAY_STATE_FAILURE:
				Intent tipsPay = new Intent(this, ErrTipsAty.class);
				tipsPay.putExtra("source", Constant.EventState.ERR_GET_PAY_STATE_FAILURE);
				startActivity(tipsPay);
				break;

		}
	}

	private UpLoadOrderService service;
	private ServiceConnection conn2 = new ServiceConnection() {


		@Override
		public void onServiceConnected(ComponentName name, IBinder binder) {
			isBound = true;
			UpLoadOrderService.MyBinder myBinder = (UpLoadOrderService.MyBinder) binder;
			service = myBinder.getService();
			Log.i("DemoLog", "ActivityA onServiceConnected");
			if (!MyApplication.isSyncNetOrderInit) {
				service.startTimer(0);
				MyApplication.isSyncNetOrderInit = true;
			}
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			isBound = false;
			Log.i("DemoLog", "ActivityA onServiceDisconnected");
		}
	};
	public boolean isBound;

	private void initService() {
		Intent intent = new Intent(this, UpLoadOrderService.class);
		intent.putExtra("from", "ScreenProtectedActivity_new");
		Log.i("DemoLog", "----------------------------------------------------------------------");
		Log.i("DemoLog", "ScreenProtectedActivity_new 执行 bindService");
		bindService(intent, conn2, BIND_AUTO_CREATE);
	}

	private void stopService() {
		unbindService(conn2);
		//		stopService(new Intent(this, UpLoadOrderService.class));
	}


	//获取支付方式列表
	private void getPayType() {
		//		if (StoreInfor.getPaymentList() != null && StoreInfor.getPaymentList().size() > 0) {
		//			initAliAndWx(StoreInfor.getPaymentList());
		//			payTypeAdapter.setData(StoreInfor.getPaymentList());
		//			return;
		//		}
		DishService dishService = null;
		try {
			dishService = DishService.getInstance();
			final DishService finalDishService = dishService;
			dishService.getPaytypeList(new ResultCallback<List<Payment>>() {
				@Override
				public void onResult(List<Payment> result) {
					if (result != null && result.size() > 0) {
						initAliAndWx(result);
						finalDishService.getCachedDishDao().savePaymentType(result);
					}
				}

				@Override
				public void onError(PosServiceException e) {
				}
			});
		} catch (PosServiceException e) {
			e.printStackTrace();
		}

	}

	//为支付宝和微信支付参数赋值
	private void initAliAndWx(List<Payment> result) {
		for (Payment payment : result) {
			if (payment.getId() == 1) {//支付宝
				AlipayConfig.APPID = payment.getAppIDs();
				AlipayConfig.key = payment.getKeyStr();
			}
			if (payment.getId() == 2) {//微信
				WXPay.APPID = payment.getAppIDs();
				WXPay.KEY = payment.getKeyStr();
				WXPay.MCH_ID = payment.getMchID();
				WXPay.APPSECRET = payment.getAppsecret();
				if (!TextUtils.isEmpty(payment.getSubMchID())) {
					WXPay.SUB_MCH_ID = payment.getSubMchID();
				}
			}
		}
	}
}
