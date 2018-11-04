package cn.acewill.mobile.pos.printer;

/**
 * Created by aqw on 2017/1/3.
 */
public enum PrinterOutputType {
    REGULAR(0), //普通打印机
    LABEL(1); //标签打印机

    int type;

    PrinterOutputType(int dotNumber) {
        this.type = dotNumber;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
