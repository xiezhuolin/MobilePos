package cn.acewill.mobile.pos.model;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by aqw on 2016/12/6.
 */
public class OrderItemReportData {

    private Long orderTotal = 0l;  //订单数量
    private BigDecimal  salesTotal = new BigDecimal(0);  //订单总额
    private Long exitOrderTotal = 0l;  //退单数量
    private BigDecimal exitOrderSalesTotal = new BigDecimal(0);  //退单总额
    private Long exitItemTotal = 0l;   //退菜数量
    private BigDecimal exitItemSalesTotal = new BigDecimal(0);  //退菜金额
    private String  note;
    private List<ItemSalesData> itemSalesDatas;

    public static class ItemSalesData {
        public String name; //支付名称
        public int itemCounts; // 支付笔数
        public BigDecimal total = new BigDecimal(0);//支付总金额
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Long getOrderTotal() {
        return orderTotal;
    }

    public void setOrderTotal(Long orderTotal) {
        this.orderTotal = orderTotal;
    }

    public BigDecimal getSalesTotal() {
        return salesTotal;
    }

    public void setSalesTotal(BigDecimal salesTotal) {
        this.salesTotal = salesTotal;
    }

    public Long getExitOrderTotal() {
        return exitOrderTotal;
    }

    public void setExitOrderTotal(Long exitOrderTotal) {
        this.exitOrderTotal = exitOrderTotal;
    }

    public BigDecimal getExitOrderSalesTotal() {
        return exitOrderSalesTotal;
    }

    public void setExitOrderSalesTotal(BigDecimal exitOrderSalesTotal) {
        this.exitOrderSalesTotal = exitOrderSalesTotal;
    }

    public Long getExitItemTotal() {
        return exitItemTotal;
    }

    public void setExitItemTotal(Long exitItemTotal) {
        this.exitItemTotal = exitItemTotal;
    }

    public BigDecimal getExitItemSalesTotal() {
        return exitItemSalesTotal;
    }

    public void setExitItemSalesTotal(BigDecimal exitItemSalesTotal) {
        this.exitItemSalesTotal = exitItemSalesTotal;
    }

    public List<ItemSalesData> getItemSalesDatas() {
        return itemSalesDatas;
    }

    public void setItemSalesDatas(List<ItemSalesData> itemSalesDatas) {
        this.itemSalesDatas = itemSalesDatas;
    }
}
