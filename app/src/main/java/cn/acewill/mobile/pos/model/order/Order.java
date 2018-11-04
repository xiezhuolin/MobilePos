package cn.acewill.mobile.pos.model.order;

import android.text.TextUtils;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ModelContainer;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import cn.acewill.mobile.pos.factory.AppDatabase;
import cn.acewill.mobile.pos.model.MarketObject;
import cn.acewill.mobile.pos.model.OrderStatus;
import cn.acewill.mobile.pos.model.PaymentList;
import cn.acewill.mobile.pos.model.WaimaiOrderExtraData;
import cn.acewill.mobile.pos.model.dish.CookingMethod;
import cn.acewill.mobile.pos.model.dish.Flavor;
import cn.acewill.mobile.pos.model.dish.Option;
import cn.acewill.mobile.pos.model.payment.WaimaiType;
import cn.acewill.mobile.pos.model.table.Table;
import cn.acewill.mobile.pos.model.wsh.Account;


/**
 * Created by Acewill on 2016/6/2.
 */
@com.raizlabs.android.dbflow.annotation.Table( name = "order", database = AppDatabase.class )
@ModelContainer
public class Order extends BaseModel implements Serializable {
    @Column
    @PrimaryKey( autoincrement = true )
    private long id;

    private String sequenceNumber;// 订单号码
    private int customerAmount;// 顾客人数
    private String comment = "";//订单说明
    //  private long bookingId; // -1表示没有预定号，

    private String appId;
    private String brandId;
    private String storeId;
    private String total;
    private String cost;
    private String source;
    private double discount;
    private double subtraction;
    private String orderType;
    private String orderTypeStr;
    private String callNumber;
    private String createdBy;
    private String saleUserId;
    private String tableNames = "";
    private String errPrinterStr = "";
    private PaymentStatus paymentStatus;
    private long createdAt; //这里不能用Date， 因为app的date格式可能和服务器不一样
    private long paidAt; //结账时间
    private List<Long> tableIds = new ArrayList<Long>();
    private Integer workShiftId;//班次ID
    private List<OrderItem> itemList = new CopyOnWriteArrayList<OrderItem>();
    private OrderStatus status;
    private List<PaymentList> paymentList;//如果是先支付，传递此参数，参数内容跟结账接口传递的Body相同
    private int printerType = -1;//打印订单类型  Constant.JsToAndroid 中有打印的详细类型
    private int tableStyle;//桌台操作类型   是否是搭台  开台   并台
    private int orderPrintType;
    private String createdAtStr;//订单产生时间

    private String customerName;//外卖顾客的姓名
    private String customerPhoneNumber;//外卖顾客的电话
    private String customerAddress;//外卖顾客的地址
    private String outerOrderid;//外部平台订单号


    //这四个参数用于打印小票使用
    private BigDecimal serviceMoney = new BigDecimal(0);//服务费
    private BigDecimal avtive_money = new BigDecimal(0);//营销活动或菜品优惠减免金额
    private String avtiveName;//营销活动名称
    private BigDecimal pay_money = new BigDecimal(0);//客付
    private BigDecimal give_money = new BigDecimal(0);//找零
    private BigDecimal takeoutFee = new BigDecimal(0);//外带费

    //打包费
    private BigDecimal take_money = new BigDecimal(0);

    //电子流水号，打印小票使用
    private String paymentNo;//微信、支付宝电子流水号

    private boolean createdOffline = false;
    /**
     * 是否是网上订单
     */
    private boolean isNetOrder = false;
    /**
     * 微生活结账会员对象
     */
    private Account accountMember;

    private List<OrderItem> orderItemList;
    private List<Table> desks; //桌台列表
    private List<CookingMethod> cookList;
    private List<Flavor> tasteList;
    private List<Option> optionList;

    private long tableId;

    //netOrder
    public BigDecimal refund = new BigDecimal("0");// 退款金额 -- 放到这里是为了出报表方便
    public String memberid; //对应微生活账号中的uid字段
    public String memberName;//对应微生活账号中的name字段
    public String memberPhoneNumber;//对应微生活账号中的phone字段
    public String customerid;
    public long deliverAt; // 门店接受订单时间
    public long preorderTime;//预订时间
    public String prepayId;
    public int payType;
    public String rejectReason;
    public BigDecimal shippingFee = new BigDecimal("0");//网上订单配送费用
    public Long netOrderid;//网络订单Id
    private Long refOrderId;//真实订单ID  退单或转台才会存在该字段  如果是退单的话，这里会保存原单的id
    public int netorderstatus;//门店未接收
    public boolean informKds;//是否通知kds打印
    public boolean informKitchen;//是否通知厨房打印
    public String changeTableName;//转台后的新桌台名称
    public String memberBizId;// String类型  微生活业务id
    public String memberGrade;//会员等级名

    public String tableid = "";//对应的桌台ID， -1代表没有桌台
    public int operation_type; //0：新下单；1：加菜单
    public long refNetOrderId;

    private List<MarketObject> marketList; //这个菜品上使用过的营销方案, 可以是单品的，也可以是全单的，全单的话需要按比例平摊到菜品上

    private int laberDishCount = 0;

    private String outerOrderIdView;
    private String outerOrderIdDaySeq;

    private String thirdPlatformOrderId;
    private String thirdPlatformOrderIdView;
    private String thirdPlatfromOrderIdDaySeq;


    private List<WaimaiOrderExtraData> waimaiOrderExtraDatas;

    private String hopeDeliverTime;//  --期望送达时间

    private String waimaiHasInvoiced;//  -- 是否开发票，1：开发票，其他不理会
    private String waimaiInvoiceTitle;// -- 发票抬头
    private String waimaiTaxpayerId;//  -- 发票税号

    /**
     * 退单人员名称
     */
    private String returnUserName;
    /**
     * 授权退单人员名称
     */
    private String authUserName;
    /**
     * 外卖类型
     */
    private WaimaiType waimaiType;
    /**
     * 餐段名称
     */
    private String menuName;

    /**
     * 是否是补打状态订单
     */
    private boolean isReprintState = false;
    /**
     * 补打时是否要打印电子发票二维码
     */
    private boolean isPrintQrcode = false;

    private Integer invoiced = 0;// 0 没有开票； 1 已经开票
    /**
     * 被抹零的金额
     */
    private BigDecimal wipingValue = new BigDecimal("0.00");

    /**
     * 挂单信息对象
     */
    private CardRecord cardRecord;

    /**
     * 补打是总单状态还是分单状态
     * 0正常 1分单  2总单
     */
    private int rushDishType = 0;

    public int getRushDishType() {
        return rushDishType;
    }

    public void setRushDishType(int rushDishType) {
        this.rushDishType = rushDishType;
    }

    public BigDecimal getWipingValue() {
        return wipingValue;
    }

    public void setWipingValue(BigDecimal wipingValue) {
        this.wipingValue = wipingValue;
    }

    public String getErrPrinterStr() {
        return errPrinterStr;
    }

    public void setErrPrinterStr(String errPrinterStr) {
        this.errPrinterStr = errPrinterStr;
    }

    public List<MarketObject> getMarketList() {
        return marketList;
    }

    public void setMarketList(List<MarketObject> marketList) {
        this.marketList = marketList;
    }

    public List<WaimaiOrderExtraData> getWaimaiOrderExtraDatas() {
        return waimaiOrderExtraDatas;
    }

    public void setWaimaiOrderExtraDatas(List<WaimaiOrderExtraData> waimaiOrderExtraDatas) {
        this.waimaiOrderExtraDatas = waimaiOrderExtraDatas;
    }

    public String getHopeDeliverTime() {
        return hopeDeliverTime;
    }

    public void setHopeDeliverTime(String hopeDeliverTime) {
        this.hopeDeliverTime = hopeDeliverTime;
    }

    public String getWaimaiHasInvoiced() {
        return waimaiHasInvoiced;
    }

    public void setWaimaiHasInvoiced(String waimaiHasInvoiced) {
        this.waimaiHasInvoiced = waimaiHasInvoiced;
    }

    public String getWaimaiInvoiceTitle() {
        return waimaiInvoiceTitle;
    }

    public void setWaimaiInvoiceTitle(String waimaiInvoiceTitle) {
        this.waimaiInvoiceTitle = waimaiInvoiceTitle;
    }

    public String getWaimaiTaxpayerId() {
        return waimaiTaxpayerId;
    }

    public void setWaimaiTaxpayerId(String waimaiTaxpayerId) {
        this.waimaiTaxpayerId = waimaiTaxpayerId;
    }

    public boolean isPrintQrcode() {
        return isPrintQrcode;
    }

    public boolean isNetOrder() {
        return isNetOrder;
    }

    public void setNetOrder(boolean netOrder) {
        isNetOrder = netOrder;
    }

    public String getOuterOrderIdView() {
        return outerOrderIdView;
    }

    public void setOuterOrderIdView(String outerOrderIdView) {
        this.outerOrderIdView = outerOrderIdView;
    }

    public String getOuterOrderIdDaySeq() {
        return outerOrderIdDaySeq;
    }

    public void setOuterOrderIdDaySeq(String outerOrderIdDaySeq) {
        this.outerOrderIdDaySeq = outerOrderIdDaySeq;
    }

    public String getThirdPlatformOrderId() {
        return thirdPlatformOrderId;
    }

    public void setThirdPlatformOrderId(String thirdPlatformOrderId) {
        this.thirdPlatformOrderId = thirdPlatformOrderId;
    }

    public String getThirdPlatformOrderIdView() {
        return thirdPlatformOrderIdView;
    }

    public void setThirdPlatformOrderIdView(String thirdPlatformOrderIdView) {
        this.thirdPlatformOrderIdView = thirdPlatformOrderIdView;
    }

    public String getThirdPlatfromOrderIdDaySeq() {
        return thirdPlatfromOrderIdDaySeq;
    }

    public void setThirdPlatfromOrderIdDaySeq(String thirdPlatfromOrderIdDaySeq) {
        this.thirdPlatfromOrderIdDaySeq = thirdPlatfromOrderIdDaySeq;
    }

    public void setPrintQrcode(boolean printQrcode) {
        isPrintQrcode = printQrcode;
    }

    public boolean isReprintState() {
        return isReprintState;
    }

    public void setReprintState(boolean reprintState) {
        isReprintState = reprintState;
    }

    public List<Flavor> getTasteList() {
        return tasteList;
    }

    public void setTasteList(List<Flavor> tasteList) {
        this.tasteList = tasteList;
    }

    public boolean isInformKitchen() {
        return informKitchen;
    }

    public void setInformKitchen(boolean informKitchen) {
        this.informKitchen = informKitchen;
    }

    public boolean isInformKds() {
        return informKds;
    }

    public void setInformKds(boolean informKds) {
        this.informKds = informKds;
    }

    public String getChangeTableName() {
        return changeTableName;
    }

    public void setChangeTableName(String changeTableName) {
        this.changeTableName = changeTableName;
    }

    public BigDecimal getRefund() {
        return refund;
    }

    public void setRefund(BigDecimal refund) {
        this.refund = refund;
    }

    public long getRefOrderId() {
        return refOrderId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public String getMemberPhoneNumber() {
        return memberPhoneNumber;
    }

    public void setMemberPhoneNumber(String memberPhoneNumber) {
        this.memberPhoneNumber = memberPhoneNumber;
    }

    public String getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(String sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
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

/*    public long getBookingId() {
        return bookingId;
    }

    public void setBookingId(long bookingId) {
        this.bookingId = bookingId;
    }*/

    public List<OrderItem> getOrderItemList() {
        return orderItemList;
    }

    public void setOrderItemList(List<OrderItem> orderItemList) {
        this.orderItemList = orderItemList;
    }

    public List<Table> getDesks() {
        return desks;
    }

    public void setDesks(List<Table> desks) {
        this.desks = desks;
    }

    public List<OrderItem> getItemList() {
        return itemList;
    }

    public void setItemList(List<OrderItem> itemList) {
        this.itemList = itemList;
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

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public double getSubtraction() {
        return subtraction;
    }

    public void setSubtraction(double subtraction) {
        this.subtraction = subtraction;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getSaleUserId() {
        return saleUserId;
    }

    public void setSaleUserId(String saleUserId) {
        this.saleUserId = saleUserId;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getPaidAt() {
        return paidAt;
    }

    public void setPaidAt(long paidAt) {
        this.paidAt = paidAt;
    }

    public List<Long> getTableIds() {
        return tableIds;
    }

    public void setTableIds(List<Long> tableIds) {
        this.tableIds = tableIds;
    }

    public void addTableIds(Long number) {
        if (number > 0) {
            if (tableIds == null) {
                tableIds = new ArrayList<Long>();
            }
            tableIds.clear();
            tableIds.add(number);
        }
    }

    public void addTableIds(long oldTableId, long newTableId) {
        if (oldTableId > 0 && newTableId > 0) {
            tableIds.clear();
            tableIds.add(oldTableId);
            tableIds.add(newTableId);
        }
    }

    public String getCallNumber() {
        return callNumber;
    }

    public void setCallNumber(String callNumber) {
        this.callNumber = callNumber;
    }

    public List<PaymentList> getPaymentList() {
        return paymentList;
    }

    public void setPaymentList(List<PaymentList> paymentList) {
        this.paymentList = paymentList;
    }

    public String getOrderTypeStr() {
        return orderTypeStr;
    }

    public void setOrderTypeStr(String orderTypeStr) {
        this.orderTypeStr = orderTypeStr;
    }

    public Integer getWorkShiftId() {
        return workShiftId;
    }


    public void setWorkShiftId(Integer workShiftId) {
        this.workShiftId = workShiftId;
    }

    public List<Option> getOptionList() {
        return optionList;
    }

    public void setOptionList(List<Option> optionList) {
        this.optionList = optionList;
    }

    public long getTableId() {
        return tableId;
    }

    public void setTableId(long tableId) {
        if (tableIds != null && tableIds.size() > 0) {
        } else {
            addTableIds(tableId);
        }
        this.tableId = tableId;
    }

    public int getPrinterType() {
        return printerType;
    }

    public void setPrinterType(int printerType) {
        this.printerType = printerType;
    }

    public int getTableStyle() {
        return tableStyle;
    }

    public void setTableStyle(int tableStyle) {
        this.tableStyle = tableStyle;
    }

    public String getTableNames() {
        if(!TextUtils.isEmpty(changeTableName))
        {
            return changeTableName;
        }
        return tableNames;
    }

    public void setTableNames(String tableNames) {
        this.tableNames = tableNames;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public String getCreatedAtStr() {
        return createdAtStr;
    }

    public void setCreatedAtStr(String createdAtStr) {
        this.createdAtStr = createdAtStr;
    }

    public int getOrderPrintType() {
        return orderPrintType;
    }

    public void setOrderPrintType(int orderPrintType) {
        this.orderPrintType = orderPrintType;
    }

    public BigDecimal getServiceMoney() {
        return serviceMoney;
    }

    public void setServiceMoney(BigDecimal serviceMoney) {
        this.serviceMoney = serviceMoney;
    }

    public BigDecimal getAvtive_money() {
        return avtive_money;
    }

    public void setAvtive_money(BigDecimal avtive_money) {
        this.avtive_money = avtive_money;
    }

    public boolean isCreatedOffline() {
        return createdOffline;
    }

    public void setCreatedOffline(boolean createdOffline) {
        this.createdOffline = createdOffline;
    }

    public BigDecimal getGive_money() {
        return give_money;
    }

    public void setGive_money(BigDecimal give_money) {
        this.give_money = give_money;
    }

    public BigDecimal getPay_money() {
        return pay_money;
    }

    public void setPay_money(BigDecimal pay_money) {
        this.pay_money = pay_money;
    }

    public BigDecimal getTake_money() {
        return take_money;
    }

    public void setTake_money(BigDecimal take_money) {
        this.take_money = take_money;
    }

    public String getPaymentNo() {
        return paymentNo;
    }

    public void setPaymentNo(String paymentNo) {
        this.paymentNo = paymentNo;
    }

    public String getAvtiveName() {
        return avtiveName;
    }

    public void setAvtiveName(String avtiveName) {
        this.avtiveName = avtiveName;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerPhoneNumber() {
        return customerPhoneNumber;
    }

    public void setCustomerPhoneNumber(String customerPhoneNumber) {
        this.customerPhoneNumber = customerPhoneNumber;
    }

    public String getCustomerAddress() {
        return customerAddress;
    }

    public void setCustomerAddress(String customerAddress) {
        this.customerAddress = customerAddress;
    }

    public String getOuterOrderid() {
        return outerOrderid;
    }

    public void setOuterOrderid(String outerOrderid) {
        this.outerOrderid = outerOrderid;
    }

    public int getOperation_type() {
        return operation_type;
    }

    public void setOperation_type(int operation_type) {
        this.operation_type = operation_type;
    }

    public String getMemberid() {
        return memberid;
    }

    public void setMemberid(String memberid) {
        this.memberid = memberid;
    }

    public BigDecimal getShippingFee() {
        return shippingFee;
    }

    public void setShippingFee(BigDecimal shippingFee) {
        this.shippingFee = shippingFee;
    }

    public long getNetOrderid() {
        if(netOrderid == null)
        {
            netOrderid = -1L;
        }
        return netOrderid;
    }

    public void setNetOrderid(long netOrderid) {
        this.netOrderid = netOrderid;
    }

    public long getRefNetOrderId() {
        return refNetOrderId;
    }

    public void setRefNetOrderId(long refNetOrderId) {
        this.refNetOrderId = refNetOrderId;
    }

    public List<CookingMethod> getCookList() {
        return cookList;
    }

    public void setCookList(List<CookingMethod> cookList) {
        this.cookList = cookList;
    }

    public String getReturnUserName() {
        return returnUserName;
    }

    public void setReturnUserName(String returnUserName) {
        this.returnUserName = returnUserName;
    }

    public String getAuthUserName() {
        return authUserName;
    }

    public void setAuthUserName(String authUserName) {
        this.authUserName = authUserName;
    }

    public WaimaiType getWaimaiType() {
        return waimaiType;
    }

    public void setWaimaiType(WaimaiType waimaiType) {
        this.waimaiType = waimaiType;
    }

    public String getMenuName() {
        return menuName;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }

    public Account getAccountMember() {
        return accountMember;
    }

    public void setAccountMember(Account accountMember) {
        this.accountMember = accountMember;
    }

    public int getLaberDishCount() {
        return laberDishCount;
    }

    public void setLaberDishCount(int laberDishCount) {
        this.laberDishCount = laberDishCount;
    }

    // 0 没有开票； 1 已经开票
    public boolean getInvoicedState() {
        if(invoiced == 1)
        {
            return false;
        }
        return true;
    }

    public void setInvoiced(Integer invoiced) {
        this.invoiced = invoiced;
    }

    public String getMemberBizId() {
        return memberBizId;
    }

    public void setMemberBizId(String memberBizId) {
        this.memberBizId = memberBizId;
    }

    public String getMemberGrade() {
        return memberGrade;
    }

    public void setMemberGrade(String memberGrade) {
        this.memberGrade = memberGrade;
    }

    public BigDecimal getTakeoutFee() {
        return takeoutFee;
    }

    public void setTakeoutFee(BigDecimal takeoutFee) {
        this.takeoutFee = takeoutFee;
    }

    public CardRecord getCardRecord() {
        return cardRecord;
    }

    public void setCardRecord(CardRecord cardRecord) {
        this.cardRecord = cardRecord;
    }

    /**
     * 网上订单的接收状态  0为未接收  1为已接收并已下单  2为已接收但下单出错  3正面处理当前订单(正在下单处理的过程中)
     */
    private int netOrderState = 0;
    public int getNetOrderState() {
        return netOrderState;
    }

    public void setNetOrderState(int netOrderState) {
        this.netOrderState = netOrderState;
    }

    /**
     *确认开启吉野家下单服务后当前下单操作是否向吉野家后台下单
     */
    private  boolean isJyjOrder = false;

    public boolean isJyjOrder() {
        return isJyjOrder;
    }

    public void setJyjOrder(boolean jyjOrder) {
        isJyjOrder = jyjOrder;
    }

    /**
     * 吉野家订单下单失败的打印订单错误信息
     */
    private String jyjPrintErrMessage = "";

    public String getJyjPrintErrMessage() {
        return jyjPrintErrMessage;
    }

    public void setJyjPrintErrMessage(String jyjPrintErrMessage) {
        this.jyjPrintErrMessage = jyjPrintErrMessage;
    }
}
