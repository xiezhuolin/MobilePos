package cn.acewill.mobile.pos.printer.usb;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class UsbUtil {

	private static final String TAG = "UsbPrinter";
	
	private final Context mContext;
	private final UsbManager mUsbManager;
	//private volatile List<UsbDevice> mUsbPrinterList = null;
	
	private static String ACTION_USB_PERMISSION = "com.posin.usbdevice.USB_PERMISSION";
	
	final BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d(TAG, intent.getAction());
			context.unregisterReceiver(this);
		}
	};

	public UsbUtil(Context context) {
		mContext = context;
		mUsbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
	}
	
//	public List<UsbDevice> getUsbPrinterList() {
//		if(mUsbPrinterList == null)
//			mUsbPrinterList = findAllUsbPrinter();
//		return mUsbPrinterList;
//	}
	
	public void requestPermission(UsbDevice usbDevice) {
		if (!mUsbManager.hasPermission(usbDevice)) {
			IntentFilter ifilter = new IntentFilter(ACTION_USB_PERMISSION);
			mContext.registerReceiver(mReceiver, ifilter);

			PendingIntent pi = PendingIntent.getBroadcast(mContext, 0, new Intent(
					ACTION_USB_PERMISSION), 0);
			mUsbManager.requestPermission(usbDevice, pi);
		}
	}
	
	private static boolean contains(int[] ids, int id) {
		for(int i : ids) {
			if(i == id)
				return true;
		}
		return false;
	}
	
	public List<UsbDevice> findDevicesByVid(int[] vids) {
        final List<UsbDevice> result = new ArrayList<UsbDevice>();

        Log.d(TAG, "find usb device ...");
        for (final UsbDevice usbDevice : mUsbManager.getDeviceList().values()) {
//        	Log.d(TAG, String.format("usb %04X:%04X : device_id=%d, device_name=%s",
//        			usbDevice.getVendorId(), usbDevice.getProductId(),
//        			usbDevice.getDeviceId(), usbDevice.getDeviceName()));
        	if(contains(vids, usbDevice.getVendorId())) {
            	Log.d(TAG, String.format("usb device %04X:%04X : device_id=%d, device_name=%s",
            			usbDevice.getVendorId(), usbDevice.getProductId(),
            			usbDevice.getDeviceId(), usbDevice.getDeviceName()));
                result.add(usbDevice);
            }
        }

        return result;
    }
//
//    private List<UsbDevice> findAllUsbPrinter() {
//        final List<UsbDevice> result = new ArrayList<UsbDevice>();
//
//        Log.d(TAG, "find usb printer...");
//        for (final UsbDevice usbDevice : mUsbManager.getDeviceList().values()) {
////        	Log.d(TAG, String.format("usb %04X:%04X : device_id=%d, device_name=%s",
////        			usbDevice.getVendorId(), usbDevice.getProductId(),
////        			usbDevice.getDeviceId(), usbDevice.getDeviceName()));
//            if (isUsbPrinterDevice(usbDevice)) {
//            	Log.d(TAG, String.format("usb printer %04X:%04X : device_id=%d, device_name=%s",
//            			usbDevice.getVendorId(), usbDevice.getProductId(),
//            			usbDevice.getDeviceId(), usbDevice.getDeviceName()));
//                result.add(usbDevice);
//            }
//        }
//
//        return result;
//    }

	
}
