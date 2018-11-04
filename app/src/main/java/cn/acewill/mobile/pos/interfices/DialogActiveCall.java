package cn.acewill.mobile.pos.interfices;

import java.math.BigDecimal;

/**
 *营销活动回调
 * Created by aqw on 2017/1/9.
 */
public interface DialogActiveCall {
    public void onOk(BigDecimal allMoney, BigDecimal activeMoney, String activeName);
}
