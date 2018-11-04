package cn.acewill.mobile.pos.model.wsh;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 账号里关联的卡券
 * Created by aqw on 2016/10/19.
 */
public class WshAccountCoupon implements Serializable{
    private String template_id;
    private List<String> coupon_ids;
    private String title;
    private float deno;//券面值
    private int type;//券类型 1:代金券；2:礼品券
    private List<Long> sids;//可用门店id列表
    private String effective_time;//券生效时间
    private String failure_time;//券失效时间
    private List<String> limitations;//使用条件与限制
    private BigDecimal enable_amount;//启用金额，例如:总金额满100元可用(单位:元)0为不限制
    private int max_use;//同类型的券，一次最多可以使用几张
    private boolean mix_use;//是否支持与其他券混合使用
    private int selectCount;//已选券张数
    private List<Long>   products;

    public String getTemplate_id() {
        return template_id;
    }

    public void setTemplate_id(String template_id) {
        this.template_id = template_id;
    }

    public List<String> getCoupon_ids() {
        return coupon_ids;
    }

    public void setCoupon_ids(List<String> coupon_ids) {
        this.coupon_ids = coupon_ids;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public float getDeno() {
        return deno;
    }

    public void setDeno(float deno) {
        this.deno = deno;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }


    public List<Long> getSids() {
        return sids;
    }

    public void setSids(List<Long> sids) {
        this.sids = sids;
    }

    public String getEffective_time() {
        return effective_time;
    }

    public void setEffective_time(String effective_time) {
        this.effective_time = effective_time;
    }

    public String getFailure_time() {
        return failure_time;
    }

    public void setFailure_time(String failure_time) {
        this.failure_time = failure_time;
    }

    public List<String> getLimitations() {
        return limitations;
    }

    public void setLimitations(List<String> limitations) {
        this.limitations = limitations;
    }

    public BigDecimal getEnable_amount() {
        return enable_amount;
    }

    public void setEnable_amount(BigDecimal enable_amount) {
        this.enable_amount = enable_amount;
    }

    public int getMax_use() {
        return max_use;
    }

    public void setMax_use(int max_use) {
        this.max_use = max_use;
    }

    public boolean isMix_use() {
        return mix_use;
    }

    public void setMix_use(boolean mix_use) {
        this.mix_use = mix_use;
    }

    public int getSelectCount() {
        return selectCount;
    }

    public void setSelectCount(int selectCount) {
        this.selectCount = selectCount;
    }

    public List<Long> getProducts() {
        return products;
    }

    public void setProducts(List<Long> products) {
        this.products = products;
    }
}
