package cn.acewill.mobile.pos.printer;


import android.text.TextUtils;

import java.io.Serializable;
import java.util.List;

import cn.acewill.mobile.pos.model.ReceiptType;


/**
 * Created by Acewill on 2016/8/5.
 */
public class Printer implements Serializable{
    private Integer id;
    private String vendor; //品牌
    private String ip;
    private String deviceName; //蓝牙打印机的名字
    private PrinterWidth width = PrinterWidth.WIDTH_80MM;
    private String description = "";
    private List<ReceiptType> receiptTypeList;
    private Integer labelHeight; //标签打印纸的高度
    private String standbyPrinterIdList;//备用打印机的ID
    private String standbyPrinterName;//备用打印机的名称
    private PrinterLinkType linkType = PrinterLinkType.NETWORK; //连接类型， 网络，蓝牙，或者USB
    private PrinterOutputType outputType = PrinterOutputType.REGULAR; //打印的是标签， 还是普通的纸， 标签和普通打印的指令完全不一样
    private Integer summaryReceiptCounts = 0; //该打印机用来打印总单的份数， 0表示不打印总单
    private Integer dishReceiptCounts = 1; //该打印机用来打印分单的份数, 0表示不打印分单

    private PrinterState printerState = PrinterState.WATING;//先设置成等待检测
    private String errMessage = "";//如果不能使用,记录不能使用的原因
    private String sealOf = "";//打印机蜂鸣器响应时段

    private boolean isStandByState = false;


    public boolean isSelect = false;

    public Printer(String deviceName)
    {
        this.deviceName = deviceName;
    }
    public Printer()
    {

    }

    public List<ReceiptType> getReceiptTypeList() {
        return receiptTypeList;
    }

    public void setReceiptTypeList(List<ReceiptType> receiptTypeList) {
        this.receiptTypeList = receiptTypeList;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getVendor() {
        return vendor;
    }


    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getDescription() {
        if(TextUtils.isEmpty(description))
        {
            description = ip;
        }
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public PrinterWidth getWidth() {
        return width;
    }

    public void setWidth(PrinterWidth width) {
        this.width = width;
    }

    public Integer getLabelHeight() {
        return labelHeight;
    }

    public void setLabelHeight(Integer labelHeight) {
        this.labelHeight = labelHeight;
    }

    public PrinterLinkType getLinkType() {
        return linkType;
    }

    public void setLinkType(PrinterLinkType linkType) {
        this.linkType = linkType;
    }

    public PrinterOutputType getOutputType() {
        return outputType;
    }

    public void setOutputType(PrinterOutputType outputType) {
        this.outputType = outputType;
    }

    public Integer getSummaryReceiptCounts() {
        return summaryReceiptCounts;
    }

    public void setSummaryReceiptCounts(Integer summaryReceiptCounts) {
        this.summaryReceiptCounts = summaryReceiptCounts;
    }

    public Integer getDishReceiptCounts() {
        return dishReceiptCounts;
    }

    public void setDishReceiptCounts(Integer dishReceiptCounts) {
        this.dishReceiptCounts = dishReceiptCounts;
    }

    public String getErrMessage() {
        return errMessage;
    }

    public void setErrMessage(String errMessage) {
        this.errMessage = errMessage;
    }

    public PrinterState getPrinterState() {
        return printerState;
    }

    public void setPrinterState(PrinterState printerState) {
        this.printerState = printerState;
    }

    public String getStandbyPrinterIdList() {
        return standbyPrinterIdList;
    }

    public void setStandbyPrinterIdList(String standbyPrinterIdList) {
        this.standbyPrinterIdList = standbyPrinterIdList;
    }

    public String getStandbyPrinterName() {
        return standbyPrinterName;
    }

    public void setStandbyPrinterName(String standbyPrinterName) {
        this.standbyPrinterName = standbyPrinterName;
    }

    public boolean isStandByState() {
        return isStandByState;
    }

    public void setStandByState(boolean standByState) {
        isStandByState = standByState;
    }

    public String getSealOf() {
        return sealOf;
    }

    public void setSealOf(String sealOf) {
        this.sealOf = sealOf;
    }
}
