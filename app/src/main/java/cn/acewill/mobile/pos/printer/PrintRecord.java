package cn.acewill.mobile.pos.printer;

import java.io.Serializable;

import cn.acewill.mobile.pos.model.order.Order;
import cn.acewill.mobile.pos.utils.Constant;


/**
 * 记录订单打印时的状态对象
 * Created by DHH on 2017/6/12.
 */
public class PrintRecord implements Serializable{
    private long orderId;
    private int orderPrintType;
    private boolean isPrint = false;
    private int checkoutReceiptNeedCounts = 0; //结账单打印了几份
    private int guestReceiptNeedCounts = 0; //客用小票打印了几份
    private Order order;//需要打印的订单对象
    private String printIp;//打印机的IP
    private String printName;//打印机的名称
    private long printTime;//打印时间
    private int summaryKitchenStallId;//总单档口的id

    private void addCheckoutReceiptCounts()
    {
        checkoutReceiptNeedCounts =+1;
    }

    private void addGuestReceiptCounts()
    {
        guestReceiptNeedCounts+=1;
    }

    /**
     * 根据小票打印样式判断是那种小票格式的打印份数要增加
     * @param orderPrinterType
     */
    public void addCheckOutOrGuestReceiptCounts(int orderPrinterType)
    {
        //如果是客用单
        if(orderPrinterType == Constant.EventState.PRINTER_ORDER)
        {
            addGuestReceiptCounts();
        }
        //如果是结账单
        else if(orderPrinterType == Constant.EventState.PRINT_CHECKOUT)
        {
            addCheckoutReceiptCounts();
        }
    }

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public String getPrintIp() {
        return printIp;
    }

    public void setPrintIp(String printIp) {
        this.printIp = printIp;
    }


    public String getPrintName() {
        return printName;
    }

    public void setPrintName(String kitchenStallName,Printer printer) {
        String printerName = "";
        if(printer != null)
        {
            printerName = kitchenStallName+printer.getDescription()+"("+printer.getIp()+")";
        }
        else{
            printerName = kitchenStallName;
        }
        this.printName = printerName;
    }

    public long getPrintTime() {
        return printTime;
    }

    public void setPrintTime() {
        this.printTime = System.currentTimeMillis();
    }

    public int getGuestReceiptNeedCounts() {
        return guestReceiptNeedCounts;
    }

    public void setGuestReceiptNeedCounts(int guestReceiptCounts) {
        this.guestReceiptNeedCounts = guestReceiptCounts;
    }

    public int getCheckoutReceiptNeedCounts() {
        return checkoutReceiptNeedCounts;
    }

    public void setCheckoutReceiptNeedCounts(int checkoutReceiptCounts) {
        this.checkoutReceiptNeedCounts = checkoutReceiptCounts;
    }

    public boolean isPrint() {
        return isPrint;
    }

    public void setPrint(boolean print) {
        isPrint = print;
    }

    public int getOrderPrintType() {
        return orderPrintType;
    }

    public void setOrderPrintType(int orderPrintType) {
        this.orderPrintType = orderPrintType;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public int getSummaryKitchenStallId() {
        return summaryKitchenStallId;
    }

    public void setSummaryKitchenStallId(int summaryKitchenStallId) {
        this.summaryKitchenStallId = summaryKitchenStallId;
    }
}

