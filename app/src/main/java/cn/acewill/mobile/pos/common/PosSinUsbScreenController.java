package cn.acewill.mobile.pos.common;


import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import cn.acewill.mobile.pos.config.MyApplication;
import cn.acewill.mobile.pos.config.Store;


/**
 * Created by DHH on 2017/9/19.
 */

public class PosSinUsbScreenController {
    private static PosSinUsbScreenController instance;
    private static Store store;
    private String mCustDspType = null;
    private String mLedPort = null;
    // 钱箱设备实例
//    private CashDrawer mCashDrawer = null;
    private boolean isPosSinScreenOpen = false;//posSin LED副屏是否可以使用

    public static PosSinUsbScreenController getInstance() {
        if (instance == null) {
            instance = new PosSinUsbScreenController();
            store = Store.getInstance(MyApplication.getInstance());
        }
        return instance;
    }

    /**
     * 判断LED副屏是否可用
     *
     * @return
     */
    public boolean isPosSinScreenOpen() {
        return isPosSinScreenOpen;
    }

    public void setPosSinScreenOpen(boolean posSinScreenOpen) {
        isPosSinScreenOpen = posSinScreenOpen;
    }

    public void init() {
//        try {
//            com.posin.device.SDK.init(MyApplication.getInstance().getContext());
//            mCashDrawer = CashDrawer.newInstance();
//        } catch (Throwable throwable) {
//            throwable.printStackTrace();
//        }
//        loadSystemProperties();
//        /*
//         * 判读系统是否配置LED数码管客显
//		 */
//        if (!"led".equals(mCustDspType)) {
//            MyApplication.getInstance().ShowToast(ToolsUtils.returnXMLStr("the_machine_has_no_LED_off"));
//        }
//        isPosSinScreenOpen = true;
//        if(isPosSinScreenOpen)
//        {
//            ledClear();
//        }
    }

    /**
     * 加载系统配置
     *
     * @return
     */
    private Properties loadSystemProperties() {
        Properties p = new Properties();
        FileInputStream is = null;

        try {
            is = new FileInputStream("/system/build.prop");
            p.load(is);
            mCustDspType = p.getProperty("ro.customerdisplay.type", "lcd");
            mLedPort = p.getProperty("ro.customerdisplay.port", "/dev/ttyACM0");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return p;
    }

    /**
     * 打开钱箱
     */
    public void openCashBox() {
//        if(isPosSinScreenOpen)
//        {
//            try {
//                mCashDrawer.kickOutPin2(100);
//            } catch (Throwable e) {
//                e.printStackTrace();
//                MyApplication.getInstance().ShowToast(ToolsUtils.returnXMLStr("open_cashbox_error")+"," + e.getMessage() + "!");
//            }
//        }
    }

    /**
     * 显示单价
     *
     * @param value 金额
     */
    public void ledDisplayPrice(String value) {
//        if(isPosSinScreenOpen)
//        {
//            LedCustomerDisplay cd = null;
//            try {
//                cd = new LedCustomerDisplay(mLedPort);
//                cd.displayPrice(value);
//            } catch (Throwable e) {
//                e.printStackTrace();
//                MyApplication.getInstance().ShowToast(ToolsUtils.returnXMLStr("the_secondary_screen_shows_an_error")+"," + e.getMessage() + "!");
//            } finally {
//                if (cd != null) {
//                    cd.close();
//                }
//            }
//        }
    }

    /**
     * 显示总计
     *
     * @param value 金额
     */
    public void ledDisplayTotal(String value) {
//        if(isPosSinScreenOpen)
//        {
//            LedCustomerDisplay cd = null;
//            try {
//                cd = new LedCustomerDisplay(mLedPort);
//                cd.displayTotal(value);
//            } catch (Throwable e) {
//                e.printStackTrace();
//                MyApplication.getInstance().ShowToast(ToolsUtils.returnXMLStr("the_secondary_screen_shows_an_error")+"," + e.getMessage() + "!");
//            } finally {
//                if (cd != null) {
//                    cd.close();
//                }
//            }
//        }
    }

    /**
     * 显示付款
     *
     * @param value 金额
     */
    public void ledDisplayPayment(String value) {
//        if(isPosSinScreenOpen)
//        {
//            LedCustomerDisplay cd = null;
//            try {
//                cd = new LedCustomerDisplay(mLedPort);
//                cd.displayPayment(value);
//            } catch (Throwable e) {
//                e.printStackTrace();
//                MyApplication.getInstance().ShowToast(ToolsUtils.returnXMLStr("the_secondary_screen_shows_an_error")+"," + e.getMessage() + "!");
//            } finally {
//                if (cd != null) {
//                    cd.close();
//                }
//            }
//        }
    }

    /**
     * 显示找零
     *
     * @param value 金额
     */
    public void ledDisplayChange(String value) {
//        if(isPosSinScreenOpen)
//        {
//            LedCustomerDisplay cd = null;
//            try {
//                cd = new LedCustomerDisplay(mLedPort);
//                cd.displayChange(value);
//            } catch (Throwable e) {
//                e.printStackTrace();
//                MyApplication.getInstance().ShowToast(ToolsUtils.returnXMLStr("the_secondary_screen_shows_an_error")+"," + e.getMessage() + "!");
//            } finally {
//                if (cd != null) {
//                    cd.close();
//                }
//            }
//        }
    }


    /**
     * 清屏
     */
    public void ledClear() {
//        if(isPosSinScreenOpen)
//        {
//            LedCustomerDisplay cd = null;
//            try {
//                cd = new LedCustomerDisplay(mLedPort);
//                cd.clear();
//            } catch (Throwable e) {
//                e.printStackTrace();
//                MyApplication.getInstance().ShowToast(ToolsUtils.returnXMLStr("clean_screen_error")+"," + e.getMessage() + "!");
//            } finally {
//                if (cd != null) {
//                    cd.close();
//                }
//            }
//        }
    }




}
