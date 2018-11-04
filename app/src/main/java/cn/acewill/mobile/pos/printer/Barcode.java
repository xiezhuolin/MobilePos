package cn.acewill.mobile.pos.printer;

/**
 * Created by Acewill on 2016/8/22.
 */
public class Barcode implements Row {
    private String barcode;

    public Barcode(String barcode) {
        this.barcode = barcode;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    @Override
    public int getScaleWidth() {
        return 1;
    }
}
