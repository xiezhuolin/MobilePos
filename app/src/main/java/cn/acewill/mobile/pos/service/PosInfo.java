package cn.acewill.mobile.pos.service;

import java.util.List;

import cn.acewill.mobile.pos.factory.HostSelectionInterceptor;
import cn.acewill.mobile.pos.model.Customer;
import cn.acewill.mobile.pos.model.Definition;
import cn.acewill.mobile.pos.model.payment.WaimaiType;
import cn.acewill.mobile.pos.model.wsh.Account;
import cn.acewill.mobile.pos.model.wsh.WshDealPreview;


/**
 *  当前这台pos的运行时信息
 * Created by Acewill on 2016/8/1.
 */
public class PosInfo {
    private String appId; //商户的id
    private String brandId; //品牌的id
    private String storeId; //门店的id
    private String terminalName; //终端名称
    private String terminalMac; //终端mac地址
    private String terminalId;
    private String currentVersion;
    private String versionId;
    private int receiveNetOrder; //是否接受网络订单
    private String token;
    private String username; //当前登录的用户
    private String realname;; //当前登录的真实姓名
    private int userId;//当前登录用户的Id
    private String serverUrl;//服务器的路径，比如http://sz.canxingjian.com:18080, 可以不带端口
    private String phone; //门店的电话号码
    private String address; //门店的地址
    private String website; //门店主页 -- 可生成二维码
    private String cookie;
    private String logoPath;
    private int preordertime; //单位分钟；是指查询离就餐还有多少时间的订单
    private int orderTradingLiMint;//网上订单自动下单交易限额
    private Integer workShiftId;//班次ID
    private String workShiftName;//当前班次名称
    private List<Definition> definitionList;
    private Definition definition;
    private int customerAmount = 1;// 顾客人数
    private String comment = "";//订单备注
    private boolean isFreeOrder = false;//是否免单
    //    private long numberOfCustomer = 0;//人数
    //    private List<Long> tableIds = new ArrayList<Long>();
    //    private String tableName;
    private static PosInfo instance;
    private String orderType = "EAT_IN";
    private Integer labelPrinterHeight = 27;
    private Customer customer;//外卖顾客信息对象
    private int checkoutReceiptCounts = 0; //结账单需要打印几份
    private int guestReceiptCounts = 0; //客用小票需要打印几份

    private boolean isMemberCheckOut = false;//创建会员交易的时候如果什么方式都没有选择的话则把该笔支付记录记下来然后用别的支付方式支付的时候需要调用提交会员交易接口
    private String bizId;//会员交易业务流水号
    private WshDealPreview wshDealPreview;//提交会员交易用到的交易预览返回对象

    private String returnUserName;//退单人员名称
    private String authUserName;// 授权退单人员名称 当退单需要权限时，记录输入密码的那个员工的名称
    private WaimaiType waimaiType;//外卖类型
    private String menuName;//餐段名称
    private String terminalIp;//终端Ip
    private boolean isAddWaiMaiMoney = false;//是否曾经将外卖配送费加入到订单中了
    private String brandName;//品牌-门店  名称
    private Account accountMember;//微生活会员对象,用来筛选后台设置的促销打折活动

    private String tableNumber = "";
    private boolean isClickDish = true;//是否可点菜  判断是否可以点菜 为了防止暴力点击点菜按钮

    public boolean isClickDish() {
        return isClickDish;
    }

    public void setClickDish(boolean clickDish) {
        isClickDish = clickDish;
    }

    public Account getAccountMember() {
        return accountMember;
    }

    public void setAccountMember(Account accountMember) {
        this.accountMember = accountMember;
    }

    public boolean isAddWaiMaiMoney() {
        return isAddWaiMaiMoney;
    }

    public void setAddWaiMaiMoney(boolean addWaiMaiMoney) {
        isAddWaiMaiMoney = addWaiMaiMoney;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

//    private Long orderId;

    public boolean isMemberCheckOut() {
        return isMemberCheckOut;
    }

    public void setMemberCheckOut(boolean memberCheckOut) {
        isMemberCheckOut = memberCheckOut;
    }

    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
    }

    public WshDealPreview getWshDealPreview() {
        return wshDealPreview;
    }

    public void setWshDealPreview(WshDealPreview wshDealPreview) {
        this.wshDealPreview = wshDealPreview;
    }

    public static PosInfo getInstance() {
        if (instance == null) {
            instance = new PosInfo();
        }
        return instance;
    }

    public String getCurrentVersion() {
        return currentVersion;
    }

    public String getReturnUserName() {
        return returnUserName;
    }

    public void setReturnUserName(String returnUserName) {
        this.returnUserName = returnUserName;
    }

    public String getMenuName() {
        return menuName;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }

    public String getWorkShiftName() {
        return workShiftName;
    }

    public void setWorkShiftName(String workShiftName) {
        this.workShiftName = workShiftName;
    }

    public int getCheckoutReceiptCounts() {
        return checkoutReceiptCounts;
    }

    public void setCheckoutReceiptCounts(int checkoutReceiptCounts) {
        this.checkoutReceiptCounts = checkoutReceiptCounts;
    }

    public int getGuestReceiptCounts() {
        return guestReceiptCounts;
    }

    public void setGuestReceiptCounts(int guestReceiptCounts) {
        this.guestReceiptCounts = guestReceiptCounts;
    }

    public WaimaiType getWaimaiType() {
        return waimaiType;
    }

//    public Long getOrderId() {
//        return orderId;
//    }

//    public void setOrderId(Long orderId) {
//        this.orderId = orderId;
//    }

    public void setWaimaiType(WaimaiType waimaiType) {
        this.waimaiType = waimaiType;
    }

    public String getAuthUserName() {
        return authUserName;
    }

    public void setAuthUserName(String authUserName) {
        this.authUserName = authUserName;
    }

    public void setCurrentVersion(String currentVersion) {
        this.currentVersion = currentVersion;
    }

    public String getVersionId() {
        return versionId;
    }

    public void setVersionId(String versionId) {
        this.versionId = versionId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public static void setInstance(PosInfo instance) {
        PosInfo.instance = instance;
    }

    public String getTerminalId() {
        return terminalId;
    }

    public void setTerminalId(String terminalId) {
        this.terminalId = terminalId;
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
        //更新okhttp的baseUrl
        HostSelectionInterceptor.setBaseUrl(serverUrl);
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getBrandId() {
        return brandId;
    }

    public void setBrandId(String brandId) {
        this.brandId = brandId;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public String getTerminalName() {
        return terminalName;
    }

    public void setTerminalName(String terminalName) {
        this.terminalName = terminalName;
    }

    public String getTerminalMac() {
        return terminalMac;
    }

    public void setTerminalMac(String terminalMac) {
        this.terminalMac = terminalMac;
    }

    public int getReceiveNetOrder() {
        return receiveNetOrder;
    }

    public void setReceiveNetOrder(int receiveNetOrder) {
        this.receiveNetOrder = receiveNetOrder;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getCookie() {
        return cookie;
    }

    public void setCookie(String cookie) {
        this.cookie = cookie;
    }

    public int getPreordertime() {
        if(preordertime == 0)
        {
            preordertime = 10;
        }
        return preordertime;
    }

    public void setPreordertime(int preordertime) {
        this.preordertime = preordertime;
    }

    public int getOrderTradingLiMint() {
        if(orderTradingLiMint == 0)
        {
            orderTradingLiMint = 50;
        }
        return orderTradingLiMint;
    }

    public void setOrderTradingLiMint(int orderTradingLiMint) {
        this.orderTradingLiMint = orderTradingLiMint;
    }

    public Integer getWorkShiftId() {
        return workShiftId;
    }

    public void setWorkShiftId(Integer workShiftId) {
        this.workShiftId = workShiftId;
    }

    public String getLogoPath() {
        return logoPath;
    }

    public void setLogoPath(String logoPath) {
        this.logoPath = logoPath;
    }

    public List<Definition> getDefinitionList() {
        return definitionList;
    }

    public void setDefinitionList(List<Definition> definitionList) {
        this.definitionList = definitionList;
    }

    public Definition getDefinition() {
        return definition;
    }

    public void setDefinition(Definition definition) {
        this.definition = definition;
    }

    //    public List<Long> getTableIds() {
    //        return tableIds;
    //    }
    //
    //    public void addTableIds(Long number) {
    //        if(number >0)
    //        {
    //            tableIds.clear();
    //            tableIds.add(number);
    //        }
    //    }
    //
    //    public void addTableIds(long oldTableId,long newTableId)
    //    {
    //        if(oldTableId >0 && newTableId >0)
    //        {
    //            tableIds.clear();
    //            tableIds.add(oldTableId);
    //            tableIds.add(newTableId);
    //        }
    //    }
    //
    //    public Long getNumberOfCustomer() {
    //        return numberOfCustomer;
    //    }
    //
    //    public void setNumberOfCustomer(long numberOfCustomer) {
    //        this.numberOfCustomer = numberOfCustomer;
    //    }

    //    public String getTableName() {
    //        return tableName;
    //    }
    //
    //    public void setTableName(String tableName) {
    //        this.tableName = tableName;
    //    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public Integer getLabelPrinterHeight() {
        return labelPrinterHeight;
    }

    public void setLabelPrinterHeight(Integer labelPrinterHeight) {
        this.labelPrinterHeight = labelPrinterHeight;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public int getCustomerAmount() {
        if(customerAmount <= 0)
        {
            customerAmount = 1;
        }
        return customerAmount;
    }

    public void setCustomerAmount(int customerAmount) {
        this.customerAmount = customerAmount;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public boolean isFreeOrder() {
        return isFreeOrder;
    }

    public void setFreeOrder(boolean freeOrder) {
        isFreeOrder = freeOrder;
    }

    public String getTerminalIp() {
        return terminalIp;
    }

    public void setTerminalIp(String terminalIp) {
        this.terminalIp = terminalIp;
    }

    public String getRealname() {
        return realname;
    }

    public void setRealname(String realname) {
        this.realname = realname;
    }

    public String getTableNumber() {
        return tableNumber;
    }

    public void setTableNumber(String tableNumber) {
        this.tableNumber = tableNumber;
    }
}
