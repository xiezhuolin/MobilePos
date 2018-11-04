package cn.acewill.mobile.pos.common;

import android.graphics.Bitmap;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import android.util.Log;

import com.sunmi.impl.V1Printer;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import cn.acewill.mobile.pos.config.MyApplication;
import cn.acewill.mobile.pos.config.Store;
import cn.acewill.mobile.pos.exception.PosServiceException;
import cn.acewill.mobile.pos.model.Definition;
import cn.acewill.mobile.pos.model.KDS;
import cn.acewill.mobile.pos.model.KitchenPrintMode;
import cn.acewill.mobile.pos.model.KitchenStall;
import cn.acewill.mobile.pos.model.MarketObject;
import cn.acewill.mobile.pos.model.MarketType;
import cn.acewill.mobile.pos.model.PaymentList;
import cn.acewill.mobile.pos.model.RePrintState;
import cn.acewill.mobile.pos.model.WaimaiOrderExtraData;
import cn.acewill.mobile.pos.model.WorkShiftNewReport;
import cn.acewill.mobile.pos.model.WorkShiftReport;
import cn.acewill.mobile.pos.model.dish.Dish;
import cn.acewill.mobile.pos.model.dish.Option;
import cn.acewill.mobile.pos.model.event.PosEvent;
import cn.acewill.mobile.pos.model.order.Order;
import cn.acewill.mobile.pos.model.order.OrderItem;
import cn.acewill.mobile.pos.model.payment.Payment;
import cn.acewill.mobile.pos.model.wsh.Account;
import cn.acewill.mobile.pos.printer.Alignment;
import cn.acewill.mobile.pos.printer.BitmapRow;
import cn.acewill.mobile.pos.printer.Column;
import cn.acewill.mobile.pos.printer.KdsState;
import cn.acewill.mobile.pos.printer.PrintModelInfo;
import cn.acewill.mobile.pos.printer.PrintRecord;
import cn.acewill.mobile.pos.printer.PrintTemplateType;
import cn.acewill.mobile.pos.printer.Printer;
import cn.acewill.mobile.pos.printer.PrinterFactory;
import cn.acewill.mobile.pos.printer.PrinterInterface;
import cn.acewill.mobile.pos.printer.PrinterLinkType;
import cn.acewill.mobile.pos.printer.PrinterOutputType;
import cn.acewill.mobile.pos.printer.PrinterState;
import cn.acewill.mobile.pos.printer.PrinterStatus;
import cn.acewill.mobile.pos.printer.PrinterTemplates;
import cn.acewill.mobile.pos.printer.PrinterVendor;
import cn.acewill.mobile.pos.printer.PrinterVendors;
import cn.acewill.mobile.pos.printer.PrinterWidth;
import cn.acewill.mobile.pos.printer.Separator;
import cn.acewill.mobile.pos.printer.Table;
import cn.acewill.mobile.pos.printer.TextRow;
import cn.acewill.mobile.pos.printer.gpnetwork.GpEnternetPrint;
import cn.acewill.mobile.pos.printer.usb.GpUsbPrinter;
import cn.acewill.mobile.pos.printer.usb.PosSinUsbPrinter;
import cn.acewill.mobile.pos.printer.usb.UsbPrinter;
import cn.acewill.mobile.pos.service.OrderService;
import cn.acewill.mobile.pos.service.PosInfo;
import cn.acewill.mobile.pos.service.ResultCallback;
import cn.acewill.mobile.pos.utils.Constant;
import cn.acewill.mobile.pos.utils.CreateImage;
import cn.acewill.mobile.pos.utils.PrintUtils;
import cn.acewill.mobile.pos.utils.SmVlPrintUtil;
import cn.acewill.mobile.pos.utils.TimeUtil;
import cn.acewill.mobile.pos.utils.ToolsUtils;
import rx.exceptions.Exceptions;

import static cn.acewill.mobile.pos.utils.Constant.EventState.PRINTER_RETREAT_DISH_GUEST;
import static cn.acewill.mobile.pos.utils.TimeUtil.getTimeStr;


/**
 * Created by DHH on 2017/3/17.
 */

public class PrinterDataController {
    private int bigSize = 3;
    private int middleSize = 2;
    private int scaleHeight = 3;
    private int small = 1;
    private int spaceWeight = 50;

    private static List<PrintRecord> printRecordList = new CopyOnWriteArrayList<>();

    public static List<PrintRecord> getPrintRecordList() {
        return printRecordList;
    }

    public void setPrintRecordList(List<PrintRecord> printRecordList) {
        this.printRecordList = printRecordList;
    }

    public void cleanPrintRecordList() {
        if (printRecordList != null && printRecordList.size() > 0) {
            printRecordList.clear();
        }
    }


    public static void setUsbPrinter(GpUsbPrinter usbPrint) {
        usbPrinter = usbPrint;
    }

    private static PrinterDataController instance;
    /**
     * 收银打印机对象
     */
    public static Printer receiptPrinter;
    /**
     * 临时附加打印机
     */
    public static Printer secondartPrinter;
    /**
     * 外卖打印机
     */
    public static Printer takeoutPrinter;

    public static Printer getSecondartPrinter() {
        return secondartPrinter;
    }

    public static void setSecondartPrinter(Printer secondartPrinter) {
        PrinterDataController.secondartPrinter = secondartPrinter;
    }

    public static Printer getTakeoutPrinter() {
        return takeoutPrinter;
    }

    public static void setTakeoutPrinter(Printer takeoutPrinter) {
        PrinterDataController.takeoutPrinter = takeoutPrinter;
    }

    /**
     * 打印模板列表
     */
    private static List<PrinterTemplates> printerTemplatesList;
    public static GpUsbPrinter usbPrinter;
    private static Store store;

    public static PrinterDataController getInstance() {
        if (instance == null) {
            instance = new PrinterDataController();
            store = Store.getInstance(MyApplication.getInstance());
        }
        return instance;
    }

    public static void cleanPrinterData() {
        if (printerList != null && printerList.size() > 0) {
            printerList.clear();
        }
    }

    public static Printer getReceiptPrinter() {
        return receiptPrinter;
    }

    public void setReceiptPrinter(Printer receiptPrinter) {
        this.receiptPrinter = receiptPrinter;
    }

    /**
     * 门店打印机列表
     */
    public static List<Printer> printerList = new ArrayList<>();
    /**
     * 门店KDS列表
     */
    public static List<KDS> KdsList = new ArrayList<>();
    /**
     * 门店档口列表
     */
    public static List<KitchenStall> kitchenStallList = new ArrayList<>();
    /**
     * 获取打印机厂商列表
     */
    public static List<PrinterVendors> printerVendorsList = new ArrayList<>();

    public static ArrayMap<Integer, Printer> printerMap = new ArrayMap<>();
    public static ArrayMap<Integer, KDS> kdsMap = new ArrayMap<>();
    private static boolean cashPrinterIsUSB = false;
    private static boolean posSinPrinterUSB = false;

    public static Map<Integer, Printer> getPrinterListforMap(List<Printer> printers, Integer cashPrinterId, Integer secondartPrinterId, Integer takeoutPrinterId) {
        printerMap.clear();
        if (printers != null && printers.size() > 0) {
            for (Printer printer : printers) {
                printerMap.put(printer.getId(), printer);
                if ((int) cashPrinterId == (int) printer.getId()) {
                    if (PrinterVendor.fromName(printer.getVendor()) != PrinterVendor.SHANGMI_FIX)//如果不是商米机器
                    {
                        if (printer.getLinkType() == PrinterLinkType.USB) {
                            cashPrinterIsUSB = true;
                            //是possin的USB打印机
                            if (PrinterVendor.fromName(printer.getVendor()) == PrinterVendor.POSIN_FIX) {
                                posSinPrinterUSB = true;
                            }
                        } else {
                            receiptPrinter = printer;
                        }
                    }
                    if (cashPrinterIsUSB) {
                        if (!posSinPrinterUSB) {
                            //                            usbPrinter.setPrinterId(printer.getId());
                        }
                        EventBus.getDefault().post(new PosEvent(Constant.EventState.CASH_PRINTER_USB, posSinPrinterUSB));
                    }
                }
                if ((int) secondartPrinterId == (int) printer.getId()) {
                    secondartPrinter = printer;
                }
                if ((int) takeoutPrinterId == (int) printer.getId()) {
                    takeoutPrinter = printer;
                }
            }
        }
        return printerMap;
    }

    public static Map<Integer, KDS> getKdsIdforMap(List<KDS> kdsList) {
        kdsMap.clear();
        if (kdsList != null && kdsList.size() > 0) {
            for (KDS kds : kdsList) {
                kdsMap.put(kds.getId(), kds);
            }
        }
        return kdsMap;
    }

    public static List<Printer> getPrinterList() {
        return printerList;
    }

    public static void setPrinterList(List<Printer> printerLists, Integer cashPrinterId, Integer secondartPrinterId, Integer takeoutPrinterId) {
        if (printerLists != null && printerLists.size() > 0) {
            printerList = new CopyOnWriteArrayList<>(printerLists);
            getPrinterListforMap(printerList, cashPrinterId, secondartPrinterId, takeoutPrinterId);
        }
    }

    public static List<KDS> getKdsList() {
        return KdsList;
    }

    public static void setKdsList(List<KDS> kdsList) {
        KdsList = kdsList;
        getKdsIdforMap(kdsList);
    }

    public static List<KitchenStall> getKitchenStallList() {
        return kitchenStallList;
    }


    public static ArrayMap<String, List<KitchenStall>> getKitchenStallPrintMap() {
        return kitchenStallPrintMap;
    }

    public static void setKitchenStallPrintMap(ArrayMap<String, List<KitchenStall>> kitchenStallPrintMap) {
        PrinterDataController.kitchenStallPrintMap = kitchenStallPrintMap;
    }

    /**
     * 打印机对应的档口映射
     */
    public static ArrayMap<String, List<KitchenStall>> kitchenStallPrintMap = new ArrayMap<>();

    public static void setKitchenStallList(List<KitchenStall> kitchenStallList) {
        PrinterDataController.kitchenStallList = kitchenStallList;
        if (kitchenStallPrintMap != null && kitchenStallPrintMap.size() > 0) {
            kitchenStallPrintMap.clear();
        }
        if (kitchenStallList != null && kitchenStallList.size() > 0) {
            for (KitchenStall kitchenStall : kitchenStallList) {
                //说明这个是打印机档口
                if (kitchenStall.getPrinterid() != null && kitchenStall.getPrinterid() > 0) {
                    List<KitchenStall> kitchenStallMapList = kitchenStallPrintMap.get("P" + kitchenStall.getPrinterid());
                    if (kitchenStallMapList == null) {
                        kitchenStallMapList = new ArrayList<>();
                    }
                    kitchenStallMapList.add(kitchenStall);
                    kitchenStallPrintMap.put("P" + kitchenStall.getPrinterid(), kitchenStallMapList);
                }
                //说明这个是KDS档口
                if (kitchenStall.getKdsid() != null && kitchenStall.getKdsid() > 0) {
                    List<KitchenStall> kitchenStallMapList = kitchenStallPrintMap.get("K" + kitchenStall.getKdsid());
                    if (kitchenStallMapList == null) {
                        kitchenStallMapList = new ArrayList<>();
                    }
                    kitchenStallMapList.add(kitchenStall);
                    kitchenStallPrintMap.put("K" + kitchenStall.getKdsid(), kitchenStallMapList);
                }
            }
        }
    }

    public static Map<Integer, Printer> getPrinterMap() {
        return printerMap;
    }

    public static void setPrinterMap(ArrayMap<Integer, Printer> printId2PrinterMap) {
        printerMap = printId2PrinterMap;
    }

    public static List<PrinterVendors> getPrinterVendorsList() {
        return printerVendorsList;
    }

    public static void setPrinterVendorsList(List<PrinterVendors> printerVendorsList) {
        PrinterDataController.printerVendorsList = printerVendorsList;
    }

    public static void removePrinter(Printer printer) {
        if (printer != null) {
            printerList.remove(printer);
        }
    }

    public static void removeKds(KDS kds) {
        if (kds != null) {
            KdsList.remove(kds);
        }
    }

    public static List<PrinterTemplates> getPrinterTemplatesList() {
        return printerTemplatesList;
    }

    /**
     * 打印模板类型映射
     */
    private static ArrayMap<Integer, ArrayMap<String, PrintModelInfo>> printModeMap = new ArrayMap<>();

    public static void setPrinterTemplatesList(List<PrinterTemplates> printerTemplatesList) {
        PrinterDataController.printerTemplatesList = printerTemplatesList;
        printModeMap.clear();
        int size = printerTemplatesList.size();
        for (int i = 0; i < size; i++) {
            PrinterTemplates printerTemplate = printerTemplatesList.get(i);
            //客用小票
            if (printerTemplate.getTemplateType() == PrintTemplateType.CUSTOMER) {
                printModeMap.put(Constant.EventState.PRINTER_ORDER, printerTemplate.getModels());
            }
            //加菜单
            if (printerTemplate.getTemplateType() == PrintTemplateType.ADD_DISH) {
                printModeMap.put(Constant.JsToAndroid.JS_A_ADDDISH, printerTemplate.getModels());
            }
            //退菜单
            if (printerTemplate.getTemplateType() == PrintTemplateType.RETREAT_DISH) {
                printModeMap.put(Constant.EventState.PRINTER_RETREAT_KITCHEN_ORDER, printerTemplate.getModels());
            }
            //预结单
            if (printerTemplate.getTemplateType() == PrintTemplateType.PRE_CHECKOUT) {
                printModeMap.put(Constant.EventState.ORDER_TYPE_ADVANCE, printerTemplate.getModels());
            }
            //结账单
            if (printerTemplate.getTemplateType() == PrintTemplateType.CHECKOUT) {
                printModeMap.put(Constant.EventState.PRINT_CHECKOUT, printerTemplate.getModels());
            }
            //标签
            if (printerTemplate.getTemplateType() == PrintTemplateType.LABEL) {

            }
            //厨房单
            if (printerTemplate.getTemplateType() == PrintTemplateType.KICHEN) {
                printModeMap.put(Constant.EventState.PRINTER_KITCHEN_ORDER, printerTemplate.getModels());
            }
        }

    }

    /**
     * 菜品对应的厨房档口映射
     */
    public static ArrayMap<Long, List<KitchenStall>> kitchenStallDishMap = new ArrayMap<>();

    /**
     * 映射厨房档口打印机数据
     */
    public static void handleKitchenStall() {
        kitchenStallDishMap.clear();
        int size = kitchenStallList.size();
        if (kitchenStallList != null && size > 0) {
            for (int i = 0; i < size; i++) {
                //循环档口列表
                KitchenStall kitchenStall = kitchenStallList.get(i);
                //如果该档口下的菜品列表不为空
                if (kitchenStall.getDishIdList() != null && kitchenStall.getDishIdList().size() > 0) {
                    for (Integer dishId : kitchenStall.getDishIdList()) {
                        //判断该菜品档口是否添加档口数据  list是因为一个菜品可能在多个档口
                        List<KitchenStall> kitchenStallList = kitchenStallDishMap.get(Long.valueOf(String.valueOf(dishId)));
                        if (kitchenStallList == null) {
                            kitchenStallList = new ArrayList<>();
                            kitchenStallDishMap.put(Long.valueOf(String.valueOf(dishId)), kitchenStallList);
                        }
                        kitchenStallList.add(kitchenStall);
                    }
                }
            }
        }
    }

    //临时存储打总单菜品的档口
    public static ArrayMap<Integer, List<KitchenStall>> summaryKitchenStallMap = new ArrayMap<>();
    //    //将要在总单上打印的菜品
    //    private ArrayMap<Integer, List<OrderItem>> summaryOiPrintMap = new ArrayMap<>();

    int laberCount = 0;

    public ArrayMap<Integer, List<OrderItem>> printKitChenOrder(Order order, int orderPrinterType) {
        summaryKitchenStallMap.clear();
        ArrayMap<Integer, List<OrderItem>> summaryOiPrintMap = new ArrayMap<>();
        if (summaryOiPrintMap != null && summaryOiPrintMap.size() > 0) {
            summaryOiPrintMap.clear();
        }
        //        PrintRecord printRecord = isHavePrintRecordForPrinterType(orderTemp.getId(), orderPrinterType, -1);
        //        printRecord.setOrder(orderTemp);
        laberCount = 0;
        StringBuffer sb = new StringBuffer();
        sb.setLength(0);
        if (order != null && order.getItemList() != null && order.getItemList().size() > 0) {
            int printPosition = 0;
            for (OrderItem oi : order.getItemList()) {
                Log.i("==========", "printKitChenOrder, orderid" + order.getId());
                Log.i("==========", "printer length: " + printerList.size());
                printPosition++;
                oi.setOrderId(order.getId());
                List<KitchenStall> kitchenStallList = kitchenStallDishMap.get(oi.getDishId());
                oi.setKitchenStallList(kitchenStallList);

                if (kitchenStallList != null) {
                    int size = kitchenStallList.size();
                    for (int i = 0; i < size; i++) {
                        KitchenStall kitchenStall = kitchenStallList.get(i);
                        Log.i("==========", "printer str: " + kitchenStall.getStallsName() + "stallsid  ==>" + kitchenStall.getStallsid() + "  dishName: " + oi.getDishName());

                        //如果是总单档口
                        if (kitchenStall.getSummaryReceiptCounts() != null && kitchenStall.getSummaryReceiptCounts() > 0) {
                            List<KitchenStall> summaryKitchenStallList = summaryKitchenStallMap.get(kitchenStall.getStallsid());
                            if (summaryKitchenStallList == null) {
                                summaryKitchenStallList = new ArrayList<>();
                                summaryKitchenStallMap.put(kitchenStall.getStallsid(), summaryKitchenStallList);
                            }
                            summaryKitchenStallList.add(kitchenStall);
                            saveKitchenStallForDish(oi, kitchenStall, summaryOiPrintMap);
                        }

                        //如果是分单档口
                        if (order.getRushDishType() != 2 && kitchenStall.getDishReceiptCounts() != null && kitchenStall.getDishReceiptCounts() > 0) {
                            //判断门店配置的打印机打印菜品
                            if (printerMap != null && printerMap.size() > 0) {
                                Printer printer = printerMap.get(kitchenStall.getPrinterid());
                                if (printer == null) {
                                    Log.e("PrintManager", "failed to find printer for receipt");
                                    continue;
                                }
                                if (oi.getQuantity() <= 0) {
                                    continue;
                                }
                                printer.setStandByState(false);
                                if (PrinterVendor.fromName(printer.getVendor()) != PrinterVendor.SPRT) {
                                    boolean isSprtOrUnknow = false;
                                    if (PrinterVendor.fromName(printer.getVendor()) != PrinterVendor.UNKNOWN) {
                                        isSprtOrUnknow = false;
                                        checkPrinterState2(printer);
                                    } else {
                                        isSprtOrUnknow = true;
                                        printer.setPrinterState(PrinterState.SUCCESS);
                                    }
                                    kitchenStall.setStandByError("");
                                    RePrintState rePrintState = rePrintCheck(printer, printer.getStandbyPrinterIdList());
                                    if (rePrintState == null) {
                                        return null;
                                    }
                                    if (rePrintState == RePrintState.PRIMARYSUCCESS) {
                                        printOrderItemDish(order, oi, printer, kitchenStall, i, isSprtOrUnknow);
                                    } else if (rePrintState == RePrintState.STANDBYSUCCESS) {
                                        Printer printerStandBy = printerMap.get(Integer.valueOf(printer.getStandbyPrinterIdList()));
                                        printer.setStandByState(true);
                                        kitchenStall.setStandByError(printer.getDescription() + "(" + printer.getIp() + ")异常,请转送!");
                                        printOrderItemDish(order, oi, printerStandBy, kitchenStall, i, isSprtOrUnknow);
                                    } else {
                                        Log.i("补打厨房单  orderItemId", oi.getId() + "");
                                        String sbb = sb.toString();
                                        if (!sbb.contains(printer.getDescription() + "(" + printer.getIp() + ")、")) {
                                            sb.append(printer.getDescription() + "(" + printer.getIp() + ")、");
                                        }
                                    }
                                } else {
                                    printOrderItemDish(order, oi, printer, kitchenStall, i, true);
                                }
                            }
                        }
                    }
                }
            }
        }
        order.setErrPrinterStr("");
        if (!TextUtils.isEmpty(sb.toString()) && order.getRushDishType() == 1) {
            EventBus.getDefault().post(new PosEvent(Constant.EventState.ERR_PRINT_KITCHEN_ORDER, order, sb.toString()));
        } else if (!TextUtils.isEmpty(sb.toString()) && order.getRushDishType() != 1) {
            order.setErrPrinterStr(sb.toString());
        }
        return summaryOiPrintMap;
    }

    public static ArrayMap<Integer, KitchenStall> summaryTempKitchenStallMap = new ArrayMap<>();

    /**
     * 初始化打印厨房总单
     *
     * @param orderKit
     */
    public void printOrderKitSummaryTicket(Order orderKit, ArrayMap<Integer, List<OrderItem>> summaryOiPrintMap, int orderPrinterType) {
        summaryTempKitchenStallMap.clear();
        StringBuffer sb = new StringBuffer();
        sb.setLength(0);
        sb.append(orderKit.getErrPrinterStr());
        if (summaryKitchenStallMap != null && summaryKitchenStallMap.size() > 0) {
            int summaryKeySize = summaryKitchenStallMap.size();
            int printPosition = 0;
            for (int i = 0; i < summaryKeySize; i++) {
                summaryTempKitchenStallMap.clear();
                printPosition++;
                int stallsid = summaryKitchenStallMap.keyAt(i);//找出总单map key
                if (orderKit != null && orderKit.getItemList() != null && orderKit.getItemList().size() > 0) {
                    List<KitchenStall> kitchenStallList = summaryKitchenStallMap.get(stallsid);
                    if (kitchenStallList != null && kitchenStallList.size() > 0) {
                        //将同一个档口的菜品集合到一起
                        for (KitchenStall kitchenStall : kitchenStallList) {
                            KitchenStall tempMap = summaryTempKitchenStallMap.get(kitchenStall.getStallsid());
                            if (tempMap == null) {
                                summaryTempKitchenStallMap.put(kitchenStall.getStallsid(), kitchenStall);
                            }
                        }
                    }

                    int summaryTempKitSize = summaryTempKitchenStallMap.size();
                    for (int j = 0; j < summaryTempKitSize; j++) {
                        int stallsid2 = summaryTempKitchenStallMap.keyAt(j);//找出总单map key
                        KitchenStall kitchenStall2 = summaryTempKitchenStallMap.get(stallsid2);

                        Printer printer = printerMap.get(kitchenStall2.getPrinterid());

                        if (printer != null) {
                            //                            PrintRecord printRecord = isHavePrintRecordForPrinterType(orderKit.getId(), orderPrinterType, stallsid2);
                            //                            printRecord.setOrder(orderKit);
                            //                            Order orderKitTemp = printRecord.getOrder();
                            Order orderKitTemp = ToolsUtils.cloneTo(orderKit);

                            printer.setStandByState(false);
                            if (PrinterVendor.fromName(printer.getVendor()) != PrinterVendor.SPRT) {
                                boolean isSprtOrUnknow = false;
                                if (PrinterVendor.fromName(printer.getVendor()) != PrinterVendor.UNKNOWN) {
                                    isSprtOrUnknow = false;
                                    checkPrinterState2(printer);
                                } else {
                                    isSprtOrUnknow = true;
                                    printer.setPrinterState(PrinterState.SUCCESS);
                                }
                                kitchenStall2.setStandByError("");
                                RePrintState rePrintState = rePrintCheck(printer, printer.getStandbyPrinterIdList());
                                if (rePrintState == null) {
                                    return;
                                }
                                if (rePrintState == RePrintState.PRIMARYSUCCESS) {
                                    //                                printRecord.setPrintName(kitchenStall2.getStallsName(), printer);
                                    PrinterInterface printerInterface = PrinterFactory.createPrinter(PrinterVendor.fromName(printer.getVendor()), printer.getIp(), printer.getWidth());
                                    printOrderDishKitSummaryTicket(printerInterface, printer, orderKitTemp, kitchenStall2, summaryOiPrintMap,isSprtOrUnknow);
                                    if(isSprtOrUnknow)
                                    {
                                        threadSleep(2);//如果是思普瑞特或者是未知的打印机,休息2秒,要不然可能会出现丢单的现象
                                    }
                                } else if (rePrintState == RePrintState.STANDBYSUCCESS) {
                                    Printer printerStandBy = printerMap.get(Integer.valueOf(printer.getStandbyPrinterIdList()));
                                    printer.setStandByState(true);
                                    kitchenStall2.setStandByError(printer.getDescription() + "(" + printer.getIp() + ")异常,请转送!");

                                    //                                printRecord.setPrintName(kitchenStall2.getStallsName(), printerStandBy);
                                    PrinterInterface printerInterface = PrinterFactory.createPrinter(PrinterVendor.fromName(printerStandBy.getVendor()), printerStandBy.getIp(), printerStandBy.getWidth());
                                    printOrderDishKitSummaryTicket(printerInterface, printerStandBy, orderKitTemp, kitchenStall2, summaryOiPrintMap,isSprtOrUnknow);
                                    if(isSprtOrUnknow)
                                    {
                                        threadSleep(2);//如果是思普瑞特或者是未知的打印机,休息2秒,要不然可能会出现丢单的现象
                                    }
                                } else {
                                    String sbb = sb.toString();
                                    if (!sbb.contains(printer.getDescription() + "(" + printer.getIp() + ")、")) {
                                        sb.append(printer.getDescription() + "(" + printer.getIp() + ")、");
                                    }
                                }
                            } else {
                                PrinterInterface printerInterface = PrinterFactory.createPrinter(PrinterVendor.fromName(printer.getVendor()), printer.getIp(), printer.getWidth());
                                printOrderDishKitSummaryTicket(printerInterface, printer, orderKitTemp, kitchenStall2, summaryOiPrintMap,false);
                                threadSleep(2);//如果是思普瑞特的打印机,休息2秒,要不然可能会出现丢单的现象
                            }
                        }
                    }
                }
            }
            if (!TextUtils.isEmpty(sb.toString())) {
                //oi.setOiRePrintIdentifying(Constant.EventState.PRINT_RETRY_REPRINT);
                EventBus.getDefault().post(new PosEvent(Constant.EventState.ERR_PRINT_KITCHEN_ORDER, orderKit, sb.toString()));
            }
        }
    }


    /**
     * 检测补打打印机是否是连通状态
     *
     * @param printer
     * @return
     */
    private RePrintState rePrintCheck(Printer printer, String str) {
        RePrintState rePrintState = RePrintState.ALLERROR;
        if (printer.getPrinterState() == PrinterState.SUCCESS) {
            rePrintState = RePrintState.PRIMARYSUCCESS;
        } else if (printer.getPrinterState() == PrinterState.ERROR) {
            if (!TextUtils.isEmpty(printer.getStandbyPrinterIdList()) && !printer.isStandByState()) {
                Printer printerStandBy = null;
                try {
                    printerStandBy = printerMap.get(Integer.valueOf(str));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    rePrintState = RePrintState.ALLERROR;
                }
                if (printerStandBy == null) {
                    return null;
                }
                checkPrinterState2(printerStandBy);
                if (printerStandBy.getPrinterState() == PrinterState.SUCCESS) {
                    rePrintState = RePrintState.STANDBYSUCCESS;
                } else {
                    rePrintState = RePrintState.ALLERROR;
                }
            } else {
                rePrintState = RePrintState.ALLERROR;
            }
        }
        return rePrintState;
    }

    /**
     * 打印菜品
     *
     * @param order
     * @param oi
     * @param printer
     * @param kitchenStall
     * @param printCount   当前的打印次数
     */
    private void printOrderItemDish(final Order order, final OrderItem oi, final Printer printer, KitchenStall kitchenStall, int printCount, boolean isSPRTprint) {
        PrinterInterface printerInterface = null;
        try {
            if (!TextUtils.isEmpty(printer.getIp()) && oi.getPrintState() == false) {
                List<KitchenStall> kitchenStallList = kitchenStallDishMap.get(oi.getDishId());
                int kitchenSize = kitchenStallList.size();
                //判断是标签纸还是普通纸  并且不是退菜单  不是转台单
                if (printer.getOutputType() == PrinterOutputType.LABEL && !isLogicRefundDish(order) && TextUtils.isEmpty(order.getChangeTableName())) {
                    oi.setThroughLabelPrint(true);//设置该菜品通过标签打印机打印
                    oi.setDishHavePrintCount(-1);
                    if (printer.getLinkType() == PrinterLinkType.USB) {
                        if (printer.getDeviceName() != null && !printer.getDeviceName().isEmpty() && usbPrinter != null) {
                            for (int i = 0; i < oi.getQuantity(); ++i) {
                                //                                printRecord.setPrintName(kitchenStall.getStallsName() + "USB标签打印机", null);
                                usbPrinter.printItem(order, oi);
                                oi.subtractDishHavePrintCount();//打印成功  减去需要打印的次数一次
                                //                                printRecord.setPrintTime();
                            }
                        }
                    }
                    //标签一份打印一张
                    else if (printer.getLinkType() == PrinterLinkType.NETWORK) {
                        for (int i = 0; i < oi.getQuantity(); i++) {
                            Log.i("标签打印:", oi.getDishName());
                            laberCount += 1;
                            //                            printRecord.setPrintName(kitchenStall.getStallsName() + "网口标签打印机", null);
                            GpEnternetPrint.gpPrint(oi, printer, printer.getLabelHeight(), laberCount, order.getLaberDishCount(), order);
                            oi.subtractDishHavePrintCount();//打印成功  减去需要打印的次数一次
                            //                            printRecord.setPrintTime();
                        }
                    }
                } else {
                    if (kitchenStall.getDishReceiptCounts() != null && (int) kitchenStall.getDishReceiptCounts() > 0) {
                        Log.i("厨房打印", "打印机" + printer.getDeviceName() + "," + printer.getIp());
                        boolean isRetreatDish = false;
                        boolean isRetreatDishAll = false;
                        //判断是否是退菜模式
                        if (order.getTableStyle() == Constant.EventState.PRINTER_RETREAT_ITEM_DISH) {
                            isRetreatDish = true;
                        }
                        //判断是否是退单模式
                        if (order.getTableStyle() == Constant.EventState.PRINTER_RETREAT_DISH) {
                            isRetreatDishAll = true;
                        }
                        int modeCount = getKitModeCount(oi, kitchenStall, isRetreatDish, isRetreatDishAll);
                        oi.setDishHavePrintCount(modeCount);
                        //                        printRecord.setPrintName(kitchenStall.getStallsName(), printer);
                        for (int i = 0; i < modeCount; i++) {
                            printerInterface = PrinterFactory.createPrinter(PrinterVendor.fromName(printer.getVendor()), printer.getIp(), printer.getWidth());
                            printKitchenReceipt(printerInterface, order, oi, kitchenStall,printer);
                            if (isSPRTprint)//如果是思普瑞特的打印机
                            {
                                threadSleep(2);//休息2秒,要不然可能会出现丢单的现象
                            }
                        }
                        //                        printRecord.setPrintTime();
                    }
                    //                    if (kitchenStall.getSummaryReceiptCounts() != null && (int) kitchenStall.getSummaryReceiptCounts() > 0) {
                    //                        saveKitchenStallForDish(oi, kitchenStall);
                    //                    }
                }
                if (printCount == kitchenSize - 1)//通过该菜品设置了多少个档口,判断打多少次,当打到最后一次的时候将该菜品的打印状态设置为已打印
                {
                    oi.setPrintState(true);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            closePrinterInterFace(printerInterface);
            //            Log.i("补打厨房单  orderItemId", oi.getId() + "");
            //            if (oi.getId() > 0) {
            //                oi.setOiRePrintIdentifying(Constant.EventState.PRINT_RETRY_REPRINT);
            //                EventBus.getDefault().post(new PosEvent(Constant.EventState.ERR_PRINT_KITCHEN_ORDER, order, oi, printer));
            //            }
        }
    }

    /**
     * 保存总单上打印的菜品
     *
     * @param oi
     * @param kitchenStall
     */
    private void saveKitchenStallForDish(OrderItem oi, KitchenStall kitchenStall, ArrayMap<Integer, List<OrderItem>> summaryOiPrintMap) {
        List<OrderItem> oiList = summaryOiPrintMap.get(kitchenStall.getStallsid());
        if (oiList == null) {
            oiList = new ArrayList<>();
            summaryOiPrintMap.put(kitchenStall.getStallsid(), oiList);
        }
        oiList.add(oi);
    }


    public void printKitchenReceipt(PrinterInterface printerInterface, Order order, OrderItem orderItem, KitchenStall kitchenStall, final Printer printer) throws Exception {
        printerInterface.init();
        if(ToolsUtils.logicPrintBuzzer(printer.getSealOf()) && printerInterface != null)
        {
            printerInterface.openBuzzer();
        }
        String orderType = order.getOrderType();
        String type = order.getOrderType();
        if (order.getTableStyle() == Constant.EventState.PRINTER_RETREAT_DISH || order.getTableStyle() == Constant.EventState.PRINTER_RETREAT_ITEM_DISH) {
            orderType = "退菜";
        } else if (order.getTableStyle() == Constant.EventState.PRINTER_RUSH_DISH) {
            orderType = "催菜";
        } else if (order.getTableStyle() == Constant.EventState.PRINTER_EXTRA_KITCHEN_RECEIPT) {
            orderType = "补打";
        } else if (!TextUtils.isEmpty(order.getChangeTableName())) {
            orderType = "转台";
        } else {
            if (type.equals("EAT_IN")) {
                orderType = "堂食";
            } else if (type.equals("SALE_OUT")) {
                orderType = "外卖";
            } else if (type.equals("TAKE_OUT")) {
                orderType = "外带";
            }
        }
        TextRow row = null;
        if (!TextUtils.isEmpty(kitchenStall.getStandByError())) {
            row = createRow(false, 2, kitchenStall.getStandByError());
            row.setAlign(Alignment.CENTER);
            printerInterface.printRow(row);
            printerInterface.printRow(new Separator(" "));
            printerInterface.printRow(new Separator("-"));
        }
        row = createRow(false, 2, kitchenStall.getStallsName() + "分单");
        row.setAlign(Alignment.CENTER);
        printerInterface.printRow(row);

        printerInterface.printRow(new Separator("-"));
        String tableName = order.getTableNames();
        if (ToolsUtils.logicIsTable()) {
            if (!TextUtils.isEmpty(tableName)) {
                TextRow rowNum = createRow(true, 2, "桌台号:" + (TextUtils.isEmpty(tableName) ? "0" : tableName) + "     " + orderType);
                rowNum.setAlign(Alignment.CENTER);
                printerInterface.printRow(rowNum);
            }
        } else {
            String cardNumberType = "";
            String callNumber = "";
            if (StoreInfor.cardNumberMode) {
                cardNumberType = "餐牌号:";
                callNumber = TextUtils.isEmpty(tableName) ? "0" : tableName;
            } else {
                cardNumberType = "取餐号:";
                callNumber = TextUtils.isEmpty(order.getCallNumber()) ? "0" : order.getCallNumber();
            }
            if (type.equals("SALE_OUT")) {
                cardNumberType = order.getSource() + "流水号:";
                callNumber = order.getThirdPlatfromOrderIdDaySeq();
            }

            row = createRow(false, 2, cardNumberType + callNumber + "     " + orderType);
            row.setAlign(Alignment.CENTER);
            row.setScaleHeight(2);
            printerInterface.printRow(row);
        }

        //        printerInterface.printRow(new Separator("-"));

        printerInterface.printRow(createRow(false, 1, "订单号: " + order.getId()));
        printerInterface.printRow(createRow(false, 1, "下单时间: " + getTimeStr(order.getCreatedAt())));

        printerInterface.printRow(new Separator(" "));
        String orderTitle = PrintUtils.getStr("菜品", 21, PrintUtils.TYPE_BACK) + PrintUtils.getStr("数量", 21, PrintUtils.TYPE_TOP);
        printerInterface.printRow(createRow(false, 1, orderTitle));
        printerInterface.printRow(new Separator("-"));

        String dishItem = "";
        String dishQuantity = "";
        if (kitchenStall.getKitchenPrintMode() == KitchenPrintMode.PER_DISH) {
            //退菜
            if (isLogicRefundDish(order)) {
                dishQuantity = orderItem.getRejectedQuantity() + "";
                dishQuantity = getDishRejectedQuantity(dishQuantity);
            }
            //正常下单打菜
            else {
                dishQuantity = orderItem.getQuantity() + "";
            }
        } else {
            dishQuantity = "1";
        }
        ArrayMap<String, PrintModelInfo> printMode = printModeMap.get(Constant.EventState.PRINTER_KITCHEN_ORDER);//厨房小票样式
        PrintModelInfo packageDetail = printMode.get("packageName");//套餐名称
        if (packageDetail != null && packageDetail.isShouldPrint())//查询是否要打印套餐名称
        {
            if (!TextUtils.isEmpty(orderItem.getPackName())) {
                printerInterface.printRow(createRow(false, 2, "(" + orderItem.getPackName() + isShowDishPackageMoney(orderItem) + ")"));
            }
        }

        String dishUnit = orderItem.getDishUnit();
        dishUnit = TextUtils.isEmpty(dishUnit) ? "份" : dishUnit;

        if (isLogicRefundDish(order)) {
            dishItem = PrintUtils.getStr("[退]" + orderItem.getDishName() + ToolsUtils.isWaiDai(order.getOrderType()), 16, PrintUtils.TYPE_BACK) + PrintUtils.getStr("-" + dishQuantity + dishUnit + isShowDishMoney(orderItem), 7, PrintUtils.TYPE_TOP);
        } else {
            dishItem = PrintUtils.getStr(orderItem.getDishName() + ToolsUtils.isWaiDai(order.getOrderType()), 16, PrintUtils.TYPE_BACK) + PrintUtils.getStr(dishQuantity + dishUnit + isShowDishMoney(orderItem), 7, PrintUtils.TYPE_TOP);
        }
        //厨房单里的菜品需要比较大的字体
        printerInterface.printRow(createRow(false, 2, dishItem));

        PrintModelInfo dishCutomerize = printMode.get("dishCutomerize");//菜品定制项
        printDishOption(true, printerInterface, orderItem.optionList, dishCutomerize);
        String comment = "";
        String allOrderComment = "";
        comment = orderItem.getComment();
        allOrderComment = order.getComment();
        if (!TextUtils.isEmpty(comment)) {
            TextRow rowNum = createRow(true, 1, "备注:( " + (TextUtils.isEmpty(comment) ? "" : comment) + " ) ");
            rowNum.setAlign(Alignment.LEFT);
            printerInterface.printRow(rowNum);
        }
        if (!TextUtils.isEmpty(allOrderComment)) {
            TextRow rowNum = createRow(true, 2, "全单备注:( " + (TextUtils.isEmpty(allOrderComment) ? "" : allOrderComment) + " ) ");
            rowNum.setAlign(Alignment.LEFT);
            printerInterface.printRow(rowNum);
        }
        printerInterface.close();
        orderItem.subtractDishHavePrintCount();//打印成功  减去需要打印的次数一次
        PosInfo posinfo = PosInfo.getInstance();
        String countInfo = "时间: " + getTimeStr(order.getCreatedAt()) + "/r/n" +
                "单号: " + order.getId() + "/r/n" +
                "操作人: " + posinfo.getRealname() + "/r/n";
        ToolsUtils.writePrinterReceiptInfo(countInfo);
    }

    /**
     * 这个订单是不是退菜模式
     */
    private static boolean isLogicRefundDish(Order order) {
        if (order.getTableStyle() == Constant.EventState.PRINTER_RETREAT_DISH || order.getTableStyle() == Constant.EventState.PRINTER_RETREAT_ITEM_DISH) {
            return true;
        }
        return false;
    }

    private static String getDishRejectedQuantity(String dishRejectedQuantity) {
        if (TextUtils.isEmpty(dishRejectedQuantity) || "0".equals(dishRejectedQuantity)) {
            dishRejectedQuantity = "1";
        }
        return dishRejectedQuantity;
    }

    /**
     * 厨房单是否要显示菜品金额  套餐项
     *
     * @param oi
     * @return
     */
    private String isShowDishPackageMoney(OrderItem oi) {
        if (store.getKitMoneyStyle()) {
            return "   ￥" + oi.getKitDishMoney().toString();
        }
        return "";
    }

    /**
     * 厨房单是否要显示菜品金额  普通菜品
     *
     * @param oi
     * @return
     */
    private String isShowDishMoney(OrderItem oi) {
        if (store.getKitMoneyStyle() && !oi.isPackageItem()) {
            return "   ￥" + oi.getKitDishMoney().toString();
        }
        return "";
    }

    /**
     * 打印普通菜品中的定制项
     *
     * @param printerInterface
     * @param optionList
     */
    private BigDecimal printDishOption(boolean isKitReceipt, PrinterInterface printerInterface, List<Option> optionList, PrintModelInfo dishCutomerize) {
        BigDecimal dishOption = new BigDecimal("0.00");
        try {
            if (printerInterface != null && optionList != null && optionList.size() > 0) {
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
                    TextRow oiRow = createRow(false, 2, "    +" + sb.toString());
                    if (isKitReceipt) {
                        oiRow.setScaleWidth(2);
                        oiRow.setScaleHeight(2);
                    }
                    printerInterface.printRow(oiRow);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return dishOption;
    }

    /**
     * 打印厨房总单
     *
     * @param printerInterface
     * @param printer
     * @param order
     * @param kitchenStall
     */
    public void printOrderDishKitSummaryTicket(PrinterInterface printerInterface, Printer printer, Order order, final KitchenStall kitchenStall, ArrayMap<Integer, List<OrderItem>> summaryOiPrintMap,boolean  isSprtOrUnknow) {
        List<OrderItem> orderItemList = null;
        if (summaryOiPrintMap != null && summaryOiPrintMap.size() > 0) {
            orderItemList = summaryOiPrintMap.get(kitchenStall.getStallsid());
            //            printRecord.getOrder().setOrderItemList(orderItemListTemp);
            //            orderItemList = printRecord.getOrder().getOrderItemList();
        }
        if (orderItemList != null && orderItemList.size() > 0) {
            try {
                for (OrderItem orderItem : orderItemList) {
                    orderItem.setPackName(orderItem.getTempPackName());
                }
                printerInterface.init();
                if(ToolsUtils.logicPrintBuzzer(printer.getSealOf()) && printerInterface != null)
                {
                    printerInterface.openBuzzer();
                }
                String orderType = order.getOrderType();
                String type = order.getOrderType();
                if (order.getTableStyle() == Constant.EventState.PRINTER_RETREAT_DISH) {
                    orderType = "退菜";
                } else if (order.getTableStyle() == Constant.EventState.PRINTER_RUSH_DISH) {
                    orderType = "催菜";
                } else if (order.getTableStyle() == Constant.EventState.PRINTER_EXTRA_KITCHEN_RECEIPT) {
                    orderType = "补打";
                } else if (!TextUtils.isEmpty(order.getChangeTableName())) {
                    orderType = "转台";
                } else {
                    if (type.equals("EAT_IN")) {
                        orderType = "堂食";
                    } else if (type.equals("SALE_OUT")) {
                        orderType = "外卖";
                    } else if (type.equals("TAKE_OUT")) {
                        orderType = "外带";
                    }
                }

                TextRow row = null;
                if (!TextUtils.isEmpty(kitchenStall.getStandByError())) {
                    row = createRow(false, 2, kitchenStall.getStandByError());
                    row.setAlign(Alignment.CENTER);
                    printerInterface.printRow(row);
                    printerInterface.printRow(new Separator(" "));
                    printerInterface.printRow(new Separator("-"));
                }

                row = createRow(false, 2, kitchenStall.getStallsName() + "总单");
                row.setAlign(Alignment.CENTER);
                printerInterface.printRow(row);

                printerInterface.printRow(new Separator("-"));
                String tableName = order.getTableNames();
                if (ToolsUtils.logicIsTable()) {
                    if (!TextUtils.isEmpty(tableName)) {
                        TextRow rowNum = createRow(true, 2, "桌台号:" + (TextUtils.isEmpty(tableName) ? "0" : tableName) + "     " + orderType);
                        rowNum.setAlign(Alignment.CENTER);
                        printerInterface.printRow(rowNum);
                    }
                } else {
                    String cardNumberType = "";
                    String callNumber = "";
                    if (StoreInfor.cardNumberMode) {
                        cardNumberType = "餐牌号:";
                        callNumber = (TextUtils.isEmpty(tableName) ? "0" : tableName);
                    } else {
                        cardNumberType = "取餐号:";
                        callNumber = (TextUtils.isEmpty(order.getCallNumber()) ? "0" : order.getCallNumber());
                    }
                    if (type.equals("SALE_OUT")) {
                        cardNumberType = order.getSource() + "流水号:";
                        callNumber = order.getThirdPlatfromOrderIdDaySeq();
                    }

                    row = createRow(false, 2, cardNumberType + callNumber + "     " + orderType);
                    row.setAlign(Alignment.CENTER);
                    row.setScaleHeight(2);
                    printerInterface.printRow(row);
                }

                printerInterface.printRow(new Separator("-"));

                printerInterface.printRow(createRow(false, 1, "订单号: " + order.getId()));
                printerInterface.printRow(createRow(false, 1, "下单时间: " + getTimeStr(order.getCreatedAt())));

                printerInterface.printRow(new Separator(" "));
                String orderTitle = PrintUtils.getStr("菜品", 21, PrintUtils.TYPE_BACK) + PrintUtils.getStr("数量", 21, PrintUtils.TYPE_TOP);
                printerInterface.printRow(createRow(false, 1, orderTitle));
                printerInterface.printRow(new Separator("-"));

                String previousPackageName = "";
                for (OrderItem orderItem : orderItemList) {
                    orderItem.setTempPackName(orderItem.getPackName());
                    if (previousPackageName.equals(orderItem.getPackName())) {
                        orderItem.setPackName("");
                    } else {
                        previousPackageName = orderItem.getPackName();
                    }
                }
                for (OrderItem orderItem : orderItemList) {
                    String dishItem = "";
                    String dishQuantity = "";
                    //                    if (kitchenStall.getKitchenPrintMode() == KitchenPrintMode.PER_DISH) {
                    //退菜
                    if (isLogicRefundDish(order)) {
                        dishQuantity = orderItem.getRejectedQuantity() + "";
                        if (orderItem.getRejectedQuantity() <= 0) {
                            continue;
                        }
                        dishQuantity = getDishRejectedQuantity(dishQuantity);
                    }
                    //正常下单打菜
                    else {
                        dishQuantity = orderItem.getQuantity() + "";
                    }

                    ArrayMap<String, PrintModelInfo> printMode = printModeMap.get(Constant.EventState.PRINTER_KITCHEN_ORDER);//厨房小票样式
                    PrintModelInfo packageDetail = printMode.get("packageName");//套餐名称
                    if (packageDetail != null && packageDetail.isShouldPrint())//查询是否要打印套餐名称
                    {
                        if (!TextUtils.isEmpty(orderItem.getPackName())) {
                            TextRow rowNum = createRow(true, 2, "(" + orderItem.getPackName() + isShowDishPackageMoney(orderItem) + ")");
                            rowNum.setAlign(Alignment.LEFT);
                            printerInterface.printRow(rowNum);
                        }
                    }
                    String dishUnit = orderItem.getDishUnit();
                    dishUnit = TextUtils.isEmpty(dishUnit) ? "份" : dishUnit;

                    if (isLogicRefundDish(order)) {
                        dishItem = PrintUtils.getStr("[退]" + orderItem.getDishName() + ToolsUtils.isWaiDai(order.getOrderType()), 16, PrintUtils.TYPE_BACK) + PrintUtils.getStr("-" + dishQuantity + dishUnit + isShowDishMoney(orderItem), 5, PrintUtils.TYPE_TOP);
                    } else {
                        dishItem = PrintUtils.getStr(orderItem.getDishName() + ToolsUtils.isWaiDai(order.getOrderType()), 16, PrintUtils.TYPE_BACK) + PrintUtils.getStr(dishQuantity + dishUnit + isShowDishMoney(orderItem), 5, PrintUtils.TYPE_TOP);
                    }

                    printerInterface.printRow(createRow(false, 2, dishItem));

                    PrintModelInfo dishCutomerize = printMode.get("dishCutomerize");//菜品定制项
                    printDishOption(true, printerInterface, orderItem.optionList, dishCutomerize);
                    String orderDishComment = orderItem.getComment();
                    if (!TextUtils.isEmpty(orderDishComment)) {
                        TextRow rowNum = createRow(true, 1, "菜品备注:( " + (TextUtils.isEmpty(orderDishComment) ? "" : orderDishComment) + " ) ");
                        rowNum.setAlign(Alignment.LEFT);
                        printerInterface.printRow(rowNum);
                    }
                }
                //检测是否在总单上打印KDS条码
                if (store.isSummaryShowKDSCode()) {
                    String kdsCode = getBarcode(order.getId() + "");
                    printerInterface.printRow(new Separator(" "));

                    Bitmap qrcode = CreateImage.createQRCode(kdsCode, 250);
                    printerInterface.printBmp(new BitmapRow(qrcode), true);
                }
                String comment = "";
                comment = order.getComment();
                if (!TextUtils.isEmpty(comment)) {
                    TextRow rowNum = createRow(true, 2, "全单备注:( " + (TextUtils.isEmpty(comment) ? "" : comment) + " ) ");
                    rowNum.setAlign(Alignment.LEFT);
                    printerInterface.printRow(rowNum);
                }
                if(isSprtOrUnknow)
                {
                    printerInterface.printRow(new Separator(" "));
                }
                //                printRecord.setPrint(true);//总单打印成功
                //                printRecord.setPrintTime();
                printerInterface.close();
                PosInfo posinfo = PosInfo.getInstance();
                String countInfo = "时间: " + getTimeStr(order.getCreatedAt()) + "/r/n" +
                        "单号: " + order.getId() + "/r/n" +
                        "操作人: " + posinfo.getRealname() + "/r/n";
                ToolsUtils.writePrinterReceiptInfo(countInfo);
            } catch (Exception e) {
                e.printStackTrace();
                closePrinterInterFace(printerInterface);
            }
        }
    }


    private String getBarcode(String oid) {
        //        byte[] start = {27,97,49,0,29,'h',100,29,'w',2,29,'H',2, 29,107,4};
        String barcode = "X" + oid + "Z";

        //        byte[] end = {0,10};
        return barcode;
    }

    private PrinterInterface logicInitPrint(Printer printer) throws IOException {
        PrinterInterface printerInterface = null;
        if (printer != null) {
            printerInterface = PrinterFactory.createPrinter(PrinterVendor.fromName(printer.getVendor()), printer.getIp(), printer.getWidth());
            Log.i("打印机id===" + printer.getId(), printer.getDescription() + "====" + printer.getIp());
        } else {
            printerInterface = PrinterFactory.createBluetoothPrinter(PrinterWidth.WIDTH_80MM);
            Log.i("蓝牙打印机===", "");
        }
        String errMessage = "";
        if (printerInterface == null) {
            if (printer == null) {
                errMessage = "收银打印机打印失败,请打开蓝牙!";
                throw Exceptions.propagate(new Exception(errMessage));
            } else {
                errMessage = "未配置收银打印机!";
                throw Exceptions.propagate(new Exception(errMessage));
            }
        }

        try {
            printerInterface.init();
        } catch (Exception e) {
            e.printStackTrace();
            errMessage = receiptPrinter.getDescription() + "打印机连接超时,请重启!";
            throw Exceptions.propagate(new Exception(errMessage));
        }

        if (printerInterface.checkStatus() != PrinterStatus.OK && receiptPrinter != null) {
            if (printerInterface.checkStatus() == PrinterStatus.COVER_OPEN) {
                errMessage = receiptPrinter.getDescription() + "打印机仓盖未关闭!";
            } else if (printerInterface.checkStatus() == PrinterStatus.NO_PAPER) {
                errMessage = receiptPrinter.getDescription() + "打印纸用尽,请更换打印纸!";
            } else if (printerInterface.checkStatus() == PrinterStatus.ERROR_OCCURED) {
                errMessage = receiptPrinter.getDescription() + "打印机未知错误,请重启!";
            }
            throw Exceptions.propagate(new Exception(errMessage));
        }
        return printerInterface;
    }

    /**
     * 检测打印机连接的状态
     *
     * @param printer
     * @throws IOException
     */
    private static void checkPrinterState(Printer printer) throws IOException {
        PrinterInterface printerInterface = null;
        if (printer != null) {
            printerInterface = PrinterFactory.createPrinter(PrinterVendor.fromName(printer.getVendor()), printer.getIp(), printer.getWidth());
            Log.i("打印机id===" + printer.getId(), printer.getDescription() + "====" + printer.getIp());
            Log.i("打印机:==", "==" + printer.getDescription() + "---" + printer.getIp());
        }
        String errMessage = "";
        if (printerInterface == null) {
            errMessage = printer.getDescription() + "打印机初始化打印机失败!";
            printer.setPrinterState(PrinterState.ERROR);
            printer.setErrMessage(errMessage);
            return;
        }
        try {
            printerInterface.init();
        } catch (Exception e) {
            e.printStackTrace();
            errMessage = printer.getDescription() + "打印机打印机连接超时,请重启!";
            printer.setPrinterState(PrinterState.ERROR);
            printer.setErrMessage(errMessage);
            Log.i("打印机:==", "==" + printer.getDescription() + "---" + printer.getIp() + "========" + errMessage);
            return;
        }
        if (printerInterface.checkStatus() != PrinterStatus.OK) {
            if (printerInterface.checkStatus() == PrinterStatus.COVER_OPEN) {
                errMessage = printer.getDescription() + "打印机仓盖未关闭!";
            } else if (printerInterface.checkStatus() == PrinterStatus.NO_PAPER) {
                errMessage = printer.getDescription() + "打印纸用尽,请更换打印纸!";
            } else if (printerInterface.checkStatus() == PrinterStatus.ERROR_OCCURED) {
                errMessage = printer.getDescription() + "出现未知错误,请重启!";
            } else if (printerInterface.checkStatus() == PrinterStatus.TIMEOUT_ERROR) {
                errMessage = printer.getDescription() + "打印机连接超时,请重启!";
            }
            printer.setPrinterState(PrinterState.ERROR);
            printerInterface.closeNoCutPaper();
            Log.i("打印机:==", "==" + printer.getDescription() + "---" + printer.getIp() + "========" + errMessage);
            return;
        }
        printerInterface.closeNoCutPaper();
        printer.setPrinterState(PrinterState.SUCCESS);
        Log.i("打印机:", "打印机=======================");
    }


    private static void checkPrinterState2(Printer printer) {
        PrinterInterface printerInterface = null;
        if (printer != null) {
            printerInterface = PrinterFactory.createPrinter(PrinterVendor.fromName(printer.getVendor()), printer.getIp(), printer.getWidth());
            Log.i("打印机id===" + printer.getId(), printer.getDescription() + "====" + printer.getIp());
            Log.i("打印机:==", "==" + printer.getDescription() + "---" + printer.getIp());
        }
        String errMessage = "";
        if (printer == null) {
            return;
        }
        if (printerInterface == null) {
            errMessage = printer.getDescription() + "打印机初始化打印机失败!";
            printer.setPrinterState(PrinterState.ERROR);
            printer.setErrMessage(errMessage);
            return;
        }
        try {
            printerInterface.init();
        } catch (Exception e) {
            e.printStackTrace();
            errMessage = printer.getDescription() + "打印机打印机连接超时,请重启!";
            printer.setPrinterState(PrinterState.ERROR);
            printer.setErrMessage(errMessage);
            Log.i("打印机:==", "==" + printer.getDescription() + "---" + printer.getIp() + "========" + errMessage);
            return;
        }
        try {
            if (printerInterface.checkStatus() != PrinterStatus.OK) {
                if (printerInterface.checkStatus() == PrinterStatus.COVER_OPEN) {
                    errMessage = printer.getDescription() + "打印机仓盖未关闭!";
                } else if (printerInterface.checkStatus() == PrinterStatus.NO_PAPER) {
                    errMessage = printer.getDescription() + "打印纸用尽,请更换打印纸!";
                } else if (printerInterface.checkStatus() == PrinterStatus.ERROR_OCCURED) {
                    errMessage = printer.getDescription() + "出现未知错误,请重启!";
                } else if (printerInterface.checkStatus() == PrinterStatus.TIMEOUT_ERROR) {
                    errMessage = printer.getDescription() + "打印机连接超时,请重启!";
                }
                printer.setPrinterState(PrinterState.ERROR);
                printer.setErrMessage(errMessage);
                printerInterface.closeNoCutPaper();
                Log.i("打印机:==", "==" + printer.getDescription() + "---" + printer.getIp() + "========" + errMessage);
                return;
            }
            printerInterface.closeNoCutPaper();
            printer.setPrinterState(PrinterState.SUCCESS);
            Log.i("打印机:", "打印机=======================");
        } catch (IOException e) {
            e.printStackTrace();
            printer.setPrinterState(PrinterState.ERROR);
            printer.setErrMessage(errMessage);
            Log.i("打印机:==", "==" + printer.getDescription() + "---" + printer.getIp() + "========" + errMessage);
        }
    }

    /**
     * 检测KDS连接情况
     *
     * @param kds
     */
    private static void checkKdsState(KDS kds) {
        if (kds != null) {
            getKdsState(kds);
        }
    }

    private static void getKdsState(final KDS kds) {
        final String[] errMessage = {""};
        try {
            OrderService orderService = OrderService.getKdsInstance(kds);
            orderService.kdsConnectCheck(new ResultCallback<Boolean>() {
                @Override
                public void onResult(Boolean result) {
                    if (result) {
                        kds.setKdsState(KdsState.SUCCESS);
                        errMessage[0] = "";
                        kds.setErrMessage(errMessage[0]);
                    }
                }

                @Override
                public void onError(PosServiceException e) {
                    kds.setKdsState(KdsState.ERROR);
                    errMessage[0] = ToolsUtils.returnXMLStr("kds_connect_error") + "," + e.getMessage();
                    kds.setErrMessage(errMessage[0]);
                }
            });
        } catch (PosServiceException e) {
            e.printStackTrace();
            kds.setKdsState(KdsState.ERROR);
            errMessage[0] = "初始化KDS失败!";
            kds.setErrMessage(errMessage[0]);
        }

    }

    //打印交接班小票
    public void printWorkShift(WorkShiftNewReport workShiftReport, int printType) {
        PrinterInterface printerInterface = null;
        String printErrStr = "";
        try {
            String printStr = "";
            //日结
            if (printType == PowerController.DAILY) {
                printStr = "日结";
            } else if (printType == PowerController.SHIFT_WORK) {
                printStr = "交接班";
            }
            Log.i("打印" + printStr + "小票", "<<======printWorkShift");

            //如果收银打印机是USB接口
            if (cashPrinterIsUSB) {
                if (!posSinPrinterUSB) {
                    if (usbPrinter == null) {
                        Log.i("收银USB打印机连接失败!====", "");
                        MyApplication.getInstance().ShowToast("收银USB打印机连接失败,请退出应用重新登录!");
                        return;
                    } else {
                        usbPrinter.printWorkShiftNew(workShiftReport, printStr);
                    }
                } else {
                    try {
                        UsbPrinter posSinUsbPrint = null;
                        //连接打印机
                        posSinUsbPrint = UsbPrinter.open(MyApplication.getInstance().getContext());
                        if (!posSinUsbPrint.ready()) {
                            Log.i("PosSinUSB打印机连接失败!====", "");
                            EventBus.getDefault().post(new PosEvent(Constant.EventState.ERR_PRINT_CASH, printType, "打印机未连接,请退出应用重试或是打印机缺纸,"));
                            return;
                        }
                        PosSinUsbPrinter.printWorkShiftNew(posSinUsbPrint, workShiftReport, printStr);
                    } catch (Throwable e) {
                        e.printStackTrace();
                        Log.i("收银PosSinUSB打印机连接失败!====", "");
                        EventBus.getDefault().post(new PosEvent(Constant.EventState.ERR_PRINT_CASH, printType, "打印机未连接,请退出应用重试或是打印机缺纸,"));
                    }
                }
            } else {
                if (store.getCashPrinterId() > 0) {
                    if (receiptPrinter != null) {
                        receiptPrinter.setStandByState(false);
                        checkPrinterState2(receiptPrinter);
                        RePrintState rePrintState = rePrintCheck(receiptPrinter, store.getCashPrinterId() + "");
                        if (rePrintState == null) {
                            return;
                        }
                        if (rePrintState == RePrintState.PRIMARYSUCCESS) {
                            printerInterface = logicInitPrint(receiptPrinter);
                        } else if (rePrintState == RePrintState.STANDBYSUCCESS) {
                            Printer printerStandBy = printerMap.get(Integer.valueOf(store.getCashPrinterId() + ""));
                            receiptPrinter.setStandByState(true);
                            printErrStr = receiptPrinter.getDescription() + "(" + receiptPrinter.getIp() + ")异常,请转送!";
                            printerInterface = logicInitPrint(printerStandBy);
                        } else {
                            closePrinterInterFace(printerInterface);
                            EventBus.getDefault().post(new PosEvent(Constant.EventState.ERR_PRINT_CASH, printType, "打印机未连接!"));
                        }
                    } else {
                        V1Printer v1Printer = new V1Printer(MyApplication.getInstance().getContext());
                        if(v1Printer != null)
                        {
                            SmVlPrintUtil.printWorkShiftNew(v1Printer, workShiftReport, printStr);
                        }
                        else{
                            EventBus.getDefault().post(new PosEvent(Constant.EventState.ERR_PRINT_CASH, printType, "打印机未连接!"));
                        }
                        return;
                        //                        printerInterface = logicInitPrint(receiptPrinter);
                    }

                }
            }

            if (printerInterface == null) {
                return;
            }
            PosInfo posInfo = PosInfo.getInstance();
            printWorkShiftReceipt(printerInterface, workShiftReport, posInfo.getDefinition(), printStr, printErrStr);
        } catch (Exception e) {
            e.printStackTrace();
            closePrinterInterFace(printerInterface);
            EventBus.getDefault().post(new PosEvent(Constant.EventState.ERR_PRINT_CASH, printType, e.getMessage()));
        }
    }

    public void printWorkShiftReceipt(PrinterInterface printerInterface, WorkShiftNewReport workShiftReport, Definition definition, String printStr, String printErrStr) {
        try {
            PosInfo posInfo = PosInfo.getInstance();
            TextRow row1 = null;
            if (!TextUtils.isEmpty(printErrStr)) {
                row1 = createRow(false, 2, printErrStr);
                row1.setAlign(Alignment.CENTER);
                printerInterface.printRow(row1);
                printerInterface.printRow(new Separator(" "));
            }

            row1 = createRow(false, 2, posInfo.getBrandName());
            row1.setAlign(Alignment.CENTER);
            printerInterface.printRow(row1);
            printerInterface.printRow(new Separator(" "));

            TextRow row = createRow(false, 2, printStr + "打印单");
            row.setAlign(Alignment.CENTER);
            printerInterface.printRow(row);
            printerInterface.printRow(new Separator(" "));

            row = createRow(false, 2, printStr + "人 : " + posInfo.getRealname());
            row.setAlign(Alignment.LEFT);
            printerInterface.printRow(row);
            printerInterface.printRow(new Separator(" "));

            if (!TextUtils.isEmpty(workShiftReport.getWorkShiftName())) {
                row = createRow(false, 1, printStr + "班次名称 : " + workShiftReport.getWorkShiftName());
                row.setAlign(Alignment.LEFT);
                printerInterface.printRow(row);
            }

            if (!TextUtils.isEmpty(workShiftReport.getStartTime())) {
                row = createRow(false, 1, printStr + "开始时间 : " + workShiftReport.getStartTime());
                row.setAlign(Alignment.LEFT);
                printerInterface.printRow(row);
            }
            if (!TextUtils.isEmpty(workShiftReport.getEndTime())) {
                row = createRow(false, 1, printStr + "结束时间 : " + workShiftReport.getEndTime());
                row.setAlign(Alignment.LEFT);
                printerInterface.printRow(row);
            }

            if (workShiftReport.getStartWorkShiftCash() > 0) {
                row = createRow(false, 1, "开班钱箱余额 : " + workShiftReport.getStartWorkShiftCash());
                row.setAlign(Alignment.LEFT);
                printerInterface.printRow(row);
            }
            if (workShiftReport.getEndWorkShiftCash() > 0) {
                row = createRow(false, 1, "交班钱箱余额 : " + workShiftReport.getEndWorkShiftCash());
                row.setAlign(Alignment.LEFT);
                printerInterface.printRow(row);
            }
            row = createRow(false, 1, "小票打印时间 : " + getTimeStr(System.currentTimeMillis()));
            row.setAlign(Alignment.LEFT);
            printerInterface.printRow(row);

            createWorkShiftItem(printerInterface, workShiftReport);

            printerInterface.printTable(createRow(false, 1, "应交现金:", String.valueOf(workShiftReport.getSubmitCash() + " / 元")));
            printerInterface.printTable(createRow(false, 1, "差额:", String.valueOf(workShiftReport.getDifferenceCash() + " / 元")));
            printerInterface.printRow(new Separator(" "));
            printerInterface.close();
        } catch (Exception e) {
            e.printStackTrace();
            closePrinterInterFace(printerInterface);
            MyApplication.getInstance().ShowToast("打印小票失败,请检查打印机设置!");
        }
    }

    private void createWorkShiftItem(PrinterInterface printerInterface, WorkShiftNewReport workShiftReport) {
        try {
            for (WorkShiftNewReport.WorkShiftCategoryDataList itemCategorySalesDataList : workShiftReport.getWorkShiftCategoryDataList()) {
                printerInterface.printRow(new Separator("-"));

                TextRow row = createRow(false, 1, itemCategorySalesDataList.getName());
                row.setAlign(Alignment.CENTER);
                printerInterface.printRow(row);

                printerInterface.printRow(new Separator("-"));

                Table table = new Table(3);

                TextRow title = new TextRow();
                title.setBoldFont(true);
                title.setScaleWidth(1);
                title.addColumn(new Column("名称", Alignment.LEFT));
                title.addColumn(new Column("数量", Alignment.LEFT));
                title.addColumn(new Column("金额", Alignment.RIGHT));
                table.setTitle(title);
                table.addRow(new Separator("-"));

                for (WorkShiftNewReport.WorkShiftCategoryDataList.WorkShiftItemDatas itemSalesDataList : itemCategorySalesDataList.getWorkShiftItemDatas()) {
                    title = new TextRow();
                    title.setScaleWidth(1);
                    title.addColumn(new Column(itemSalesDataList.getName() + " ", Alignment.LEFT));
                    title.addColumn(new Column(String.valueOf(itemSalesDataList.getItemCounts()), Alignment.LEFT));
                    title.addColumn(new Column(String.valueOf(itemSalesDataList.getTotal()), Alignment.RIGHT));
                    table.addRow(title);
                }
                //统计行
                printerInterface.printTable(table);
            }
        } catch (Exception e) {
            e.printStackTrace();
            closePrinterInterFace(printerInterface);
        }
    }


    //打印交接班小票
    public void printWorkShift(WorkShiftReport workShiftReport, int printType) {
        PrinterInterface printerInterface = null;
        String printErrStr = "";
        try {
            String printStr = "";
            //日结
            if (printType == PowerController.DAILY) {
                printStr = "日结";
            } else if (printType == PowerController.SHIFT_WORK) {
                printStr = "交接班";
            }
            Log.i("打印" + printStr + "小票", "<<======printWorkShift");

            //如果收银打印机是USB接口
            if (cashPrinterIsUSB) {
                if (!posSinPrinterUSB) {
                    if (usbPrinter == null) {
                        Log.i("收银USB打印机连接失败!====", "");
                        MyApplication.getInstance().ShowToast("收银USB打印机连接失败,请退出应用重新登录!");
                        return;
                    } else {
                        usbPrinter.printWorkShift(workShiftReport, printStr);
                    }
                } else {
                    try {
                        UsbPrinter posSinUsbPrint = null;
                        //连接打印机
                        posSinUsbPrint = UsbPrinter.open(MyApplication.getInstance().getContext());
                        if (!posSinUsbPrint.ready()) {
                            Log.i("PosSinUSB打印机连接失败!====", "");
                            EventBus.getDefault().post(new PosEvent(Constant.EventState.ERR_PRINT_CASH, printType, "打印机未连接,请退出应用重试或是打印机缺纸,"));
                            return;
                        }
                        PosSinUsbPrinter.printWorkShift(posSinUsbPrint, workShiftReport, printStr);
                    } catch (Throwable e) {
                        e.printStackTrace();
                        Log.i("收银PosSinUSB打印机连接失败!====", "");
                        EventBus.getDefault().post(new PosEvent(Constant.EventState.ERR_PRINT_CASH, printType, "打印机未连接,请退出应用重试或是打印机缺纸,"));
                    }
                }
            } else {
                if (store.getCashPrinterId() > 0) {
                    if (receiptPrinter != null) {
                        receiptPrinter.setStandByState(false);
                        checkPrinterState2(receiptPrinter);
                        RePrintState rePrintState = rePrintCheck(receiptPrinter, store.getCashPrinterId() + "");
                        if (rePrintState == null) {
                            return;
                        }
                        if (rePrintState == RePrintState.PRIMARYSUCCESS) {
                            printerInterface = logicInitPrint(receiptPrinter);
                        } else if (rePrintState == RePrintState.STANDBYSUCCESS) {
                            Printer printerStandBy = printerMap.get(Integer.valueOf(store.getCashPrinterId() + ""));
                            receiptPrinter.setStandByState(true);
                            printErrStr = receiptPrinter.getDescription() + "(" + receiptPrinter.getIp() + ")异常,请转送!";
                            printerInterface = logicInitPrint(printerStandBy);
                        } else {
                            closePrinterInterFace(printerInterface);
                            EventBus.getDefault().post(new PosEvent(Constant.EventState.ERR_PRINT_CASH, printType, "打印机未连接!"));
                        }
                    } else {
                        V1Printer v1Printer = new V1Printer(MyApplication.getInstance().getContext());
                        if(v1Printer != null)
                        {
                            SmVlPrintUtil.printWorkShift(v1Printer, workShiftReport, printStr);
                        }
                        else{
                            EventBus.getDefault().post(new PosEvent(Constant.EventState.ERR_PRINT_CASH, printType, "打印机未连接!"));
                        }
                        return;
//                        printerInterface = logicInitPrint(receiptPrinter);
                    }
                }
            }

            if (printerInterface == null) {
                return;
            }
            PosInfo posInfo = PosInfo.getInstance();
            printWorkShiftReceipt(printerInterface, workShiftReport, posInfo.getDefinition(), printStr, printErrStr);
        } catch (Exception e) {
            e.printStackTrace();
            closePrinterInterFace(printerInterface);
            EventBus.getDefault().post(new PosEvent(Constant.EventState.ERR_PRINT_CASH, printType, e.getMessage()));
        }
    }

    public void printWorkShiftReceipt(PrinterInterface printerInterface, WorkShiftReport workShiftReport, Definition definition, String printStr, String printErrStr) {
        try {
            PosInfo posInfo = PosInfo.getInstance();
            TextRow row = null;
            if (!TextUtils.isEmpty(printErrStr)) {
                row = createRow(false, 2, printErrStr);
                row.setAlign(Alignment.CENTER);
                printerInterface.printRow(row);
                printerInterface.printRow(new Separator(" "));
            }


            row = createRow(false, 2, printStr + "打印单");
            row.setAlign(Alignment.CENTER);
            printerInterface.printRow(row);
            printerInterface.printRow(new Separator(" "));

            row = createRow(false, 2, printStr + "人 : " + posInfo.getRealname());
            row.setAlign(Alignment.LEFT);
            printerInterface.printRow(row);
            printerInterface.printRow(new Separator(" "));

            if (!TextUtils.isEmpty(workShiftReport.getStartTime())) {
                row = createRow(false, 1, printStr + "开始时间 : " + workShiftReport.getStartTime());
                row.setAlign(Alignment.LEFT);
                printerInterface.printRow(row);
            }
            if (!TextUtils.isEmpty(workShiftReport.getEndTime())) {
                row = createRow(false, 1, printStr + "结束时间 : " + workShiftReport.getEndTime());
                row.setAlign(Alignment.LEFT);
                printerInterface.printRow(row);
            }
            row = createRow(false, 1, "小票打印时间 : " + getTimeStr(System.currentTimeMillis()));
            row.setAlign(Alignment.LEFT);
            printerInterface.printRow(row);

            createWorkShiftItem(printerInterface, workShiftReport);
            printerInterface.close();
        } catch (Exception e) {
            e.printStackTrace();
            closePrinterInterFace(printerInterface);
            MyApplication.getInstance().ShowToast("打印小票失败,请检查打印机设置!");
        }
    }

    private void createWorkShiftItem(PrinterInterface printerInterface, WorkShiftReport workShiftReport) {
        try {
            for (WorkShiftReport.ItemCategorySalesDataList itemCategorySalesDataList : workShiftReport.getItemCategorySalesDataList()) {
                printerInterface.printRow(new Separator("-"));

                TextRow row = createRow(false, 1, itemCategorySalesDataList.getName());
                row.setAlign(Alignment.CENTER);
                printerInterface.printRow(row);

                printerInterface.printRow(new Separator("-"));

                Table table = new Table(3);

                TextRow title = new TextRow();
                title.setBoldFont(true);
                title.setScaleWidth(1);
                title.addColumn(new Column("名称", Alignment.LEFT));
                title.addColumn(new Column("数量", Alignment.LEFT));
                title.addColumn(new Column("金额", Alignment.RIGHT));
                table.setTitle(title);
                table.addRow(new Separator("-"));

                for (WorkShiftReport.ItemCategorySalesDataList.ItemSalesDataList itemSalesDataList : itemCategorySalesDataList.getItemSalesDataList()) {
                    title = new TextRow();
                    title.setScaleWidth(1);
                    title.addColumn(new Column(itemSalesDataList.getName() + " ", Alignment.LEFT));
                    title.addColumn(new Column(String.valueOf(itemSalesDataList.getItemCounts()), Alignment.LEFT));
                    title.addColumn(new Column(String.valueOf(itemSalesDataList.getTotal()), Alignment.RIGHT));
                    table.addRow(title);
                }
                //统计行
                printerInterface.printTable(table);
            }

            printerInterface.printRow(new Separator("-"));

            TextRow row = createRow(false, 2, "客单价统计");
            row.setAlign(Alignment.CENTER);
            printerInterface.printRow(row);

            printerInterface.printRow(new Separator("-"));

            WorkShiftReport.PctData pctData = workShiftReport.getPctData();
            printerInterface.printTable(createRow(false, 1, "订单总数", String.valueOf(pctData.getOrderCounts() + " /条")));
            printerInterface.printTable(createRow(false, 1, "客人总数", String.valueOf(pctData.getCustomerCounts() + " /人")));
            printerInterface.printTable(createRow(false, 1, "订单均价", " ￥ " + String.valueOf(pctData.getPricePerOrder() + " / 元")));
            printerInterface.printTable(createRow(false, 1, "客单价", " ￥ " + String.valueOf(pctData.getPricePerCustomer() + " / 元")));
        } catch (Exception e) {
            e.printStackTrace();
            closePrinterInterFace(printerInterface);
        }
    }

    private void returnLogoUrl() {

    }

    private Alignment getAlian(int alian) {
        Alignment align = Alignment.LEFT;
        if (alian == 0) {
            align = Alignment.LEFT;
        }
        if (alian == 2) {
            align = Alignment.CENTER;
        }
        if (alian == 3) {
            align = Alignment.LEFT;
        }
        return align;
    }

    private void printEnter(PrinterInterface printerInterface, boolean isPrintEnter) throws IOException {
        if (isPrintEnter) {
            printerInterface.printRow(new Separator(" "));
        }
    }

    private void printErrMessage() {
        MyApplication.getInstance().ShowToast("打印失败,请重启打印机后重试打印!");
    }

    public void printerTest(Printer printer) {
        //标签
        if (printer.getOutputType() == PrinterOutputType.LABEL) {
            //USB标签
            if (printer.getLinkType() == PrinterLinkType.USB) {
                if (printer.getDeviceName() != null && !printer.getDeviceName().isEmpty() && usbPrinter != null) {
                    usbPrinter.printTestOrder();
                } else {
                    printErrMessage();
                }
            }
            //网口标签
            else if (printer.getLinkType() == PrinterLinkType.NETWORK) {
                try {
                    GpEnternetPrint.testGpPrint(printer);
                } catch (IOException e) {
                    e.printStackTrace();
                    printErrMessage();
                }
            }
        }
        //网口
        else {
            testPrintShangMi(printer);
        }
    }


    private void testPrintShangMi(Printer printer) {
        PrinterInterface printerInterface = null;
        boolean isShangMiprinter = false;
        //商米机器
        if (PrinterVendor.fromName(printer.getVendor()) == PrinterVendor.SHANGMI_FIX) {
            V1Printer v1Printer = new V1Printer(MyApplication.getInstance().getContext());
            if(v1Printer == null)
            {
                Log.i("商米V1打印机连接失败!====", "");
                MyApplication.getInstance().ShowToast("打印失败,请重新开启APP尝试重试打印!");
                return;
            }
            SmVlPrintUtil.testPrint(v1Printer);
            return;
        } else {
            isShangMiprinter = false;
            printerInterface = PrinterFactory.createPrinter(PrinterVendor.fromName(printer.getVendor()), printer.getIp(), printer.getWidth());
        }
        if (printerInterface == null) {
            MyApplication.getInstance().ShowToast("打印失败,请关闭打印机后尝试重试打印!");
            return;
        }
        try {
            printerInterface.init();
            if(printerInterface != null)
            {
                printerInterface.openBuzzer();
            }
            TextRow rowStore = null;
            rowStore = PrinterController.getSingleLineText(true, bigSize, ToSBC("测试打印"));
            rowStore.setScaleHeight(scaleHeight);
            rowStore.setScaleWidth(scaleHeight);
            rowStore.setAlign(Alignment.CENTER);
            printerInterface.printRow(rowStore);

            rowStore = createRow(false, 1, "打印时间:" + TimeUtil.getHourStr());
            printerInterface.printRow(rowStore);

            if (!isShangMiprinter) {
                rowStore = createRow(false, 1, "打印IP:" + printer.getIp());
                printerInterface.printRow(rowStore);
            }
            printerInterface.close();
        } catch (Exception e) {
            e.printStackTrace();
            closePrinterInterFace(printerInterface);
            MyApplication.getInstance().ShowToast("打印失败,请关闭打印机后尝试重试打印!");
        }
    }

    private PrintRecord isHavePrintRecordForPrinterType(long orderId, int orderPrinterType, int summaryKitchenStallId) {
        PrintRecord printRecordInfo = new PrintRecord();
        boolean isAddInfo = true;//是否要添加一条数据
        if (printRecordList != null && printRecordList.size() > 0) {
            for (PrintRecord printRecord : printRecordList) {
                //如果设置了多个总单档口  总单记录就需要显示多条
                if (orderPrinterType == Constant.EventState.PRINTER_KITCHEN_SUMMARY_ORDER) {
                    if (orderId == printRecord.getOrderId() && orderPrinterType == printRecord.getOrderPrintType() && summaryKitchenStallId == printRecord.getSummaryKitchenStallId()) {
                        printRecordInfo = printRecord;
                        isAddInfo = false;
                        break;
                    }
                } else {
                    if (orderId == printRecord.getOrderId() && orderPrinterType == printRecord.getOrderPrintType()) {
                        printRecordInfo = printRecord;
                        isAddInfo = false;
                        break;
                    }
                }
            }
        }
        if (isAddInfo) {
            printRecordInfo.setSummaryKitchenStallId(summaryKitchenStallId);
            printRecordInfo.setOrderPrintType(orderPrinterType);
            printRecordList.add(printRecordInfo);
        }
        return printRecordInfo;
    }


    //打印客用单
    public void printGuestOrder(Order order, int orderPrinterType) {
        PrinterInterface printerInterface = null;
        String printErrStr = "";
        boolean isSunMiPrinter = false;
        PrintRecord printRecord = isHavePrintRecordForPrinterType(order.getId(), orderPrinterType, -1);
        printRecord.setOrderId(order.getId());
        try {
            Log.i("打印客用单", "<<======printGuestOrder");
            Log.i("订单Id==orderId==", order.getId() + "");
            ArrayMap<String, PrintModelInfo> printMode = printModeMap.get(orderPrinterType);
            if (printMode != null && printMode.size() > 0) {
                if (PrinterDataController.getInstance().getTakeoutPrinter() != null && Constant.EventState.PRINT_WAIMAI == order.getPrinterType()) {
                    printRecord.setPrintName(takeoutPrinter.getDescription(), takeoutPrinter);
                    printerInterface = logicInitPrint(takeoutPrinter);
                } else if (Constant.EventState.PRINT_WAIMAI == order.getPrinterType()) {
                    printRecord.setPrintName(secondartPrinter.getDescription(), secondartPrinter);
                    printerInterface = logicInitPrint(secondartPrinter);
                } else {
                    //如果收银打印机是USB接口
                    if (cashPrinterIsUSB) {
                        if (!posSinPrinterUSB) {
                            if (usbPrinter == null) {
                                Log.i("收银USB打印机连接失败!====", "");
                                MyApplication.getInstance().ShowToast("收银USB打印机连接失败,请退出应用重新登录!");
                                return;
                            } else {
                                printRecord.setPrintName("收银USB打印机", null);
                                usbPrinter.printCashReceipt(order, printMode, orderPrinterType, store);
                            }
                        } else {
                            try {
                                UsbPrinter posSinUsbPrint = null;
                                //连接打印机
                                posSinUsbPrint = UsbPrinter.open(MyApplication.getInstance().getContext());
                                if (!posSinUsbPrint.ready()) {
                                    Log.i("PosSinUSB打印机连接失败!====", "");
                                    EventBus.getDefault().post(new PosEvent(Constant.EventState.ERR_PRINT_CASH, orderPrinterType, "打印机未连接,请退出应用重试或是打印机缺纸,"));
                                    return;
                                }
                                PosSinUsbPrinter.printCashReceipt(posSinUsbPrint, order, printMode, orderPrinterType, store);
                            } catch (Throwable e) {
                                e.printStackTrace();
                                Log.i("收银PosSinUSB打印机连接失败!====", "");
                                EventBus.getDefault().post(new PosEvent(Constant.EventState.ERR_PRINT_CASH, orderPrinterType, "打印机未连接,请退出应用重试或是打印机缺纸,"));
                            }
                        }
                    } else {
                        if (receiptPrinter == null) {
                            if(store.getCashPrinterId() > 0)
                            {
                                isSunMiPrinter = true;
                                //商米V1打印机服务
                                V1Printer v1Printer = new V1Printer(MyApplication.getInstance().getContext());
                                if(v1Printer == null)
                                {
                                    Log.i("商米V1打印机连接失败!====", "");
                                    EventBus.getDefault().post(new PosEvent(Constant.EventState.ERR_PRINT_CASH, orderPrinterType, "打印机未连接,请退出应用重试或是打印机缺纸,"));
                                    return;
                                }
                                SmVlPrintUtil.printCashReceipt(v1Printer, order, printMode, orderPrinterType, store);
                            }
                            return;
                        }
                        if (store.getCashPrinterId() > 0) {
                            //                            printerInterface = logicInitPrint(receiptPrinter);
                            if (receiptPrinter != null) {
                                receiptPrinter.setStandByState(false);
                                checkPrinterState2(receiptPrinter);
                                RePrintState rePrintState = rePrintCheck(receiptPrinter, store.getCashPrinterId() + "");
                                if (rePrintState == null) {
                                    return;
                                }
                                if (rePrintState == RePrintState.PRIMARYSUCCESS) {
                                    printerInterface = logicInitPrint(receiptPrinter);
                                } else if (rePrintState == RePrintState.STANDBYSUCCESS) {
                                    Printer printerStandBy = printerMap.get(Integer.valueOf(store.getCashPrinterId() + ""));
                                    receiptPrinter.setStandByState(true);
                                    printErrStr = receiptPrinter.getDescription() + "(" + receiptPrinter.getIp() + ")异常,请转送!";
                                    printerInterface = logicInitPrint(printerStandBy);
                                } else {
                                    closePrinterInterFace(printerInterface);
                                    EventBus.getDefault().post(new PosEvent(Constant.EventState.ERR_PRINT_CASH, orderPrinterType, "打印机未连接!"));
                                }
                            } else {
                                printerInterface = logicInitPrint(receiptPrinter);
                            }
                        }
                    }
                }

                if (receiptPrinter == null) {
                    printRecord.setPrintName("收银商米打印机", null);
                } else {
                    printRecord.setPrintName(receiptPrinter.getDescription(), receiptPrinter);
                }

                if (printerInterface == null) {
                    return;
                }
                if (isSunMiPrinter) {
                    spaceWeight = 50;
                } else {
                    spaceWeight = 40;
                    if(ToolsUtils.logicPrintBuzzer(receiptPrinter.getSealOf()) && printerInterface != null)
                    {
                        printerInterface.openBuzzer();
                    }
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


                String brandNameForStoreName = PrinterController.getBrandNameAndStoreName(printMode);
                PrintModelInfo modeTitle = printMode.get("brandName");//品牌名
                TextRow rowStore = null;

                if (!TextUtils.isEmpty(printErrStr)) {
                    rowStore = createRow(false, 2, printErrStr);
                    rowStore.setAlign(Alignment.CENTER);
                    printerInterface.printRow(rowStore);
                    printerInterface.printRow(new Separator(" "));
                }

                if (!TextUtils.isEmpty(brandNameForStoreName)) {
                    rowStore = PrinterController.getSingleLineText(false, bigSize, ToSBC(brandNameForStoreName));
                    rowStore.setScaleHeight(scaleHeight);
                    rowStore.setAlign(Alignment.CENTER);
                    printerInterface.printRow(rowStore);
                }

                printEnter(printerInterface, true);

                boolean isRefund = false;//是否是退单  退菜模式

                PrintModelInfo modeTicketType = printMode.get("ticketType");//小票类型
                if (modeTicketType.isShouldPrint()) {
                    String ticketType = "";
                    if (Constant.EventState.PRINT_WAIMAI == order.getPrinterType()) {
                        ticketType = orderType;
                    } else {
                        ticketType = modeTicketType.getValue();
                    }
                    if ((Constant.EventState.PRINTER_RETREAT_ORDER == order.getPrinterType()) || (PRINTER_RETREAT_DISH_GUEST == order.getPrinterType())) {
                        ticketType = "退菜单";
                        isRefund = true;
                    }
                    rowStore = PrinterController.getSingleLineText(false, bigSize, ticketType);
                    rowStore.setScaleHeight(scaleHeight);
                    rowStore.setAlign(Alignment.CENTER);
                    printerInterface.printRow(rowStore);
                    //                    printEnter(printerInterface, modeTicketType.isPrintEnter());
                }

                PrintModelInfo modeSeparator = printMode.get("separator");
                printerInterface.printRow(new Separator(modeSeparator.getValue()));//分隔符

                PrintModelInfo modeTableName = printMode.get("tableName");//桌台号
                String sourse = "";
                if (Constant.EventState.PRINT_WAIMAI == order.getPrinterType()) {
                    sourse = order.getSource();
                }
                String eatType = "";
                String eatNumber = "";
                if (ToolsUtils.logicIsTable()) {
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
                    if (type.equals("SALE_OUT")) {
                        eatType = order.getSource() + "流水号:";
                        eatNumber = order.getThirdPlatfromOrderIdDaySeq();
                    }
                }
                // printerInterface.printTable(PrinterController.getDoubleLineText(false, bigSize, ToSBC(eatType + eatNumber), "   "+orderType));
                if (modeTableName.isShouldPrint()) {
                    String eatMode = "";
                    if (isSunMiPrinter) {
                        eatMode = PrintUtils.getStr(ToSBC(eatType + eatNumber), "", sourse.equals("") ? orderType : sourse, spaceWeight);
                    } else {
                        eatMode = PrintUtils.getStr(eatType + eatNumber, "", sourse.equals("") ? orderType : sourse, spaceWeight - 20);
                    }
                    rowStore = PrinterController.getSingleLineText(false, bigSize, eatMode);//取餐号和桌台号餐牌号

                    rowStore.setScaleHeight(scaleHeight);
                    rowStore.setAlign(Alignment.LEFT);
                    printerInterface.printRow(rowStore);
                    //printEnter(printerInterface, modeTableName.isPrintEnter());
                }

                PrintModelInfo modeOrderId = printMode.get("orderId");//订单号
                PrintModelInfo modeCustomerCount = printMode.get("customerCount");//人数
                //                printerInterface.printTable(PrinterController.getDoubleLineText(modeCustomerCount.isBold(), small, "顾客人数:" + order.getCustomerAmount(), "订单号:" + order.getId()));
                //订单号
                if (modeOrderId.isShouldPrint()) {
                    String orderNumber = "";
                    String number = "订单号:";
                    if (isSunMiPrinter) {
                        orderNumber = PrintUtils.getStr(ToSBC(number + order.getId()), "", "", 0);
                    }
                    else{
                        orderNumber = PrintUtils.getStr(number + order.getId(), "", "", 0);
                    }
                    rowStore = PrinterController.getSingleLineText(false, bigSize, orderNumber);
                    rowStore.setScaleHeight(scaleHeight);
                    rowStore.setAlign(Alignment.LEFT);
                    printerInterface.printRow(rowStore);
                    //                    printerInterface.printRow(new Separator(" "));
                    //                    printEnter(printerInterface, modeOrderId.isPrintEnter());
                }

                if (!type.equals("SALE_OUT")) {

                    //人数
                    if (modeCustomerCount.isShouldPrint() && order.getCustomerAmount() > 0) {
                        rowStore = PrinterController.getSingleLineText(false, small, "顾客人数:" + order.getCustomerAmount());
                        rowStore.setAlign(Alignment.LEFT);
                        printerInterface.printRow(rowStore);
                        printEnter(printerInterface, modeCustomerCount.isPrintEnter());
                    }

                    //操作终端
                    String terminalName = posInfo.getTerminalName();
                    //操作人
                    String userName = posInfo.getRealname();//服务员

                    String terminalUser = "";
                    if (isSunMiPrinter) {
                        terminalUser = PrintUtils.getStr("操 作 人:" + userName, "", "操作终端:" + terminalName, spaceWeight - 10);
                    } else {
                        terminalUser = PrintUtils.getStr("操 作 人:" + userName, "", "操作终端:" + terminalName, spaceWeight - 30);
                    }
                    rowStore = PrinterController.getSingleLineText(false, small, terminalUser);//取餐号和桌台号餐牌号
                    rowStore.setScaleHeight(small);
                    rowStore.setAlign(Alignment.LEFT);
                    printerInterface.printRow(rowStore);
                }

                //下单时间
                PrintModelInfo modeOrderTime = printMode.get("orderTime");
                if (modeOrderTime.isShouldPrint()) {
                    rowStore = PrinterController.getSingleLineText(false, small, "下单时间:" + getTimeStr(order.getCreatedAt()));
                    rowStore.setAlign(Alignment.LEFT);
                    printerInterface.printRow(rowStore);
                    //                    printEnter(printerInterface, modeOrderTime.isPrintEnter());
                }
                if (!TextUtils.isEmpty(order.getHopeDeliverTime())) {
                    rowStore = PrinterController.getSingleLineText(false, 2, "期望送达时间:" + order.getHopeDeliverTime());
                    rowStore.setAlign(Alignment.LEFT);
                    printerInterface.printRow(rowStore);
                }
                printerInterface.printRow(new Separator(modeSeparator.getValue()));//分隔符
                //                printerInterface.printTable(PrinterController.getDoubleLineText(modeOrderId.isBold(), small, "操作终端:" + terminalName, "服务员:" + userName));
                //                if (modeOrderTime.isShouldPrint()) {
                //                    rowStore = PrinterController.getSingleLineText(false, small, "下单时间:" + getTimeStr(order.getCreatedAt()));
                //                    rowStore.setAlign(getAlian(modeOrderTime.getPrintAlian()));
                //                    printerInterface.printRow(rowStore);
                //                }
                String dishTitle = PrintUtils.getStr("菜品", "数量*单价", "金额", spaceWeight);
                printerInterface.printRow(createRow(false, small, dishTitle));

                printerInterface.printRow(new Separator(modeSeparator.getValue()));//分隔符

                List<OrderItem> itemList = order.getItemList();
                BigDecimal dishCount = new BigDecimal("0.00");
                for (int i = 0; i < itemList.size(); i++) {
                    OrderItem orderItem = itemList.get(i);
                    int logicQuantity = 0;
                    if (order.getPrinterType() == PRINTER_RETREAT_DISH_GUEST) {
                        logicQuantity = orderItem.getRejectedQuantity();
                    } else if (order.getPrinterType() == Constant.EventState.PRINTER_RETREAT_ORDER) {
                        logicQuantity = orderItem.getQuantity();
                        orderItem.setRejectedQuantity(orderItem.getQuantity());
                    } else {
                        logicQuantity = orderItem.getQuantity();
                    }
                    if (logicQuantity <= 0) {
                        continue;
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
                        String oi = PrintUtils.getStr("(" + orderItem.getDishName() + zc + ")", PrinterController.getDishCount(order, orderItem), money.toString(), spaceWeight);
                        TextRow oiRow = createRow(false, 2, oi);
                        oiRow.setScaleWidth(1);
                        oiRow.setScaleHeight(2);
                        printerInterface.printRow(oiRow);

                        String dishComment = orderItem.getComment();
                        if (!TextUtils.isEmpty(dishComment)) {
                            oiRow = createRow(false, 1, "备注:"+dishComment);
                            oiRow.setScaleWidth(1);
                            oiRow.setScaleHeight(1);
                            printerInterface.printRow(oiRow);
                        }

                        String disCountStr = getDisCountStr(orderItem.getMarketList(),orderItem.getQuantity());
                        if (!TextUtils.isEmpty(disCountStr)) {
                            oiRow = createRow(false, 1, disCountStr);
                            oiRow.setScaleWidth(1);
                            oiRow.setScaleHeight(1);
                            printerInterface.printRow(oiRow);
                        }

                        PrintModelInfo packageDetail = printMode.get("packageDetail");//套餐明细
                        if (packageDetail != null && packageDetail.isShouldPrint())//查询是否要打印套餐子项
                        {
                            // 套餐子项
                            List<Dish.Package> subItemList = orderItem.getSubItemList();
                            for (int a = 0; a < subItemList.size(); a++) {
                                String itemPrice = "";
                                if (subItemList.get(a).getItemPrice() != null && subItemList.get(a).getItemPrice().compareTo(BigDecimal.ZERO) > 0) {
                                    itemPrice = subItemList.get(a).getItemPrice().toString();
                                    if (TextUtils.isEmpty(itemPrice)) {
                                        itemPrice = "";
                                    }
                                }
                                itemPrice = "";
                                String oiSub = PrintUtils.getStr("[套]" + subItemList.get(a).getDishName(), PrinterController.getPackateCount(order, orderItem, subItemList.get(a)), itemPrice, spaceWeight);

                                TextRow oiSubRow = createRow(false, 1, oiSub);
                                printerInterface.printRow(oiSubRow);

                                //                                if (!isDishHaveMarket(orderItem.getMarketList())) {
                                if (!isGift && !isRefund) {
                                    PrintModelInfo dishCutomerize = printMode.get("dishCutomerize");//菜品定制项
                                    dishCount = dishCount.add(printDishPackageOption(false, printerInterface, subItemList.get(a), dishCutomerize));
                                }
                                //                                }
                            }
                        }
                    }
                    //普通菜品
                    else {
                        BigDecimal price = orderItem.getCost();
                        price = price == null ? new BigDecimal(0) : price;
                        int quantity = 0;
                        if (isRefund) {
                            quantity = orderItem.getRejectedQuantity();
                        } else {
                            quantity = orderItem.getQuantity();
                        }
                        //                        MyApplication.getInstance().ShowToast("price======="+price+"=========="+"quantity======="+quantity);
                        BigDecimal money = price.multiply(new BigDecimal(quantity)).setScale(2, BigDecimal.ROUND_DOWN);
                        dishCount = dishCount.add(money);
                        boolean isGift = false;
                        String zc = "";
                        if (money.compareTo(BigDecimal.ZERO) != 1) {
                            zc = "(赠)";
                            isGift = true;
                        }
                        String oi = PrintUtils.getStr(orderItem.getDishName() + zc, PrinterController.getDishCount(order, orderItem), money.toString(), spaceWeight);
                        TextRow oiRow = createRow(false, 2, oi);
                        oiRow.setScaleWidth(1);
                        oiRow.setScaleHeight(2);
                        printerInterface.printRow(oiRow);

                        String dishComment = orderItem.getComment();
                        if (!TextUtils.isEmpty(dishComment)) {
                            oiRow = createRow(false, 1, "备注:"+dishComment);
                            oiRow.setScaleWidth(1);
                            oiRow.setScaleHeight(1);
                            printerInterface.printRow(oiRow);
                        }

                        String disCountStr = getDisCountStr(orderItem.getMarketList(),orderItem.getQuantity());
                        if (!TextUtils.isEmpty(disCountStr)) {
                            oiRow = createRow(false, 1, disCountStr);
                            oiRow.setScaleWidth(1);
                            oiRow.setScaleHeight(1);
                            printerInterface.printRow(oiRow);
                        }

                        //                        if (!isDishHaveMarket(orderItem.getMarketList())) {
                        if (!isRefund) {
                            PrintModelInfo dishCutomerize = printMode.get("dishCutomerize");//菜品定制项
                            dishCount = dishCount.add(printDishOption(false, printerInterface, orderItem.optionList, dishCutomerize));
                        }
                        //                        }
                    }
                }

                printerInterface.printRow(new Separator(modeSeparator.getValue()));//分隔符

                if (order.getWaimaiOrderExtraDatas() != null && order.getWaimaiOrderExtraDatas().size() > 0) {
                    for (WaimaiOrderExtraData data : order.getWaimaiOrderExtraDatas()) {
                        String totalMoney = PrintUtils.getStr(data.getRemark(), "", data.getReduce_fee(), spaceWeight);
                        rowStore = PrinterController.getSingleLineText(false, small, totalMoney);
                        printerInterface.printRow(rowStore);
                    }
                }

                int textNum1 = 2;
                int textNum2 = 1;
                if (orderPrinterType == Constant.EventState.PRINTER_ORDER) {
                    textNum1 = 1;
                    textNum2 = 1;
                } else if (orderPrinterType == Constant.EventState.PRINT_CHECKOUT) {
                    textNum1 = 2;
                    textNum2 = 1;
                }

                if (type.equals("SALE_OUT")) {
                    TextRow waiMaiRow;
                    if (!TextUtils.isEmpty(order.getOuterOrderid())) {
                        String outerOrderId = "";
                        if(!TextUtils.isEmpty(order.getThirdPlatformOrderId()))
                        {
                            outerOrderId = order.getThirdPlatformOrderId();
                        }
                        else{
                            outerOrderId = order.getOuterOrderid();
                        }
                        waiMaiRow = createRow(true, textNum1, order.getSource() + "订单号:" + outerOrderId);
                        waiMaiRow.setAlign(Alignment.LEFT);
                        waiMaiRow.setScaleWidth(textNum2);
                        printerInterface.printRow(waiMaiRow);
                    }
                    TextRow rowNum = null;
                    if (!TextUtils.isEmpty(order.getCustomerName())) {
                        rowNum = createRow(true, textNum1, "顾客姓名:" + order.getCustomerName());
                        rowNum.setAlign(Alignment.LEFT);
                        rowNum.setScaleWidth(textNum2);
                        printerInterface.printRow(rowNum);
                    }

                    if (!TextUtils.isEmpty(order.getCustomerPhoneNumber())) {
                        rowNum = createRow(true, textNum1, "顾客电话:" + order.getCustomerPhoneNumber());
                        rowNum.setScaleWidth(textNum2);
                        rowNum.setAlign(Alignment.LEFT);
                        printerInterface.printRow(rowNum);
                    }

                    if (!TextUtils.isEmpty(order.getCustomerAddress())) {
                        rowNum = createRow(true, textNum1, "顾客地址:" + order.getCustomerAddress());
                        rowNum.setScaleWidth(textNum2);
                        rowNum.setAlign(Alignment.LEFT);
                        printerInterface.printRow(rowNum);
                    }

                    if (!TextUtils.isEmpty(getShippingFee(order.getShippingFee()).toString())) {
                        rowNum = createRow(true, textNum1, "外卖配送费: ￥" + getShippingFee(order.getShippingFee()));
                        rowNum.setScaleWidth(textNum2);
                        rowNum.setAlign(Alignment.LEFT);
                        printerInterface.printRow(rowNum);
                    }
                    if (order.getTakeoutFee() != null && order.getTakeoutFee().compareTo(BigDecimal.ZERO) == 1) {
                        rowNum = createRow(true, textNum1, "外带费: ￥" + order.getTakeoutFee());
                        rowNum.setScaleWidth(textNum2);
                        rowNum.setAlign(Alignment.LEFT);
                        printerInterface.printRow(rowNum);
                    }
                }


                if (orderPrinterType == Constant.EventState.PRINTER_ORDER) {
                    //                    if (order.getShippingFee() != null && order.getShippingFee().compareTo(new BigDecimal("0")) == 1) {
                    //                        String oi = PrintUtils.getStr("外卖配送费", "", "" + order.getShippingFee(), spaceWeight);
                    //                        TextRow oiRow = createRow(false, 2, oi);
                    //                        oiRow.setScaleWidth(1);
                    //                        oiRow.setScaleHeight(1);
                    //                        printerInterface.printRow(oiRow);
                    //                    }
                    //                    BigDecimal countMoney = dishCount;
                    //                    if (order.getShippingFee() != null) {
                    //                        countMoney = dishCount.add(order.getShippingFee());
                    //                    }
                    BigDecimal countMoney = new BigDecimal("0.00");
                    if (isRefund) {
                        countMoney = dishCount;
                    } else {
                        countMoney = new BigDecimal(order.getCost());
                    }
                    //                    if (orderPrinterType == Constant.EventState.PRINTER_RETREAT_DISH_GUEST || orderPrinterType == Constant.EventState.PRINTER_RETREAT_ORDER) {
                    //                        countMoney = dishCount;
                    //                    }
                    //                    else{
                    //                        countMoney = new BigDecimal(order.getCost());
                    //                    }

                    PrintModelInfo modeOrderTotal = printMode.get("orderTotal");//消费总计  最后要付的钱
                    String totalMoney = PrintUtils.getStr("合    计", "", countMoney.toString(), spaceWeight);
                    rowStore = PrinterController.getSingleLineText(modeOrderTotal.isBold(), small, totalMoney);
                    printerInterface.printRow(rowStore);
                } else if (orderPrinterType == Constant.EventState.PRINT_CHECKOUT) {

                    PrintModelInfo modeOrderTotal = printMode.get("orderTotal");//消费总计  最后要付的钱
                    BigDecimal countMoney = new BigDecimal("0.00");
                    //如果是退单或者是退菜,显示的合计金额应该是用计算出来的countmoney
                    //                    if (orderPrinterType == Constant.EventState.PRINTER_RETREAT_DISH_GUEST || orderPrinterType == Constant.EventState.PRINTER_RETREAT_ORDER) {
                    //                        countMoney = dishCount;
                    //                        isRefund = true;
                    //                    } else {
                    //                        countMoney = new BigDecimal(order.getCost());
                    //                        isRefund = false;
                    //                    }
                    if (isRefund) {
                        countMoney = dishCount;
                    } else {
                        countMoney = new BigDecimal(order.getCost());
                    }

                    if (order.getServiceMoney() != null && order.getServiceMoney().compareTo(BigDecimal.ZERO) > 0) {
                        String serviceMoney = PrintUtils.getStr("服 务 费:", "", order.getServiceMoney().toString(), spaceWeight);
                        rowStore = PrinterController.getSingleLineText(modeOrderTotal.isBold(), small, serviceMoney);
                        printerInterface.printRow(rowStore);
                    }


                    if (type.equals("TAKE_OUT") || type.equals("SALE_OUT") && !order.getTake_money().toString().equals("0")) {
                        String takeMoney = PrintUtils.getStr("打 包 费:", "", order.getTake_money().toString(), spaceWeight);
                        rowStore = PrinterController.getSingleLineText(modeOrderTotal.isBold(), small, takeMoney);
                        printerInterface.printRow(rowStore);
                    }

                    if (!isRefund) {
                        BigDecimal totalMoney = new BigDecimal(order.getTotal()).setScale(2, BigDecimal.ROUND_DOWN);
                        String totalMoneySth = PrintUtils.getStr("原价合计:", "", totalMoney.toString(), spaceWeight);
                        rowStore = PrinterController.getSingleLineText(modeOrderTotal.isBold(), small, totalMoneySth);
                        printerInterface.printRow(rowStore);

                        BigDecimal avtiveMoney = totalMoney.subtract(new BigDecimal(order.getCost()));

                        String activeMoney = PrintUtils.getStr("优惠总计:", "", avtiveMoney.setScale(2, BigDecimal.ROUND_DOWN).toString(), spaceWeight);
                        rowStore = PrinterController.getSingleLineText(modeOrderTotal.isBold(), small, activeMoney);
                        printerInterface.printRow(rowStore);
                    }

                    String costMoney = PrintUtils.getStr("实收合计:", "", countMoney.toString(), spaceWeight);
                    rowStore = PrinterController.getSingleLineText(modeOrderTotal.isBold(), small, costMoney);
                    printerInterface.printRow(rowStore);

                    BigDecimal payMoney = new BigDecimal("0.00").add(countMoney);
                    if (order.getGive_money().setScale(2, BigDecimal.ROUND_DOWN).compareTo(BigDecimal.ZERO) < 0) {
                        payMoney = payMoney.add(order.getGive_money().setScale(2, BigDecimal.ROUND_DOWN).abs());
                    }

                    String payMoneySth = PrintUtils.getStr("客    付:", "", payMoney.setScale(2, BigDecimal.ROUND_DOWN).toString(), spaceWeight);
                    rowStore = PrinterController.getSingleLineText(modeOrderTotal.isBold(), small, payMoneySth);
                    printerInterface.printRow(rowStore);

                    if (order.getGive_money().setScale(2, BigDecimal.ROUND_DOWN).compareTo(BigDecimal.ZERO) < 0) {
                        String giveMoney = PrintUtils.getStr("找    零:", "", order.getGive_money().setScale(2, BigDecimal.ROUND_DOWN).toString(), spaceWeight);
                        rowStore = PrinterController.getSingleLineText(modeOrderTotal.isBold(), small, giveMoney);
                        printerInterface.printRow(rowStore);
                    }
                }
                printerInterface.printRow(new Separator(modeSeparator.getValue()));//分隔符

                if (Constant.EventState.PRINTER_RETREAT_ORDER != order.getPrinterType() && PRINTER_RETREAT_DISH_GUEST != order.getPrinterType()) {
                    if (order.getPaymentList() != null && order.getPaymentList().size() > 0) {
                        printerInterface.printRow(createRow(false, 1, "支付方式"));
                        printerInterface.printRow(new Separator("-"));

                        for (PaymentList paymentList : order.getPaymentList()) {
                            int paymentTypeId = paymentList.getPaymentTypeId();
                            Payment payment = StoreInfor.getPaymentById(paymentTypeId);
                            if (payment != null) {
                                String payMentName = "";
                                if (paymentTypeId == 3) {
                                    //                                    payMentName = "会员支付(储值余额)";
                                    payMentName = "会员储值";
                                }
                                if (paymentTypeId == 4) {
                                    //                                    payMentName = "会员支付(优惠券)";
                                    payMentName = "会员优惠券";
                                }
                                if (paymentTypeId == 5) {
                                    //                                    payMentName = "会员支付(积分余额)";
                                    payMentName = "会员积分";
                                }
                                if (TextUtils.isEmpty(payMentName)) {
                                    payMentName = payment.getName();
                                }
                                String payType = PrintUtils.getStr(payMentName, "", paymentList.getValue().setScale(2, BigDecimal.ROUND_DOWN).toString(), spaceWeight);
                                printerInterface.printRow(createRow(false, small, payType));

                                if (!TextUtils.isEmpty(order.getPaymentNo()) && paymentTypeId != 0) {
                                    String paymentNo = PrintUtils.getStr("电子流水号:", "", order.getPaymentNo(), spaceWeight);
                                    rowStore = PrinterController.getSingleLineText(false, small, paymentNo);
                                    printerInterface.printRow(rowStore);
                                }
                            }
                        }
//                        printerInterface.printRow(new Separator(" "));

                        if (order.getAccountMember() != null) {
                            Account account = order.getAccountMember();
                            printerInterface.printRow(createRow(false, 1, "会员消费详情"));
                            printerInterface.printRow(createRow(false, 1, "会员卡号:" + account.getUno() + "(" + ToolsUtils.replacePhone(account.getPhone()) + ")"));
                            printerInterface.printRow(createRow(false, 1, "会员姓名:" + ToolsUtils.getStarString2(account.getName(), 1, 0)));
                            printerInterface.printRow(createRow(false, 1, "卡 等 级:" + account.getGradeName()));
                            printerInterface.printRow(createRow(false, 1, "消费金额:" + account.getMemberConsumeCost()));
                            printerInterface.printRow(createRow(false, 1, "(如有获赠积分卡券等,此与会员消费规则有关,详情咨询门店)"));
                        }

                    }
                }

                boolean isPrintQrcode = false;
                //设置的本地开关
                if (store.isPrintQRCode()) {
                    isPrintQrcode = true;
                }
                //补打的是否要打印二维码的订单开关
                if (order.isPrintQrcode()) {
                    isPrintQrcode = true;
                } else {
                    isPrintQrcode = false;
                }
                if (orderPrinterType == Constant.EventState.PRINT_CHECKOUT && isPrintQrcode) {
                    printerInterface.printRow(new Separator(" "));
                    TextRow rowNum = createRow(true, 1, "扫描下面二维码获取电子发票");
                    rowNum.setAlign(Alignment.CENTER);
                    printerInterface.printRow(rowNum);
                    Bitmap bitmap = null;
                    Bitmap qrcode = CreateImage.creatQRImage(getBarcodeUrl(order.getId() + ""), bitmap, 250, 250);
                    boolean isEnter = true;//是否打印回车
                    if (receiptPrinter != null) {
                        isEnter = true;
                    } else {
                        isEnter = false;
                    }
                    printerInterface.printBmp(new BitmapRow(qrcode), isEnter);
                }

                PrintModelInfo modeOrderQrCode = printMode.get("orderQrCode");//订单二维码
                if (orderPrinterType == Constant.EventState.PRINT_CHECKOUT && modeOrderQrCode.isShouldPrint()) {
                    TextRow rowNum = createRow(true, 1, "扫描下面二维码获取订单详情");
                    rowNum.setAlign(Alignment.CENTER);
                    printerInterface.printRow(rowNum);
                    Bitmap bitmap = null;
                    Bitmap qrcode = CreateImage.creatQRImage(getOrderQrCodeUrl(order.getId() + ""), bitmap, 250, 250);
                    boolean isEnter = true;//是否打印回车
                    if (receiptPrinter != null) {
                        isEnter = true;
                    } else {
                        isEnter = false;
                    }
                    printerInterface.printBmp(new BitmapRow(qrcode), isEnter);
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

                printerInterface.printRow(new Separator(modeSeparator.getValue()));//分隔符

                PrintModelInfo modeFreeText = printMode.get("freeText");//自定义文字
                if (modeFreeText.isShouldPrint()) {
                    rowStore = PrinterController.getSingleLineText(modeFreeText.isBold(), small, modeFreeText.getValue());
                    rowStore.setAlign(getAlian(modeFreeText.getPrintAlian()));
                    printerInterface.printRow(rowStore);
                }

                //全单备注
                if (!TextUtils.isEmpty(order.getComment())) {
                    rowStore = PrinterController.getSingleLineText(true, 2, "全单备注:" + order.getComment());
                    printerInterface.printRow(rowStore);
                }
                if (!TextUtils.isEmpty(order.getWaimaiHasInvoiced()) && order.getWaimaiHasInvoiced().equals("1")) {
                    rowStore = PrinterController.getSingleLineText(true, 1, "需要发票");
                    printerInterface.printRow(rowStore);

                    rowStore = PrinterController.getSingleLineText(true, 1, "发票抬头:" + order.getWaimaiInvoiceTitle());
                    printerInterface.printRow(rowStore);

                    rowStore = PrinterController.getSingleLineText(true, 1, "发票税号:" + order.getWaimaiTaxpayerId());
                    printerInterface.printRow(rowStore);
                }

                printerInterface.printRow(new Separator(" "));
                printerInterface.close();

                //printRecord.setPrint(true);
                printRecord.addCheckOutOrGuestReceiptCounts(orderPrinterType);
                printRecord.setPrintTime();
            }
        } catch (Exception e) {
            e.printStackTrace();
            closePrinterInterFace(printerInterface);
            //判断是预结小票还是结账小票还是客票退菜
            if (orderPrinterType == Constant.EventState.PRINT_CHECKOUT) {
                //预结小票
                if (order.getPrinterType() == Constant.EventState.ORDER_TYPE_ADVANCE) {
                    EventBus.getDefault().post(new PosEvent(Constant.EventState.ERR_PRINT_CASH, Constant.EventState.ORDER_TYPE_ADVANCE, Constant.EventState.ORDER_TYPE_ADVANCE, order, e.getMessage()));
                }
                //客票退菜
                else if (order.getPrinterType() == Constant.EventState.PRINTER_RETREAT_DISH_GUEST) {
                    EventBus.getDefault().post(new PosEvent(Constant.EventState.ERR_PRINT_CASH, Constant.EventState.PRINTER_RETREAT_DISH_GUEST, Constant.EventState.PRINTER_RETREAT_DISH_GUEST, order, e.getMessage()));
                }
                //客票退单
                else if (order.getPrinterType() == Constant.EventState.PRINTER_RETREAT_ORDER) {
                    EventBus.getDefault().post(new PosEvent(Constant.EventState.ERR_PRINT_CASH, Constant.EventState.PRINTER_RETREAT_ORDER, Constant.EventState.PRINTER_RETREAT_ORDER, order, e.getMessage()));
                }
                //结账小票
                else {
                    EventBus.getDefault().post(new PosEvent(Constant.EventState.ERR_PRINT_CASH, Constant.EventState.PRINT_CHECKOUT, Constant.EventState.PRINT_CHECKOUT, order, e.getMessage()));
                }
            }
            //客用单
            if (orderPrinterType == Constant.EventState.PRINTER_ORDER) {
                EventBus.getDefault().post(new PosEvent(Constant.EventState.ERR_PRINT_CASH, Constant.EventState.PRINTER_ORDER, order, e.getMessage()));
            }

        }
    }

    private BigDecimal getShippingFee(BigDecimal shippingFee) {
        if (shippingFee != null) {
            return shippingFee;
        }
        return new BigDecimal("0");
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

    private String getDisCountStr(List<MarketObject> marketList,int quantity) {
        StringBuffer sb = new StringBuffer();
        if (marketList != null && marketList.size() > 0) {
            int size = marketList.size();
            for (int i = 0; i < size; i++) {
                MarketObject market = marketList.get(i);
                if(market.getMarketType() == MarketType.MANUAL && market.getDiscountType() == 1)
                {
                    sb.append("    -" + market.getMarketName() + "-" + market.getReduceCash().multiply(new BigDecimal(quantity)).toString() + "元");
                }
                else{
                    sb.append("    -" + market.getMarketName() + "-" + market.getReduceCash().toString() + "元");
                }
                if (i != size - 1) {
                    sb.append("\n");
                }
            }
        }
        return sb.toString();
    }

    /**
     * 获取打印机的连接状态情况
     */
    public static void getPrinterListState() {
        try {
            if (getPrinterList() != null && getPrinterList().size() > 0) {
                for (Printer printer : getPrinterList()) {
                    //如果不是商米机器 并且不是标签打印机
                    if (PrinterVendor.fromName(printer.getVendor()) != PrinterVendor.SHANGMI_FIX && printer.getOutputType() != PrinterOutputType.LABEL && PrinterVendor.fromName(printer.getVendor()) != PrinterVendor.SPRT && PrinterVendor.fromName(printer.getVendor()) != PrinterVendor.UNKNOWN) {
                        checkPrinterState(printer);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取KDS的连接状态情况
     */
    public static void getKdsListState() {
        try {
            if (getKdsList() != null && getKdsList().size() > 0) {
                for (KDS kds : getKdsList()) {
                    checkKdsState(kds);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static TextRow createRow(boolean bold, int size, String content) {
        TextRow title = new TextRow(content);
        title.setScaleWidth(size);
        title.setScaleHeight(size);
        title.setBoldFont(bold);
        return title;
    }

    //创建一行， 内容为 左对齐 和 有对齐 的2列
    private Table createRow(boolean bold, int size, String left, String right) {
        Table table = new Table(2);
        TextRow row = new TextRow();

        row.setScaleHeight(size);
        row.setScaleWidth(size);
        row.setBoldFont(bold);
        row.addColumn(new Column(left, Alignment.LEFT));
        row.addColumn(new Column(right, Alignment.RIGHT));

        table.addRow(row);
        return table;
    }

    public static String ToSBC(String input) {
        //半角转全角：
        char[] c = input.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == 32) {
                c[i] = (char) 12288;
                continue;
            }
            if (c[i] < 127)
                c[i] = (char) (c[i] + 65248);
        }
        return new String(c);
    }

    /**
     * 打开钱箱
     */
    public void openCashBox() {
        ToolsUtils.writeUserOperationRecords("打开钱箱");
        if (PosSinUsbScreenController.getInstance().isPosSinScreenOpen()) {
            PosSinUsbScreenController.getInstance().openCashBox();
        } else {
            if (receiptPrinter == null) {
                //如果收银打印机是USB接口
                if (cashPrinterIsUSB) {
                    if (usbPrinter == null) {
                        Log.i("钱箱未连接", "钱箱未连接,请打开蓝牙");
                        MyApplication.getInstance().ShowToast("未配置钱箱!");
                    } else {
                        usbPrinter.openUSBCashBox();
                    }
                } else {
//                    MyApplication.getInstance().openSunMicashBox();
                }
            } else {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        PrinterInterface printerInterface = null;
                        try {
                            printerInterface = PrinterFactory.createPrinter(PrinterVendor.fromName(receiptPrinter.getVendor()), receiptPrinter.getIp(), receiptPrinter.getWidth());
                            if (printerInterface == null) {
                                Log.i("钱箱未连接", "钱箱未连接,请打开蓝牙");
                                MyApplication.getInstance().ShowToast("未配置钱箱!");
                                return;
                            }
                            printerInterface.init();
                            printerInterface.openCachBox();
                            printerInterface.closeNoCutPaper();
                        } catch (Exception e) {
                            e.printStackTrace();
                            try {
                                printerInterface.closeNoCutPaper();
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                        }
                    }
                }).start();
            }
        }
    }


    private int getKitModeCount(OrderItem oi, KitchenStall kitchenStall, boolean isRefundDish, boolean isRefunDishAll) {
        int modeCount = 0;
        if (kitchenStall.getKitchenPrintMode() == KitchenPrintMode.PER_DISH) {//多份一单
            modeCount = 1;
        } else //多份多单
        {
            if (isRefundDish) {
                modeCount = oi.getRejectedQuantity();
            } else if (isRefunDishAll) {
                oi.setRejectedQuantity(oi.getQuantity());
                modeCount = oi.getRejectedQuantity();
            } else {
                modeCount = oi.getQuantity();
            }
        }
        return modeCount;
    }

    /**
     * 打印套餐项中的定制项
     */
    private BigDecimal printDishPackageOption(boolean isKitReceipt, PrinterInterface printerInterface, Dish.Package subitem, PrintModelInfo dishCutomerize) {
        BigDecimal dishOption = new BigDecimal("0.00");
        if (subitem != null && subitem.optionList != null && subitem.optionList.size() > 0) {
            dishOption = dishOption.add(printDishOption(isKitReceipt, printerInterface, subitem.optionList, dishCutomerize).multiply(new BigDecimal(subitem.quantity))).add(new BigDecimal(subitem.extraCost).multiply(new BigDecimal(subitem.quantity)));
        }
        return dishOption;
    }

    private boolean isDishHaveMarket(List<MarketObject> marketList) {
        boolean haveDishMarket = false;
        if (marketList != null && marketList.size() > 0) {
            haveDishMarket = true;
        }
        return haveDishMarket;
    }


    private void closePrinterInterFace(PrinterInterface printerInterface) {
        if (printerInterface != null) {
            try {
                printerInterface.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void threadSleep(long time) {
        try {
            Thread.sleep(time * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
