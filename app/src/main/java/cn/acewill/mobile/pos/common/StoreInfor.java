package cn.acewill.mobile.pos.common;

import java.util.ArrayList;
import java.util.List;

import cn.acewill.mobile.pos.model.PaymentFlow;
import cn.acewill.mobile.pos.model.Receipt;
import cn.acewill.mobile.pos.model.ScanType;
import cn.acewill.mobile.pos.model.TerminalInfo;
import cn.acewill.mobile.pos.model.order.MarketingActivity;
import cn.acewill.mobile.pos.model.payment.Payment;
import cn.acewill.mobile.pos.printer.PrintConfiguration;
import cn.acewill.mobile.pos.printer.Printer;


/**
 * 门店运营基本信息(支付正反扫、先下单后下单、小票等)
 * Created by aqw on 2016/8/24.
 */
public class StoreInfor {
    public static PaymentFlow paymentFlow;
    public static ScanType scanType;
    public static List<Receipt> receiptList;
    public static List<Printer> printerList;
    public static String storeMode;
    public static String storeName;
    public static String address;
    public static String phoneNumber;
    public static int wipeZero = 0;//抹零处理：0：自动抹零；1：四舍五入；其他：不抹零  默认为自动抹零
    public static boolean cardNumberMode = false; //true则为送餐模式， 需要在结账时弹框，输入一个餐牌号码   false：顾客自取
    public static boolean repastPopulation = false; // 是否输入就餐人数  true 有输入就餐人数的选项  false没有
    public static PrintConfiguration printConfiguration;
    public static boolean createDishFromPOS = false;//是否要添加菜品选项

    public static List<Payment> getPaymentList() {
        return paymentList;
    }

    public static void setPaymentList(List<Payment> paymentList) {
        StoreInfor.paymentList = paymentList;
    }

    private static List<Payment> paymentList;
    public static List<MarketingActivity> marketingActivities = new ArrayList<>();//营销活动列表
    public static boolean customerInfoForWaimai = false;//选取外卖时是否要填写顾客信息  false不需要
    public static boolean printInvoiceBarcode = false;//是否在结账单上打发票二维码
    public static TerminalInfo terminalInfo;//门店设置信息


    public static Payment getPaymentById(int payTypeID){
        Payment paymentTemp = null;
        if(paymentList!=null&&paymentList.size()>0){
            for (Payment payment : paymentList) {
                if((int)payment.getId() == (int)payTypeID){
                    paymentTemp = payment;
                    break;
                }
            }
        }
        return paymentTemp;
    }

    public static boolean isRepastPopulation() {
        return repastPopulation;
    }

    public static void setRepastPopulation(boolean repastPopulation) {
        StoreInfor.repastPopulation = repastPopulation;
    }
}
