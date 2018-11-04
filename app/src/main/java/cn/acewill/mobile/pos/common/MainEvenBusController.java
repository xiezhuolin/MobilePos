package cn.acewill.mobile.pos.common;

import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import cn.acewill.mobile.pos.config.MyApplication;
import cn.acewill.mobile.pos.config.Store;
import cn.acewill.mobile.pos.exception.PosServiceException;
import cn.acewill.mobile.pos.model.KDS;
import cn.acewill.mobile.pos.model.KdsDishItem;
import cn.acewill.mobile.pos.model.KdsOrderData;
import cn.acewill.mobile.pos.model.dish.Dish;
import cn.acewill.mobile.pos.model.dish.Option;
import cn.acewill.mobile.pos.model.event.PosEvent;
import cn.acewill.mobile.pos.model.order.Order;
import cn.acewill.mobile.pos.model.order.OrderItem;
import cn.acewill.mobile.pos.model.order.PaymentStatus;
import cn.acewill.mobile.pos.printer.Printer;
import cn.acewill.mobile.pos.service.OrderService;
import cn.acewill.mobile.pos.service.ResultCallback;
import cn.acewill.mobile.pos.service.retrofit.response.KDSResponse;
import cn.acewill.mobile.pos.utils.Constant;
import cn.acewill.mobile.pos.utils.TimeUtil;
import cn.acewill.mobile.pos.utils.ToolsUtils;

import static cn.acewill.mobile.pos.common.DishDataController.initOrder2;


/**
 * Created by DHH on 2017/3/29.
 */

public class MainEvenBusController {
    /**
     * 打印客用小票
     *
     * @param order
     * @param guestReceiptCounts
     */
    public static void printGuestOrder(final Store store, final Order order, final int guestReceiptCounts, final int checkOutPrintCounts) {
        if (order != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Order tempCost = ToolsUtils.cloneTo(order);
                    if (order.getOrderType().equals("SALE_OUT") && PrinterDataController.getInstance().getTakeoutPrinter() != null) {
                        if (guestReceiptCounts == 0 && store.getReceiveNetOrder()) {
                            EventBus.getDefault().post(new PosEvent(Constant.EventState.NETORDER_ON));
                        }
                        for (int i = 0; i < guestReceiptCounts; i++) {
                            tempCost.setPrinterType(Constant.EventState.PRINT_WAIMAI);
                            PrinterDataController.getInstance().printGuestOrder(tempCost, Constant.EventState.PRINTER_ORDER);
                            threadSleep(4);
                        }
                    } else {
                        if (!ToolsUtils.isList(order.getItemList())) {
                            if (guestReceiptCounts == 0 && store.getReceiveNetOrder()) {
                                EventBus.getDefault().post(new PosEvent(Constant.EventState.NETORDER_ON));
                            }
                            for (int i = 0; i < guestReceiptCounts; i++) {
                                PrinterDataController.getInstance().printGuestOrder(order, Constant.EventState.PRINTER_ORDER);
                                threadSleep(4);
                            }
                        }
                        if (!ToolsUtils.isList(tempCost.getItemList())) {
                            if (PrinterDataController.getInstance().getSecondartPrinter() != null && !order.getOrderType().equals("EAT_IN")) {
                                if (guestReceiptCounts == 0 && store.getReceiveNetOrder()) {
                                    EventBus.getDefault().post(new PosEvent(Constant.EventState.NETORDER_ON));
                                }
                                for (int i = 0; i < guestReceiptCounts; i++) {
                                    tempCost.setPrinterType(Constant.EventState.PRINT_WAIMAI);
                                    PrinterDataController.getInstance().printGuestOrder(tempCost, Constant.EventState.PRINTER_ORDER);
                                    threadSleep(4);
                                }
                            }
                        }
                    }
                }
            }).start();//启动打印线程
        }
    }

    /**
     * 打印结账单
     *
     * @param order
     * @param position
     * @param checkOutPrintCounts
     */
    public static void printCheckOutOrder(final Store store, final Order order, final int position, final int checkOutPrintCounts) {
        if (order != null) {
            if (position == Constant.EventState.ORDER_TYPE_ADVANCE) {
                order.setPrinterType(Constant.EventState.ORDER_TYPE_ADVANCE);
            } else {
                order.setPrinterType(-1);
            }
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (!ToolsUtils.isList(order.getItemList())) {
                        if (order.getPrinterType() == Constant.EventState.ORDER_TYPE_ADVANCE) {
                            //预结单只打印一份
                            PrinterDataController.getInstance().printGuestOrder(order, Constant.EventState.PRINT_CHECKOUT);
                            threadSleep(3);
                        } else {
                            //结账单会根据需要打多份
                            //如果是补打模式
                            if (order.isReprintState()) {
                                PrinterDataController.getInstance().printGuestOrder(order, Constant.EventState.PRINT_CHECKOUT);
                                threadSleep(3);
                            } else {
                                if (order.getOrderType().equals("SALE_OUT") && PrinterDataController.getInstance().getTakeoutPrinter() != null) {
                                    if (checkOutPrintCounts == 0 && store.getReceiveNetOrder()) {
                                        EventBus.getDefault().post(new PosEvent(Constant.EventState.NETORDER_ON));
                                    }
                                    for (int i = 0; i < checkOutPrintCounts; i++) {
                                        order.setPrinterType(Constant.EventState.PRINT_WAIMAI);
                                        PrinterDataController.getInstance().printGuestOrder(order, Constant.EventState.PRINT_CHECKOUT);
                                        threadSleep(3);
                                    }
                                } else {
                                    if (checkOutPrintCounts == 0 && store.getReceiveNetOrder()) {
                                        EventBus.getDefault().post(new PosEvent(Constant.EventState.NETORDER_ON));
                                    }
                                    for (int i = 0; i < checkOutPrintCounts; i++) {
                                        PrinterDataController.getInstance().printGuestOrder(order, Constant.EventState.PRINT_CHECKOUT);
                                        threadSleep(3);
                                    }
                                }
                            }

                        }
                    }
                }
            }).start();//启动打印线程
        }
    }

    /**
     * 打印厨房小票
     *
     * @param order
     */
    public static void printKitChenOrder(final Order order) {
        if (order != null) {
            if (!ToolsUtils.isList(order.getItemList())) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        threadSleep(2);//如果不这样的话Order就会重新赋值导致最终结果不是理想的,不信可以试试
                        Order tempOrder = DishDataController.initOrder2(order);//重新赋值order
                        ArrayMap<Integer, List<OrderItem>> summaryOiPrintMap = PrinterDataController.getInstance().printKitChenOrder(tempOrder, Constant.EventState.PRINTER_KITCHEN_ORDER);
                        if(summaryOiPrintMap != null && summaryOiPrintMap.size() >0)
                        {
                            PrinterDataController.getInstance().printOrderKitSummaryTicket(tempOrder, summaryOiPrintMap, Constant.EventState.PRINTER_KITCHEN_SUMMARY_ORDER);
                        }
                    }
                }).start();//启动打印线程
            }
        }
    }

    /**
     * 催菜厨房打印
     */
    public static void printKitChenRushDishOrder(final Order orderss) {
        final Order order = ToolsUtils.cloneTo(orderss);
        if (order != null) {
            if (!ToolsUtils.isList(order.getItemList())) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Order tempOrder = initOrder2(order);//重新赋值order
                        tempOrder.setTableStyle(Constant.EventState.PRINTER_EXTRA_KITCHEN_RECEIPT);
                        if (tempOrder.getRushDishType() == 1) {
                            PrinterDataController.getInstance().printKitChenOrder(tempOrder, Constant.EventState.PRINTER_EXTRA_KITCHEN_RECEIPT);
                        } else {
                            ArrayMap<Integer, List<OrderItem>> summaryOiPrintMap = PrinterDataController.getInstance().printKitChenOrder(tempOrder, Constant.EventState.PRINTER_EXTRA_KITCHEN_RECEIPT);
                            PrinterDataController.getInstance().printOrderKitSummaryTicket(tempOrder, summaryOiPrintMap, Constant.EventState.PRINTER_KITCHEN_SUMMARY_ORDER);
                        }
                    }
                }).start();//启动打印线程
            }
        }
    }

    /**
     * 厨房退菜打印
     */
    public static void printKitChenRetreatDishOrder(final Order order) {
        order.setTableStyle(Constant.EventState.PRINTER_RETREAT_ITEM_DISH);
        if (order != null) {
            if (!ToolsUtils.isList(order.getItemList())) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        threadSleep(3);//如果不这样的话Order就会重新赋值导致最终结果不是理想的,不信可以试试
                        Order tempOrder = initOrder2(order);//重新赋值order
                        ArrayMap<Integer, List<OrderItem>> summaryOiPrintMap = PrinterDataController.getInstance().printKitChenOrder(tempOrder, Constant.EventState.PRINTER_RETREAT_ITEM_DISH);
                        PrinterDataController.getInstance().printOrderKitSummaryTicket(tempOrder, summaryOiPrintMap, Constant.EventState.PRINTER_KITCHEN_SUMMARY_ORDER);
                    }
                }).start();//启动打印线程
            }
        }
    }

    /**
     * 客用退单厨房打印
     */
    public static void printGuestKitchenRetreatOrder(final Order order) {
        order.setTableStyle(Constant.EventState.PRINTER_RETREAT_DISH);
        if (order != null) {
            if (!ToolsUtils.isList(order.getItemList())) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        threadSleep(3);//如果不这样的话Order就会重新赋值导致最终结果不是理想的,不信可以试试
                        Order tempOrder = initOrder2(order);//重新赋值order
                        ArrayMap<Integer, List<OrderItem>> summaryOiPrintMap = PrinterDataController.getInstance().printKitChenOrder(tempOrder, Constant.EventState.PRINTER_RETREAT_DISH);
                        PrinterDataController.getInstance().printOrderKitSummaryTicket(tempOrder, summaryOiPrintMap, Constant.EventState.PRINTER_KITCHEN_SUMMARY_ORDER);
                    }
                }).start();//启动打印线程
            }
        }
    }

    /**
     * 客票退菜打印
     *
     * @param order
     * @param guestReceiptCounts
     */
    public static void printGuestRetreatDish(final Store store,final Order order, final int guestReceiptCounts, final int checkOutPrintCounts) {
        if (order != null) {
            if (!ToolsUtils.isList(order.getItemList())) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < 1; i++) {
                            order.setPrinterType(Constant.EventState.PRINTER_RETREAT_DISH_GUEST);
                            PrinterDataController.getInstance().printGuestOrder(order, Constant.EventState.PRINTER_ORDER);
                            threadSleep(3);
                        }
                    }
                }).start();//启动打印线程
            }
        }
    }

    /**
     * 客用退单打印
     *
     * @param order
     * @param guestReceiptCounts
     */
    public static void printGuestRetreatOrder(final Store store,final Order order, final int guestReceiptCounts, final int checkOutPrintCounts) {
        if (order != null) {
            if (!ToolsUtils.isList(order.getItemList())) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < 1; i++) {
                            order.setPrinterType(Constant.EventState.PRINTER_RETREAT_ORDER);
                            PrinterDataController.getInstance().printGuestOrder(order, Constant.EventState.PRINTER_ORDER);
                            threadSleep(3);
                        }
                    }
                }).start();//启动打印线程
            }
        }
    }

    /**
     * 补打下单标识
     *
     * @param order
     * @param guestReceiptCounts
     */
    public static void printExtraReceipt(final Store store,final Order order, final int guestReceiptCounts, final int checkOutPrintCounts) {
        if (order != null) {
            if (!ToolsUtils.isList(order.getItemList())) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < 1; i++) {
                            PrinterDataController.getInstance().printGuestOrder(order, Constant.EventState.PRINTER_ORDER);
                            threadSleep(3);
                        }

                    }
                }).start();//启动打印线程
            }
        }
    }

    /**
     * 测试打印
     *
     * @param printer
     */
    public static void testPrint(final Printer printer) {
        if (printer != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    PrinterDataController.getInstance().printerTest(printer);
                }
            }).start();//启动打印线程
        }
    }

    public static void testPrintKds(KDS kds) {
        if (kds != null) {
            try {
                OrderService kdsOrderService = OrderService.getKdsInstance(kds);
                KdsOrderData kdsOrderData = new KdsOrderData();
                List<KdsDishItem> kdsDishItemList = new ArrayList<>();

                kdsOrderData.oid = "-" + TimeUtil.getDateStr();
                kdsOrderData.createTime = System.currentTimeMillis();
                if (StoreInfor.cardNumberMode) {
                    kdsOrderData.fetchID = "测试桌台";
                    kdsOrderData.tablename = "测试桌台";
                } else {
                    kdsOrderData.fetchID = "999";
                }
                int type = 0;
                kdsOrderData.type = type;
                kdsOrderData.total = "100";
                int paystatus = 0;
                kdsOrderData.paystatus = paystatus;

                KdsDishItem kdsItem = new KdsDishItem();
                kdsItem.did = "1";
                kdsItem.name = "测试菜品";
                kdsItem.count = 1;
                kdsItem.dishKind = "1";
                kdsItem.seq = "366-99";
                kdsItem.cook = "测试定制项";
                kdsItem.price = "100";
                kdsDishItemList.add(kdsItem);
                kdsOrderData.dishitems = kdsDishItemList;

                kdsOrderService.kdsCreatOrder(new Gson().toJson(kdsOrderData), new ResultCallback<KDSResponse>() {
                    @Override
                    public void onResult(KDSResponse results) {
                        Log.i("KDS下单返回数据", ToolsUtils.getPrinterSth(results) + "");
                        if (results.isSuccess()) {
                            MyApplication.getInstance().ShowToast(ToolsUtils.returnXMLStr("kds_create_order_success"));
                        } else {
                            Log.i("KDS下单失败", results.getMsg());
                            MyApplication.getInstance().ShowToast(results.getMsg());
                        }
                    }

                    @Override
                    public void onError(PosServiceException e) {
                        MyApplication.getInstance().ShowToast(e.getMessage());
                    }
                });
            } catch (PosServiceException e) {
                e.printStackTrace();
            }
        }
    }


    public static void kdsChangeOrderTable(final long netOrderId, final String newTableName) {
        List<KDS> kdsList = PrinterDataController.getKdsList();
        if (kdsList != null && kdsList.size() > 0) {
            int size = kdsList.size();
            for (int i = 0; i < size; i++) {
                KDS kds = kdsList.get(i);
                try {
                    OrderService orderService = OrderService.getKdsInstance(kds);
                    orderService.kdsChangeOrderTable(netOrderId, newTableName, new ResultCallback<Boolean>() {
                        @Override
                        public void onResult(Boolean result) {
                            if (result) {
                                MyApplication.getInstance().ShowToast(ToolsUtils.returnXMLStr("kds_change_table_success"));
                            } else {
                                MyApplication.getInstance().ShowToast(ToolsUtils.returnXMLStr("kds_change_table_error"));
                                KDSOrderFailure(netOrderId, newTableName);
                            }
                        }

                        @Override
                        public void onError(PosServiceException e) {
                            MyApplication.getInstance().ShowToast(ToolsUtils.returnXMLStr("kds_change_table_error")+"," + e.getMessage());
                            Log.i("通知KDS换台失败", e.getMessage());
                            KDSOrderFailure(netOrderId, newTableName);
                        }
                    });
                } catch (PosServiceException e) {
                    e.printStackTrace();
                    Log.i("KDS退单失败", e.getMessage());
                    KDSOrderFailure(netOrderId, newTableName);
                }
            }
        }
    }


    /**
     * KDS创建订单
     */
    public static void kdsCreatOrder(final Order result, final String tableName) {
        try {
            if (result != null) {
                Store store = Store.getInstance(MyApplication.getInstance().getContext());
                //        progressDialog.showLoading("正在下单");
                List<KDS> kdsList = PrinterDataController.getKdsList();
                if (kdsList != null && kdsList.size() > 0) {
                    int size = kdsList.size();
                    for (int i = 0; i < size; i++) {
                        KDS kds = kdsList.get(i);
                        if ((int) kds.getId() == store.getCashKdsId()) {
                            String kdsServer = kds.getIp();
                            String kdsPort = TextUtils.isEmpty(kds.getPort()) ? "8080" : kds.getPort();

                            Log.i("KDS下单订单ID", result.getId() + "");
                            MyApplication.getInstance().ShowToast(ToolsUtils.returnXMLStr("kds_is_placing_an_order"));
                            Log.i("KDS信息", kdsServer + "========" + kdsPort);
                            if (!TextUtils.isEmpty(kdsServer) && !TextUtils.isEmpty(kdsPort)) {
                                try {
                                    OrderService kdsOrderService = OrderService.getKdsInstance(kds);
                                    KdsOrderData kdsOrderData = new KdsOrderData();
                                    List<KdsDishItem> kdsDishItemList = new ArrayList<>();

                                    kdsOrderData.oid = result.getId() + "";
                                    kdsOrderData.createTime = result.getCreatedAt();
                                    kdsOrderData.comment = result.getComment();
                                    kdsOrderData.thirdPlatformOrderId = result.getThirdPlatformOrderId();
                                    kdsOrderData.thirdPlatformOrderIdView = result.getThirdPlatformOrderIdView();
                                    kdsOrderData.thirdPlatfromOrderIdDaySeq = result.getThirdPlatfromOrderIdDaySeq();
                                    kdsOrderData.source = result.getSource();
                                    if (StoreInfor.cardNumberMode) {
                                        kdsOrderData.fetchID = tableName;
                                        kdsOrderData.tablename = tableName;
                                    } else {
                                        kdsOrderData.fetchID = result.getCallNumber();
                                    }
                                    int type = 0;
                                    if ("EAT_IN".equals(result.getOrderType())) {//堂食
                                        type = 0;
                                    } else if ("TAKE_OUT".equals(result.getOrderType())) {//打包外带
                                        type = 1;
                                    } else {//外卖
                                        type = 2;
                                    }
                                    kdsOrderData.type = type;
                                    kdsOrderData.total = result.getTotal();
                                    int paystatus = 0;
                                    if (result.getPaymentStatus() == PaymentStatus.NOT_PAYED) {//未付款
                                        paystatus = 0;
                                    } else if (result.getPaymentStatus() == PaymentStatus.PAYED) {//已付款
                                        paystatus = 1;
                                    }
                                    kdsOrderData.paystatus = paystatus;

                                    //菜品项
                                    for (OrderItem orderItem : result.getItemList()) {
                                        if (orderItem.subItemList != null && orderItem.subItemList.size() > 0) {
                                            for (Dish.Package dish : orderItem.subItemList) {
                                                KdsDishItem kdsItem = new KdsDishItem();
                                                kdsItem.did = dish.getDishId() + "";
                                                kdsItem.name = dish.getDishName();
                                                kdsItem.comment = dish.comment;
                                                kdsItem.count = dish.quantity * orderItem.getQuantity();
                                                kdsItem.dishKind = dish.dishId + "";
                                                kdsItem.seq = dish.sku;
                                                if (dish.optionList != null && dish.optionList.size() > 0) {
                                                    StringBuffer sb = new StringBuffer();
                                                    for (Option option : dish.optionList) {
                                                        sb.append(option.name + ",");
                                                    }
                                                    kdsItem.cook = sb.toString();
                                                }
                                                kdsItem.price = "0";
                                                kdsDishItemList.add(kdsItem);
                                                kdsOrderData.dishitems = kdsDishItemList;
                                            }
                                        } else {
                                            KdsDishItem kdsItem = new KdsDishItem();
                                            kdsItem.did = orderItem.getDishId() + "";
                                            kdsItem.name = orderItem.getDishName();
                                            kdsItem.count = orderItem.getQuantity();
                                            kdsItem.comment = orderItem.getComment();
                                            kdsItem.dishKind = orderItem.getDishId() + "";
                                            kdsItem.seq = orderItem.getSku();
                                            if (orderItem.optionList != null && orderItem.optionList.size() > 0) {
                                                StringBuffer sb = new StringBuffer();
                                                for (Option option : orderItem.optionList) {
                                                    sb.append(option.name + ",");
                                                }
                                                kdsItem.cook = sb.toString();
                                            }
                                            kdsItem.price = orderItem.getPrice().toString();
                                            kdsDishItemList.add(kdsItem);
                                            kdsOrderData.dishitems = kdsDishItemList;
                                        }
                                    }
                                    //kdsOrder.orderdata = kdsOrderData;
                                    kdsOrderService.kdsCreatOrder(new Gson().toJson(kdsOrderData), new ResultCallback<KDSResponse>() {
                                        @Override
                                        public void onResult(KDSResponse results) {
                                            Log.i("KDS下单返回数据", ToolsUtils.getPrinterSth(results) + "");
                                            //progressDialog.disLoading();
                                            if (results.isSuccess()) {
                                                MyApplication.getInstance().ShowToast(ToolsUtils.returnXMLStr("kds_create_order_success"));
                                            } else {
                                                Log.i("KDS下单失败", results.getMsg());
                                                MyApplication.getInstance().ShowToast(results.getMsg());
                                                KDSOrderFailure(result, tableName);
                                            }
                                        }

                                        @Override
                                        public void onError(PosServiceException e) {
                                            Log.i("KDS下单失败", e.getMessage());
                                            //progressDialog.disLoading();
                                            KDSOrderFailure(result, tableName);
                                            MyApplication.getInstance().ShowToast(e.getMessage());
                                        }
                                    });

                                } catch (PosServiceException e) {
                                    e.printStackTrace();
                                    Log.i("KDS下单失败", e.getMessage());
                                    KDSOrderFailure(result, tableName);
                                }
                            }
                        }
                    }
                } else {
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * KDS退单
     *
     * @param orderId
     */
    public static void kdsDeleteOrder(final String orderId) {
        List<KDS> kdsList = PrinterDataController.getKdsList();
        Store store = Store.getInstance(MyApplication.getInstance().getContext());
        if (kdsList != null && kdsList.size() > 0) {
            int size = kdsList.size();
            for (int i = 0; i < size; i++) {
                KDS kds = kdsList.get(i);
                if ((int) kds.getId() == store.getCashKdsId()) {
                    try {
                        OrderService orderService = OrderService.getKdsInstance(kds);
                        orderService.kdsDeleteOrder(orderId, new ResultCallback<Boolean>() {
                            @Override
                            public void onResult(Boolean result) {
                                if (result) {
                                    MyApplication.getInstance().ShowToast(ToolsUtils.returnXMLStr("kds_retreat_order_the_success"));
                                } else {
                                    MyApplication.getInstance().ShowToast(ToolsUtils.returnXMLStr("kds_retreat_order_the_error"));
                                    KDSOrderFailure(orderId, null, null);
                                }
                            }

                            @Override
                            public void onError(PosServiceException e) {
                                MyApplication.getInstance().ShowToast(ToolsUtils.returnXMLStr("kds_retreat_order_the_error")+"," + e.getMessage());
                                Log.i("KDS退单失败", e.getMessage());
                                KDSOrderFailure(orderId, null, null);
                            }
                        });
                    } catch (PosServiceException e) {
                        e.printStackTrace();
                        Log.i("KDS退单失败", e.getMessage());
                        KDSOrderFailure(orderId, null, null);
                    }
                }
            }
        }
    }


    /**
     * Kds退菜
     *
     * @param orderItemList
     */
    public static void kdsDeleteDish(final List<OrderItem> orderItemList, final String orderId) {
        KdsOrderData kdsOrderData = new KdsOrderData();
        kdsOrderData.deletedishes = new ArrayList<>();
        for (OrderItem item : orderItemList) {
            if (item.getRejectedQuantity() > 0) {
                if (item.subItemList != null && item.subItemList.size() > 0) {
                    for (Dish.Package dish : item.subItemList) {
                        KdsDishItem kdsItem = new KdsDishItem();
                        kdsItem.seq = dish.sku;
                        kdsItem.oid = orderId;
                        kdsItem.did = String.valueOf(dish.getDishId());
                        kdsItem.deletecount = item.getRejectedQuantity() * dish.quantity;
                        kdsOrderData.deletedishes.add(kdsItem);
                    }
                } else {
                    KdsDishItem kdsItem = new KdsDishItem();
                    kdsItem.oid = orderId;
                    kdsItem.did = String.valueOf(item.getDishId());
                    kdsItem.seq = item.getSku();
                    kdsItem.deletecount = item.getRejectedQuantity();
                    if (item.subItemList != null && item.subItemList.size() > 0) {
                        kdsItem.price = "0";
                    } else {
                        kdsItem.price = item.getPrice().toString();
                    }
                    kdsOrderData.deletedishes.add(kdsItem);
                }
            }
        }
        //        System.out.println(ToolsUtils.getPrinterSth(kdsOrderData.deletedishes));

        Store store = Store.getInstance(MyApplication.getInstance().getContext());
        List<KDS> kdsList = PrinterDataController.getKdsList();
        if (kdsList != null && kdsList.size() > 0) {
            int size = kdsList.size();
            for (int i = 0; i < size; i++) {
                KDS kds = kdsList.get(i);
                if ((int) kds.getId() == store.getCashKdsId()) {
                    try {
                        OrderService orderService = OrderService.getKdsInstance(kds);
                        orderService.kdsDeleteDish(ToolsUtils.getPrinterSth(kdsOrderData.deletedishes), new ResultCallback<Boolean>() {
                            @Override
                            public void onResult(Boolean result) {
                                if (result) {
                                    MyApplication.getInstance().ShowToast(ToolsUtils.returnXMLStr("kds_retreat_dish_the_success"));
                                } else {
                                    MyApplication.getInstance().ShowToast(ToolsUtils.returnXMLStr("kds_retreat_dish_the_error"));
                                    KDSOrderFailure(null, orderItemList, orderId);
                                }
                            }

                            @Override
                            public void onError(PosServiceException e) {
                                MyApplication.getInstance().ShowToast(ToolsUtils.returnXMLStr("kds_retreat_dish_the_error")+"," + e.getMessage());
                                Log.i("KDS退菜失败", e.getMessage());
                                KDSOrderFailure(null, orderItemList, orderId);
                            }
                        });
                    } catch (PosServiceException e) {
                        e.printStackTrace();
                        MyApplication.getInstance().ShowToast(ToolsUtils.returnXMLStr("kds_retreat_dish_the_error")+"," + e.getMessage());
                        Log.i("KDS退菜失败", e.getMessage());
                        KDSOrderFailure(null, orderItemList, orderId);
                    }
                }
            }
        }
    }

    /**
     * KDS连接失败的操作
     */
    private static void KDSOrderFailure(final long netOrderId, final String newTableName) {
//        DialogUtil.ordinaryDialog(MyApplication.getInstance().getContext(), ToolsUtils.returnXMLStr("kds_connect_error"), ToolsUtils.returnXMLStr("please_re_switch_KDS"), new DialogCallback() {
//            @Override
//            public void onConfirm() {
//                kdsChangeOrderTable(netOrderId, newTableName);
//            }
//
//            @Override
//            public void onCancle() {
//            }
//        });
    }


    /**
     * KDS连接失败的操作
     */
    private static void KDSOrderFailure(final Order order, final String tableName) {
//        DialogUtil.ordinaryDialog(MyApplication.getInstance().getContext(), ToolsUtils.returnXMLStr("kds_connect_error"), ToolsUtils.returnXMLStr("please_re_switch_KDS"), new DialogCallback() {
//            @Override
//            public void onConfirm() {
//                kdsCreatOrder(order, tableName);
//            }
//
//            @Override
//            public void onCancle() {
//            }
//        });


        //        final AlertDialog[] alertDialog = {null};
        //        Looper.prepare();
        //        if (alertDialog[0] == null) {
        //            alertDialog[0] = DialogUtil.LoginErrorDialog(MyApplication.getInstance().getContext(), "KDS连接失败", "请重新开关KDS后,选择“是”尝试重新连接KDS.", new DialogCallback() {
        //                @Override
        //                public void onConfirm() {
        //                    kdsCreatOrder(order, tableName);
        //                    alertDialog[0] = null;
        //                }
        //
        //                @Override
        //                public void onCancle() {
        //                    alertDialog[0] = null;
        //                }
        //            });
        //        }
        //        Looper.loop();
    }

    /**
     * KDS连接失败的操作
     */
    public static void KDSOrderFailure(final String orderId, final List<OrderItem> orderItemList, final String oid) {
//        DialogUtil.ordinaryDialog(MyApplication.getInstance().getContext(), ToolsUtils.returnXMLStr("kds_connect_error"), ToolsUtils.returnXMLStr("please_re_switch_KDS"), new DialogCallback() {
//            @Override
//            public void onConfirm() {
//                if (!TextUtils.isEmpty(orderId)) {
//                    kdsDeleteOrder(orderId);
//                } else {
//                    if (orderItemList != null && orderItemList.size() > 0) {
//                        kdsDeleteDish(orderItemList, oid);
//                    }
//                }
//            }
//
//            @Override
//            public void onCancle() {
//            }
//        });



        //        final AlertDialog[] alertDialog = {null};
        ////        Looper.prepare();
        //        if (alertDialog[0] == null) {
        //            alertDialog[0] = DialogUtil.LoginErrorDialog(MyApplication.getInstance().getContext(), "KDS连接失败", "请重新开关KDS后,选择“是”尝试重新连接KDS.", new DialogCallback() {
        //                @Override
        //                public void onConfirm() {
        //                    if (!TextUtils.isEmpty(orderId)) {
        //                        kdsDeleteOrder(orderId);
        //                    } else {
        //                        if (orderItemList != null && orderItemList.size() > 0) {
        //                            kdsDeleteDish(orderItemList, oid);
        //                        }
        //                    }
        //                    alertDialog[0] = null;
        //                }
        //
        //                @Override
        //                public void onCancle() {
        //                    alertDialog[0] = null;
        //                }
        //            });
        //        }
        //        Looper.loop();
    }


    private static void threadSleep(long time) {
        try {
            Thread.sleep(time * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
