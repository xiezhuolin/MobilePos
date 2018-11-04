package cn.acewill.mobile.pos.base.activity;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.acewill.mobile.pos.R;
import cn.acewill.mobile.pos.config.MyApplication;
import cn.acewill.mobile.pos.utils.PermissionsUtil;
import cn.acewill.mobile.pos.utils.SystemUIUtils;
import cn.acewill.mobile.pos.utils.WindowUtil;
import cn.acewill.mobile.pos.widget.ProgressDialogF;


/**
 * Created by DHH on 2016/6/12.
 */
public class BaseActivity extends FragmentActivity {
    public MyApplication myApplication;
    public Resources resources;
    public Context context;

    private LinearLayout contentRl;
    public TextView textTitle;
//    public ImageView imgIcon;
    private ImageView imgRightIcon;
    public TextView tvLogin;
    public RelativeLayout rel_back;
    public RelativeLayout rel_title;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_base);
        //初始化 上下文对象
        context = BaseActivity.this;
        progressDialogF = new ProgressDialogF(context);

//        //友盟
//        MobclickAgent.setScenarioType(this, MobclickAgent.EScenarioType.E_UM_NORMAL);

        //初始化 MyApplication
        myApplication = MyApplication.getInstance();
        //初始化 Resources
        resources = getResources();

        contentRl = (LinearLayout) findViewById(R.id.contentRl);
        rel_back = (RelativeLayout) findViewById(R.id.rel_back);
        rel_title = (RelativeLayout) findViewById(R.id.rel_title);
        textTitle = (TextView) findViewById(R.id.textTitle);
        imgRightIcon = (ImageView) findViewById(R.id.img_rightIcon);
        tvLogin = (TextView) findViewById(R.id.tv_login);

        rel_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    protected void onPause() {
        super.onPause();
        WindowUtil.hiddenKey();
    }

    public void closeMenu(){};

    @Override
    public void onBackPressed() {
        WindowUtil.hiddenKey();
        super.onBackPressed();
    }

    public void setContentXml(int layoutID) {
        addViewXML(contentRl, layoutID, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    }

    public void addViewXML(ViewGroup group, int id, int width, int height) {
        View contentView = View.inflate(this, id, null);
        group.addView(contentView, width, height);
    }

    public void setShowBtnBack(boolean isShow)
    {
        rel_back.setVisibility(isShow == true ? View.VISIBLE:View.INVISIBLE);
    }

    public void setTitle(String str) {
        if (str == null || TextUtils.isEmpty(str)) {
            textTitle.setText(R.string.app_name);
        } else {
            textTitle.setText(str);
        }
    }

    public void setRightText(String str)
    {
        if (str == null || TextUtils.isEmpty(str)) {
            tvLogin.setVisibility(View.GONE);
        } else {
            tvLogin.setVisibility(View.VISIBLE);
            tvLogin.setText(str);
        }
    }

    public void setRightImage(int imageResId)
    {
        if(imageResId < 0)
        {
            imgRightIcon.setVisibility(View.GONE);
        }
        else
        {
            imgRightIcon.setVisibility(View.VISIBLE);
            imgRightIcon.setBackgroundResource(imageResId);
        }
    }

    //申请SD卡与电话权限
    public void requestPermisions(){
        PermissionsUtil.checkPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_PHONE_STATE});
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode== PermissionsUtil.PERMISSION_REQUEST_CODE){
            if(grantResults!=null&&grantResults.length>0){
                for (int i = 0; i < grantResults.length; i++) {
                    if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                        //未开启相关权限
                        //MyApplication.getInstance().exit();
                        showToast("请打开设置-应用-开启SD卡与电话权限");
                    }
                }
            }
        }
    }

    /**
     * activity跳转
     *
     * @param clas activity类
     */
    protected void startActivity(Class<? extends BaseActivity> clas) {
        if (clas == null)
            return;
        Intent intent = new Intent(this, clas);
        startActivity(intent);
    }

    /**
     * 跳转Activity
     */
    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
    }

    public void showToast(String str) {
        myApplication.ShowToast(str);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        myApplication.popActivity(this);
    }

    public ProgressDialogF progressDialogF;
    public void showProgress()
    {
        progressDialogF.showLoading("");
    }

    public void showProgress(String str)
    {
        progressDialogF.showLoading(str);
    }


    public void dissmiss() {
        progressDialogF.disLoading();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        SystemUIUtils.setStickFullScreen(getWindow().getDecorView());
    }

}
