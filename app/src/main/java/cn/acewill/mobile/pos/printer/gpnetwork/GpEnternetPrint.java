package cn.acewill.mobile.pos.printer.gpnetwork;


import android.text.TextUtils;

import java.io.IOException;
import java.math.BigDecimal;

import cn.acewill.mobile.pos.common.StoreInfor;
import cn.acewill.mobile.pos.model.dish.CookingMethod;
import cn.acewill.mobile.pos.model.dish.Flavor;
import cn.acewill.mobile.pos.model.dish.Option;
import cn.acewill.mobile.pos.model.order.Order;
import cn.acewill.mobile.pos.model.order.OrderItem;
import cn.acewill.mobile.pos.printer.Printer;
import cn.acewill.mobile.pos.printer.PrinterVendor;
import cn.acewill.mobile.pos.utils.TimeUtil;


/**
 * 佳博网口标签打印
 * Created by aqw on 2016/12/29.
 */
public class GpEnternetPrint {

    public static final int port = 9100;

    public static void testGpPrint(Printer printer) throws IOException {
        boolean isSprtPrint = false;
        if (PrinterVendor.fromName(printer.getVendor()) == PrinterVendor.SPRT) {
            isSprtPrint = true;
        }
        GpPrintCommand gp = new GpPrintCommand();

        gp.openport(printer.getIp(), port);

        gp.setup(60, 30, 4, 4, 0, 2, 0);
        gp.clearbuffer();
        gp.setDirection(1);
        gp.setReference(0, 0);
        gp.setTear("ON");

        gp.isSprt(isSprtPrint);
        String count = "  1/1";
        gp.printerfont(0, 1, 1, "订单号:0010  " + count+" 堂食");//第一行
        gp.printerfont(1, 1, 1, "时间:" + TimeUtil.getHourStr());//第二行
        gp.printerfont(2, 1, 2, "测试菜品");//第三行
        gp.printerfont(3, 1, 1, "价格:" + "0.01" + "￥");//第四行
        gp.printerfont(4, 1, 1, "定制项:" + "甜");//第五行

        gp.printlabel(1, 1);//打印出缓冲区的数据,第一个参数是打印的份数，第二个是每份打印的张数
        gp.clearbuffer();
        gp.closeport();
    }

    /**
     * 打印饮料
     *
     * @param orderItem   品项
     * @param printer printer 打印机对象
     * @param lableHeight 标签纸高度/mm
     * @throws IOException
     */
    public static void gpPrint(final OrderItem orderItem, Printer printer, Integer lableHeight, int printIndex, int orderDishCount, Order order) throws IOException {
        boolean isSprtPrint = false;
        if (PrinterVendor.fromName(printer.getVendor()) == PrinterVendor.SPRT) {
            isSprtPrint = true;
        }
        GpPrintCommand gp = new GpPrintCommand();

        gp.openport(printer.getIp(), port);

        if (lableHeight == null || lableHeight == 0) {
            lableHeight = 26;//默认26mm,按最小尺寸来
        }

        gp.setup(60, lableHeight, 4, 4, 0, 2, 0);
        gp.clearbuffer();
        gp.setDirection(1);
        gp.setReference(0, 0);
        gp.setTear("ON");

        printOrderItem(gp, orderItem, printIndex,orderDishCount, isSprtPrint,order,lableHeight);
    }

    //打印标签明细
    public static void printOrderItem(GpPrintCommand gp, OrderItem orderItem, int printerIndex, int orderDishCount, boolean isSprinter, Order order, Integer lableHeight) {
        int rowLine = 0;
        String type = order.getOrderType();
        String orderType = "";
        if (type.equals("EAT_IN")) {
            orderType = "堂食";
        } else if (type.equals("SALE_OUT")) {
            orderType = "外卖";
        } else if (type.equals("TAKE_OUT")) {
            orderType = "外带";
        }
        gp.isSprt(isSprinter);
        if(!TextUtils.isEmpty(order.getCallNumber()))
        {
            gp.isCallNumber(true);
        }
        String count = "  " + printerIndex + "/" + orderDishCount;
        if(lableHeight >= 30 && !TextUtils.isEmpty(order.getCallNumber()))
        {
            String cardNumberType = "";
            String callNumber = "";
            if (StoreInfor.cardNumberMode) {
                cardNumberType = "餐牌号:";
                String tableName = order.getTableNames();
                callNumber = TextUtils.isEmpty(tableName) ? "0" : tableName;
            }
            else{
                cardNumberType = "取餐号:";
                callNumber = TextUtils.isEmpty(order.getCallNumber()) ? "0" : order.getCallNumber();
            }
            gp.printerfont(rowLine, 1, 1, cardNumberType + callNumber);//第一行
            rowLine += 1;
        }
        gp.printerfont(rowLine, 1, 1, "订单号:" + orderItem.getOrderId()+count+" "+orderType);//第一行
        gp.printerfont(rowLine + 1, 1, 1, "时间:" + TimeUtil.getHourStr());//第二行
        gp.printerfont(rowLine + 2, 1, 2, orderItem.getDishName());//第三行
        String sku = getRemarks(orderItem);

        if (!TextUtils.isEmpty(sku)) {
            gp.printerfont(rowLine + 3, 1, 1, sku);//第五行
            rowLine += 1;
        }
        if (orderItem.getCost() != null && orderItem.getCost().compareTo(BigDecimal.ZERO) == 1) {
            gp.printerfont(rowLine + 3, 1, 1, "价格:" + orderItem.getCost().setScale(2, BigDecimal.ROUND_DOWN) + "￥");//第四行
        }

//        if (orderItem.getCost() != null && orderItem.getCost().compareTo(BigDecimal.ZERO) == 1) {
////            gp.printerfont(rowLine + 3, 1, 1, "价格:" + orderItem.getCost().setScale(2, BigDecimal.ROUND_DOWN) + "￥");//第四行
//            if (!TextUtils.isEmpty(sku)) {
//                gp.printerfont(rowLine + 4, 1, 1, sku);//第五行
//            }
//        } else {//因为是根据坐标定义行，所以当价格为空时，把第四行给定制项，不然的话有可能会空白一行
//            if (!TextUtils.isEmpty(sku)) {
//                gp.printerfont(rowLine + 3, 1, 1, sku);//第四行
//            }
//        }

        gp.printlabel(1, 1);//打印出缓冲区的数据,第一个参数是打印的份数，第二个是每份打印的张数
        gp.clearbuffer();
        gp.closeport();
    }


    //获取定制项或做法或口味
    private static String getRemarks(OrderItem orderItem) {
        String commnt = "";
        //定制项
        if (orderItem.getOptionList() != null && orderItem.getOptionList().size() > 0) {

            for (Option option : orderItem.getOptionList()) {
                commnt += option.getName() + ",";
            }
        }
        //口味
        if (orderItem.getCookList() != null && orderItem.getCookList().size() > 0) {
            for (CookingMethod cookingMethod : orderItem.getCookList()) {
                float price = cookingMethod.getPrice();
                commnt += cookingMethod.cookName + ",";
            }
        }
        //做法
        if (orderItem.getTasteList() != null && orderItem.getTasteList().size() > 0) {
            for (Flavor flavor : orderItem.getTasteList()) {
                commnt += flavor.tasteName + ",";
            }
        }
        if (commnt.length() > 0) {
            commnt = commnt.substring(0, commnt.length() - 1);
        }
        return commnt;
    }

}
