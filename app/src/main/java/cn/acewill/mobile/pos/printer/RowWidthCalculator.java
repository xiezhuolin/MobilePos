package cn.acewill.mobile.pos.printer;

import android.graphics.Paint;

import java.io.UnsupportedEncodingException;

/**
 * Created by Acewill on 2016/8/26.
 */
public class RowWidthCalculator {
    private TextRow row;
    private int totalWidth;
  //  private Paint paint;

    public RowWidthCalculator(TextRow row, int width, Paint paint) {
        this.row = row;
        this.totalWidth = width;
     //   this.paint = paint;
    }

    public void updateTableWidth() throws UnsupportedEncodingException {
        int contentWidth = 0; //真正的文字内容所占用的宽度
        for (Column column : row.getColumnList()) {
            contentWidth += column.getContent().getBytes("gb2312").length;//int)paint.measureText(column.getContent());
        }

        int extraWidth = totalWidth - contentWidth;//多余的宽度
        int extraWidthPerColumn = extraWidth / row.getColumnList().size(); //每列多余多少宽度
        //int extraSpace = extraWidthPerColumn / (int)paint.measureText(" ");


        String space = getSpace(extraWidthPerColumn);
        //在每列上加上空格
        for (Column column : row.getColumnList()) {
            if (column.getAlignment() == Alignment.LEFT) {
                column.setContent(column.getContent() + space);
            } else if (column.getAlignment() == Alignment.RIGHT) {
                column.setContent(space + column.getContent());
            }
        }

    }

    private String getSpace(int size) {
        int i = 0;
        String space = "";
        while (i ++ <  size) {
            space += " ";
        }

        return space;
    }
}
