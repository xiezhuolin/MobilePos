package cn.acewill.mobile.pos.printer;

/**
 * Created by DHH on 2017/3/24.
 */

public enum  PrinterStatus {
    COVER_OPEN,//上盖开
    ERROR_OCCURED,//有错误情况
    NO_PAPER,//没有纸
    TIMEOUT_ERROR,
    OK; //状态正常

    public static byte COVER_OPEN_VALUE = 0x04;//上盖开
    public static byte NO_PAPER_VALUE = 0x32;//没有纸
    public static byte ERROR_OCCURED_VALUE = 0x64; //有错误情况
}
