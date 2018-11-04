package cn.acewill.mobile.pos.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.acewill.mobile.pos.R;
import cn.acewill.mobile.pos.base.activity.BaseActivity;
import cn.acewill.mobile.pos.config.Store;
import cn.acewill.mobile.pos.model.user.UserData;
import cn.acewill.mobile.pos.service.PosInfo;
import cn.acewill.mobile.pos.utils.ToolsUtils;

/**
 * Created by DHH on 2016/6/12.
 */
public class ManageSetAty extends BaseActivity {
    @BindView( R.id.title_left )
    LinearLayout titleLeft;
    @BindView( R.id.right_left_ll )
    LinearLayout rightLeftll;
    @BindView( R.id.lin_automatic )
    LinearLayout linAutoMatic;
    @BindView( R.id.ck_select_netorder )
    CheckBox ckSelectNetorder;
    @BindView( R.id.ck_select_printPackageName )
    CheckBox ckSelectPackageName;
    @BindView( R.id.ck_select_printQrCode )
    CheckBox ckSelectQrCode;
    @BindView( R.id.ck_select_kitchMoney )
    CheckBox ckSelectKitchMoney;
    @BindView( R.id.ck_select_automatic_netorder )
    CheckBox ckSelectAutoMaticNetOrder;
    @BindView( R.id.ck_select_summary_code )
    CheckBox ckSelectSummaryCode;
    @BindView( R.id.ck_select_waiMai_order_receive )
    CheckBox ckSelectWaiMaiOrderReveive;
    @BindView( R.id.ck_select_waiMai_guest_info )
    CheckBox ckSelectWaiMaiGuestInfo;
    @BindView( R.id.ck_is_create_order_jyj )
    CheckBox ckIsCreateOrderJyj;
    @BindView( R.id.ck_select_zs )
    CheckBox ckSelectZs;
    @BindView( R.id.ck_select_checkout_in_money )
    CheckBox ckSelectCheckOutInMoney;
    @BindView( R.id.ck_select_show_reDish )
    CheckBox ckSlectShowReDish;

    private Store store;
    private PosInfo posInfo;
    private UserData mUserData;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_manage_set);
        ButterKnife.bind(this);
        myApplication.addPage(ManageSetAty.this);
        initView();
    }

    private void initView() {
        store = Store.getInstance(context);
        mUserData = UserData.getInstance(context);
        posInfo = PosInfo.getInstance();
        ckSelectNetorder.setChecked(store.getReceiveNetOrder());
        ckSelectPackageName.setChecked(store.isPrintPackageName());
        ckSelectQrCode.setChecked(store.isPrintQRCode());
        ckSelectKitchMoney.setChecked(store.getKitMoneyStyle());
        ckSelectAutoMaticNetOrder.setChecked(store.getAutoMaticNetOrder());
        ckSelectSummaryCode.setChecked(store.isSummaryShowKDSCode());
        ckSelectWaiMaiOrderReveive.setChecked(store.isWaiMaiOrderReceive());
        ckSelectWaiMaiGuestInfo.setChecked(store.isWaiMaiGuestInfo());
        ckIsCreateOrderJyj.setChecked(store.isCreateOrderJyj());
        ckSelectZs.setChecked(store.isFront());
        ckSelectCheckOutInMoney.setChecked(store.isCheckOutInPutMoney());
        ckSlectShowReDish.setChecked(store.isShowReDishButton());
        if (store.getReceiveNetOrder()) {
            linAutoMatic.setVisibility(View.VISIBLE);
        } else {
            linAutoMatic.setVisibility(View.GONE);
        }

        //电子支付生成码是否为正扫(POS生成二维码让顾客扫描),反之为反扫(用机器摄像头扫描顾客二维码)
        ckSelectZs.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    store.setFront(true);
                } else {
                    store.setFront(false);
                }
            }
        });
        //是否在厨房单上显示菜品金额
        ckSelectKitchMoney.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    store.setKitMoneyStyle(true);
                } else {
                    store.setKitMoneyStyle(false);
                }
            }
        });
        //网络接单
        ckSelectNetorder.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    store.setReceiveNetOrder(true);
                    linAutoMatic.setVisibility(View.VISIBLE);
                } else {
                    store.setReceiveNetOrder(false);
                    linAutoMatic.setVisibility(View.GONE);
                }
            }
        });
        //是否自动接收网络接单
        ckSelectAutoMaticNetOrder.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    store.setAutoMaticNetOrder(true);
                } else {
                    store.setAutoMaticNetOrder(false);
                }
            }
        });
        //是否打印套餐名
        ckSelectPackageName.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    store.setPrintPackageName(true);
                } else {
                    store.setPrintPackageName(false);
                }
            }
        });
        //是否打印发票二维码
        ckSelectQrCode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    store.setPrintQRCode(true);
                } else {
                    store.setPrintQRCode(false);
                }
            }
        });
        //是否在总单小票上打印KDS叫号、消号条码
        ckSelectSummaryCode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    store.setSummaryShowKDSCode(true);
                } else {
                    store.setSummaryShowKDSCode(false);
                }
            }
        });
        //外卖单是否自动接收
        ckSelectWaiMaiOrderReveive.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    store.setWaiMaiOrderReceive(true);
                } else {
                    store.setWaiMaiOrderReceive(false);
                }
            }
        });
        //外卖单是否要输入顾客信息
        ckSelectWaiMaiGuestInfo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    store.setWaiMaiGuestInfo(true);
                } else {
                    store.setWaiMaiGuestInfo(false);
                }
            }
        });
        //线上支付是否要输入金额
        ckSelectCheckOutInMoney.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    store.setCheckOutInPutMoney(true);
                } else {
                    store.setCheckOutInPutMoney(false);
                }
            }
        });
        //订单详情界面是否显示退菜按钮
        ckSlectShowReDish.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    store.setShowReDishButton(true);
                } else {
                    store.setShowReDishButton(false);
                }
            }
        });
        //是否下单到吉野家服务器
        ckIsCreateOrderJyj.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    store.setCreateOrderJyj(true);
                } else {
                    store.setCreateOrderJyj(false);
                }
                ToolsUtils.writeUserOperationRecords("跳转到login界面");
                UserData mUserData = UserData.getInstance(context);
                mUserData.setUserName("");
                mUserData.setPwd("");
                mUserData.setSaveStated(false);
                myApplication.soundPool.stop(1);
                myApplication.clean();
                Intent orderIntent = new Intent(ManageSetAty.this, LoginAty.class);
                orderIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(orderIntent);
            }
        });
    }

    @OnClick( {R.id.title_left} )
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_left:
                finish();
                break;
        }
    }

}
