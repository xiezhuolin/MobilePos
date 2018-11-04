package cn.acewill.mobile.pos.printer;

/**
 * Created by Acewill on 2016/6/8.
 */
public enum BarcodePosition {
    NONE(1),
    TOP(1), //条形码的数字打印在上方
    BOTTOM(2), //条形码的数字打印在下方
    BOTH(3); //在上方和下方都打印

    private int status;
    BarcodePosition(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
