package cn.acewill.mobile.pos.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.acewill.mobile.pos.R;
import cn.acewill.mobile.pos.base.activity.BaseActivity;
import cn.acewill.mobile.pos.model.order.OrderItem;
import cn.acewill.mobile.pos.utils.Constant;
import cn.acewill.mobile.pos.utils.ToolsUtils;


/**
 * Created by DHH on 2016/6/12.
 */
public class ErrPrinteAty extends BaseActivity {
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
    private int ERR_PRINTER_CODE;
//    private Printer printer;
//    private Order order;
//    private OrderItem oi;
    private String errorStr;
//    private ArrayList<OrderItem> oiList;
//    private Dish.Package dishPackageItem;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_err_printer);
        ButterKnife.bind(this);
        myApplication.addPage(ErrPrinteAty.this);

        intent = getIntent();
        errorStr = intent.getStringExtra("errStr");
        ERR_PRINTER_CODE = intent.getIntExtra("source", 0);
//        order = (Order) intent.getSerializableExtra("order");
//        printer = (Printer) intent.getSerializableExtra("printer");
//        oi = (OrderItem) intent.getSerializableExtra("oi");
//        oiList = (ArrayList) intent.getParcelableArrayListExtra("oiList");
//        dishPackageItem = (Dish.Package) intent.getSerializableExtra("dishPackage");
        String title = "";
        switch (ERR_PRINTER_CODE) {
            //打印订单小票出错
            case Constant.EventState.ERR_PRINT_ORDER:
                title = ToolsUtils.returnXMLStr("err_print_order");
                break;
            //打印厨房小票出错
            case Constant.EventState.ERR_PRINT_KITCHEN_ORDER:
                title = ToolsUtils.returnXMLStr("err_print_kitchen_order");
                break;
            //打印退单小票出错
            case Constant.EventState.ERR_PRINT_REFUND_ORDER:
                title = ToolsUtils.returnXMLStr("err_print_refund_order");
                break;
            //打印客用订单小票出错
            case Constant.EventState.ERR_PRINT_GUEST_ORDER:
                title = ToolsUtils.returnXMLStr("err_print_guest_order");
                break;
            //打印厨房总单小票出错
            case Constant.EventState.ERR_PRINT_KITCHEN_SUMMARY_ORDER:
                title = ToolsUtils.returnXMLStr("err_print_kitchen_summary_order");
                break;
        }

        printTitle.setText(title);
        tvContent.setText(errorStr+ToolsUtils.returnXMLStr("err_print_reason"));
    }

//    private void setOrderItemPrint(OrderItem oi, boolean printState) {
//        try {
//            int size = order.getItemList().size();
//            for (int i = 0; i < size; i++) {
//                OrderItem orderItem = order.getItemList().get(i);
//                if((orderItem != null && orderItem.getId() >0) && (oi != null && oi.getId() >0))
//                {
//                    if (orderItem.getId() == oi.getId()) {
//                        if (orderItem.getSubItemList() != null && orderItem.getSubItemList().size() > 0) {
//                            OrderItem item = order.getItemList().get(i);
//                            List<Dish.Package> subDishs = item.getSubItemList();
//                            int subDishSize = subDishs.size();
//                            for (int j = 0; j < subDishSize; j++) {
//                                Dish.Package subdish = subDishs.get(j);
//                                if(subdish.getDishId() == dishPackageItem.getDishId())
//                                {
//                                    order.getItemList().get(i).getSubItemList().get(j).isPrint = printState;
//                                }
//                            }
//                        }
//                        else
//                        {
//                            order.getItemList().get(i).setPrintState(printState);
//                        }
//                    }
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    private void setOrderItemListPrint(ArrayList<OrderItem> oiList, boolean printState)
    {
        if(oiList != null && oiList.size() >0)
        {
            int size = oiList.size();
            for (int i = 0; i < size; i++) {
                oiList.get(i).setPrintState(printState);
            }
        }
    }

    @OnClick( {R.id.print_close_ll, R.id.print_cancle, R.id.print_ok} )
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.print_close_ll:
                finish();
                break;
            case R.id.print_cancle:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        switch (ERR_PRINTER_CODE) {
                            //打印厨房总单小票出错
                            case Constant.EventState.ERR_PRINT_KITCHEN_SUMMARY_ORDER:
                                ToolsUtils.writeUserOperationRecords("打印厨房小票总单出错--取消--order信息==>>");
//                                setOrderItemListPrint(oiList, true);
//                                printManager.printOrderKitSummaryTicket(order);
                                break;
                            //打印厨房小票出错
                            case Constant.EventState.ERR_PRINT_KITCHEN_ORDER:
                                ToolsUtils.writeUserOperationRecords("打印厨房小票出错--取消--order信息==>>");
//                                setOrderItemPrint(oi, true);
//                                printManager.printKitChenOrder(order);
                                break;
                            //打印订单小票出错
                            case Constant.EventState.ERR_PRINT_ORDER:
                                break;
                            //打印退单小票出错
                            case Constant.EventState.ERR_PRINT_REFUND_ORDER:
                                break;
                            //打印客用订单小票出错
                            case Constant.EventState.ERR_PRINT_GUEST_ORDER:
                                break;
                        }
                    }
                }).start();
                ErrPrinteAty.this.finish();
                break;
            case R.id.print_ok:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        switch (ERR_PRINTER_CODE) {
                            //打印厨房总单小票出错
                            case Constant.EventState.ERR_PRINT_KITCHEN_SUMMARY_ORDER:
                                ToolsUtils.writeUserOperationRecords("打印厨房小票总单出错--取消--order信息==>>");
//                                printManager.printOrderKitSummaryTicket(order);
                                break;
                            //打印厨房小票出错
                            case Constant.EventState.ERR_PRINT_KITCHEN_ORDER:
                                ToolsUtils.writeUserOperationRecords("打印厨房小票出错--确定--order信息==>>");
//                                printManager.printKitChenOrder(order);
                                break;
                            //打印订单小票出错
                            case Constant.EventState.ERR_PRINT_ORDER:
                                ToolsUtils.writeUserOperationRecords("打印订单小票出错--确定--order信息==>>");
//                                printManager.printCheckOut(order);
                                break;
                            //打印退单小票出错
                            case Constant.EventState.ERR_PRINT_REFUND_ORDER:
                                ToolsUtils.writeUserOperationRecords("打印退单小票出错--确定--order信息==>>");
//                                printManager.printRefundOrder(order);
                                break;
                            //打印客用订单小票出错
                            case Constant.EventState.ERR_PRINT_GUEST_ORDER:
                                ToolsUtils.writeUserOperationRecords("打印客用小票出错--确定--order信息==>>");
//                                printManager.printGuestOrder(order);
                                break;
                        }
                    }
                }).start();
                ErrPrinteAty.this.finish();
                break;
        }
    }
}
