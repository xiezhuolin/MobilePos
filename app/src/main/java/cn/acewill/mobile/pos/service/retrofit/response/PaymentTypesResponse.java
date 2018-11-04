package cn.acewill.mobile.pos.service.retrofit.response;

import java.util.List;

import cn.acewill.mobile.pos.model.payment.Payment;


/**
 * Created by Acewill on 2016/8/3.
 */
public class PaymentTypesResponse extends PosResponse {
    private List<Payment> paymentTypes;

    public List<Payment> getPaymentTypes() {
        return paymentTypes;
    }

    public void setPaymentTypes(List<Payment> paymentTypes) {
        this.paymentTypes = paymentTypes;
    }
}
