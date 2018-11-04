package cn.acewill.mobile.pos.printer;

/**
 * Created by DHH on 2017/3/18.
 */

public class PrinterVendors {
    private String value;//厂商枚举值
    private String displayName;//显示的名称

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}
