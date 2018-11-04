package cn.acewill.mobile.pos.model;

/**
 * Created by Acewill on 2016/8/23.
 */
public enum ScanType {
    SCAN_USER("SCAN_USER"), //扫顾客（反扫）
    USER_SCAN("USER_SCAN");//顾客扫POS上的二维码 （正扫）

    private String name;

    ScanType(String name) {
        this.name = name;
    }
}
