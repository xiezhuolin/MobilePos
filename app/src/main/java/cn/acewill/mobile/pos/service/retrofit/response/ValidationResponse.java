package cn.acewill.mobile.pos.service.retrofit.response;

import java.io.Serializable;
import java.util.List;

import cn.acewill.mobile.pos.utils.TimeUtil;


/**
 * Created by DHH on 2017/10/17.
 */

public class ValidationResponse implements Serializable {
    private int result = 1;//int  0表示成功，其他都表示失败，具体看message
    private int count = 0;//最大可验证张数 int
    private int minConsume = 0;//最小消费张数 int
    private int dealId;//项目ID  int
    private double couponBuyPrice;//购买价格 double
    private double dealPrice;//售卖价格 double
    private double dealValue;//市场价格(能够抵消多少现金) double
    private String message;// 返回消息 String 成功时为””，失败返回具体消息
    private String couponCode;// 券码 String
    private String dealBeginTime;// 项目开始时间 String (yyyy-MM-dd)
    private String couponEndTime;// 券码有效期 String
    private String dealTitle;// 项目名称 String
    private boolean isVoucher;//是否代金券   true代表代金券,false代表套餐券
    private List<DealMenu> dealMenu;//套餐类券码对应套餐详细内容，非套餐券不存在这个字段
    private int operateType = 0;// 0 不允许操作  1 可增加一项   2 可删除本项   3 初始项

    //通过时间判断该券是否处于有效期
    public boolean vouchersIsEff()
    {
        int vouchersTime = Integer.valueOf(couponEndTime.replace("-",""));
        int dayTime = Integer.valueOf(TimeUtil.getDayTime());
        return dayTime <= vouchersTime;
    }

    public boolean isSuccess()
    {
        if(result == 0)
        {
            return true;
        }
        else return false;
    }
    public static class DealMenu implements Serializable {
        private String content;// 套餐内容String
        private String specification;// 数量/规格  String
        private String price;// 单价 String
        private String total;// 小计 String
        private String type;// 区别表头和行 String  0表示表头，128表示行

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getTotal() {
            return total;
        }

        public void setTotal(String total) {
            this.total = total;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public String getSpecification() {
            return specification;
        }

        public void setSpecification(String specification) {
            this.specification = specification;
        }
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public List<DealMenu> getDealMenu() {
        return dealMenu;
    }

    public void setDealMenu(List<DealMenu> dealMenu) {
        this.dealMenu = dealMenu;
    }

    public String getDealTitle() {
        return dealTitle;
    }

    public void setDealTitle(String dealTitle) {
        this.dealTitle = dealTitle;
    }

    public String getCouponEndTime() {
        return couponEndTime;
    }

    public void setCouponEndTime(String couponEndTime) {
        this.couponEndTime = couponEndTime;
    }

    public String getDealBeginTime() {
        return dealBeginTime;
    }

    public void setDealBeginTime(String dealBeginTime) {
        this.dealBeginTime = dealBeginTime;
    }

    public String getCouponCode() {
        return couponCode;
    }

    public void setCouponCode(String couponCode) {
        this.couponCode = couponCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public double getDealValue() {
        return dealValue;
    }

    public void setDealValue(double dealValue) {
        this.dealValue = dealValue;
    }

    public double getDealPrice() {
        return dealPrice;
    }

    public void setDealPrice(double dealPrice) {
        this.dealPrice = dealPrice;
    }

    public double getCouponBuyPrice() {
        return couponBuyPrice;
    }

    public void setCouponBuyPrice(double couponBuyPrice) {
        this.couponBuyPrice = couponBuyPrice;
    }

    public int getDealId() {
        return dealId;
    }

    public void setDealId(int dealId) {
        this.dealId = dealId;
    }

    public int getMinConsume() {
        return minConsume;
    }

    public void setMinConsume(int minConsume) {
        this.minConsume = minConsume;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public boolean isVoucher() {
        return isVoucher;
    }

    public void setVoucher(boolean voucher) {
        isVoucher = voucher;
    }

    public int getOperateType() {
        return operateType;
    }

    public void setOperateType(int operateType) {
        this.operateType = operateType;
    }
}
