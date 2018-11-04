package cn.acewill.mobile.pos.interfices;

import java.math.BigDecimal;
import java.util.List;

import cn.acewill.mobile.pos.model.payment.Payment;
import cn.acewill.mobile.pos.model.wsh.Account;
import cn.acewill.mobile.pos.model.wsh.WshDealPreview;


/**
 * Created by aqw on 2016/12/3.
 */
public interface CreatDealBack {
    public void onDeal(String bizid, WshDealPreview result, BigDecimal money, boolean isCheckOut, Account account, List<Payment> memberPayMent);//微生活创建交易预览成功后调用
}
