package cn.acewill.mobile.pos.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.acewill.paylibrary.EPayTask;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArrayList;

import cn.acewill.mobile.pos.R;
import cn.acewill.mobile.pos.common.ReprintController;
import cn.acewill.mobile.pos.config.MyApplication;
import cn.acewill.mobile.pos.config.Store;
import cn.acewill.mobile.pos.exception.PosServiceException;
import cn.acewill.mobile.pos.interfices.CreatDealBack;
import cn.acewill.mobile.pos.interfices.DialogCall;
import cn.acewill.mobile.pos.interfices.DialogCallBack;
import cn.acewill.mobile.pos.interfices.DialogEtCallback;
import cn.acewill.mobile.pos.interfices.DialogMTCallback;
import cn.acewill.mobile.pos.interfices.DialogTCallback;
import cn.acewill.mobile.pos.interfices.DishCheckCallback;
import cn.acewill.mobile.pos.interfices.InterfaceDialog;
import cn.acewill.mobile.pos.interfices.KeyCallBack;
import cn.acewill.mobile.pos.interfices.VoucherRefrushLisener;
import cn.acewill.mobile.pos.interfices.WSHListener;
import cn.acewill.mobile.pos.model.Customer;
import cn.acewill.mobile.pos.model.NetOrderRea;
import cn.acewill.mobile.pos.model.dish.Cart;
import cn.acewill.mobile.pos.model.dish.Dish;
import cn.acewill.mobile.pos.model.dish.DishCount;
import cn.acewill.mobile.pos.model.event.PosEvent;
import cn.acewill.mobile.pos.model.order.CardRecord;
import cn.acewill.mobile.pos.model.order.Order;
import cn.acewill.mobile.pos.model.order.OrderItem;
import cn.acewill.mobile.pos.model.payment.Payment;
import cn.acewill.mobile.pos.model.user.User;
import cn.acewill.mobile.pos.model.user.UserData;
import cn.acewill.mobile.pos.model.wsh.Account;
import cn.acewill.mobile.pos.model.wsh.WshAccountCoupon;
import cn.acewill.mobile.pos.model.wsh.WshCreateDeal;
import cn.acewill.mobile.pos.model.wsh.WshDealPreview;
import cn.acewill.mobile.pos.printer.Printer;
import cn.acewill.mobile.pos.service.DialogCallback;
import cn.acewill.mobile.pos.service.PosInfo;
import cn.acewill.mobile.pos.service.ResultCallback;
import cn.acewill.mobile.pos.service.StoreBusinessService;
import cn.acewill.mobile.pos.service.WshService;
import cn.acewill.mobile.pos.service.retrofit.response.ValidationResponse;
import cn.acewill.mobile.pos.ui.adapter.CouponAdapter;
import cn.acewill.mobile.pos.ui.adapter.DialogListAdp;
import cn.acewill.mobile.pos.ui.adapter.MemberAdapter;
import cn.acewill.mobile.pos.ui.adapter.ReprintAdp;
import cn.acewill.mobile.pos.ui.adapter.RushItemAdp;
import cn.acewill.mobile.pos.widget.ComTextView;
import cn.acewill.mobile.pos.widget.CommonDialog;
import cn.acewill.mobile.pos.widget.CommonEditText;
import cn.acewill.mobile.pos.widget.ProgressDialogF;
import cn.acewill.mobile.pos.widget.ScrolListView;

/**
 * Created by DHH on 2017/12/5.
 */

public class DialogUtil {


	//创建一个Dialog
	public static Dialog getDialogShow(final Context context, int layout, float width, float height, boolean touchOutside, boolean isShow) {
		LayoutInflater inflater = LayoutInflater.from(context);
		View           view     = inflater.inflate(layout, null);
		final Dialog   dialog   = new CommonDialog(context);
		//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(view);
		dialog.getWindow().setBackgroundDrawableResource(R.color.transparent);
		dialog.setCanceledOnTouchOutside(touchOutside);

		Window                     dialogWindow = dialog.getWindow();
		WindowManager              m            = ((Activity) context).getWindowManager();
		Display                    d            = m.getDefaultDisplay(); // 获取屏幕宽、高用
		WindowManager.LayoutParams p            = dialogWindow.getAttributes(); // 获取对话框当前的参数值
		p.width = (int) (d.getWidth() * width); // 高度设置为屏幕的0.5
		//        p.height = (int) (d.getHeight() * height); // 宽度设置为屏幕的0.5
		dialogWindow.setAttributes(p);

		if (isShow) {
			dialog.show();
		}
		return dialog;
	}

	public static Dialog getDialog(final Context context, View view, int width, int height) {

		final Dialog dialog = new Dialog(context);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(view);
		dialog.getWindow().setBackgroundDrawableResource(R.color.transparent);
		dialog.setCanceledOnTouchOutside(false);

		Window                     dialogWindow = dialog.getWindow();
		WindowManager              m            = ((Activity) context).getWindowManager();
		Display                    d            = m.getDefaultDisplay(); // 获取屏幕宽、高用
		WindowManager.LayoutParams p            = dialogWindow.getAttributes(); // 获取对话框当前的参数值
		if (width == LinearLayout.LayoutParams.MATCH_PARENT || width == LinearLayout.LayoutParams.WRAP_CONTENT) {
			p.width = width;
		} else {
			p.width = (int) (d.getWidth() * width * 0.1);
		}
		if (height == LinearLayout.LayoutParams.MATCH_PARENT || height == LinearLayout.LayoutParams.WRAP_CONTENT) {
			p.height = height;
		} else {
			p.height = (int) (d.getHeight() * height * 0.1);
		}

		dialogWindow.setAttributes(p);

		return dialog;
	}

	/**
	 * 创建一个不显示的Dialog,
	 *
	 * @param context
	 * @param layout  布局视图
	 * @param width   占屏幕百分比宽
	 * @param height  占屏幕百分比高
	 * @return
	 */
	public static Dialog getDialog(final Context context, int layout, int width, int height) {
		LayoutInflater inflater = LayoutInflater.from(context);
		View           view     = inflater.inflate(layout, null);
		final Dialog   dialog   = getDialog(context, view, width, height);

		return dialog;
	}

	/**
	 * 创建一个Dialog
	 *
	 * @param context
	 * @param layout  布局视图
	 * @param width   占屏幕百分比宽
	 * @param height  占屏幕百分比高
	 * @return
	 */
	public static Dialog createDialog(final Context context, int layout, int width, int height) {
		Dialog dialog = getDialog(context, layout, width, height);

		dialog.show();

		return dialog;
	}

	public static AlertDialog LoginErrorDialog(final Context context, final String title, final String content, final DialogCallback dialogCallback) {
		final AlertDialog alertDialog = new AlertDialog.Builder(context).create();
		alertDialog.show();
		Window window = alertDialog.getWindow();
		window.setContentView(R.layout.dialog_ordinary);
		window.setBackgroundDrawableResource(R.color.transparent);
		alertDialog.setCanceledOnTouchOutside(false);

		WindowManager              m = ((Activity) context).getWindowManager();
		Display                    d = m.getDefaultDisplay(); // 获取屏幕宽、高用
		WindowManager.LayoutParams p = window.getAttributes(); // 获取对话框当前的参数值
		p.width = (int) (d.getWidth() * 0.8); // 高度设置为屏幕的0.5
		//        p.height = (int) (d.getHeight() * 0.3); // 宽度设置为屏幕的0.5
		window.setAttributes(p);

		TextView     print_title    = (TextView) window.findViewById(R.id.print_title);
		LinearLayout print_close_ll = (LinearLayout) window.findViewById(R.id.print_close_ll);
		TextView     tv_content     = (TextView) window.findViewById(R.id.tv_content);
		TextView     print_cancle   = (TextView) window.findViewById(R.id.print_cancle);
		TextView     print_ok       = (TextView) window.findViewById(R.id.print_ok);
		if (!TextUtils.isEmpty(title)) {
			print_title.setText(title);
		}
		if (!TextUtils.isEmpty(content)) {
			tv_content.setText(content);
		}

		print_close_ll.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ToolsUtils.writeUserOperationRecords("关闭" + title + "窗口");
				alertDialog.dismiss();
				dialogCallback.onCancle();
			}
		});
		print_cancle.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ToolsUtils.writeUserOperationRecords("确定" + title + "按钮");
				alertDialog.dismiss();
				dialogCallback.onCancle();
			}
		});
		print_ok.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ToolsUtils.writeUserOperationRecords("取消" + title + "按钮");
				alertDialog.dismiss();
				dialogCallback.onConfirm();
			}
		});
		return alertDialog;
	}

	/**
	 * 清空购物车警告
	 *
	 * @return
	 */
	public static void clearCartWarn(final Context context, final Activity activity) {
		final Dialog dialog = createDialog(context, R.layout.dialog_clear_warn, 7, LinearLayout.LayoutParams.WRAP_CONTENT);

		TextView cancle = (TextView) dialog.findViewById(R.id.cancle);
		TextView ok     = (TextView) dialog.findViewById(R.id.ok);

		cancle.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				UserAction.log("取消", "清空购物车警告", context);
				dialog.dismiss();
			}
		});

		ok.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				UserAction.log("确定", "清空购物车警告", context);
				dialog.dismiss();
				Cart.cleanDishList();
				if (activity != null) {
					activity.finish();
				}
			}
		});
	}

	public static Dialog initTableInfo(final Context context, final DialogTCallback dialogCallback) {
		final Dialog dialog = DialogUtil
				.createDialog(context, R.layout.dialog_table_info, 9, LinearLayout.LayoutParams.WRAP_CONTENT);
		final CommonEditText ed_tableNumber = (CommonEditText) dialog
				.findViewById(R.id.ed_tableNumber);
		final CommonEditText ed_peopleNums = (CommonEditText) dialog
				.findViewById(R.id.ed_peopleNums);
		TextView print_ok     = (TextView) dialog.findViewById(R.id.print_ok);
		TextView print_cancle = (TextView) dialog.findViewById(R.id.print_cancle);
		LinearLayout print_close_ll = (LinearLayout) dialog
				.findViewById(R.id.print_close_ll);
		LinearLayout lin_money = (LinearLayout) dialog.findViewById(R.id.lin_money);
		lin_money.setVisibility(View.GONE);
		print_close_ll.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ToolsUtils.writeUserOperationRecords("关闭餐牌信息窗口");
				dialog.dismiss();
				dialogCallback.onCancle();
			}
		});
		print_cancle.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ToolsUtils.writeUserOperationRecords("取消餐牌信息按钮");
				dialog.dismiss();
				dialogCallback.onCancle();
			}
		});
		print_ok.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ToolsUtils.writeUserOperationRecords("确定餐牌信息按钮");
				String tableNumber = ed_tableNumber.getText().toString().trim();
				String nums        = ed_peopleNums.getText().toString().trim();
				if (TextUtils.isEmpty(tableNumber)) {
					MyApplication.getInstance()
							.ShowToast(ToolsUtils.returnXMLStr("please_input_tableNumber"));
				} else {
					Order order = new Order();
					order.setTableNames(tableNumber);
					dialogCallback.onConfirm(order);
					dialog.dismiss();
				}
			}
		});
		return dialog;
	}

	/**
	 * 外卖dialog
	 *
	 * @param context
	 * @param dialogTCallback
	 * @return
	 */
	public static Dialog takeOutDialog(final Context context, final DialogTCallback dialogTCallback) {
		final String title  = ToolsUtils.returnXMLStr("sth_take_out_title");
		final Dialog dialog = DialogUtil.createDialog(context, R.layout.dialog_take_out, 9, 7);
		dialog.setCanceledOnTouchOutside(false);
		TextView tv_back = (TextView) dialog.findViewById(R.id.tv_back);
		TextView tv_ok   = (TextView) dialog.findViewById(R.id.tv_ok);

		final EditText ed_customrerName = (EditText) dialog.findViewById(R.id.ed_customrerName);
		final EditText ed_phone         = (EditText) dialog.findViewById(R.id.ed_phone);
		final EditText ed_address       = (EditText) dialog.findViewById(R.id.ed_address);
		final EditText ed_outerOrderId  = (EditText) dialog.findViewById(R.id.ed_outerOrderId);

		tv_back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ToolsUtils.writeUserOperationRecords("取消" + title + "按钮");
				dialog.cancel();
				dialogTCallback.onCancle();
			}
		});
		tv_ok.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ToolsUtils.writeUserOperationRecords("确定" + title + "按钮");
				String   customrerName = ed_customrerName.getText().toString().trim();
				String   phone         = ed_phone.getText().toString().trim();
				String   address       = ed_address.getText().toString().trim();
				String   outerOrderId  = ed_outerOrderId.getText().toString().trim();
				Customer customer      = new Customer();
				customer.setCustomerAddress(address);
				customer.setCustomerName(customrerName);
				customer.setCustomerPhoneNumber(phone);
				customer.setCustomerOuterOrderId(outerOrderId);
				PosInfo posInfo = PosInfo.getInstance();
				posInfo.setCustomer(customer);
				dialog.cancel();
				dialogTCallback.onConfirm(customer);
			}
		});
		return dialog;
	}


	/**
	 * 自定义金额键盘
	 *
	 * @param context
	 * @param money
	 * @return
	 */
	public static void keyNumDialog(final Context context, final BigDecimal money, final KeyCallBack callBack) {

		final int[] othValues = FormatUtils.getMoney(money.intValue());

		final Dialog dialog = createDialog(context, R.layout.key_number, 9, 7);

		TextView           key_close       = (TextView) dialog.findViewById(R.id.key_close);
		final TextView     key_money       = (TextView) dialog.findViewById(R.id.key_money);
		final LinearLayout no_pay_ll       = (LinearLayout) dialog.findViewById(R.id.no_pay_ll);
		final TextView     no_pay_tv       = (TextView) dialog.findViewById(R.id.no_pay_tv);
		final TextView     no_pay_money    = (TextView) dialog.findViewById(R.id.no_pay_money);
		TextView           key_title       = (TextView) dialog.findViewById(R.id.key_title);
		TextView           key_clear       = (TextView) dialog.findViewById(R.id.key_clear);
		TextView           key_one         = (TextView) dialog.findViewById(R.id.key_one);
		TextView           key_two         = (TextView) dialog.findViewById(R.id.key_two);
		TextView           key_three       = (TextView) dialog.findViewById(R.id.key_three);
		TextView           key_other_one   = (TextView) dialog.findViewById(R.id.key_other_one);
		TextView           key_four        = (TextView) dialog.findViewById(R.id.key_four);
		TextView           key_five        = (TextView) dialog.findViewById(R.id.key_five);
		TextView           key_six         = (TextView) dialog.findViewById(R.id.key_six);
		TextView           key_other_two   = (TextView) dialog.findViewById(R.id.key_other_two);
		TextView           key_seven       = (TextView) dialog.findViewById(R.id.key_seven);
		TextView           key_eight       = (TextView) dialog.findViewById(R.id.key_eight);
		TextView           key_nine        = (TextView) dialog.findViewById(R.id.key_nine);
		TextView           key_other_three = (TextView) dialog.findViewById(R.id.key_other_three);
		TextView           key_delete      = (TextView) dialog.findViewById(R.id.key_delete);
		TextView           key_zero        = (TextView) dialog.findViewById(R.id.key_zero);
		TextView           key_point       = (TextView) dialog.findViewById(R.id.key_point);
		TextView           key_ok          = (TextView) dialog.findViewById(R.id.key_ok);

		key_other_one.setText(othValues[0] + "￥");
		key_other_two.setText(othValues[1] + "￥");
		key_other_three.setText(othValues[2] + "￥");

		no_pay_ll.setVisibility(View.VISIBLE);

		if (money.compareTo(BigDecimal.ZERO) == 1) {
			no_pay_tv.setText("未支付");
			no_pay_money.setText(money.setScale(2, BigDecimal.ROUND_DOWN) + "￥");
			no_pay_money.setTextColor(context.getResources().getColor(R.color.actionsheet_red));
		} else {
			no_pay_tv.setText("找零");
			no_pay_money.setText(money.setScale(2, BigDecimal.ROUND_DOWN).negate() + "￥");
			no_pay_money.setTextColor(context.getResources().getColor(R.color.green));
		}

		class OnClickLisener implements View.OnClickListener {
			@Override
			public void onClick(View v) {
				switch (v.getId()) {
					case R.id.key_close:
						dialog.dismiss();
						UserAction.log("取消", "自定义金额键盘", context);
						break;
					case R.id.key_clear:
						printValue(Keys.KEYCL);
						UserAction.log("刚好", "自定义金额键盘", context);
						break;
					case R.id.key_one:
						printValue(Keys.KEY1);
						UserAction.log("1", "自定义金额键盘", context);
						break;
					case R.id.key_two:
						printValue(Keys.KEY2);
						UserAction.log("2", "自定义金额键盘", context);
						break;
					case R.id.key_three:
						printValue(Keys.KEY3);
						UserAction.log("3", "自定义金额键盘", context);
						break;
					case R.id.key_other_one:
						printValue(Keys.KEYMONEY_ONE);
						UserAction.log("￥" + othValues[0], "自定义金额键盘", context);
						break;
					case R.id.key_four:
						printValue(Keys.KEY4);
						UserAction.log("4", "自定义金额键盘", context);
						break;
					case R.id.key_five:
						printValue(Keys.KEY5);
						UserAction.log("5", "自定义金额键盘", context);
						break;
					case R.id.key_six:
						printValue(Keys.KEY6);
						UserAction.log("6", "自定义金额键盘", context);
						break;
					case R.id.key_other_two:
						printValue(Keys.KEYMONEY_TWO);
						UserAction.log("￥" + othValues[1], "自定义金额键盘", context);
						break;
					case R.id.key_seven:
						printValue(Keys.KEY7);
						UserAction.log("7", "自定义金额键盘", context);
						break;
					case R.id.key_eight:
						printValue(Keys.KEY8);
						UserAction.log("8", "自定义金额键盘", context);
						break;
					case R.id.key_nine:
						printValue(Keys.KEY9);
						UserAction.log("9", "自定义金额键盘", context);
						break;
					case R.id.key_other_three:
						printValue(Keys.KEYMONEY_THREE);
						UserAction.log("￥" + othValues[2], "自定义金额键盘", context);
						break;
					case R.id.key_delete:
						printValue(Keys.KEYDE);
						UserAction.log("DE", "自定义金额键盘", context);
						break;
					case R.id.key_zero:
						printValue(Keys.KEY0);
						UserAction.log("0", "自定义金额键盘", context);
						break;
					case R.id.key_point:
						printValue(Keys.KEYPOINT);
						UserAction.log("POINT", "自定义金额键盘", context);
						break;
					case R.id.key_ok:
						printValue(Keys.KEYOK);
						UserAction.log("OK", "自定义金额键盘", context);
						break;

				}
			}

			private String     print_str   = "";//输入的字符
			private BigDecimal print_money = new BigDecimal(0);//输入金额

			private void printValue(Keys keys) {

				switch (keys.getValue()) {
					case 0:
					case 1:
					case 2:
					case 3:
					case 4:
					case 5:
					case 6:
					case 7:
					case 8:
					case 9:
						if (print_str.length() < 7) {
							print_str += keys.getValue() + "";
						}
						break;
					case 10://点
						if (!print_str.contains(".")) {
							if (print_str.length() < 7) {
								print_str += ".";
							}
						}
						break;
					case 11://删除
						if (print_str.length() > 0) {
							print_str = print_str.substring(0, print_str.length() - 1);
						}
						break;
					case 12://刚好
						print_str = money + "";
						break;
					case 14://确认
						dialog.dismiss();
						callBack.onOk(print_money);
						break;
					case 15://自动生成金额1
						print_str = othValues[0] + "";
						break;
					case 16://自动生成金额2
						print_str = othValues[1] + "";
						break;
					case 17://自动生成金额3
						print_str = othValues[2] + "";
						break;
				}

				if (keys.getValue() != 14) {
					if (print_str.equals(".") || TextUtils.isEmpty(print_str)) {
						print_money = new BigDecimal(0);
					} else if (print_str.length() > 0 && ".".equals(print_str.charAt(0))) {
						print_money = new BigDecimal("0" + print_str);
					} else if (print_str.length() > 0 && "."
							.equals(print_str.charAt(print_str.length() - 1))) {
						print_money = new BigDecimal(print_str
								.substring(0, print_str.length() - 1));
					} else {
						print_money = new BigDecimal(print_str);
					}
					key_money.setText((TextUtils.isEmpty(print_str) ? "0" : print_str) + "￥");

					BigDecimal nopay_money = money.subtract(print_money);
					if (nopay_money.compareTo(BigDecimal.ZERO) == 1) {
						no_pay_tv.setText("未支付");
						no_pay_money.setText(nopay_money.setScale(2, BigDecimal.ROUND_DOWN) + "￥");
						no_pay_money.setTextColor(context.getResources()
								.getColor(R.color.actionsheet_red));
					} else {
						no_pay_tv.setText("找零");
						no_pay_money.setText(nopay_money.setScale(2, BigDecimal.ROUND_DOWN)
								.negate() + "￥");
						no_pay_money.setTextColor(context.getResources().getColor(R.color.green));
					}
				}

				if (keys.getValue() == 12) {
					dialog.dismiss();
					callBack.onOk(print_money);
				}

			}
		}

		OnClickLisener onClickLisener = new OnClickLisener();

		key_close.setOnClickListener(onClickLisener);
		key_clear.setOnClickListener(onClickLisener);
		key_one.setOnClickListener(onClickLisener);
		key_two.setOnClickListener(onClickLisener);
		key_three.setOnClickListener(onClickLisener);
		key_other_one.setOnClickListener(onClickLisener);
		key_four.setOnClickListener(onClickLisener);
		key_five.setOnClickListener(onClickLisener);
		key_six.setOnClickListener(onClickLisener);
		key_other_two.setOnClickListener(onClickLisener);
		key_seven.setOnClickListener(onClickLisener);
		key_eight.setOnClickListener(onClickLisener);
		key_nine.setOnClickListener(onClickLisener);
		key_other_three.setOnClickListener(onClickLisener);
		key_delete.setOnClickListener(onClickLisener);
		key_zero.setOnClickListener(onClickLisener);
		key_point.setOnClickListener(onClickLisener);
		key_ok.setOnClickListener(onClickLisener);
	}


	public static Dialog switchWftPay(Context context, final KeyCallBack keyCallBack) {
		final Dialog       dialog     = createDialog(context, R.layout.dialog_sw_wft_pay, 9, 5);
		final LinearLayout wx_ll      = (LinearLayout) dialog.findViewById(R.id.wx_ll);
		final LinearLayout ali_ll     = (LinearLayout) dialog.findViewById(R.id.ali_ll);
		ComTextView        wx_tv      = (ComTextView) dialog.findViewById(R.id.wx_tv);
		ComTextView        ali_tv     = (ComTextView) dialog.findViewById(R.id.ali_tv);
		ComTextView        scan_close = (ComTextView) dialog.findViewById(R.id.scan_close);
		//WX
		wx_ll.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ToolsUtils.writeUserOperationRecords("WFT选择支付方式微信按钮");
				wx_ll.setSelected(true);
				ali_ll.setSelected(false);
				keyCallBack.onOk(0);
				dialog.cancel();
			}
		});

		//ALI
		ali_ll.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ToolsUtils.writeUserOperationRecords("WFT选择支付方式支付宝按钮");
				wx_ll.setSelected(false);
				ali_ll.setSelected(true);
				keyCallBack.onOk(1);
				dialog.cancel();
			}
		});
		scan_close.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ToolsUtils.writeUserOperationRecords("WFT选择支付方式取消按钮");
				dialog.cancel();
			}
		});
		return dialog;
	}

	public static OrderItem getCartDishById(long dishId, List<OrderItem> orderItems) {
		for (OrderItem cartDish : orderItems) {
			if (cartDish.dishId == dishId) {
				return cartDish;
			}
		}
		return null;
	}


	/**
	 * 会员消费
	 *
	 * @param context
	 * @param typeid         支付类型，3:会员卡(储值),4:优惠券,5:积分
	 * @param money          未支付金额，用于优惠券判断是否满多少可以使用
	 * @param orderItems     菜品项
	 * @param isCheckBalance 是否要对订单进行全部支付
	 * @param creatDealBack
	 * @return
	 */
	public static Dialog memberDialog(final Context context, final int typeid, BigDecimal money, final List<OrderItem> orderItems, final boolean isCheckBalance, final CreatDealBack creatDealBack) {
		final Account account = PosInfo.getInstance().getAccountMember();
		final BigDecimal memberMoney = ToolsUtils
				.cloneTo(money.setScale(2, BigDecimal.ROUND_DOWN));
		final PosInfo posInfo = PosInfo.getInstance();
		final Dialog  dialog  = createDialog(context, R.layout.wsh_layout, 9, LinearLayout.LayoutParams.WRAP_CONTENT);
		//		final TextView wsh_title = (TextView) dialog.findViewById(R.id.wsh_title);
		final TextView wsh_close = (TextView) dialog.findViewById(R.id.wsh_close);
		final CommonEditText cardno = (CommonEditText) dialog
				.findViewById(R.id.cardno);
		final TextView checkmember = (TextView) dialog
				.findViewById(R.id.checkmember);
		final TextView name       = (TextView) dialog.findViewById(R.id.name);
		final TextView cardnum_tv = (TextView) dialog.findViewById(R.id.cardnum_tv);
		final TextView cancel_saveMember = (TextView) dialog
				.findViewById(R.id.cancel_saveMember);
		TextView       cancle   = (TextView) dialog.findViewById(R.id.cancle);
		TextView       use      = (TextView) dialog.findViewById(R.id.use);
		final TextView wsh_warn = (TextView) dialog.findViewById(R.id.wsh_warn);
		final LinearLayout wsh_info_ll = (LinearLayout) dialog
				.findViewById(R.id.wsh_info_ll);
		final ListView couponList = (ListView) dialog.findViewById(R.id.couponList);
		final LinearLayout coupon_ll = (LinearLayout) dialog
				.findViewById(R.id.coupon_ll);
		final RelativeLayout balance_ll = (RelativeLayout) dialog
				.findViewById(R.id.balance_ll);
		final TextView tv_balance_hine = (TextView) dialog
				.findViewById(R.id.tv_balance_hine);
		final TextView tv_credit_hine = (TextView) dialog
				.findViewById(R.id.tv_credit_hine);
		final TextView tv_sure = (TextView) dialog.findViewById(R.id.tv_sure);
		final TextView member_info = (TextView) dialog
				.findViewById(R.id.member_info);
		final TextView tv_coupon_hine = (TextView) dialog
				.findViewById(R.id.tv_coupon_hine);
		final TextView order_money = (TextView) dialog
				.findViewById(R.id.order_money);
		final ScrolListView lv_memberCard = (ScrolListView) dialog
				.findViewById(R.id.lv_memberCard);
		final RelativeLayout credit_ll = (RelativeLayout) dialog
				.findViewById(R.id.credit_ll);
		final RelativeLayout rel_selectMemberCard = (RelativeLayout) dialog
				.findViewById(R.id.rel_selectMemberCard);

		final CheckBox ckSelectBalance = (CheckBox) dialog.findViewById(R.id.ck_select_balance);
		final CheckBox ckSelectCredit  = (CheckBox) dialog.findViewById(R.id.ck_select_credit);
		final TextView tv_pay_balance  = (TextView) dialog.findViewById(R.id.tv_pay_balance);
		final TextView tv_pay_credit   = (TextView) dialog.findViewById(R.id.tv_pay_credit);

		if (!isCheckBalance) {
			cancel_saveMember.setVisibility(View.GONE);
		} else {
			cancel_saveMember.setVisibility(View.VISIBLE);
		}
		wsh_warn.setVisibility(View.GONE);
		wsh_info_ll.setVisibility(View.VISIBLE);
		//		memberInfo[0] = account;
		//		setMemberInfoShow(account);
		if (account.getBalance() != null && account.getBalance() > 0) {
			balance_ll.setVisibility(View.VISIBLE);
		} else {
			balance_ll.setVisibility(View.GONE);
		}
		if (account.isUsecredit()) {
			credit_ll.setVisibility(View.VISIBLE);
		} else {
			credit_ll.setVisibility(View.GONE);
		}
		if (account.getCoupons() != null && account.getCoupons().size() > 0) {
			tv_coupon_hine.setVisibility(View.VISIBLE);
			couponList.setVisibility(View.VISIBLE);
		} else {
			tv_coupon_hine.setVisibility(View.GONE);
			couponList.setVisibility(View.GONE);
		}
		if (account.getCredit() != null && account.getCredit() > 0) {
			credit_ll.setVisibility(View.VISIBLE);
		} else {
			credit_ll.setVisibility(View.GONE);
		}
		name.setText(account.getName());
		cardnum_tv.setText(account.getUno());
		//用户的储值金额
		BigDecimal userStored = new BigDecimal(account
				.getBalance() / 100.0)
				.setScale(2, BigDecimal.ROUND_DOWN);
		tv_balance_hine.setText(ToolsUtils
				.returnXMLStr("amount_of_the_remaining_stored") + userStored + "(￥)");
		order_money.setText(ToolsUtils
				.returnXMLStr("wating_pay_money") + memberMoney
				.toString());
		tv_credit_hine.setText(ToolsUtils
				.returnXMLStr("intergral_of_the_remaining_stored") + account
				.getCredit());
		member_info.setText(ToolsUtils
				.returnXMLStr("member_card_number_xx2") + account
				.getUno() + "  " + ToolsUtils
				.returnXMLStr("phoneNumber") + "  " + account
				.getPhone() + "  " + ToolsUtils
				.returnXMLStr("sth_name2") + "  " + account
				.getName() + ToolsUtils
				.logicUserCardGender(account) + "  " + ToolsUtils
				.returnXMLStr("grade") + "  " + account.getGradeName());

		ckSelectBalance.setChecked(false);
		tv_pay_balance.setVisibility(View.GONE);
		ckSelectCredit.setChecked(false);
		tv_pay_credit.setVisibility(View.GONE);

		//		if (account != null) {
		//			posInfo.setAccountMember(account);//获取会员信息
		//			EventBus.getDefault()
		//					.post(new PosEvent(Constant.EventState.MEMBER_INFO_CHANGE));
		//		}


		//		switch (typeid) {
		//			case 3://会员卡
		//				wsh_title.setText(ToolsUtils.returnXMLStr("stored_value"));
		//				break;
		//			case 4://优惠券
		//				wsh_title.setText(ToolsUtils.returnXMLStr("chit"));
		//				break;
		//			case 5://积分
		//				wsh_title.setText(ToolsUtils.returnXMLStr("integrals"));
		//				break;
		//		}

		final boolean[] isCreditOpen = {false};//积分开关开启状态
		final boolean[] isStoredOpen = {false};//储值开关开启状态

		//		final Account[] memberInfo = new Account[1];
		//优惠券
		final List<WshAccountCoupon>[] coupons = new List[1];

		class MemberPay {
			public void logicPay(List dataList, boolean isCreditOpen, boolean isStoredOpen) {
				if (account != null) {
					//优惠券
					coupons[0] = ToolsUtils.cloneTo(dataList);
					int moneyCredit = 0;//优惠券金额
					if (coupons[0] != null && coupons[0].size() > 0) {
						for (WshAccountCoupon coupon : coupons[0]) {
							float deductibleAmount = coupon.getDeno() * coupon.getSelectCount();
							moneyCredit += deductibleAmount;
						}
					}
					//用户的积分
					BigDecimal userCredit = new BigDecimal("0.00");
					if (account.getCredit() != null && account
							.isUsecredit() && isCreditOpen) {
						userCredit = new BigDecimal(account.getCredit());
					}
					//用户的储值金额
					BigDecimal userStored = new BigDecimal("0.00");
					if (isStoredOpen) {
						userStored = new BigDecimal(account.getBalance() / 100.0)
								.setScale(2, BigDecimal.ROUND_DOWN);
					}
					//用户使用券的金额
					BigDecimal userCoupon = new BigDecimal(moneyCredit);
					//用户的储值金额 + 使用了优惠券金额 + 积分
					BigDecimal countMoney = userStored.add(userCoupon).add(userCredit);
					//用户支付了多少
					BigDecimal userPay = new BigDecimal("0.00");
					//用户还需要支付多少
					BigDecimal userNeedPay = new BigDecimal("0.00");
					//是否支付完成
					boolean userIsPayEnd = false;
					userNeedPay = ToolsUtils.cloneTo(memberMoney);
					//如果使用了券
					if (userCoupon.compareTo(BigDecimal.ZERO) > 0) {
						userPay = userPay.add(userCoupon);
						userNeedPay = userNeedPay.subtract(userPay);
						if (userNeedPay.compareTo(BigDecimal.ZERO) != 1) {
							userNeedPay = new BigDecimal("0.00");
							userIsPayEnd = true;
						}

						order_money
								.setText(ToolsUtils.returnXMLStr("wating_pay_money") + userNeedPay);
						ckSelectBalance.setChecked(false);
						tv_pay_balance.setVisibility(View.GONE);
						ckSelectCredit.setChecked(false);
						tv_pay_credit.setVisibility(View.GONE);

					}
					if (!userIsPayEnd) {
						//如果积分大于0
						if (userCredit.compareTo(BigDecimal.ZERO) > 0) {
							BigDecimal decimal  = new BigDecimal("0");//小数部分 最后要给加上
							int        userNeed = userNeedPay.intValue();
							decimal = userNeedPay.subtract(new BigDecimal(userNeed));
							userNeedPay = new BigDecimal(userNeed + "");
							//积分等于需要支付的金额
							if (userNeedPay.compareTo(userCredit) == 0) {
								userNeedPay = new BigDecimal("0.00");
								order_money.setText(ToolsUtils
										.returnXMLStr("wating_pay_money") + decimal);
								ckSelectBalance.setChecked(false);
								tv_pay_balance.setVisibility(View.GONE);
								ckSelectCredit.setChecked(true);
								tv_pay_credit.setVisibility(View.VISIBLE);
								tv_pay_credit.setText(ToolsUtils
										.returnXMLStr("pay_intergral") + userCredit);
								//                                userNeedPay = userNeedPay.subtract(userCredit);
							}
							//需要支付的金额小于用户的积分
							else if (userNeedPay.compareTo(userCredit) == -1) {
								//                                userNeedPay = userCredit.subtract(userNeedPay);
								order_money.setText(ToolsUtils
										.returnXMLStr("wating_pay_money") + decimal);
								ckSelectBalance.setChecked(false);
								tv_pay_balance.setVisibility(View.GONE);
								ckSelectCredit.setChecked(true);
								tv_pay_credit.setVisibility(View.VISIBLE);
								tv_pay_credit.setText(ToolsUtils
										.returnXMLStr("pay_intergral") + userNeedPay);
								userNeedPay = new BigDecimal("0.00");
							}
							//需要支付的金额大于用户的积分
							else if (userNeedPay.compareTo(userCredit) == 1) {
								userNeedPay = userNeedPay.subtract(userCredit);
								order_money.setText(ToolsUtils
										.returnXMLStr("wating_pay_money") + decimal
										.add(userNeedPay));
								ckSelectBalance.setChecked(false);
								tv_pay_balance.setVisibility(View.GONE);
								ckSelectCredit.setChecked(true);
								tv_pay_credit.setVisibility(View.VISIBLE);
								tv_pay_credit.setText(ToolsUtils
										.returnXMLStr("pay_intergral") + userCredit);
							}
							userNeedPay = userNeedPay.add(decimal);
							if (userNeedPay.compareTo(BigDecimal.ZERO) == 0) {
								userIsPayEnd = true;
							}
						}
					}
					if (!userIsPayEnd) {
						//如果储值大于0
						if (userStored.compareTo(BigDecimal.ZERO) > 0) {
							BigDecimal storedShow = new BigDecimal("0.00");
							storedShow = ToolsUtils.cloneTo(userNeedPay);
							userNeedPay = userNeedPay.subtract(userStored);
							if (userNeedPay.compareTo(BigDecimal.ZERO) != 1) {
								userNeedPay = new BigDecimal("0.00");
								userIsPayEnd = true;
							}
							order_money.setText(ToolsUtils
									.returnXMLStr("wating_pay_money") + userNeedPay);
							ckSelectBalance.setChecked(true);
							tv_pay_balance.setVisibility(View.VISIBLE);
							tv_pay_balance
									.setText(ToolsUtils.returnXMLStr("use_banlance") + storedShow);

							//                            balance.setText(storedShow+"");
							if (!userIsPayEnd) {
								//                                balance.setText(userStored+"");
								tv_pay_balance
										.setText(ToolsUtils
												.returnXMLStr("use_banlance") + userStored);
							}
						}
					}
					if (!isCreditOpen) {
						tv_pay_credit.setText("");
						tv_pay_credit.setVisibility(View.GONE);
					}
					if (!isStoredOpen) {
						tv_pay_balance.setText("");
						tv_pay_balance.setVisibility(View.GONE);
					}
					if (!userIsPayEnd) {
						order_money
								.setText(ToolsUtils.returnXMLStr("wating_pay_money") + userNeedPay);
						if (isCheckBalance) {
							if (userNeedPay.compareTo(BigDecimal.ZERO) > 0) {
								MyApplication.getInstance().ShowToast(ToolsUtils
										.returnXMLStr("member_pay_money_error"));
							}
						}
					}
				}
			}
		}

		final MemberPay memberPay = new MemberPay();

		final CouponAdapter adapter = new CouponAdapter(context, memberMoney, new WSHListener() {
			@Override
			public void refrush(List dataList) {
				memberPay.logicPay(dataList, isCreditOpen[0], isStoredOpen[0]);
			}
		});
		couponList.setAdapter(adapter);
		adapter.chandDataSize(account.getCoupons().size());
		adapter.setData(getUsefulCoupon(orderItems, account
				.getCoupons()));
		//		updateMemberInfo();
		//储值开关是否开启
		ckSelectBalance.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					isStoredOpen[0] = true;
				} else {
					isStoredOpen[0] = false;
				}
				memberPay.logicPay(coupons[0], isCreditOpen[0], isStoredOpen[0]);
			}
		});

		//积分开关是否开启
		ckSelectCredit.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					isCreditOpen[0] = true;
				} else {
					isCreditOpen[0] = false;
				}
				memberPay.logicPay(coupons[0], isCreditOpen[0], isStoredOpen[0]);
			}
		});


		class MemberInfor {

			ProgressDialogF progressDialog;
			WshService      wshService;

			public MemberInfor() {
				try {
					progressDialog = new ProgressDialogF(context);
					wshService = WshService.getInstance();
					//					mScanGunKeyEventHelper = new ScanGunKeyEventHelper(this);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			//创建交易
			public void creatDeal() {

				boolean isCanCreat1  = false;
				boolean isCanCreat2  = false;
				boolean isCanCreat3  = false;
				boolean isCanCreat4  = false;
				int     consumAmount = 0;//消费金额/分

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
				request.setCno(account.getUno()); //卡号
				request.setUid(account.getUid());

				//				//处理菜品参数
				//				if (orderItems != null && orderItems.size() > 0) {
				//					List<WshCreateDeal.Pruduct> products = new ArrayList<>();
				//					for (OrderItem orderItem : orderItems) {
				//						WshCreateDeal.Pruduct pruduct = new WshCreateDeal.Pruduct();
				//						pruduct.name = orderItem.getDishName();
				//						pruduct.no = orderItem.getDishId() + "";
				//						pruduct.num = orderItem.getQuantity();
				//						pruduct.price = orderItem.getMemberPrice().multiply(new BigDecimal(100))
				//								.intValue();//传的是分?????
				//						pruduct.is_activity = 2;//不参加活动
				//						products.add(pruduct);
				//					}
				//					request.setProducts(products);
				//				}
				final List<Payment> memberPayMent = new CopyOnWriteArrayList<>();
				int                 storedId      = 3;//储值
				int                 vouchersId    = 4;//券
				int                 integralId    = 5;//积分
				//储值
				//                if (memberInfo[0].getBalance() > 0) {
				String balanceStr = tv_pay_balance.getText().toString();
				balanceStr = balanceStr.replace(ToolsUtils.returnXMLStr("use_banlance"), "");
				try {
					if (!TextUtils.isEmpty(balanceStr)) {
						isCanCreat1 = true;
						isCanCreat4 = true;
						float balance = Float.parseFloat(balanceStr);
						balance = balance * 100;
						BigDecimal bd = new BigDecimal((double) balance);
						bd = bd.setScale(0, BigDecimal.ROUND_HALF_UP);
						BigDecimal bdd = bd
								.divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_UP);
						Payment payment = new Payment(storedId, ToolsUtils
								.returnXMLStr("member_stored_value"), bdd.floatValue());
						memberPayMent.add(payment);
						consumAmount += bd.floatValue();
						request.setSub_balance((int) bd.floatValue());
					}
				} catch (NumberFormatException e) {
					e.printStackTrace();
					MyApplication.getInstance().ShowToast(ToolsUtils
							.returnXMLStr("please_input_correct_stored_value"));
					return;
				}
				//                }

				//优惠券
				List<WshAccountCoupon> coupons = adapter.getSelectCoupon();


				if (coupons != null && coupons.size() > 0) {
					isCanCreat2 = true;
					isCanCreat4 = true;
					float        money     = 0;//优惠券金额
					List<String> dinos     = new ArrayList<String>();
					List<String> giftdinos = new ArrayList<String>();
					for (WshAccountCoupon coupon : coupons) {


						//						int deductibleAmount = coupon.getDeno() * coupon.getSelectCount();
						//						money += deductibleAmount;
						//						for (int i = 0; i < Math
						//								.min(coupon.getSelectCount(), coupon.getCoupon_ids().size()); i++) {
						//							dinos.add(coupon.getCoupon_ids().get(i));
						//						}

						if (coupon.getType() == 1) {
							consumAmount += coupon.getDeno() * 100;
							dinos.add(coupon.getCoupon_ids().get(0));
							request.setDeno_coupon_ids(dinos);
							money = coupon.getDeno();
						} else if (coupon.getType() == 2) {
							OrderItem orderItem = getCartDishById(coupon.getProducts()
									.get(0), orderItems);
							int balance = orderItem.getCost().multiply(new BigDecimal("100"))
									.intValue();
							consumAmount += balance;
							giftdinos.add(coupon.getCoupon_ids().get(0));
							money = orderItem.getCost().floatValue();


							List<WshCreateDeal.Pruduct> products = new ArrayList<>();
							WshCreateDeal.Pruduct       pruduct  = new WshCreateDeal.Pruduct();
							pruduct.name = orderItem.getDishName();
							pruduct.no = orderItem.getDishId() + "";
							pruduct.num = orderItem.getQuantity();
							pruduct.price = orderItem.getCost().multiply(new BigDecimal(100))
									.intValue();//传的是分?????
							pruduct.is_activity = 2;//不参加活动
							products.add(pruduct);
							request.setProducts(products);
						}

					}
					Payment payment = new Payment(vouchersId, ToolsUtils
							.returnXMLStr("coupon"), money);
					memberPayMent.add(payment);
					//					consumAmount += money * 100;
					request.setDeno_coupon_ids(dinos);
					request.setGift_coupons_ids(giftdinos);
				}

				//积分
				//if (memberInfo[0].getCredit() > 0) {
				String creditStr = tv_pay_credit.getText().toString();
				creditStr = creditStr.replace(ToolsUtils.returnXMLStr("pay_intergral"), "");
				try {
					if (!TextUtils.isEmpty(creditStr)) {
						isCanCreat3 = true;
						isCanCreat4 = true;
						consumAmount += Integer.parseInt(creditStr) * 100;
						Payment payment = new Payment(integralId, ToolsUtils
								.returnXMLStr("member_intergrals"), Integer.parseInt(creditStr));
						memberPayMent.add(payment);
						request.setSub_credit(Integer.parseInt(creditStr));
					}
				} catch (NumberFormatException e) {
					e.printStackTrace();
					MyApplication.getInstance().ShowToast(ToolsUtils
							.returnXMLStr("please_input_correct_stored_value"));
					return;
				}
				//                }

				if (isCanCreat4) {
					request.setConsume_amount(memberMoney.multiply(new BigDecimal("100"))
							.intValue());
					BigDecimal paymentAmount = memberMoney.multiply(new BigDecimal("100"))
							.subtract(new BigDecimal(consumAmount));
					if (paymentAmount.compareTo(BigDecimal.ZERO) != 1) {
						paymentAmount = new BigDecimal("0");
					}
					request.setPayment_amount(paymentAmount.intValue());
				} else if (isCanCreat1 == false && isCanCreat2 == false && isCanCreat3 == false) {
					request.setPayment_amount(memberMoney.multiply(new BigDecimal("100"))
							.intValue());
					request.setConsume_amount(memberMoney.multiply(new BigDecimal("100"))
							.intValue());
				}

				if (isCanCreat1 == false && isCanCreat2 == false && isCanCreat3 == false || isCanCreat4) {
					progressDialog.showLoading("");
					try {
						final boolean finalIsCanCreat1  = isCanCreat1;
						final boolean finalIsCanCreat2  = isCanCreat2;
						final boolean finalIsCanCreat3  = isCanCreat3;
						final boolean finalIsCanCreat4  = isCanCreat4;
						final int     finalConsumAmount = consumAmount;
						Log.e("DialogUtil", "手持Pos微生活预览的参数>" + new Gson().toJson(request));
						wshService.createDeal(request, new ResultCallback<WshDealPreview>() {
							@Override
							public void onResult(WshDealPreview result) {
								progressDialog.disLoading();
								BigDecimal print_money = new BigDecimal(request
										.getConsume_amount() / 100.0)
										.setScale(2, BigDecimal.ROUND_HALF_UP);
								if (isCheckBalance && finalIsCanCreat4)//需要结算订单剩下的全部余额
								{
									if (print_money.compareTo(memberMoney) == -1) {
										MyApplication.getInstance().ShowToast(ToolsUtils
												.returnXMLStr("please_enter_a_valid_amount"));
										return;
									}
								}
								dialog.dismiss();
								if (finalIsCanCreat1 == false && finalIsCanCreat2 == false && finalIsCanCreat3 == false) {
									if (!isCheckBalance) {
										creatDealBack
												.onDeal(bis_id, result, new BigDecimal(finalConsumAmount / 100.0)
														.setScale(2, BigDecimal.ROUND_HALF_UP), false, account, memberPayMent);
									} else {
										creatDealBack.onDeal(bis_id, result, print_money
												.setScale(2, BigDecimal.ROUND_HALF_UP), false, account, memberPayMent);
									}
									//                                    creatDealBack.onDeal(bis_id, result, new BigDecimal(finalConsumAmount / 100.0).setScale(2, BigDecimal.ROUND_HALF_UP), false, accountMemberInfo[0], memberPayMent);

								} else {
									creatDealBack
											.onDeal(bis_id, result, new BigDecimal(finalConsumAmount / 100.0)
													.setScale(2, BigDecimal.ROUND_HALF_UP), true, account, memberPayMent);
								}
							}

							@Override
							public void onError(PosServiceException e) {
								progressDialog.disLoading();
								MyApplication.getInstance().ShowToast(e.getMessage());
							}
						});
					} catch (Exception e) {
						e.printStackTrace();
						progressDialog.disLoading();
					}
				} else {
					MyApplication.getInstance()
							.ShowToast(ToolsUtils.returnXMLStr("please_select_valid_member_pay"));
				}
			}

		}

		final MemberInfor member = new MemberInfor();


		wsh_close.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ToolsUtils.writeUserOperationRecords("关闭会员消费窗口");
				dialog.dismiss();
			}
		});

		cancle.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ToolsUtils.writeUserOperationRecords("取消会员消费按钮");
				dialog.dismiss();
			}
		});

		//创建微生活交易
		use.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ToolsUtils.writeUserOperationRecords("创建微生活交易按钮");
				if (Cart.getDishItemList() != null && Cart.getDishItemList().size() > 0) {
					if (!isCheckBalance) {
						member.creatDeal();
					} else {
						final CheckOutUtil checUtil = new CheckOutUtil(context, new Payment(998, "11"));
						checUtil.getDishStock(Cart.getDishItemList(), new DishCheckCallback() {
							@Override
							public void haveStock() {
								member.creatDeal();
							}

							@Override
							public void noStock(List dataList) {
								refreshDish(dataList, Cart.getDishItemList());
							}
						});
					}
				} else {
					MyApplication.getInstance()
							.ShowToast(ToolsUtils.returnXMLStr("please_click_dish"));
				}

			}
		});

		cancel_saveMember.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ToolsUtils.writeUserOperationRecords("取消但保留会员信息按钮");
				if (account != null) {
					posInfo.setAccountMember(account);
					dialog.dismiss();
				} else {
					MyApplication.getInstance()
							.ShowToast(ToolsUtils.returnXMLStr("please_input_correct_member_info"));
				}
			}
		});
		return dialog;
	}


	/**
	 * 菜品券，在购物车中有这个菜就显示，没有就不显示
	 *
	 * @param orderItems 购物车中的菜品
	 * @param coupons    券（代金券和菜品券）
	 * @return
	 */
	private static List getUsefulCoupon(List<OrderItem> orderItems, List<WshAccountCoupon> coupons) {
		ArrayList<WshAccountCoupon> usefulCoupons = new ArrayList<>();
		for (WshAccountCoupon coupon : coupons) {
			if (coupon.getType() == 2) {//菜品券
				for (OrderItem item : orderItems) {
					if (item.dishId == coupon.getProducts().get(0)) {
						coupon.setDeno(item.getCost().floatValue());
						usefulCoupons.add(coupon);
					}
				}
			} else {//代金券
				usefulCoupons.add(coupon);
			}
		}
		return usefulCoupons;
	}

	/**
	 * 会员结账之前的登录
	 *
	 * @param context
	 * @param creatDealBack
	 * @return
	 */
	public static Dialog memberVerDialog(final Context context, final DialogCallBack creatDealBack) {
		final Account[] accountMemberInfo = {null};
		final PosInfo   posInfo           = PosInfo.getInstance();
		final Dialog    dialog            = createDialog(context, R.layout.wsh_ver_layout, 9, LinearLayout.LayoutParams.WRAP_CONTENT);
		final TextView  wsh_title         = (TextView) dialog.findViewById(R.id.wsh_title);
		final TextView  wsh_close         = (TextView) dialog.findViewById(R.id.wsh_close);
		final CommonEditText cardno = (CommonEditText) dialog
				.findViewById(R.id.cardno);
		final TextView checkmember = (TextView) dialog
				.findViewById(R.id.checkmember);
		final TextView name       = (TextView) dialog.findViewById(R.id.name);
		final TextView cardnum_tv = (TextView) dialog.findViewById(R.id.cardnum_tv);
		final TextView cancle     = (TextView) dialog.findViewById(R.id.cancle);
		final TextView wsh_warn   = (TextView) dialog.findViewById(R.id.wsh_warn);
		final LinearLayout wsh_info_ll = (LinearLayout) dialog
				.findViewById(R.id.wsh_info_ll);
		final ListView couponList = (ListView) dialog.findViewById(R.id.couponList);
		final LinearLayout coupon_ll = (LinearLayout) dialog
				.findViewById(R.id.coupon_ll);
		final LinearLayout lin_bottom = (LinearLayout) dialog
				.findViewById(R.id.lin_bottom);
		final RelativeLayout balance_ll = (RelativeLayout) dialog
				.findViewById(R.id.balance_ll);
		final TextView tv_balance_hine = (TextView) dialog
				.findViewById(R.id.tv_balance_hine);
		final TextView tv_credit_hine = (TextView) dialog
				.findViewById(R.id.tv_credit_hine);
		final TextView tv_sure = (TextView) dialog.findViewById(R.id.tv_sure);
		final TextView member_info = (TextView) dialog
				.findViewById(R.id.member_info);
		final TextView tv_coupon_hine = (TextView) dialog
				.findViewById(R.id.tv_coupon_hine);
		final TextView order_money = (TextView) dialog
				.findViewById(R.id.order_money);
		final ScrolListView lv_memberCard = (ScrolListView) dialog
				.findViewById(R.id.lv_memberCard);
		final RelativeLayout credit_ll = (RelativeLayout) dialog
				.findViewById(R.id.credit_ll);
		final RelativeLayout rel_selectMemberCard = (RelativeLayout) dialog
				.findViewById(R.id.rel_selectMemberCard);


		wsh_title.setText(ToolsUtils.returnXMLStr("member_verification"));

		//		final Account[] memberInfo = new Account[1];

		class MemberInfor implements ScanGunKeyEventHelper.OnScanSuccessListener {

			ProgressDialogF       progressDialog;
			WshService            wshService;
			ScanGunKeyEventHelper mScanGunKeyEventHelper;

			public MemberInfor() {
				try {
					progressDialog = new ProgressDialogF(context);
					wshService = WshService.getInstance();
					mScanGunKeyEventHelper = new ScanGunKeyEventHelper(this);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			public void keyEvent(KeyEvent event) {
				mScanGunKeyEventHelper.analysisKeyEvent(event);
			}

			//获取会员信息
			public void getMemberInfo(final String num) {
				try {
					progressDialog.showLoading("");
					wsh_info_ll.setVisibility(View.GONE);
					wshService.getMemberInfo(num, new ResultCallback<List<Account>>() {
						@Override
						public void onResult(final List<Account> result) {
							progressDialog.disLoading();
							if (result != null && result.size() > 0) {
								lin_bottom.setVisibility(View.GONE);
								if (result.size() == 1) {
									Account account = result.get(0);
									accountMemberInfo[0] = ToolsUtils.cloneTo(account);
									wsh_warn.setVisibility(View.GONE);
									wsh_info_ll.setVisibility(View.VISIBLE);
									//									memberInfo[0] = account;

									if (accountMemberInfo[0] != null) {
										posInfo.setAccountMember(accountMemberInfo[0]);
										creatDealBack.onOk();
										dialog.dismiss();
										EventBus.getDefault()
												.post(new PosEvent(Constant.EventState.MEMBER_INFO_CHANGE));
									}
									Cart.getInstance().changeToMemberPrice();
								} else {
									rel_selectMemberCard.setVisibility(View.VISIBLE);
									wsh_warn.setVisibility(View.GONE);
									final MemberAdapter memberAdapter = new MemberAdapter(context);
									memberAdapter.setData(result);
									lv_memberCard.setAdapter(memberAdapter);
									final int[] current = {-1};
									lv_memberCard
											.setOnItemClickListener(new AdapterView.OnItemClickListener() {
												@Override
												public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
													current[0] = position;
													memberAdapter.setCurrent_select(position);
												}
											});

									tv_sure.setOnClickListener(new View.OnClickListener() {
										@Override
										public void onClick(View v) {
											if (current[0] != -1) {
												Cart.getInstance().changeToMemberPrice();
												Account account = result.get(current[0]);
												accountMemberInfo[0] = ToolsUtils.cloneTo(account);
												wsh_warn.setVisibility(View.GONE);
												rel_selectMemberCard.setVisibility(View.GONE);
												wsh_info_ll.setVisibility(View.VISIBLE);

												//												memberInfo[0] = account;

												if (accountMemberInfo[0] != null) {
													posInfo.setAccountMember(accountMemberInfo[0]);
													creatDealBack.onOk();
													dialog.dismiss();
													EventBus.getDefault()
															.post(new PosEvent(Constant.EventState.MEMBER_INFO_CHANGE));
												}
											} else {
												MyApplication.getInstance().ShowToast(ToolsUtils
														.returnXMLStr("please_select_member_card"));
											}
										}
									});
								}
							} else {
								lin_bottom.setVisibility(View.VISIBLE);
								wsh_warn.setVisibility(View.VISIBLE);
								rel_selectMemberCard.setVisibility(View.GONE);
								wsh_warn.setText(ToolsUtils
										.returnXMLStr("this_number_is_not_member"));
							}
						}

						@Override
						public void onError(PosServiceException e) {
							progressDialog.disLoading();
							wsh_info_ll.setVisibility(View.GONE);
							rel_selectMemberCard.setVisibility(View.GONE);
							wsh_warn.setVisibility(View.VISIBLE);
							wsh_warn.setText(e.getMessage());
							//                            cardno.setText("");
							MyApplication.getInstance().ShowToast(e.getMessage());
						}
					});
				} catch (Exception e) {
					e.printStackTrace();
					progressDialog.disLoading();
					wsh_info_ll.setVisibility(View.GONE);
					wsh_warn.setVisibility(View.VISIBLE);
					wsh_warn.setText(e.getMessage());
				}
			}

			@Override
			public void onScanSuccess(String barcode) {
				Log.e("扫码枪扫会员卡返回:", barcode);
				if (!TextUtils.isEmpty(barcode)) {
					getMemberInfo(barcode);//扫码验证会员
				}
			}
		}

		final MemberInfor member = new MemberInfor();
		//		if (posInfo.getAccountMember() != null) {
		//			Account account  = posInfo.getAccountMember();
		//			String  showText = "";
		//			if (!TextUtils.isEmpty(account.getPhone())) {
		//				showText = account.getPhone();
		//			} else {
		//				showText = account.getUno();
		//			}
		//			cardno.setText(showText);
		//			member.getMemberInfo(account.getUno());
		//		}

		//截获扫码枪按键事件.发给ScanGunKeyEventHelper
		dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				if (keyCode != KeyEvent.KEYCODE_BACK && keyCode != KeyEvent.KEYCODE_HOME && keyCode != KeyEvent.KEYCODE_MENU) {
					if (KeyEvent.KEYCODE_ENTER == keyCode && KeyEvent.ACTION_DOWN == event
							.getAction()) {
						member.keyEvent(event);
					}
				}
				return false;
			}
		});

		cardno.setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				//MyApplication.getInstance().ShowToast(keyCode+"============"+event.getAction());
				if (KeyEvent.KEYCODE_ENTER == keyCode && KeyEvent.ACTION_DOWN == event
						.getAction()) {
					if (!TextUtils.isEmpty(cardno.getText().toString().trim())) {
						final String num = cardno.getText().toString().trim();
						if (TextUtils.isEmpty(num)) {
							MyApplication.getInstance()
									.ShowToast(ToolsUtils.returnXMLStr("sth_member_number_hine"));
						} else {
							WindowUtil.hiddenKey();
							member.getMemberInfo(num);//刷卡验证会员
						}
					}
					return true;
				}
				return false;
			}
		});


		wsh_close.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ToolsUtils.writeUserOperationRecords("关闭会员消费窗口");
				dialog.dismiss();
			}
		});

		cancle.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				posInfo.setAccountMember(null);
				ToolsUtils.writeUserOperationRecords("非会员点餐");
				creatDealBack.onCancle();
				dialog.dismiss();
			}
		});


		//验证会员
		checkmember.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ToolsUtils.writeUserOperationRecords("验证会员按钮");
				final String num = cardno.getText().toString().trim();
				if (TextUtils.isEmpty(num)) {
					MyApplication.getInstance()
							.ShowToast(ToolsUtils.returnXMLStr("please_input_member_number"));
					return;
				}
				member.getMemberInfo(num);//输入账号验证会员
			}
		});

		return dialog;
	}

	public static void refreshDish(List<DishCount> result, List<Dish> dishs) {
		//刷新菜品数据,显示沽清
		String names = Cart.getInstance().getItemNameByDids((ArrayList) result, dishs);
		MyApplication.getInstance().ShowToast(ToolsUtils
				.returnXMLStr("the_following_items_are_not_enough") + "\n\n" + names + "。\n\n" + ToolsUtils
				.returnXMLStr("please_re_order"));
		Log.i("以下菜品份数不足:", names + "====<<");
	}

	public static Dialog meiTuanDialog(final Context context, final Long orderId, final BigDecimal money, final boolean isCheckOut, CopyOnWriteArrayList<ValidationResponse> addValidationLists, final DialogMTCallback dialogMTCallback) {
		final double totalMoney = Double
				.valueOf(money.toString());
		final int[]                                    maxVouchersNums   = {99};//该张订单最大使用券的张数
		final CopyOnWriteArrayList<String>             addTicketList     = new CopyOnWriteArrayList<>();
		final CopyOnWriteArrayList<ValidationResponse> addValidationList = new CopyOnWriteArrayList<>();
		if (addValidationLists != null && addValidationLists.size() > 0) {
			for (ValidationResponse vv : addValidationLists) {
				addValidationList.add(vv);
				addTicketList.add(vv.getCouponCode());
			}
		}
		final Dialog      dialog   = createDialog(context, R.layout.meituan_layout, 9, LinearLayout.LayoutParams.WRAP_CONTENT);
		final ComTextView mt_close = (ComTextView) dialog.findViewById(R.id.mt_close);
		final TextView tv_not_pay_price = (TextView) dialog
				.findViewById(R.id.tv_not_pay_price);
		final TextView tv_pay_price = (TextView) dialog.findViewById(R.id.tv_pay_price);
		final TextView wsh_warn     = (TextView) dialog.findViewById(R.id.wsh_warn);
		final ScrolListView ticket_list = (ScrolListView) dialog
				.findViewById(R.id.ticket_list);
		final TextView query  = (TextView) dialog.findViewById(R.id.query);
		final TextView submit = (TextView) dialog.findViewById(R.id.submit);
		tv_not_pay_price.setText(money.toString());

		//展示券列表的adapter
		class MTVouchersAdp extends BaseAdapter {
			private Context               context;
			private VoucherRefrushLisener callback;
			private String voucherStr = "";
			private EditText mCurrentEdtView;
			private StringBuffer                             sbb      = new StringBuffer();
			private CopyOnWriteArrayList<ValidationResponse> dataList = new CopyOnWriteArrayList<>();

			public ValidationResponse getValidation(int position) {
				return dataList.get(position);
			}

			/**
			 *  获得已经选择可使用券的总金额
			 * @return
			 */
			public double getVouchersMoney() {
				double money = 0;
				sbb.setLength(0);
				if (addValidationList != null && addValidationList.size() > 0) {
					addValidationList.clear();
				}
				if (dataList != null && dataList.size() > 0) {
					for (ValidationResponse validation : dataList) {
						if (validation.isSuccess() && validation.vouchersIsEff()) {
							money += validation.getDealValue();
							addValidationList.add(validation);//将已经成功添加了的券加入到列表中,后面下单需要此参数
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
				}
				voucherStr = sbb.toString();
				return money;
			}

			public String getVoucherStr() {
				return voucherStr;
			}

			public MTVouchersAdp(Context context, VoucherRefrushLisener callback) {
				this.context = context;
				this.callback = callback;
				if (dataList != null && dataList.size() > 0) {
					dataList.clear();
				}
			}

			public void addNullItem() {
				if (dataList == null || dataList.size() == 0) {
					ValidationResponse validation2 = new ValidationResponse();
					validation2.setOperateType(3);//添加一个初始项
					dataList.add(validation2);
					notifyDataSetChanged();
				}
			}

			@Override
			public int getCount() {
				return dataList.size() != 0 ? dataList.size() : 0;
			}

			@Override
			public Object getItem(int position) {
				return null;
			}

			@Override
			public long getItemId(int position) {
				return 0;
			}

			public void addValidation(ValidationResponse validation) {
				if (dataList == null) {
					dataList = new CopyOnWriteArrayList<>();
				}
				int dataSize           = dataList.size();
				int validationSuccSize = 0;//可以正常使用券的张数
				for (int i = 0; i < dataSize; i++) {
					ValidationResponse vv = dataList.get(i);
					if (vv != null) {
						if (vv.isSuccess() && vv.vouchersIsEff()) {
							validationSuccSize += 1;
						}
					}
					if (dataList.get(i) != null) {
						if (dataList.get(i).getOperateType() == 2 || dataList.get(i)
								.getOperateType() == 3) {
							removeValidation(i, true);//删除空项
						}
					}
				}
				//券有效并且在有效时间内
				if (validation.isSuccess() && validation.vouchersIsEff()) {
					if (validationSuccSize == 0) {
						maxVouchersNums[0] = validation.getCount();//将最大可使用的券张数保存起来,方便下次判断
						dataList.add(validation);
						addTicketList.add(validation.getCouponCode());
						callback.refrush(getVouchersMoney());
						notifyDataSetChanged();
					} else {
						if (validationSuccSize >= maxVouchersNums[0]) {
							MyApplication.getInstance()
									.ShowToast(ToolsUtils.returnXMLStr("voucher_is_max"));
							return;
						}
						if (maxVouchersNums[0] > validation.getCount()) {
							maxVouchersNums[0] = validation.getCount();
						}
						dataList.add(validation);
						addTicketList.add(validation.getCouponCode());
						callback.refrush(getVouchersMoney());
						notifyDataSetChanged();
					}
				} else {
					if (validation.getOperateType() == 2) //是一个可删除的项
					{
						dataList.add(validation);
						addTicketList.add(validation.getCouponCode());
						notifyDataSetChanged();
					} else {
						MyApplication.getInstance()
								.ShowToast(ToolsUtils.returnXMLStr("voucher_info_error"));
					}
				}
			}

			public void removeValidation(int position, boolean isRefresh) {
				if (dataList.get(position) != null) {
					ValidationResponse validation = dataList.get(position);
					if (validation.isSuccess()) {
						addTicketList.remove(validation.getCouponCode());
					}
					dataList.remove(position);
					callback.refrush(getVouchersMoney());
					notifyDataSetChanged();
				}
			}

			public EditText getCurrentEditView() {
				return mCurrentEdtView;
			}

			private View.OnFocusChangeListener mFocusChangedListener = new View.OnFocusChangeListener() {
				@Override
				public void onFocusChange(View view, boolean hasFocus) {
					if (hasFocus) {
						mCurrentEdtView = (EditText) view;
					}
				}
			};

			@Override
			public View getView(final int position, View convertView, ViewGroup parent) {
				ViewHolder         holder     = null;
				ValidationResponse validation = null;
				if (dataList.get(position) != null) {
					validation = dataList.get(position);
				}
				if (convertView == null) {
					holder = new ViewHolder();
					convertView = LayoutInflater.from(context)
							.inflate(R.layout.meituan_add_view, null);
					holder.voucherCodeEd = (CommonEditText) convertView
							.findViewById(R.id.voucherCode);
					holder.lin_voucher_info = (LinearLayout) convertView
							.findViewById(R.id.lin_voucher_info);
					holder.tv_ver_state = (TextView) convertView.findViewById(R.id.tv_ver_state);
					holder.tv_cost = (TextView) convertView.findViewById(R.id.tv_cost);
					holder.tv_min_voucher = (TextView) convertView
							.findViewById(R.id.tv_min_voucher);
					holder.tv_ver_eff_time = (TextView) convertView
							.findViewById(R.id.tv_ver_eff_time);
					holder.tv_max_voucher = (TextView) convertView
							.findViewById(R.id.tv_max_voucher);
					holder.rel_right = (RelativeLayout) convertView.findViewById(R.id.rel_right);
					holder.btn_add = (Button) convertView.findViewById(R.id.btn_add);
					holder.btn_minus = (Button) convertView.findViewById(R.id.btn_minus);

					holder.voucherCodeEd.setOnFocusChangeListener(mFocusChangedListener);
					convertView.setTag(holder);
				} else {
					holder = (ViewHolder) convertView.getTag();
				}
				if (validation != null) {
					if (validation.getOperateType() != 3) {
						holder.lin_voucher_info.setVisibility(View.VISIBLE);
						holder.rel_right.setVisibility(View.INVISIBLE);
						if (validation.getOperateType() == 1) {
							holder.btn_add.setVisibility(View.VISIBLE);
							holder.btn_minus.setVisibility(View.GONE);
						} else if (validation.getOperateType() == 2) {
							holder.btn_add.setVisibility(View.GONE);
							holder.btn_minus.setVisibility(View.VISIBLE);
						}
					} else {
						holder.lin_voucher_info.setVisibility(View.GONE);
						holder.rel_right.setVisibility(View.GONE);
					}
					if (validation.isSuccess()) {
						holder.voucherCodeEd.setText(validation.getCouponCode());//券码
						holder.tv_ver_state.setText(ToolsUtils.returnXMLStr("success"));
					} else {
						holder.tv_ver_state.setText(ToolsUtils.returnXMLStr("failure"));
					}
					holder.tv_cost.setText(validation.getDealValue() + " ￥");
					holder.tv_min_voucher.setText(validation.getMinConsume() + " " + ToolsUtils
							.returnXMLStr("leaf"));
					holder.tv_max_voucher
							.setText(validation.getCount() + " " + ToolsUtils.returnXMLStr("leaf"));
					holder.tv_ver_eff_time.setText(validation.getDealBeginTime());
					if (validation.getMinConsume() > 1 || validation.getCount() > 1) {
						holder.rel_right.setVisibility(View.VISIBLE);
						holder.btn_add.setVisibility(View.VISIBLE);
						holder.btn_minus.setVisibility(View.GONE);

						ValidationResponse validation2 = new ValidationResponse();
						validation2.setOperateType(2);//添加一个可删除的项
						//                        addValidation(validation2);
					} else {
						if (validation.getOperateType() != 3) {
							holder.rel_right.setVisibility(View.VISIBLE);
							holder.lin_voucher_info.setVisibility(View.VISIBLE);
							holder.voucherCodeEd.setText("");
							if (validation.getOperateType() == 0) {
								holder.btn_add.setVisibility(View.GONE);
								holder.btn_minus.setVisibility(View.GONE);
							} else if (validation.getOperateType() == 1) {
								holder.btn_add.setVisibility(View.VISIBLE);
								holder.btn_minus.setVisibility(View.GONE);
							} else if (validation.getOperateType() == 2) {
								holder.btn_add.setVisibility(View.GONE);
								holder.btn_minus.setVisibility(View.VISIBLE);
								holder.lin_voucher_info.setVisibility(View.GONE);
							}
						} else {
							holder.lin_voucher_info.setVisibility(View.GONE);
							holder.rel_right.setVisibility(View.GONE);
						}
					}
				} else {
					holder.lin_voucher_info.setVisibility(View.GONE);
				}

				holder.btn_add.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						ValidationResponse validation2 = new ValidationResponse();
						validation2.setOperateType(2);//添加一个可删除的项
						addValidation(validation2);
					}
				});
				holder.btn_minus.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						removeValidation(position, true);
					}
				});

				//                holder.voucherCodeEd.setOnTouchListener(new View.OnTouchListener() {
				//                    @Override
				//                    public boolean onTouch(View v, MotionEvent event) {
				//                        ((ViewGroup) v.getParent())
				//                                .setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
				//                        return false;
				//                    }
				//                });

				//                convertView.setOnTouchListener(new View.OnTouchListener() {
				//                    @Override
				//                    public boolean onTouch(View v, MotionEvent event) {
				//                        ((ViewGroup) v)
				//                                .setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
				//                        return false;
				//                    }
				//                });
				return convertView;
			}

			class ViewHolder {
				CommonEditText voucherCodeEd;
				TextView       tv_ver_state;
				TextView       tv_cost;
				TextView       tv_min_voucher;
				TextView       tv_ver_eff_time;
				TextView       tv_max_voucher;
				LinearLayout   lin_voucher_info;
				RelativeLayout rel_right;
				Button         btn_add;
				Button         btn_minus;
			}
		}

		final MTVouchersAdp mtVouchersAdp = new MTVouchersAdp(context, new VoucherRefrushLisener() {
			@Override
			public void refrush(double payMoney) {
				tv_pay_price.setText(String.format("%.2f ", (totalMoney - payMoney)) + "");
				if (isCheckOut) {
					if (payMoney >= totalMoney) {
						query.setVisibility(View.GONE);
						submit.setVisibility(View.VISIBLE);
					}
				} else {
					if (payMoney > 0) {
						query.setVisibility(View.VISIBLE);
						submit.setVisibility(View.VISIBLE);
					}

				}
			}
		});

		mtVouchersAdp.addNullItem();
		ticket_list.setAdapter(mtVouchersAdp);

		class VoucherCode implements ScanGunKeyEventHelper.OnScanSuccessListener {
			ProgressDialogF       progressDialog;
			StoreBusinessService  storeBusinessService;
			ScanGunKeyEventHelper mScanGunKeyEventHelper;

			public VoucherCode() {
				try {
					progressDialog = new ProgressDialogF(context);
					storeBusinessService = StoreBusinessService.getInstance();
					mScanGunKeyEventHelper = new ScanGunKeyEventHelper(this);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			public void keyEvent(KeyEvent event) {
				mScanGunKeyEventHelper.analysisKeyEvent(event);
			}

			//获取券码信息
			public void getVoucherInfo(final String num) {
				try {
					wsh_warn.setVisibility(View.GONE);
					progressDialog.showLoading("");
					storeBusinessService
							.validationSetout(num, new ResultCallback<ValidationResponse>() {
								@Override
								public void onResult(ValidationResponse result) {
									progressDialog.disLoading();
									if (result.isSuccess()) {
										wsh_warn.setVisibility(View.GONE);
										//如何第0项是初始项的话
										if (mtVouchersAdp.getValidation(0) != null && mtVouchersAdp
												.getValidation(0).getOperateType() == 3) {
											mtVouchersAdp.removeValidation(0, true);//删除空项
										}
										if (addTicketList != null && addTicketList.size() > 0) {
											if (addTicketList.contains(result.getCouponCode())) {
												MyApplication.getInstance().ShowToast(ToolsUtils
														.returnXMLStr("voucher_info_existing"));
												return;
											}
										}
										mtVouchersAdp.addValidation(result);
									} else {
										wsh_warn.setVisibility(View.VISIBLE);
										wsh_warn.setText(result.getMessage());
									}
								}

								@Override
								public void onError(PosServiceException e) {
									progressDialog.disLoading();
									MyApplication.getInstance().ShowToast(e.getMessage());
									wsh_warn.setVisibility(View.VISIBLE);
									wsh_warn.setText(e.getMessage());
									Log.e("查询券有误:====" + num, e.getMessage());
								}
							});
				} catch (Exception e) {
					e.printStackTrace();
					progressDialog.disLoading();
					wsh_warn.setVisibility(View.VISIBLE);
					wsh_warn.setText(e.getMessage());
					Log.e("查询券有误:====" + num, e.getMessage());
				}
			}

			public void executeCode() {
				wsh_warn.setVisibility(View.GONE);
				progressDialog.showLoading("");
				storeBusinessService.executeCode(mtVouchersAdp
						.getVoucherStr(), orderId, new ResultCallback<ValidationResponse>() {
					@Override
					public void onResult(ValidationResponse result) {
						progressDialog.disLoading();
						if (result.isSuccess()) {
							dialog.dismiss();
							wsh_warn.setVisibility(View.GONE);
							dialogMTCallback
									.onCheckout(new BigDecimal(mtVouchersAdp.getVouchersMoney())
											.setScale(2, BigDecimal.ROUND_HALF_UP), isCheckOut, addValidationList);
						} else {
							wsh_warn.setVisibility(View.VISIBLE);
							wsh_warn.setText(ToolsUtils.returnXMLStr("voucher_info_null"));
						}
					}

					@Override
					public void onError(PosServiceException e) {
						progressDialog.disLoading();
						Log.e("提交美团交易有误:====" + orderId, e.getMessage());
					}
				});
			}

			@Override
			public void onScanSuccess(String barcode) {
				Log.e("扫码枪扫会员卡返回:", barcode);
				if (!TextUtils.isEmpty(barcode)) {
					if (mtVouchersAdp.getVouchersMoney() >= totalMoney) {
						MyApplication.getInstance().ShowToast("券金额已支付完毕,请勿再次添加券");
						return;
					}
					getVoucherInfo(barcode);
				}
			}
		}

		final VoucherCode voucherCode = new VoucherCode();
		//截获扫码枪按键事件.发给ScanGunKeyEventHelper
		dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				if (keyCode != KeyEvent.KEYCODE_BACK && keyCode != KeyEvent.KEYCODE_HOME && keyCode != KeyEvent.KEYCODE_MENU) {
					//                    if (KeyEvent.KEYCODE_ENTER == keyCode && KeyEvent.ACTION_DOWN == event.getAction()) {
					voucherCode.keyEvent(event);
					//                    }
				}
				return false;
			}
		});

		//验券
		query.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ToolsUtils.writeUserOperationRecords("验证美团验券提交按钮");
				if (mtVouchersAdp.getCurrentEditView() != null) {
					final String num = mtVouchersAdp.getCurrentEditView().getText().toString()
							.trim();
					if (TextUtils.isEmpty(num)) {
						MyApplication.getInstance()
								.ShowToast(ToolsUtils.returnXMLStr("please_input_voucher_code"));
						return;
					}
					if (addTicketList != null && addTicketList.size() > 0) {
						if (addTicketList.contains(num)) {
							MyApplication.getInstance()
									.ShowToast(ToolsUtils.returnXMLStr("voucher_info_existing"));
							return;
						}
					}
					voucherCode.getVoucherInfo(num);
				} else {
					MyApplication.getInstance()
							.ShowToast(ToolsUtils.returnXMLStr("please_input_voucher_code"));
				}
			}
		});

		//提交付款申请
		submit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (isCheckOut) {
					//                    System.out.println("券码===》》" + mtVouchersAdp.getVoucherStr());
					Log.e("美团交易券列表:====" + orderId, mtVouchersAdp.getVoucherStr());
					voucherCode.executeCode();
				} else {
					if (addValidationList != null && addValidationList.size() > 0) {
						dialog.dismiss();
						dialogMTCallback.onCheckout(new BigDecimal(mtVouchersAdp.getVouchersMoney())
								.setScale(2, BigDecimal.ROUND_HALF_UP), isCheckOut, addValidationList);
					} else {
						MyApplication.getInstance().ShowToast(ToolsUtils
								.returnXMLStr("please_select_at_least_one_coupon"));
					}
				}
			}
		});

		mt_close.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ToolsUtils.writeUserOperationRecords("关闭美团验券窗口");
				dialog.dismiss();
			}
		});

		//        voucherCodeEd.setOnKeyListener(new View.OnKeyListener() {
		//            @Override
		//            public boolean onKey(View v, int keyCode, KeyEvent event) {
		//                //MyApplication.getInstance().ShowToast(keyCode+"============"+event.getAction());
		//                if (KeyEvent.KEYCODE_ENTER == keyCode && KeyEvent.ACTION_DOWN == event.getAction()) {
		//                    if (!TextUtils.isEmpty(voucherCodeEd.getText().toString().trim())) {
		//                        final String num = voucherCodeEd.getText().toString().trim();
		//                        if (TextUtils.isEmpty(num)) {
		//                            MyApplication.getInstance().ShowToast(ToolsUtils.returnXMLStr("please_input_voucher"));
		//                        } else {
		//                            WindowUtil.hiddenKey();
		//                            voucherCode.getVoucherInfo(num);
		//                        }
		//                    }
		//                    return true;
		//                }
		//                return false;
		//            }
		//        });
		return dialog;
	}

	/**
	 * 刷卡支付下单
	 *
	 * @param context
	 * @param cost
	 * @param dialogCall
	 * @return
	 */
	public static Dialog showMemberDialog(final Context context, final BigDecimal cost, final DialogCall dialogCall) {
		final Dialog dialog = createDialog(context, R.layout.dialog_member_card_pay, 9, LinearLayout.LayoutParams.WRAP_CONTENT);
		dialog.setCanceledOnTouchOutside(true);
		final EditText et_member  = (EditText) dialog.findViewById(R.id.et_member);
		final TextView tv_price   = (TextView) dialog.findViewById(R.id.tv_price);
		final TextView tv_card_no = (TextView) dialog.findViewById(R.id.tv_card_no);
		final TextView tv_balance = (TextView) dialog.findViewById(R.id.tv_balance);
		tv_card_no.setTag(0);
		tv_price.setText(ToolsUtils.returnXMLStr("this_consumption") + "   " + cost);

		et_member.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
			                          int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
			                              int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				String  cardNo = s.toString();
				Integer tag    = (Integer) tv_card_no.getTag();// 是否读取过会员卡，反正重复读取
				if (tag == 1) {
					return;
				}
				if (cardNo.contains("\n") || cardNo.contains("\r")) {
					et_member.setText("");
					String                  mCardNo = cardNo.replace("\n", "").replace("\r", "");
					HashMap<String, String> cardMap = ToolsUtils.getCardInfo(context);
					String                  balance = cardMap.get(mCardNo);
					if (balance == null) {
						tv_card_no.setText(ToolsUtils.returnXMLStr("card_numb") + ToolsUtils
								.returnXMLStr("not_find_info"));
						tv_balance.setText(ToolsUtils.returnXMLStr("balance"));
						return;
					}
					BigDecimal subtract = new BigDecimal(balance).subtract(cost);
					tv_card_no.setText(ToolsUtils.returnXMLStr("card_numb") + mCardNo);
					if (subtract.compareTo(new BigDecimal(0)) < 0) {
						tv_balance.setText(ToolsUtils
								.returnXMLStr("balance") + balance + "￥,  " + ToolsUtils
								.returnXMLStr("insufficient_balance"));
						return;
					}
					tv_card_no.setTag(1);
					tv_balance.setText(ToolsUtils.returnXMLStr("balance") + balance + "￥");

					dialogCall.onOk("Success");
					dialog.dismiss();
				}
			}
		});
		return dialog;
	}

	public static Dialog cardRecordDialog(final Context context, final DialogTCallback dialogTCallback) {
		final String title  = ToolsUtils.returnXMLStr("sth_cardRecord_title");
		final Dialog dialog = DialogUtil.createDialog(context, R.layout.dialog_cardrecord, 9, 5);
		dialog.setCanceledOnTouchOutside(false);
		TextView tv_back = (TextView) dialog.findViewById(R.id.tv_back);
		TextView tv_ok   = (TextView) dialog.findViewById(R.id.tv_ok);

		final EditText ed_customrerName = (EditText) dialog.findViewById(R.id.ed_customrerName);
		final EditText ed_contact       = (EditText) dialog.findViewById(R.id.ed_contact);

		tv_back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ToolsUtils.writeUserOperationRecords("取消" + title + "按钮");
				dialog.cancel();
				dialogTCallback.onCancle();
			}
		});
		tv_ok.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ToolsUtils.writeUserOperationRecords("确定" + title + "按钮");
				String customrerName = ed_customrerName.getText().toString().trim();
				String contact       = ed_contact.getText().toString().trim();
				if (TextUtils.isEmpty(customrerName)) {
					MyApplication.getInstance().ShowToast("请输入联系人姓名");
					return;
				} else if (TextUtils.isEmpty(contact)) {
					MyApplication.getInstance().ShowToast("请填写联系人联系方式!");
					return;
				}
				CardRecord cardRecord = new CardRecord();
				cardRecord.setName(customrerName);
				cardRecord.setContact(contact);
				dialogTCallback.onConfirm(cardRecord);
				dialog.cancel();
			}
		});
		return dialog;
	}

	/**
	 * 扫码弹出框
	 *
	 * @param context
	 * @param type      1:支付宝，2:微信
	 * @param qrCodeUrl 二维码号
	 */
	public static Dialog scanPadDialog(final Context context, final int type, final EPayTask inTask, final String qrCodeUrl, final Bitmap qrCodeBit, final InterfaceDialog interfaceDialog) {
		final Dialog       dialog       = createDialog(context, R.layout.dialog_pay_pad_scancode, 9, LinearLayout.LayoutParams.WRAP_CONTENT);
		final Store        store        = Store.getInstance(context);
		final TextView     scan_code_tv = (TextView) dialog.findViewById(R.id.scan_code_tv);
		final LinearLayout retry_ll     = (LinearLayout) dialog.findViewById(R.id.retry_ll);
		ImageView          img          = (ImageView) dialog.findViewById(R.id.scan_code_iv);
		TextView           creat        = (TextView) dialog.findViewById(R.id.creat);
		TextView           retry        = (TextView) dialog.findViewById(R.id.retry);

		final int   second = 90;
		final int[] num    = new int[1];
		num[0] = second;
		final Timer mTimer = new Timer();

		String str = type == 1 ? ToolsUtils.returnXMLStr("sth_zfb") : ToolsUtils
				.returnXMLStr("sth_wx");
		if (type == 12) {
			str = ToolsUtils.returnXMLStr("lkl_pay");
		} else if (type == -8) {
			str = "";
		}
		final String title = ToolsUtils.returnXMLStr("guest") + str + ToolsUtils
				.returnXMLStr("scan_pos_qrcode");

		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message m) {

				if (num[0] >= 0) {
					scan_code_tv.setText(title + "(" + num[0] + "s)");
				} else {
					mTimer.cancel();
					retry_ll.setVisibility(View.VISIBLE);
				}
			}
		};

		TimerTask timerTask = new TimerTask() {
			@Override
			public void run() {
				Message message = new Message();
				num[0]--;
				handler.sendMessage(message);
			}
		};
		mTimer.schedule(timerTask, 1000, 1000);

		if (type == -8) {
			if (qrCodeBit == null) {
				//                img.setImageBitmap(CreateImage.convertStringToIcon(qrCodeUrl));
			} else {
				img.setImageBitmap(qrCodeBit);
			}
		} else {
			Bitmap bitmap = null;
			Bitmap qrcode = CreateImage.creatQRImage(qrCodeUrl, bitmap, 450, 450);
			img.setImageBitmap(qrcode);
		}

		scan_code_tv.setText(title);

		creat.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				ToolsUtils.writeUserOperationRecords("已支付==扫码弹出框");
				interfaceDialog.onCancle();
			}
		});
		retry.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ToolsUtils.writeUserOperationRecords("重试==扫码弹出框");
				retry_ll.setVisibility(View.GONE);
				if (inTask != null) {
					inTask.cancel(true);
				}
				interfaceDialog.onOk("");
				dialog.dismiss();
			}
		});
		dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
				if (mTimer != null) {
					mTimer.cancel();
				}
				if (inTask != null) {
					inTask.cancel(true);
				}
			}
		});
		return dialog;
	}

	public static Dialog ordinaryDialog(final Context context, final String title, final String content, final DialogCallback dialogCallback) {
		final Dialog dialog         = getDialogShow(context, R.layout.dialog_ordinary, 0.9f, 0.8f, false, true);
		TextView     print_title    = (TextView) dialog.findViewById(R.id.print_title);
		LinearLayout print_close_ll = (LinearLayout) dialog.findViewById(R.id.print_close_ll);
		TextView     tv_content     = (TextView) dialog.findViewById(R.id.tv_content);
		TextView     tv_tips        = (TextView) dialog.findViewById(R.id.tv_tips);
		TextView     print_cancle   = (TextView) dialog.findViewById(R.id.print_cancle);
		TextView     print_ok       = (TextView) dialog.findViewById(R.id.print_ok);
		if (!TextUtils.isEmpty(title)) {
			print_title.setText(title);
		}
		tv_content.setText(TextUtils.isEmpty(content) ? ToolsUtils
				.returnXMLStr("text_app_version_hine") : content);
		if (!TextUtils.isEmpty(title) && title.equals(ToolsUtils.returnXMLStr("sth_home_logout"))) {
			tv_tips.setVisibility(View.VISIBLE);
		} else {
			tv_tips.setVisibility(View.GONE);
		}

		print_close_ll.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ToolsUtils.writeUserOperationRecords("关闭" + title + "窗口");
				dialog.dismiss();
				dialogCallback.onCancle();
			}
		});
		print_cancle.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ToolsUtils.writeUserOperationRecords("取消" + title + "按钮");
				dialog.dismiss();
				dialogCallback.onCancle();
			}
		});
		print_ok.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ToolsUtils.writeUserOperationRecords("确定" + title + "按钮");
				dialog.dismiss();
				dialogCallback.onConfirm();
			}
		});
		return dialog;
	}

	/**
	 * 补打结账单选择dialog
	 *
	 * @param context
	 * @param title
	 * @param printerList
	 * @param dialogTCallback
	 * @return
	 */
	public static Dialog ReprintDialog(final Context context, final String title, List<Printer> printerList, final DialogTCallback dialogTCallback) {
		//        final Dialog dialog = DialogUtil.getDialog(context, R.layout.dialog_empty_table, 0.5f, 0.4f, 1);
		final Dialog dialog   = createDialog(context, R.layout.dialog_empty_table, 9, LinearLayout.LayoutParams.WRAP_CONTENT);
		TextView     tv_back  = (TextView) dialog.findViewById(R.id.tv_back);
		TextView     tv_add   = (TextView) dialog.findViewById(R.id.tv_add);
		TextView     tv_title = (TextView) dialog.findViewById(R.id.tv_title);
		GridView     handleGv = (GridView) dialog.findViewById(R.id.handle_gv);
		tv_title.setText(title);
		handleGv.setNumColumns(2);
		final ReprintAdp reprintAdp = new ReprintAdp(context);
		reprintAdp.setData(printerList);
		handleGv.setAdapter(reprintAdp);
		tv_back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ToolsUtils.writeUserOperationRecords("取消" + title + "按钮");
				dialog.cancel();
				dialogTCallback.onCancle();
			}
		});
		tv_add.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ToolsUtils.writeUserOperationRecords("确定" + title + "按钮");
				dialog.cancel();
				dialogTCallback.onConfirm(1);
			}
		});
		handleGv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Printer printer = (Printer) ReprintController.getRePrinterList().get(position);
				if (printer != null) {
					if (printer.isSelect) {
						printer.isSelect = false;
						ToolsUtils
								.writeUserOperationRecords("取消选择" + printer.getDeviceName() + "按钮");
					} else {
						printer.isSelect = true;
						ToolsUtils.writeUserOperationRecords("选择" + printer.getDeviceName() + "按钮");
					}
					reprintAdp.notifyDataSetChanged();
				}
			}
		});
		return dialog;
	}


	/**
	 * 催菜dialog
	 *
	 * @param context
	 * @param title
	 * @param orderItems
	 * @param dialogCallback
	 * @return
	 */
	public static Dialog rushDishDialog(final Context context, final String title, final List<OrderItem> orderItems, int sectionsStyle, final DialogCallback dialogCallback) {
		final Dialog    dialog   = createDialog(context, R.layout.dialog_rush_item, 9, 7);
		TextView        tv_back  = (TextView) dialog.findViewById(R.id.tv_back);
		TextView        tv_add   = (TextView) dialog.findViewById(R.id.tv_add);
		TextView        tv_title = (TextView) dialog.findViewById(R.id.tv_title);
		final TextView  tv_all   = (TextView) dialog.findViewById(R.id.tv_all);
		ListView        lv_order = (ListView) dialog.findViewById(R.id.lv_order);
		final boolean[] isCheck  = {true};
		tv_title.setText(title);
		final RushItemAdp rushItemAdp = new RushItemAdp(context, sectionsStyle);
		rushItemAdp.setData(orderItems);
		lv_order.setAdapter(rushItemAdp);
		tv_back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ToolsUtils.writeUserOperationRecords("取消" + title + "按钮");
				dialog.cancel();
				dialogCallback.onCancle();
			}
		});
		tv_add.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ToolsUtils.writeUserOperationRecords("确定" + title + "按钮");
				dialog.cancel();
				dialogCallback.onConfirm();
			}
		});
		tv_all.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (isCheck[0]) {
					ToolsUtils.writeUserOperationRecords("全部勾选按钮==>>" + title);
					tv_all.setText(ToolsUtils.returnXMLStr("all_order_cancle"));
					isCheck[0] = false;
					for (OrderItem orderItem : orderItems) {
						orderItem.isSelectItem = true;
					}
				} else {
					ToolsUtils.writeUserOperationRecords("全部取消按钮==>>" + title);
					tv_all.setText(ToolsUtils.returnXMLStr("all_order_select"));
					isCheck[0] = true;
					for (OrderItem orderItem : orderItems) {
						orderItem.isSelectItem = false;
					}
				}
				rushItemAdp.notifyDataSetChanged();
			}
		});

		return dialog;
	}


	/**
	 * 发票冲红
	 *
	 * @param context
	 * @param dialogTCallback
	 * @return
	 */
	public static Dialog revokeInvoiceDialog(final Context context, final DialogTCallback dialogTCallback) {
		final String title = ToolsUtils.returnXMLStr("ticket_revokeinvoice");
		final Dialog dialog = DialogUtil
				.createDialog(context, R.layout.dialog_revoke_invoice, 9, 5);
		dialog.setCanceledOnTouchOutside(false);
		TextView tv_back = (TextView) dialog.findViewById(R.id.tv_back);
		TextView tv_ok   = (TextView) dialog.findViewById(R.id.tv_ok);

		final EditText ed_outerOrderId = (EditText) dialog.findViewById(R.id.ed_outerOrderId);

		tv_back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ToolsUtils.writeUserOperationRecords("取消" + title + "按钮");
				dialog.cancel();
				dialogTCallback.onCancle();
			}
		});
		tv_ok.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ToolsUtils.writeUserOperationRecords("确定" + title + "按钮");
				String outerOrderId = ed_outerOrderId.getText().toString().trim();
				if (TextUtils.isEmpty(outerOrderId)) {
					MyApplication.getInstance().ShowToast(ToolsUtils
							.returnXMLStr("please_input_ticket_revokeinvoice_reason"));
					return;
				}
				Customer customer = new Customer();
				customer.setCustomerOuterOrderId(outerOrderId);
				dialog.cancel();
				dialogTCallback.onConfirm(customer);
			}
		});
		return dialog;
	}

	public static Dialog inputDialog(final Context context, final String title, final String content, final String contentHint, final int maxNums, final boolean isContentNull, final boolean isPrintZero, final DialogEtCallback dialogEtCallback) {
		Dialog dialog = null;
		if (!TextUtils.isEmpty(title) && title.equals(ToolsUtils.returnXMLStr("ct_kt"))) {
			dialog = DialogUtil
					.getDialog(context, R.layout.dialog_work_shift2, 8, LinearLayout.LayoutParams.WRAP_CONTENT);
		} else {
			dialog = DialogUtil
					.getDialog(context, R.layout.dialog_work_shift2, 8, LinearLayout.LayoutParams.WRAP_CONTENT);
			LinearLayout lin_select_work = (LinearLayout) dialog.findViewById(R.id.lin_select_work);
			lin_select_work.setVisibility(View.GONE);
		}
		TextView       print_title         = (TextView) dialog.findViewById(R.id.print_title);
		TextView       tv_standby_money    = (TextView) dialog.findViewById(R.id.tv_standby_money);
		final EditText ed_standby_moneyEnd = (EditText) dialog.findViewById(R.id.ed_standby_money);
		TextView       print_ok            = (TextView) dialog.findViewById(R.id.print_ok);
		TextView       print_cancle        = (TextView) dialog.findViewById(R.id.print_cancle);
		LinearLayout print_close_ll = (LinearLayout) dialog
				.findViewById(R.id.print_close_ll);

		if (isContentNull) {
			//            ed_standby_moneyEnd.setInputType(InputType.TYPE_CLASS_TEXT);
		}
		if (!TextUtils.isEmpty(title)) {
			print_title.setText(title);
			if (title.equals(ToolsUtils.returnXMLStr("ct_kt"))) {
				print_close_ll.setVisibility(View.GONE);
				print_cancle.setText(ToolsUtils.returnXMLStr("ct_kt"));
				print_ok.setText(ToolsUtils.returnXMLStr("open_table_cliick_dish"));
			}
		}
		if (!TextUtils.isEmpty(content)) {
			tv_standby_money.setText(content);
		}
		if (!TextUtils.isEmpty(contentHint)) {
			ed_standby_moneyEnd.setHint(contentHint);
		}
		final Dialog finalDialog = dialog;
		print_close_ll.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ToolsUtils.writeUserOperationRecords("关闭" + title + "窗口");
				finalDialog.dismiss();
				dialogEtCallback.onCancle();
			}
		});


		print_cancle.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ToolsUtils.writeUserOperationRecords("取消" + title + "按钮");
				finalDialog.dismiss();
				dialogEtCallback.onCancle();
			}
		});
		print_ok.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ToolsUtils.writeUserOperationRecords("确定" + title + "按钮");
				final String sth = ed_standby_moneyEnd.getText().toString().trim();
				if (isContentNull) {
					dialogEtCallback.onConfirm(sth);
					finalDialog.dismiss();
					return;
				}
				if (TextUtils.isEmpty(sth)) {
					MyApplication.getInstance()
							.ShowToast(ToolsUtils.returnXMLStr("please_enter_it_correctly"));
				} else if (!TextUtils.isEmpty(sth)) {
					if (maxNums != 0) {
						int num = Integer.valueOf(sth);
						if (num > maxNums) {
							MyApplication.getInstance().ShowToast(ToolsUtils
									.returnXMLStr("select_dish_person_more_is_select_person"));
						} else if (num == maxNums) {
							dialogEtCallback.onConfirm(sth);
							finalDialog.dismiss();
						}
					} else {
						int num = Integer.valueOf(sth);
						if (isPrintZero) {
							dialogEtCallback.onConfirm(sth);
							finalDialog.dismiss();
						} else {
							if (num == 0) {
								MyApplication.getInstance().ShowToast(ToolsUtils
										.returnXMLStr("please_enter_it_correctly"));
							} else {
								dialogEtCallback.onConfirm(sth);
								finalDialog.dismiss();
							}
						}
					}
				} else {
					dialogEtCallback.onConfirm(sth);
					finalDialog.dismiss();
				}
			}
		});
		dialog.show();
		return dialog;
	}

	public static Dialog aboutCloudPos(final Context context) {
		Store        store     = Store.getInstance(context);
		UserData     mUserData = UserData.getInstance(context);
		PosInfo      posInfo   = PosInfo.getInstance();
		WifiManager  wifiMgr   = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		WifiInfo     info      = wifiMgr.getConnectionInfo();
		String       wifiId    = info != null ? info.getSSID() : "";
		final Dialog dialog    = createDialog(context, R.layout.dialog_about_pos, 9, LinearLayout.LayoutParams.WRAP_CONTENT);
		dialog.setCanceledOnTouchOutside(true);
		TextView tv_appVersion     = (TextView) dialog.findViewById(R.id.tv_appVersion);
		TextView os_version        = (TextView) dialog.findViewById(R.id.os_version);
		TextView device_Name       = (TextView) dialog.findViewById(R.id.device_Name);
		TextView ip_address        = (TextView) dialog.findViewById(R.id.ip_address);
		TextView wifi_name         = (TextView) dialog.findViewById(R.id.wifi_name);
		TextView user_Name         = (TextView) dialog.findViewById(R.id.user_Name);
		TextView store_endTime     = (TextView) dialog.findViewById(R.id.store_endTime);
		TextView device_isInternet = (TextView) dialog.findViewById(R.id.device_isInternet);
		TextView server_address    = (TextView) dialog.findViewById(R.id.server_address);
		TextView memory_Size       = (TextView) dialog.findViewById(R.id.memory_Size);

		String currentapiVersion = android.os.Build.VERSION.RELEASE;
		tv_appVersion.setText("V " + ToolsUtils.getVersionName(context));
		os_version.setText("V " + currentapiVersion);
		device_Name.setText(store.getTerminalMac());
		ip_address.setText(ToolsUtils.getIPAddress(context));
		wifi_name.setText(wifiId.replace("\"", ""));
		user_Name.setText(mUserData.getRealName());
		store_endTime.setText(store.getStoreEndTime());
		device_isInternet.setText(TextUtils.isEmpty(wifiId) ? ToolsUtils
				.returnXMLStr("not_connect") : ToolsUtils.returnXMLStr("connect"));
		server_address.setText(posInfo.getServerUrl());
		memory_Size.setText(ToolsUtils.getAvailableInternalMemorySize(context));
		return dialog;
	}

	public static Dialog listDialog(final Context context, final String title, List<NetOrderRea> dataList, final DialogEtCallback dialogEtCallback) {
		if (ToolsUtils.isList(dataList)) {
			return null;
		}
		final Dialog dialog = DialogUtil
				.createDialog(context, R.layout.dialog_list_item, 9, 5);
		TextView     print_title    = (TextView) dialog.findViewById(R.id.print_title);
		LinearLayout print_close_ll = (LinearLayout) dialog.findViewById(R.id.print_close_ll);
		ListView     lv_list        = (ListView) dialog.findViewById(R.id.lv_list);
		if (!TextUtils.isEmpty(title)) {
			print_title.setText(title);
		}
		print_close_ll.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ToolsUtils.writeUserOperationRecords("关闭" + title + "窗口");
				dialog.dismiss();
				dialogEtCallback.onCancle();
			}
		});
		final DialogListAdp dialogListAdp = new DialogListAdp(context);
		dialogListAdp.setData(dataList);
		lv_list.setAdapter(dialogListAdp);
		lv_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				NetOrderRea reason = (NetOrderRea) dialogListAdp.getItem(position);
				if (reason != null) {
					dialogEtCallback.onConfirm(reason.refuseReason);
					dialog.dismiss();
				}
			}
		});
		return dialog;
	}

	/**
	 * 两个输入框的dialog
	 *
	 * @param context
	 * @param dialogTCallback
	 * @return
	 */
	public static Dialog inputDoubleDialog(final Context context, final String titleStr, final String oneLineStr, final String twoLineStr, final DialogTCallback dialogTCallback) {
		final Dialog dialog = DialogUtil
				.createDialog(context, R.layout.dialog_double_simple, 9, LinearLayout.LayoutParams.WRAP_CONTENT);
		dialog.setCanceledOnTouchOutside(false);
		TextView             tv_back   = (TextView) dialog.findViewById(R.id.tv_back);
		TextView             tv_ok     = (TextView) dialog.findViewById(R.id.tv_ok);
		TextView             tv_title  = (TextView) dialog.findViewById(R.id.tv_title);
		TextView             tv_oneStr = (TextView) dialog.findViewById(R.id.tv_oneStr);
		TextView             tv_twoStr = (TextView) dialog.findViewById(R.id.tv_twoStr);
		final CommonEditText ed_oneStr = (CommonEditText) dialog.findViewById(R.id.ed_oneStr);
		final CommonEditText ed_twoStr = (CommonEditText) dialog.findViewById(R.id.ed_twoStr);

		tv_title.setText(titleStr);
		tv_oneStr.setText(oneLineStr);
		tv_twoStr.setText(twoLineStr);
		ed_oneStr.setHint(ToolsUtils.returnXMLStr("please_input") + oneLineStr);
		ed_twoStr.setHint(ToolsUtils.returnXMLStr("please_input") + twoLineStr);
		if (twoLineStr.contains(ToolsUtils.returnXMLStr("string_login_pwd")) || twoLineStr
				.contains("password")) {
			ed_twoStr
					.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
		} else {
			ed_twoStr.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
		}

		tv_back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ToolsUtils.writeUserOperationRecords("取消" + titleStr + "按钮");
				dialog.cancel();
				dialogTCallback.onCancle();
			}
		});

		tv_ok.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ToolsUtils.writeUserOperationRecords("确定" + titleStr + "按钮");
				String edOneLineStr = ed_oneStr.getText().toString().trim();
				String edTwoLineStr = ed_twoStr.getText().toString().trim();
				if (TextUtils.isEmpty(edOneLineStr)) {
					MyApplication.getInstance()
							.ShowToast(oneLineStr + ToolsUtils.returnXMLStr("not_is_null"));
					return;
				}
				if (TextUtils.isEmpty(edTwoLineStr)) {
					MyApplication.getInstance()
							.ShowToast(twoLineStr + ToolsUtils.returnXMLStr("not_is_null"));
					return;
				}
				User user = new User();
				user.setName(edOneLineStr);
				user.setPassword(edTwoLineStr);
				dialog.cancel();
				dialogTCallback.onConfirm(user);
			}
		});
		return dialog;
	}


}
