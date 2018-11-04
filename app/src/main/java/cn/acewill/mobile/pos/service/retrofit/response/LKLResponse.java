package cn.acewill.mobile.pos.service.retrofit.response;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Created by DHH on 2017/3/10.
 */

public class LKLResponse implements Serializable{
    private String id;
    private String orderNo;
    private BigDecimal amount;
    private String subject;
    private String body;
    private String channel;
    private String clientIp;
    private String description;
    private Object metadata;
    /**
     * user_id : 789456
     * return_url :
     */

    private Extra extra;
    private Object transactionNo;
    private String app;
    private int amountSettle;
    private String currency;
    private int amountRefunded;
    private Object refunded;
    private long timeCreated;
    private Object timePaid;
    private long timeExpire;
    private Object timeSettle;
    /**
     * lakala_app : {"totalAmount":"100","merchantUserNo":"789456","orderTime":"20170309","merchantId":"888010048160001","merchantOrderNo":"ch_df8b3c6365a9d22d2a303411","token":"20170309201703090023557398","merchantName":"Paymax 联调演示账号"}
     */

    private Credential credential;
    private Object failureCode;
    private Object failureMsg;
    private boolean liveMode;
    private String status;
    private boolean reqSuccessFlag;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getClientIp() {
        return clientIp;
    }

    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Object getMetadata() {
        return metadata;
    }

    public void setMetadata(Object metadata) {
        this.metadata = metadata;
    }

    public Extra getExtra() {
        return extra;
    }

    public void setExtra(Extra extra) {
        this.extra = extra;
    }

    public Object getTransactionNo() {
        return transactionNo;
    }

    public void setTransactionNo(Object transactionNo) {
        this.transactionNo = transactionNo;
    }

    public String getApp() {
        return app;
    }

    public void setApp(String app) {
        this.app = app;
    }

    public int getAmountSettle() {
        return amountSettle;
    }

    public void setAmountSettle(int amountSettle) {
        this.amountSettle = amountSettle;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public int getAmountRefunded() {
        return amountRefunded;
    }

    public void setAmountRefunded(int amountRefunded) {
        this.amountRefunded = amountRefunded;
    }

    public Object getRefunded() {
        return refunded;
    }

    public void setRefunded(Object refunded) {
        this.refunded = refunded;
    }

    public long getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(long timeCreated) {
        this.timeCreated = timeCreated;
    }

    public Object getTimePaid() {
        return timePaid;
    }

    public void setTimePaid(Object timePaid) {
        this.timePaid = timePaid;
    }

    public long getTimeExpire() {
        return timeExpire;
    }

    public void setTimeExpire(long timeExpire) {
        this.timeExpire = timeExpire;
    }

    public Object getTimeSettle() {
        return timeSettle;
    }

    public void setTimeSettle(Object timeSettle) {
        this.timeSettle = timeSettle;
    }

    public Credential getCredential() {
        return credential;
    }

    public void setCredential(Credential credential) {
        this.credential = credential;
    }

    public Object getFailureCode() {
        return failureCode;
    }

    public void setFailureCode(Object failureCode) {
        this.failureCode = failureCode;
    }

    public Object getFailureMsg() {
        return failureMsg;
    }

    public void setFailureMsg(Object failureMsg) {
        this.failureMsg = failureMsg;
    }

    public boolean isLiveMode() {
        return liveMode;
    }

    public void setLiveMode(boolean liveMode) {
        this.liveMode = liveMode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isReqSuccessFlag() {
        return reqSuccessFlag;
    }

    public void setReqSuccessFlag(boolean reqSuccessFlag) {
        this.reqSuccessFlag = reqSuccessFlag;
    }

    public static class Extra {
        private String user_id;
        private String return_url;

        public String getUser_id() {
            return user_id;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }

        public String getReturn_url() {
            return return_url;
        }

        public void setReturn_url(String return_url) {
            this.return_url = return_url;
        }
    }

    public static class Credential {
        /**
         * totalAmount : 100
         * merchantUserNo : 789456
         * orderTime : 20170309
         * merchantId : 888010048160001
         * merchantOrderNo : ch_df8b3c6365a9d22d2a303411
         * token : 20170309201703090023557398
         * merchantName : Paymax 联调演示账号
         */

        private WechatCsb wechat_csb;

        public WechatCsb getWechat_csb() {
            return wechat_csb;
        }

        public void setLakala_app(WechatCsb wechat_csb) {
            this.wechat_csb = wechat_csb;
        }

        public static class WechatCsb {
            private String qr_code;

            public String getQr_code() {
                return qr_code;
            }

            public void setQr_code(String qr_code) {
                this.qr_code = qr_code;
            }
        }
    }
}
