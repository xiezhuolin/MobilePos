package cn.acewill.mobile.pos.printer.usb;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import com.gprinter.aidl.GpService;
import com.gprinter.command.EscCommand;
import com.gprinter.command.GpCom;
import com.gprinter.command.TscCommand;
import com.gprinter.io.PortParameters;
import com.gprinter.service.GpPrintService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import cn.acewill.mobile.pos.common.PrinterController;
import cn.acewill.mobile.pos.common.StoreInfor;
import cn.acewill.mobile.pos.config.Store;
import cn.acewill.mobile.pos.model.MarketObject;
import cn.acewill.mobile.pos.model.PaymentList;
import cn.acewill.mobile.pos.model.WorkShiftNewReport;
import cn.acewill.mobile.pos.model.WorkShiftReport;
import cn.acewill.mobile.pos.model.dish.Dish;
import cn.acewill.mobile.pos.model.dish.Option;
import cn.acewill.mobile.pos.model.order.Order;
import cn.acewill.mobile.pos.model.order.OrderItem;
import cn.acewill.mobile.pos.model.payment.Payment;
import cn.acewill.mobile.pos.printer.PrintModelInfo;
import cn.acewill.mobile.pos.service.PosInfo;
import cn.acewill.mobile.pos.utils.Constant;
import cn.acewill.mobile.pos.utils.CreateImage;
import cn.acewill.mobile.pos.utils.PrintUtils;
import cn.acewill.mobile.pos.utils.TimeUtil;
import cn.acewill.mobile.pos.utils.ToolsUtils;

import static cn.acewill.mobile.pos.utils.TimeUtil.getTimeStr;


/**
 * 佳博USB标签打印机
 * 调用流程：
 * 1. 先调用  GpUsbPrinter.listGpUsbPrinterList 来获得佳博usb打印机列表
 * 2. 然后把其中的某个usb设备的名字传递给GpUsbPrinter 构造函数, 然后调用init函数
 * 3. 然后调用 printOrderItem来打印内容
 * Created by Acewill on 2016/12/20.
 */

public class GpUsbPrinter {
    public static final String ACTION_CONNECT_STATUS = "action.connect.status";

    private String TAG = "GpUsbPrinter";


    private Integer labelHeight; //标签打印纸的高度
    private String usbDeviceName;
    private Context context;
    private GpService mGpService;
    private Integer printerId;
    private PrinterServiceConnection conn;
    private int bigSize = 2;
    private int scaleHeight = 2;
    private int small = 1;

    public Integer getPrinterId() {
        return printerId;
    }

    public void setPrinterId(Integer printerId) {
        this.printerId = printerId;
    }

    public GpUsbPrinter(Context context, String usbDeviceName) {
        this.usbDeviceName = usbDeviceName;
        this.context = context;
    }

    //绑定佳博打印机服务
    public void init() {
        bindService();
    }


    //找到设备上的所有的佳博USB打印机
    public static List<String> listGpUsbPrinterList(Context context) {
        UsbManager manager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
        // Get the list of attached devices
        HashMap<String, UsbDevice> devices = manager.getDeviceList();
        Iterator<UsbDevice> deviceIterator = devices.values().iterator();
        int count = devices.size();
        Log.d("", "found " + count + " usb devices");

        List<String> usbPrinterNameList = new ArrayList<>();

        if (count > 0) {
            while (deviceIterator.hasNext()) {
                UsbDevice device = deviceIterator.next();
                String devicename = device.getDeviceName();
                if (checkUsbDevicePidVid(device)) {
                    Log.d("", "found GP printer on USB: " + devicename);
                    usbPrinterNameList.add(devicename);
                    break;
                }
            }
        } else {
            Log.d("", "no USB Devices found");
        }

        return usbPrinterNameList;
    }

    //检查是否为佳博的打印机
    private static boolean checkUsbDevicePidVid(UsbDevice dev) {
        int pid = dev.getProductId();
        int vid = dev.getVendorId();
        boolean rel = false;
        if ((vid == 34918 && pid == 256) || (vid == 1137 && pid == 85)
                || (vid == 6790 && pid == 30084)
                || (vid == 26728 && pid == 256) || (vid == 26728 && pid == 512)
                || (vid == 26728 && pid == 256) || (vid == 26728 && pid == 768)
                || (vid == 26728 && pid == 1024) || (vid == 26728 && pid == 1280)
                || (vid == 26728 && pid == 1536)) {
            rel = true;
        }
        return rel;
    }

    private class PrinterServiceConnection implements ServiceConnection {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i(TAG, "onServiceDisconnected() called");
            mGpService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mGpService = GpService.Stub.asInterface(service);
            //绑定后打开打印机
            openUsbPrinter(usbDeviceName);
        }
    }

    private void bindService() {
        conn = new PrinterServiceConnection();
        Log.i(TAG, "connection");
        //        Intent intent = new Intent("com.gprinter.aidl.GpPrintService");
        Intent intent = new Intent(context, GpPrintService.class);
        context.bindService(intent, conn, Context.BIND_AUTO_CREATE);
    }

    public void unbindService() {
        if (conn != null) {
            context.unbindService(conn);
        }
    }

    private void openUsbPrinter(String usbDeviceName) {
        int rel = 0;
        try {
            rel = mGpService.openPort(0, PortParameters.USB, usbDeviceName, 0);
            GpCom.ERROR_CODE r = GpCom.ERROR_CODE.values()[rel];
            if (r != GpCom.ERROR_CODE.SUCCESS) {
                if (r == GpCom.ERROR_CODE.DEVICE_ALREADY_OPEN) {
                    Log.i("INFO", "usb device: " + usbDeviceName + " already open");
                } else {
                    Log.e("ERROR", "failed to open usb device: " + usbDeviceName);
                }

            } else {
                Log.i("SUCCESS", "success to open usb device: " + usbDeviceName);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }


    //每一份都打印一张标签
    public void printItem(Order order, OrderItem orderItem) {
        printSingleItem(order, orderItem);
    }

    /**
     * 打印换行
     *
     * @param esc
     */
    private void printEnter(EscCommand esc) {
        if (esc != null) {
            esc.addText("\n");
        }
    }

    /**
     * 设置字体对齐方式
     * EscCommand.JUSTIFICATION.CENTER 居中
     * EscCommand.JUSTIFICATION.LEFT 左对齐
     * EscCommand.JUSTIFICATION.RIGHT 右对齐
     *
     * @param esc
     */
    private void printAlign(EscCommand esc, EscCommand.JUSTIFICATION just) {
        if (esc != null) {
            esc.addSelectJustification(just);
        }
    }

    /**
     * 设置倍高倍宽   doubleheight true-倍高  doublewidth true-倍宽  false反之
     *
     * @param esc
     */
    private void printMode(EscCommand esc, boolean doubleheight, boolean doublewidth) {
        EscCommand.ENABLE doubleheightENABLE = null;
        EscCommand.ENABLE doublewidthENABLE = null;
        if (esc != null) {
            if (doubleheight == true) {
                doubleheightENABLE = EscCommand.ENABLE.ON;
            } else {
                doubleheightENABLE = EscCommand.ENABLE.OFF;
            }
            if (doublewidth == true) {
                doublewidthENABLE = EscCommand.ENABLE.ON;
            } else {
                doublewidthENABLE = EscCommand.ENABLE.OFF;
            }
            esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF, doubleheightENABLE, doublewidthENABLE, EscCommand.ENABLE.OFF);
        }
    }

    /**
     * 打印文字
     *
     * @param esc
     * @param text
     */
    private void printText(EscCommand esc, String text) {
        if (esc != null) {
            esc.addText(text);
        }
    }

    /**
     * 打印分隔符
     *
     * @param esc
     * @param separator
     */
    private void printSeparator(EscCommand esc, String separator) {
        if (esc != null) {
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < 32; i++) {
                sb.append(separator);
            }
            printMode(esc, false, false);//要先将倍高倍宽先关闭
            esc.addText(sb.toString());
        }
    }

    public void openUSBCashBox() {
        if (mGpService == null) {
            Log.e("", "USB printer not ready yet");
            return;
        }
        EscCommand esc = new EscCommand();
        esc.addInitializePrinter();
        // 开钱箱
        esc.addGeneratePlus(TscCommand.FOOT.F5, (byte) 255, (byte) 255);
//        esc.addPrintAndFeedLines((byte) 8);

        Vector<Byte> datas = esc.getCommand(); // 发送数据
        byte[] bytes = ToolsUtils.ByteTo_byte(datas);
        String sss = Base64.encodeToString(bytes, Base64.DEFAULT);
        int rs;
        try {
            rs = mGpService.sendEscCommand(0, sss);
            GpCom.ERROR_CODE r = GpCom.ERROR_CODE.values()[rs];
            if (r != GpCom.ERROR_CODE.SUCCESS) {
                Log.e("ERROR", "failed to print text " + GpCom.getErrorText(r));
            }
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void printWorkShiftNew(WorkShiftNewReport workShiftReport, String printStr) {
        if (mGpService == null) {
            Log.e("", "USB printer not ready yet");
            return;
        }
        EscCommand esc = new EscCommand();
        PosInfo posInfo = PosInfo.getInstance();
        esc.addInitializePrinter();
        esc.addPrintAndLineFeed();

        printAlign(esc, EscCommand.JUSTIFICATION.CENTER);// 设置打印居中
        printMode(esc, false, false);// 设置为倍高倍宽
        printText(esc, posInfo.getBrandName()); // 打印文字
        printEnter(esc);

        printAlign(esc, EscCommand.JUSTIFICATION.CENTER);// 设置打印居中
        printMode(esc, false, false);// 设置为倍高倍宽
        printText(esc, printStr + "打印单"); // 打印文字
        printEnter(esc);

        printAlign(esc, EscCommand.JUSTIFICATION.CENTER);// 设置打印居中
        printMode(esc, false, false);// 设置为倍高倍宽
        printText(esc, printStr + "人 : " + posInfo.getRealname()); // 打印文字
        printEnter(esc);

        if (!TextUtils.isEmpty(workShiftReport.getWorkShiftName())) {
            printAlign(esc, EscCommand.JUSTIFICATION.LEFT);// 设置打印居中
            printMode(esc, false, false);// 设置为倍高倍宽
            printText(esc, printStr + "班次名称 : " + workShiftReport.getWorkShiftName()); // 打印文字
            printEnter(esc);
        }

        if (!TextUtils.isEmpty(workShiftReport.getStartTime())) {
            printAlign(esc, EscCommand.JUSTIFICATION.LEFT);// 设置打印居中
            printMode(esc, false, false);// 设置为倍高倍宽
            printText(esc,printStr + "开始时间 : " + workShiftReport.getStartTime()); // 打印文字
            printEnter(esc);
        }

        if (!TextUtils.isEmpty(workShiftReport.getStartTime())) {
            printAlign(esc, EscCommand.JUSTIFICATION.LEFT);// 设置打印居中
            printMode(esc, false, false);// 设置为倍高倍宽
            printText(esc,printStr + "结束时间 : " + workShiftReport.getEndTime()); // 打印文字
            printEnter(esc);
        }

        if (workShiftReport.getStartWorkShiftCash() > 0) {
            printAlign(esc, EscCommand.JUSTIFICATION.LEFT);// 设置打印居中
            printMode(esc, false, false);// 设置为倍高倍宽
            printText(esc,"开班钱箱余额 : " + workShiftReport.getStartWorkShiftCash()); // 打印文字
            printEnter(esc);
        }

        if (workShiftReport.getEndWorkShiftCash() > 0) {
            printAlign(esc, EscCommand.JUSTIFICATION.LEFT);// 设置打印居中
            printMode(esc, false, false);// 设置为倍高倍宽
            printText(esc,"交班钱箱余额 : " + workShiftReport.getEndWorkShiftCash()); // 打印文字
            printEnter(esc);
        }

        printAlign(esc, EscCommand.JUSTIFICATION.LEFT);// 设置打印居中
        printMode(esc, false, false);// 设置为倍高倍宽
        printText(esc,"小票打印时间 : " + getTimeStr(System.currentTimeMillis())); // 打印文字
        printEnter(esc);

        createWorkShiftNewItem(esc,workShiftReport);

        String submitCash = PrintUtils.getStr("应交现金:", "", String.valueOf(workShiftReport.getSubmitCash() + " / 元"), 32);
        printAlign(esc, EscCommand.JUSTIFICATION.LEFT);// 设置打印居中
        printMode(esc, false, false);// 设置为倍高倍宽
        printText(esc,submitCash); // 打印文字
        printEnter(esc);

        String differenceCash = PrintUtils.getStr("差额:", "", String.valueOf(workShiftReport.getDifferenceCash() + " / 元"), 32);
        printAlign(esc, EscCommand.JUSTIFICATION.LEFT);// 设置打印居中
        printMode(esc, false, false);// 设置为倍高倍宽
        printText(esc,differenceCash); // 打印文字
        printEnter(esc);

        printSeparator(esc, " ");
        printSeparator(esc, " ");
        printSeparator(esc, " ");

        Vector<Byte> datas = esc.getCommand(); // 发送数据
        byte[] bytes = ToolsUtils.ByteTo_byte(datas);
        String sss = Base64.encodeToString(bytes, Base64.DEFAULT);
        int rs;
        try {
            rs = mGpService.sendEscCommand(0, sss);
            GpCom.ERROR_CODE r = GpCom.ERROR_CODE.values()[rs];
            if (r != GpCom.ERROR_CODE.SUCCESS) {
                Log.e("ERROR", "failed to print text " + GpCom.getErrorText(r));
            }
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    private void createWorkShiftNewItem(EscCommand esc, WorkShiftNewReport workShiftReport) {
        try {
            for (WorkShiftNewReport.WorkShiftCategoryDataList itemCategorySalesDataList : workShiftReport.getWorkShiftCategoryDataList()) {
                printSeparator(esc, "-");
                printAlign(esc, EscCommand.JUSTIFICATION.CENTER);// 设置打印居中
                printMode(esc, false, false);// 设置为倍高倍宽
                printText(esc,returnStr(itemCategorySalesDataList.getName())); // 打印文字
                printEnter(esc);
                printSeparator(esc, "-");

                String dishTitle = PrintUtils.getStr("名称", "数量", "金额", 32);
                printAlign(esc, EscCommand.JUSTIFICATION.LEFT);// 设置打印居中
                printMode(esc, false, false);// 设置为倍高倍宽
                printText(esc, dishTitle); // 打印文字
                printEnter(esc);
                printSeparator(esc, "-");

                for (WorkShiftNewReport.WorkShiftCategoryDataList.WorkShiftItemDatas itemSalesDataList : itemCategorySalesDataList.getWorkShiftItemDatas()) {
                    String itemInfo = PrintUtils.getStr(returnStr(itemSalesDataList.getName()), returnStr(String.valueOf(itemSalesDataList.getItemCounts())), returnStr(String.valueOf(itemSalesDataList.getTotal())), 28);
                    printAlign(esc, EscCommand.JUSTIFICATION.LEFT);// 设置打印居中
                    printMode(esc, false, false);// 设置为倍高倍宽
                    printText(esc, itemInfo); // 打印文字
                    printEnter(esc);
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }



    public void printWorkShift(WorkShiftReport workShiftReport, String printStr) {
        if (mGpService == null) {
            Log.e("", "USB printer not ready yet");
            return;
        }
        EscCommand esc = new EscCommand();
        PosInfo posInfo = PosInfo.getInstance();
        esc.addInitializePrinter();
        esc.addPrintAndLineFeed();

        printAlign(esc, EscCommand.JUSTIFICATION.CENTER);// 设置打印居中
        printMode(esc, false, false);// 设置为倍高倍宽
        printText(esc, posInfo.getBrandName()); // 打印文字
        printEnter(esc);

        printAlign(esc, EscCommand.JUSTIFICATION.CENTER);// 设置打印居中
        printMode(esc, false, false);// 设置为倍高倍宽
        printText(esc, printStr + "打印单"); // 打印文字
        printEnter(esc);

        printAlign(esc, EscCommand.JUSTIFICATION.CENTER);// 设置打印居中
        printMode(esc, false, false);// 设置为倍高倍宽
        printText(esc, printStr + "人 : " + posInfo.getRealname()); // 打印文字
        printEnter(esc);

        if (!TextUtils.isEmpty(workShiftReport.getStartTime())) {
            printAlign(esc, EscCommand.JUSTIFICATION.LEFT);// 设置打印居中
            printMode(esc, false, false);// 设置为倍高倍宽
            printText(esc,printStr + "开始时间 : " + workShiftReport.getStartTime()); // 打印文字
            printEnter(esc);
        }

        if (!TextUtils.isEmpty(workShiftReport.getStartTime())) {
            printAlign(esc, EscCommand.JUSTIFICATION.LEFT);// 设置打印居中
            printMode(esc, false, false);// 设置为倍高倍宽
            printText(esc,printStr + "结束时间 : " + workShiftReport.getEndTime()); // 打印文字
            printEnter(esc);
        }

        printAlign(esc, EscCommand.JUSTIFICATION.LEFT);// 设置打印居中
        printMode(esc, false, false);// 设置为倍高倍宽
        printText(esc,"小票打印时间 : " + getTimeStr(System.currentTimeMillis())); // 打印文字
        printEnter(esc);

        createWorkShiftItem(esc,workShiftReport);

        printSeparator(esc, " ");
        printSeparator(esc, " ");
        printSeparator(esc, " ");

        Vector<Byte> datas = esc.getCommand(); // 发送数据
        byte[] bytes = ToolsUtils.ByteTo_byte(datas);
        String sss = Base64.encodeToString(bytes, Base64.DEFAULT);
        int rs;
        try {
            rs = mGpService.sendEscCommand(0, sss);
            GpCom.ERROR_CODE r = GpCom.ERROR_CODE.values()[rs];
            if (r != GpCom.ERROR_CODE.SUCCESS) {
                Log.e("ERROR", "failed to print text " + GpCom.getErrorText(r));
            }
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void createWorkShiftItem(EscCommand esc, WorkShiftReport workShiftReport) {
        try {
            for (WorkShiftReport.ItemCategorySalesDataList itemCategorySalesDataList : workShiftReport.getItemCategorySalesDataList()) {
                printSeparator(esc, "-");
                printAlign(esc, EscCommand.JUSTIFICATION.CENTER);// 设置打印居中
                printMode(esc, false, false);// 设置为倍高倍宽
                printText(esc,returnStr(itemCategorySalesDataList.getName())); // 打印文字
                printEnter(esc);
                printSeparator(esc, "-");

                String dishTitle = PrintUtils.getStr("名称", "数量", "金额", 32);
                printAlign(esc, EscCommand.JUSTIFICATION.LEFT);// 设置打印居中
                printMode(esc, false, false);// 设置为倍高倍宽
                printText(esc, dishTitle); // 打印文字
                printEnter(esc);
                printSeparator(esc, "-");

                for (WorkShiftReport.ItemCategorySalesDataList.ItemSalesDataList itemSalesDataList : itemCategorySalesDataList.getItemSalesDataList()) {
                    String itemInfo = PrintUtils.getStr(returnStr(itemSalesDataList.getName()),returnStr(String.valueOf(itemSalesDataList.getItemCounts())) , returnStr(String.valueOf(itemSalesDataList.getTotal())), 28);
                    printAlign(esc, EscCommand.JUSTIFICATION.LEFT);// 设置打印居中
                    printMode(esc, false, false);// 设置为倍高倍宽
                    printText(esc, itemInfo); // 打印文字
                    printEnter(esc);
                }
            }

            printSeparator(esc, "-");
            printAlign(esc, EscCommand.JUSTIFICATION.CENTER);// 设置打印居中
            printMode(esc, false, false);// 设置为倍高倍宽
            printText(esc,"客单价统计"); // 打印文字
            printEnter(esc);
            printSeparator(esc, "-");

            WorkShiftReport.PctData pctData = workShiftReport.getPctData();
            String orderCount = PrintUtils.getStr("订单总数", "", returnStr(String.valueOf(pctData.getOrderCounts()) + " /条"), 28);
            printAlign(esc, EscCommand.JUSTIFICATION.LEFT);// 设置打印居中
            printMode(esc, false, false);// 设置为倍高倍宽
            printText(esc,orderCount); // 打印文字
            printEnter(esc);

            String userCount = PrintUtils.getStr("客人总数", "", returnStr(String.valueOf(pctData.getCustomerCounts()) + " /人"), 28);
            printAlign(esc, EscCommand.JUSTIFICATION.LEFT);// 设置打印居中
            printMode(esc, false, false);// 设置为倍高倍宽
            printText(esc,userCount); // 打印文字
            printEnter(esc);

            String orderTotal = PrintUtils.getStr("订单均价", "", " ￥ " + returnStr(String.valueOf(pctData.getPricePerOrder()) + " / 元"), 28);
            printAlign(esc, EscCommand.JUSTIFICATION.LEFT);// 设置打印居中
            printMode(esc, false, false);// 设置为倍高倍宽
            printText(esc,orderTotal); // 打印文字
            printEnter(esc);

            String guestTotal = PrintUtils.getStr("客单价", "", " ￥ " + returnStr(String.valueOf(pctData.getPricePerCustomer()) + " / 元"), 28);
            printAlign(esc, EscCommand.JUSTIFICATION.LEFT);// 设置打印居中
            printMode(esc, false, false);// 设置为倍高倍宽
            printText(esc,guestTotal); // 打印文字
            printEnter(esc);

        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String returnStr(String str)
    {
        return TextUtils.isEmpty(str) ? "":str;
    }


    public void printCashReceipt(Order order, ArrayMap<String, PrintModelInfo> printMode, int orderPrinterType, Store store) {
        if (mGpService == null) {
            Log.e("", "USB printer not ready yet");
            return;
        }
        PosInfo posInfo = PosInfo.getInstance();
        String type = order.getOrderType();
        String orderType = "堂食";
        if (type.equals("EAT_IN")) {
            orderType = "堂食";
        } else if (type.equals("SALE_OUT")) {
            orderType = "外卖";
        } else if (type.equals("TAKE_OUT")) {
            orderType = "外带";
        }
        EscCommand esc = new EscCommand();
        String brandNameForStoreName = PrinterController.getBrandNameAndStoreName(printMode);
        PrintModelInfo modeTitle = printMode.get("brandName");//品牌名
        if (!TextUtils.isEmpty(brandNameForStoreName)) {
            esc.addInitializePrinter();
            esc.addPrintAndLineFeed();
            printAlign(esc, EscCommand.JUSTIFICATION.CENTER);// 设置打印居中
            printMode(esc, false, false);// 设置为倍高倍宽
            printText(esc, brandNameForStoreName); // 打印文字
            esc.addPrintAndLineFeed();
        }
        printEnter(esc);

        PrintModelInfo modeTicketType = printMode.get("ticketType");//小票类型
        if (modeTicketType.isShouldPrint()) {
            String ticketType = "";
            if (Constant.EventState.PRINT_WAIMAI == order.getPrinterType()) {
                ticketType = orderType;
            } else {
                ticketType = modeTicketType.getValue();
            }
            printAlign(esc, EscCommand.JUSTIFICATION.CENTER);// 设置打印居中
            printMode(esc, false, false);// 设置为倍高倍宽
            printText(esc, ticketType); // 打印文字
            printEnter(esc);
        }

        boolean isRefund = false;//是否是退单  退菜模式
        if (order.getPrinterType() == Constant.EventState.PRINTER_RETREAT_DISH_GUEST || order.getPrinterType() == Constant.EventState.PRINTER_RETREAT_ORDER) {
            isRefund = true;
        } else {
            isRefund = false;
        }

        PrintModelInfo modeSeparator = printMode.get("separator");
        printSeparator(esc, modeSeparator.getValue());//分隔符

        PrintModelInfo modeTableName = printMode.get("tableName");//桌台号
        String eatType = "";
        String eatNumber = "";
        if (!TextUtils.isEmpty(order.getTableNames())) {
            eatType = "桌台号:";
            eatNumber = order.getTableNames();
        } else {
            if (StoreInfor.cardNumberMode) {
                eatType = "餐牌号:";
                eatNumber = order.getTableNames();
            } else {
                eatType = "取餐号:";
                eatNumber = order.getCallNumber();
            }
        }
        if (modeTableName.isShouldPrint()) {
            printAlign(esc, EscCommand.JUSTIFICATION.LEFT);// 设置打印居中
            printMode(esc, false, false);// 设置为倍高倍宽
            printText(esc, eatType + eatNumber + "      " + orderType); // 打印文字
            printEnter(esc);
        }

        PrintModelInfo modeOrderId = printMode.get("orderId");//订单号
        PrintModelInfo modeCustomerCount = printMode.get("customerCount");//人数
        //                printerInterface.printTable(PrinterController.getDoubleLineText(modeCustomerCount.isBold(), small, "顾客人数:" + order.getCustomerAmount(), "订单号:" + order.getId()));
        //订单号
        if (modeOrderId.isShouldPrint()) {

            printAlign(esc, EscCommand.JUSTIFICATION.LEFT);// 设置打印居中
            printMode(esc, false, false);// 设置为倍高倍宽
            printText(esc, "订单号:" + order.getId()); // 打印文字
            printEnter(esc);
        }
        //人数
        if (modeCustomerCount.isShouldPrint() && order.getCustomerAmount() > 0) {

            printAlign(esc, EscCommand.JUSTIFICATION.LEFT);// 设置打印居中
            printMode(esc, false, false);// 设置为倍高倍宽
            printText(esc, "顾客人数:" + order.getCustomerAmount()); // 打印文字
            printEnter(esc);
        }

        //操作终端
        String terminalName = posInfo.getTerminalName();
        printAlign(esc, EscCommand.JUSTIFICATION.LEFT);// 设置打印居中
        printMode(esc, false, false);// 设置为倍高倍宽
        printText(esc, "操作终端:" + terminalName); // 打印文字
        printEnter(esc);

        //操作人
        String userName = posInfo.getRealname();//服务员

        printAlign(esc, EscCommand.JUSTIFICATION.LEFT);// 设置打印居中
        printMode(esc, false, false);// 设置为倍高倍宽
        printText(esc, "操作人:" + userName); // 打印文字
        printEnter(esc);

        //下单时间
        PrintModelInfo modeOrderTime = printMode.get("orderTime");
        if (modeOrderTime.isShouldPrint()) {

            printAlign(esc, EscCommand.JUSTIFICATION.LEFT);// 设置打印居中
            printMode(esc, false, false);// 设置为倍高倍宽
            printText(esc, "下单时间:" + getTimeStr(order.getCreatedAt())); // 打印文字
            printEnter(esc);
        }

        printSeparator(esc, modeSeparator.getValue());//分隔符
        String dishTitle = PrintUtils.getStr("菜品", "数量单价", "金额", 32);
        printAlign(esc, EscCommand.JUSTIFICATION.LEFT);// 设置打印居中
        printMode(esc, false, false);// 设置为倍高倍宽
        printText(esc, dishTitle); // 打印文字
        printSeparator(esc, modeSeparator.getValue());

        List<OrderItem> itemList = order.getItemList();
        BigDecimal dishCount = new BigDecimal("0.00");
        for (int i = 0; i < itemList.size(); i++) {
            OrderItem orderItem = itemList.get(i);

            if(order.getPrinterType() == Constant.EventState.PRINTER_RETREAT_ORDER)
            {
                orderItem.setRejectedQuantity(orderItem.getQuantity());
            }

            // 套餐菜品
            if (ToolsUtils.getIsPackage(orderItem)) {
                int quantity = 0;
                if (isRefund) {
                    quantity = orderItem.getRejectedQuantity();
                } else {
                    quantity = orderItem.getQuantity();
                }
                BigDecimal money = orderItem.getCost().setScale(2, BigDecimal.ROUND_DOWN).multiply(new BigDecimal(quantity + ""));
                boolean isGift = false;
                dishCount = dishCount.add(money);
                String zc = "";
                if (money.compareTo(BigDecimal.ZERO) != 1) {
                    zc = "(赠)";
                    isGift = true;
                }
                String oi = PrintUtils.getStr("(" + orderItem.getDishName() + zc + ")", PrinterController.getDishCount(order, orderItem), money.toString(), 32);
                String disCountStr = getDisCountStr(orderItem.getMarketList());

                printAlign(esc, EscCommand.JUSTIFICATION.LEFT);// 设置打印居中
                printMode(esc, false, false);// 设置为倍高倍宽
                printText(esc, oi); // 打印文字
                printEnter(esc);

                printAlign(esc, EscCommand.JUSTIFICATION.LEFT);// 设置打印居中
                printMode(esc, false, false);// 设置为倍高倍宽
                printText(esc, disCountStr); // 打印文字


                PrintModelInfo packageDetail = printMode.get("packageDetail");//套餐明细
                if (packageDetail != null && packageDetail.isShouldPrint())//查询是否要打印套餐子项
                {
                    // 套餐子项
                    List<Dish.Package> subItemList = orderItem.getSubItemList();
                    for (int a = 0; a < subItemList.size(); a++) {
                        String itemPrice = "";
                        if (subItemList.get(a).getItemPrice() != null) {
                            itemPrice = subItemList.get(a).getItemPrice().toString();
                            if (TextUtils.isEmpty(itemPrice)) {
                                itemPrice = "";
                            }
                        }
                        String oiSub = PrintUtils.getStr("[套]" + subItemList.get(a).getDishName(), PrinterController.getPackateCount(order, orderItem, subItemList.get(a)), itemPrice, 32);

                        printAlign(esc, EscCommand.JUSTIFICATION.LEFT);// 设置打印居中
                        printMode(esc, false, false);// 设置为倍高倍宽
                        printText(esc, oiSub); // 打印文字
                        printEnter(esc);

                        if (!isGift && !isRefund) {
                            dishCount = dishCount.add(printDishPackageOption(false, esc, subItemList.get(a)));
                        }
                    }
                }
            }
            //普通菜品
            else {
                int quantity = 0;
                if (isRefund) {
                    quantity = orderItem.getRejectedQuantity();
                } else {
                    quantity = orderItem.getQuantity();
                }

                BigDecimal price = orderItem.getCost();
                price = price == null ? new BigDecimal(0) : price;
                BigDecimal money = price.multiply(new BigDecimal(quantity)).setScale(2, BigDecimal.ROUND_DOWN);
                dishCount = dishCount.add(money);
                boolean isGift = false;
                String zc = "";
                if (money.compareTo(BigDecimal.ZERO) != 1) {
                    zc = "(赠)";
                    isGift = true;
                }
                String oi = PrintUtils.getStr(orderItem.getDishName() + zc, PrinterController.getDishCount(order, orderItem), money.toString(), 32);
                String disCountStr = getDisCountStr(orderItem.getMarketList());

                printAlign(esc, EscCommand.JUSTIFICATION.LEFT);// 设置打印居中
                printMode(esc, false, false);// 设置为倍高倍宽
                printText(esc, oi); // 打印文字
                printEnter(esc);

                printAlign(esc, EscCommand.JUSTIFICATION.LEFT);// 设置打印居中
                printMode(esc, false, false);// 设置为倍高倍宽
                printText(esc, disCountStr); // 打印文字

                if(!isRefund)
                {
                    dishCount = dishCount.add(printDishOption(false, esc, orderItem.optionList));
                }
            }
        }

        printSeparator(esc, modeSeparator.getValue());;//分隔符

//        if (order.getShippingFee() != null && order.getShippingFee().compareTo(new BigDecimal("0")) == 1) {
//            String oi = PrintUtils.getStr("外卖配送费", "", "" + order.getShippingFee(), 32);
//
//            printAlign(esc, EscCommand.JUSTIFICATION.LEFT);// 设置打印居中
//            printMode(esc, false, false);// 设置为倍高倍宽
//            printText(esc, oi); // 打印文字
//            printEnter(esc);
//        }

        if (order.getPrinterType() == Constant.EventState.PRINTER_ORDER) {
            BigDecimal countMoney = new BigDecimal("0.00");
            if (isRefund) {
                countMoney = dishCount;
            } else {
                countMoney = new BigDecimal(order.getCost());
            }

//            BigDecimal countMoney = dishCount;
//            if (order.getShippingFee() != null) {
//                countMoney = dishCount.add(order.getShippingFee());
//            }
            PrintModelInfo modeOrderTotal = printMode.get("orderTotal");//消费总计  最后要付的钱
            String totalMoney = PrintUtils.getStr("合计", "", countMoney.toString(), 32);

            printAlign(esc, EscCommand.JUSTIFICATION.LEFT);// 设置打印居中
            printMode(esc, false, false);// 设置为倍高倍宽
            printText(esc, totalMoney); // 打印文字
            printEnter(esc);
        } else if (orderPrinterType == Constant.EventState.PRINT_CHECKOUT) {
            PrintModelInfo modeOrderTotal = printMode.get("orderTotal");//消费总计  最后要付的钱
//            BigDecimal countMoney = new BigDecimal(order.getCost()).add(getShippingFee(order.getShippingFee()));
            BigDecimal countMoney = new BigDecimal("0.00");

            if (isRefund) {
                countMoney = dishCount;
            } else {
                countMoney = new BigDecimal(order.getCost());
            }

            String serviceMoney = PrintUtils.getStr("服务费:", "", order.getServiceMoney().toString(), 32);

            printAlign(esc, EscCommand.JUSTIFICATION.LEFT);// 设置打印居中
            printMode(esc, false, false);// 设置为倍高倍宽
            printText(esc, serviceMoney); // 打印文字
            printEnter(esc);
            if (type.equals("TAKE_OUT") || type.equals("SALE_OUT")) {
                String takeMoney = PrintUtils.getStr("打包费:", "", order.getTake_money().toString(), 32);
                printAlign(esc, EscCommand.JUSTIFICATION.LEFT);// 设置打印居中
                printMode(esc, false, false);// 设置为倍高倍宽
                printText(esc, takeMoney); // 打印文字
                printEnter(esc);
            }

            if (type.equals("SALE_OUT")) {
                if(!TextUtils.isEmpty(order.getCustomerName()))
                {
                    printAlign(esc, EscCommand.JUSTIFICATION.LEFT);// 设置打印居中
                    printMode(esc, false, false);// 设置为倍高倍宽
                    printText(esc, "顾客姓名:" + order.getCustomerName()); // 打印文字
                    printEnter(esc);
                }

                if(!TextUtils.isEmpty(order.getCustomerPhoneNumber()))
                {
                    printAlign(esc, EscCommand.JUSTIFICATION.LEFT);// 设置打印居中
                    printMode(esc, false, false);// 设置为倍高倍宽
                    printText(esc, "顾客电话:" + order.getCustomerPhoneNumber()); // 打印文字
                    printEnter(esc);
                }

                if(!TextUtils.isEmpty(order.getCustomerAddress()))
                {
                    printAlign(esc, EscCommand.JUSTIFICATION.LEFT);// 设置打印居中
                    printMode(esc, false, false);// 设置为倍高倍宽
                    printText(esc, "顾客地址:" + order.getCustomerAddress()); // 打印文字
                    printEnter(esc);
                }

                if(!TextUtils.isEmpty(getShippingFee(order.getShippingFee()).toString()))
                {
                    printAlign(esc, EscCommand.JUSTIFICATION.LEFT);// 设置打印居中
                    printMode(esc, false, false);// 设置为倍高倍宽
                    printText(esc, "外卖配送费: ￥" + getShippingFee(order.getShippingFee())); // 打印文字
                    printEnter(esc);
                }
                if(order.getTakeoutFee() != null && order.getTakeoutFee().compareTo(BigDecimal.ZERO) == 1)
                {
                    printAlign(esc, EscCommand.JUSTIFICATION.LEFT);// 设置打印居中
                    printMode(esc, false, false);// 设置为倍高倍宽
                    printText(esc, "外带费: ￥" + order.getTakeoutFee()); // 打印文字
                    printEnter(esc);
                }
            }

//            if (type.equals("SALE_OUT")) {
//                String saleMoney = PrintUtils.getStr("配送费:", "", getShippingFee(order.getShippingFee()).toString() + "", 32);
//                printAlign(esc, EscCommand.JUSTIFICATION.LEFT);// 设置打印居中
//                printMode(esc, false, false);// 设置为倍高倍宽
//                printText(esc, saleMoney); // 打印文字
//                printEnter(esc);
//
//                printText(esc, "顾客姓名:" + order.getCustomerName()); // 打印文字
//                printEnter(esc);
//
//                printText(esc, "顾客电话:" + order.getCustomerPhoneNumber()); // 打印文字
//                printEnter(esc);
//
//                printText(esc, "顾客地址:" + order.getCustomerAddress()); // 打印文字
//                printEnter(esc);
//
//                printText(esc, "外卖配送费: ￥" + getShippingFee(order.getShippingFee())); // 打印文字
//                printEnter(esc);
//            }
            BigDecimal avtiveMoney = new BigDecimal(order.getTotal()).subtract(new BigDecimal(order.getCost()));

            String activeMoney = PrintUtils.getStr("优惠:", "", avtiveMoney.toString(), 32);
            printText(esc, activeMoney); // 打印文字
            printEnter(esc);

            String giveMoney = PrintUtils.getStr("找零:", "", order.getGive_money().setScale(2, BigDecimal.ROUND_DOWN).toString(), 32);
            printText(esc, giveMoney); // 打印文字
            printEnter(esc);


            String totalMoney = PrintUtils.getStr("合计", "", countMoney.toString(), 32);
            printText(esc, totalMoney); // 打印文字
            printEnter(esc);
        }
        printSeparator(esc, modeSeparator.getValue());

        if (order.getPaymentList() != null && order.getPaymentList().size() > 0) {
            printText(esc, "支付方式"); // 打印文字
            printEnter(esc);
            printSeparator(esc, "-");

            for (PaymentList paymentList : order.getPaymentList()) {
                int paymentTypeId = paymentList.getPaymentTypeId();
                Payment payment = StoreInfor.getPaymentById(paymentTypeId);
                if (payment != null) {
                    String payType = PrintUtils.getStr(payment.getName(), "", paymentList.getValue().setScale(2, BigDecimal.ROUND_DOWN).toString(), 32);
                    printText(esc, payType); // 打印文字
                    printEnter(esc);
                }
            }
        }

        if (!TextUtils.isEmpty(order.getPaymentNo())) {
            String paymentNo = PrintUtils.getStr("电子流水号", "", order.getPaymentNo(), 32);
            printText(esc, paymentNo); // 打印文字
            printEnter(esc);
        }

        if (orderPrinterType == Constant.EventState.PRINT_CHECKOUT && store.isPrintQRCode()) {
            esc.addPrintAndLineFeed();
            printAlign(esc, EscCommand.JUSTIFICATION.CENTER);// 设置打印居中
            printMode(esc, false, false);// 设置为倍高倍宽
            printText(esc, "扫描下面二维码获取电子发票"); // 打印文字
            esc.addPrintAndLineFeed();
            printAlign(esc, EscCommand.JUSTIFICATION.CENTER);// 设置打印居中
            Bitmap bitmap = null;
            Bitmap qrcode = CreateImage.creatQRImage(getBarcodeUrl(order.getId() + ""), bitmap, 250, 250);
            esc.addRastBitImage(qrcode, 384, 0); // 打印图片
            printEnter(esc);
        }
        PrintModelInfo modeOrderQrCode = printMode.get("orderQrCode");//订单二维码
        if (orderPrinterType == Constant.EventState.PRINT_CHECKOUT && modeOrderQrCode.isShouldPrint()) {
            esc.addPrintAndLineFeed();
            printAlign(esc, EscCommand.JUSTIFICATION.CENTER);// 设置打印居中
            printMode(esc, false, false);// 设置为倍高倍宽
            printText(esc, "扫描下面二维码获取订单详情"); // 打印文字
            esc.addPrintAndLineFeed();
            printAlign(esc, EscCommand.JUSTIFICATION.CENTER);// 设置打印居中
            Bitmap bitmap = null;
            Bitmap qrcode = CreateImage.creatQRImage(getOrderQrCodeUrl(order.getId() + ""), bitmap, 250, 250);
            esc.addRastBitImage(qrcode, 384, 0); // 打印图片
            printEnter(esc);
        }
        PrintModelInfo modeQrCode = printMode.get("qrCode");//公众号二维码
        if (orderPrinterType == Constant.EventState.PRINT_CHECKOUT && modeQrCode.isShouldPrint()) {
            //                    TextRow rowNum = createRow(true, 1, "扫描下面二维码关注公众号");
            //                    rowNum.setAlign(Alignment.CENTER);
            //                    printerInterface.printRow(rowNum);
            //                    Bitmap bitmap = null;
            //                    Bitmap qrcode = CreateImage.creatQRImage(getOrderQrCodeUrl(order.getId() + ""), bitmap, 250, 250);
            //                    boolean isEnter = true;//是否打印回车
            //                    if (receiptPrinter != null) {
            //                        isEnter = true;
            //                    } else {
            //                        isEnter = false;
            //                    }
            //                    printerInterface.printBmp(new BitmapRow(qrcode), isEnter);
        }

        printSeparator(esc, modeSeparator.getValue());

        PrintModelInfo modeFreeText = printMode.get("freeText");//自定义文字
        if (modeFreeText.isShouldPrint()) {

            printAlign(esc, EscCommand.JUSTIFICATION.CENTER);// 设置打印居中
            printMode(esc, false, false);// 设置为倍高倍宽
            printText(esc, modeFreeText.getValue()); // 打印文字
            printEnter(esc);
        }

        //全单备注
        if (!TextUtils.isEmpty(order.getComment())) {
            printAlign(esc, EscCommand.JUSTIFICATION.CENTER);// 设置打印居中
            printMode(esc, false, false);// 设置为倍高倍宽
            printText(esc, "全单备注:" + order.getComment()); // 打印文字
            printEnter(esc);
        }
        printSeparator(esc, " ");
        printSeparator(esc, " ");
        printSeparator(esc, " ");

        Vector<Byte> datas = esc.getCommand(); // 发送数据
        byte[] bytes = ToolsUtils.ByteTo_byte(datas);
        String sss = Base64.encodeToString(bytes, Base64.DEFAULT);
        int rs;
        try {
            rs = mGpService.sendEscCommand(0, sss);
            GpCom.ERROR_CODE r = GpCom.ERROR_CODE.values()[rs];
            if (r != GpCom.ERROR_CODE.SUCCESS) {
                Log.e("ERROR", "failed to print text " + GpCom.getErrorText(r));
            }
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    //打印订单中某个菜品的标签
    public void printSingleItem(Order order, OrderItem orderItem) {
        if (mGpService == null) {
            Log.e("", "USB printer not ready yet");
            return;
        }

        Log.i("=====", "starting print dish， height: " + PosInfo.getInstance().getLabelPrinterHeight());
        TscCommand tsc = new TscCommand();
        if (labelHeight != null) {
            tsc.addSize(60, labelHeight.intValue()); //设置标签尺寸，按照实际尺寸设置
        } else {
            tsc.addSize(60, PosInfo.getInstance().getLabelPrinterHeight()); //设置标签尺寸，按照实际尺寸设置
        }

        tsc.addGap(0);           //设置标签间隙，按照实际尺寸设置，如果为无间隙纸则设置为0
        tsc.addDirection(TscCommand.DIRECTION.BACKWARD, TscCommand.MIRROR.NORMAL);//设置打印方向
        tsc.addReference(0, 0);//设置原点坐标
        tsc.addTear(EscCommand.ENABLE.ON); //撕纸模式开启

        tsc.addCls();// 清除打印缓冲区
        //绘制简体中文
        String orderText = "无效桌台号";
        if (order.getTableNames() != null && !order.getTableNames().isEmpty()) {
            orderText = "桌台号: " + order.getTableNames();
        } else {
            orderText = "订单号: " + order.getId();
        }

        //这个的单位是 点（dot), 不是英寸或者毫米, 标签共216个点那么高
        tsc.addText(20, 30, TscCommand.FONTTYPE.SIMPLIFIED_CHINESE, TscCommand.ROTATION.ROTATION_0, TscCommand.FONTMUL.MUL_1, TscCommand.FONTMUL.MUL_1, orderText);
        String dishInfo = "";
        if (orderItem.getCost() != null) {

            dishInfo = orderItem.getDishName() + "   " + orderItem.getCost();
        } else {
            dishInfo = orderItem.getDishName();
        }
        tsc.addText(20, 60, TscCommand.FONTTYPE.SIMPLIFIED_CHINESE, TscCommand.ROTATION.ROTATION_0, TscCommand.FONTMUL.MUL_1, TscCommand.FONTMUL.MUL_1, dishInfo);

        if (orderItem.getOptionList() != null) {
            String optionsStr = "";
            for (Option o : orderItem.getOptionList()) {
                optionsStr += o.getName() + ",";
            }
            tsc.addText(20, 90, TscCommand.FONTTYPE.SIMPLIFIED_CHINESE, TscCommand.ROTATION.ROTATION_0, TscCommand.FONTMUL.MUL_1, TscCommand.FONTMUL.MUL_1, optionsStr);
        }

        if (StoreInfor.phoneNumber != null) {
            String storeInfo = "电话: " + StoreInfor.phoneNumber;
            tsc.addText(20, 200 - 80, TscCommand.FONTTYPE.SIMPLIFIED_CHINESE, TscCommand.ROTATION.ROTATION_0, TscCommand.FONTMUL.MUL_1, TscCommand.FONTMUL.MUL_1, storeInfo);
        }

        tsc.addText(20, 200 - 40, TscCommand.FONTTYPE.SIMPLIFIED_CHINESE, TscCommand.ROTATION.ROTATION_0, TscCommand.FONTMUL.MUL_1, TscCommand.FONTMUL.MUL_1, TimeUtil.getStringTimeLong(order.getCreatedAt()));

        tsc.addPrint(1, 1); // 打印标签
        Vector<Byte> datas = tsc.getCommand(); //发送数据
        Byte[] Bytes = datas.toArray(new Byte[datas.size()]);
        byte[] bytes = ToolsUtils.Byte2byte(Bytes);
        String str = Base64.encodeToString(bytes, Base64.DEFAULT);
        int rel;
        try {
            rel = mGpService.sendTscCommand(0, str);
            GpCom.ERROR_CODE r = GpCom.ERROR_CODE.values()[rel];
            if (r != GpCom.ERROR_CODE.SUCCESS) {
                Log.e("ERROR", "failed to print text " + GpCom.getErrorText(r));
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }


    //打印一个测试用的菜品
    public void printTestOrder() {
        Order order = new Order();
        order.setId(111);
        order.setCreatedAt(System.currentTimeMillis());
        OrderItem oi = new OrderItem();
        oi.setCost(new BigDecimal("13.5"));
        oi.setDishName("红烧乌龙茶-测试");
        oi.setSkuStr("加冰,加珍珠,加牛奶，加咖啡");
        PosInfo.getInstance().setPhone("123556669");
        printSingleItem(order, oi);
    }

    public Integer getLabelHeight() {
        return labelHeight;
    }

    public void setLabelHeight(Integer labelHeight) {
        this.labelHeight = labelHeight;
    }

    private BigDecimal getShippingFee(BigDecimal shippingFee) {
        if (shippingFee != null) {
            return shippingFee;
        }
        return new BigDecimal("0");
    }

    private String getDisCountStr(List<MarketObject> marketList) {
        StringBuffer sb = new StringBuffer();
        if (marketList != null && marketList.size() > 0) {
            int size = marketList.size();
            for (int i = 0; i < size; i++) {
                MarketObject market = marketList.get(i);
                sb.append(market.getMarketName() + "-" + market.getReduceCash().toString() + "元");
                if (i != size - 1) {
                    sb.append("\n");
                }
            }
        }
        return sb.toString();
    }

    /**
     * 打印套餐项中的定制项
     */
    private BigDecimal printDishPackageOption(boolean isKitReceipt, EscCommand esc, Dish.Package subitem) {
        BigDecimal dishOption = new BigDecimal("0.00");
        if (subitem != null && subitem.optionList != null && subitem.optionList.size() > 0) {
            dishOption = dishOption.add(printDishOption(isKitReceipt, esc, subitem.optionList).multiply(new BigDecimal(subitem.quantity))).add(new BigDecimal(subitem.extraCost).multiply(new BigDecimal(subitem.quantity)));
        }
        return dishOption;
    }

    /**
     * 打印普通菜品中的定制项
     *
     * @param esc
     * @param optionList
     */
    private BigDecimal printDishOption(boolean isKitReceipt, EscCommand esc, List<Option> optionList) {
        BigDecimal dishOption = new BigDecimal("0.00");
        try {
            if (esc != null && optionList != null && optionList.size() > 0) {
                if (optionList != null && optionList.size() > 0) {
                    StringBuffer sb = new StringBuffer();
                    for (Option option : optionList) {
                        if (option.getPrice().compareTo(new BigDecimal("0")) == 0) {
                            sb.append(option.name + "、");
                        } else {
                            dishOption = dishOption.add(option.getPrice());
                            sb.append(option.name + "(" + option.getPrice() + "元)、");
                        }
                    }
                    //                    TextRow oiRow = createRow(false, 1, "    " + sb.toString());
                    //
                    //                    if (isKitReceipt) {
                    //                        oiRow.setScaleWidth(2);
                    //                        oiRow.setScaleHeight(2);
                    //                    }
                    //                    printerInterface.printRow(oiRow);

                    printAlign(esc, EscCommand.JUSTIFICATION.LEFT);// 设置打印居中
                    printMode(esc, false, false);// 设置为倍高倍宽
                    printText(esc, "   " + sb.toString()); // 打印文字
                    printEnter(esc);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dishOption;
    }

    private String getBarcodeUrl(String orderId) {
        PosInfo posInfo = PosInfo.getInstance();
        String mob = "mobilereport/invoice.html?";
        String storeInfo = "appId=" + posInfo.getAppId() + "&brandId=" + posInfo.getBrandId() + "&storeId=" + posInfo.getStoreId() + "&orderId=" + orderId;
        String url = posInfo.getServerUrl() + mob + storeInfo;
        return url;
    }

    private String getOrderQrCodeUrl(String orderId) {
        PosInfo posInfo = PosInfo.getInstance();
        String mob = "api/orders/checkServeStatus?";
        String storeInfo = "appId=" + posInfo.getAppId() + "&brandId=" + posInfo.getBrandId() + "&storeId=" + posInfo.getStoreId() + "&orderId=" + orderId;
        String url = posInfo.getServerUrl() + mob + storeInfo;
        return url;
    }

}
