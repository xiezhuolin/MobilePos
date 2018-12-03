package cn.acewill.mobile.pos.ui.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListPopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.acewill.mobile.pos.R;
import cn.acewill.mobile.pos.base.activity.BaseActivity;
import cn.acewill.mobile.pos.common.StoreInfor;
import cn.acewill.mobile.pos.config.Configure;
import cn.acewill.mobile.pos.config.MyApplication;
import cn.acewill.mobile.pos.config.Store;
import cn.acewill.mobile.pos.dao.cache.CachedDao;
import cn.acewill.mobile.pos.exception.PosServiceException;
import cn.acewill.mobile.pos.model.StoreConfiguration;
import cn.acewill.mobile.pos.model.TerminalInfo;
import cn.acewill.mobile.pos.model.user.User;
import cn.acewill.mobile.pos.model.user.UserData;
import cn.acewill.mobile.pos.model.user.UserRet;
import cn.acewill.mobile.pos.presenter.LoginPresenter;
import cn.acewill.mobile.pos.service.PosInfo;
import cn.acewill.mobile.pos.service.ResultCallback;
import cn.acewill.mobile.pos.service.StoreBusinessService;
import cn.acewill.mobile.pos.service.SystemService;
import cn.acewill.mobile.pos.ui.DialogView;
import cn.acewill.mobile.pos.ui.adapter.UserNameAdp;
import cn.acewill.mobile.pos.utils.Constant;
import cn.acewill.mobile.pos.utils.FileLog;
import cn.acewill.mobile.pos.utils.PermissionsUtil;
import cn.acewill.mobile.pos.utils.TimeUtil;
import cn.acewill.mobile.pos.utils.ToolsUtils;
import cn.acewill.mobile.pos.utils.crash.FileUtil;
import cn.acewill.mobile.pos.widget.ProgressDialogF;

/**
 * Created by DHH on 2017/11/27.
 */

public class LoginAty extends BaseActivity implements DialogView {
	@BindView(R.id.login_title)
	TextView       loginTitle;
	@BindView(R.id.login_user)
	EditText       loginUser;
	@BindView(R.id.login_pw)
	EditText       loginPw;
	@BindView(R.id.login_btn)
	TextView       loginBtn;
	@BindView(R.id.account_ll)
	LinearLayout   accountLl;
	@BindView(R.id.login_devices)
	EditText       loginDevices;
	@BindView(R.id.device_ll)
	LinearLayout   deviceLl;
	@BindView(R.id.ll_userName)
	LinearLayout   userNameLl;
	@BindView(R.id.ll_pw)
	LinearLayout   pwLl;
	@BindView(R.id.store_endTime)
	TextView       storeEndTime;
	@BindView(R.id.rel_arrow)
	RelativeLayout relArrow;
	@BindView(R.id.login_version)
	TextView       loginVersion;

	private Store store;
	private String userName = "";
	private String pwd      = "";
	private String dvs      = "";
	private LoginPresenter  loginPresenter;
	private ProgressDialogF progressDialog;
	private PosInfo         posinfo;
	private UserData        mUserData;
	private CachedDao       cacheDao;
	private TextWatcher     watcher;
	private String          serialNumber;//设备序列号
	private boolean isBined             = false;//是否绑定过此终端
	private String  onLineServerAddress = "www.smarant.com";
	private String terminalMac;
	private long oneDayCurrTimeMillis = 86399858;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		myApplication.addPage(LoginAty.this);
		ButterKnife.bind(this);
		loadData();
		loginPresenter = new LoginPresenter(this);
		showListPopulWindow();
		requestPermisions();//申请权限
	}

	/**
	 * 自动上传昨天日志
	 */
	private void autoUploadLog() {
		String lastUploadTime = null;
		try {
			lastUploadTime = ToolsUtils.getStringFromShares("lastUploadTime", "", context);
		} catch (Exception e) {
			e.printStackTrace();
		}
		//获取当前时间
		String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
		//是不是同一天
		if (!today.equals(lastUploadTime)) {
			File log = FileUtil.getUploadLog(1);
			upLog(log);
		}

	}

	//调用上传接口
	private void upLog(final File logFile) {
		try {
			SystemService systemService = SystemService.getInstance();
			if (logFile != null) {
				systemService.upLoadLogFile(logFile, new ResultCallback() {
					@Override
					public void onResult(Object result) {
						String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
						try {
							ToolsUtils.setStringToShares("lastUploadTime", today, context);
							logFile.delete();
							FileLog.log("日志自动上传成功");
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

					@Override
					public void onError(PosServiceException e) {
						FileLog.log("日志自动上传失败1");
					}
				});
			}
		} catch (PosServiceException e) {
			FileLog.log("日志自动上传失败2");
			e.printStackTrace();
		}
	}


	@Override
	protected void onResume() {
		super.onResume();
		logicBindOrLogin(null);
		if (!TextUtils.isEmpty(terminalMac)) {
			getTerminInfo();
		} else {
			saveTerminalInfo(null);
		}
		MyApplication.setContext(context);
	}

	public void getTerminInfo() {
		onClickCount = 0;
		saveTerminalInfo(null);
		getStoreInfo(context, !TextUtils.isEmpty(terminalMac) ? terminalMac : store
				.getTerminalMac());
	}

	private void getStoreInfo(Context context, String macAddress) {
		try {
			String serviceAddress = store.getServiceAddress();
			String servicePort    = store.getStorePort();
			posinfo.setServerUrl(Constant.SERVER_ADDRESS_URL + serviceAddress + ":" + servicePort + "/");
			StoreBusinessService storeBusinessService = StoreBusinessService.getInstance();
			storeBusinessService
					.getTerminalInfo(context, macAddress, new ResultCallback<TerminalInfo>() {
						@Override
						public void onResult(TerminalInfo result) {
							if (result != null && result.isActive()) {//有绑定信息走登录流程 并且为激活状态
								StoreInfor.terminalInfo = result;
								saveTerminalInfo(result);
								logicBindOrLogin(result);
								saveCashPrinter(result);
								//                        dishMenu.setVisibility(View.GONE);
							} else {//无绑定信息走绑定流程
								saveTerminalInfo(null);
								logicBindOrLogin(result);
								terminalMac = store.getTerminalMac();
								if (!TextUtils.isEmpty(terminalMac)) {
									loginDevices.setText(terminalMac);
								}
								loginUser.setText("");
								loginPw.setText("");
							}
						}

						@Override
						public void onError(PosServiceException e) {
							showToast(resources.getString(R.string.get_store_info_failure) + "," + e
									.getMessage());
							Log.i("获取门店信息失败", e.getMessage());
						}
					});
		} catch (PosServiceException e) {
			e.printStackTrace();
			showToast(resources.getString(R.string.get_store_info_failure) + "," + e.getMessage());
			Log.i("获取门店信息失败", e.getMessage());
		}
	}

	private void isBind(boolean isBind) {
		isBined = isBind;
		if (isBind) {
			userNameLl.setVisibility(View.VISIBLE);
			pwLl.setVisibility(View.VISIBLE);
			deviceLl.setVisibility(View.GONE);
			loginBtn.setText(resources.getString(R.string.accountlogin));
			loginTitle.setText(resources.getString(R.string.accountlogin));
		} else {
			userNameLl.setVisibility(View.GONE);
			pwLl.setVisibility(View.GONE);
			deviceLl.setVisibility(View.VISIBLE);
			loginBtn.setText(resources.getString(R.string.bind));
			loginTitle.setText(resources.getString(R.string.bind));
		}
	}

	/**
	 * 找出收银打印机
	 *
	 * @param result
	 */
	private void saveCashPrinter(TerminalInfo result) {
		//        if (result != null && result.getPrinterid() > 0) {
		store.setCashPrinterId(result.getPrinterid());
		store.setCashKdsId(result.getKdsid());
		store.setSecondaryPrinterId(result.getSecondaryPrinterId());
		store.setTakeoutPrinterId(result.getTakeoutPrinterid());
		//        }
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		//		if (conn != null) {
		//			//解除LogService绑定
		//			unbindService(conn);
		//		}
	}

	/**
	 * 判断绑定还是登录
	 */
	private void logicBindOrLogin(TerminalInfo result) {
		if (!TextUtils.isEmpty(store.getTerminalMac()) && result != null && result.isActive()) {
			isBind(true);
		} else {
			isBind(false);
		}
	}

	/**
	 * 保存获取的门店终端信息
	 *
	 * @param result
	 */
	private void saveTerminalInfo(TerminalInfo result) {
		if (TextUtils.isEmpty(store.getServiceAddress())) {
			store.setServiceAddress(onLineServerAddress);
			store.setStorePort("8080");
		} else {
			store.setServiceAddress(store.getServiceAddress());
			if (store.getServiceAddress().equals(onLineServerAddress)) {
				store.setStorePort("8080");
			} else {
				if (TextUtils.isEmpty(store.getStorePort())) {
					store.setStorePort("18080");
				} else {
					store.setStorePort(store.getStorePort());
				}
			}
		}
		if (result != null) {
			store.setStoreAppId(result.appid);
			store.setBrandId(result.brandid);
			store.setStoreId(result.storeid);
			store.setStoreName(result.sname);
			store.setDeviceName(result.tname);
			store.setTerminalTypeStr(result.terminalTypeStr);
			System.out.println(System.currentTimeMillis());
			store.setStoreEndTime(TimeUtil.times(result.getStoreEndTime() + ""));
			if ((result.getStoreEndTime() - System
					.currentTimeMillis()) < (oneDayCurrTimeMillis * 30)) {
				storeEndTime.setVisibility(View.VISIBLE);
				if ((result.getStoreEndTime() - System.currentTimeMillis()) < 0) {
					storeEndTime.setText("注意:本店服务已到期,请联系服务商续费,避免影响门店正常运营.");
				} else {
					storeEndTime.setText("注意:本店将在" + TimeUtil
							.times2(result.getStoreEndTime() + "") + "终止服务,请及时联系服务商续费,避免影响门店正常运营.");
				}
			}
			store.setSaveState(true);
			saveInfo();
		}
		String serviceAddress = store.getServiceAddress();
		String servicePort    = store.getStorePort();
		if (!TextUtils.isEmpty(servicePort)) {
			posinfo.setServerUrl(Constant.SERVER_ADDRESS_URL + serviceAddress + ":" + servicePort + "/");
		} else {
			posinfo.setServerUrl(Constant.SERVER_ADDRESS_URL + serviceAddress + "/");
		}
	}

	/**
	 * 保存设置的配置信息
	 */
	private boolean saveInfo() {
		String storeMerchantsId = store.getStoreAppId();
		String storeBrandId     = store.getBrandId();
		String storeStoreId     = store.getStoreId();

		String storeDeviceName = store.getDeviceName();
		String serviceAddress  = store.getServiceAddress();
		String servicePort     = store.getStorePort();
		String macAddress      = store.getTerminalMac();

		if (TextUtils.isEmpty(serviceAddress)) {
			showToast(resources.getString(R.string.server_address_is_not_null));
			return false;
		}
		if (TextUtils.isEmpty(servicePort)) {
			showToast(resources.getString(R.string.port_is_not_null));
			return false;
		}

		PosInfo posinfo = PosInfo.getInstance();
		posinfo.setAppId(storeMerchantsId);
		posinfo.setBrandId(storeBrandId);
		posinfo.setStoreId(storeStoreId);
		posinfo.setTerminalName(storeDeviceName);
		posinfo.setTerminalMac(macAddress);
		posinfo.setReceiveNetOrder(1);
		posinfo.setVersionId(ToolsUtils.getVersionCode(this) + "");
		posinfo.setCurrentVersion(ToolsUtils.getVersionName(this));

		if (!TextUtils.isEmpty(servicePort)) {
			posinfo.setServerUrl(Constant.SERVER_ADDRESS_URL + serviceAddress + ":" + servicePort + "/");
		} else {
			posinfo.setServerUrl(Constant.SERVER_ADDRESS_URL + serviceAddress + "/");
		}
		return true;
	}


	private void loadData() {
		Configure.init(LoginAty.this);
		cacheDao = new CachedDao();
		store = Store.getInstance(context);
		progressDialog = new ProgressDialogF(this);
		myApplication.setScreenHeight(Configure.screenHeight);
		myApplication.setScreenWidth(Configure.screenWidth);
		posinfo = PosInfo.getInstance();
		loginVersion.setText(String
				.format(getResources().getString(R.string.company_name), ToolsUtils
						.getVersionName(this)));
		terminalMac = store.getTerminalMac();
		mUserData = UserData.getInstance(context);
		posinfo.setTerminalIp(ToolsUtils.getIPAddress(context));
		boolean saveState = mUserData.getSaveState();
		if (saveState) {
			String userName = mUserData.getRealName();
			String pwd      = mUserData.getPwd();
			loginUser.setText("");
			loginPw.setText("");
		}
	}

	ListPopupWindow listPopupWindow;
	UserNameAdp     userNameAdp;
	List<User> userList = new ArrayList<>();

	private void showListPopulWindow() {
		userList.clear();
		userList = cacheDao.getAllUser();
		listPopupWindow = new ListPopupWindow(this);
		userNameAdp = new UserNameAdp(context);
		listPopupWindow.setAdapter(userNameAdp);
		userNameAdp.setData(userList);
		listPopupWindow.setAnchorView(loginUser);
		listPopupWindow.setModal(true);

		listPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
				User user = (User) userNameAdp.getItem(i);
				if (user != null) {
					loginUser.setText(user.getName());
					loginPw.setText("");
					listPopupWindow.dismiss();
				}
			}
		});
	}

	//申请SD卡与电话权限
	public void requestPermisions() {
		PermissionsUtil.checkPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
				Manifest.permission.READ_PHONE_STATE});
	}


	/**
	 * 查询门店设置信息
	 */
	private void getStoreConfiguration() {
		try {
			final StoreBusinessService storeBusinessService = StoreBusinessService.getInstance();
			storeBusinessService.getStoreConfiguration(new ResultCallback<StoreConfiguration>() {
				@Override
				public void onResult(StoreConfiguration result) {
					if (result != null) {
						if (result.isTableServer()) {
							StoreInfor.storeMode = "TABLE";
						} else {
							StoreInfor.storeMode = "NOTABLE";
						}
						StoreInfor.printConfiguration = result.getPrintConfiguration();
						StoreInfor.cardNumberMode = result.isCardNumberMode();
						StoreInfor.setRepastPopulation(result.isRetreatCheckAuthority());
						StoreInfor.wipeZero = result.getWipeZero();
						storeBusinessService.getDao().saveStoreConfiguration(result);
						jumpTableMain();
					}
				}

				@Override
				public void onError(PosServiceException e) {
					Log.i("门店配置信息获取失败", e.getMessage());
				}
			});
		} catch (PosServiceException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void showDialog() {
		loginUser.setEnabled(false);
		progressDialog.showLoading(resources.getString(R.string.loging));
	}

	@Override
	public void dissDialog() {
		loginUser.setEnabled(true);
		progressDialog.disLoading();
	}

	@Override
	public void showError(PosServiceException e) {
		loginUser.setEnabled(true);
		showToast(resources.getString(R.string.login_failure) + "," + e.getMessage());
	}

	@Override
	public <T> void callBackData(T t) {
		User    user     = (User) t;
		UserRet userInfo = user.getUserRet();
		//        if(store.isRememberPw())
		//        {
		mUserData.setUserName(userName);
		mUserData.setPwd(pwd);
		mUserData.setRealName(userInfo.getRealname());
		//            mUserData.setSaveStated(true);
		//        }

		PosInfo posinfo = PosInfo.getInstance();
		posinfo.setUsername(userName);
		posinfo.setUserId(user.getId());
		posinfo.setRealname(userInfo.getRealname());
		if (userInfo.getSupportCallMaterial() != 0) {
			store.setSupportCallGoods(true);
		}
		getStoreConfiguration();
		autoUploadLog();
	}

	/**
	 * 绑定门店
	 *
	 * @param terminalMac
	 */
	private void bindStore(final String terminalMac) {
		try {
			StoreBusinessService storeBusinessService = StoreBusinessService.getInstance();
			storeBusinessService.bindStore(terminalMac, new ResultCallback<TerminalInfo>() {
				@Override
				public void onResult(TerminalInfo result) {
					if (result != null)//绑定成功
					{
						store.setTerminalMac(terminalMac);//绑定成功后将机器识别码写入到文件中
						store.setBindUUID(result.getBindUUID());
						showToast(resources.getString(R.string.auth_success_now_login));
						logicBindOrLogin(result);
						StoreInfor.terminalInfo = result;
						saveCashPrinter(result);
						saveTerminalInfo(result);
						saveInfo();


					}
				}

				@Override
				public void onError(PosServiceException e) {
					showToast(resources.getString(R.string.bind_store_failure) + "," + e
							.getMessage());
					Log.i("绑定门店失败", e.getMessage());
				}
			});
		} catch (PosServiceException e) {
			e.printStackTrace();
			showToast(resources.getString(R.string.bind_store_failure) + "," + e.getMessage());
			Log.i("绑定门店失败", e.getMessage());
		}
	}

	/**
	 * ****登录
	 */
	private void Login() {
		userName = loginUser.getText().toString().trim();
		pwd = loginPw.getText().toString().trim();
		terminalMac = loginDevices.getText().toString().trim();
		if (!isBined)//绑定
		{
			if (TextUtils.isEmpty(terminalMac)) {
				showToast(resources.getString(R.string.terminal_auth_code_is_not_null));
				return;
			}
			ToolsUtils.writeUserOperationRecords("绑定设备");
			store.setDeviceName(terminalMac);
			bindStore(terminalMac);
		} else//登录
		{
			if (TextUtils.isEmpty(userName)) {
				showToast(resources.getString(R.string.terminal_auth_code_is_not_null));
				return;
			} else if (TextUtils.isEmpty(pwd)) {
				showToast(resources.getString(R.string.input_password));
				return;
			}
			if (!saveInfo()) {
				return;
			}
			ToolsUtils.writeUserOperationRecords("登录设备");
			loginPresenter.getLoginWork(userName, pwd);
		}
	}


	int onClickCount = 0;//已点击的次数
	int logicClick   = 4;//需要点击的次数

	@OnClick({R.id.login_title, R.id.login_btn, R.id.rel_arrow})
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.login_title:
				++onClickCount;
				if (onClickCount >= logicClick) {
					startActivityForResult(new Intent(LoginAty.this, SettingActivity.class), 1);
					//                    DialogUtil.loginSetDialog(this);
				}
				break;
			case R.id.login_btn:
				Login();
				break;
			case R.id.rel_arrow:
				if (!ToolsUtils.isList(userList) && listPopupWindow != null) {
					listPopupWindow.show();
				} else {
					showToast(resources.getString(R.string.user_login_recording_is_null));
				}
				break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (store.getSaveState()) {
			getTerminInfo();
			saveInfo();
		}
	}


	private void jumpTableMain() {
		Intent orderIntent = new Intent(LoginAty.this, OrderDishMainAty.class);
		myApplication.clean();
		LoginAty.this.finish();
		startActivity(orderIntent);
	}
}
