package cn.acewill.mobile.pos.model;

/**
 * Created by Acewill on 2016/8/23.
 */
public enum PaymentFlow {
    PAY_BEFORE("PAY_BEFORE"), //下单前支付
    PAY_AFTER("PAY_AFTER");//下单后支付

    private String name;

    PaymentFlow(String name) {
        this.name = name;
    }
}
