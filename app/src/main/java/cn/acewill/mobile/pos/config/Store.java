package cn.acewill.mobile.pos.config;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 商户appId=1，品牌brandId=1，门店storeId=1,区域sectionId=1
 * Created by DHH on 2016/6/16.
 */
public class Store {
    /**
     * 商户id
     */
    private static String appId;
    /**
     * 品牌id
     */
    private static String brandId;
    /**
     * 门店id
     */
    private static String storeId;
    /**
     * 区域id
     */
    private static String sectionId;
    /**
     * 门店名称
     */
    public static String storeName = "";
    /**
     * 联系电话
     */
    public static String storePhone = "";
    /**
     * 门店地址
     */
    public static String storeAddress = "";
    /**
     * 设备名称
     */
    private static String deviceName;
    /**
     * 终端设备描述
     */
    private static String terminalTypeStr;
    /**
     * 服务器地址
     */
    private static String serviceAddress;
    /**
     * 门店结束时间
     */
    private String storeEndTime;
    /**
     * 设备Id
     */
    private static String storePosId;
    /**
     * 端口号
     */
    private static String storePort;
    /**
     * 店铺网址
     */
    private static String websit;

    /**
     * app版本号
     */
    private static String versionCode;

    private static boolean saveState = false;

    /**
     * 菜品显示样式 false文本  true图文
     */
    private static boolean dishShowStyle = false;

    /**
     * 是否接收网上订单
     */
    private static boolean receiveNetOrder = false;
    /**
     * 是否自动接收网上订单
     */
    private static boolean autoMaticNetOrder = false;

    /**
     * 是否在厨房单上显示菜品金额
     */
    private static boolean kitMoneyStyle = false;
    /**
     * 是否开启快捷扫码下单
     */
    private static boolean isOpenQuickOrder = false;

    /**
     * 是否记住密码
     */
    private static boolean isRememberPw = true;

    /**
     * 订单管理-网上订单是否优先显示
     */
    private static boolean isNetOrderFirstShow = true;
    /**
     * 是否在总单小票上打印KDS叫号、消号条码
     */
    private static boolean isSummaryShowKDSCode = true;
    /**
     * 外卖单是否自动接收
     */
    private static boolean isWaiMaiOrderReceive = false;
    /**
     * 外卖单是否要输入顾客信息
     */
    private static boolean isWaiMaiGuestInfo = false;
    /**
     * 是否支持叫货功能
     */
    private static boolean isSupportCallGoods = false;
    /**
     * 线上支付是否要输入金额
     */
    private static boolean isCheckOutInPutMoney = true;
    /**
     * 订单详情界面是否显示退菜按钮
     */
    private static boolean isShowReDishButton = true;

    /**
     * 客显屏选中的图片id
     */
    private static String selectIds;
    /**
     * //单位分钟；是指查询离就餐还有多少时间的订单
     */
    private int preordertime;

    /**
     * //网上订单自动下单交易限额
     */
    private int orderTradingLiMint;

    /**
     * 吉野家服务器地址
     */
    private static String serviceAddressJyj;

    /**
     * 是否下单到吉野家服务器
     */
    private static boolean isCreateOrderJyj = false;

    /**
     * 吉野家端口号
     */
    private static String storePortJyj;

    /**
     * KDS服务器地址
     */
    private static String kdsServer;

    /**
     * KDS端口号
     */
    private static String kdsPort;

    /**
     * 服务器用来绑定设备所用到的十位token码,用来替代以前的macAddress、序列号
     */
    private static String tenTokenStr;

    /**
     * 选中的打印机列表(用逗号隔开)
     */
    private static String printerSelect;

    /**
     * 扫码方式:正扫、反扫
     */
    private static boolean front;

    /**
     * 外卖配送费
     */
    private static float saleMoney;

    private static float tareWeight;
    /**
     * 收银打印机的Id
     */
    private static int cashPrinterId;

    private static int cashKdsId;

    private static int secondaryPrinterId;
    /**
     * 外卖打印机
     */
    private static int takeoutPrinterid;
    /**
     * 门店绑定的唯一标识,绑定成功时需要保存到本地方便下次验证是否是在本机绑定的后台F码
     */
    private static String bindUUID;
    /**
     * 软件的语言
     */
    private static String languageSett;
    /**
     * 是否打印套餐名
     */
    private static boolean isPrintPackageName = true;

    private static boolean isPrintQrCode = true;

    private static Store store;
    private static Context mContext;
    private SharedPreferences spfPreferences;
    private SharedPreferences.Editor editor;
    /**
     * 默认查询离就餐时间订单
     */
    private int default_preordertime = 30;
    private int default_orderTrading = 100;


    /**
     * 终端授权码
     */
    private String terminalMac;

    private Store() {
        if (mContext == null)
            return;
        spfPreferences = mContext.getSharedPreferences("storeInfo", 0);
        editor = spfPreferences.edit();
    }

    public static Store getInstance(Context context) {
        mContext = context;
        if (store == null) {
            Lock lock = new ReentrantLock();
            lock.lock();
            if (store == null) {
                store = new Store();
            }
            lock.unlock();
        }

        return store;
    }

    /**
     * 商户Id
     *
     * @param appId
     */
    public void setStoreAppId(String appId) {
        editor.putString("appId", appId)
                .commit();
    }

    /**
     * 返回 appId
     *
     * @return
     */
    public String getStoreAppId() {
        appId = spfPreferences.getString("appId", "");
        return appId;
    }

    /**
     * 品牌Id
     *
     * @param brandId
     */
    public void setBrandId(String brandId) {
        editor.putString("brandId", brandId).commit();
    }

    /**
     * 返回 brandId
     *
     * @return
     */
    public String getBrandId() {
        brandId = spfPreferences.getString("brandId", "");
        return brandId;
    }

    /**
     * 门店Id
     *
     * @param storeId
     */
    public void setStoreId(String storeId) {
        editor.putString("storeId", storeId).commit();
    }

    /**
     * 返回 storeId
     *
     * @return
     */
    public String getStoreId() {
        storeId = spfPreferences.getString("storeId", "");
        return storeId;
    }

    /**
     * 设备名称
     *
     * @param DeviceName
     */
    public void setDeviceName(String DeviceName) {
        editor.putString("DeviceName", DeviceName).commit();
    }

    /**
     * 返回 设备名称
     *
     * @return
     */
    public String getDeviceName() {
        deviceName = spfPreferences.getString("DeviceName", "");
        return deviceName;
    }

    /**
     * 设备名称
     */
    public void setTerminalTypeStr(String terminalTypeStr) {
        editor.putString("terminalTypeStr", terminalTypeStr).commit();
    }

    /**
     * 返回 设备名称
     *
     * @return
     */
    public String getTerminalTypeStr() {
        terminalTypeStr = spfPreferences.getString("terminalTypeStr", "");
        return terminalTypeStr;
    }

    /**
     * 服务器地址
     *
     * @param ServiceAddress
     */
    public void setServiceAddress(String ServiceAddress) {
        editor.putString("ServiceAddress", ServiceAddress).commit();
    }

    /**
     * 返回 服务器地址
     *
     * @return
     */
    public String getServiceAddress() {
        serviceAddress = spfPreferences.getString("ServiceAddress", "");
        return serviceAddress;
    }


    /**
     * 本机Id
     *
     * @param PosId
     */
    public void setStorePosId(String PosId) {
        editor.putString("PosId", PosId).commit();
    }

    /**
     * 返回 本机Id
     *
     * @return
     */
    public String getStorePosId() {
        storePosId = spfPreferences.getString("PosId", "");
        return storePosId;
    }

    /**
     * 端口号
     *
     * @param StorePort
     */
    public void setStorePort(String StorePort) {
        editor.putString("StorePort", StorePort).commit();
    }

    /**
     * 返回 端口号
     *
     * @return
     */
    public String getStorePort() {
        storePort = spfPreferences.getString("StorePort", "");
        return storePort;
    }


    public boolean getSaveState() {
        //        if (!saveState)
        saveState = spfPreferences.getBoolean("saveState", false);
        return saveState;
    }

    public void setSaveState(boolean saveState) {
        editor.putBoolean("saveState", saveState);
        editor.commit();
    }

    public boolean getDishShowStyle() {
        dishShowStyle = spfPreferences.getBoolean("dishShowStyle", false);
        return dishShowStyle;
    }

    public void setDishShowStyle(boolean dishShowStyle) {
        editor.putBoolean("dishShowStyle", dishShowStyle);
        editor.commit();
    }

    public String getSelectIds() {
        selectIds = spfPreferences.getString("selectIds", "");
        return selectIds;
    }

    public void setSelectIds(String selectIds) {
        editor.putString("selectIds", selectIds);
        editor.commit();
    }

    public String getBindUUID() {
        bindUUID = spfPreferences.getString("bindUUID", "");
        return bindUUID;
    }

    public void setBindUUID(String bindUUID) {
        editor.putString("bindUUID", bindUUID);
        editor.commit();
    }

    public String getAppId() {
        appId = spfPreferences.getString("appId", "");
        return appId;
    }

    public void setAppId(String appId) {
        editor.putString("appId", appId);
        editor.commit();
    }

    public String getKdsServer() {
        kdsServer = spfPreferences.getString("kdsServer", "");
        return kdsServer;
    }

    public void setKdsServer(String kdsServer) {
        editor.putString("kdsServer", kdsServer);
        editor.commit();
    }

    public String getKdsPort() {
        kdsPort = spfPreferences.getString("kdsPort", "");
        return kdsPort;
    }

    public void setKdsPort(String kdsPort) {
        editor.putString("kdsPort", kdsPort);
        editor.commit();
    }

    public String getSectionId() {
        sectionId = spfPreferences.getString("sectionId", "");
        return sectionId;
    }

    public void setSectionId(String sectionId) {
        editor.putString("sectionId", sectionId);
        editor.commit();
    }

    public String getStoreName() {
        storeName = spfPreferences.getString("storeName", "");
        return storeName;
    }

    public void setStoreName(String storeName) {
        editor.putString("storeName", storeName);
        editor.commit();
    }

    public String getStorePhone() {
        storePhone = spfPreferences.getString("storePhone", "");
        return storePhone;
    }

    public void setStorePhone(String storePhone) {
        editor.putString("storePhone", storePhone);
        editor.commit();
    }

    public String getStoreAddress() {
        storeAddress = spfPreferences.getString("storeAddress", "");
        return storeAddress;
    }

    public void setStoreAddress(String storeAddress) {
        editor.putString("storeAddress", storeAddress);
        editor.commit();
    }

    public String getWebsit() {
        websit = spfPreferences.getString("websit", "");
        return websit;
    }

    public void setWebsit(String websit) {
        editor.putString("websit", websit);
        editor.commit();
    }

    public String getVersionCode() {
        versionCode = spfPreferences.getString("versionCode", "");
        return versionCode;
    }

    public void setVersionCode(String versionCode) {
        editor.putString("versionCode", versionCode);
        editor.commit();
    }

    public int getPreordertime() {
        preordertime = spfPreferences.getInt("preordertime", default_preordertime);
        return preordertime;
    }

    public void setPreordertime(int preordertime) {
        editor.putInt("preordertime", preordertime);
        editor.commit();
    }


    public int getOrderTradingLiMint() {
        orderTradingLiMint = spfPreferences.getInt("orderTradingLiMint", default_orderTrading);
        return orderTradingLiMint;
    }

    public void setOrderTradingLiMint(int orderTradingLiMint) {
        editor.putInt("orderTradingLiMint", orderTradingLiMint);
        editor.commit();
    }

    public int getCashPrinterId() {
        cashPrinterId = spfPreferences.getInt("cashPrinterId", 0);
        return cashPrinterId;
    }

    public void setCashPrinterId(int cashPrinterId) {
        editor.putInt("cashPrinterId", cashPrinterId);
        editor.commit();
    }

    public int getCashKdsId() {
        cashKdsId = spfPreferences.getInt("cashKdsId", 0);
        return cashKdsId;
    }

    public void setCashKdsId(int cashKdsId) {
        editor.putInt("cashKdsId", cashKdsId);
        editor.commit();
    }

    public int getSecondaryPrinterId() {
        secondaryPrinterId = spfPreferences.getInt("secondaryPrinterId", 0);
        return secondaryPrinterId;
    }

    public void setSecondaryPrinterId(int secondaryPrinterId) {
        editor.putInt("secondaryPrinterId", secondaryPrinterId);
        editor.commit();
    }

    public int getTakeoutPrinterId() {
        takeoutPrinterid = spfPreferences.getInt("takeoutPrinterid", 0);
        return takeoutPrinterid;
    }

    public void setTakeoutPrinterId(int takeoutPrinterid) {
        editor.putInt("takeoutPrinterid", takeoutPrinterid);
        editor.commit();
    }

    public String getPrinterSelect() {
        printerSelect = spfPreferences.getString("printerSelect", "");
        return printerSelect;
    }

    public void setPrinterSelect(String printerSelect) {
        editor.putString("printerSelect", printerSelect);
        editor.commit();
    }

    public boolean isFront() {
        front = spfPreferences.getBoolean("front", false);
        return front;
    }

    public void setFront(boolean front) {
        editor.putBoolean("front", front);
        editor.commit();
    }

    public float getSaleMoney() {
        saleMoney = spfPreferences.getFloat("saleMoney", 0);
        return saleMoney;
    }

    public void setSaleMoney(float saleMoney) {
        editor.putFloat("saleMoney", saleMoney);
        editor.commit();
    }

    public float getTareWeight() {
        saleMoney = spfPreferences.getFloat("TareWeight", 0);
        return saleMoney;
    }

    public void setTareWeight(float tareWeight) {
        editor.putFloat("TareWeight", tareWeight);
        editor.commit();
    }

    public String getTenTokenStr() {
        tenTokenStr = spfPreferences.getString("tenTokenStr", "");
        return tenTokenStr;
    }

    public void setTenTokenStr(String tenTokenStr) {
        editor.putString("tenTokenStr", tenTokenStr);
        editor.commit();
    }

    public boolean getReceiveNetOrder() {
        receiveNetOrder = spfPreferences.getBoolean("receiveNetOrder", false);
        return receiveNetOrder;
    }

    public void setReceiveNetOrder(boolean receiveNetOrder) {
        editor.putBoolean("receiveNetOrder", receiveNetOrder);
        editor.commit();
    }

    public boolean getAutoMaticNetOrder() {
        autoMaticNetOrder = spfPreferences.getBoolean("autoMaticNetOrder", false);
        return autoMaticNetOrder;
    }

    public void setAutoMaticNetOrder(boolean autoMaticNetOrder) {
        editor.putBoolean("autoMaticNetOrder", autoMaticNetOrder);
        editor.commit();
    }

    public boolean getKitMoneyStyle() {
        kitMoneyStyle = spfPreferences.getBoolean("kitMoneyStyle", false);
        return kitMoneyStyle;
    }

    public void setKitMoneyStyle(boolean kitMoneyStyle) {
        editor.putBoolean("kitMoneyStyle", kitMoneyStyle);
        editor.commit();
    }

    public boolean isPrintPackageName() {
        isPrintPackageName = spfPreferences.getBoolean("isPrintPackageName", true);
        return isPrintPackageName;
    }

    public void setPrintPackageName(boolean isPrintPackageName) {
        editor.putBoolean("isPrintPackageName", isPrintPackageName);
        editor.commit();
    }

    public boolean isPrintQRCode() {
        isPrintQrCode = spfPreferences.getBoolean("isPrintQrCode", true);
        return isPrintQrCode;
    }

    public void setPrintQRCode(boolean isPrintQrCode) {
        editor.putBoolean("isPrintQrCode", isPrintQrCode);
        editor.commit();
    }

    public boolean isNetOrderFirstShow() {
        isNetOrderFirstShow = spfPreferences.getBoolean("isNetOrderFirstShow", true);
        return isNetOrderFirstShow;
    }

    public void setNetOrderFirstShow(boolean isNetOrderFirstShow) {
        editor.putBoolean("isNetOrderFirstShow", isNetOrderFirstShow);
        editor.commit();
    }

    public boolean isSummaryShowKDSCode() {
        isSummaryShowKDSCode = spfPreferences.getBoolean("isSummaryShowKDSCode", false);
        return isSummaryShowKDSCode;
    }

    public void setSummaryShowKDSCode(boolean isSummaryShowKDSCode) {
        editor.putBoolean("isSummaryShowKDSCode", isSummaryShowKDSCode);
        editor.commit();
    }

    public boolean isWaiMaiOrderReceive() {
        isWaiMaiOrderReceive = spfPreferences.getBoolean("isWaiMaiOrderReceive", true);
        return isWaiMaiOrderReceive;
    }

    public void setWaiMaiOrderReceive(boolean isWaiMaiOrderReceive) {
        editor.putBoolean("isWaiMaiOrderReceive", isWaiMaiOrderReceive);
        editor.commit();
    }

    public boolean isWaiMaiGuestInfo() {
        isWaiMaiGuestInfo = spfPreferences.getBoolean("isWaiMaiGuestInfo", false);
        return isWaiMaiGuestInfo;
    }

    public void setWaiMaiGuestInfo(boolean isWaiMaiGuestInfo) {
        editor.putBoolean("isWaiMaiGuestInfo", isWaiMaiGuestInfo);
        editor.commit();
    }

    public boolean isSupportCallGoods() {
        isSupportCallGoods = spfPreferences.getBoolean("isSupportCallGoods", false);
        return isSupportCallGoods;
    }

    public void setSupportCallGoods(boolean isSupportCallGoods) {
        editor.putBoolean("isSupportCallGoods", isSupportCallGoods);
        editor.commit();
    }

    public boolean isCheckOutInPutMoney() {
        isCheckOutInPutMoney = spfPreferences.getBoolean("isCheckOutInPutMoney", true);
        return isCheckOutInPutMoney;
    }

    public void setCheckOutInPutMoney(boolean isCheckOutInPutMoney) {
        editor.putBoolean("isCheckOutInPutMoney", isCheckOutInPutMoney);
        editor.commit();
    }

    public boolean isShowReDishButton() {
        isShowReDishButton = spfPreferences.getBoolean("isShowReDishButton", true);
        return isShowReDishButton;
    }

    public void setShowReDishButton(boolean isShowReDishButton) {
        editor.putBoolean("isShowReDishButton", isShowReDishButton);
        editor.commit();
    }

    public boolean isOpenQuickOrder() {
        isOpenQuickOrder = spfPreferences.getBoolean("isOpenQuickOrder", false);
        return isOpenQuickOrder;
    }

    public void setOpenQuickOrder(boolean isOpenQuickOrder) {
        editor.putBoolean("isOpenQuickOrder", isOpenQuickOrder);
        editor.commit();
    }

    public boolean isRememberPw() {
        isRememberPw = spfPreferences.getBoolean("isRememberPw", false);
        return isRememberPw;
    }

    public void setRememberPw(boolean isRememberPw) {
        editor.putBoolean("isRememberPw", isRememberPw);
        editor.commit();
    }

    public String getTerminalMac() {
        terminalMac = spfPreferences.getString("terminalMac","");
        return terminalMac;
    }

    public void setTerminalMac(String terminalMac) {
        editor.putString("terminalMac", terminalMac);
        editor.commit();
    }

    public String getStoreEndTime() {
        storeEndTime = spfPreferences.getString("storeEndTime","");
        return storeEndTime;
    }

    public void setStoreEndTime(String storeEndTime) {
        editor.putString("storeEndTime", storeEndTime);
        editor.commit();
    }

    public String getLanguageSett() {
        storeEndTime = spfPreferences.getString("languageSett","zh");
        return storeEndTime;
    }

    public void setLanguageSett(String language) {
        editor.putString("languageSett", language);
        editor.commit();
    }

    /**
     * 吉野家服务器地址
     *
     * @param serviceAddressJyj
     */
    public void setServiceAddressJyj(String serviceAddressJyj) {
        editor.putString("serviceAddressJyj", serviceAddressJyj).commit();
    }

    /**
     * 返回 服务器地址
     *
     * @return
     */
    public String getServiceAddressJyj() {
        serviceAddressJyj = spfPreferences.getString("serviceAddressJyj", "");
        return serviceAddressJyj;
    }

    /**
     * 吉野家端口号
     *
     * @param storePortJyj
     */
    public void setStorePortJyj(String storePortJyj) {
        editor.putString("storePortJyj", storePortJyj).commit();
    }

    /**
     * 返回 端口号
     *
     * @return
     */
    public String getStorePortJyj() {
        storePortJyj = spfPreferences.getString("storePortJyj", "");
        return storePortJyj;
    }

    public boolean isCreateOrderJyj() {
        isCreateOrderJyj = spfPreferences.getBoolean("isCreateOrderJyj", false);
        return isCreateOrderJyj;
    }

    public void setCreateOrderJyj(boolean isCreateOrderJyj) {
        editor.putBoolean("isCreateOrderJyj", isCreateOrderJyj);
        editor.commit();
    }
}