package cn.acewill.mobile.pos.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by DHH on 2017/3/17.
 */

public class KitchenStall implements Serializable{
    private Integer stallsid;
    private String stallsName ;
    private Integer printerid ;
    private Integer kdsid  ;
    private Integer summaryReceiptCounts;
    private Integer dishReceiptCounts ;
    private KitchenPrintMode kitchenPrintMode = KitchenPrintMode.PER_DISH;
    private List<Integer> dishIdList;
    private String standByError;//备用打印机错误提示

    private int summartDishCounts = 1;//如果该档口的打印模式是多份多单的情况  用来保存需要打印多少份

    public Integer getStallsid() {
        return stallsid;
    }

    public void setStallsid(Integer stallsid) {
        this.stallsid = stallsid;
    }

    public List<Integer> getDishIdList() {
        return dishIdList;
    }

    public void setDishIdList(List<Integer> dishIdList) {
        this.dishIdList = dishIdList;
    }

    public Integer getDishReceiptCounts() {
        return dishReceiptCounts;
    }

    public void setDishReceiptCounts(Integer dishReceiptCounts) {
        this.dishReceiptCounts = dishReceiptCounts;
    }

    public Integer getSummaryReceiptCounts() {
        return summaryReceiptCounts;
    }

    public void setSummaryReceiptCounts(Integer summaryReceiptCounts) {
        this.summaryReceiptCounts = summaryReceiptCounts;
    }

    public Integer getKdsid() {
        return kdsid;
    }

    public void setKdsid(Integer kdsid) {
        this.kdsid = kdsid;
    }

    public Integer getPrinterid() {
        return printerid;
    }

    public void setPrinterid(Integer printerid) {
        this.printerid = printerid;
    }

    public String getStallsName() {
        return stallsName;
    }

    public void setStallsName(String stallsName) {
        this.stallsName = stallsName;
    }

    public KitchenPrintMode getKitchenPrintMode() {
        return kitchenPrintMode;
    }

    public void setKitchenPrintMode(KitchenPrintMode kitchenPrintMode) {
        this.kitchenPrintMode = kitchenPrintMode;
    }

    public String getStandByError() {
        return standByError;
    }

    public void setStandByError(String standByError) {
        this.standByError = standByError;
    }
}
