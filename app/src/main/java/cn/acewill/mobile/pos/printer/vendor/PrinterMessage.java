package cn.acewill.mobile.pos.printer.vendor;

/**
 *
 * Created by Acewill on 2016/8/18.
 */
public class PrinterMessage {
    private int status;
    private String message;

    public PrinterMessage(int status, String message) {
        this.status = status;
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
