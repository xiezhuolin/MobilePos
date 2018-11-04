package cn.acewill.mobile.pos.ui.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.acewill.paylibrary.EPayTask;
import com.acewill.paylibrary.MicropayTask;
import com.acewill.paylibrary.PayReqModel;
import com.acewill.paylibrary.epay.EPayResult;

import org.greenrobot.eventbus.EventBus;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArrayList;

import cn.acewill.mobile.pos.R;
import cn.acewill.mobile.pos.base.adapter.BaseAdapter;
import cn.acewill.mobile.pos.common.PowerController;
import cn.acewill.mobile.pos.common.PrinterDataController;
import cn.acewill.mobile.pos.config.MyApplication;
import cn.acewill.mobile.pos.config.Store;
import cn.acewill.mobile.pos.exception.PosServiceException;
import cn.acewill.mobile.pos.interfices.CreatDealBack;
import cn.acewill.mobile.pos.interfices.DialogCall;
import cn.acewill.mobile.pos.interfices.DialogMTCallback;
import cn.acewill.mobile.pos.interfices.DialogTCallback;
import cn.acewill.mobile.pos.interfices.DishCheckCallback;
import cn.acewill.mobile.pos.interfices.InterfaceDialog;
import cn.acewill.mobile.pos.interfices.KeyCallBack;
import cn.acewill.mobile.pos.interfices.PermissionCallback;
import cn.acewill.mobile.pos.model.PaymentList;
import cn.acewill.mobile.pos.model.WftRespOnse;
import cn.acewill.mobile.pos.model.dish.Cart;
import cn.acewill.mobile.pos.model.dish.Dish;
import cn.acewill.mobile.pos.model.dish.DishCount;
import cn.acewill.mobile.pos.model.event.PosEvent;
import cn.acewill.mobile.pos.model.order.CardRecord;
import cn.acewill.mobile.pos.model.order.Order;
import cn.acewill.mobile.pos.model.payment.Payment;
import cn.acewill.mobile.pos.model.payment.PaymentCategory;
import cn.acewill.mobile.pos.model.wsh.Account;
import cn.acewill.mobile.pos.model.wsh.WshDealPreview;
import cn.acewill.mobile.pos.service.OrderService;
import cn.acewill.mobile.pos.service.PosInfo;
import cn.acewill.mobile.pos.service.ResultCallback;
import cn.acewill.mobile.pos.service.retrofit.response.ValidationResponse;
import cn.acewill.mobile.pos.ui.activity.CheckOutNewAty;
import cn.acewill.mobile.pos.ui.activity.ScanActivity;
import cn.acewill.mobile.pos.utils.CheckOutUtil;
import cn.acewill.mobile.pos.utils.Constant;
import cn.acewill.mobile.pos.utils.DialogUtil;
import cn.acewill.mobile.pos.utils.FileLog;
import cn.acewill.mobile.pos.utils.ScanGunKeyEventHelper;
import cn.acewill.mobile.pos.utils.TimeUtil;
import cn.acewill.mobile.pos.utils.ToolsUtils;
import cn.acewill.mobile.pos.widget.ProgressDialogF;


/**
 * 支付方式列表
 * Created by aqw on 2016/11/25.
 */
public class PayTypeAdapter extends BaseAdapter implements
		ScanGunKeyEventHelper.OnScanSuccessListener {

	private static final String TAG = "PayTypeAdapter";
	private ProgressDialogF progressDialog;

	public static final  int               FAIL_PAY     = 0;//支付失败
	private static final int               FAIL_ORDER   = 1;//下单失败
	private static final int               FAIL_LKL_PAY = 2;//拉卡拉失败
	private static final int               FAIL_WFT_PAY = 3;//威富通失败
	private              List<PaymentList> paymentLists = new ArrayList<>();
	private CheckOutNewAty aty;
	private InTask         task;
	private OutTask        outTask;
	private BigDecimal     printMoney;
	public boolean isDebug = false;
	private int     payTypeID;
	private Payment pt;
	private int pay_channel = PayReqModel.PTID_SSS_WEIXIN;// 支付宝：PayReqModel.PTID_SSS_ALI，微信:PTID_SSS_WEIXIN
	private Dialog                paydialog;
	private ScanGunKeyEventHelper mScanGunKeyEventHelper;
	private boolean scaning = false;//防止扫码枪扫多次
	private String code_scan;//反扫返回的二维码号，用于重试使用
	private Dialog failDialog;
	private Cart   cart;
	private Order  order;//用于副屏显示数据

	private final Timer timer = new Timer();//轮询拉卡拉支付交易情况
	private TimerTask lklTask;
	private Handler   handler;

	private Timer timerwft = new Timer();//轮询威富通支付交易情况
	private TimerTask wftTask;
	private Handler   wfthandler;
	private CardRecord cardRecord = null;
	private int    totalFee;
	private String orderTradeNo;
	private String autoCode;
	private String paymentNo;//流水号
	private String transactionNo = ""; //微信，支付宝，刷卡等方式的支付流水号，特别长的一段字符串

	private int delayedTime = 2 * 1000;//延迟2秒
	private int cycleTime   = 3 * 1000;//周期循环时间

	private PosInfo posInfo;

	boolean isDisCount = false;//支付时检测菜品沽清状态 如果成功 其它的支付方式就不去检测沽清状态正常支付下单

	private int wftPayType = 3;//选择威富通 是微信支付还是支付宝支付  0是微信 1是支付宝
	private Store store;
	private static final int     REQUEST_CODE = 110;
	private              boolean isClickPay   = true;

	public boolean isClickPay() {
		return isClickPay;
	}

	public void setClickPay(boolean clickPay) {
		isClickPay = clickPay;
	}


	public PayTypeAdapter(Context context) {
		super(context);
		cart = Cart.getInstance();
		progressDialog = new ProgressDialogF(context);
		aty = (CheckOutNewAty) context;
		store = Store.getInstance(context);
		mScanGunKeyEventHelper = new ScanGunKeyEventHelper(this);
		isDisCount = false;
		posInfo = PosInfo.getInstance();
	}


	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		final Payment    payment = (Payment) getItem(position);
		if (convertView == null) {
			holder = new ViewHolder();

			convertView = LayoutInflater.from(context).inflate(R.layout.item_paytype, null);
			holder.type_name = (TextView) convertView.findViewById(R.id.type_name);
			holder.money = (TextView) convertView.findViewById(R.id.money);
			holder.lin_count = (LinearLayout) convertView.findViewById(R.id.lin_count);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		int        size   = paymentLists.size();
		BigDecimal money  = new BigDecimal("0.00");
		boolean    isHave = false;


		for (int i = 0; i < size; i++) {
			PaymentList payType = paymentLists.get(i);
			if (payment.getId() == 5 && (payType.getPaymentTypeId() == 3 || payType
					.getPaymentTypeId() == 4 || payType.getPaymentTypeId() == 5)) {
				isHave = true;
				money = money.add(payType.getValue());
				holder.lin_count.setBackgroundColor(res.getColor(R.color.yellow_bg));
				holder.money.setVisibility(View.VISIBLE);
			} else if (payment.getId() == payType.getPaymentTypeId()) {
				isHave = true;
				money = money.add(payType.getValue());
				holder.lin_count.setBackgroundColor(res.getColor(R.color.yellow_bg));
				holder.money.setVisibility(View.VISIBLE);
			}

		}

		if (!isHave) {
			holder.lin_count.setBackgroundColor(res.getColor(R.color.white));
			holder.money.setVisibility(View.INVISIBLE);
		}
		if (money.compareTo(BigDecimal.ZERO) > 0) {
			holder.money.setText("￥" + money);
		}

		holder.type_name.setText(payment.getName());

		convertView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (isClickPay()) {
					payTypeID = payment.getId();
					pt = payment;
					payTypeLogic(payment);
				}
			}
		});

		return convertView;
	}


	public BigDecimal getPrintMoney() {
		return printMoney;
	}

	public Payment getPt() {
		return pt;
	}

	public int getPayChange() {
		return pay_channel;
	}

	public int getTotalFee() {
		return totalFee;
	}

	/**
	 * 当支付dialog被关闭后与之相匹配的倒计时查询服务也应当被关闭
	 */
	private void payDialogDismissLis() {
		if (paydialog != null) {
			paydialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
				@Override
				public void onDismiss(DialogInterface dialog) {
					wftTimerCancel();
					if (failDialog != null && failDialog.isShowing()) {
						failDialog.dismiss();
					}
					isShowFailDialog = false;
					if (task != null)
						task.cancel(true);
					if (outTask != null)
						outTask.cancel(true);
				}
			});
		}
	}

	private Long orderNewId = 0L;

	public void getOrderId(final String barcode, final boolean isAgain) {
		try {
			OrderService orderService = OrderService.getInstance();
			orderService.getNextOrderId(new ResultCallback<Long>() {//扫码成功之后获取订单号
				@Override
				public void onResult(Long result) {
					if (result > 0) {
						orderNewId = result;
						outPay(barcode, result, isAgain);
					} else {
						setClickPay(true);
					}
				}

				@Override
				public void onError(PosServiceException e) {
					setClickPay(true);
					Log.i("获取订单Id失败", e.getMessage());
					EventBus.getDefault()
							.post(new PosEvent(Constant.EventState.ERR_CREATE_ORDERID_FILURE));
					//                    aty.showToast(ToolsUtils.returnXMLStr("get_order_id_failure"));
				}
			});
		} catch (PosServiceException e) {
			e.printStackTrace();
		}
	}

	public void scanSuccess(String scanStr) {
		sb.append(scanStr);
		String code = sb.toString().trim();
		if (code.length() == 18) {
			if (!scaning) {
				System.out.println("===>>" + code);
				scaning = true;
				code_scan = code;
				if (ToolsUtils.isReverseCheckOut(aty.reverseCheckOutFlag)) {
					getOrderId(code_scan, false);
					Log.e(TAG, "getOrderId1");
					FileLog.log(TAG + ">getOrderId1");
				} else {
					outPay(code_scan, 0L, false);
				}
			}
		}
	}

	private StringBuffer sb = new StringBuffer();

	@Override
	public void onScanSuccess(String barcode) {
		Log.e("反扫code返回:", barcode);
		scanSuccess(barcode);
		//        if (!TextUtils.isEmpty(barcode)) {
		//            if (!scaning) {
		//                scaning = true;
		//                code_scan = barcode;
		//                if(ToolsUtils.isReverseCheckOut(aty.reverseCheckOutFlag))
		//                {
		//                    getOrderId(barcode,false);
		//                }
		//                else{
		//                    outPay(barcode,0L ,false);
		//                }
		//            }
		//        }
	}

	class ViewHolder {
		LinearLayout lin_count;
		TextView     type_name;
		TextView     money;
	}

	public void setPayTypeList(List<PaymentList> paymentLists) {
		if (paymentLists != null && paymentLists.size() > 0) {
			this.paymentLists = paymentLists;
			notifyDataSetChanged();
		} else {
			this.paymentLists = new ArrayList<>();
		}
	}


	public void refreshDish(List<DishCount> result, List<Dish> dishs) {
		//刷新菜品数据,显示沽清
		String names = Cart.getInstance().getItemNameByDids((ArrayList) result, dishs);
		MyApplication.getInstance().ShowToast(ToolsUtils
				.returnXMLStr("the_following_items_are_not_enough") + "\n\n" + names + "。\n\n" + ToolsUtils
				.returnXMLStr("please_re_order"));
		if (progressDialog != null) {
			progressDialog.disLoading();
		}
		Log.i("以下菜品份数不足:", names + "====<<");
	}


	//选择支付
	private void payTypeLogic(final Payment payment) {
		scaning = false;
		if (aty.nopay_money.compareTo(BigDecimal.ZERO) == 0 || aty.nopay_money
				.compareTo(BigDecimal.ZERO) == -1) {
			aty.showToast(ToolsUtils.returnXMLStr("is_pay_please_check_checkout"));
			return;
		}
		if (!isDisCount) {
			final CheckOutUtil checUtil = new CheckOutUtil(aty, payment);
			checUtil.getDishStock(Cart.getDishItemList(), new DishCheckCallback() {
				@Override
				public void haveStock() {
					isDisCount = true;
					payOrder(payment);
				}

				@Override
				public void noStock(List dataList) {
					refreshDish(dataList, Cart.getDishItemList());
				}
			});
		} else if (isDisCount) {
			payOrder(payment);
		}

	}

	private void payOrder(final Payment payment) {
		//现金类型
		if (payment.getCategory() == PaymentCategory.CASH) {

			DialogUtil.keyNumDialog(context, aty.nopay_money, new KeyCallBack() {
				@Override
				public void onOk(Object o) {
					printMoney = (BigDecimal) o;
					if (printMoney.compareTo(BigDecimal.ZERO) == 1) {
						if (payment.getId() == 0) {
							PrinterDataController.getInstance().openCashBox();
						}
						aty.changeMoney(printMoney, printMoney
								.min(aty.nopay_money), payment, "", transactionNo, null, null);//现金类型
					} else {
						aty.showToast(ToolsUtils.returnXMLStr("please_enter_a_valid_amount"));
					}
				}
			});
			return;
		}
		switch (payment.getId()) {
			case 1://支付宝
				if (store.isCheckOutInPutMoney()) {
					DialogUtil.keyNumDialog(context, aty.nopay_money, new KeyCallBack() {
						@Override
						public void onOk(Object o) {
							printMoney = (BigDecimal) o;
							if (printMoney.compareTo(BigDecimal.ZERO) == 1) {
								pay_channel = PayReqModel.PTID_SSS_ALI;
								scanLogic(payment.getId());
							} else {
								aty.showToast(ToolsUtils
										.returnXMLStr("please_enter_a_valid_amount"));
							}
						}
					});
				} else {
					printMoney = aty.nopay_money;
					if (printMoney.compareTo(BigDecimal.ZERO) == 1) {
						pay_channel = PayReqModel.PTID_SSS_ALI;
						scanLogic(payment.getId());
					} else {
						aty.showToast(ToolsUtils.returnXMLStr("please_enter_a_valid_amount"));
					}
				}
				break;
			case 2://微信
				if (store.isCheckOutInPutMoney()) {
					DialogUtil.keyNumDialog(context, aty.nopay_money, new KeyCallBack() {
						@Override
						public void onOk(Object o) {
							printMoney = (BigDecimal) o;
							if (printMoney.compareTo(BigDecimal.ZERO) == 1) {
								pay_channel = PayReqModel.PTID_SSS_WEIXIN;
								scanLogic(payment.getId());
							} else {
								aty.showToast(ToolsUtils
										.returnXMLStr("please_enter_a_valid_amount"));
							}
						}
					});
				} else {
					printMoney = aty.nopay_money;
					if (printMoney.compareTo(BigDecimal.ZERO) == 1) {
						pay_channel = PayReqModel.PTID_SSS_WEIXIN;
						scanLogic(payment.getId());
					} else {
						aty.showToast(ToolsUtils.returnXMLStr("please_enter_a_valid_amount"));
					}
				}
				break;
			case -8://威富通
				if (store.isCheckOutInPutMoney()) {
					DialogUtil.keyNumDialog(context, aty.nopay_money, new KeyCallBack() {
						@Override
						public void onOk(Object o) {
							printMoney = (BigDecimal) o;
							if (printMoney.compareTo(BigDecimal.ZERO) == 1) {
								pay_channel = PayReqModel.PTID_SSS_WEIFUTONG;
								DialogUtil.switchWftPay(context, new KeyCallBack() {
									@Override
									public void onOk(Object o) {
										wftPayType = (int) o;
										aty.setWftPayType(wftPayType);
										scanLogic(payment.getId());
									}
								});
							} else {
								aty.showToast(ToolsUtils
										.returnXMLStr("please_enter_a_valid_amount"));
							}
						}
					});
				} else {
					printMoney = aty.nopay_money;
					if (printMoney.compareTo(BigDecimal.ZERO) == 1) {
						pay_channel = PayReqModel.PTID_SSS_WEIFUTONG;
						DialogUtil.switchWftPay(context, new KeyCallBack() {
							@Override
							public void onOk(Object o) {
								wftPayType = (int) o;
								aty.setWftPayType(wftPayType);
								scanLogic(payment.getId());
							}
						});
					} else {
						aty.showToast(ToolsUtils.returnXMLStr("please_enter_a_valid_amount"));
					}
				}
				break;
			//挂账
			case -34:
				logicChargeOffs(payment);
				break;
			case 3:
			case 4:
			case 5://储值、积分、优惠券
				showMemberCheckOutDialog(payment);
				break;
			case -32://美团
				DialogUtil.meiTuanDialog(context, Long
						.parseLong(aty.orderId), aty.nopay_money, false, aty.addValidation, new DialogMTCallback() {
					@Override
					public void onCheckout(BigDecimal money, boolean isCheckOut, CopyOnWriteArrayList<ValidationResponse> addValidationLists) {
						printMoney = money;
						//                        aty.setAddValidation(addValidationLists);
						aty.changeMoney(printMoney, printMoney, payment, "", transactionNo, null, addValidationLists);//美团
						//                        addValidationList = ToolsUtils.cloneTo(addValidationLists);
						//                        creatOrdAndCheckOut();
					}
				});
				break;
			case 7://银行卡
				DialogUtil.keyNumDialog(context, aty.nopay_money, new KeyCallBack() {
					@Override
					public void onOk(Object o) {
						printMoney = (BigDecimal) o;
						if (printMoney.compareTo(BigDecimal.ZERO) == 1) {
							aty.changeMoney(printMoney, printMoney, payment, "", transactionNo, null, null);//银行卡
						} else {
							aty.showToast(ToolsUtils.returnXMLStr("please_enter_a_valid_amount"));
						}
					}
				});
				break;
			case -9://拉卡拉
				break;
			case -10://储值卡
				DialogUtil.keyNumDialog(context, aty.nopay_money, new KeyCallBack() {
					@Override
					public void onOk(Object o) {
						printMoney = (BigDecimal) o;
						if (printMoney.compareTo(BigDecimal.ZERO) == 1) {
							DialogUtil.showMemberDialog(context, aty.nopay_money, new DialogCall() {
								@Override
								public void onOk(Object obj) {
									if (((String) obj).equals("Success")) {
										aty.showToast(ToolsUtils.returnXMLStr("pay_success"));
										transactionNo = ToolsUtils.completionOrderId(aty.orderId);
										aty.changeMoney(printMoney, printMoney, payment, "", transactionNo, null, null);//储值卡
									}
								}

								@Override
								public void onCancle(Object obj) {
								}
							});
						} else {
							aty.showToast(ToolsUtils.returnXMLStr("please_enter_a_valid_amount"));
						}
					}
				});
				break;
			default:
				aty.showToast(ToolsUtils.returnXMLStr("this_method_is_not_supported"));
				break;
		}
	}

	private void showMemberCheckOutDialog(final Payment payment) {
		//显示会员结账的对话框
		aty.removeMemberPay();//每一次重新选择会员支付方式,都要先将原先的会员支付给清空,然后重新操作
		DialogUtil
				.memberDialog(context, payTypeID, aty.nopay_money, aty.orderItems, false, new CreatDealBack() {
					@Override
					public void onDeal(String bizid, WshDealPreview result, BigDecimal money, boolean isCheckOut, Account acount, List<Payment> memberPayMent) {//money为消费会员金额,bizid是业务流水号，提交交易时使用
						aty.changeMoney(money, money, payment, bizid, transactionNo, memberPayMent, null);//储值、积分、优惠券
						posInfo.setMemberCheckOut(false);
						aty.setBiz_id(bizid);
						aty.setWshDealPreview(result);
						posInfo.setBizId(bizid);
						posInfo.setWshDealPreview(result);
						if (acount != null) {
							if (isCheckOut) {
								acount.setMemberConsumeCost(money);
							}
							aty.setAccountMember(acount);
						}
						if (!isCheckOut) {
							posInfo.setMemberCheckOut(true);
						}
					}
				});
	}

	//扫码判断
	private void scanLogic(int payTypeID) {
		setClickPay(false);
		// 正扫
		if (Store.getInstance(context).isFront()) {

			progressDialog.showLoading("");
			String      storeName = Store.getInstance(context).getStoreName();
			PayReqModel model     = new PayReqModel();
			task = new InTask();
			model.totalAmount = printMoney.setScale(2, BigDecimal.ROUND_DOWN).doubleValue();
			model.orderNo = aty.orderId;
			model.wxGoodsDetail = TextUtils.isEmpty(storeName) ? ToolsUtils
					.returnXMLStr("product_details") : storeName;
			model.isDebug = isDebug;
			model.payType = pay_channel;
			model.authCode = "";
			model.aliGoodsItem = aty.aliGoodsItem;
			model.storeName = TextUtils.isEmpty(storeName) ? ToolsUtils
					.returnXMLStr("food_consumption") : storeName;
			model.storeId = Store.getInstance(context).getStoreId();
			model.terminalId = Store.getInstance(context).getDeviceName();
			if (wftPayType == 0 || wftPayType == 1) {
				model.pay_type = wftPayType;
				createWtfZsQrCode();
			} else {
				task.execute(model);
			}
		} else {
			//调用扫码枪
			scanGunDialog();
		}
	}

	/**
	 * 挂账
	 */
	private void logicChargeOffs(final Payment payment) {
		//判断是否有退菜权限
		PowerController
				.isLogicPower(context, PowerController.REFUND_DISH, new PermissionCallback() {
					@Override
					public void havePermission() {
						DialogUtil.cardRecordDialog(context, new DialogTCallback() {
							@Override
							public void onConfirm(Object o) {
								cardRecord = (CardRecord) o;
								if (cardRecord != null) {
									printMoney = aty.total_money;
									aty.setCardRecord(cardRecord);
									aty.changeMoney(printMoney, printMoney
											.min(aty.nopay_money), payment, "", transactionNo, null, null);//挂账
									//                            chargeOffs(payment,cardRecord);
								}
							}

							@Override
							public void onCancle() {

							}
						});
					}

					@Override
					public void withOutPermission() {

					}
				});
	}

	/**
	 * 正扫生成威富通二维码
	 */
	private void createWtfZsQrCode() {
		try {
			setClickPay(false);
			progressDialog.showLoading("");
			OrderService orderService = OrderService.getInstance();
			totalFee = printMoney.multiply(new BigDecimal("100")).intValue();


			String orderi = "";
			//            String body = "";
			if (ToolsUtils.isReverseCheckOut(aty.reverseCheckOutFlag)) {
				orderi = orderNewId + "";
				//                body = "1231*321";
			} else {
				orderi = aty.orderId;
				//                body = ToolsUtils.handleCarDish(cart.getDishItemList());
			}
			orderTradeNo = ToolsUtils.completionOrderId(orderi) + "_" + TimeUtil.getTimeToken();

			orderService
					.createCodeUrl(wftPayType, totalFee, orderTradeNo, new ResultCallback<WftRespOnse>() {
						@Override
						public void onResult(WftRespOnse result) {
							progressDialog.disLoading();
							if (result != null && !TextUtils.isEmpty(result.getCodeImgUrl())) {
								//                        createWFTQrCode(result.getCodeImgUrl());
							} else {
								setClickPay(true);
								MyApplication.getInstance().ShowToast(ToolsUtils
										.returnXMLStr("creat_qrcode_failure") + "!");
								Log.e("正扫", "生成WFT二维码失败");
							}
						}

						@Override
						public void onError(PosServiceException e) {
							setClickPay(true);
							progressDialog.disLoading();
							MyApplication.getInstance().ShowToast(ToolsUtils
									.returnXMLStr("creat_qrcode_failure") + "," + e
									.getMessage() + "!");
							Log.e("正扫", "生成WFT二维码失败");
						}
					});
		} catch (PosServiceException e) {
			e.printStackTrace();
			setClickPay(true);
		}
	}

	//    private void createWFTQrCode(String qrCode) {
	//        if (!TextUtils.isEmpty(qrCode)) {
	//            Bitmap qrCodeBig = CreateImage.convertStringToIcon(qrCode);
	//            timerwft = new Timer();
	//            cycleWFTPay(orderTradeNo, orderTradeNo);
	//            paydialog = DialogUtil.scanPadDialog(context, payTypeID, task, qrCode, qrCodeBig, new InterfaceDialog() {
	//                @Override
	//                public void onCancle() {
	//                    setClickPay(true);
	//                    Log.i("正扫手动下单：", "");
	//                    if (task != null)
	//                        task.cancel(true);
	//                    if (outTask != null)
	//                        outTask.cancel(true);
	//                }
	//
	//                @Override
	//                public void onOk(Object o) {
	//                    Log.i("正扫重试：", "");
	//                    setClickPay(true);
	//                    //                            scanLogic();
	//                }
	//            });
	//            payDialogDismissLis();
	//
	//        }
	//    }


	// 反扫支付(扫码枪扫完后调用这个方法)
	private void outPay(String code, Long orderNewId, boolean isQuery) {
		MyApplication.getInstance().ShowToast(ToolsUtils.returnXMLStr("being_paid"));
		String      storeName = Store.getInstance(context).getStoreName();
		PayReqModel model     = new PayReqModel();
		outTask = new OutTask();
		model.totalAmount = printMoney.setScale(2, BigDecimal.ROUND_DOWN).doubleValue();
		if (ToolsUtils.isReverseCheckOut(aty.reverseCheckOutFlag)) {
			model.orderNo = orderNewId + "";
		} else {
			model.orderNo = aty.orderId;
		}
		model.wxGoodsDetail = TextUtils.isEmpty(storeName) ? ToolsUtils
				.returnXMLStr("product_details") : storeName;
		model.isDebug = isDebug;
		model.payType = pay_channel;
		model.authCode = code;
		model.aliGoodsItem = aty.aliGoodsItem;
		model.isQuery = isQuery;
		model.storeName = TextUtils.isEmpty(storeName) ? ToolsUtils
				.returnXMLStr("food_consumption") : storeName;
		model.storeId = Store.getInstance(context).getStoreId();
		model.terminalId = Store.getInstance(context).getDeviceName();

		outTask.execute(model);
	}

	/**
	 * 正扫(生成二维码)
	 *
	 * @author aqw
	 */
	class InTask extends EPayTask {
		@Override
		protected void onPostExecute(EPayResult result) {
			progressDialog.disLoading();
			if (result != null && !result.success) {
				setClickPay(true);
				EventBus.getDefault().post(new PosEvent(Constant.EventState.REFRESH_ORDERID));
				hideDialog();
				Log.e(TAG, "getOrderId3");
				FileLog.log(TAG + ">getOrderId3");
				aty.getOrderId(false);
				aty.showToast(ToolsUtils.returnXMLStr("creat_qrcode_failure") + "," + result
						.getErrMessage() + "!");
				Log.e("正扫", "生成二维码失败");
			} else if (EPayResult.PAY_STATUS_COMPLETE
					.equalsIgnoreCase(result.trade_status)) {// 支付宝支付成功
				aty.showToast(ToolsUtils.returnXMLStr("pay_success"));
				//                paymentNo = result.trade_no;
				paymentNo = aty.orderId;
				transactionNo = result.trade_no;
				aty.changeMoney(printMoney, printMoney, pt, paymentNo, transactionNo, null, null);//支付宝
				hideDialog();
			} else if (result != null && result.success) {// 微信支付成功
				aty.showToast(ToolsUtils.returnXMLStr("pay_success"));
				paymentNo = result.transaction_id;
				transactionNo = result.transaction_id;
				aty.changeMoney(printMoney, printMoney, pt, result.transaction_id, transactionNo, null, null);// 微信
				hideDialog();
			} else {
				setClickPay(true);
				aty.getOrderId(false);
				Log.e(TAG, "getOrderId4");
				FileLog.log(TAG + ">getOrderId4");
				if (pay_channel != PayReqModel.PTID_SSS_WEIXIN) {
					aty.showToast(ToolsUtils.returnXMLStr("pay_failure"));
					Log.e("正扫", "支付失败");
					exceptMethod(FAIL_PAY);
				}
			}
		}

		@Override
		protected void onProgressUpdate(String... values) {
			progressDialog.disLoading();
			if (!TextUtils.isEmpty(values[0])) {
				System.out.println("二维码是:" + values[0]);

				paydialog = DialogUtil
						.scanPadDialog(context, payTypeID, task, values[0], null, new InterfaceDialog() {
							@Override
							public void onCancle() {
								setClickPay(true);
								Log.i("正扫手动下单：", "");
								if (task != null)
									task.cancel(true);
								if (outTask != null)
									outTask.cancel(true);
							}

							@Override
							public void onOk(Object o) {
								setClickPay(true);
								Log.i("正扫重试：", "");
								//                            scanLogic();
							}
						});
				payDialogDismissLis();
			}
		}
	}

	/**
	 * 反扫
	 *
	 * @author aqw
	 */
	class OutTask extends MicropayTask {
		@Override
		protected void onPostExecute(EPayResult result) {
			if (result != null && !result.success && result.WFT) {
				setClickPay(true);
				EventBus.getDefault().post(new PosEvent(Constant.EventState.REFRESH_ORDERID));
				hideDialog();
				aty.getOrderId(false);
				Log.e(TAG, "getOrderId5");
				FileLog.log(TAG + ">getOrderId5");
				//                aty.showToast(ToolsUtils.returnXMLStr("pay_failure")+"," + result.getErrMessage() + "!");
				scaning = false;
				EventBus.getDefault()
						.post(new PosEvent(Constant.EventState.ERR_GET_ONLINE_PAY_STATE_FAILURE));
				Log.e("反扫", "支付失败");
			} else if (result != null && EPayResult.PAY_STATUS_COMPLETE
					.equalsIgnoreCase(result.trade_status)) {// 支付宝支付成功
				aty.showToast(ToolsUtils.returnXMLStr("pay_success"));
				//                paymentNo = result.trade_no;
				paymentNo = aty.orderId;
				transactionNo = result.trade_no;
				aty.changeMoney(printMoney, printMoney, pt, aty.orderId, transactionNo, null, null);// 支付宝
				hideDialog();
			} else if (result != null && result.success && !result.WFT) {// 微信支付成功
				aty.showToast(ToolsUtils.returnXMLStr("pay_success"));
				paymentNo = result.transaction_id;
				transactionNo = result.transaction_id;
				aty.changeMoney(printMoney, printMoney, pt, result.transaction_id, transactionNo, null, null);// 微信
				hideDialog();
			} else if (result != null && result.weifutongPayStart)// 威富通成功
			{
				aty.showToast(ToolsUtils.returnXMLStr("auth_code_success"));
				hideDialog();
				totalFee = printMoney.multiply(new BigDecimal("100")).intValue();

				String orderi = "";
				String body   = "";
				if (ToolsUtils.isReverseCheckOut(aty.reverseCheckOutFlag)) {
					orderi = orderNewId + "";
					body = "1231*321";
				} else {
					orderi = aty.orderId;
					body = ToolsUtils.handleCarDish(cart.getDishItemList());
				}
				orderTradeNo = ToolsUtils.completionOrderId(orderi) + "_" + TimeUtil.getTimeToken();
				autoCode = result.code;
				paymentNo = orderTradeNo;
				transactionNo = orderTradeNo;
				//				getWayWeiFuTong(autoCode, body, totalFee, orderTradeNo);
			} else {
				setClickPay(true);
				aty.getOrderId(false);
				Log.e(TAG, "getOrderId6");
				FileLog.log(TAG + ">getOrderId6");
				if (pay_channel != PayReqModel.PTID_SSS_WEIXIN) {
					aty.showToast(ToolsUtils.returnXMLStr("pay_failure"));
					Log.e("反扫", "支付失败");
					exceptMethod(FAIL_PAY);
				}
			}
			sb.setLength(0);
		}
	}

	/**
	 * 反扫调用摄像头
	 */
	private void scanGunDialog() {
		Intent intent = new Intent(context, ScanActivity.class);
		((CheckOutNewAty) context).startActivityForResult(intent, REQUEST_CODE);
	}


	/**
	 * 反扫重试
	 */
	private void scanRetry(boolean isAgain) {
		if (ToolsUtils.isReverseCheckOut(aty.reverseCheckOutFlag)) {
			Log.e(TAG, "getOrderId2");
			FileLog.log(TAG + ">getOrderId2");
			getOrderId(code_scan, isAgain);
		} else {
			outPay(code_scan, 0L, isAgain);
		}
		scanGunDialog();
	}

	/**
	 * 支付或下单失败弹框
	 *
	 * @param t
	 */
	private boolean isShowFailDialog = false;

	public void exceptMethod(final int t) {
		setClickPay(true);
		hideDialog();

		if (failDialog == null || !failDialog.isShowing() && !isShowFailDialog) {
			failDialog = DialogUtil
					.createDialog(context, R.layout.dialog_payfail, 8, LinearLayout.LayoutParams.WRAP_CONTENT);
			isShowFailDialog = true;
		}

		TextView msg   = (TextView) failDialog.findViewById(R.id.msg);
		TextView retry = (TextView) failDialog.findViewById(R.id.retry);
		TextView creat = (TextView) failDialog.findViewById(R.id.creat);

		retry.setText(ToolsUtils.returnXMLStr("common_cancel"));
		creat.setVisibility(View.GONE);

		failDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
				progressDialog.disLoading();
				isShowFailDialog = false;
				if (task != null)
					task.cancel(true);
				if (outTask != null)
					outTask.cancel(true);
			}
		});

		switch (t) {
			case FAIL_PAY://支付失败
				//                creat.setVisibility(View.VISIBLE);
				msg.setText(ToolsUtils.returnXMLStr("pay_failure_or_timeout_please_try_again"));
				if (task != null)
					task.cancel(true);
				if (outTask != null)
					outTask.cancel(true);
				Log.i("支付失败", "点击重试");
				break;
			case FAIL_ORDER://下单失败
				msg.setText(ToolsUtils.returnXMLStr("checkout_faliure_please_try_again"));
				break;
			case FAIL_LKL_PAY://拉卡拉支付失败
				creat.setVisibility(View.VISIBLE);
				msg.setText("拉卡拉支付失败或超时，请取消重试");
				break;
			case FAIL_WFT_PAY://威富通支付失败
				creat.setVisibility(View.VISIBLE);
				msg.setText(ToolsUtils.returnXMLStr("wft_pay_failure"));
				break;
		}

		retry.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				failDialog.dismiss();
				switch (t) {
					case FAIL_PAY://支付失败
						if (task != null)
							task.cancel(true);
						if (outTask != null)
							outTask.cancel(true);
						Log.i("支付失败", "点击重试");

						scanRetry(true);
						break;
					case FAIL_LKL_PAY://拉卡拉支付失败
						Log.i("拉卡拉支付失败", "点击重试");
						break;
					case FAIL_WFT_PAY:
						Log.i("威富通支付失败", "点击取消");
						//                        if(!TextUtils.isEmpty(autoCode))
						//                        {
						//                            getWayWeiFuTong(autoCode, ToolsUtils.handleCarDish(cart.getDishItemList()), totalFee, orderTradeNo);//交易失败后重新获取支付信息
						//                        }
						break;
				}
			}
		});

		//        //出现查询支付状态超时时，手动点击确认成功
		//        creat.setOnClickListener(new View.OnClickListener() {
		//            @Override
		//            public void onClick(View v) {
		//                failDialog.dismiss();
		//                if (task != null)
		//                    task.cancel(true);
		//                if (outTask != null)
		//                    outTask.cancel(true);
		//                wftTimerCancel();
		//                aty.changeMoney(printMoney, printMoney, pt, paymentNo,null);
		//                Log.i("支付失败", "手动确认");
		//            }
		//        });

	}

	private void cancelFailDialog() {
		if (failDialog != null && failDialog.isShowing()) {
			failDialog.dismiss();
		}
	}

	//隐藏dialog
	public void hideDialog() {
		if (paydialog != null && paydialog.isShowing()) {
			paydialog.dismiss();
		}
	}

	public Order getOrder() {
		return order;
	}

	public void setOrder(Order order) {
		this.order = order;
	}

	private void wftTimerCancel() {
		hideDialog();
		if (timerwft != null) {
			//			queryWeifuTongCount = 0;
			timerwft.cancel();
			timerwft = null;
		}
	}

	//	private void getWayWeiFuTong(String auth_code, String body, Integer total_fee, final String out_trade_no) {
	//		try {
	//			progressDialog.showLoading(ToolsUtils.returnXMLStr("query_pay_state"));
	//			StoreBusinessService storeBusinessService = StoreBusinessService.getInstance();
	//			storeBusinessService
	//					.getWayWeiFuTong(auth_code, body, total_fee, out_trade_no, new ResultCallback<WeiFuTongResponse>() {
	//						@Override
	//						public void onResult(final WeiFuTongResponse result) {
	//							new Handler().postDelayed(new Runnable() {
	//								public void run() {
	//									progressDialog.disLoading();
	//									if (result != null) {
	//										timerwft = new Timer();
	//										cycleWFTPay(out_trade_no, result.getTransaction_id());
	//										//                        String transaction_id = result.getTransaction_id();
	//										//                        if(!TextUtils.isEmpty(result.getTransaction_id()))
	//										//                        {
	//										//                            cycleWFTPay(out_trade_no,transaction_id);
	//										//                        }
	//										//                        else{
	//										//                            showToast("获取威富通支付信息失败,"+result.getMessage());
	//										//                            Log.v("获取威富通支付信息失败", ToolsUtils.getPrinterSth(result));
	//										//                        }
	//									}
	//								}
	//							}, 2000);
	//						}
	//
	//						@Override
	//						public void onError(PosServiceException e) {
	//							progressDialog.disLoading();
	//							//                    aty.showToast(ToolsUtils.returnXMLStr("get_wft_pay_info_failure")+"," + e.getMessage());
	//							EventBus.getDefault()
	//									.post(new PosEvent(Constant.EventState.ERR_GET_WFT_PAY_STATE));
	//							Log.i("获取威富通支付信息失败", e.getMessage());
	//						}
	//					});
	//		} catch (PosServiceException e) {
	//			e.printStackTrace();
	//			progressDialog.disLoading();
	//			//            aty.showToast(ToolsUtils.returnXMLStr("get_wft_pay_info_failure"));
	//			EventBus.getDefault().post(new PosEvent(Constant.EventState.ERR_GET_WFT_PAY_STATE));
	//			Log.i("获取威富通支付信息失败", e.getMessage());
	//		}
	//	}

	//	private int queryWeifuTongCount = 0;

	//	private void cycleWFTPay(final String out_trade_no, final String chargeid) {
	//		wfthandler = new Handler() {
	//			@Override
	//			public void handleMessage(Message msg) {
	//				// TODO Auto-generated method stub
	//				super.handleMessage(msg);
	//				// 要做的事情
	//				if (queryWeifuTongCount >= 30) {
	//					wftTimerCancel();
	//					exceptMethod(FAIL_WFT_PAY);
	//				} else {
	//					queryWeifuTongCount += 1;
	//					queryWeiFuTong(out_trade_no, chargeid);
	//				}
	//			}
	//		};
	//		wftTask = new TimerTask() {
	//			@Override
	//			public void run() {
	//				// TODO Auto-generated method stub
	//				Message message = new Message();
	//				message.what = 1;
	//				wfthandler.sendMessage(message);
	//			}
	//		};
	//		timerwft.schedule(wftTask, delayedTime, cycleTime);//延时3秒并且3秒循环一次获取威富通交易情况
	//	}

	//	private void queryWeiFuTong(final String out_trade_no, final String transaction_id) {
	//		try {
	//			if (!Store.getInstance(context).isFront()) {
	//				progressDialog.showLoading("");
	//			}
	//			StoreBusinessService storeBusinessService = StoreBusinessService.getInstance();
	//			storeBusinessService
	//					.queryWeiFuTong(out_trade_no, transaction_id, new ResultCallback<WeiFuTongResponse>() {
	//						@Override
	//						public void onResult(WeiFuTongResponse result) {
	//							if (result != null) {
	//								String query   = result.getNeed_query();
	//								String errcode = result.getErrcode();
	//								if (result.isSuccess()) {
	//									wftTimerCancel();
	//									progressDialog.disLoading();
	//									aty.showToast(ToolsUtils.returnXMLStr("pay_success"));
	//									Log.i("创建威富通交易成功", out_trade_no);
	//									cancelFailDialog();
	//									aty.changeMoney(printMoney, printMoney, pt, out_trade_no, out_trade_no, null, null);//威富通
	//								} else if (!result.isSuccess() && "false".equals(query)) {
	//									wftTimerCancel();
	//									EventBus.getDefault()
	//											.post(new PosEvent(Constant.EventState.REFRESH_ORDERID));
	//									progressDialog.disLoading();
	//									exceptMethod(FAIL_WFT_PAY);
	//									aty.showToast(ToolsUtils.returnXMLStr("wft_pay_failure"));
	//									Log.i("威富通支付失败===>>", out_trade_no);
	//								}
	//							}
	//						}
	//
	//						@Override
	//						public void onError(PosServiceException e) {
	//							setClickPay(true);
	//							progressDialog.disLoading();
	//							Log.i("创建威富通交易失败", e.getMessage());
	//							getWayWeiFuTong(autoCode, ToolsUtils.handleCarDish(cart
	//									.getDishItemList()), totalFee, orderTradeNo);//交易失败后重新获取支付信息
	//						}
	//					});
	//		} catch (PosServiceException e) {
	//			e.printStackTrace();
	//		}
	//	}
}
