package cn.acewill.mobile.pos.printer;

/**
 * Created by Administrator on 2017-02-17.
 */
public class PrintModelInfo
{
    // 打印内容类型，详情参照 PrintModelType
    private PrintModelType modelType;

    // 打印的文本内容
    private String value = "";

    // 字体大小 0:小；1：中；2：大
    private int printSize = 0;
    // 布局，0：靠左； 1：中间； 2：靠右
    private int printAlian = 0;
    // 是否粗体
    private boolean isBold = false;
    // 是否斜体
    private boolean isItalic = false;
    // 倍高
    private boolean doubleH = false;
    // 倍宽
    private boolean doubleW = false;

    // 打印这一行后，是否打印换行
    private boolean printEnter = true;
    // 是否打印这一行
    private boolean shouldPrint = true;

    private int seq;

    private String displayName;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getPrintSize() {
        return printSize;
    }

    public void setPrintSize(int printSize) {
        this.printSize = printSize;
    }

    public int getPrintAlian() {
        return printAlian;
    }

    public void setPrintAlian(int printAlian) {
        this.printAlian = printAlian;
    }

    public boolean isBold() {
        return isBold;
    }

    public void setBold(boolean bold) {
        isBold = bold;
    }

    public boolean isItalic() {
        return isItalic;
    }

    public void setItalic(boolean italic) {
        isItalic = italic;
    }

    public boolean isDoubleH() {
        return doubleH;
    }

    public void setDoubleH(boolean doubleH) {
        this.doubleH = doubleH;
    }

    public boolean isDoubleW() {
        return doubleW;
    }

    public void setDoubleW(boolean doubleW) {
        this.doubleW = doubleW;
    }

    public boolean isPrintEnter() {
        return printEnter;
    }

    public void setPrintEnter(boolean printEnter) {
        this.printEnter = printEnter;
    }

    public boolean isShouldPrint() {
        return shouldPrint;
    }

    public void setShouldPrint(boolean shouldPrint) {
        this.shouldPrint = shouldPrint;
    }

    public PrintModelType getModelType() {
        return modelType;
    }

    public void setModelType(PrintModelType modelType) {
        this.modelType = modelType;
    }

    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}
