package cn.acewill.mobile.pos.ui.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.acewill.paylibrary.epay.AliGoodsItem;

import org.greenrobot.eventbus.EventBus;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.acewill.mobile.pos.R;
import cn.acewill.mobile.pos.base.activity.BaseActivity;
import cn.acewill.mobile.pos.common.DishDataController;
import cn.acewill.mobile.pos.common.StoreInfor;
import cn.acewill.mobile.pos.config.MyApplication;
import cn.acewill.mobile.pos.config.Store;
import cn.acewill.mobile.pos.exception.PosServiceException;
import cn.acewill.mobile.pos.interfices.DialogActiveCall;
import cn.acewill.mobile.pos.model.Customer;
import cn.acewill.mobile.pos.model.MarketObject;
import cn.acewill.mobile.pos.model.MarketType;
import cn.acewill.mobile.pos.model.PaymentList;
import cn.acewill.mobile.pos.model.dish.Cart;
import cn.acewill.mobile.pos.model.event.PosEvent;
import cn.acewill.mobile.pos.model.order.CardRecord;
import cn.acewill.mobile.pos.model.order.Order;
import cn.acewill.mobile.pos.model.order.OrderItem;
import cn.acewill.mobile.pos.model.order.PaymentStatus;
import cn.acewill.mobile.pos.model.payment.Payment;
import cn.acewill.mobile.pos.model.payment.PaymentCategory;
import cn.acewill.mobile.pos.model.wsh.Account;
import cn.acewill.mobile.pos.model.wsh.WshCreateDeal;
import cn.acewill.mobile.pos.model.wsh.WshDealPreview;
import cn.acewill.mobile.pos.printer.Printer;
import cn.acewill.mobile.pos.service.DishService;
import cn.acewill.mobile.pos.service.OrderService;
import cn.acewill.mobile.pos.service.PosInfo;
import cn.acewill.mobile.pos.service.ResultCallback;
import cn.acewill.mobile.pos.service.StoreBusinessService;
import cn.acewill.mobile.pos.service.TradeService;
import cn.acewill.mobile.pos.service.WshService;
import cn.acewill.mobile.pos.service.retrofit.response.PosResponse;
import cn.acewill.mobile.pos.service.retrofit.response.ValidationResponse;
import cn.acewill.mobile.pos.ui.adapter.PayTypeAdapter;
import cn.acewill.mobile.pos.utils.Constant;
import cn.acewill.mobile.pos.utils.FileLog;
import cn.acewill.mobile.pos.utils.FormatUtils;
import cn.acewill.mobile.pos.utils.PayDialogUtil;
import cn.acewill.mobile.pos.utils.ToolsUtils;
import cn.acewill.mobile.pos.utils.UserAction;
import cn.acewill.mobile.pos.utils.WindowUtil;
import cn.acewill.mobile.pos.widget.CommonEditText;
import cn.acewill.mobile.pos.widget.ProgressDialogF;

import static cn.acewill.mobile.pos.utils.DialogUtil.getDialogShow;

/**
 * 结账页面
 * Created by DHH on 2016/6/12.
 */
public class CheckOutNewAty extends BaseActivity {
	private static final String TAG = "CheckOutNewAty";
	@BindView(R.id.back_ll)
	LinearLayout backLl;
	@BindView(R.id.pay_money)
	TextView     payMoney;
	@BindView(R.id.pay_btn)
	TextView     payBtn;
	@BindView(R.id.pay_type_list)
	ListView     payTypeList;
	@BindView(R.id.reduce_name)
	TextView     reduceName;
	@BindView(R.id.reduce_money)
	TextView     reduMoney;
	@BindView(R.id.reduce_ll)
	LinearLayout reduceLl;
	@BindView(R.id.active_ll)
	LinearLayout activeLl;
	@BindView(R.id.nopay_name)
	TextView     nopayName;
	@BindView(R.id.nopay_money)
	TextView     nopayMoney;
	@BindView(R.id.symbol)
	TextView     symbol;
	@BindView(R.id.payed_money)
	TextView     payedMoney;
	@BindView(R.id.active_money)
	TextView     ticketDiscount;
	@BindView(R.id.refersh_id)
	TextView     refershId;
	@BindView(R.id.page_title)
	TextView     pageTitle;

	private DishService     dishService;
	private TradeService    tradeService;
	private OrderService    orderService;
	private WshService      wshService;
	private ProgressDialogF progressDialog;
	private PayTypeAdapter  payTypeAdapter;
	//    private PayTypeSelectedAdp paySelectAdp;
	//    private PayOrderItemAdp payOrderItemAdp;
	private List<Printer>   printerList;//打印机列表

	public  BigDecimal total_money   = new BigDecimal(0);//总金额
	public  BigDecimal nopay_money   = new BigDecimal(0);//未支付金额
	public  BigDecimal temp_money    = new BigDecimal(0);//总支付金额，用于营销活动计算使用
	private BigDecimal service_money = new BigDecimal(0);//服务费
	private BigDecimal avtive_money  = new BigDecimal(0);//营销活动减免金额
	private BigDecimal wipingValue   = new BigDecimal("0.00");//被抹零的金额
	private List<Printer> selectPrint;//选择的打印机
	private List<PaymentList>                        paymentLists  = new ArrayList<PaymentList>();//结账时选择的支付方式信息
	public  List<AliGoodsItem>                       aliGoodsItem  = new ArrayList<AliGoodsItem>();//支付宝参数
	public  CopyOnWriteArrayList<ValidationResponse> addValidation = new CopyOnWriteArrayList<>();
	public String orderId;//订单id
	private int source = 0;//0:从点菜页面过来；1:从当日订单过来，2：桌台过来
	private Cart    cart;
	private Store   store;
	private PosInfo posInfo;
	private String  biz_id;//微生活会员业务流水号，用于提交交易时使用，创建交易预览时给此值赋值
	private Account    accountMember = null;//结账微生活会员信息  用于记录当前结账微生活会员信息
	private CardRecord cardRecord    = null;//挂账信息
	private WshDealPreview  wshDealPreview;//创建交易预览时给此值赋值
	public  List<OrderItem> orderItems;
	private Order           printOrder;
	private boolean isAppendDish  = false;
	private boolean isAnyCheckOut = false;//允许输入任意金额结账,但必须有一种支付方式即使为零

	private String wft_transaction_id;

	public  int reverseCheckOutFlag = 0;//反结账标识
	private int reasonId            = 0;//原因Id
	private String code_scan;//反扫返回的二维码号，用于重试使用
	private              boolean    isReCheckOut          = false;
	private              BigDecimal anyCheckoutPrintMoney = null;//任意输入的支付金额
	private static final int        REQUEST_CODE          = 110;//ScanActivity回调
	private static final int        FAIL_PAY              = 0;//支付失败
	private static final int        FAIL_ORDER            = 1;//下单失败
	private static final int        FAIL_CHECKOUT         = 2;//结账失败
	private              int        wftPayType            = 3;//选择威富通 是微信支付还是支付宝支付  0是微信 1是支付宝

	public int getWftPayType() {
		return wftPayType;
	}

	public void setWftPayType(int wftPayType) {
		this.wftPayType = wftPayType;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		posInfo.setTableNumber("");
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pay);
		ButterKnife.bind(this);
		myApplication.addPage(CheckOutNewAty.this);
		getOrderId(true);
		Log.e(TAG, "getOrderId1");
		FileLog.log(TAG + ">getOrderId1");
	}

	public void getOrderId(final boolean isInit) {
		try {
			OrderService orderService = OrderService.getInstance();

			orderService.getNextOrderId(new ResultCallback<Long>() {//结账页面获取订单号
				@Override
				public void onResult(Long result) {
					if (result > 0) {
						orderId = result + "";
						Log.i("获取订单Id成功", "====>>:NextOrderId=" + orderId);
						if (isInit) {
							initView();
							switchLogic();
							List<Payment> list = ToolsUtils
									.cloneTo(StoreInfor.getPaymentList());
							if (posInfo.getAccountMember() == null) {
								Iterator<Payment> iterator = list.iterator();
								while (iterator.hasNext()) {
									Payment next = iterator.next();
									if (next.getId() == 5)
										iterator.remove();
								}
							}
							payTypeAdapter.setData(list);
						} else {
							showToast("获取订单号成功,订单号为:" + result);
						}
					} else {
						showToast("获取订单号失败,请重新获取!");
						if (isInit) {
							finish();
						}
					}
				}

				@Override
				public void onError(PosServiceException e) {
					Log.i("获取订单Id失败", e.getMessage());
					showToast("获取订单号失败,请重新获取!");
					finish();
					EventBus.getDefault()
							.post(new PosEvent(Constant.EventState.ERR_CREATE_ORDERID_FILURE));
					//                    showToast(ToolsUtils.returnXMLStr("get_order_id_failure"));
				}
			});
		} catch (PosServiceException e) {
			e.printStackTrace();
		}
	}


	private void initView() {
		progressDialog = new ProgressDialogF(this);
		payTypeAdapter = new PayTypeAdapter(context);
		payTypeList.setAdapter(payTypeAdapter);
		progressDialog.setCanceledOnTouchOutside(false);
		payBtnEnable(false, false);
		source = getIntent().getIntExtra("source", 0);
		posInfo = PosInfo.getInstance();
		cart = Cart.getInstance();
		store = Store.getInstance(context);
		if (posInfo != null && posInfo.getAccountMember() != null) {
			pageTitle.setText(posInfo.getAccountMember().getName() + "的订单");
		}
		try {
			dishService = DishService.getInstance();
			tradeService = TradeService.getInstance();
			orderService = OrderService.getInstance();
			wshService = WshService.getInstance();
		} catch (PosServiceException e) {
			e.printStackTrace();
		}
	}


	//根据不同页面来源处理
	private String tableName;

	private void switchLogic() {
		switch (source) {
			case 0://下单页面过来
				Order tableOrder = (Order) getIntent().getSerializableExtra("tableOrder");
				avtive_money = (BigDecimal) getIntent().getSerializableExtra("active_money");
				tableName = getIntent().getStringExtra("tableName");
				String activeName = getIntent().getStringExtra("activityName");
				BigDecimal cartCost = new BigDecimal(cart.getCost())
						.setScale(2, BigDecimal.ROUND_HALF_UP);
				temp_money = total_money = nopay_money = ToolsUtils.wipeZeroMoney(cartCost);
				wipingValue = cartCost.subtract(total_money);
				payMoney.setText(total_money.setScale(2, BigDecimal.ROUND_DOWN) + "");//合计

				printOrder = cart.getOrderItem(tableOrder, cart.getDishItemList());
				orderItems = cart.getOrderItem(tableOrder, cart.getDishItemList()).getItemList();
				updateTicket();//switchLogic case 0

				payTypeAdapter.setOrder(printOrder);
				if (avtive_money != null && avtive_money.compareTo(BigDecimal.ZERO) > 0) {
					ticketDiscount.setText("-" + avtive_money);
					//客显屏显示优惠金额
					printOrder.setAvtive_money(avtive_money);
				}
				if (!TextUtils.isEmpty(activeName)) {
					printOrder.setAvtiveName(activeName);
				}

				if (orderItems != null) {
					for (OrderItem orderItem : orderItems) {
						AliGoodsItem item = new AliGoodsItem();
						item.setGoods_id(orderItem.getId() + "");
						item.setGoods_category(orderItem.getDishId() + "");
						item.setPrice(FormatUtils
								.getDoubleW(new BigDecimal(orderItem.getPrice() + "")
										.doubleValue()));
						item.setQuantity(orderItem.getQuantity() + "");
						item.setGoods_name(orderItem.getDishName());
						aliGoodsItem.add(item);
					}
				}
				break;
			case 2:
				Order table = (Order) getIntent().getSerializableExtra("tableOrder");
				reverseCheckOutFlag = getIntent().getIntExtra("reverseCheckOutFlag", 0);
				reasonId = getIntent().getIntExtra("reasonId", 0);
				isReCheckOut = getIntent().getBooleanExtra("isReCheckOut", false);

				printOrder = table;
				payTypeAdapter.setOrder(printOrder);
				DishDataController.handleOrder(printOrder);
				if (table != null) {
					orderId = String.valueOf(table.getId());
					BigDecimal cartCost2 = new BigDecimal(table.getCost())
							.setScale(2, BigDecimal.ROUND_HALF_UP);
					temp_money = total_money = nopay_money = ToolsUtils.wipeZeroMoney(cartCost2);
					wipingValue = cartCost2.subtract(total_money);
					payMoney.setText(total_money.setScale(2, BigDecimal.ROUND_DOWN) + "");
					updateTicket();//switchLogic case 2

					orderItems = table.getItemList();

					if (orderItems != null) {
						for (OrderItem orderItem : orderItems) {
							AliGoodsItem item = new AliGoodsItem();
							item.setGoods_id(orderItem.getId() + "");
							item.setGoods_category(orderItem.getDishId() + "");
							item.setPrice(FormatUtils
									.getDoubleW(new BigDecimal(orderItem.getPrice() + "")
											.doubleValue()));
							item.setQuantity(orderItem.getQuantity() + "");
							item.setGoods_name(orderItem.getDishName());
							aliGoodsItem.add(item);
						}
					}
				}
				break;
		}
	}


	public String getBiz_id() {
		return biz_id;
	}

	public void setBiz_id(String biz_id) {
		this.biz_id = biz_id;
	}

	public WshDealPreview getWshDealPreview() {
		return wshDealPreview;
	}

	public void setWshDealPreview(WshDealPreview wshDealPreview) {
		this.wshDealPreview = wshDealPreview;
	}

	public Account getAccountMember() {
		return accountMember;
	}

	public void setAccountMember(Account accountMember) {
		this.accountMember = accountMember;
	}

	public CardRecord getCardRecord() {
		return cardRecord;
	}

	public void setCardRecord(CardRecord cardRecord) {
		this.cardRecord = cardRecord;
	}

	public CopyOnWriteArrayList<ValidationResponse> getAddValidation() {
		return addValidation;
	}

	public void setAddValidation(CopyOnWriteArrayList<ValidationResponse> addValidations) {
		this.addValidation = ToolsUtils.cloneTo(addValidations);
	}

	//控制支付按钮点击
	private void payBtnEnable(boolean enable, boolean isCheckout) {
		if (enable) {
			payBtn.setSelected(true);
			payBtn.setEnabled(true);
			payBtn.setText("结  账");
		} else {
			payBtn.setSelected(false);
			payBtn.setEnabled(false);
			if (isCheckout) {
				payBtn.setText("正在结账...");
			} else {
				payBtn.setText("结  账");
			}

		}
	}

	/**
	 * 会员选择添加成功后,又重新选择了一次,而第一次的会员支付又没有删除所以会导致错误添加
	 * 故要将其删除
	 */
	public void removeMemberPay() {
		if (paymentLists != null && paymentLists.size() > 0) {
			int size = paymentLists.size();
			for (int i = 0; i < size; i++) {
				if (paymentLists.size() == i) {
					break;
				}
				boolean     isDeleteItem = false;
				PaymentList payMent      = paymentLists.get(i);
				if (payMent != null) {
					int id = payMent.getPaymentTypeId();
					if (id == 3 || id == 4 || id == 5) {
						nopay_money = nopay_money.add(payMent.getValue());
						BigDecimal noPayMoTex = new BigDecimal(nopayMoney.getText().toString())
								.setScale(2, BigDecimal.ROUND_DOWN);
						BigDecimal payEdMoTex = new BigDecimal(payedMoney.getText().toString())
								.setScale(2, BigDecimal.ROUND_DOWN);
						nopayMoney.setText(noPayMoTex.add(payMent.getValue())
								.setScale(2, BigDecimal.ROUND_DOWN) + "");
						payedMoney.setText(payEdMoTex.subtract(payMent.getValue())
								.setScale(2, BigDecimal.ROUND_DOWN) + "");

						paymentLists.remove(payMent);
						isDeleteItem = true;
					}
					if (size == 1) {
						break;
					}
					if (isDeleteItem) {
						i -= 1;
					}
				}
			}
		}
	}

	public boolean isHaveMemberPay(int payId) {
		boolean isHave = false;
		if (paymentLists != null && paymentLists.size() > 0) {
			int size = paymentLists.size();
			for (int i = 0; i < size; i++) {
				PaymentList payment = paymentLists.get(i);
				if (payId == payment.getPaymentTypeId()) {
					isHave = true;
				}
			}
		}
		return isHave;
	}


	/**
	 * 更新未支付金额及UI
	 *
	 * @param pay_money  输入的金额
	 * @param cash_money 现金支付金额
	 * @param payment    支付方式ID
	 * @param paymentNo  微信支付宝流水号
	 */
	public void changeMoney(BigDecimal pay_money, BigDecimal cash_money, Payment payment, String paymentNo, String transactionNo, List<Payment> memberPayMent, CopyOnWriteArrayList<ValidationResponse> addValidationLists) {
		nopay_money = nopay_money.subtract(pay_money).setScale(2, BigDecimal.ROUND_DOWN);
		boolean isHaveMemberPay = false;
		int     memberListSize  = 0;
		if (memberPayMent != null && memberPayMent.size() > 0) {
			isHaveMemberPay = true;
			memberListSize = memberPayMent.size();
		}
		if (payment.getId() == 3 || payment.getId() == 4 || payment.getId() == 5) {
			if (isHaveMemberPay && nopay_money.compareTo(BigDecimal.ZERO) == -1) {
				nopay_money = new BigDecimal(0);
			}
		}
		PosInfo posInfo = PosInfo.getInstance();

		updateTicket();//changeMoney

		boolean isAdded = false;
		for (PaymentList paymentList : paymentLists) {
			for (int i = 0; i < memberListSize; i++) {
				Payment memberPay = memberPayMent.get(i);
				if (memberPay.getId() == paymentList.getPaymentTypeId()) {
					isAdded = true;
					//如果使用的是会员，则重置会员消费金额，因为只有在点击结账时，才提交会员交易
					if (memberPay.getId() == 3 || memberPay.getId() == 4 || memberPay
							.getId() == 5) {
						if (memberPay.getDeductibleAmount() > 0) {
							paymentList.setValue(new BigDecimal(memberPay.getDeductibleAmount())
									.setScale(2, BigDecimal.ROUND_HALF_UP));
							paymentList.setPaymentNo(paymentNo);
						}
					}
				}
				break;
			}

			if (payment.getId() == paymentList.getPaymentTypeId() && (payment
					.getId() != 3 && payment.getId() != 4 && payment.getId() != 5 && payment
					.getId() != -32 && payment.getId() != -8)) {
				isAdded = true;
				//其他支付方式只相加金额
				//如果是现金类型则不能按输入的金额算，取输入金额与未支付金额最小值
				if (payment.getCategory() == PaymentCategory.CASH) {
					if (cash_money.compareTo(BigDecimal.ZERO) == 1) {
						paymentList.setValue(paymentList.getValue().add(cash_money));
					}
				} else {
					if (pay_money.compareTo(BigDecimal.ZERO) == 1) {
						paymentList.setValue(paymentList.getValue().add(pay_money));
					}
				}
				break;
			}
		}
		//支付成功后添加到支付方式列表paymentLists，用于下单或结账接口使用
		if (!isAdded) {
			boolean isHaveMber = false;
			for (int i = 0; i < memberListSize; i++) {
				isHaveMber = true;
				Payment memberPay = memberPayMent.get(i);
				if (memberPay != null) {
					if (memberPay.getDeductibleAmount() > 0 || memberPay.getId() == 4) {
						PaymentList checkModle = new PaymentList();
						checkModle.setAppId(posInfo.getAppId());
						checkModle.setBrandId(posInfo.getBrandId());
						checkModle.setStoreId(posInfo.getStoreId());
						checkModle.setOrderId(orderId);
						checkModle.setPaymentTypeId(memberPay.getId());
						checkModle.setCreatedAt(System.currentTimeMillis());
						checkModle.setPaidAt(System.currentTimeMillis());
						if (memberPay.getId() == 3 || memberPay.getId() == 4 || memberPay
								.getId() == 5 || memberPay.getId() == -8) {
							checkModle.setPaymentNo(paymentNo);
							if (memberPay.getId() == -8) {
								wft_transaction_id = transactionNo;
								if (wftPayType == 0) {
									checkModle.setSource(ToolsUtils.returnXMLStr("wxPay"));
								} else if (wftPayType == 1) {
									checkModle.setSource(ToolsUtils.returnXMLStr("sth_zfb"));
								}
								checkModle.setPaymentNo(transactionNo);
							}
						} else {
							checkModle.setPaymentNo(orderId);
						}
						checkModle.setValue(new BigDecimal(memberPay.getDeductibleAmount())
								.setScale(2, BigDecimal.ROUND_HALF_UP));
						checkModle.setOperation("PAY");
						checkModle.setPayName(memberPay.getName());
						paymentLists.add(checkModle);
					}
				}
			}

			boolean isHaveMt = false;
			if (addValidationLists != null && addValidationLists.size() > 0) {
				int validationSize = addValidationLists.size();
				for (int i = 0; i < validationSize; i++) {
					isHaveMt = true;
					ValidationResponse validation = addValidationLists.get(i);
					if (validation != null) {
						addValidation.add(validation);
						if (validation.isSuccess() && validation.getDealValue() > 0) {
							PaymentList checkModle = new PaymentList();
							checkModle.setAppId(posInfo.getAppId());
							checkModle.setBrandId(posInfo.getBrandId());
							checkModle.setStoreId(posInfo.getStoreId());
							checkModle.setOrderId(orderId);
							checkModle.setPaymentTypeId(payment.getId());
							checkModle.setCreatedAt(System.currentTimeMillis());
							checkModle.setPaidAt(System.currentTimeMillis());
							checkModle.setPaymentNo(validation.getCouponCode());
							checkModle.setValue(new BigDecimal(validation.getDealValue())
									.setScale(2, BigDecimal.ROUND_HALF_UP));
							checkModle.setOperation("PAY");
							checkModle.setPayName(payment.getName());
							paymentLists.add(checkModle);
						}
					}
				}
			}

			if (!isHaveMber && !isHaveMt) {
				PaymentList checkModle = new PaymentList();
				checkModle.setAppId(posInfo.getAppId());
				checkModle.setBrandId(posInfo.getBrandId());
				checkModle.setStoreId(posInfo.getStoreId());
				checkModle.setOrderId(orderId);
				checkModle.setPaymentTypeId(payment.getId());
				checkModle.setCreatedAt(System.currentTimeMillis());
				checkModle.setPaidAt(System.currentTimeMillis());
				if (payment.getId() == 3 || payment.getId() == 4 || payment.getId() == 5 || payment
						.getId() == -8) {
					checkModle.setPaymentNo(paymentNo);
					if (payment.getId() == -8) {
						wft_transaction_id = transactionNo;
						if (wftPayType == 0) {
							checkModle.setSource(ToolsUtils.returnXMLStr("wxPay"));
						} else if (wftPayType == 1) {
							checkModle.setSource(ToolsUtils.returnXMLStr("sth_zfb"));
						}
						checkModle.setPaymentNo(transactionNo);
					}
				} else {
					checkModle.setPaymentNo(orderId);
				}
				if (payment
						.getCategory() == PaymentCategory.CASH) {//如果是现金类型则不能按输入的金额算，取输入金额与未支付金额最小值
					checkModle.setValue(cash_money);
				} else {
					checkModle.setValue(pay_money);
				}
				if (!TextUtils.isEmpty(transactionNo)) {
					checkModle.setTransactionNo(transactionNo);
				}
				checkModle.setOperation("PAY");
				checkModle.setPayName(payment.getName());

				//如果是现金类型则不能按输入的金额算，取输入金额与未支付金额最小值
				if (payment.getCategory() == PaymentCategory.CASH) {
					if (cash_money.compareTo(BigDecimal.ZERO) == 1) {
						paymentLists.add(checkModle);
					}
				} else {
					if (pay_money.compareTo(BigDecimal.ZERO) == 1) {
						paymentLists.add(checkModle);
					}
				}
			}

		}
		payTypeAdapter.setPayTypeList(paymentLists);
		printOrder.setPaymentList(paymentLists);
		printOrder.setPay_money(total_money.subtract(nopay_money));

		if (!TextUtils.isEmpty(orderId)) {
			printOrder.setPaymentNo(orderId);
		}
		payTypeAdapter.setClickPay(true);
	}

	//更新小票UI数据
	private void updateTicket() {
		//更新结账按钮状态
		if (nopay_money.compareTo(BigDecimal.ZERO) == 1) {
			nopayName.setText(ToolsUtils.returnXMLStr("unpaid2"));
			nopayMoney.setTextColor(ContextCompat.getColor(context, R.color.btn_red_pressed));
			symbol.setTextColor(ContextCompat.getColor(context, R.color.btn_red_pressed));
			payBtnEnable(false, false);
		} else {
			nopayName.setText(ToolsUtils.returnXMLStr("sth_returnMoney"));
			nopayMoney.setTextColor(ContextCompat.getColor(context, R.color.green));
			symbol.setTextColor(ContextCompat.getColor(context, R.color.green));
			payBtnEnable(true, true);
			printOrder.setGive_money(nopay_money);
		}
		nopayMoney.setText(nopay_money.setScale(2, BigDecimal.ROUND_DOWN) + "");
		payedMoney
				.setText(total_money.subtract(nopay_money).setScale(2, BigDecimal.ROUND_DOWN) + "");
		if (accountMember != null) {
			pageTitle.setText(accountMember.getName() + "的订单");
		}
	}


	// 反扫返回信息
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		progressDialog.disLoading();
		if (resultCode == -110) {
			switch (requestCode) {
				case REQUEST_CODE:
					if (data != null) {
						code_scan = data.getStringExtra("data");
						if (!TextUtils.isEmpty(code_scan)) {
							payTypeAdapter.scanSuccess(code_scan);
						} else {
							payTypeAdapter.setClickPay(true);
							showToast("扫描二维码失败！");
							Log.i("扫描二维码失败", "activity返回");
						}

					}
					break;
			}
		} else {
			payTypeAdapter.setClickPay(true);
		}
	}

	public void executeCode(CopyOnWriteArrayList<ValidationResponse> addValidation) {
		progressDialog.showLoading("");
		StoreBusinessService storeBusinessService = null;
		try {
			storeBusinessService = StoreBusinessService.getInstance();
			StringBuffer sbb = new StringBuffer();
			for (ValidationResponse validation : addValidation) {
				if (validation.isSuccess() && validation.vouchersIsEff()) {
					String sb   = sbb.toString();
					String code = validation.getCouponCode();
					String h    = ",";
					if (TextUtils.isEmpty(sb)) {
						sbb.append(code);
					} else {
						sbb.append(h + code);
					}
				}
			}
			//            System.out.println("券码===》2》" + sbb.toString());
			Log.e("组合结账美团交易券列表:====" + orderId, sbb.toString());
			storeBusinessService.executeCode(sbb.toString(), Long
					.parseLong(orderId), new ResultCallback<ValidationResponse>() {
				@Override
				public void onResult(ValidationResponse result) {
					progressDialog.disLoading();
					if (result.isSuccess()) {
						creatOrdAndCheckOut();
					} else {
						MyApplication.getInstance().ShowToast(result.getMessage());
					}
				}

				@Override
				public void onError(PosServiceException e) {
					progressDialog.disLoading();
					Log.e("提交美团交易有误:====" + orderId, e.getMessage());
				}
			});
		} catch (PosServiceException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 判断是先支付还是后支付:先支付走下单方法，后支付走结账方法
	 */
	private void tradeLogic() {
		switch (source) {
			case 0://先支付
				if (addValidation != null && addValidation.size() > 0) {
					executeCode(addValidation);
				} else {
					creatOrdAndCheckOut();
				}
				break;
			case 1:
			case 2://后支付
				if (ToolsUtils.isReverseCheckOut(reverseCheckOutFlag)) {
					reCheckOut();
				} else {
					checkOut();
				}
				break;
		}
	}

	private void reCheckOut() {
		progressDialog.showLoading("");
		tradeService
				.reCheckOut(paymentLists, orderId, reasonId, isReCheckOut, new ResultCallback<PosResponse>() {
					@Override
					public void onResult(PosResponse result) {
						progressDialog.disLoading();
						if (result.isSuccessful()) {//结账成功
							EventBus.getDefault()
									.post(new PosEvent(Constant.EventState.NETORDER_OFF));
							Log.i("结账页面反结账成功", "orderId ==" + orderId + "===" + ToolsUtils
									.getPrinterSth(result));
							EventBus.getDefault()
									.post(new PosEvent(Constant.EventState.SELECT_FRAGMTNT_TABLE));
							//                    Order tableOrder = (Order) getIntent().getSerializableExtra("tableOrder");
							printOrder.setServiceMoney(service_money
									.setScale(2, BigDecimal.ROUND_DOWN));
							printOrder.setAvtive_money(avtive_money
									.setScale(2, BigDecimal.ROUND_DOWN));
							printOrder.setCost(total_money.setScale(2, BigDecimal.ROUND_DOWN)
									.toString());//注意，此处total_money已经是计算了服务费与营销活动后的金额
							if (!TextUtils.isEmpty(tableName)) {
								printOrder.setTableNames(tableName);
							}
							//打双份结账单，发了两次
							EventBus.getDefault()
									.post(new PosEvent(Constant.EventState.PRINT_CHECKOUT, printOrder));
							showToast(ToolsUtils.returnXMLStr("recheck_out_success"));
							setResult(Constant.EventState.SELECT_FRAGMTNT_ORDER);
							finish();
						} else {//结账失败
							showToast(result.getErrmsg());
							Log.i("订单反结账失败", ToolsUtils.getPrinterSth(result));
						}
					}

					@Override
					public void onError(PosServiceException e) {
						progressDialog.disLoading();
						Log.i("订单反结账失败", e.getMessage());
					}
				});
	}

	/**
	 * 结账
	 */
	private void checkOut() {
		progressDialog.showLoading(ToolsUtils.returnXMLStr("is_checking_out"));
		tradeService.checkOut(paymentLists, orderId, new ResultCallback<PosResponse>() {
			@Override
			public void onResult(PosResponse result) {
				progressDialog.disLoading();
				if (result.isSuccessful()) {//结账成功
					EventBus.getDefault().post(new PosEvent(Constant.EventState.NETORDER_OFF));
					Log.i("结账页面结账成功", "orderId ==" + orderId + "===" + ToolsUtils
							.getPrinterSth(result));
					EventBus.getDefault()
							.post(new PosEvent(Constant.EventState.SELECT_FRAGMTNT_TABLE));
					//                    Order tableOrder = (Order) getIntent().getSerializableExtra("tableOrder");
					printOrder.setServiceMoney(service_money.setScale(2, BigDecimal.ROUND_DOWN));
					printOrder.setAvtive_money(avtive_money.setScale(2, BigDecimal.ROUND_DOWN));
					printOrder.setCost(total_money.setScale(2, BigDecimal.ROUND_DOWN)
							.toString());//注意，此处total_money已经是计算了服务费与营销活动后的金额
					if (!TextUtils.isEmpty(tableName)) {
						printOrder.setTableNames(tableName);
					}
					EventBus.getDefault()
							.post(new PosEvent(Constant.EventState.PRINT_CHECKOUT, printOrder));
					showToast(ToolsUtils.returnXMLStr("check_out_success"));
					setResult(Constant.EventState.SELECT_FRAGMTNT_ORDER);
					finish();
				} else {//结账失败
					showToast(result.getErrmsg());
					Log.i("订单结账失败", ToolsUtils.getPrinterSth(result));
				}
			}

			@Override
			public void onError(PosServiceException e) {
				progressDialog.disLoading();
				Log.i("订单结账失败", e.getMessage());
			}
		});
	}

	//结账
	private void pay() {
		if (TextUtils.isEmpty(biz_id)) {
			if (paymentLists != null && paymentLists.size() >= 1 && isAnyCheckOut) {
				tradeLogic();//结账按钮
			} else {
				if (nopay_money.compareTo(BigDecimal.ZERO) == -1 || nopay_money
						.compareTo(BigDecimal.ZERO) == 0) {
					tradeLogic();//结账按钮
				} else {
					showToast(ToolsUtils.returnXMLStr("please_pay_lave_money"));
				}
			}
		} else {//提交会员交易
			commitDeal();
		}
	}

	/**
	 * 提交会员交易
	 */
	private void commitDeal() {
		try {
			boolean isNotVerity = true;//不需要任何验证

			final Dialog         smsDialog = getDialogShow(context, R.layout.dialog_vertify, 0.9f, 0.25f, false, false);
			TextView             title     = (TextView) smsDialog.findViewById(R.id.title);
			final CommonEditText code      = (CommonEditText) smsDialog.findViewById(R.id.code);
			TextView dialog_cancle = (TextView) smsDialog
					.findViewById(R.id.dialog_cancle);
			TextView dialog_ok = (TextView) smsDialog.findViewById(R.id.dialog_ok);

			dialog_cancle.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					smsDialog.dismiss();
				}
			});

			dialog_ok.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					commitPay(smsDialog, code);
					//                    final CheckOutUtil checUtil = new CheckOutUtil(context);
					//                    checUtil.getDishStock(getDishItemList(), new DishCheckCallback() {
					//                        @Override
					//                        public void haveStock() {
					//                            commitPay(smsDialog,code);
					//                        }
					//
					//                        @Override
					//                        public void noStock(List dataList) {
					//                            refreshDish(dataList, getDishItemList());
					//                        }
					//                    });
				}
			});

			if (wshDealPreview.isVerify_sms()) {//是否需要短信验证
				isNotVerity = false;
				title.setText(ToolsUtils.returnXMLStr("SMS_verification"));
				code.setHint(ToolsUtils.returnXMLStr("please_input_SMS_verification"));
				smsDialog.show();

			}
			if (wshDealPreview.isVerify_password()) {//是否需要交易密码
				isNotVerity = false;
				title.setText(ToolsUtils.returnXMLStr("transaction_password_verification"));
				code.setHint(ToolsUtils
						.returnXMLStr("please_input_transaction_password_verification"));
				smsDialog.show();
			}

			code.setOnKeyListener(new View.OnKeyListener() {
				@Override
				public boolean onKey(View v, int keyCode, KeyEvent event) {
					if (KeyEvent.KEYCODE_ENTER == keyCode && KeyEvent.ACTION_DOWN == event
							.getAction()) {
						if (!TextUtils.isEmpty(code.getText().toString().trim())) {
							final String num = code.getText().toString().trim();
							if (TextUtils.isEmpty(num)) {
								if (wshDealPreview.isVerify_sms()) {
									MyApplication.getInstance().ShowToast(ToolsUtils
											.returnXMLStr("please_input_SMS_verification"));
								}
								if (wshDealPreview.isVerify_password()) {
									MyApplication.getInstance().ShowToast(ToolsUtils
											.returnXMLStr("please_input_transaction_password_verification"));
								}
							} else {
								WindowUtil.hiddenKey();
								commitPay(smsDialog, code);
								//                                final CheckOutUtil checUtil = new CheckOutUtil(context);
								//                                checUtil.getDishStock(getDishItemList(), new DishCheckCallback() {
								//                                    @Override
								//                                    public void haveStock() {
								//                                        commitPay(smsDialog,code);
								//                                    }
								//
								//                                    @Override
								//                                    public void noStock(List dataList) {
								//                                        refreshDish(dataList, getDishItemList());
								//                                    }
								//                                });

							}
						}
						return true;
					}
					return false;
				}
			});

			if (isNotVerity) {//不需要任何验证
				progressDialog.showLoading("");
				wshService.commitDeal(biz_id, "", new ResultCallback<PosResponse>() {
					@Override
					public void onResult(PosResponse result) {
						progressDialog.disLoading();
						if (result.getResult() == 0) {//提交成功
							posInfo.setMemberCheckOut(false);
							if (nopay_money.compareTo(BigDecimal.ZERO) == -1 || nopay_money
									.compareTo(BigDecimal.ZERO) == 0) {
								tradeLogic();//微生活commit之后
							} else {
								showToast(ToolsUtils.returnXMLStr("please_pay_lave_money"));
							}
						} else {
							showToast(result.getErrmsg());
						}
					}

					@Override
					public void onError(PosServiceException e) {
						progressDialog.disLoading();
						showToast(e.getMessage());
					}
				});
			}

		} catch (Exception e) {
			progressDialog.disLoading();
			e.printStackTrace();
		}
	}

	private void commitPay(final Dialog smsDialog, CommonEditText code) {
		String sms = code.getText().toString();
		if (TextUtils.isEmpty(sms)) {
			showToast(code.getHint() + "");
			return;
		}
		progressDialog.showLoading("");
		wshService.commitDeal(biz_id, sms, new ResultCallback<PosResponse>() {
			@Override
			public void onResult(PosResponse result) {
				progressDialog.disLoading();

				if (result.getResult() == 0) {//提交成功
					smsDialog.dismiss();
					posInfo.setMemberCheckOut(false);
					if (nopay_money.compareTo(BigDecimal.ZERO) == -1 || nopay_money
							.compareTo(BigDecimal.ZERO) == 0) {
						tradeLogic();//微生活commit之后
					} else {
						showToast(ToolsUtils.returnXMLStr("please_pay_lave_money"));
					}
				} else {
					showToast(result.getErrmsg());
				}
			}

			@Override
			public void onError(PosServiceException e) {
				progressDialog.disLoading();
				showToast(e.getMessage());
			}
		});
	}

	private void creatDeal(Account accountMember) {
		progressDialog.showLoading("");
		final WshCreateDeal.Request request = new WshCreateDeal.Request();
		final String                bis_id  = System.currentTimeMillis() + "";
		request.setBiz_id(bis_id);
		request.setConsume_amount(0);//分
		request.setCount_num(1);
		request.setPayment_amount(0);
		request.setPayment_mode(1);
		request.setSub_balance(0);
		request.setSub_credit(0);
		request.setRemark(ToolsUtils.returnXMLStr("consumption_preview"));
		request.setCno(accountMember.getUno()); //卡号
		request.setUid(accountMember.getUid());
		request.setPayment_amount(total_money.multiply(new BigDecimal("100")).intValue());
		request.setConsume_amount(total_money.multiply(new BigDecimal("100")).intValue());
		try {
			WshService wshService = WshService.getInstance();
			wshService.createDeal(request, new ResultCallback<WshDealPreview>() {
				@Override
				public void onResult(WshDealPreview result) {
					progressDialog.disLoading();
					commitDeal();
				}

				@Override
				public void onError(PosServiceException e) {
					progressDialog.disLoading();
					MyApplication.getInstance().ShowToast(ToolsUtils
							.returnXMLStr("submission_of_member_transaction_failed") + "," + e
							.getMessage() + "!");
				}
			});
		} catch (PosServiceException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 算出订单优惠的金额
	 *
	 * @param order
	 */
	private BigDecimal getOrderDishPreferential(Order order) {
		BigDecimal orderMoney = new BigDecimal("0.00");
		if (order != null && order.getItemList() != null && order.getItemList().size() > 0) {
			for (OrderItem orderItem : order.getItemList()) {
				if (orderItem.getMarketList() != null && orderItem.getMarketList().size() > 0) {
					for (MarketObject marketObject : orderItem.getMarketList()) {
						orderMoney = orderMoney.add(marketObject.getReduceCash());
					}
				}
			}
		}
		return orderMoney;
	}

	private void setOrderItemMarketInfo() {
		List<OrderItem> orderItemList = printOrder.getItemList();
		int             size          = orderItemList.size();
		for (int i = 0; i < size; i++) {
			OrderItem oi = orderItemList.get(i);
			oi.marketList = null;//要先将菜品里已经存在的优惠方案给清除掉然后下面会重新全部添加
			if (oi.getMarketList() == null) {
				oi.marketList = new ArrayList<>();
			}
			if (!ToolsUtils.isList(orderItems.get(i).getMarketList())) {
				oi.getMarketList().addAll(orderItems.get(i).getMarketList());//添加优惠方案
			}
			if (!ToolsUtils.isList(orderItems.get(i).getTempMarketList())) {
				oi.getMarketList().addAll(orderItems.get(i).getTempMarketList());//添加临时列表里的优惠方案
				oi.setCost(orderItems.get(i).getTempPrice());//把存储的价格赋值给cost
			}
			if (posInfo.isFreeOrder())//如果是免单
			{
				MarketObject marketObject = new MarketObject(ToolsUtils
						.returnXMLStr("free_order"), oi.getCost(), MarketType.MANUAL);
				oi.getMarketList().add(marketObject);
			}
		}
	}

	/**
	 * 打印
	 *
	 * @param newOrder 原本向后台提交下单的order
	 * @param result   下单后服务器返回的order
	 */
	private void orderPrint(final Order newOrder, Order result, final boolean isJyj) {
		if (!TextUtils.isEmpty(wft_transaction_id)) {
			Log.i("创建威富通交易成功", "订单ID:" + result.getId() + "==威富通交易流水号:" + wft_transaction_id);
		}
		EventBus.getDefault().post(new PosEvent(Constant.EventState.NETORDER_OFF));
		Log.i("下单成功==》打印结账单跟厨房单", "");
		if (isJyj) {
			Log.i("结账页面下单成功", "orderId ==" + orderId + "===" + ToolsUtils.getPrinterSth(result));
		} else {
			Log.i("JYJ结账页面下单成功", "orderId ==" + orderId + "===" + ToolsUtils.getPrinterSth(result));
		}
		EventBus.getDefault().post(new PosEvent(Constant.EventState.CLEAN_CART));
		//System.out.println(ToolsUtils.getPrinterSth(result));
		EventBus.getDefault().post(new PosEvent(Constant.EventState.SELECT_FRAGMTNT_TABLE));

		newOrder.setId(result.getId());
		newOrder.setCallNumber(result.getCallNumber());
		newOrder.setCustomerAmount(newOrder.getCustomerAmount());
		if (!TextUtils.isEmpty(newOrder.getTableNames())) {
			newOrder.setTableNames(newOrder.getTableNames());
		}

		//下单成功取消免单
		posInfo.setFreeOrder(false);
		EventBus.getDefault().post(new PosEvent(Constant.EventState.SEND_INFO_KDS, ToolsUtils
				.cloneTo(result), tableName));

		EventBus.getDefault().post(new PosEvent(Constant.EventState.PRINTER_ORDER, ToolsUtils
				.cloneTo(newOrder)));

		new Handler().postDelayed(new Runnable() {
			public void run() {
				EventBus.getDefault()
						.post(new PosEvent(Constant.EventState.PRINTER_KITCHEN_ORDER, ToolsUtils
								.cloneTo(newOrder)));
			}
		}, 3000);

		new Handler().postDelayed(new Runnable() {
			public void run() {
				EventBus.getDefault()
						.post(new PosEvent(Constant.EventState.PRINT_CHECKOUT, ToolsUtils
								.cloneTo(newOrder)));
			}
		}, 3000);
		progressDialog.disLoading();
		setResult(Constant.EventState.SELECT_FRAGMTNT_ORDER);
		finish();
	}

	/**
	 * 先支付后下单：下单接口同时处理结账
	 */
	public static final int  MIN_CLICK_DELAY_TIME = 1000;//防止重复点击造成的重复下单
	private             long lastClickTime        = 0;

	private void creatOrdAndCheckOut() {
		if (posInfo.isMemberCheckOut()) {
			creatDeal(posInfo.getAccountMember());//微生活预览消费
		}
		long currentTime = Calendar.getInstance().getTimeInMillis();
		if (currentTime - lastClickTime <= Constant.MIN_CLICK_DELAY_TIME) {
			lastClickTime = currentTime;
			return;
		}
		progressDialog.showLoading(ToolsUtils.returnXMLStr("is_placing_an_order"));
		//是挂单
		if (cardRecord != null) {
			printOrder.setCardRecord(cardRecord);
		}
		printOrder.setPaymentStatus(PaymentStatus.PAYED);
		printOrder.setPaymentList(paymentLists);
		printOrder.setPaidAt(System.currentTimeMillis());
		//免单
		if (posInfo.isFreeOrder()) {
			BigDecimal freeMoney = new BigDecimal("0.00");
			printOrder.setCost("0");
			printOrder.setTake_money(freeMoney);
			printOrder.setServiceMoney(freeMoney);
			printOrder.setAvtive_money(new BigDecimal(Cart.getPriceSum())
					.setScale(2, BigDecimal.ROUND_DOWN));//优惠金额为订单总价
		} else {
			printOrder.setCost(total_money.setScale(2, BigDecimal.ROUND_DOWN)
					.toString());//注意，此处total_money已经是计算了服务费与营销活动后的金额
			printOrder.setTake_money(cart.getTakeMoney());
			if (anyCheckoutPrintMoney != null) {
				printOrder.setCost(anyCheckoutPrintMoney.setScale(2, BigDecimal.ROUND_DOWN)
						.toString());//注意,因为是允许输入任意金额支付所以这里需要把cost改为任意输入的金额
			}

			BigDecimal dish_active = getOrderDishPreferential(printOrder);//菜品优惠金额
			//            BigDecimal dish_active = new BigDecimal(cart.getPriceSum() - cart.getCost());
			printOrder.setServiceMoney(service_money.setScale(2, BigDecimal.ROUND_DOWN));
			printOrder.setAvtive_money(avtive_money.add(dish_active)
					.setScale(2, BigDecimal.ROUND_DOWN));
		}
		printOrder.setTableNames(tableName);
		if (store.isWaiMaiGuestInfo() && posInfo.getOrderType().equals("SALE_OUT") && posInfo
				.getCustomer() != null) {
			Customer customer = posInfo.getCustomer();
			if (customer != null) {
				printOrder.setCustomerAddress(customer.getCustomerAddress());
				printOrder.setCustomerPhoneNumber(customer.getCustomerPhoneNumber());
				printOrder.setCustomerName(customer.getCustomerName());
				printOrder.setOuterOrderid(customer.getCustomerOuterOrderId());
				printOrder.setSource(ToolsUtils.returnXMLStr("manual_input"));//手动录入标识
			}
		}
		if (accountMember != null) {
			accountMember.setMemberConsumeCost(new BigDecimal(printOrder.getCost()));
			printOrder.setMemberGrade(accountMember.getGradeName());
			printOrder.setMemberBizId(getBiz_id());
			printOrder.setAccountMember(accountMember);
			printOrder.setMemberid(accountMember.getUno());
			printOrder.setMemberName(accountMember.getName());
			printOrder.setMemberPhoneNumber(accountMember.getPhone());
		} else if (posInfo.getAccountMember() != null) {
			accountMember = ToolsUtils.cloneTo(posInfo.getAccountMember());
			printOrder.setMemberGrade(accountMember.getGradeName());
			accountMember.setMemberConsumeCost(new BigDecimal(printOrder.getCost()));
			printOrder.setMemberBizId(getBiz_id());
			printOrder.setAccountMember(accountMember);
			printOrder.setMemberid(accountMember.getUno());
			printOrder.setMemberName(accountMember.getName());
			printOrder.setMemberPhoneNumber(accountMember.getPhone());
		}
		printOrder.setCustomerAmount(posInfo.getCustomerAmount());
		printOrder.setComment(posInfo.getComment());
		printOrder.setWipingValue(wipingValue);
		final Order newOrder = ToolsUtils.cloneTo(printOrder);
		setOrderItemMarketInfo();
		if (printOrder != null) {
			printOrder.setId(Long.parseLong(orderId));
			orderService.createOrder(printOrder, new ResultCallback<Order>() {
				@Override
				public void onResult(Order result) {
					anyCheckoutPrintMoney = null;
					if (result != null) {
						if (store.isCreateOrderJyj()) {
							result.setJyjOrder(true);//将下单状态改为向JYJ下单
							createOrderJyj(result);
						} else {
							orderPrint(newOrder, result, false);
						}
						//                        kdsCreatOrder(result, tableName);
					} else {
						Log.i("订单下单失败", ToolsUtils.getPrinterSth(result));
						progressDialog.disLoading();
						showToast(ToolsUtils.returnXMLStr("orders_failed"));
					}
				}

				@Override
				public void onError(PosServiceException e) {
					Log.i("订单下单失败", e.getMessage());
					progressDialog.disLoading();
					showToast(ToolsUtils.returnXMLStr("orders_failed") + e.getMessage());
				}
			});

		}
	}


	private void createOrderJyj(final Order order) {
		progressDialog.showLoading(ToolsUtils.returnXMLStr("is_placing_an_order"));
		final Order newOrder = ToolsUtils.cloneTo(order);
		if (newOrder != null) {
			try {
				OrderService orderService = OrderService.getJyjOrderService();
				orderService.createOrder(newOrder, new ResultCallback<Order>() {
					@Override
					public void onResult(Order result) {
						progressDialog.disLoading();
						if (result != null) {
							orderPrint(result, result, true);
						} else {
							Log.e("JYJ下单", "JYJ下单失败:" + "null");
							showToast(ToolsUtils.returnXMLStr("orders_failed") + "," + ToolsUtils
									.returnXMLStr("sync_jyj_order_err"));
							order.setJyjPrintErrMessage(ToolsUtils
									.returnXMLStr("jyj_order_error_message"));
							orderPrint(order, order, true);
						}
					}

					@Override
					public void onError(PosServiceException e) {
						progressDialog.disLoading();
						Log.e("JYJ下单", "JYJ下单失败:" + e.getMessage());
						//
						showToast(ToolsUtils.returnXMLStr("orders_failed") + "," + ToolsUtils
								.returnXMLStr("sync_jyj_order_err") + "," + e.getMessage());
						order.setJyjPrintErrMessage(ToolsUtils
								.returnXMLStr("jyj_order_error_message"));
						orderPrint(order, order, true);
					}
				});
			} catch (PosServiceException e) {
				e.printStackTrace();
			}

		}
	}

	@OnClick({R.id.pay_btn, R.id.back_ll, R.id.active_ll})
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.pay_btn://结账
				UserAction.log("结账", context);
				pay();
				break;
			case R.id.back_ll://返回
				UserAction.log("返回", context);
				if (paymentLists.size() > 0) {
					showToast(ToolsUtils.returnXMLStr("checkout_exit_error"));
				} else {
					finish();
				}
				break;
			case R.id.active_ll://营销方案
				UserAction.log("营销方案", context);
				PayDialogUtil.getActiveDialog(context, new BigDecimal[]{
						temp_money}, printOrder, null, new DialogActiveCall() {
					@Override
					public void onOk(BigDecimal allMoney, BigDecimal activeMoney, String activeName) {
						avtive_money = activeMoney;
						//优惠的金额保留小数点
						avtive_money = avtive_money.setScale(2, BigDecimal.ROUND_DOWN);
						ticketDiscount.setText("-" + avtive_money);

						allMoney = ToolsUtils.wipeZeroMoney(allMoney);
						nopay_money = total_money = allMoney;
						//最后的总额去掉小数点

						//setOrderItemMarket(new MarketObject(activeName, avtive_money, MarketType.DISCOUNT));

						updateTicket();// onClick R.id.active_ll
						orderItems = ToolsUtils.cloneTo(printOrder.getItemList());

						//客显屏显示优惠金额
						printOrder.setAvtive_money(avtive_money);
						printOrder.setAvtiveName(activeName);
					}
				});
				break;
		}
	}

	/**
	 * 会员登录之后改变总价格
	 */
	public void updateTotalPrice() {

	}


}
