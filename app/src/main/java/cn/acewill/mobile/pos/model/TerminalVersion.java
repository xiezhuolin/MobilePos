package cn.acewill.mobile.pos.model;

/**
 * Created by DHH on 2016/9/5.
 */
public class TerminalVersion {
    /**
     * versionid : 11
     * version : 11
     * filename : http://szfileserver.419174855.mtmssdn.com/terminal/11/11/销售明细汇总.png
     * updatetime : 1473070397317
     * description :
     * brandname : 11
     * storename : 11
     * updatetimeStr : 2016-09-05 18:13:17
     */

    private String versionid;
    private String version;
    private String filename;
    private long updatetime;
    private String description;
    private String brandname;
    private String storename;
    private String updatetimeStr;

    public void setVersionid(String versionid) {
        this.versionid = versionid;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public void setUpdatetime(long updatetime) {
        this.updatetime = updatetime;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setBrandname(String brandname) {
        this.brandname = brandname;
    }

    public void setStorename(String storename) {
        this.storename = storename;
    }

    public void setUpdatetimeStr(String updatetimeStr) {
        this.updatetimeStr = updatetimeStr;
    }

    public String getVersionid() {
        return versionid;
    }

    public String getVersion() {
        return version;
    }

    public String getFilename() {
        return filename;
    }

    public long getUpdatetime() {
        return updatetime;
    }

    public String getDescription() {
        return description;
    }

    public String getBrandname() {
        return brandname;
    }

    public String getStorename() {
        return storename;
    }

    public String getUpdatetimeStr() {
        return updatetimeStr;
    }
}
