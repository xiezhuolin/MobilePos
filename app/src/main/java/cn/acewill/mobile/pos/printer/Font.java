package cn.acewill.mobile.pos.printer;

/**
 * Created by Acewill on 2016/8/11.
 */
public enum Font {
    REGULAR_ASCII(14), //普通ASCII字符 （12* 24个点）
    SMALL_ASCII(11); //(9x17个点)
    //一个汉字相当2个英文

    private int dotNumber; // 每个字符所占的点数

    Font(int dotNumber) {
        this.dotNumber = dotNumber;
    }

    public int getDotNumber() {
        return dotNumber;
    }

    public void setDotNumber(int dotNumber) {
        this.dotNumber = dotNumber;
    }
}
