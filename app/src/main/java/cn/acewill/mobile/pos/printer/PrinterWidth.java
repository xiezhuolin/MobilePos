package cn.acewill.mobile.pos.printer;

/**
 * Created by Acewill on 2016/8/19.
 */
public enum PrinterWidth {
    //打印纸的宽度是80mm，有效宽度72mm(576个点)
    WIDTH_80MM(576),
    //打印纸的宽度是56mm，有效宽度48mm(384个点)
    WIDTH_56MM(384),
    //打印纸的宽度是76
    WIDTH_76MM(100);

    int dotNumber;

    PrinterWidth(int dotNumber) {
        this.dotNumber = dotNumber;
    }

    public int getDotNumber() {
        return dotNumber;
    }

    public void setDotNumber(int dotNumber) {
        this.dotNumber = dotNumber;
    }
}
