package cn.acewill.mobile.pos.printer.vendor;

import java.io.IOException;

import cn.acewill.mobile.pos.printer.BarcodePosition;
import cn.acewill.mobile.pos.printer.PrinterCommand;
import cn.acewill.mobile.pos.printer.PrinterWidth;


/**
 * 爱普生网络打印机
 * Created by linmingren on 16/8/3.
 */
public class EpsonPrinter extends WifiPrinter {

    public EpsonPrinter(String host, PrinterWidth width) {
        super(host, width == PrinterWidth.WIDTH_80MM ? 42 : 32);
    }

    /**
     * 只支持长度12的二维码
     *
     * @param barcode
     * @throws IOException
     */
    public void printBarcode(String barcode) throws IOException {
       /* if (barcode.getBytes().length != 12) {
            //只支持长度在[11,12]的字符串
            return;
        }*/
        outputStream.write(PrinterCommand.EPSON_BAR_CODE2(120, 4, BarcodePosition.BOTTOM));
        outputStream.write(barcode.getBytes());
        outputStream.write(0x00); //表明条形码指令结束
    }

    @Override
    public void openCachBox() throws IOException {
        super.openCachBox();
    }

    @Override
    public void openBuzzer() throws IOException {
        super.openBuzzer();
    }
}
