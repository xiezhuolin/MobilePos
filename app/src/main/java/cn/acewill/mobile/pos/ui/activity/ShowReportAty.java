package cn.acewill.mobile.pos.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.acewill.mobile.pos.R;
import cn.acewill.mobile.pos.base.activity.BaseActivity;
import cn.acewill.mobile.pos.common.PowerController;
import cn.acewill.mobile.pos.model.WorkShiftReport;
import cn.acewill.mobile.pos.model.event.PosEvent;
import cn.acewill.mobile.pos.service.DialogCallback;
import cn.acewill.mobile.pos.service.PosInfo;
import cn.acewill.mobile.pos.ui.adapter.ShowReportAdp;
import cn.acewill.mobile.pos.utils.Constant;
import cn.acewill.mobile.pos.utils.DialogUtil;
import cn.acewill.mobile.pos.utils.ToolsUtils;
import cn.acewill.mobile.pos.widget.ScrolListView;

import static cn.acewill.mobile.pos.utils.TimeUtil.getTimeStr;

/**
 * Created by DHH on 2017/3/21.
 */

public class ShowReportAty extends BaseActivity {
    @BindView( R.id.tv_titckTitle )
    TextView tvTitckTitle;
    @BindView( R.id.tv_printerName )
    TextView tvPrinterName;
    @BindView( R.id.tv_timeStart )
    TextView tvTimeStart;
    @BindView( R.id.tv_timeEnd )
    TextView tvTimeEnd;
    @BindView( R.id.tv_printTime )
    TextView tvPrintTime;
    @BindView( R.id.lv_report )
    ScrolListView lvReport;
    @BindView( R.id.print_cancle )
    TextView printCancle;
    @BindView( R.id.print_ok )
    TextView printOk;
    @BindView( R.id.sc_scroll )
    ScrollView scScroll;
    @BindView( R.id.tv_count )
    TextView tvCount;
    @BindView( R.id.tv_customerCount )
    TextView tvCustomerCount;
    @BindView( R.id.tv_pricePer )
    TextView tvPricePer;
    @BindView( R.id.tv_pricePerCustomer )
    TextView tvPricePerCustomer;

    private PosInfo posInfo;
    private WorkShiftReport workShiftReport;
    private int printType;

    private ShowReportAdp showReportAdp;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_show_report);
        ButterKnife.bind(this);
        initData();
    }

    private void initData() {
//        scScroll.post(new Runnable() {
//            @Override
//            public void run() {
//                scScroll.fullScroll(ScrollView.FOCUS_UP);
//            }
//        });
        posInfo = PosInfo.getInstance();
        workShiftReport = (WorkShiftReport) getIntent().getSerializableExtra("WorkShiftReport");
        printType = getIntent().getIntExtra("printType", PowerController.DAILY);
        String printStr = "";
        //日结
        if (printType == PowerController.DAILY) {
            printStr = ToolsUtils.returnXMLStr("daily");
        } else if (printType == PowerController.SHIFT_WORK) {
            printStr = ToolsUtils.returnXMLStr("shift");
        }
        tvTitckTitle.setText(printStr + ToolsUtils.returnXMLStr("ticket"));
        tvPrinterName.setText(printStr + ToolsUtils.returnXMLStr("persion") + posInfo.getRealname());
        tvTimeStart.setText(printStr + ToolsUtils.returnXMLStr("start_time") + workShiftReport.getStartTime());
        tvTimeEnd.setText(printStr + ToolsUtils.returnXMLStr("end_time") + workShiftReport.getEndTime());
        tvPrintTime.setText(ToolsUtils.returnXMLStr("print_time") + getTimeStr(System.currentTimeMillis()));
        showReportAdp = new ShowReportAdp(context);
        showReportAdp.setData(workShiftReport.getItemCategorySalesDataList());
        lvReport.setAdapter(showReportAdp);

        loadCustomerData(workShiftReport.getPctData());
    }

    private void loadCustomerData(WorkShiftReport.PctData pctData) {
        if (pctData != null) {
            //订单总数
            tvCount.setText(pctData.getOrderCounts() + ToolsUtils.returnXMLStr("article"));
            //客人总数
            tvCustomerCount.setText(pctData.getCustomerCounts() + ToolsUtils.returnXMLStr("people"));
            //平均订单金额
            tvPricePer.setText(" ￥ " + pctData.getPricePerOrder() + " / ￥");
            //客单价
            tvPricePerCustomer.setText(" ￥ " + pctData.getPricePerCustomer() + " / ￥");
        }
    }

    @OnClick( {R.id.print_cancle, R.id.print_ok} )
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.print_cancle:
                ShowReportAty.this.finish();
                break;
            case R.id.print_ok:
                DialogUtil.ordinaryDialog(context, ToolsUtils.returnXMLStr("daily"), ToolsUtils.returnXMLStr("daily_warning"), new DialogCallback() {
                    @Override
                    public void onConfirm() {
                        if(workShiftReport != null)
                        {
                            EventBus.getDefault().post(new PosEvent(Constant.EventState.PRINT_CONFIRM_DALIY,workShiftReport));
                        }
                        else{
                            showToast(ToolsUtils.returnXMLStr("daily_report_failure_please_try_again"));
                        }
                    }

                    @Override
                    public void onCancle() {

                    }
                });
                break;
        }
    }
}
