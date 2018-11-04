package cn.acewill.mobile.pos.printer.usb;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;

import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.google.zxing.qrcode.encoder.ByteMatrix;
import com.google.zxing.qrcode.encoder.Encoder;
import com.google.zxing.qrcode.encoder.QRCode;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Hashtable;
import java.util.List;

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
import cn.acewill.mobile.pos.utils.PrintUtils;
import cn.acewill.mobile.pos.utils.ToolsUtils;

import static cn.acewill.mobile.pos.utils.TimeUtil.getTimeStr;


/**
 * Created by DHH on 2017/9/18.
 */

public class PosSinUsbPrinter {

    private static String getBarcodeUrl(String orderId) {
        PosInfo posInfo = PosInfo.getInstance();
        String mob = "mobilereport/invoice.html?";
        String storeInfo = "appId=" + posInfo.getAppId() + "&brandId=" + posInfo.getBrandId() + "&storeId=" + posInfo.getStoreId() + "&orderId=" + orderId;
        String url = posInfo.getServerUrl() + mob + storeInfo;
        return url;
    }

    private static String getOrderQrCodeUrl(String orderId) {
        PosInfo posInfo = PosInfo.getInstance();
        String mob = "api/orders/checkServeStatus?";
        String storeInfo = "appId=" + posInfo.getAppId() + "&brandId=" + posInfo.getBrandId() + "&storeId=" + posInfo.getStoreId() + "&orderId=" + orderId;
        String url = posInfo.getServerUrl() + mob + storeInfo;
        return url;
    }

    private static String getDisCountStr(List<MarketObject> marketList) {
        StringBuffer sb = new StringBuffer();
        if (marketList != null && marketList.size() > 0) {
            int size = marketList.size();
            for (int i = 0; i < size; i++) {
                MarketObject market = marketList.get(i);
                sb.append(market.getMarketName() + "-" + market.getReduceCash().toString() + "￥");
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
    private static BigDecimal printDishPackageOption(boolean isKitReceipt, UsbPrinter posSinUsbPrint, Dish.Package subitem) {
        BigDecimal dishOption = new BigDecimal("0.00");
        if (subitem != null && subitem.optionList != null && subitem.optionList.size() > 0) {
            dishOption = dishOption.add(printDishOption(isKitReceipt, posSinUsbPrint, subitem.optionList).multiply(new BigDecimal(subitem.quantity))).add(new BigDecimal(subitem.extraCost).multiply(new BigDecimal(subitem.quantity)));
        }
        return dishOption;
    }

    private static BigDecimal getShippingFee(BigDecimal shippingFee) {
        if (shippingFee != null) {
            return shippingFee;
        }
        return new BigDecimal("0");
    }

    /**
     * 打印普通菜品中的定制项
     *
     * @param optionList
     */
    private static BigDecimal printDishOption(boolean isKitReceipt, UsbPrinter posSinUsbPrint, List<Option> optionList) {
        BigDecimal dishOption = new BigDecimal("0.00");
        try {
            if (posSinUsbPrint != null && optionList != null && optionList.size() > 0) {
                if (optionList != null && optionList.size() > 0) {
                    StringBuffer sb = new StringBuffer();
                    for (Option option : optionList) {
                        if (option.getPrice().compareTo(new BigDecimal("0")) == 0) {
                            sb.append(option.name + "、");
                        } else {
                            dishOption = dishOption.add(option.getPrice());
                            sb.append(option.name + "(" + option.getPrice() + "￥)、");
                        }
                    }
                    //                    TextRow oiRow = createRow(false, 1, "    " + sb.toString());
                    //
                    //                    if (isKitReceipt) {
                    //                        oiRow.setScaleWidth(2);
                    //                        oiRow.setScaleHeight(2);
                    //                    }
                    //                    printerInterface.printRow(oiRow);

                    printAlign(posSinUsbPrint,UsbPrinter.ALIGNMENT.LEFT);
                    printText(posSinUsbPrint,UsbPrinter.FONT.FONT_A,false,false,false,"   " + sb.toString());
                    printEnter(posSinUsbPrint);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dishOption;
    }

    private static void printText(UsbPrinter posSinUsbPrint,int alignMentType,boolean isEnter,String text)
    {
        if(posSinUsbPrint != null)
        {
            if(alignMentType == 1)
            {
                printAlign(posSinUsbPrint,UsbPrinter.ALIGNMENT.LEFT);
            }
            else if(alignMentType == 2)
            {
                printAlign(posSinUsbPrint,UsbPrinter.ALIGNMENT.CENTER);
            }
            else if(alignMentType == 3)
            {
                printAlign(posSinUsbPrint,UsbPrinter.ALIGNMENT.RIGHT);
            }
            else{
                printAlign(posSinUsbPrint,UsbPrinter.ALIGNMENT.LEFT);
            }
            printText(posSinUsbPrint,UsbPrinter.FONT.FONT_A,false,false,false,text);
            if(isEnter)
            {
                printEnter(posSinUsbPrint);
            }
        }
    }

    private static String returnStr(String str)
    {
        return TextUtils.isEmpty(str) ? "":str;
    }

    public static void printWorkShiftNew(UsbPrinter posSinUsbPrint, WorkShiftNewReport workShiftReport, String printStr) {
        if(posSinUsbPrint == null)
        {
            return;
        }
        PosInfo posInfo = PosInfo.getInstance();

        printText(posSinUsbPrint,2,true,posInfo.getBrandName());

        printText(posSinUsbPrint,2,true,printStr + "打印单");

        printText(posSinUsbPrint,2,true,printStr + "人 : " + posInfo.getRealname());

        if (!TextUtils.isEmpty(workShiftReport.getWorkShiftName())) {
            printText(posSinUsbPrint,1,true,printStr + "班次名称 : " + workShiftReport.getWorkShiftName());
        }

        if (!TextUtils.isEmpty(workShiftReport.getStartTime())) {
            printText(posSinUsbPrint,1,true,printStr + "开始时间 : " + workShiftReport.getStartTime());
        }

        if (!TextUtils.isEmpty(workShiftReport.getStartTime())) {
            printText(posSinUsbPrint,1,true,printStr + "结束时间 : " + workShiftReport.getEndTime());
        }

        if (workShiftReport.getStartWorkShiftCash() > 0) {
            printText(posSinUsbPrint,1,true,"开班钱箱余额 : " + workShiftReport.getStartWorkShiftCash());
        }

        if (workShiftReport.getEndWorkShiftCash() > 0) {
            printText(posSinUsbPrint,1,true,"交班钱箱余额 : " + workShiftReport.getEndWorkShiftCash());
        }

        printText(posSinUsbPrint,1,true,"小票打印时间 : " + getTimeStr(System.currentTimeMillis()));

        createWorkShiftNewItem(posSinUsbPrint,workShiftReport);

        String submitCash = PrintUtils.getStr("应交现金:", "", String.valueOf(workShiftReport.getSubmitCash() + " / 元"), 32);
        printText(posSinUsbPrint,1,true,submitCash);

        String differenceCash = PrintUtils.getStr("差额:", "", String.valueOf(workShiftReport.getDifferenceCash() + " / 元"), 32);
        printText(posSinUsbPrint,1,true,differenceCash);

        printEnter(posSinUsbPrint);
        printEnter(posSinUsbPrint);
        printEnter(posSinUsbPrint);
        printEnter(posSinUsbPrint);

        cutPaper(posSinUsbPrint);//切纸
        close(posSinUsbPrint);
    }

    public static void printWorkShift(UsbPrinter posSinUsbPrint, WorkShiftReport workShiftReport, String printStr) {
        if(posSinUsbPrint == null)
        {
            return;
        }
        PosInfo posInfo = PosInfo.getInstance();

        printText(posSinUsbPrint,2,true,posInfo.getBrandName());

        printText(posSinUsbPrint,2,true,printStr + "打印单");

        printText(posSinUsbPrint,2,true,printStr + "人 : " + posInfo.getRealname());

        if (!TextUtils.isEmpty(workShiftReport.getStartTime())) {
            printText(posSinUsbPrint,1,true,printStr + "开始时间 : " + workShiftReport.getStartTime());
        }

        if (!TextUtils.isEmpty(workShiftReport.getStartTime())) {
            printText(posSinUsbPrint,1,true,printStr + "结束时间 : " + workShiftReport.getEndTime());
        }

        printText(posSinUsbPrint,1,true,"小票打印时间 : " + getTimeStr(System.currentTimeMillis()));

        createWorkShiftItem(posSinUsbPrint,workShiftReport);

        printEnter(posSinUsbPrint);
        printEnter(posSinUsbPrint);
        printEnter(posSinUsbPrint);
        printEnter(posSinUsbPrint);

        cutPaper(posSinUsbPrint);//切纸
        close(posSinUsbPrint);
    }

    private static void createWorkShiftNewItem(UsbPrinter posSinUsbPrint,WorkShiftNewReport workShiftReport) {
        try {
            for (WorkShiftNewReport.WorkShiftCategoryDataList itemCategorySalesDataList : workShiftReport.getWorkShiftCategoryDataList()) {
                printSeparator(posSinUsbPrint,"-");
                printText(posSinUsbPrint,2,true,returnStr(itemCategorySalesDataList.getName()));
                printSeparator(posSinUsbPrint,"-");

                String dishTitle = PrintUtils.getStr("名称", "数量", "金额", 32);
                printText(posSinUsbPrint,1,true,dishTitle);
                printSeparator(posSinUsbPrint,"-");

                for (WorkShiftNewReport.WorkShiftCategoryDataList.WorkShiftItemDatas itemSalesDataList : itemCategorySalesDataList.getWorkShiftItemDatas()) {
                    String itemInfo = PrintUtils.getStr(returnStr(itemSalesDataList.getName()), returnStr(String.valueOf(itemSalesDataList.getItemCounts())), returnStr(String.valueOf(itemSalesDataList.getTotal())), 30);
                    printText(posSinUsbPrint,1,true,itemInfo);
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void createWorkShiftItem(UsbPrinter posSinUsbPrint,WorkShiftReport workShiftReport) {
        try {
            for (WorkShiftReport.ItemCategorySalesDataList itemCategorySalesDataList : workShiftReport.getItemCategorySalesDataList()) {
                printSeparator(posSinUsbPrint,"-");
                printText(posSinUsbPrint,2,true,returnStr(itemCategorySalesDataList.getName()));
                printSeparator(posSinUsbPrint,"-");

                String dishTitle = PrintUtils.getStr("名称", "数量", "金额", 32);
                printText(posSinUsbPrint,1,true,dishTitle);
                printSeparator(posSinUsbPrint,"-");

                for (WorkShiftReport.ItemCategorySalesDataList.ItemSalesDataList itemSalesDataList : itemCategorySalesDataList.getItemSalesDataList()) {
                    String itemInfo = PrintUtils.getStr(returnStr(itemSalesDataList.getName()), returnStr(String.valueOf(itemSalesDataList.getItemCounts())), returnStr(String.valueOf(itemSalesDataList.getTotal())), 30);
                    printText(posSinUsbPrint,1,true,itemInfo);
                }
            }

            printSeparator(posSinUsbPrint,"-");
            printText(posSinUsbPrint,2,true,"客单价统计");
            printSeparator(posSinUsbPrint,"-");

            WorkShiftReport.PctData pctData = workShiftReport.getPctData();
            String orderCount = PrintUtils.getStr("订单总数", "", returnStr(String.valueOf(pctData.getOrderCounts()) + " /条"), 28);
            printText(posSinUsbPrint,1,true,orderCount);

            String userCount = PrintUtils.getStr("客人总数", "", returnStr(String.valueOf(pctData.getCustomerCounts()) + " /人"), 28);
            printText(posSinUsbPrint,1,true,userCount);

            String orderTotal = PrintUtils.getStr("订单均价", "", " ￥ " + returnStr(String.valueOf(pctData.getPricePerOrder()) + " / 元"), 28);
            printText(posSinUsbPrint,1,true,orderTotal);

            String guestTotal = PrintUtils.getStr("客单价", "", " ￥ " + returnStr(String.valueOf(pctData.getPricePerCustomer()) + " / 元"), 28);
            printText(posSinUsbPrint,1,true,guestTotal);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void printCashReceipt(UsbPrinter posSinUsbPrint, Order order, ArrayMap<String, PrintModelInfo> printMode, int orderPrinterType, Store store) {
        if(posSinUsbPrint == null)
        {
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
        String brandNameForStoreName = PrinterController.getBrandNameAndStoreName(printMode);
        PrintModelInfo modeTitle = printMode.get("brandName");//品牌名
        if (!TextUtils.isEmpty(brandNameForStoreName)) {
            printEnter(posSinUsbPrint);
            printText(posSinUsbPrint,2,true,brandNameForStoreName);
        }
        printEnter(posSinUsbPrint);

        PrintModelInfo modeTicketType = printMode.get("ticketType");//小票类型
        if (modeTicketType.isShouldPrint()) {
            String ticketType = "";
            if (Constant.EventState.PRINT_WAIMAI == order.getPrinterType()) {
                ticketType = orderType;
            } else {
                ticketType = modeTicketType.getValue();
            }
            printText(posSinUsbPrint,2,true,ticketType);
        }

        boolean isRefund = false;//是否是退单  退菜模式
        if (order.getPrinterType() == Constant.EventState.PRINTER_RETREAT_DISH_GUEST || order.getPrinterType() == Constant.EventState.PRINTER_RETREAT_ORDER) {
            isRefund = true;
        } else {
            isRefund = false;
        }

        PrintModelInfo modeSeparator = printMode.get("separator");
        printSeparator(posSinUsbPrint, modeSeparator.getValue());//分隔符
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
            printAlign(posSinUsbPrint,UsbPrinter.ALIGNMENT.LEFT);
            printText(posSinUsbPrint,UsbPrinter.FONT.FONT_A,false,false,false, eatType + eatNumber + "      " + orderType);
        }

        PrintModelInfo modeOrderId = printMode.get("orderId");//订单号
        PrintModelInfo modeCustomerCount = printMode.get("customerCount");//人数
        //                printerInterface.printTable(PrinterController.getDoubleLineText(modeCustomerCount.isBold(), small, "顾客人数:" + order.getCustomerAmount(), "订单号:" + order.getId()));
        //订单号
        if (modeOrderId.isShouldPrint()) {
            printText(posSinUsbPrint,1,true,"订单号:" + order.getId());
        }
        //人数
        if (modeCustomerCount.isShouldPrint() && order.getCustomerAmount() > 0) {
            printText(posSinUsbPrint,1,true,"顾客人数:" + order.getCustomerAmount());
        }

        //操作终端
        String terminalName = posInfo.getTerminalName();
        printText(posSinUsbPrint,1,true,"操作终端:" + terminalName);

        //操作人
        String userName = posInfo.getRealname();//服务员
        printText(posSinUsbPrint,1,true,"操作人:" + userName);

        //下单时间
        PrintModelInfo modeOrderTime = printMode.get("orderTime");
        if (modeOrderTime.isShouldPrint()) {
            printText(posSinUsbPrint,1,true,"下单时间:" + getTimeStr(order.getCreatedAt()));
        }

        printSeparator(posSinUsbPrint, modeSeparator.getValue());//分隔符
        String dishTitle = PrintUtils.getStr("菜品", "数量单价", "金额", 32);
        printAlign(posSinUsbPrint,UsbPrinter.ALIGNMENT.LEFT);
        printText(posSinUsbPrint,UsbPrinter.FONT.FONT_A,false,false,false,dishTitle);
        printSeparator(posSinUsbPrint, modeSeparator.getValue());

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

                printText(posSinUsbPrint,1,true,oi);

                printAlign(posSinUsbPrint,UsbPrinter.ALIGNMENT.LEFT);
                printText(posSinUsbPrint,UsbPrinter.FONT.FONT_A,false,false,false,disCountStr);

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
                        printText(posSinUsbPrint,1,true,oiSub);

                        if (!isGift && !isRefund) {
                            dishCount = dishCount.add(printDishPackageOption(false, posSinUsbPrint, subItemList.get(a)));
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

                printText(posSinUsbPrint,1,true,oi);

                printAlign(posSinUsbPrint,UsbPrinter.ALIGNMENT.LEFT);
                printText(posSinUsbPrint,UsbPrinter.FONT.FONT_A,false,false,false,disCountStr);

                if(!isRefund)
                {
                    dishCount = dishCount.add(printDishOption(false, posSinUsbPrint, orderItem.optionList));
                }
            }
        }

        printSeparator(posSinUsbPrint, modeSeparator.getValue());;//分隔符

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
            printText(posSinUsbPrint,1,true,totalMoney);
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
            printText(posSinUsbPrint,1,true,serviceMoney);

            if (type.equals("TAKE_OUT") || type.equals("SALE_OUT")) {
                String takeMoney = PrintUtils.getStr("打包费:", "", order.getTake_money().toString(), 32);
                printText(posSinUsbPrint,1,true,takeMoney);
            }
            if (type.equals("SALE_OUT")) {
                if(!TextUtils.isEmpty(order.getCustomerName()))
                {
                    printText(posSinUsbPrint,1,true,"顾客姓名:" + order.getCustomerName());
                }
                if(!TextUtils.isEmpty(order.getCustomerPhoneNumber()))
                {
                    printText(posSinUsbPrint,1,true,"顾客电话:" + order.getCustomerPhoneNumber());
                }
                if(!TextUtils.isEmpty(order.getCustomerAddress()))
                {
                    printText(posSinUsbPrint,1,true,"顾客地址:" + order.getCustomerAddress());
                }
                if(!TextUtils.isEmpty(getShippingFee(order.getShippingFee()).toString()))
                {
                    printText(posSinUsbPrint,1,true,"外卖配送费: ￥" + getShippingFee(order.getShippingFee()));
                }
                if(order.getTakeoutFee() != null && order.getTakeoutFee().compareTo(BigDecimal.ZERO) == 1)
                {
                    printText(posSinUsbPrint,1,true,"外带费: ￥" + order.getTakeoutFee());
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
            printText(posSinUsbPrint,1,true,activeMoney);

            String giveMoney = PrintUtils.getStr("找零:", "", order.getGive_money().setScale(2, BigDecimal.ROUND_DOWN).toString(), 32);
            printText(posSinUsbPrint,1,true,giveMoney);

            String totalMoney = PrintUtils.getStr("合计", "", countMoney.toString(), 32);
            printText(posSinUsbPrint,1,true,totalMoney);
        }
        printSeparator(posSinUsbPrint, modeSeparator.getValue());

        if (order.getPaymentList() != null && order.getPaymentList().size() > 0) {
            printText(posSinUsbPrint,1,true,"支付方式");
            printSeparator(posSinUsbPrint, "-");

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
                    String payType = PrintUtils.getStr(payMentName, "", paymentList.getValue().setScale(2, BigDecimal.ROUND_DOWN).toString(), 32);
                    printText(posSinUsbPrint,1,true,payType);
                }
            }
        }

        if (!TextUtils.isEmpty(order.getPaymentNo())) {
            String paymentNo = PrintUtils.getStr("电子流水号", "", order.getPaymentNo(), 32);
            printText(posSinUsbPrint,1,true,paymentNo);
        }

        if (orderPrinterType == Constant.EventState.PRINT_CHECKOUT && store.isPrintQRCode()) {
//            esc.addPrintAndLineFeed();
//            printAlign(esc, EscCommand.JUSTIFICATION.CENTER);// 设置打印居中
//            printMode(esc, false, false);// 设置为倍高倍宽
//            printText(esc, "扫描下面二维码获取电子发票"); // 打印文字
//            esc.addPrintAndLineFeed();
//            printAlign(esc, EscCommand.JUSTIFICATION.CENTER);// 设置打印居中
//            Bitmap bitmap = null;
//            Bitmap qrcode = CreateImage.creatQRImage(getBarcodeUrl(order.getId() + ""), bitmap, 250, 250);
//            esc.addRastBitImage(qrcode, 384, 0); // 打印图片
//            printEnter(esc);

//            printAlign(posSinUsbPrint,UsbPrinter.ALIGNMENT.CENTER);
//            printText(posSinUsbPrint,UsbPrinter.FONT.FONT_A,false,false,false,"扫描下面二维码获取电子发票");
//            printEnter(posSinUsbPrint);
//            printBitmapCode(posSinUsbPrint,getBarcodeUrl(order.getId() + ""));
//            printEnter(posSinUsbPrint);
        }
        PrintModelInfo modeOrderQrCode = printMode.get("orderQrCode");//订单二维码
        if (orderPrinterType == Constant.EventState.PRINT_CHECKOUT && modeOrderQrCode.isShouldPrint()) {
//            esc.addPrintAndLineFeed();
//            printAlign(esc, EscCommand.JUSTIFICATION.CENTER);// 设置打印居中
//            printMode(esc, false, false);// 设置为倍高倍宽
//            printText(esc, "扫描下面二维码获取订单详情"); // 打印文字
//            esc.addPrintAndLineFeed();
//            printAlign(esc, EscCommand.JUSTIFICATION.CENTER);// 设置打印居中
//            Bitmap bitmap = null;
//            Bitmap qrcode = CreateImage.creatQRImage(getOrderQrCodeUrl(order.getId() + ""), bitmap, 250, 250);
//            esc.addRastBitImage(qrcode, 384, 0); // 打印图片
//            printEnter(esc);
//            printAlign(posSinUsbPrint,UsbPrinter.ALIGNMENT.CENTER);
//            printText(posSinUsbPrint,UsbPrinter.FONT.FONT_A,false,false,false,"扫描下面二维码获取订单详情");
//            printEnter(posSinUsbPrint);
//            printBitmapCode(posSinUsbPrint,getOrderQrCodeUrl(order.getId() + ""));
//            printEnter(posSinUsbPrint);
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

        printSeparator(posSinUsbPrint, modeSeparator.getValue());


        PrintModelInfo modeFreeText = printMode.get("freeText");//自定义文字
        if (modeFreeText.isShouldPrint()) {
            printText(posSinUsbPrint,2,true,modeFreeText.getValue());
        }

        //全单备注
        if (!TextUtils.isEmpty(order.getComment())) {
            printText(posSinUsbPrint,2,true,"全单备注:" + order.getComment());
        }
        printEnter(posSinUsbPrint);
        printEnter(posSinUsbPrint);
        printEnter(posSinUsbPrint);
        printEnter(posSinUsbPrint);

        cutPaper(posSinUsbPrint);//切纸
        close(posSinUsbPrint);
    }

    private static void close(UsbPrinter posSinUsbPrint)
    {
        if(posSinUsbPrint != null)
        {
            posSinUsbPrint.close();
        }
    }

    private static void cutPaper(UsbPrinter posSinUsbPrint)
    {
        if(posSinUsbPrint != null)
        {
            try {
                posSinUsbPrint.cutPaper();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private static final byte[] CMD_LINE_FEED = { 0x0a };
    private static final byte[] CMD_INIT = { 0x1B, 0x40};
    private static final byte[] CMD_ALIGN_CENTER = {0x1B, 0x61, 1};
    private static final byte[] CMD_FEED_AND_CUT = {0x0A, 0x0A, 0x0A, 0x0A, 0x1D, 0x56, 0x01};
    private static void printBitmapCode(UsbPrinter posSinUsbPrint,String url)
    {
        if(posSinUsbPrint != null)
        {
            try {
                Bitmap bmp = encodeQRCode(url, ErrorCorrectionLevel.L, 8);

                byte[] data = genBitmapCode(bmp, false, false);
                posSinUsbPrint.write(CMD_INIT);
                posSinUsbPrint.write(CMD_LINE_FEED);
                posSinUsbPrint.write(CMD_ALIGN_CENTER);
                posSinUsbPrint.write(data);
                posSinUsbPrint.write(CMD_FEED_AND_CUT);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (WriterException e) {
                e.printStackTrace();
            }
        }
    }



    private static Bitmap encodeQRCode(String text, ErrorCorrectionLevel errorCorrectionLevel,
                                       int scale) throws WriterException {

        Hashtable<EncodeHintType, Object> hints = new Hashtable<EncodeHintType, Object>();
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        hints.put(EncodeHintType.ERROR_CORRECTION, errorCorrectionLevel);

//        QRCode code = new QRCode();

        QRCode code = Encoder.encode(text, errorCorrectionLevel,hints);

        final ByteMatrix m = code.getMatrix();
        final int mw = m.getWidth();
        final int mh = m.getHeight();

        // 转为单色图
        final int IMG_WIDTH = mw*scale;
        final int IMG_HEIGHT = mh*scale;

        Bitmap bmp = Bitmap.createBitmap(IMG_WIDTH, IMG_HEIGHT, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmp);
        Paint p = new Paint();

        c.drawColor(Color.WHITE);
        p.setColor(Color.BLACK);

        for (int y = 0; y < mh; y++) {
            for (int x = 0; x < mw; x++) {
                if (m.get(x, y) == 1) {
                    c.drawRect(x*scale, y*scale,
                            (x+1)*scale, (y+1)*scale, p);
                }
            }
        }

        return bmp;
    }

//    public static final int MAX_BIT_WIDTH = 384;
    public static final int MAX_BIT_WIDTH = 576;
    private static byte[] genBitmapCode(Bitmap bm, boolean doubleWidth, boolean doubleHeight) {
//        int w = bm.getWidth();
//        int h = bm.getHeight();
//        if(w > MAX_BIT_WIDTH)
//            w = MAX_BIT_WIDTH;
//        int bitw = ((w+7)/8)*8;
//        int bith = h;
//        int pitch = bitw / 8;
//        byte[] cmd = {0x1D, 0x76, 0x30, 0x00, (byte)(pitch&0xff), (byte)((pitch>>8)&0xff), (byte) (bith&0xff), (byte) ((bith>>8)&0xff)};
//        byte[] bits = new byte[bith*pitch];
//
//        // 倍宽
//        if(doubleWidth)
//            cmd[3] |= 0x01;
//        // 倍高
//        if(doubleHeight)
//            cmd[3] |= 0x02;
//
//        for (int y = 0; y < h; y++) {
//            for (int x = 0; x < w; x++) {
//                int color = bm.getPixel(x, y);
//                if ((color&0xFF) < 128) {
//                    bits[y * pitch + x/8] |= (0x80 >> (x%8));
//                }
//            }
//        }
//        ByteBuffer bb = ByteBuffer.allocate(cmd.length+bits.length);
//        bb.put(cmd);
//        bb.put(bits);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
//        data2 = baos.toByteArray();
        return baos.toByteArray();
    }


    /**
     * 打印文字
     * @param posSinUsbPrint
     * @param font
     * @param bold
     * @param doubleHeight
     * @param doubleWidth
     */
    private static void printText(UsbPrinter posSinUsbPrint,UsbPrinter.FONT font,boolean bold,
                                  boolean doubleHeight, boolean doubleWidth,String string)
    {
        if(posSinUsbPrint != null)
        {
            try {
                posSinUsbPrint.selectFont(font);
                posSinUsbPrint.setFontStyleBold(bold);
                posSinUsbPrint.setFontStyleUnderline(false);
                posSinUsbPrint.setFontSize(doubleWidth?2:1, doubleHeight?2:1);
                posSinUsbPrint.write(string.getBytes("GB2312"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 打印换行
     */
    private static void printEnter(UsbPrinter posSinUsbPrint) {
        if (posSinUsbPrint != null) {
            try {
                posSinUsbPrint.feedLine(1);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 设置文字对齐方式
     */
    private static void printAlign(UsbPrinter posSinUsbPrint,UsbPrinter.ALIGNMENT alignment)
    {
        if (posSinUsbPrint != null) {
                try {
                    if(alignment == UsbPrinter.ALIGNMENT.LEFT)
                    {
                        posSinUsbPrint.selectAlignment(UsbPrinter.ALIGNMENT.LEFT);
                    }
                    else if(alignment == UsbPrinter.ALIGNMENT.CENTER)
                    {
                        posSinUsbPrint.selectAlignment(UsbPrinter.ALIGNMENT.CENTER);
                    }
                    else if(alignment == UsbPrinter.ALIGNMENT.RIGHT)
                    {
                        posSinUsbPrint.selectAlignment(UsbPrinter.ALIGNMENT.RIGHT);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }

    /**
     * 打印分隔符
     *
     * @param posSinUsbPrint
     * @param separator
     */
    private static void printSeparator(UsbPrinter posSinUsbPrint, String separator) {
        if (posSinUsbPrint != null) {
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < 32; i++) {
                sb.append(separator);
            }
            printText(posSinUsbPrint,UsbPrinter.FONT.FONT_A,false,false,false,sb.toString());
        }
    }

}
