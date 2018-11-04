package cn.acewill.mobile.pos.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.acewill.mobile.pos.R;
import cn.acewill.mobile.pos.base.activity.BaseActivity;
import cn.acewill.mobile.pos.exception.PosServiceException;
import cn.acewill.mobile.pos.model.user.UserData;
import cn.acewill.mobile.pos.service.ResultCallback;
import cn.acewill.mobile.pos.service.SystemService;
import cn.acewill.mobile.pos.utils.ToolsUtils;
import cn.acewill.mobile.pos.utils.UserAction;

/**
 * Created by DHH on 2016/6/12.
 */
public class ModifyPwAty extends BaseActivity {
    @BindView( R.id.title_left )
    LinearLayout titleLeft;
    @BindView( R.id.right_left_ll )
    LinearLayout rightLeftll;
    @BindView( R.id.login_old_pw_et )
    EditText loginOldPwEt;
    @BindView( R.id.login_new_1_pw_et )
    EditText loginNew1pwEt;
    @BindView( R.id.login_new_2_pw_et )
    EditText loginNew2pwEt;

    private UserData mUserData;
    private String userName;
    private String userPw;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_modify_pw);
        ButterKnife.bind(this);
        myApplication.addPage(ModifyPwAty.this);
        initView();
    }

    private void initView() {
        myApplication.addPage(ModifyPwAty.this);
        setTitle(ToolsUtils.returnXMLStr("modify_pw"));
        setShowBtnBack(true);

        mUserData = UserData.getInstance(context);
        userName = mUserData.getUserName();
        userPw = mUserData.getPwd();
    }

    private void checkModifyPw()
    {
        String oldPw = loginOldPwEt.getText().toString().trim();
        String new1Pw = loginNew1pwEt.getText().toString().trim();
        String new2Pw = loginNew2pwEt.getText().toString().trim();
        if (TextUtils.isEmpty(oldPw)) {
            showToast(ToolsUtils.returnXMLStr("old_password_is_not_null"));
            return;
        }
        if (!oldPw.equals(userPw)) {
            showToast(ToolsUtils.returnXMLStr("input_old_password_is_error"));
            return;
        }
        if (TextUtils.isEmpty(new1Pw)) {
            showToast(ToolsUtils.returnXMLStr("new_password_is_not_null"));
            return;
        }
        if (!new1Pw.equals(new2Pw)) {
            showToast(ToolsUtils.returnXMLStr("two_passwords_are_not_consistent"));
            return;
        }
        modifyPw(oldPw,new1Pw);
    }

    private void modifyPw(String oldPw ,String newPw)
    {
        try {
            SystemService systemService = SystemService.getInstance();
            systemService.changePwd(userName, oldPw,newPw,new ResultCallback() {
                @Override
                public void onResult(Object result) {
                    if((int)result == 0)
                    {
                        showToast(ToolsUtils.returnXMLStr("modify_success"));
                        ToolsUtils.writeUserOperationRecords("跳转到login界面");
                        UserData mUserData = UserData.getInstance(context);
                        mUserData.setPwd("");
                        mUserData.setSaveStated(false);
                        myApplication.clean();
                        Intent orderIntent = new Intent(ModifyPwAty.this, LoginAty.class);
                        startActivity(orderIntent);
                    }
                }

                @Override
                public void onError(PosServiceException e) {
                    showToast(ToolsUtils.returnXMLStr("modify_pw_error")+"," + e.getMessage());
                    Log.i("修改密码失败", e.getMessage());
                }
            });
        } catch (PosServiceException e) {
            e.printStackTrace();
            showToast(ToolsUtils.returnXMLStr("modify_pw_error")+","+ e.getMessage());
            Log.i("修改密码失败", e.getMessage());
        }
    }

    @OnClick( {R.id.title_left,R.id.tv_modify} )
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_modify:
                ToolsUtils.writeUserOperationRecords("重置密码");
                checkModifyPw();
                break;
            case R.id.title_left://返回
                UserAction.log("返回", context);
                finish();
                break;

        }
    }

}
