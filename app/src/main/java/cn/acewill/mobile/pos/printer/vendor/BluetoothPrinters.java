package cn.acewill.mobile.pos.printer.vendor;

import android.bluetooth.BluetoothDevice;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Acewill on 2016/8/22.
 */
public class BluetoothPrinters {
    private static Map<String, BluetoothDevice> name2DeviceMap = new HashMap<>();
    public static void addDevice(String name, BluetoothDevice device) {
        name2DeviceMap.put(name, device);
    }

    public static BluetoothDevice getDeviceByName(String name) {
        return name2DeviceMap.get(name);
    }
}
