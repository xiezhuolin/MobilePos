package cn.acewill.mobile.pos.printer.vendor;

import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.AlignmentSpan;
import android.text.style.StyleSpan;
import android.widget.TextView;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import cn.acewill.mobile.pos.printer.Alignment;
import cn.acewill.mobile.pos.printer.Barcode;
import cn.acewill.mobile.pos.printer.BitmapRow;
import cn.acewill.mobile.pos.printer.Column;
import cn.acewill.mobile.pos.printer.PrinterInterface;
import cn.acewill.mobile.pos.printer.PrinterStatus;
import cn.acewill.mobile.pos.printer.Row;
import cn.acewill.mobile.pos.printer.Separator;
import cn.acewill.mobile.pos.printer.Table;
import cn.acewill.mobile.pos.printer.TableWidthCalculator;
import cn.acewill.mobile.pos.printer.TextRow;


/**
 * 把打印内容写到TextView上，这样可以在界面预览打印内容
 * 最大的难点是textview上的字符就算设置了MONOSPACE, 也不能保证一个中文字符的宽度刚好是一个英文字符的2倍 (虽然用paint.measureText计算出来的值确实是2倍)，这样计算宽度总会出错。
 *
 *  用 paint.getTextBounds来计算宽度可能更合理，但是那样表格的宽度计算太复杂了。
 *
 * Created by Acewill on 2016/8/25.
 */
public class PreviewPrinter implements PrinterInterface {
    private TextView textView;
    private int maxCharacterPerRow;
    private int NORMAL_FONT_SIZE = 16;
    private Paint paint;


    List<SpanSetting> settingList = new ArrayList<>(); // 每一行对应的配置
    private int currentStringIndex = -1;
    private String text = "";

    public PreviewPrinter(TextView textView, int maxCharacterPerRow) {
        this.textView = textView;
        this.paint = textView.getPaint();
        this.maxCharacterPerRow = maxCharacterPerRow;
    }

    @Override
    public void init() throws IOException {
        //不需要处理
    }

    @Override
    public void close() throws IOException {
        //设置到textview上
        textView.setTypeface(Typeface.MONOSPACE); //所有字符一样的宽度
        textView.setTextSize(NORMAL_FONT_SIZE);
        textView.setTextScaleX(0);

        SpannableString spannableString = new SpannableString(text);

        for (SpanSetting ss : settingList) {
            spannableString.setSpan(ss.getWhat(), ss.getStart(), ss.getEnd(), ss.getFlags());
        }

        textView.setText(spannableString);
    }

    @Override
    public void closeNoCutPaper() throws IOException {

    }

    @Override
    public void openCachBox() throws IOException {

    }

    @Override
    public void openBuzzer() throws IOException {

    }

    @Override
    public void openSumMiCachBox() throws IOException {

    }

    @Override
    public PrinterStatus checkStatus() throws IOException {
        return null;
    }

    @Override
    public void printRow(Row row) throws IOException {
        if (row instanceof TextRow) {
            TextRow textRow = (TextRow) row;

            //如果有多列， 则重新计算宽度
            /*if (textRow.getColumnList().size() > 1) {
                RowWidthCalculator calculator = new RowWidthCalculator(textRow, maxCharacterPerRow, paint);
                calculator.updateTableWidth();
            }*/

            addRowSpan(textRow);
        } else if (row instanceof Separator) {
            //默认字体是REGULAR_ASCII
            String separator = ((Separator)row).getContent();
            for (int i = 0; i < this.maxCharacterPerRow; ++i) {
                text += separator;
            }
            text +=  "\n";

            //两个字符的宽度是一个中文要大一点
            SpanSetting rs = new SpanSetting(new AbsoluteSizeSpan((int)(NORMAL_FONT_SIZE)), text.length() - maxCharacterPerRow-1, text.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            this.settingList.add(rs);
        }
    }

    @Override
    public void printTable(Table table) throws IOException {
        TableWidthCalculator tc = new TableWidthCalculator(table, this.maxCharacterPerRow);
        tc.updateTableWidth();

        if (table.getTitle() != null) {
            printRow(table.getTitle());
        }

        for (Row row : table.getRows()) {
            printRow(row);
        }
    }

    @Override
    public void printBarcode(Barcode barcode) throws IOException {

    }

    @Override
    public void printBmp(BitmapRow bitmapRow, boolean isEnter) throws IOException {

    }

    private String getRowContent(TextRow row) {
        String rowContent = "";
        for(Column column : row.getColumnList()) {
            rowContent += column.getContent();
        }

        Rect rect = new Rect();
        paint.getTextBounds(rowContent,0, rowContent.length() -1, rect);
        try {
            System.out.println("row length: " + rowContent.getBytes("gb2312").length + " bound: " + rect.width() + " measure: " + paint.measureText(rowContent) +  " content: \"" + rowContent + "\"");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return rowContent;
    }

    private void addRowSpan(TextRow row) {
        String rowContent = getRowContent(row);


        SpanSetting rs = new SpanSetting(new AbsoluteSizeSpan(NORMAL_FONT_SIZE * row.getScaleWidth()), text.length(), text.length() + rowContent.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        this.settingList.add(rs);

        if (row.isBoldFont()) {
            rs = new SpanSetting(new StyleSpan(Typeface.BOLD), text.length() , text.length() + rowContent.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            this.settingList.add(rs);
        }

        //只有一列的话，对齐在行级别处理
        if (row.getColumnList().size() == 1) {
            if (row.getAlign() == Alignment.LEFT) {
                rs = new SpanSetting(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_NORMAL), text.length() , text.length() + rowContent.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            } else if (row.getAlign() == Alignment.CENTER) {
                rs = new SpanSetting(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER), text.length() , text.length() + rowContent.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            } else {
                //右对齐
                rs = new SpanSetting(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_OPPOSITE), text.length() , text.length() + rowContent.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            }

            this.settingList.add(rs);
        }

        text += rowContent + "\n";
    }

    private static class SpanSetting {
        private Object what;
        private int start;
        private int end;
        private int flags;

        public SpanSetting(Object what, int start, int end, int flags) {
            this.what = what;
            this.start = start;
            this.end = end;
            this.flags = flags;
        }

        public Object getWhat() {
            return what;
        }

        public void setWhat(Object what) {
            this.what = what;
        }

        public int getStart() {
            return start;
        }

        public void setStart(int start) {
            this.start = start;
        }

        public int getEnd() {
            return end;
        }

        public void setEnd(int end) {
            this.end = end;
        }

        public int getFlags() {
            return flags;
        }

        public void setFlags(int flags) {
            this.flags = flags;
        }
    }
}
