package cn.acewill.mobile.pos.model.report;

import java.math.BigDecimal;

/**
 * Created by aqw on 2016/12/15.
 */
public class DishReport {

    private String name;//菜品名称
    private int salesAmount;//销售数量
    private BigDecimal total;//销售金额
    private BigDecimal proportion;//占百分比

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSalesAmount() {
        return salesAmount;
    }

    public void setSalesAmount(int salesAmount) {
        this.salesAmount = salesAmount;
    }

    public BigDecimal getTotal() {
        if(total == null)
        {
            return new BigDecimal("0.00");
        }
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public BigDecimal getProportion() {
        if(proportion == null)
        {
            return new BigDecimal("0.00");
        }
        return proportion;
    }

    public void setProportion(BigDecimal proportion) {
        this.proportion = proportion;
    }
}
