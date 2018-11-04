package cn.acewill.mobile.pos.ui.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.acewill.mobile.pos.R;
import cn.acewill.mobile.pos.base.activity.BaseActivity;
import cn.acewill.mobile.pos.common.PrinterDataController;
import cn.acewill.mobile.pos.model.KDS;
import cn.acewill.mobile.pos.printer.Printer;
import cn.acewill.mobile.pos.ui.adapter.KdsStateAdp;
import cn.acewill.mobile.pos.ui.adapter.PrinterStateAdp;
import cn.acewill.mobile.pos.utils.UserAction;

/**
 * Created by DHH on 2016/6/12.
 */
public class TestPrintAty extends BaseActivity {
    @BindView( R.id.title_left )
    LinearLayout titleLeft;
    @BindView( R.id.right_left_ll )
    LinearLayout rightLeftll;
    @BindView( R.id.lv_printer )
    ListView lv_printer;
    @BindView( R.id.lv_KDS )
    ListView lv_KDS;

    PrinterStateAdp printerStateAdp;
    KdsStateAdp kdsStateAdp;
    long delayedTime = 800;
    long cycleTime = 4000;
    List<Printer> printerList = PrinterDataController.getPrinterList();
    List<KDS> kdsList = PrinterDataController.getKdsList();
    Timer timer;
    Handler handler;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_test_print);
        ButterKnife.bind(this);
        myApplication.addPage(TestPrintAty.this);
        initView();
        setTimer();
    }

    private void initView() {
        printerStateAdp = new PrinterStateAdp(context);
        kdsStateAdp = new KdsStateAdp(context);
        lv_printer.setAdapter(printerStateAdp);
        lv_KDS.setAdapter(kdsStateAdp);
    }

    private void setTimer() {
        printerList = PrinterDataController.getPrinterList();
        kdsList = PrinterDataController.getKdsList();
        //        final List<PrintRecord> printRecordList = PrinterDataController.getPrintRecordList();
        timer = new Timer();//轮询打印机连接状态
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                // TODO Auto-generated method stub
                super.handleMessage(msg);
                // 要做的事情
                if (printerList != null && printerList.size() > 0) {
                    printerStateAdp.setData(printerList);
                }
                if (kdsList != null && kdsList.size() > 0) {
                    kdsStateAdp.setData(kdsList);
                }
                //                if(printRecordList != null && printRecordList.size() >0)
                //                {
                //                    printRecordAdp.setData(printRecordList);
                //                }
            }
        };

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                Message message = new Message();
                message.what = 1;
                handler.sendMessage(message);
            }
        };
        timer.schedule(task, delayedTime, cycleTime);//延时3秒并且2秒循环一次获取拉卡拉交易情况
    }

    @OnClick( {R.id.title_left} )
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_left://返回
                UserAction.log("返回", context);
                finish();
                break;
        }
    }

}
