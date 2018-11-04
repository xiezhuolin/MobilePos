package cn.acewill.mobile.pos.ui.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.acewill.mobile.pos.R;
import cn.acewill.mobile.pos.base.activity.BaseActivity;
import cn.acewill.mobile.pos.config.Store;
import cn.acewill.mobile.pos.service.PosInfo;
import cn.acewill.mobile.pos.utils.ToolsUtils;
import cn.acewill.mobile.pos.utils.UserAction;
import cn.acewill.mobile.pos.widget.TitleView;

/**
 * 系统设置
 */
public class SettingActivity extends BaseActivity {

    @BindView( R.id.title_left)
    LinearLayout title_left;
    @BindView( R.id.lin_jyj)
    LinearLayout linJyj;
    @BindView( R.id.setting_title)
    TitleView settingTitle;
    @BindView( R.id.login_server)
    EditText loginServer;
    @BindView( R.id.login_port)
    EditText loginPort;
    @BindView( R.id.login_merchant)
    EditText loginMerchant;
    @BindView( R.id.login_store)
    EditText loginStore;
    @BindView( R.id.device_name)
    EditText deviceName;
    @BindView( R.id.login_server_jyj)
    EditText login_server_jyj;
    @BindView( R.id.login_port_jyj)
    EditText login_port_jyj;
    @BindView( R.id.save_btn)
    TextView saveBtn;
    @BindView( R.id.store_info)
    TextView store_info;

    private Store store;
    boolean isCreateOrderJyj = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);
        initView();
    }

    private void initView(){
        store = Store.getInstance(context);
        PosInfo posInfo = PosInfo.getInstance();
        isCreateOrderJyj = store.isCreateOrderJyj();
        if (isCreateOrderJyj) {
            linJyj.setVisibility(View.VISIBLE);
        } else {
            linJyj.setVisibility(View.GONE);
        }
        login_server_jyj.setText(store.getServiceAddressJyj());
        login_port_jyj.setText(store.getStorePortJyj());

        loginServer.setText(store.getServiceAddress());
        loginPort.setText(store.getStorePort());
//        StringBuilder sb = new StringBuilder();
//        sb.append("设备号:").append(store.getDeviceName()).append(";  商户号:").append(store.getStoreAppId())
//                .append(";  品牌号:").append(store.getBrandId()).append(";  门店号:").append(store.getStoreName())
//        .append(";  唯一标示:").append(posInfo.getTerminalMac());
//        store_info.setText(sb.toString());
//        store_info.setTextColor(0xff000000);

//        loginMerchant.setText(store.getStoreAppId());
//        loginStore.setText(store.getStoreId());
//        loginBrand.setText(store.getBrandId());
//        deviceName.setText(store.getDeviceName());

//        loginServer.setText("43.241.226.10");
//        loginServer.setText("sz.canxingjian.com");
//        loginPort.setText("18080");
//        loginMerchant.setText("a123");
//        loginStore.setText("2");
//        loginBrand.setText("2");
//        deviceName.setText("pos1444");
    }



    @OnClick({R.id.save_btn, R.id.title_left})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.save_btn:
                UserAction.log("保存",this);
                setData();
                break;
            case R.id.title_left:
                UserAction.log("返回",this);
                finish();
                break;
        }



    }

    private void setData(){
//        String storeMerchantsId = loginMerchant.getText().toString().trim();
//        String storeBrandId = loginBrand.getText().toString().trim();
//        String storeStoreId = loginStore.getText().toString().trim();

//        String storeDeviceName = deviceName.getText().toString().trim();
        String serviceAddress = loginServer.getText().toString().trim();
        String servicePort = loginPort.getText().toString().trim();

        if (TextUtils.isEmpty(serviceAddress)) {
            myApplication.ShowToast("服务器地址不能为空");
            return;
        }
        if (TextUtils.isEmpty(servicePort)) {
            myApplication.ShowToast("端口号不能为空");
            return;
        }

        if (isCreateOrderJyj) {
            String serviceAddress_jyj = login_server_jyj.getText().toString().trim();
            String servicePort_jyj = login_port_jyj.getText().toString().trim();
            if (TextUtils.isEmpty(serviceAddress_jyj)) {
                myApplication.ShowToast(ToolsUtils.returnXMLStr("server_address_is_not_null_jyj"));
                return;
            }
            if (TextUtils.isEmpty(servicePort_jyj)) {
                myApplication.ShowToast(ToolsUtils.returnXMLStr("port_is_not_null_jyj"));
                return;
            }
            store.setServiceAddressJyj(serviceAddress_jyj);
            store.setStorePortJyj(servicePort_jyj);
        }
//        if (TextUtils.isEmpty(storeMerchantsId)) {
//            myApplication.ShowToast("商户ID不能为空");
//            return;
//        }
//
//        if (TextUtils.isEmpty(storeStoreId)) {
//            myApplication.ShowToast("门店ID不能为空");
//            return;
//        }
//
//        if (TextUtils.isEmpty(storeBrandId)) {
//            myApplication.ShowToast("品牌ID不能为空");
//            return;
//        }

//        if (TextUtils.isEmpty(storeDeviceName)) {
//            myApplication.ShowToast("设备名称不能为空");
//            return;
//        }

        store.setStorePort(servicePort);
//        store.setStoreAppId(storeMerchantsId);
//        store.setBrandId(storeBrandId);
//        store.setStoreId(storeStoreId);
        store.setServiceAddress(serviceAddress);
//        store.setDeviceName(storeDeviceName);
        store.setSaveState(true);
        finish();
    }
}
