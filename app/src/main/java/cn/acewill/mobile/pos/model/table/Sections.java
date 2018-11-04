package cn.acewill.mobile.pos.model.table;

import java.util.List;

/**
 * 桌台区域
 * Created by aqw on 2016/12/17.
 */
public class Sections {

    /**
     * id : 1055
     * appId : jwcj1
     * brandId : 49
     * storeId : 1
     * name : lobby
     * status : AVAILABLE
     * imageName : http://szfileserver.419174855.mtmssdn.com/dish/jwcj1/49/1/20161130171005_4863.jpeg
     * printerIdStr :
     * totalTables : 9
     * inuseTables : 6
     */

    private Long id;
    private String appId;
    private String brandId;
    private String storeId;
    private String name;
    private SectionStatus status;
    private String imageName;
    private String printerIdStr;
    private int totalTables;
    private int inuseTables;
    private List<TableInfor> tableList;
    private int sectionsStyle;

    public Sections(String name,int sectionsStyle)
    {
        this.name = name;
        this.sectionsStyle = sectionsStyle;
    }
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getBrandId() {
        return brandId;
    }

    public void setBrandId(String brandId) {
        this.brandId = brandId;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
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

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getPrinterIdStr() {
        return printerIdStr;
    }

    public void setPrinterIdStr(String printerIdStr) {
        this.printerIdStr = printerIdStr;
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

    public List<TableInfor> getTableList() {
        return tableList;
    }

    public void setTableList(List<TableInfor> tableList) {
        this.tableList = tableList;
    }

    public int getSectionsStyle() {
        return sectionsStyle;
    }

    public void setSectionsStyle(int sectionsStyle) {
        this.sectionsStyle = sectionsStyle;
    }
}
