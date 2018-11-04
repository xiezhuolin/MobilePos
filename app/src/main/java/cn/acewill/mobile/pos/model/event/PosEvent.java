package cn.acewill.mobile.pos.model.event;

import android.text.TextUtils;

import java.math.BigDecimal;
import java.util.List;

import cn.acewill.mobile.pos.model.KDS;
import cn.acewill.mobile.pos.model.WorkShiftReport;
import cn.acewill.mobile.pos.model.dish.Dish;
import cn.acewill.mobile.pos.model.order.Order;
import cn.acewill.mobile.pos.model.order.OrderItem;
import cn.acewill.mobile.pos.printer.Printer;


/**
 * Created by DHH on 2016/6/17.
 */
public class PosEvent {
    int action;
    int position = -1;
    long tableId;
    List<Order> orderList;
    Order order;
    Printer printer;
    public Dish.Package dishPackageItem;
    OrderItem oi;
    List<OrderItem> oiList;//订单中的菜品列表
    int customerAmount;// 顾客人数
    String errMessage;//错误信息
    int cashActionId;//收银打印机打印动作Id
    WorkShiftReport workShiftReport;//日结 交班 数据报表对象
    KDS kds;//kds对象
    String tableName;//桌台名
    String orderId;//订单Id
    long refOrderId;//网上订单ID
    BigDecimal price;
    int dishItemCount;
    List<Dish> marketActList;
    boolean cashPrinterType = false;//false 是其它  true 是POSsin
    public PosEvent(int action)
    {
        this.action = action;
    }

    public PosEvent(int action,boolean cashPrinterType)
    {
        this.action = action;
        this.cashPrinterType = cashPrinterType;
    }

    public PosEvent(int action, List<Dish> marketActList, BigDecimal price,int dishItemCount)
    {
        this.action = action;
        this.price = price;
        this.marketActList = marketActList;
        this.dishItemCount = dishItemCount;
    }

    public PosEvent(int action,WorkShiftReport workShiftReport)
    {
        this.action = action;
        this.workShiftReport = workShiftReport;
    }
    public PosEvent(int action,long refOrderId,String tableName)
    {
        this.action = action;
        this.refOrderId = refOrderId;
        this.tableName = tableName;
    }

    public PosEvent(int action,String orderId)
    {
        this.action = action;
        this.orderId = orderId;
    }

    public PosEvent(int action,List<OrderItem> oiList,String  orderId)
    {
        this.action = action;
        this.oiList = oiList;
        this.orderId = orderId;
    }

    public PosEvent(int action,int cashActionId,String errMessage)
    {
        this.action = action;
        this.errMessage = errMessage;
        this.cashActionId = cashActionId;
    }

    public PosEvent(int action,int cashActionId,Order order,String errMessage)
    {
        this.order = order;
        this.action = action;
        this.errMessage = substringErrmessage(errMessage);
        this.cashActionId = cashActionId;
    }

    public PosEvent(int action,int cashActionId,int position,Order order,String errMessage)
    {
        this.order = order;
        this.action = action;
        this.errMessage = substringErrmessage(errMessage);
        this.cashActionId = cashActionId;
        this.position = position;
    }

    private String substringErrmessage(String errMessage)
    {
        String str = "";
        if(!TextUtils.isEmpty(errMessage))
        {
            str = errMessage.substring(errMessage.indexOf(":")+1,errMessage.length());
        }
        return str;
    }

    public PosEvent(int action, int position)
    {
        this.action = action;
        this.position = position;
    }

    public PosEvent(int action, List<Order> orderList)
    {
        this.action = action;
        this.orderList = orderList;
    }

    public PosEvent(int action, Order order)
    {
        this.action = action;
        this.order = order;
        this.position = -1;
    }

    public PosEvent(int action, Order order,String tableName)
    {
        this.action = action;
        this.order = order;
        this.tableName = tableName;
    }

    public PosEvent(int action, Order order,int position)
    {
        this.action = action;
        this.order = order;
        this.position = position;
    }

    public PosEvent(int action, Order order,Printer printer)
    {
        this.action = action;
        this.order = order;
        this.printer = printer;
    }

    public PosEvent(int action,Printer printer)
    {
        this.action = action;
        this.printer = printer;
    }

    public PosEvent(int action,KDS kds)
    {
        this.action = action;
        this.kds = kds;
    }

    public PosEvent(int action, Order order,OrderItem oi, Printer printer)
    {
        this.action = action;
        this.oi = oi;
        this.order = order;
        this.printer = printer;
    }
    public PosEvent(int action, Order order,List<OrderItem> oiList, Printer printer)
    {
        this.action = action;
        this.oiList = oiList;
        this.order = order;
        this.printer = printer;
    }

    public PosEvent(int action, Order order,OrderItem oi,Dish.Package dishPackageItem,Printer printer)
    {
        this.action = action;
        this.oi = oi;
        this.order = order;
        this.printer = printer;
        this.dishPackageItem = dishPackageItem;
    }


    public PosEvent(int action, long tableId)
    {
        this.action = action;
        this.tableId = tableId;
    }

    public int getAction() {
        return action;
    }

    public int getPosition() {
        return position;
    }
    public List<Order> getOrderList() {
        return orderList;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public long getTableId() {
        return tableId;
    }

    public void setTableId(long tableId) {
        this.tableId = tableId;
    }

    public int getCustomerAmount() {
        return customerAmount;
    }

    public void setCustomerAmount(int customerAmount) {
        this.customerAmount = customerAmount;
    }

    public Printer getPrinter() {
        return printer;
    }

    public void setPrinter(Printer printer) {
        this.printer = printer;
    }

    public OrderItem getOi() {
        return oi;
    }

    public void setOi(OrderItem oi) {
        this.oi = oi;
    }

    public void setAction(int action) {
        this.action = action;
    }

    public Dish.Package getDishPackageItem() {
        return dishPackageItem;
    }

    public void setDishPackageItem(Dish.Package dishPackageItem) {
        this.dishPackageItem = dishPackageItem;
    }

    public List<OrderItem> getOiList() {
        return oiList;
    }

    public void setOiList(List<OrderItem> oiList) {
        this.oiList = oiList;
    }

    public String getErrMessage() {
        return errMessage;
    }

    public void setErrMessage(String errMessage) {
        this.errMessage = errMessage;
    }

    public int getCashActionId() {
        return cashActionId;
    }

    public void setCashActionId(int cashActionId) {
        this.cashActionId = cashActionId;
    }

    public WorkShiftReport getWorkShiftReport() {
        return workShiftReport;
    }

    public void setWorkShiftReport(WorkShiftReport workShiftReport) {
        this.workShiftReport = workShiftReport;
    }

    public KDS getKds() {
        return kds;
    }

    public void setKds(KDS kds) {
        this.kds = kds;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public long getRefOrderId() {
        return refOrderId;
    }

    public void setRefOrderId(long refOrderId) {
        this.refOrderId = refOrderId;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public int getDishItemCount() {
        return dishItemCount;
    }

    public void setDishItemCount(int dishItemCount) {
        this.dishItemCount = dishItemCount;
    }

    public List<Dish> getMarketActList() {
        return marketActList;
    }

    public void setMarketActList(List<Dish> marketActList) {
        this.marketActList = marketActList;
    }

    public boolean isCashPrinterType() {
        return cashPrinterType;
    }

    public void setCashPrinterType(boolean cashPrinterType) {
        this.cashPrinterType = cashPrinterType;
    }
}
