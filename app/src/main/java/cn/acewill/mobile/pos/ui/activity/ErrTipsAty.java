package cn.acewill.mobile.pos.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.acewill.mobile.pos.R;
import cn.acewill.mobile.pos.base.activity.BaseActivity;
import cn.acewill.mobile.pos.utils.Constant;
import cn.acewill.mobile.pos.utils.ToolsUtils;


/**
 * Created by DHH on 2016/6/12.
 */
public class ErrTipsAty extends BaseActivity {
    @BindView( R.id.print_title )
    TextView printTitle;
    @BindView( R.id.print_close_ll )
    LinearLayout printCloseLl;
    @BindView( R.id.tv_content )
    TextView tvContent;
    @BindView( R.id.print_cancle )
    TextView printCancle;
    @BindView( R.id.print_ok )
    TextView printOk;
    @BindView( R.id.lin_all )
    LinearLayout linAll;

    private Intent intent;
    private int ERR_CODE;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_err_tips);
        ButterKnife.bind(this);
        myApplication.addPage(ErrTipsAty.this);

        intent = getIntent();
        ERR_CODE = intent.getIntExtra("source", 0);
        String reason = "";
        switch (ERR_CODE) {
            //生成订单ID失败的错误提示标签
            case Constant.EventState.ERR_CREATE_ORDERID_FILURE:
                reason = ToolsUtils.returnXMLStr("err_reason_creat_order_id");
                break;
            //获取线上支付状态失败提示标签
            case Constant.EventState.ERR_GET_ONLINE_PAY_STATE_FAILURE:
                reason = ToolsUtils.returnXMLStr("err_online_pay_failure");
                break;
            //获取威富通支付状态失败提示标签
            case Constant.EventState.ERR_GET_WFT_PAY_STATE:
                reason = ToolsUtils.returnXMLStr("err_print_wft");
                break;
            //获取支付状态失败提示标签
            case Constant.EventState.ERR_GET_PAY_STATE_FAILURE:
                reason = ToolsUtils.returnXMLStr("err_print_pay");
                break;
        }

        printTitle.setText(ToolsUtils.returnXMLStr("err_title"));
        tvContent.setText(reason);
    }

    @OnClick( {R.id.print_close_ll, R.id.print_cancle, R.id.print_ok} )
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.print_close_ll:
                ErrTipsAty.this.finish();
                break;
            case R.id.print_cancle:
                ErrTipsAty.this.finish();
                break;
            case R.id.print_ok:
                ErrTipsAty.this.finish();
                break;
        }
    }
}
