package cn.acewill.mobile.pos.printer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by linmingren on 16/8/12.
 */
public class Table {
    TextRow title;
    int columnSize;
    List<Row> rows = new ArrayList<>();

    public Table(int columnSize) {
        this.columnSize = columnSize;
    }

    public void addTitle(TextRow title) {
        this.title = title;
    }

    public void addRow(Row row) {
        this.rows.add(row);
    }

    public TextRow getTitle() {
        return title;
    }

    public void setTitle(TextRow title) {
        this.title = title;
    }

    public List<Row> getRows() {
        return rows;
    }

    public void setRows(List<Row> rows) {
        this.rows = rows;
    }

    //获取列数
    public int getColumnSize() {
       return this.columnSize;
    }

}
