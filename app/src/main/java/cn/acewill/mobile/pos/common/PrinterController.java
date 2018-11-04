package cn.acewill.mobile.pos.common;

import android.support.v4.util.ArrayMap;
import android.text.TextUtils;

import cn.acewill.mobile.pos.model.TerminalInfo;
import cn.acewill.mobile.pos.model.dish.Dish;
import cn.acewill.mobile.pos.model.order.Order;
import cn.acewill.mobile.pos.model.order.OrderItem;
import cn.acewill.mobile.pos.printer.Alignment;
import cn.acewill.mobile.pos.printer.Column;
import cn.acewill.mobile.pos.printer.PrintModelInfo;
import cn.acewill.mobile.pos.printer.Table;
import cn.acewill.mobile.pos.printer.TextRow;
import cn.acewill.mobile.pos.utils.Constant;


/**
 * Created by DHH on 2017/3/28.
 */

public class PrinterController {
    public static String getBrandNameAndStoreName(ArrayMap<String, PrintModelInfo> printMode) {
        String storeName = "";
        String brandName = "";
        String brandAndStore = "";
        TerminalInfo terminalInfo = StoreInfor.terminalInfo;
        if (terminalInfo != null) {
            brandName = TextUtils.isEmpty(terminalInfo.brandName) ? "" : terminalInfo.brandName;
            storeName = TextUtils.isEmpty(terminalInfo.sname) ? "" : terminalInfo.sname;
        }

        PrintModelInfo printModeForBrandName = printMode.get("brandName");//品牌名
        PrintModelInfo printModeForStoreName = printMode.get("storeName");//店名
        String symbol = "";
        if (printModeForBrandName.isShouldPrint() && printModeForStoreName.isShouldPrint()) {
            symbol = "-";
            brandAndStore = brandName + symbol + storeName;
        } else if (!printModeForBrandName.isShouldPrint() && printModeForStoreName.isShouldPrint()) {
            brandAndStore = storeName;
        } else if (printModeForBrandName.isShouldPrint() && !printModeForStoreName.isShouldPrint()) {
            brandAndStore = brandName;
        }
        return brandAndStore;
    }

    /**
     * 获取打印显示的菜品份数,
     * eg:如果是正常的话显示菜品已经选取的份数取orderItem.quantity字段,
     * 如果是退菜打印的话取菜品orderItem.getRejectedQuantity退菜份数字段
     */
    public static String getDishCount(Order order, OrderItem orderItem) {
        String dishCount = "";
        if (order.getPrinterType() == Constant.EventState.PRINTER_RETREAT_DISH_GUEST) {
            dishCount = getDishRejectedQuantity(orderItem.getRejectedQuantity() + "");
            dishCount = "-" + dishCount;
        }
        else if(order.getPrinterType() == Constant.EventState.PRINTER_RETREAT_ORDER)
        {
            dishCount = "-" +orderItem.getQuantity();
        }
        else {
            dishCount = orderItem.getQuantity() + "";
        }
        return dishCount+"*"+orderItem.getPrice();
    }

    public static String getPackateCount(Order order, OrderItem orderItem, Dish.Package dishPackage) {
        String dishPackageCount = "";
        if (order.getPrinterType() == Constant.EventState.PRINTER_RETREAT_DISH_GUEST) {
            dishPackageCount = dishPackage.quantity * orderItem.getRejectedQuantity() + "";
            dishPackageCount = "-" + dishPackageCount;
        }
        else if(order.getPrinterType() == Constant.EventState.PRINTER_RETREAT_ORDER)
        {
            dishPackageCount = "-" +dishPackage.quantity * orderItem.quantity;
        }
        else {
            dishPackageCount = dishPackage.quantity * orderItem.quantity + "";
        }
        return dishPackageCount;
    }


    /**
     * 这个订单是不是退菜模式
     */
    private static boolean isLogicRefundDish(Order order) {
        if (order.getTableStyle() == Constant.EventState.PRINTER_RETREAT_DISH) {
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

    public static TextRow getSingleLineText(boolean bold, int width, String content) {
        TextRow textRow = null;
//        width += 1;
        textRow = createRow(bold, width, content);
        return textRow;
    }

    private static TextRow createRow(boolean bold, int size, String content) {
        TextRow title = new TextRow(content);
        title.setScaleWidth(size);
        title.setScaleHeight(size);
        title.setBoldFont(bold);
        return title;
    }

    public static Table getDoubleLineText(boolean bold, int width, String left, String right) {
        Table table = null;
//        width += 1;
        table = createRow(bold, width, left, right);
        return table;
    }

    //创建一行， 内容为 左对齐 和 有对齐 的2列
    private static Table createRow(boolean bold, int size, String left, String right) {
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

}
