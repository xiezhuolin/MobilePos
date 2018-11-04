package cn.acewill.mobile.pos.printer;


import java.io.IOException;

/**
 * 打印机统一接口
 * Created by Acewill on 2016/8/17.
 */
public interface PrinterInterface {
    void init() throws IOException;
    void close() throws IOException;
    void closeNoCutPaper() throws IOException;
    void openCachBox() throws IOException;
    void openBuzzer() throws IOException;
    void openSumMiCachBox() throws IOException;
    PrinterStatus checkStatus() throws IOException;


    /**
     * 打印一行, 可以是文本行， 条形码， 图像， 或者一行分隔符
     * 对应的实例是Text, Barcode, Separator
     * @param row
     * @throws IOException
     */
    void printRow(Row row) throws IOException;

    /**
     * 打印一个表格，表格可以有标题，也可以无标题， 可以有内容，也可以无内容
     * @param table
     * @throws IOException
     */
    void printTable(Table table) throws IOException;

    /**
     * 打印条维码
     * @param barcode
     * @throws IOException
     */
    void printBarcode(Barcode barcode) throws IOException;

    /**
     * 打印二维码
     * @param bitmapRow
     * @throws IOException
     */
    void printBmp(BitmapRow bitmapRow, boolean isEnter) throws IOException;


}
