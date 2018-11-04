package cn.acewill.mobile.pos.printer;

/**
 * Created by Administrator on 2017-02-17.
 */
public enum PrintModelType
{
    LOGO("logo"),  //logo图片
    BRAND_NAME("brandName"), // 品牌名称
    STORE_NAME("storeName"), //门店名称
    STORE_ADDRESS("storeAddress"), // 门店地址
    STORE_PHONE("storePhone"), // 门店电话
    TICKET_TYPE("ticketType"), // 小票类型（客用单，预结单，结账单）
    SEPARATOR("separator"), // 分隔符
    CALL_NUMBER("callNumber"), // 取餐号
    TABLE_NAME("tableName"), // 桌台名称
    ORDER_ID("orderId"),  //订单号
    MEAL_TYPE("mealType"), // 堂食/外带
    PRINT_TIME("orderTime"), // 打印时间
    CHECK_TIME("checkoutTime"), // 结账时间
    OPERATOR("operator"), // 操作者名称
    CUSTOMER_COUNT("customerCount"), // 就餐人数
    DISH_NAME("dishName"),   // 菜品
    DISH_COUNT("dishCount"), // 数量
    DISH_PRICE("dishPrice"), // 价格
    DISH_DETAIL("dishDetail"), // 菜品明细
    DISH_CUSTOMERIZE("dishCutomerize"), // 定制内容
    COMMENTS("comments"),
    DISH_DISCOUNT("dishDiscount"), // 菜品优惠
    DISH_FREE("dishFree"), // 赠菜
    DISH_PACKAGE_DETAIL("packageDetail"), // 套餐明细
    SERVICE_CHARGE("serviceCharge"),  // 服务费
    TAKE_OUT_CHARGE("takeoutCharge"), // 打包费
    TOTAL("total"), // 总额
    ORDER_TOTAL("orderTotal"), // 订单总额
    DISCOUNT_TOTAL("dishcountTotal"), // 优惠总额
    COST("cost"), // 应收款总额
    PAY_DETAIL("payDetail"), // 支付明细
    FREE_TEXT("freeText"), // 自由输入内容
    BAR_CODE("barCode"), // 条形码
    QR_CODE("qrCode"),  // 二维码
    INVOICE("invoice"), // 电子发票

    KICHEN_STALLS("stallsName"), // 厨房档口名称
    KICHEN_DISH_TYPE("kichenDishType"),
    PRECHECK_TIMES("preCheckoutTimes"); // 预结次数

    private String value;
    private PrintModelType(String value)
    {
        this.value = value;
    }

    public String getString()
    {
        return this.value;
    }
}
