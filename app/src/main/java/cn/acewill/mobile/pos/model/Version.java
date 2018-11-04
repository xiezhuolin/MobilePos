package cn.acewill.mobile.pos.model;

/**版本信息
 * Created by aqw on 2016/8/22.
 */
public class Version {

    private String versionName;
    private int versionCode;
    private String des;//描述
    private String tips;//更新提醒

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    public String getTips() {
        return tips;
    }

    public void setTips(String tips) {
        this.tips = tips;
    }
}
