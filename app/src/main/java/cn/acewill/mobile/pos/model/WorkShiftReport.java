package cn.acewill.mobile.pos.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * Created by DHH on 2016/10/18.
 */

public class WorkShiftReport implements Serializable{
    /**
     * 客单价统计
     */
    private PctData pctData;
    private String printerIp;
    private String startTime;
    private String endTime;
    private List<ItemCategorySalesDataList> itemCategorySalesDataList;
    public PctData getPctData() {
        return pctData;
    }
    public void setPctData(PctData pctData) {
        this.pctData = pctData;
    }

    public List<ItemCategorySalesDataList> getItemCategorySalesDataList() {
        return itemCategorySalesDataList;
    }

    public void setItemCategorySalesDataList(List<ItemCategorySalesDataList> itemCategorySalesDataList) {
        this.itemCategorySalesDataList = itemCategorySalesDataList;
    }
    public static class PctData implements Serializable{
        private BigDecimal orderCounts;//订单数量
        private BigDecimal pricePerOrder;//订单均价
        private BigDecimal customerCounts;//客人数量
        private BigDecimal pricePerCustomer;//客单价
        private BigDecimal discount;//折扣

        public BigDecimal getOrderCounts() {
            return orderCounts;
        }

        public void setOrderCounts(BigDecimal orderCounts) {
            this.orderCounts = orderCounts;
        }

        public BigDecimal getPricePerOrder() {
            return pricePerOrder;
        }

        public void setPricePerOrder(BigDecimal pricePerOrder) {
            this.pricePerOrder = pricePerOrder;
        }

        public BigDecimal getCustomerCounts() {
            return customerCounts;
        }

        public void setCustomerCounts(BigDecimal customerCounts) {
            this.customerCounts = customerCounts;
        }

        public BigDecimal getPricePerCustomer() {
            return pricePerCustomer;
        }

        public void setPricePerCustomer(BigDecimal pricePerCustomer) {
            this.pricePerCustomer = pricePerCustomer;
        }

        public BigDecimal getDiscount() {
            return discount;
        }

        public void setDiscount(BigDecimal discount) {
            this.discount = discount;
        }
    }

    public static class ItemCategorySalesDataList implements Serializable{
        private String name;//销售汇总
        private List<ItemSalesDataList> itemSalesDataList;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<ItemSalesDataList> getItemSalesDataList() {
            return itemSalesDataList;
        }

        public void setItemSalesDataList(List<ItemSalesDataList> itemSalesDataList) {
            this.itemSalesDataList = itemSalesDataList;
        }

        public static class ItemSalesDataList implements Serializable{
            private String name;//品类名称
            private BigDecimal itemCounts;//个数
            private BigDecimal total;//价格

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public BigDecimal getItemCounts() {
                return itemCounts;
            }

            public void setItemCounts(BigDecimal itemCounts) {
                this.itemCounts = itemCounts;
            }

            public BigDecimal getTotal() {
                return total;
            }

            public void setTotal(BigDecimal total) {
                this.total = total;
            }
        }
    }

    public String getPrinterIp() {
        return printerIp;
    }

    public void setPrinterIp(String printerIp) {
        this.printerIp = printerIp;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
}
