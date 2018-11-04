package cn.acewill.mobile.pos.printer;

/**
 * Created by Acewill on 2016/8/19.
 */
public enum PrinterState {
    //连接成功
    SUCCESS(576),
    //等待检测
    WATING(384),
    //错误
    ERROR(100);

    int printerState;

    PrinterState(int printerState) {
        this.printerState = printerState;
    }

    public int getPrinterState() {
        return printerState;
    }

    public void setPrinterState(int printerState) {
        this.printerState = printerState;
    }
}
