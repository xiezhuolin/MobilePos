package cn.acewill.mobile.pos.model;

import java.util.List;

import cn.acewill.mobile.pos.printer.PrintConfiguration;
import cn.acewill.mobile.pos.printer.Printer;


/**
 * 门店运营需要的基本信息
 * Created by Acewill on 2016/8/23.
 */
public class StoreBusinessInformation {
    PaymentFlow paymentFlow;
    ScanType scanType;
    List<Receipt> receiptList;
    List<Printer> printerList;
    String storeMode;
    Printer receiptPrinter;
    String storeName;
    String address;
    String phoneNumber;
    boolean cardNumberMode = false; //true则为送餐模式， 需要在结账时弹框，输入一个餐牌号码   false：顾客自取
    PrintConfiguration printConfiguration;
    boolean createDishFromPOS = false;
    boolean customerInfoForWaimai = false;//选取外卖时是否要填写顾客信息
    boolean printInvoiceBarcode = false; //是否在结账单上打发票二维码

    public PaymentFlow getPaymentFlow() {
        return paymentFlow;
    }

    public void setPaymentFlow(PaymentFlow paymentFlow) {
        this.paymentFlow = paymentFlow;
    }

    public ScanType getScanType() {
        return scanType;
    }

    public void setScanType(ScanType scanType) {
        this.scanType = scanType;
    }

    public List<Receipt> getReceiptList() {
        return receiptList;
    }

    public void setReceiptList(List<Receipt> receiptList) {
        this.receiptList = receiptList;
    }

    public List<Printer> getPrinterList() {
        return printerList;
    }

    public void setPrinterList(List<Printer> printerList) {
        this.printerList = printerList;
    }

    public String getStoreMode() {
        return storeMode;
    }

    public void setStoreMode(String storeMode) {
        this.storeMode = storeMode;
    }

    public Printer getReceiptPrinter() {
        return receiptPrinter;
    }

    public void setReceiptPrinter(Printer receiptPrinter) {
        this.receiptPrinter = receiptPrinter;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public boolean isCardNumberMode() {
        return cardNumberMode;
    }

    public void setCardNumberMode(boolean cardNumberMode) {
        this.cardNumberMode = cardNumberMode;
    }

    public PrintConfiguration getPrintConfiguration() {
        return printConfiguration;
    }

    public void setPrintConfiguration(PrintConfiguration printConfiguration) {
        this.printConfiguration = printConfiguration;
    }

    public boolean isCreateDishFromPOS() {
        return createDishFromPOS;
    }

    public void setCreateDishFromPOS(boolean createDishFromPOS) {
        this.createDishFromPOS = createDishFromPOS;
    }

    public boolean isCustomerInfoForWaimai() {
        return customerInfoForWaimai;
    }

    public void setCustomerInfoForWaimai(boolean customerInfoForWaimai) {
        this.customerInfoForWaimai = customerInfoForWaimai;
    }

    public boolean isPrintInvoiceBarcode() {
        return printInvoiceBarcode;
    }

    public void setPrintInvoiceBarcode(boolean printInvoiceBarcode) {
        this.printInvoiceBarcode = printInvoiceBarcode;
    }
}
