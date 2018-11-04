package cn.acewill.mobile.pos.model.table;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ModelContainer;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.structure.BaseModel;

import cn.acewill.mobile.pos.factory.AppDatabase;


/**
 * Created by DHH on 2016/6/16.
 */
@com.raizlabs.android.dbflow.annotation.Table(name="area",database = AppDatabase.class)
@ModelContainer
public class Area extends BaseModel {
    @Column
    @PrimaryKey(autoincrement = true)
    private long id;
    private String appId; // 商户ID
    private long brandId; //品牌ID
    private long storeId; //门店ID
    @Column
    private String name; //就是区域1 大厅1这样的名字
    @Column
    private int totalTables; //总的桌台数
    @Column
    private int inuseTables; //正在使用的桌台数
    @Column
    private SectionStatus status;  //使用状态

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public SectionStatus getStatus() {
        return status;
    }

    public void setStatus(SectionStatus status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getStoreId() {
        return storeId;
    }

    public void setStoreId(long storeId) {
        this.storeId = storeId;
    }

    public long getBrandId() {
        return brandId;
    }

    public void setBrandId(long brandId) {
        this.brandId = brandId;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public int getTotalTables() {
        return totalTables;
    }

    public void setTotalTables(int totalTables) {
        this.totalTables = totalTables;
    }

    public int getInuseTables() {
        return inuseTables;
    }

    public void setInuseTables(int inuseTables) {
        this.inuseTables = inuseTables;
    }
}
