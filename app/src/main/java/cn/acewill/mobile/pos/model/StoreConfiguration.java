package cn.acewill.mobile.pos.model;


import cn.acewill.mobile.pos.printer.PrintConfiguration;

/**
 * Created by DHH on 2017/3/28.
 */

public class StoreConfiguration {
    private int wipeZero;//抹零处理：0：自动抹零到元；1：四舍五入到元；2：自动抹零到角； 3：四舍五入到角；其他：不抹零
    private boolean tableServer;//是否启用桌台设置
    private boolean cardNumberMode; //是否为餐牌号模式
    private boolean callNumberMode;// 是否为叫号模式
    private boolean payBeforeMeal;// true: 先付款后吃饭； false: 后付款
    private boolean cashOpenDrawer;// 现金支付弹出钱箱
    private boolean cardOpenDrawer;// 刷卡支付弹出钱箱
    private boolean allowSpecialDishDiscount;// 允许特殊菜品(酒水)参与打折活动
    private boolean shiftPrintReport;// 交接班的时候打印报表
    private boolean pirntDishShortName;// 打印菜品简称
    private boolean printRetreatDish;// 小票上打印退菜情况
    private boolean printWhenRetreat;// 退菜时打印小票
    private boolean wxOrderToKichen;// 微信订单直接到厨房
    private boolean saleoutOrderToKichen;// 外卖订单直接到厨房
    private boolean retreatCheckAuthority ;// 退菜时需要权限检查
    private boolean deleteCheckAuthority; // 删菜时需要权限检查
    private boolean openDrawerCheckAuthority ;// 打开钱箱时需要权限检查
    private boolean openPosCheckAuthority;// 开机是否需要检查权限
    private boolean printInvoiceBarcode;//结账时打印电子发票相关的二维码
    private String autoDayoutTime; // 自动日结时间，默认23:59
    private long sleepTime;// 默认的启动休眠时间
    private PrintConfiguration printConfiguration;//打印机配置

    public int getWipeZero() {
        return wipeZero;
    }

    public void setWipeZero(int wipeZero) {
        this.wipeZero = wipeZero;
    }

    public boolean isTableServer() {
        return tableServer;
    }

    public void setTableServer(boolean tableServer) {
        this.tableServer = tableServer;
    }

    public boolean isCardNumberMode() {
        return cardNumberMode;
    }

    public void setCardNumberMode(boolean cardNumberMode) {
        this.cardNumberMode = cardNumberMode;
    }

    public boolean isCallNumberMode() {
        return callNumberMode;
    }

    public void setCallNumberMode(boolean callNumberMode) {
        this.callNumberMode = callNumberMode;
    }

    public boolean isPayBeforeMeal() {
        return payBeforeMeal;
    }

    public void setPayBeforeMeal(boolean payBeforeMeal) {
        this.payBeforeMeal = payBeforeMeal;
    }

    public boolean isCardOpenDrawer() {
        return cardOpenDrawer;
    }

    public void setCardOpenDrawer(boolean cardOpenDrawer) {
        this.cardOpenDrawer = cardOpenDrawer;
    }

    public boolean isCashOpenDrawer() {
        return cashOpenDrawer;
    }

    public void setCashOpenDrawer(boolean cashOpenDrawer) {
        this.cashOpenDrawer = cashOpenDrawer;
    }

    public boolean isAllowSpecialDishDiscount() {
        return allowSpecialDishDiscount;
    }

    public void setAllowSpecialDishDiscount(boolean allowSpecialDishDiscount) {
        this.allowSpecialDishDiscount = allowSpecialDishDiscount;
    }

    public boolean isShiftPrintReport() {
        return shiftPrintReport;
    }

    public void setShiftPrintReport(boolean shiftPrintReport) {
        this.shiftPrintReport = shiftPrintReport;
    }

    public boolean isPirntDishShortName() {
        return pirntDishShortName;
    }

    public void setPirntDishShortName(boolean pirntDishShortName) {
        this.pirntDishShortName = pirntDishShortName;
    }

    public boolean isPrintRetreatDish() {
        return printRetreatDish;
    }

    public void setPrintRetreatDish(boolean printRetreatDish) {
        this.printRetreatDish = printRetreatDish;
    }

    public boolean isPrintWhenRetreat() {
        return printWhenRetreat;
    }

    public void setPrintWhenRetreat(boolean printWhenRetreat) {
        this.printWhenRetreat = printWhenRetreat;
    }

    public boolean isWxOrderToKichen() {
        return wxOrderToKichen;
    }

    public void setWxOrderToKichen(boolean wxOrderToKichen) {
        this.wxOrderToKichen = wxOrderToKichen;
    }

    public boolean isSaleoutOrderToKichen() {
        return saleoutOrderToKichen;
    }

    public void setSaleoutOrderToKichen(boolean saleoutOrderToKichen) {
        this.saleoutOrderToKichen = saleoutOrderToKichen;
    }

    public boolean isRetreatCheckAuthority() {
        return retreatCheckAuthority;
    }

    public void setRetreatCheckAuthority(boolean retreatCheckAuthority) {
        this.retreatCheckAuthority = retreatCheckAuthority;
    }

    public boolean isDeleteCheckAuthority() {
        return deleteCheckAuthority;
    }

    public void setDeleteCheckAuthority(boolean deleteCheckAuthority) {
        this.deleteCheckAuthority = deleteCheckAuthority;
    }

    public boolean isOpenDrawerCheckAuthority() {
        return openDrawerCheckAuthority;
    }

    public void setOpenDrawerCheckAuthority(boolean openDrawerCheckAuthority) {
        this.openDrawerCheckAuthority = openDrawerCheckAuthority;
    }

    public boolean isOpenPosCheckAuthority() {
        return openPosCheckAuthority;
    }

    public void setOpenPosCheckAuthority(boolean openPosCheckAuthority) {
        this.openPosCheckAuthority = openPosCheckAuthority;
    }

    public boolean isPrintInvoiceBarcode() {
        return printInvoiceBarcode;
    }

    public void setPrintInvoiceBarcode(boolean printInvoiceBarcode) {
        this.printInvoiceBarcode = printInvoiceBarcode;
    }

    public String getAutoDayoutTime() {
        return autoDayoutTime;
    }

    public void setAutoDayoutTime(String autoDayoutTime) {
        this.autoDayoutTime = autoDayoutTime;
    }

    public long getSleepTime() {
        return sleepTime;
    }

    public void setSleepTime(long sleepTime) {
        this.sleepTime = sleepTime;
    }

    public PrintConfiguration getPrintConfiguration() {
        return printConfiguration;
    }

    public void setPrintConfiguration(PrintConfiguration printConfiguration) {
        this.printConfiguration = printConfiguration;
    }
}
