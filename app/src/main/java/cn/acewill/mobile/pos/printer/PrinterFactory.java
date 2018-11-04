package cn.acewill.mobile.pos.printer;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

import cn.acewill.mobile.pos.printer.vendor.BluetoothPrinter;
import cn.acewill.mobile.pos.printer.vendor.EpsonPrinter;
import cn.acewill.mobile.pos.printer.vendor.WifiPrinter;
import cn.acewill.mobile.pos.printer.vendor.YptPrinter;


/**
 * Created by linmingren on 16/8/3.
 */
public class PrinterFactory {
    public static PrinterInterface createPrinter(PrinterVendor type, String ip, PrinterWidth width) {
        if (type == PrinterVendor.EPSON) {
            return new EpsonPrinter(ip, width);
        } else if (type == PrinterVendor.YPT) {
            return new YptPrinter(ip, width);
        }else {
            //找不到对应的品牌，
            return new WifiPrinter(ip, width == PrinterWidth.WIDTH_80MM ? 30 : 32);
        }
    }

    public static PrinterInterface createBluetoothPrinter(PrinterVendor type, String name, PrinterWidth width) {
        for (final BluetoothDevice device : BluetoothAdapter.getDefaultAdapter().getBondedDevices()) {
            if (device.getName().equals(name)) {
                return new BluetoothPrinter(device, width);
            }
        }

        return null;
    }

    //商米蓝牙打印机
    public static PrinterInterface createBluetoothPrinter(PrinterWidth width) {
        for (final BluetoothDevice device : BluetoothAdapter.getDefaultAdapter().getBondedDevices()) {
            if (device.getAddress().equals("00:11:22:33:44:55")) {
                return new BluetoothPrinter(device, width);
            }
        }
        return null;
    }


}
