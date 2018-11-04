package cn.acewill.mobile.pos.utils;

import android.graphics.Bitmap;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;

import com.sunmi.impl.V1Printer;

import org.greenrobot.eventbus.EventBus;

import java.math.BigDecimal;
import java.util.List;

import cn.acewill.mobile.pos.common.PrinterController;
import cn.acewill.mobile.pos.common.StoreInfor;
import cn.acewill.mobile.pos.config.Store;
import cn.acewill.mobile.pos.model.MarketObject;
import cn.acewill.mobile.pos.model.MarketType;
import cn.acewill.mobile.pos.model.PaymentList;
import cn.acewill.mobile.pos.model.WaimaiOrderExtraData;
import cn.acewill.mobile.pos.model.WorkShiftNewReport;
import cn.acewill.mobile.pos.model.WorkShiftReport;
import cn.acewill.mobile.pos.model.dish.Dish;
import cn.acewill.mobile.pos.model.dish.Option;
import cn.acewill.mobile.pos.model.event.PosEvent;
import cn.acewill.mobile.pos.model.order.Order;
import cn.acewill.mobile.pos.model.order.OrderItem;
import cn.acewill.mobile.pos.model.payment.Payment;
import cn.acewill.mobile.pos.model.wsh.Account;
import cn.acewill.mobile.pos.printer.Alignment;
import cn.acewill.mobile.pos.printer.PrintModelInfo;
import cn.acewill.mobile.pos.printer.TextRow;
import cn.acewill.mobile.pos.service.PosInfo;

import static cn.acewill.mobile.pos.common.PrinterDataController.receiptPrinter;
import static cn.acewill.mobile.pos.utils.TimeUtil.getTimeStr;

/**
 * Created by DHH on 2017/12/14.
 */

public class SmVlPrintUtil {
    private static int spaceWeight = 32;

    /**
     * 打印换行
     */
    private static void printEnter(V1Printer v1Printer) {
        if (v1Printer != null) {
            v1Printer.printText("\n");
        }
    }

    /**
     * 设置文字对齐方式
     */
    private static void printAlign(V1Printer v1Printer, Alignment align) {
        if (v1Printer != null) {
            if (align == Alignment.LEFT) {
                v1Printer.setAlignment(0);
            } else if (align == Alignment.CENTER) {
                v1Printer.setAlignment(1);
            } else if (align == Alignment.RIGHT) {
                v1Printer.setAlignment(2);
            }
        }
    }

    /**
     * 打印分隔符
     *
     * @param v1Printer
     * @param separator
     */
    private static void printSeparator(V1Printer v1Printer, String separator) {
        if (v1Printer != null) {
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < spaceWeight; i++) {
                sb.append(separator);
            }
            printText(v1Printer, sb.toString());
        }
    }

    /**
     * 打印文字
     *
     * @param v1Printer
     * @param alignMentType
     * @param isEnter
     * @param text
     */
    private static void printText(V1Printer v1Printer, int alignMentType, boolean isEnter, String text) {
        if (v1Printer != null) {
            if (alignMentType == 1) {
                printAlign(v1Printer, Alignment.LEFT);
            } else if (alignMentType == 2) {
                printAlign(v1Printer, Alignment.CENTER);
            } else if (alignMentType == 3) {
                printAlign(v1Printer, Alignment.RIGHT);
            } else {
                printAlign(v1Printer, Alignment.LEFT);
            }
            printText(v1Printer, text);
            if (isEnter) {
                printEnter(v1Printer);
            }
        }
    }

    /**
     * 打印套餐项中的定制项
     */
    private static BigDecimal printDishPackageOption(V1Printer v1Printer, boolean isKitReceipt, Dish.Package subitem, PrintModelInfo dishCutomerize) {
        BigDecimal dishOption = new BigDecimal("0.00");
        if (subitem != null && subitem.optionList != null && subitem.optionList.size() > 0) {
            dishOption = dishOption.add(printDishOption(v1Printer, isKitReceipt, subitem.optionList, dishCutomerize).multiply(new BigDecimal(subitem.quantity))).add(new BigDecimal(subitem.extraCost).multiply(new BigDecimal(subitem.quantity)));
        }
        return dishOption;
    }

    private static String getBarcodeUrl(String orderId) {
        PosInfo posInfo = PosInfo.getInstance();
        String mob = "mobilereport/invoice.html?";
        String storeInfo = "appId=" + posInfo.getAppId() + "&brandId=" + posInfo.getBrandId() + "&storeId=" + posInfo.getStoreId() + "&orderId=" + orderId;
        String url = posInfo.getServerUrl() + mob + storeInfo;
        return url;
    }

    private static String getOrderQrCodeUrl(String orderId) {
        PosInfo posInfo = PosInfo.getInstance();
        String mob = "api/orders/checkServeStatus?";
        String storeInfo = "appId=" + posInfo.getAppId() + "&brandId=" + posInfo.getBrandId() + "&storeId=" + posInfo.getStoreId() + "&orderId=" + orderId;
        String url = posInfo.getServerUrl() + mob + storeInfo;
        return url;
    }

    /**
     * 打印普通菜品中的定制项
     *
     * @param v1Printer
     * @param optionList
     */
    private static BigDecimal printDishOption(V1Printer v1Printer, boolean isKitReceipt, List<Option> optionList, PrintModelInfo dishCutomerize) {
        BigDecimal dishOption = new BigDecimal("0.00");
        if (v1Printer != null && optionList != null && optionList.size() > 0) {
            if (optionList != null && optionList.size() > 0) {
                StringBuffer sb = new StringBuffer();
                for (Option option : optionList) {
                    if (option.getPrice().compareTo(new BigDecimal("0")) == 0) {
                        sb.append(option.name + "、");
                    } else {
                        dishOption = dishOption.add(option.getPrice());
                        sb.append(option.name + "(" + option.getPrice() + "元)、");
                    }
                }
                printText(v1Printer, 1, true, "    +" + sb.toString());
            }
        }
        return dishOption;
    }

    private static BigDecimal getShippingFee(BigDecimal shippingFee) {
        if (shippingFee != null) {
            return shippingFee;
        }
        return new BigDecimal("0");
    }

    /**
     * 打印文字
     *
     * @param v1Printer
     * @param string
     */
    private static void printText(V1Printer v1Printer, String string) {
        if (v1Printer != null) {
//            v1Printer.setFontSize(35);
            v1Printer.setFontName("gh");
            v1Printer.printText(string);
        }
    }

    public static void testPrint(V1Printer v1Printer)
    {
        if (v1Printer == null) {
            return;
        }
        v1Printer.beginTransaction();

        printEnter(v1Printer);
        printEnter(v1Printer);
        printText(v1Printer, 2, true, "测试打印");
        printText(v1Printer, 2, true, "打印时间:" + TimeUtil.getHourStr());
        printText(v1Printer, 2, true, "商米移动POS打印成功");
        printEnter(v1Printer);
        printEnter(v1Printer);
        v1Printer.commitTransaction();
    }


    public static void printCashReceipt(V1Printer v1Printer, Order order, ArrayMap<String, PrintModelInfo> printMode, int orderPrinterType, Store store) {
        if (v1Printer == null) {
            return;
        }
        v1Printer.beginTransaction();
        PosInfo posInfo = PosInfo.getInstance();
        String type = order.getOrderType();
        String orderType = "堂食";
        if (type.equals("EAT_IN")) {
            orderType = "堂食";
        } else if (type.equals("SALE_OUT")) {
            orderType = "外卖";
        } else if (type.equals("TAKE_OUT")) {
            orderType = "外带";
        }
        String jyjErrMessage = order.getJyjPrintErrMessage();
        String brandNameForStoreName = PrinterController.getBrandNameAndStoreName(printMode);
        PrintModelInfo modeTitle = printMode.get("brandName");//品牌名
        if (!TextUtils.isEmpty(jyjErrMessage)) {
            printEnter(v1Printer);
            printText(v1Printer, 2, true, jyjErrMessage);
        }
        if (!TextUtils.isEmpty(brandNameForStoreName)) {
            printText(v1Printer, 2, true, brandNameForStoreName);
        }
        printEnter(v1Printer);
        boolean isRefund = false;//是否是退单  退菜模式
        PrintModelInfo modeTicketType = printMode.get("ticketType");//小票类型
        if (modeTicketType.isShouldPrint()) {
            String ticketType = "";
            if (Constant.EventState.PRINT_WAIMAI == order.getPrinterType()) {
                ticketType = orderType;
            } else {
                ticketType = modeTicketType.getValue();
            }
            if ((Constant.EventState.PRINTER_RETREAT_ORDER == order.getPrinterType()) || (Constant.EventState.PRINTER_RETREAT_DISH_GUEST == order.getPrinterType())) {
                ticketType = "退菜单";
                isRefund = true;
            }
            printText(v1Printer, 2, true, ticketType);
        }

        PrintModelInfo modeSeparator = printMode.get("separator");
        printSeparator(v1Printer, modeSeparator.getValue());//分隔符
        printEnter(v1Printer);
        PrintModelInfo modeTableName = printMode.get("tableName");//桌台号
        String sourse = "";
        if (Constant.EventState.PRINT_WAIMAI == order.getPrinterType()) {
            sourse = order.getSource();
        }
        String eatType = "";
        String eatNumber = "";
        if (ToolsUtils.logicIsTable()) {
            eatType = "桌台号:";
            eatNumber = order.getTableNames();
        } else {
            if (StoreInfor.cardNumberMode) {
                eatType = "餐牌号:";
                eatNumber = order.getTableNames();
            } else {
                eatType = "取餐号:";
                eatNumber = order.getCallNumber();
            }
            if (type.equals("SALE_OUT")) {
                eatType = order.getSource() + "流水号:";
                eatNumber = order.getThirdPlatfromOrderIdDaySeq();
            }
        }
        if (modeTableName.isShouldPrint()) {
            String eatMode = "";
            eatMode = PrintUtils.getStr(eatType + eatNumber, "", sourse.equals("") ? orderType : sourse, spaceWeight);
            printText(v1Printer, 1, true, eatMode);
        }
        PrintModelInfo modeOrderId = printMode.get("orderId");//订单号
        PrintModelInfo modeCustomerCount = printMode.get("customerCount");//人数

        //订单号
        if (modeOrderId.isShouldPrint()) {
            String orderNumber = "";
            String number = "订单号:";
            orderNumber = PrintUtils.getStr(number + order.getId(), "", "", 0);
            printText(v1Printer, 1, true, orderNumber);
        }

        if (!type.equals("SALE_OUT")) {
            //人数
            if (modeCustomerCount.isShouldPrint() && order.getCustomerAmount() > 0) {
                printText(v1Printer, 1, true, "顾客人数:" + order.getCustomerAmount());
            }

            //操作终端
            String terminalName = posInfo.getTerminalName();
            printText(v1Printer, 1, true, "操作终端:" + terminalName);
            //操作人
            String userName = posInfo.getRealname();//服务员
            printText(v1Printer, 1, true, "操作人:" + userName);
        }

        //下单时间
        PrintModelInfo modeOrderTime = printMode.get("orderTime");
        if (modeOrderTime.isShouldPrint()) {
            printText(v1Printer, 1, true, "下单时间:" + getTimeStr(order.getCreatedAt()));
            //                    printEnter(printerInterface, modeOrderTime.isPrintEnter());
        }
        if (!TextUtils.isEmpty(order.getHopeDeliverTime())) {
            printText(v1Printer, 1, true, "期望送达时间:" + order.getHopeDeliverTime());
        }

        printSeparator(v1Printer, modeSeparator.getValue());
        String dishTitle = PrintUtils.getStr("菜品", "数量*单价", "金额", spaceWeight-2);
        printText(v1Printer, 1, true, dishTitle);
        printSeparator(v1Printer, modeSeparator.getValue());

        List<OrderItem> itemList = order.getItemList();
        BigDecimal dishCount = new BigDecimal("0.00");
        for (int i = 0; i < itemList.size(); i++) {
            OrderItem orderItem = itemList.get(i);
            int logicQuantity = 0;
            if (order.getPrinterType() == Constant.EventState.PRINTER_RETREAT_DISH_GUEST) {
                logicQuantity = orderItem.getRejectedQuantity();
            } else if (order.getPrinterType() == Constant.EventState.PRINTER_RETREAT_ORDER) {
                logicQuantity = orderItem.getQuantity();
                orderItem.setRejectedQuantity(orderItem.getQuantity());
            } else {
                logicQuantity = orderItem.getQuantity();
            }
            if (logicQuantity <= 0) {
                continue;
            }

            // 套餐菜品
            if (ToolsUtils.getIsPackage(orderItem)) {
                int quantity = 0;
                if (isRefund) {
                    quantity = orderItem.getRejectedQuantity();
                } else {
                    quantity = orderItem.getQuantity();
                }
                BigDecimal money = orderItem.getCost().setScale(2, BigDecimal.ROUND_DOWN).multiply(new BigDecimal(quantity + ""));
                boolean isGift = false;
                dishCount = dishCount.add(money);
                String zc = "";
                if (money.compareTo(BigDecimal.ZERO) != 1) {
                    zc = "(赠)";
                    isGift = true;
                }
                String oi = PrintUtils.getStr("(" + orderItem.getDishName() + zc + ")", PrinterController.getDishCount(order, orderItem), money.toString(), spaceWeight);
                printText(v1Printer, 1, true, oi);

                String dishComment = orderItem.getComment();
                if (!TextUtils.isEmpty(dishComment)) {
                    printText(v1Printer, 1, true, "备注:"+dishComment);
                }

                String disCountStr = getDisCountStr(orderItem.getMarketList(),orderItem.getQuantity());
                if (!TextUtils.isEmpty(disCountStr)) {
                    printText(v1Printer, 1, true, disCountStr);
                }

                PrintModelInfo packageDetail = printMode.get("packageDetail");//套餐明细
                if (packageDetail != null && packageDetail.isShouldPrint())//查询是否要打印套餐子项
                {
                    // 套餐子项
                    List<Dish.Package> subItemList = orderItem.getSubItemList();
                    for (int a = 0; a < subItemList.size(); a++) {
                        String itemPrice = "";
                        if (subItemList.get(a).getItemPrice() != null && subItemList.get(a).getItemPrice().compareTo(BigDecimal.ZERO) > 0) {
                            itemPrice = subItemList.get(a).getItemPrice().toString();
                            if (TextUtils.isEmpty(itemPrice)) {
                                itemPrice = "";
                            }
                        }
                        itemPrice = "";
                        String oiSub = PrintUtils.getStr("[套]" + subItemList.get(a).getDishName(), PrinterController.getPackateCount(order, orderItem, subItemList.get(a)), itemPrice, spaceWeight);
                        printText(v1Printer, 1, true, oiSub);

                        //                                if (!isDishHaveMarket(orderItem.getMarketList())) {
                        if (!isGift && !isRefund) {
                            PrintModelInfo dishCutomerize = printMode.get("dishCutomerize");//菜品定制项
                            dishCount = dishCount.add(printDishPackageOption(v1Printer, false, subItemList.get(a), dishCutomerize));
                        }
                        //                                }
                    }
                }
            }
            //普通菜品
            else {
                BigDecimal price = orderItem.getCost();
                price = price == null ? new BigDecimal(0) : price;
                int quantity = 0;
                if (isRefund) {
                    quantity = orderItem.getRejectedQuantity();
                } else {
                    quantity = orderItem.getQuantity();
                }
                //                        MyApplication.getInstance().ShowToast("price======="+price+"=========="+"quantity======="+quantity);
                BigDecimal money = price.multiply(new BigDecimal(quantity)).setScale(2, BigDecimal.ROUND_DOWN);
                dishCount = dishCount.add(money);
                boolean isGift = false;
                String zc = "";
                if (money.compareTo(BigDecimal.ZERO) != 1) {
                    zc = "(赠)";
                    isGift = true;
                }
                String oi = PrintUtils.getStr(orderItem.getDishName() + zc, PrinterController.getDishCount(order, orderItem), money.toString(), spaceWeight);
                printText(v1Printer, 1, true, oi);

                String dishComment = orderItem.getComment();
                if (!TextUtils.isEmpty(dishComment)) {
                    printText(v1Printer, 1, true, "备注:"+dishComment);
                }

                String disCountStr = getDisCountStr(orderItem.getMarketList(),orderItem.getQuantity());
                if (!TextUtils.isEmpty(disCountStr)) {
                    printText(v1Printer, 1, true, disCountStr);
                }
                if (!isRefund) {
                    PrintModelInfo dishCutomerize = printMode.get("dishCutomerize");//菜品定制项
                    dishCount = dishCount.add(printDishOption(v1Printer, false, orderItem.optionList, dishCutomerize));
                }
            }
        }

        printSeparator(v1Printer, modeSeparator.getValue());//分隔符
        if (order.getWaimaiOrderExtraDatas() != null && order.getWaimaiOrderExtraDatas().size() > 0) {
            for (WaimaiOrderExtraData data : order.getWaimaiOrderExtraDatas()) {
                String totalMoney = PrintUtils.getStr(data.getRemark(), "", data.getReduce_fee(), spaceWeight);

                printText(v1Printer, 1, true, totalMoney);
            }
        }

        int textNum1 = 2;
        int textNum2 = 1;
        if (orderPrinterType == Constant.EventState.PRINTER_ORDER) {
            textNum1 = 1;
            textNum2 = 1;
        } else if (orderPrinterType == Constant.EventState.PRINT_CHECKOUT) {
            textNum1 = 2;
            textNum2 = 1;
        }

        if (type.equals("SALE_OUT")) {
            TextRow waiMaiRow;
            if (!TextUtils.isEmpty(order.getOuterOrderid())) {
                String outerOrderId = "";
                if(!TextUtils.isEmpty(order.getThirdPlatformOrderId()))
                {
                    outerOrderId = order.getThirdPlatformOrderId();
                }
                else{
                    outerOrderId = order.getOuterOrderid();
                }
                printText(v1Printer, 1, true, order.getSource() + "订单号:" + outerOrderId);
            }
            TextRow rowNum = null;
            if (!TextUtils.isEmpty(order.getCustomerName())) {
                printText(v1Printer, 1, true, "顾客姓名:" + order.getCustomerName());
            }

            if (!TextUtils.isEmpty(order.getCustomerPhoneNumber())) {
                printText(v1Printer, 1, true, "顾客电话:" + order.getCustomerPhoneNumber());
            }

            if (!TextUtils.isEmpty(order.getCustomerAddress())) {
                printText(v1Printer, 1, true, "顾客地址:" + order.getCustomerAddress());
            }
            if (!TextUtils.isEmpty(getShippingFee(order.getShippingFee()).toString())) {
                printText(v1Printer, 1, true, "外卖配送费: ￥" + getShippingFee(order.getShippingFee()));
            }
            if (order.getTakeoutFee() != null && order.getTakeoutFee().compareTo(BigDecimal.ZERO) == 1) {
                printText(v1Printer, 1, true, "外带费: ￥" + order.getTakeoutFee());
            }
        }

        if (orderPrinterType == Constant.EventState.PRINTER_ORDER) {
            BigDecimal countMoney = new BigDecimal("0.00");
            if (isRefund) {
                countMoney = dishCount;
            } else {
                countMoney = new BigDecimal(order.getCost());
            }
            PrintModelInfo modeOrderTotal = printMode.get("orderTotal");//消费总计  最后要付的钱
            String totalMoney = PrintUtils.getStr("合    计", "", countMoney.toString(), spaceWeight);
            printText(v1Printer, 1, true, totalMoney);
        } else if (orderPrinterType == Constant.EventState.PRINT_CHECKOUT) {
            PrintModelInfo modeOrderTotal = printMode.get("orderTotal");//消费总计  最后要付的钱
            BigDecimal countMoney = new BigDecimal("0.00");
            //如果是退单或者是退菜,显示的合计金额应该是用计算出来的countmoney
            if (isRefund) {
                countMoney = dishCount;
            } else {
                countMoney = new BigDecimal(order.getCost());
            }

            if (order.getServiceMoney() != null && order.getServiceMoney().compareTo(BigDecimal.ZERO) > 0) {
                String serviceMoney = PrintUtils.getStr("服 务 费:", "", order.getServiceMoney().toString(), spaceWeight);
                printText(v1Printer, 1, true, serviceMoney);
            }


            if (type.equals("TAKE_OUT") || type.equals("SALE_OUT") && !order.getTake_money().toString().equals("0")) {
                String takeMoney = PrintUtils.getStr("打 包 费:", "", order.getTake_money().toString(), spaceWeight);
                printText(v1Printer, 1, true, takeMoney);
            }

            if (!isRefund) {
                BigDecimal totalMoney = new BigDecimal(order.getTotal()).setScale(2, BigDecimal.ROUND_DOWN);
                String totalMoneySth = PrintUtils.getStr("原价合计:", "", totalMoney.toString(), spaceWeight);
                printText(v1Printer, 1, true, totalMoneySth);

                BigDecimal avtiveMoney = totalMoney.subtract(new BigDecimal(order.getCost()));

                String activeMoney = PrintUtils.getStr("优惠总计:", "", avtiveMoney.setScale(2, BigDecimal.ROUND_DOWN).toString(), spaceWeight);
                printText(v1Printer, 1, true, activeMoney);
            }

            String costMoney = PrintUtils.getStr("实收合计:", "", countMoney.toString(), spaceWeight);
            printText(v1Printer, 1, true, costMoney);

            BigDecimal payMoney = new BigDecimal("0.00").add(countMoney);
            if (order.getGive_money().setScale(2, BigDecimal.ROUND_DOWN).compareTo(BigDecimal.ZERO) < 0) {
                payMoney = payMoney.add(order.getGive_money().setScale(2, BigDecimal.ROUND_DOWN).abs());
            }

            String payMoneySth = PrintUtils.getStr("客    付:", "", payMoney.setScale(2, BigDecimal.ROUND_DOWN).toString(), spaceWeight);
            printText(v1Printer, 1, true, payMoneySth);

            if (order.getGive_money().setScale(2, BigDecimal.ROUND_DOWN).compareTo(BigDecimal.ZERO) < 0) {
                String giveMoney = PrintUtils.getStr("找    零:", "", order.getGive_money().setScale(2, BigDecimal.ROUND_DOWN).toString(), spaceWeight);
                printText(v1Printer, 1, true, giveMoney);
            }
        }
        printSeparator(v1Printer, modeSeparator.getValue());//分隔符

        if (Constant.EventState.PRINTER_RETREAT_ORDER != order.getPrinterType() && Constant.EventState.PRINTER_RETREAT_DISH_GUEST != order.getPrinterType()) {
            if (order.getPaymentList() != null && order.getPaymentList().size() > 0) {
                printText(v1Printer, 1, true, "支付方式");
                printSeparator(v1Printer, "-");//分隔符

                for (PaymentList paymentList : order.getPaymentList()) {
                    int paymentTypeId = paymentList.getPaymentTypeId();
                    Payment payment = StoreInfor.getPaymentById(paymentTypeId);
                    if (payment != null) {
                        String payMentName = "";
                        if (paymentTypeId == 3) {
                            //                                    payMentName = "会员支付(储值余额)";
                            payMentName = "会员储值";
                        }
                        if (paymentTypeId == 4) {
                            //                                    payMentName = "会员支付(优惠券)";
                            payMentName = "会员优惠券";
                        }
                        if (paymentTypeId == 5) {
                            //                                    payMentName = "会员支付(积分余额)";
                            payMentName = "会员积分";
                        }
                        if (TextUtils.isEmpty(payMentName)) {
                            payMentName = payment.getName();
                        }
                        String payType = PrintUtils.getStr(payMentName, "", paymentList.getValue().setScale(2, BigDecimal.ROUND_DOWN).toString(), spaceWeight);
                        printText(v1Printer, 1, true, payType);

                        if (!TextUtils.isEmpty(paymentList.getPaymentNo()) && paymentTypeId != 0) {
                            String paymentNo = PrintUtils.getStr("电子流水号:", "", paymentList.getPaymentNo(), spaceWeight);
                            printText(v1Printer, 1, true, paymentNo);
                        }
                    }
                }
                if (order.getAccountMember() != null) {
                    Account account = order.getAccountMember();
                    printText(v1Printer, 1, true, "会员消费详情");
                    printText(v1Printer, 1, true, "会员卡号:" + account.getUno() + "(" + ToolsUtils.replacePhone(account.getPhone()) + ")");
                    printText(v1Printer, 1, true, "会员姓名:" + ToolsUtils.getStarString2(account.getName(), 1, 0));
                    printText(v1Printer, 1, true, "卡 等 级:" + account.getGradeName());
                    printText(v1Printer, 1, true, "消费金额:" + account.getMemberConsumeCost());
                    printText(v1Printer, 1, true, "(如有获赠积分卡券等,此与会员消费规则有关,详情咨询门店)");
                }
            }
        }

        boolean isPrintQrcode = false;
        //设置的本地开关
        if (store.isPrintQRCode()) {
            isPrintQrcode = true;
        }
        //补打的是否要打印二维码的订单开关
        if (order.isPrintQrcode()) {
            isPrintQrcode = true;
        } else {
            isPrintQrcode = false;
        }
        if (orderPrinterType == Constant.EventState.PRINT_CHECKOUT && isPrintQrcode) {
            printSeparator(v1Printer, " ");//分隔符

            printText(v1Printer, 2, true, "扫描下面二维码获取电子发票");
            Bitmap bitmap = null;
            Bitmap qrcode = CreateImage.creatQRImage(getBarcodeUrl(order.getId() + ""), bitmap, 250, 250);
            boolean isEnter = true;//是否打印回车
            if (receiptPrinter != null) {
                isEnter = true;
            } else {
                isEnter = false;
            }

            v1Printer.printBitmap(qrcode);

        }

        PrintModelInfo modeOrderQrCode = printMode.get("orderQrCode");//订单二维码
        if (orderPrinterType == Constant.EventState.PRINT_CHECKOUT && modeOrderQrCode.isShouldPrint()) {
            printText(v1Printer, 2, true, "扫描下面二维码获取订单详情");
            Bitmap bitmap = null;
            Bitmap qrcode = CreateImage.creatQRImage(getOrderQrCodeUrl(order.getId() + ""), bitmap, 250, 250);
            boolean isEnter = true;//是否打印回车
            if (receiptPrinter != null) {
                isEnter = true;
            } else {
                isEnter = false;
            }
            v1Printer.printBitmap(qrcode);
        }
        PrintModelInfo modeQrCode = printMode.get("qrCode");//公众号二维码
        if (orderPrinterType == Constant.EventState.PRINT_CHECKOUT && modeQrCode.isShouldPrint()) {
            //                    TextRow rowNum = createRow(true, 1, "扫描下面二维码关注公众号");
            //                    rowNum.setAlign(Alignment.CENTER);
            //                    printerInterface.printRow(rowNum);
            //                    Bitmap bitmap = null;
            //                    Bitmap qrcode = CreateImage.creatQRImage(getOrderQrCodeUrl(order.getId() + ""), bitmap, 250, 250);
            //                    boolean isEnter = true;//是否打印回车
            //                    if (receiptPrinter != null) {
            //                        isEnter = true;
            //                    } else {
            //                        isEnter = false;
            //                    }
            //                    printerInterface.printBmp(new BitmapRow(qrcode), isEnter);
        }

        printSeparator(v1Printer, modeSeparator.getValue());//分隔符
        PrintModelInfo modeFreeText = printMode.get("freeText");//自定义文字
        if (modeFreeText.isShouldPrint()) {
            printText(v1Printer, 1, true, modeFreeText.getValue());
        }

        //全单备注
        if (!TextUtils.isEmpty(order.getComment())) {
            printText(v1Printer, 1, true, "全单备注:" + order.getComment());
        }
        if (!TextUtils.isEmpty(order.getWaimaiHasInvoiced()) && order.getWaimaiHasInvoiced().equals("1")) {
            printText(v1Printer, 1, true, "需要发票");
            printText(v1Printer, 1, true, "发票抬头:" + order.getWaimaiInvoiceTitle());
            printText(v1Printer, 1, true, "发票税号:" + order.getWaimaiTaxpayerId());
        }
        printSeparator(v1Printer, " ");//分隔符

        printText(v1Printer, 1, true, "\n- - - - - 由此线撕开 - - - - - -\n\n\n");

        if (store.getReceiveNetOrder()) {
            EventBus.getDefault().post(new PosEvent(Constant.EventState.NETORDER_ON));
        }
        v1Printer.commitTransaction();
    }

    public static void printWorkShiftNew(V1Printer v1Printer, WorkShiftNewReport workShiftReport, String printStr) {
        if(v1Printer == null)
        {
            return;
        }
        v1Printer.beginTransaction();
        PosInfo posInfo = PosInfo.getInstance();

        printText(v1Printer,2,true,posInfo.getBrandName());

        printText(v1Printer,2,true,printStr + "打印单");

        printText(v1Printer,2,true,printStr + "人 : " + posInfo.getRealname());

        if (!TextUtils.isEmpty(workShiftReport.getWorkShiftName())) {
            printText(v1Printer,1,true,printStr + "班次名称 : " + workShiftReport.getWorkShiftName());
        }

        if (!TextUtils.isEmpty(workShiftReport.getStartTime())) {
            printText(v1Printer,1,true,printStr + "开始时间 : " + workShiftReport.getStartTime());
        }

        if (!TextUtils.isEmpty(workShiftReport.getStartTime())) {
            printText(v1Printer,1,true,printStr + "结束时间 : " + workShiftReport.getEndTime());
        }

        if (workShiftReport.getStartWorkShiftCash() > 0) {
            printText(v1Printer,1,true,"开班钱箱余额 : " + workShiftReport.getStartWorkShiftCash());
        }

        if (workShiftReport.getEndWorkShiftCash() > 0) {
            printText(v1Printer,1,true,"交班钱箱余额 : " + workShiftReport.getEndWorkShiftCash());
        }

        printText(v1Printer,1,true,"小票打印时间 : " + getTimeStr(System.currentTimeMillis()));

        createWorkShiftNewItem(v1Printer,workShiftReport);

        String submitCash = PrintUtils.getStr("应交现金:", "", String.valueOf(workShiftReport.getSubmitCash() + " / 元"), spaceWeight);
        printText(v1Printer,1,true,submitCash);

        String differenceCash = PrintUtils.getStr("差额:", "", String.valueOf(workShiftReport.getDifferenceCash() + " / 元"), spaceWeight);
        printText(v1Printer,1,true,differenceCash);

        printEnter(v1Printer);
        printEnter(v1Printer);
        printEnter(v1Printer);
        printEnter(v1Printer);
        v1Printer.commitTransaction();
    }

    private static String returnStr(String str)
    {
        return TextUtils.isEmpty(str) ? "":str;
    }

    private static void createWorkShiftNewItem(V1Printer v1Printer,WorkShiftNewReport workShiftReport) {
        try {
            for (WorkShiftNewReport.WorkShiftCategoryDataList itemCategorySalesDataList : workShiftReport.getWorkShiftCategoryDataList()) {
                printSeparator(v1Printer,"-");
                printText(v1Printer,2,true,returnStr(itemCategorySalesDataList.getName()));
                printSeparator(v1Printer,"-");

                String dishTitle = PrintUtils.getStr("名称", "数量", "金额", spaceWeight);
                printText(v1Printer,1,true,dishTitle);
                printSeparator(v1Printer,"-");

                for (WorkShiftNewReport.WorkShiftCategoryDataList.WorkShiftItemDatas itemSalesDataList : itemCategorySalesDataList.getWorkShiftItemDatas()) {
                    String itemInfo = PrintUtils.getStr(returnStr(itemSalesDataList.getName()), returnStr(String.valueOf(itemSalesDataList.getItemCounts())), returnStr(String.valueOf(itemSalesDataList.getTotal())), 30);
                    printText(v1Printer,1,true,itemInfo);
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void printWorkShift(V1Printer v1Printer, WorkShiftReport workShiftReport, String printStr) {
        if(v1Printer == null)
        {
            return;
        }
        v1Printer.beginTransaction();
        PosInfo posInfo = PosInfo.getInstance();

        printText(v1Printer,2,true,posInfo.getBrandName());

        printText(v1Printer,2,true,printStr + "打印单");

        printText(v1Printer,2,true,printStr + "人 : " + posInfo.getRealname());

        if (!TextUtils.isEmpty(workShiftReport.getStartTime())) {
            printText(v1Printer,1,true,printStr + "开始时间 : " + workShiftReport.getStartTime());
        }

        if (!TextUtils.isEmpty(workShiftReport.getStartTime())) {
            printText(v1Printer,1,true,printStr + "结束时间 : " + workShiftReport.getEndTime());
        }

        printText(v1Printer,1,true,"小票打印时间 : " + getTimeStr(System.currentTimeMillis()));

        createWorkShiftItem(v1Printer,workShiftReport);

        printEnter(v1Printer);
        printEnter(v1Printer);
        printEnter(v1Printer);
        printEnter(v1Printer);
        v1Printer.commitTransaction();
    }

    private static void createWorkShiftItem(V1Printer v1Printer,WorkShiftReport workShiftReport) {
        try {
            for (WorkShiftReport.ItemCategorySalesDataList itemCategorySalesDataList : workShiftReport.getItemCategorySalesDataList()) {
                printSeparator(v1Printer,"-");
                printText(v1Printer,2,true,returnStr(itemCategorySalesDataList.getName()));
                printSeparator(v1Printer,"-");

                String dishTitle = PrintUtils.getStr("名称", "数量", "金额", spaceWeight);
                printText(v1Printer,1,true,dishTitle);
                printSeparator(v1Printer,"-");

                for (WorkShiftReport.ItemCategorySalesDataList.ItemSalesDataList itemSalesDataList : itemCategorySalesDataList.getItemSalesDataList()) {
                    String itemInfo = PrintUtils.getStr(returnStr(itemSalesDataList.getName()), returnStr(String.valueOf(itemSalesDataList.getItemCounts())), returnStr(String.valueOf(itemSalesDataList.getTotal())), 30);
                    printText(v1Printer,1,true,itemInfo);
                }
            }

            printSeparator(v1Printer,"-");
            printText(v1Printer,2,true,"客单价统计");
            printSeparator(v1Printer,"-");

            WorkShiftReport.PctData pctData = workShiftReport.getPctData();
            String orderCount = PrintUtils.getStr("订单总数", "", returnStr(String.valueOf(pctData.getOrderCounts()) + " /条"), spaceWeight);
            printText(v1Printer,1,true,orderCount);

            String userCount = PrintUtils.getStr("客人总数", "", returnStr(String.valueOf(pctData.getCustomerCounts()) + " /人"), spaceWeight);
            printText(v1Printer,1,true,userCount);

            String orderTotal = PrintUtils.getStr("订单均价", "", " ￥ " + returnStr(String.valueOf(pctData.getPricePerOrder()) + " / 元"), spaceWeight);
            printText(v1Printer,1,true,orderTotal);

            String guestTotal = PrintUtils.getStr("客单价", "", " ￥ " + returnStr(String.valueOf(pctData.getPricePerCustomer()) + " / 元"), spaceWeight);
            printText(v1Printer,1,true,guestTotal);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String getDisCountStr(List<MarketObject> marketList, int quantity) {
        StringBuffer sb = new StringBuffer();
        if (marketList != null && marketList.size() > 0) {
            int size = marketList.size();
            for (int i = 0; i < size; i++) {
                MarketObject market = marketList.get(i);
                if(market.getMarketType() == MarketType.MANUAL && market.getDiscountType() == 1)
                {
                    sb.append("    -" + market.getMarketName() + "-" + market.getReduceCash().multiply(new BigDecimal(quantity)).toString() + "元");
                }
                else{
                    sb.append("    -" + market.getMarketName() + "-" + market.getReduceCash().toString() + "元");
                }
                if (i != size - 1) {
                    sb.append("\n");
                }
            }
        }
        return sb.toString();
    }


    
}
