package cn.acewill.mobile.pos.ui.activity;

import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.acewill.mobile.pos.R;
import cn.acewill.mobile.pos.base.activity.BaseActivity;
import cn.acewill.mobile.pos.exception.PosServiceException;
import cn.acewill.mobile.pos.interfices.RefrushLisener;
import cn.acewill.mobile.pos.model.wsh.Account;
import cn.acewill.mobile.pos.service.ResultCallback;
import cn.acewill.mobile.pos.service.WshService;
import cn.acewill.mobile.pos.ui.adapter.MemberOrderAdapter;
import cn.acewill.mobile.pos.utils.ScanGunKeyEventHelper;
import cn.acewill.mobile.pos.utils.ToolsUtils;
import cn.acewill.mobile.pos.widget.CommonEditText;
import cn.acewill.mobile.pos.widget.ProgressDialogF;

/**
 * 会员
 * Created by DHH on 2016/6/12.
 */
public class MemberAty extends BaseActivity implements ScanGunKeyEventHelper.OnScanSuccessListener, SwipeRefreshLayout.OnRefreshListener, RefrushLisener {
    @BindView( R.id.search_cotent )
    CommonEditText edMemberNumber;
    @BindView( R.id.search_clear )
    LinearLayout searchClear;
    @BindView( R.id.order_lv )
    RecyclerView orderLv;
    @BindView( R.id.order_srl )
    SwipeRefreshLayout orderSrl;
    @BindView( R.id.tv_login )
    TextView tvLogin;

    private String TAG = "MemberAty";
    private ProgressDialogF progressDialog;
    private ScanGunKeyEventHelper mScanGunKeyEventHelper;
    private SoundPool soundPoolSuccess;
    private SoundPool soundPoolFailure;

    private int peopleNums = 1;//顾客人数
    private int page = 0;
    private int limit = 20;

    private MemberOrderAdapter adapter;
    private List<Account> memberList = new ArrayList<Account>();
    private int lastVisibleItem = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_member);
        ButterKnife.bind(this);
        initView();
        loadData();
//        ViewUtil.setActivityWindow(context, 9, 9);
    }

    private void initMucic() {
        soundPoolSuccess = new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);
        soundPoolSuccess.load(this, R.raw.scan_success, 1);

        soundPoolFailure = new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);
        soundPoolFailure.load(this, R.raw.scan_failure, 1);
    }

    private void playScanFailureMusic() {
        soundPoolFailure.play(1, 1, 1, 0, 0, 1);
    }

    private void playScanSuccessMusic() {
        soundPoolSuccess.play(1, 1, 1, 0, 0, 1);
    }

    private void loadData() {
        //edtext的控件内容监听
        edMemberNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    searchClear.setVisibility(View.VISIBLE);
                } else {
                    searchClear.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


    }

    private void initView() {
        myApplication.addPage(MemberAty.this);
        setTitle(ToolsUtils.returnXMLStr("member"));
        setShowBtnBack(true);
        setRightText(ToolsUtils.returnXMLStr("confirm"));
        mScanGunKeyEventHelper = new ScanGunKeyEventHelper(this);
        progressDialog = new ProgressDialogF(this);
        initMucic();
        orderSrl.setOnRefreshListener(this);
        orderSrl.setColorSchemeResources(R.color.green, R.color.blue, R.color.black);
        adapter = new MemberOrderAdapter(context, memberList, this);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(OrientationHelper.VERTICAL);
        orderLv.setLayoutManager(linearLayoutManager);

        orderLv.setAdapter(adapter);
        orderLv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                if (lastVisibleItem + 1 == adapter.getItemCount() && adapter.load_more_status == adapter.LOAD_MORE && dy > 0) {
                    adapter.setLoadType(adapter.UP_LOAD_TYPE);
                    adapter.changeMoreStatus(adapter.LOADING);
                }
            }
        });

    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onScanSuccess(String barcode) {
        if (!TextUtils.isEmpty(barcode)) {
            edMemberNumber.setText("");
            edMemberNumber.setText(barcode);
            validateUserCommitDealInfo(barcode);
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent keyCode) {
        if (keyCode != null && keyCode.getKeyCode() != KeyEvent.KEYCODE_BACK && keyCode.getKeyCode() != KeyEvent.KEYCODE_HOME && keyCode.getKeyCode() != KeyEvent.KEYCODE_MENU) {
            mScanGunKeyEventHelper.analysisKeyEvent(keyCode);
            return true;
        }
        return false;
    }

    /**
     * 验证会员信息
     *
     * @param memberNumber
     */
    private void validateUserCommitDealInfo(final String memberNumber) {
        try {
            progressDialog.showLoading("");
            WshService wshService = WshService.getInstance();
            wshService.getMemberInfo(memberNumber, new ResultCallback<List<Account>>() {
                @Override
                public void onResult(List<Account> result) {
                    progressDialog.dismiss();
                    Log.i("验证成功", "会员账号:" + memberNumber);
                    if (result != null && result.size() > 0) {
                        playScanSuccessMusic();
                        getMemberInfoList(result);
                        orderLv.scrollToPosition(0);
                    } else {
                        showToast(ToolsUtils.returnXMLStr("verification_member_error"));
                        Log.i(TAG, "后台轮训网上订单信息条数=====0");
                        playScanFailureMusic();
                    }
                }

                @Override
                public void onError(PosServiceException e) {
                    progressDialog.dismiss();
                    showToast(ToolsUtils.returnXMLStr("verification_member_error") + e.getMessage());
                    Log.i("验证会员失败", "会员账号:" + memberNumber);
                    playScanFailureMusic();
                }
            });
        } catch (PosServiceException e) {
            e.printStackTrace();
            progressDialog.dismiss();
            Log.i("验证会员失败", "会员账号:" + memberNumber);
        }
    }


    @OnClick( {R.id.search_clear, R.id.tv_login} )
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_login:
                validationInfo();
                break;
            case R.id.search_clear:
                edMemberNumber.setText("");
                break;
        }
    }

    private void validationInfo() {
        String barcode = edMemberNumber.getText().toString().trim();
        if (!TextUtils.isEmpty(barcode)) {
            validateUserCommitDealInfo(barcode);
        } else {
            showToast(ToolsUtils.returnXMLStr("member_number_is_not_null"));
        }
    }

    @Override
    public void onRefresh() {
        validationInfo();
    }

    @Override
    public void refrush() {
        validationInfo();
    }

    public void getMemberInfoList(List<Account> memberList) {
        if (!ToolsUtils.isList(memberList)) {
            adapter.setLoadType(adapter.DOWN_LOAD_TYPE);
            setMemberInfoData(memberList);
        } else {
            adapter.setData(null);
            adapter.changeMoreStatus(adapter.NO_MORE);
            orderSrl.setRefreshing(false);
            Log.i("没有会员信息", ToolsUtils.getPrinterSth(memberList));
        }
    }

    private void setMemberInfoData(List<Account> memberList) {
        if (memberList != null && memberList.size() > 0) {
            adapter.setData(memberList);
            if (memberList.size() < limit) {
                adapter.changeMoreStatus(adapter.NO_MORE);
            } else {
                adapter.changeMoreStatus(adapter.LOAD_MORE);
            }
        } else {
            adapter.changeMoreStatus(adapter.NO_MORE);
        }
        orderSrl.setRefreshing(false);
    }

}
