package cn.acewill.mobile.pos.printer;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by linmingren on 16/8/12.
 */
public class TableWidthCalculator {
    private Table table;
    private Map<Integer, List<String>> column2Content; //列和对应的内容
    private Map<Integer, Integer> column2Width = new HashMap<>();
    private int totalWidth;

    /**
     *
     * @param table
     * @param totalWidth 打印纸张一行能打印多少个字符?
     */
    public TableWidthCalculator(Table table, int totalWidth) {
        this.table = table;
        this.totalWidth = totalWidth;
    }

    public void updateTableWidth() throws UnsupportedEncodingException {
        List<Row> rows = table.getRows();
        //把标题也加上 -- 因为计算宽度时需要把标题也计算在里面
        if (table.getTitle() != null) {
            rows.add(0,table.getTitle());
        }


        column2Content = new HashMap<>();
        for (Row row : rows) {
            if (row instanceof TextRow) {
                TextRow textRow = (TextRow) row;
                for (int column = 0; column < textRow.getColumnList().size(); ++column) {
                    List<String> contentList = column2Content.get(column);
                    if (contentList == null) {
                        contentList = new ArrayList<>();
                        column2Content.put(column, contentList);
                    }

                    contentList.add(textRow.getColumnList().get(column).content);
                }
            }
        }

        //计算每列的最大值
        int totalColumnWidth = 0;
        for (Integer column : column2Content.keySet()) {
            List<String> contentList = column2Content.get(column);
            int maxLength = 0;
            int contentLength = 0;
            for (int rowIndex =0 ;rowIndex < contentList.size(); ++rowIndex) {
                String content = contentList.get(rowIndex);
                contentLength = content.getBytes("GB2312").length * rows.get(rowIndex).getScaleWidth();
                if (contentLength > maxLength) {
                    maxLength = contentLength;
                }
            }

            totalColumnWidth += maxLength;
            column2Width.put(column, Integer.valueOf(maxLength));
        }

        int totalExtraWidth = totalWidth - totalColumnWidth; //每行多余多少个字符
        if (totalExtraWidth <=0) {
            return;
        }
        int extraSpaceColumn = table.getColumnSize(); //在2列中间插入空格列
        int extraWidthPerColumn = totalExtraWidth / extraSpaceColumn; //每列多余多少个字符
        int extraWidth = totalExtraWidth - (extraWidthPerColumn * extraSpaceColumn); //最后还剩一些宽度

        //调整每列的大小
        boolean firstColumn = true;
        for (Integer column : column2Width.keySet()) {
            column2Width.put(column, column2Width.get(column) + extraWidthPerColumn);
        }

        column2Width.put(extraSpaceColumn - 1, column2Width.get(extraSpaceColumn -1) + extraWidth);

        if (table.getTitle() != null) {
            List<Column> oldTitleSection = table.getTitle().getColumnList();
            List<Column> newTitleSection = updateRowSections(oldTitleSection, table.getTitle().getScaleWidth());

            //把每一列的内容都自动加上空格
            table.getTitle().setColumnList(newTitleSection);
        }

        //处理内容
        for(Row row : table.getRows()) {
            if (row instanceof TextRow) {
                TextRow textRow = (TextRow)row;
                textRow.setColumnList(updateRowSections(textRow.getColumnList(), row.getScaleWidth()));
            }
        }

        //删除标题，不然会重复打印标题
        if (table.getTitle() != null) {
            rows.remove(0);
        }
    }

    private List<Column> updateRowSections(List<Column> oldTitleSection, int widthScale) throws UnsupportedEncodingException {

        List<Column> newTitleSection = new ArrayList<>();
        for(int column = 0 ; column < oldTitleSection.size(); ++column) {

            String oldContent = oldTitleSection.get(column).content;
            if (oldTitleSection.get(column).getAlignment() == Alignment.LEFT) {
                String space = getSpace(column2Width.get(column) - oldContent.getBytes("GB2312").length * widthScale);
                newTitleSection.add(new Column(oldContent + space));
            } else if (oldTitleSection.get(column).getAlignment() == Alignment.RIGHT) {
                String space = getSpace(column2Width.get(column) - oldContent.getBytes("GB2312").length * widthScale);
                newTitleSection.add(new Column(space + oldContent));
            }

        }

        return newTitleSection;
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
