package cn.acewill.mobile.pos.printer;

/**
 * Created by aqw on 2017/1/3.
 */
public enum PrinterLinkType {
    NETWORK(0),
    BLUETOOTH(1),
    USB(2);

    int type;

    PrinterLinkType(int dotNumber) {
        this.type = dotNumber;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
