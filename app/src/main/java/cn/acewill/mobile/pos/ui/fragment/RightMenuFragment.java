package cn.acewill.mobile.pos.ui.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.acewill.mobile.pos.R;
import cn.acewill.mobile.pos.common.PowerController;
import cn.acewill.mobile.pos.config.MyApplication;
import cn.acewill.mobile.pos.exception.PosServiceException;
import cn.acewill.mobile.pos.model.MainSelect;
import cn.acewill.mobile.pos.model.event.PosEvent;
import cn.acewill.mobile.pos.model.user.UserData;
import cn.acewill.mobile.pos.service.PosInfo;
import cn.acewill.mobile.pos.service.ResultCallback;
import cn.acewill.mobile.pos.service.SystemService;
import cn.acewill.mobile.pos.ui.activity.CallGoodsAty;
import cn.acewill.mobile.pos.ui.activity.CardRecordsAty;
import cn.acewill.mobile.pos.ui.activity.DishCountAty;
import cn.acewill.mobile.pos.ui.activity.ManageSetAty;
import cn.acewill.mobile.pos.ui.activity.MemberAty;
import cn.acewill.mobile.pos.ui.activity.ModifyPwAty;
import cn.acewill.mobile.pos.ui.activity.TestPrintAty;
import cn.acewill.mobile.pos.ui.adapter.ItemSelectAdp;
import cn.acewill.mobile.pos.utils.Constant;
import cn.acewill.mobile.pos.utils.DialogUtil;
import cn.acewill.mobile.pos.utils.ToastUitl;
import cn.acewill.mobile.pos.utils.ToolsUtils;
import cn.acewill.mobile.pos.utils.crash.FileUtil;
import cn.acewill.mobile.pos.widget.ScrolListView;

/**
 * Created by hzc on 2017/2/22.
 * 右侧菜单
 */
public class RightMenuFragment extends Fragment {

	@BindView(R.id.login_name)
	TextView      loginName;
	@BindView(R.id.workshift_btn)
	TextView      workShiftBtn;
	@BindView(R.id.logout)
	TextView      logOut;
	@BindView(R.id.work_name)
	TextView      workName;
	@BindView(R.id.version_name)
	TextView      versionName;
	@BindView(R.id.lv_item)
	ScrolListView lvItem;

	private Activity      mContext;
	private ItemSelectAdp itemSelectAdp;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View inflate = inflater.inflate(R.layout.fragment_right_menu, container, false);
		ButterKnife.bind(this, inflate);
		initView();
		return inflate;
	}

	private void initView() {
		mContext = getActivity();
		loginName.setText(UserData.getInstance(mContext).getUserName());
		workName.setText(PosInfo.getInstance().getWorkShiftName());
		versionName.setText(MyApplication.getVersionName());

		itemSelectAdp = new ItemSelectAdp(mContext);
		lvItem.setAdapter(itemSelectAdp);
		itemSelectAdp.setData(PowerController.getSelectPopList());
		lvItem.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				MainSelect mainSelect = (MainSelect) itemSelectAdp.getItem(position);
				if (mainSelect != null) {
					//沽清
					if (mainSelect.getSelectId() == PowerController.SELL_OUT) {
						ToolsUtils.writeUserOperationRecords("沽清");
						startActivity(new Intent(mContext, DishCountAty.class));
					}
					//检测更新
					if (mainSelect.getSelectId() == PowerController.UPDATE) {
						EventBus.getDefault().post(new PosEvent(PowerController.UPDATE));
					}
					//更改密码
					if (mainSelect.getSelectId() == PowerController.MODIFY_PW) {
						ToolsUtils.writeUserOperationRecords("更改密码");
						startActivity(new Intent(mContext, ModifyPwAty.class));
					}
					//门店订货
					if (mainSelect.getSelectId() == PowerController.SUPPORT_CALL_GOODS) {
						ToolsUtils.writeUserOperationRecords("门店订货");
						startActivity(new Intent(mContext, CallGoodsAty.class));
					}
					//会员
					if (mainSelect.getSelectId() == PowerController.MEMBER) {
						ToolsUtils.writeUserOperationRecords("会员");
						startActivity(new Intent(mContext, MemberAty.class));
					}
					//日结
					if (mainSelect.getSelectId() == PowerController.DAILY) {
						EventBus.getDefault().post(new PosEvent(PowerController.DAILY));
					}
					//上传日志
					if (mainSelect.getSelectId() == PowerController.UPLOAD_LOG) {
						ToolsUtils.writeUserOperationRecords("上传日志");
						upLoadLog();
						//						startActivity(new Intent(mContext, UpLoadActivity.class));
					}
					//解绑
					if (mainSelect.getSelectId() == PowerController.UNBIND_DEVICE) {
						EventBus.getDefault().post(new PosEvent(PowerController.UNBIND_DEVICE));
					}
					//关于云POS
					if (mainSelect.getSelectId() == PowerController.ABOUT_CLOUDPOS) {
						EventBus.getDefault().post(new PosEvent(PowerController.ABOUT_CLOUDPOS));
					}
					//挂账列表
					if (mainSelect.getSelectId() == PowerController.CARD_RECORDS) {
						ToolsUtils.writeUserOperationRecords("挂账列表");
						startActivity(new Intent(mContext, CardRecordsAty.class));
					}
					//设置
					if (mainSelect.getSelectId() == PowerController.ADVANCED_SETUP) {
						ToolsUtils.writeUserOperationRecords("高级设置");
						startActivity(new Intent(mContext, ManageSetAty.class));
					}
					//设置
					if (mainSelect.getSelectId() == PowerController.TEST_PRINTER) {
						ToolsUtils.writeUserOperationRecords("测试打印");
						startActivity(new Intent(mContext, TestPrintAty.class));
					}

				}
			}
		});
	}

	@OnClick({R.id.workshift_btn, R.id.logout})
	public void onClick(View view) {
		switch (view.getId()) {
			//交班
			case R.id.workshift_btn:
				EventBus.getDefault().post(new PosEvent(PowerController.SHIFT_WORK));
				break;
			//注销
			case R.id.logout:
				EventBus.getDefault().post(new PosEvent(Constant.EventState.LOGOUT));
				break;
		}
	}

	/**
	 * 上传日志文件
	 */
	private Dialog dialog;

	/**
	 * 手动只能上传当天日志
	 */
	private void upLoadLog() {
		dialog = DialogUtil
				.createDialog(mContext, R.layout.dialog_uplog, 9, LinearLayout.LayoutParams.WRAP_CONTENT);

		//0.表示今天
		//1.表示昨天
		//2.表示前天
		File   log = FileUtil.getUploadLog(0);
		if (log == null) {
			ToastUitl.showShort(mContext, "没有今天的日志");
			return;
		}
		upLog(log, dialog);
		//        zipFile(filePath, time,dialog);
	}


	//调用上传接口
	private void upLog(final File logFile, final Dialog dialog) {
		try {
			SystemService systemService = SystemService.getInstance();
			if (logFile != null) {
				systemService.upLoadLogFile(logFile, new ResultCallback() {
					@Override
					public void onResult(Object result) {
						dialog.dismiss();
						ToastUitl.showShort(mContext, ToolsUtils.returnXMLStr("upload_success"));
						Log.i("日志上传成功:", "success");
					}

					@Override
					public void onError(PosServiceException e) {
						dialog.dismiss();
						ToastUitl.showShort(mContext, ToolsUtils.returnXMLStr("upload_failure") + e
								.getMessage());
						Log.e("日志上传失败:", e.getMessage());
					}
				});
			}
		} catch (PosServiceException e) {
			e.printStackTrace();
			dialog.dismiss();
			ToastUitl.showShort(mContext, ToolsUtils.returnXMLStr("upload_failure") + e
					.getMessage());
			Log.e("日志上传失败:", e.getMessage());
		}
	}

}
