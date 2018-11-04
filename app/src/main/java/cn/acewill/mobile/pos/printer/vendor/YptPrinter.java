package cn.acewill.mobile.pos.printer.vendor;


import cn.acewill.mobile.pos.printer.PrinterWidth;

/**
 * 莹普通打印机
 * Created by Acewill on 2016/8/17.
 */
public class YptPrinter extends WifiPrinter {
    public YptPrinter(String host, PrinterWidth width) {
        super(host, width == PrinterWidth.WIDTH_80MM ? 42 : 32);
    }
}
