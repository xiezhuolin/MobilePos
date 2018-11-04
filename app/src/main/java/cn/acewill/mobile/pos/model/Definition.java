package cn.acewill.mobile.pos.model;

/**
 * Created by DHH on 2016/9/27.
 */
public class Definition {

    /**
     * id : 4
     * appId : a123
     * storeId : 3
     * brandId : 1
     * name : 早班
     * startTime : 6:00:00
     * endTime : 12:00:00
     */

    private Integer id;
    private String appId;
    private String storeId;
    private String brandId;
    private String name;
    private String startTime;
    private String endTime;

    public void setId(Integer id) {
        this.id = id;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public void setBrandId(String brandId) {
        this.brandId = brandId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public Integer getId() {
        return id;
    }

    public String getAppId() {
        return appId;
    }

    public String getStoreId() {
        return storeId;
    }

    public String getBrandId() {
        return brandId;
    }

    public String getName() {
        return name;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }
}
