package cn.acewill.mobile.pos.printer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Acewill on 2016/6/8.
 */
public class TextRow implements Row{
    private byte height = 0x1e; //1 - 255
    private Alignment align = Alignment.LEFT; //左 中 右 对齐
    private int scaleWidth = 1; //宽度倍数
    private int scaleHeight =1 ; //宽度倍数
    private boolean boldFont = false; //字体是否加粗
    private Font font = Font.REGULAR_ASCII;
    private boolean revertMode = false; //是否反显打印 -- 也就是字体白色，背景黑色
    private boolean showUnderline = false; //显示下划线

    //一行可能包含多个字段
    List<Column> sectionList = new ArrayList<>();
    public TextRow(Column section) {
        sectionList.add(section);
    }

    public TextRow(String content) {
        this(new Column(content));
    }

    public TextRow() {

    }

    public void addColumn(Column section) {
        sectionList.add(section);
    }

    public List<Column> getColumnList() {
        return sectionList;
    }

    public void setColumnList(List<Column> sectionList) {
        this.sectionList = sectionList;
    }

    public byte getHeight() {
        return height;
    }

    public void setHeight(byte height) {
        this.height = height;
    }

    public Alignment getAlign() {
        return align;
    }

    public void setAlign(Alignment align) {
        this.align = align;
    }

    public int getScaleWidth() {
        return scaleWidth;
    }

    public void setScaleWidth(int scaleWidth) {
        this.scaleWidth = scaleWidth;
    }

    public int getScaleHeight() {
        return scaleHeight;
    }

    public void setScaleHeight(int scaleHeight) {
        this.scaleHeight = scaleHeight;
    }

    public boolean isBoldFont() {
        return boldFont;
    }

    public void setBoldFont(boolean boldFont) {
        this.boldFont = boldFont;
    }

    public Font getFont() {
        return font;
    }

    public void setFont(Font font) {
        this.font = font;
    }

    public boolean isRevertMode() {
        return revertMode;
    }

    public void setRevertMode(boolean revertMode) {
        this.revertMode = revertMode;
    }

    public boolean isShowUnderline() {
        return showUnderline;
    }

    public void setShowUnderline(boolean showUnderline) {
        this.showUnderline = showUnderline;
    }
}
