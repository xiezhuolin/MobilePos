package cn.acewill.mobile.pos.ui.activity;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.concurrent.CopyOnWriteArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.acewill.mobile.pos.R;
import cn.acewill.mobile.pos.base.activity.BaseActivity;
import cn.acewill.mobile.pos.common.DishDataController;
import cn.acewill.mobile.pos.common.DishOptionController;
import cn.acewill.mobile.pos.common.StoreInfor;
import cn.acewill.mobile.pos.config.MyApplication;
import cn.acewill.mobile.pos.config.Store;
import cn.acewill.mobile.pos.exception.PosServiceException;
import cn.acewill.mobile.pos.interfices.DialogCallBack;
import cn.acewill.mobile.pos.interfices.DialogTCallback;
import cn.acewill.mobile.pos.interfices.DishCheckCallback;
import cn.acewill.mobile.pos.model.dish.Cart;
import cn.acewill.mobile.pos.model.dish.Dish;
import cn.acewill.mobile.pos.model.dish.DishCount;
import cn.acewill.mobile.pos.model.dish.DishType;
import cn.acewill.mobile.pos.model.dish.Menu;
import cn.acewill.mobile.pos.model.order.Order;
import cn.acewill.mobile.pos.model.payment.Payment;
import cn.acewill.mobile.pos.model.user.UserData;
import cn.acewill.mobile.pos.service.DialogCallback;
import cn.acewill.mobile.pos.service.DishService;
import cn.acewill.mobile.pos.service.PosInfo;
import cn.acewill.mobile.pos.service.ResultCallback;
import cn.acewill.mobile.pos.service.StoreBusinessService;
import cn.acewill.mobile.pos.ui.adapter.CartDishAdapter;
import cn.acewill.mobile.pos.ui.adapter.DishAdp;
import cn.acewill.mobile.pos.ui.adapter.DishKindsAdp;
import cn.acewill.mobile.pos.utils.Constant;
import cn.acewill.mobile.pos.utils.DialogUtil;
import cn.acewill.mobile.pos.utils.ToolsUtils;
import cn.acewill.mobile.pos.utils.UserAction;
import cn.acewill.mobile.pos.widget.ProgressDialogF;
import cn.acewill.mobile.pos.widget.TitleView;

import static cn.acewill.mobile.pos.common.DishDataController.dishKindList;
import static cn.acewill.mobile.pos.model.dish.Cart.getDishItemList;
import static cn.acewill.mobile.pos.utils.DialogUtil.clearCartWarn;

/**
 * 点菜页面
 * Created by Lyndon on 2018/11/03
 */
public class CreateOrderAty extends BaseActivity {
	@BindView(R.id.title_left)
	LinearLayout   titleLeft;
	@BindView(R.id.right_left_ll)
	LinearLayout   rightLeftll;
	@BindView(R.id.title_right_icon)
	LinearLayout   titleRight;
	@BindView(R.id.title_icon)
	ImageView      titleRightIcon;
	@BindView(R.id.login_title)
	TitleView      loginTitle;
	@BindView(R.id.cart_all_money)
	TextView       cartAllMoney;
	@BindView(R.id.creat_btn)
	TextView       creatBtn;
	@BindView(R.id.buttom_ll)
	LinearLayout   buttomLl;
	@BindView(R.id.dishKinds)
	ListView       dishKinds;
	@BindView(R.id.dishItems)
	ListView       dishItems;
	@BindView(R.id.top_ll)
	LinearLayout   topLl;
	@BindView(R.id.cart_btn)
	ImageView      cartBtn;
	@BindView(R.id.cart_count_tv)
	TextView       cartCountTv;
	@BindView(R.id.cart_count_rl)
	RelativeLayout cartCountRl;
	@BindView(R.id.cart_list_ll)
	LinearLayout   cart_list_ll;
	@BindView(R.id.bg_ll)
	LinearLayout   bg_ll;
	@BindView(R.id.clear_cart)
	TextView       clearCart;
	@BindView(R.id.cart_list)
	ListView       cartList;
	@BindView(R.id.parabola_iv)
	ImageView      parabola_iv;
	@BindView(R.id.search_cancle)
	TextView       searchCancle;
	@BindView(R.id.search_cotent)
	EditText       searchCotent;
	@BindView(R.id.search_clear)
	LinearLayout   searchClear;
	@BindView(R.id.search_btn)
	TextView       searchBtn;
	@BindView(R.id.search_list)
	ListView       searchListView;
	@BindView(R.id.search_ll)
	LinearLayout   searchLl;

	LinearLayout eat_in_ll, sale_out_ll, tack_out_ll;
	private Store    store;
	private UserData mUserData;
	private Intent   intent;
	private PosInfo  posInfo;
	//    private NetOrderService netOrderService;
	private boolean scaning = false;//防止扫码枪扫多次

	private ProgressDialogF progressDialog;
	private DishKindsAdp    dishKidsTopAdp;
	private DishAdp         dishAdp;
	private CartDishAdapter cartDishAdapter;
	//    private DishAdp searchAdp;
	private List<DishType>  dishKind;//菜品分类
	private Cart            cart;

	private AnimatorSet animator_up;
	private AnimatorSet animator_down;
	private Animation   scaleAnim;
	private boolean isShowing = false;
	private String  orderType = "EAT_IN";//堂食or外带or外卖
	private PopupWindow popupWindow;
	private int current_kind = 0;//当前选中菜品类别
	private String tableid;//桌台号，进入页面前输入的
	private String tableName;//送餐桌台号
	private String people;//就餐人数
	private List<Dish> searchList  = new ArrayList<>();
	private boolean    isSearching = false;//是否在搜索页面，用于抛物线处理
	private String  searchT;//搜索内容
	private Timer   timer;
	private Handler handler;
	/**
	 * 滑动page的下标位置
	 */
	private        int           currentPosition = 0;
	private        Long          orderId         = -1L;
	private        int           dishType        = 0;//类型 0：堂食，1：外带，4：外卖
	private static int           CHECKOUT        = 110;//跳转到支付界面的Activity回调
	private        String        activityName    = "";//营销活动名称
	private        List<Payment> paymentList     = new ArrayList<>();//支持方式列表
	private        List<Dish>    marketActList   = new ArrayList<>();
	private HandlerThread handThread;
	private Handler       mHandler;
	private boolean mRunning = false;

	//监听back键,弹出退出提示dialog
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			DialogUtil.ordinaryDialog(context, ToolsUtils.returnXMLStr("exit"), ToolsUtils
					.returnXMLStr("whether_to_exit"), new DialogCallback() {
				@Override
				public void onConfirm() {
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
		setContentView(R.layout.activity_create_order);
		ButterKnife.bind(this);
		myApplication.addPage(CreateOrderAty.this);
		initData();
		initView();
		cleanCart();
		initAnimator();
		//		initPayType();
		getDishCounts();
	}

	@Override
	public void onResume() {
		super.onResume();
		mRunning = true;
	}

	@Override
	protected void onStop() {
		super.onStop();
		mRunning = false;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		//销毁线程
		mHandler.removeCallbacks(mBackgroundRunnable);
	}

	private void initData() {
		intent = new Intent();
		cart = Cart.getInstance();
		store = Store.getInstance(context);
		posInfo = PosInfo.getInstance();
		mUserData = UserData.getInstance(context);
		//        netOrderService = MyApplication.getInstance().getNetOrderService();
		progressDialog = new ProgressDialogF(this);
		dishKidsTopAdp = new DishKindsAdp(context);
		cartDishAdapter = new CartDishAdapter(context);
		dishAdp = new DishAdp(context);
		cartList.setAdapter(cartDishAdapter);
		posInfo.setOrderType("EAT_IN");
		handThread = new HandlerThread("MyHandlerThread");
		handThread.start();//创建一个HandlerThread并启动它
		mHandler = new Handler(handThread
				.getLooper());//使用HandlerThread的looper对象创建Handler，如果使用默认的构造方法，很有可能阻塞UI线程
		cart = Cart.getInstance();
		if (getDishItemList() != null && getDishItemList().size() > 0) {
			if (cart != null) {
				cart.clear();
			}
		}

		//适配菜品分类数据
		dishKinds.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				currentPosition = position;
				setCurrentItem(currentPosition);
				setDishCount(cart.dishCountsList);
				dishKidsTopAdp.setSelect(currentPosition);
			}
		});


		//购物车发现变化时的监听
		cart.addListener(new Cart.ChangeListener() {
			@Override
			public void contentChanged(int type) {
				if (dishAdp != null) {
					//更新购物车数据
					if (cart.dishItemList.size() > 0) {
						mHandler.post(mBackgroundRunnable);//将线程post到Handler中
					} else {
						cartBtn.setSelected(false);
						creatBtn.setSelected(false);
						cartCountRl.setVisibility(View.GONE);
						cartCountTv.setTextSize(13);
						cartAllMoney.setText("0.0");
						DishDataController.cleanDishMarkMap();
						if (dishKidsTopAdp != null) {
							dishKidsTopAdp.notifyDataSetChanged();
						}
						if (dishAdp != null) {
							dishAdp.notifyDataSetChanged();
						}

						if (isShowing) {
							showHideCart();
						}
					}
				}

			}
		});
	}

	//实现耗时操作的线程
	Runnable mBackgroundRunnable = new Runnable() {

		@Override
		public void run() {
			marketActList = cart.getDishItemMarketActList();
			if (marketActList != null && marketActList.size() > 0) {
				synchronized (marketActList) {
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							cartDishAdapter.setDataInfo(marketActList);
							dishAdp.notifyDataSetChanged();
							dishKidsTopAdp.notifyDataSetChanged();
							int count = cart.getQuantity();
							cartBtn.setSelected(true);
							creatBtn.setSelected(true);
							cartCountRl.setVisibility(View.VISIBLE);
							cartCountTv.setText(count + "");
							BigDecimal price = new BigDecimal(cart.getCost())
									.setScale(3, BigDecimal.ROUND_DOWN);
							cartAllMoney.setText(String.format("%.2f ", price));

							if (count > 9) {
								cartCountTv.setTextSize(10);
							} else {
								cartCountTv.setTextSize(13);
							}
						}
					});
				}
			}
		}
	};


	//	private void initPayType() {
	//		getPayType();
	//	}

	private void cleanPayMentList() {
		if (paymentList != null && paymentList.size() > 0) {
			paymentList.clear();
		}
	}

	//获取支付方式列表
	//	private void getPayType() {
	//		try {
	//			cleanPayMentList();
	//			if (StoreInfor.getPaymentList() != null && StoreInfor.getPaymentList().size() > 0) {
	//				for (Payment payment : StoreInfor.getPaymentList()) {
	//					paymentList.add(payment);
	//				}
	//				initAliAndWx(paymentList);
	//				return;
	//			}
	//			DishService dishService = DishService.getInstance();
	//			dishService.getPaytypeList(new ResultCallback<List<Payment>>() {
	//
	//				@Override
	//				public void onResult(List<Payment> result) {
	//					if (result != null && result.size() > 0) {
	//						paymentList = result;
	//						initAliAndWx(result);
	//					}
	//				}
	//
	//				@Override
	//				public void onError(PosServiceException e) {
	//					Log.i("获取支付方式列表", e.getMessage());
	//				}
	//			});
	//		} catch (PosServiceException e) {
	//			e.printStackTrace();
	//			Log.i("获取支付方式列表", e.getMessage());
	//		}
	//	}

	//为支付宝和微信支付参数赋值
	//	private void initAliAndWx(List<Payment> result) {
	//		for (Payment payment : result) {
	//			if (payment.getId() == 1) {//支付宝
	//				AlipayConfig.APPID = payment.getAppIDs();
	//				AlipayConfig.key = payment.getKeyStr();
	//			}
	//			if (payment.getId() == 2) {//微信
	//				WXPay.APPID = payment.getAppIDs();
	//				WXPay.KEY = payment.getKeyStr();
	//				WXPay.MCH_ID = payment.getMchID();
	//				WXPay.APPSECRET = payment.getAppsecret();
	//				if (!TextUtils.isEmpty(payment.getSubMchID())) {
	//					WXPay.SUB_MCH_ID = payment.getSubMchID();
	//				}
	//			}
	//		}
	//	}

	private void initView() {
		//当购物车弹出后，上面变暗背景监听点击事件
		bg_ll.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:
						int y = (int) (event.getY());
						if (y < cart_list_ll.getTop()) {
							if (isShowing) {
								showHideCart();
							}
						}
						break;
				}
				return false;
			}
		});

		//初始化堂食外带
		titleRightIcon.setBackgroundResource(R.drawable.eat_in);

		View view = LayoutInflater.from(context).inflate(R.layout.pup_ordertype, null);
		eat_in_ll = (LinearLayout) view.findViewById(R.id.eat_in_ll);
		sale_out_ll = (LinearLayout) view.findViewById(R.id.sale_out_ll);
		tack_out_ll = (LinearLayout) view.findViewById(R.id.tack_out_ll);
		popupWindow = new PopupWindow(view, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
		popupWindow.setOutsideTouchable(true);
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		eat_in_ll.setSelected(true);

		eat_in_ll.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				UserAction.log("堂食", context);
				eat_in_ll.setSelected(true);
				tack_out_ll.setSelected(false);
				sale_out_ll.setSelected(false);

				titleRightIcon.setBackgroundResource(R.drawable.eat_in);
				popupWindow.dismiss();

				orderType = "EAT_IN";
				dishType = 0;

				if (!posInfo.getOrderType().equals("EAT_IN")) {
					posInfo.setOrderType("EAT_IN");
					Cart.switchEATINPrice();
				}
			}
		});
		tack_out_ll.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				UserAction.log("外带", context);
				tack_out_ll.setSelected(true);
				eat_in_ll.setSelected(false);
				sale_out_ll.setSelected(false);

				titleRightIcon.setBackgroundResource(R.drawable.take_out);
				popupWindow.dismiss();

				orderType = "TAKE_OUT";
				dishType = 1;
				if (!posInfo.getOrderType().equals("TAKE_OUT")) {
					posInfo.setOrderType("TAKE_OUT");
					Cart.switchEATINPrice();
				}
			}
		});
		sale_out_ll.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				UserAction.log("外买", context);
				sale_out_ll.setSelected(true);
				eat_in_ll.setSelected(false);
				tack_out_ll.setSelected(false);

				titleRightIcon.setBackgroundResource(R.drawable.sale_out);
				popupWindow.dismiss();

				orderType = "SALE_OUT";
				dishType = 4;

				if (!posInfo.getOrderType().equals("SALE_OUT")) {
					posInfo.setOrderType("SALE_OUT");
					Cart.switchWaiMaiPrice();
				}
			}
		});

	}

	//显示/隐藏购物车列表
	private void showHideCart() {
		if (isShowing) {
			isShowing = false;
			animator_down.start();
			cart_list_ll.setVisibility(View.GONE);
		} else {
			cart_list_ll.setVisibility(View.VISIBLE);
			isShowing = true;
			animator_up.start();
			bg_ll.setVisibility(View.VISIBLE);
		}

	}

	/**
	 * 购物车动画
	 */
	private void initAnimator() {
		animator_up = new AnimatorSet();
		animator_down = new AnimatorSet();

		//购物车按钮变大
		scaleAnim = AnimationUtils.loadAnimation(context, R.anim.scale_anim);


		//购物车列表弹出动画
		ObjectAnimator anim_up = (ObjectAnimator) AnimatorInflater
				.loadAnimator(context, R.animator.translate_up);
		ObjectAnimator anim_down = (ObjectAnimator) AnimatorInflater
				.loadAnimator(context, R.animator.translate_down);
		anim_up.setTarget(cart_list_ll);
		anim_down.setTarget(cart_list_ll);

		//背景设置透明度动画
		ObjectAnimator alpha_up = (ObjectAnimator) AnimatorInflater
				.loadAnimator(context, R.animator.alpha_up);
		ObjectAnimator alpha_down = (ObjectAnimator) AnimatorInflater
				.loadAnimator(context, R.animator.alpha_down);
		alpha_up.setTarget(bg_ll);
		alpha_down.setTarget(bg_ll);

		//动画组合
		animator_up.playTogether(anim_up, alpha_up);
		animator_down.playTogether(anim_down, alpha_down);

		animator_up.setDuration(200);
		animator_down.setDuration(200);

		animator_down.addListener(new Animator.AnimatorListener() {
			@Override
			public void onAnimationStart(Animator animation) {

			}

			@Override
			public void onAnimationEnd(Animator animation) {
				bg_ll.setVisibility(View.GONE);
			}

			@Override
			public void onAnimationCancel(Animator animation) {

			}

			@Override
			public void onAnimationRepeat(Animator animation) {

			}
		});

		anim_down.start();
	}


	public void getDishCounts() {
		try {
			StoreBusinessService storeBusinessService = StoreBusinessService.getInstance();
			storeBusinessService.getDishCounts(new ResultCallback<List<DishCount>>() {
				@Override
				public void onResult(List<DishCount> result) {
					progressDialog.disLoading();
					if (result != null && result.size() > 0) {
						cart.dishCountsList = result;
						dishAdp.setDishCount(result);
					} else {
						MyApplication.getInstance()
								.ShowToast(ToolsUtils.returnXMLStr("get_dish_sell_out_state_err"));
					}
					loadData();
				}

				@Override
				public void onError(PosServiceException e) {
					progressDialog.disLoading();
					loadData();
					Log.i("获取菜品沽清状态失败", e.getMessage());
					MyApplication.getInstance().ShowToast(ToolsUtils
							.returnXMLStr("get_dish_sell_out_state_err") + "," + e.getMessage());
				}
			});
		} catch (PosServiceException e) {
			Log.i("获取菜品沽清状态失败", e.getMessage());
			e.printStackTrace();
			MyApplication.getInstance()
					.ShowToast(ToolsUtils.returnXMLStr("get_dish_sell_out_state_err"));
		}
	}

	private void loadData() {
		if (dishKindList != null && dishKindList.size() > 0) {
			if (DishDataController.menuData != null && DishDataController.menuData.size() > 0) {
				setKidsMap(dishKindList);
				initDishAdapter();
			} else {
				getDishInfo();
			}

		} else {
			getKindInfo();
		}
	}

	/**
	 * 得到菜品数据  dishList
	 */
	private void getDishInfo() {
		DishService dishService = null;
		try {
			dishService = DishService.getInstance();
		} catch (PosServiceException e) {
			e.printStackTrace();
			return;
		}
		dishService.getDishList(new ResultCallback<List<Menu>>() {
			@Override
			public void onResult(List<Menu> result) {
				if (result != null && result.size() > 0) {
					DishDataController.setDishData(result);
					initDishAdapter();
				}
			}

			@Override
			public void onError(PosServiceException e) {
				showToast(e.getMessage());
				Log.i("获取菜品为空", e.getMessage());
			}
		});
	}

	/**
	 * 获取菜品分类数据
	 */
	private void getKindInfo() {
		progressDialog.showLoading("");
		DishService dishService = null;
		try {
			dishService = DishService.getInstance();
		} catch (PosServiceException e) {
			e.printStackTrace();
			return;
		}
		dishService.getKindDataInfo(new ResultCallback<List<DishType>>() {
			@Override
			public void onResult(List<DishType> result) {
				progressDialog.disLoading();
				if (result != null && result.size() > 0) {
					dishKind = result;
					dishKindList = dishKind;
					setKidsMap(dishKindList);
					getDishInfo();
				} else {
					showToast(ToolsUtils.returnXMLStr("get_dish_kind_is_null"));
					Log.i("获取菜品分类为空", "");
				}
			}

			@Override
			public void onError(PosServiceException e) {
				progressDialog.disLoading();
				showToast(ToolsUtils.returnXMLStr("get_dish_kind_is_null") + "," + e.getMessage());
				Log.i("获取菜品分类为空", e.getMessage());
			}
		});
	}

	private void initDishAdapter() {
		progressDialog.disLoading();
		if (dishKidsTopAdp != null && dishKinds != null) {
			setKidsMap(dishKindList);
			dishKinds.setAdapter(dishKidsTopAdp);
			setCurrentItem(currentPosition);
			dishItems.setAdapter(dishAdp);
			dishKidsTopAdp.setSelect(currentPosition);
		}
	}

	/**
	 * 根据选择小类去获取对应的小类菜品
	 *
	 * @param currentPosition
	 */
	private void setCurrentItem(int currentPosition) {
		dishAdp.setDataInfo(DishDataController.getdishsForKind(currentPosition));
	}

	private void setKidsMap(List<DishType> dishKindList) {
		dishKidsTopAdp.setData(dishKindList);
	}

	//添加菜品时抛物线
	@TargetApi(Build.VERSION_CODES.KITKAT)
	private void parabola(int[] StartPoint) {

		StartPoint[0] = StartPoint[0] + 10;
		StartPoint[1] = StartPoint[1] + 10;
		try {

			parabola_iv.setLeft(StartPoint[0]);
			parabola_iv.setTop(StartPoint[1]);
			parabola_iv.setVisibility(View.VISIBLE);

			//        Log.e("开始位置:",StartPoint[0]+";"+StartPoint[1]);

			int[] endPoint = new int[2];
			cartBtn.getLocationOnScreen(endPoint);
			endPoint[0] = endPoint[0] + cartBtn.getWidth() / 2 - 10;
			endPoint[1] = endPoint[1] - cartBtn.getHeight() / 2 + 50;
			//        Log.e("结束位置:",endPoint[0]+";"+endPoint[1]);

			//抛物线动画
			ObjectAnimator translateAnimationX = ObjectAnimator
					.ofFloat(parabola_iv, "translationX", StartPoint[0], endPoint[0]);
			translateAnimationX.setDuration(500);
			translateAnimationX.setInterpolator(new LinearInterpolator());

			ObjectAnimator translateAnimationY = ObjectAnimator
					.ofFloat(parabola_iv, "translationY", StartPoint[1], endPoint[1]);
			translateAnimationY.setDuration(500);
			translateAnimationY.setInterpolator(new AccelerateInterpolator());

			//缩小动画
			ObjectAnimator scaleX = ObjectAnimator.ofFloat(cartBtn, "scaleX", 1, 0.8f, 1);
			scaleX.setDuration(100);
			ObjectAnimator scaleY = ObjectAnimator.ofFloat(cartBtn, "scaleY", 1, 0.8f, 1);
			scaleY.setDuration(100);

			translateAnimationY.addListener(new Animator.AnimatorListener() {
				@Override
				public void onAnimationStart(Animator animation) {

				}

				@Override
				public void onAnimationEnd(Animator animation) {
					parabola_iv.setVisibility(View.GONE);
				}

				@Override
				public void onAnimationCancel(Animator animation) {

				}

				@Override
				public void onAnimationRepeat(Animator animation) {

				}
			});

			AnimatorSet animatorSet = new AnimatorSet();
			animatorSet.play(translateAnimationX).with(translateAnimationY);
			animatorSet.play(scaleX).with(scaleY).after(translateAnimationX);

			animatorSet.start();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}


	private List<DishCount> dishCountList = new CopyOnWriteArrayList<>();

	public void setDishCount(List<DishCount> dishCountList) {
		if (dishCountList != null && dishCountList.size() > 0) {
			this.dishCountList = dishCountList;
			dishAdp.setDishCount(dishCountList);
		}
	}

	private int getOrderDishItemSum() {
		int dishItemSum = 0;
		if (!ToolsUtils.isList(getDishItemList())) {
			int size = getDishItemList().size();
			for (int i = 0; i < size; i++) {
				dishItemSum += getDishItemList().get(i).getQuantity();
			}
		}
		return dishItemSum;
	}

	/**
	 * 清空购物车,以及清空一些数据缓存
	 */
	public void cleanCart() {
		Cart.cleanDishList();
		DishDataController.cleanDishMarkMap();
		if (dishKidsTopAdp != null) {
			dishKidsTopAdp.notifyDataSetChanged();
		}
		if (dishAdp != null) {
			dishAdp.notifyDataSetChanged();
		}
		posInfo.setOrderType("EAT_IN");
		eat_in_ll.setSelected(true);
		tack_out_ll.setSelected(false);
		sale_out_ll.setSelected(false);
		titleRightIcon.setBackgroundResource(R.drawable.eat_in);
		orderType = "EAT_IN";
		dishType = 0;
		DishOptionController.cleanCartDishMap();//下单后清除菜品的定制项缓存
		Cart.notifyContentChange();
	}

	/**
	 * 检测菜品沽清状况
	 *
	 * @param dishs
	 * @param callback callback.haveStock(); 有库存   callback.noStock();无库存
	 */
	public void getDishStock(List<Dish> dishs, final DishCheckCallback callback) {
		progressDialog.showLoading("");
		try {
			DishService dishService = DishService.getInstance();

			List<DishCount> dishCountList = new ArrayList<>();
			int             size          = dishs.size();
			for (int i = 0; i < size; i++) {
				Dish dish = dishs.get(i);

				DishCount count = new DishCount();
				count.setDishid(dish.getDishId());
				count.setCount(dish.quantity);
				dishCountList.add(count);

				if (dish.isPackage()) {
					for (Dish.Package aPackage : dish.subItemList) {
						DishCount dc = new DishCount();
						dc.setDishid(aPackage.getDishId());
						dc.setCount(aPackage.quantity * dish.quantity);
						dishCountList.add(dc);
					}
				}
			}
			dishService.checkDishCount(dishCountList, new ResultCallback<List<DishCount>>() {
				@Override
				public void onResult(List<DishCount> result) {
					progressDialog.disLoading();
					if (result == null || result.size() <= 0) {
						callback.haveStock();
					} else {
						callback.noStock(result);
					}
				}

				@Override
				public void onError(PosServiceException e) {
					showToast(e.getMessage());
					progressDialog.disLoading();
				}
			});
		} catch (PosServiceException e) {
			e.printStackTrace();
			return;
		}
	}

	public void refreshDish(List<DishCount> result, List<Dish> dishs) {
		//刷新菜品数据,显示沽清
		getDishInfo();
		String names = Cart.getInstance().getItemNameByDids((ArrayList) result, dishs);
		showToast(ToolsUtils.returnXMLStr("the_following_items_are_not_enough") + "\n\n" + names
				+ "。\n\n" + ToolsUtils.returnXMLStr("please_re_order"));
		progressDialog.disLoading();
		Log.i("以下菜品份数不足:", names + "====<<");
	}

	private void swichPay(final String tableNames) {
		if (getDishItemList().size() > 0) {
			if (!TextUtils.isEmpty(tableNames)) {
				tableName = tableNames;
			}
			jumpCheckout();
		} else {
			myApplication.ShowToast(ToolsUtils.returnXMLStr("please_click_dish"));
		}
	}

	/**
	 * 跳转到结账界面
	 */
	private Order tableOrder;

	private void jumpCheckout() {
		float dishPrice = Cart.getPriceSum();
		float dishCost  = Cart.getCost();
		BigDecimal activeMoney = new BigDecimal(String.valueOf(dishPrice - dishCost))
				.setScale(2, BigDecimal.ROUND_HALF_UP);
		Intent intent = new Intent(CreateOrderAty.this, CheckOutNewAty.class);
		intent.putExtra("source", Constant.EventState.SOURCE_CREAT_ORDER);
		intent.putExtra("tableOrder", (Serializable) tableOrder);
		intent.putExtra("active_money", (Serializable) activeMoney);
		intent.putExtra("activityName", activityName);
		intent.putExtra("tableName", tableName);
		startActivityForResult(intent, CHECKOUT);
	}

	private void dishStock(final String tableNames) {
		getDishStock(getDishItemList(), new DishCheckCallback() {
			@Override
			public void haveStock() {
				if (posInfo.getOrderType().equals("SALE_OUT") && store.isWaiMaiGuestInfo()) {
					DialogUtil.takeOutDialog(context, new DialogTCallback() {
						@Override
						public void onConfirm(Object o) {
							swichPay(tableNames);
						}

						@Override
						public void onCancle() {
						}
					});
				} else {
					swichPay(tableNames);
				}
			}

			@Override
			public void noStock(List dataList) {
				refreshDish(dataList, getDishItemList());
			}
		});
	}

	private void logicOrder() {
		if (StoreInfor.cardNumberMode) {
			tableName = posInfo.getTableNumber();
			if (TextUtils.isEmpty(tableName)) {
				DialogUtil.initTableInfo(context, new DialogTCallback() {
					@Override
					public void onConfirm(Object o) {
						Order order = (Order) o;
						if (order != null) {
							if (!TextUtils.isEmpty(order.getTableNames())) {
								tableName = order.getTableNames();
								posInfo.setTableNumber(tableName);
								//                                setCardNumber();
								dishStock(tableName);
							}
						}
					}

					@Override
					public void onCancle() {

					}
				});
			} else {
				dishStock(tableName);
			}
		} else {
			dishStock(null);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == CHECKOUT) {
			if (resultCode == Constant.EventState.SELECT_FRAGMTNT_ORDER) {
				finish();
			}
		}
	}

	private void toCheckOutAty() {
		UserAction.log("下单", context);
		if (creatBtn.isSelected()) {
			if (cart.dishItemList.size() > 0) {
				logicOrder();
			} else {
				showToast(ToolsUtils.returnXMLStr("please_click_dish"));
			}
		}
	}

	@OnClick({R.id.creat_btn, R.id.cart_btn, R.id.title_right_icon, R.id.title_left,
			R.id.clear_cart})
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.creat_btn://下单
				if (posInfo.getAccountMember() != null) {
					toCheckOutAty();
					Cart.getInstance().changeToMemberPrice();
				} else {
					DialogUtil.memberVerDialog(context, new DialogCallBack() {
						@Override
						public void onOk() {
							//更新总计的价格
							toCheckOutAty();
						}

						@Override
						public void onCancle() {
							toCheckOutAty();
						}
					});
				}
				break;
			case R.id.cart_btn://购物车
				UserAction.log("购物车", context);
				if (cartBtn.isSelected()) {
					showHideCart();
				}
				break;
			case R.id.clear_cart://清空购物车
				UserAction.log("清空购物车", context);
				cleanCart();
				break;
			case R.id.title_left://返回
				UserAction.log("返回", context);
				if (cart.getDishCount() > 0) {
					clearCartWarn(context, this);
				} else {
					finish();
				}
				break;
			case R.id.title_right_icon://堂食\外带
				UserAction.log("堂食\\外带", context);
				if (popupWindow.isShowing()) {
					popupWindow.dismiss();
				} else {
					popupWindow.showAsDropDown(titleRight, 0, 0);
				}

				break;
		}
	}
}
