package cn.acewill.mobile.pos.printer.gpnetwork;

/**
 * Created by aqw on 2017/1/3.
 */
public enum FontType {
    ONE("1"),//8 x 12 dot 英数字体
    TWO("2"),//12 x 20 dot英数字体
    THREE("3"),//16 x 24 dot英数字体
    FOUR("4"),//24 x 32 dot英数字体
    FIVE("5"),//32 x 48 dot英数字体
    SIX("6"),//14 x 19 dot英数字体 OCR-B
    SEVEN("7"),//21 x 27 dot 英数字体OCR-B
    EIGHT("8"),//14 x25 dot英数字体OCR-A
    TST24("TST24.BF2"),//繁体中文 24 x 24 font (大五码)
    TSS24("TSS24.BF2"),//简体中文 24 x 24 font (GB 码)
    K("K");//韩文 24 x 24 font (KS 码)

    private String type;
    private FontType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
