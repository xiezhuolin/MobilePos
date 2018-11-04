package cn.acewill.mobile.pos.model.table;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ModelContainer;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.util.List;

import cn.acewill.mobile.pos.factory.AppDatabase;


/**
 * Created by Acewill on 2016/6/2.
 * 桌台所在的区域， 比如大厅，包间等等
 */
@com.raizlabs.android.dbflow.annotation.Table(name="section",database = AppDatabase.class)
@ModelContainer
public class Section extends BaseModel {
    @Column
    @PrimaryKey(autoincrement = true)
    private long id;
    @Column
    private String name;
    @Column
    public SectionStatus status;

    //以下字段不存在数据库中
    private TableCounts tableCounts;
    public String imageName;//":
    private List<Table> tableList; //这个区域下有哪些桌台
    public SectionUIProperties sectionUIProperties;
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SectionStatus getStatus() {
        return status;
    }

    public void setStatus(SectionStatus status) {
        this.status = status;
    }

    public TableCounts getTableCounts() {
        return tableCounts;
    }

    public void setTableCounts(TableCounts tableCounts) {
        this.tableCounts = tableCounts;
    }

    public List<Table> getTableList() {
        return tableList;
    }

    public void setTableList(List<Table> tableList) {

        this.tableList = tableList;
    }
}
