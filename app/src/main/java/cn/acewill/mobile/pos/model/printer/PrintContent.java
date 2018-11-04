package cn.acewill.mobile.pos.model.printer;

import java.util.ArrayList;


public class PrintContent {
    public int modeStyle;
    public ArrayList<PrintInfo> modeInfo = new ArrayList<>();
    /**
     * 是否显示(选中显示)
     */
    public PrinterStyle.EnableStatus enableStatus;
    /**
     * 打印样式模板  那个模块显示 那个模块不显示
     */
    public PrinterStyle printerStyle;
    /**
     * 基础字体大小 20  几倍字体大小在这个基础上*2 *3 ...以此类推
     */
    public int TextSize = 20;
}
