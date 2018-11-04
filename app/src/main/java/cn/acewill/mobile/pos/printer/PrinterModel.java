package cn.acewill.mobile.pos.printer;

/**
 * Created by DHH on 2017/3/23.
 */

public class PrinterModel {
    public int modelType;
    public String value;
    public int printSize;
    public int printAlian;
    public boolean doubleH;
    public boolean doubleW;
    public boolean printEnter;
    public boolean shouldPrint;
    public boolean bold;
    public boolean italic;
    public String seq;
    public String displayName;


    public int getModelType() {
        return modelType;
    }

    public void setModelType(int modelType) {
        this.modelType = modelType;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getSeq() {
        return seq;
    }

    public void setSeq(String seq) {
        this.seq = seq;
    }

    public boolean isShouldPrint() {
        return shouldPrint;
    }

    public void setShouldPrint(boolean shouldPrint) {
        this.shouldPrint = shouldPrint;
    }

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

    public boolean isItalic() {
        return italic;
    }

    public void setItalic(boolean italic) {
        this.italic = italic;
    }

    public boolean isBold() {
        return bold;
    }

    public void setBold(boolean bold) {
        this.bold = bold;
    }

    public boolean isPrintEnter() {
        return printEnter;
    }

    public void setPrintEnter(boolean printEnter) {
        this.printEnter = printEnter;
    }

    public boolean isDoubleW() {
        return doubleW;
    }

    public void setDoubleW(boolean doubleW) {
        this.doubleW = doubleW;
    }

    public boolean isDoubleH() {
        return doubleH;
    }

    public void setDoubleH(boolean doubleH) {
        this.doubleH = doubleH;
    }

    public int getPrintAlian() {
        return printAlian;
    }

    public void setPrintAlian(int printAlian) {
        this.printAlian = printAlian;
    }
}
