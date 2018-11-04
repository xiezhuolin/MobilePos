package cn.acewill.mobile.pos.model.order;

import android.text.TextUtils;

import java.math.BigDecimal;
import java.util.ArrayList;

import cn.acewill.mobile.pos.model.dish.Cart;


/**
 * Created by Acewill on 2016/8/3.
 */
public class OrderDiscount {
    public DiscountTime discountTime;// : 折扣时段，
    public ArrayList<DiscountInfo> discountInfo;// : [ 折扣方案, array
    public class DiscountInfo {
        public int discountType;// : 折扣类型int, 0:满减， 1:折扣
        public String supportMinPrice;// : 高于某价格开始执行, decimal
        public String supportMaxPrice;// : 高于某价格开始不执行, decimal
        public String unsupportMinPrice;// :低于某价格部分不参与, decimal
        public String unsupportMaxPrice;// : 高于某价格部分不参与, decimal
        public String rate;// : 折扣率, decaimal(2位小数，例如0.8，8折)
        public String reduce;// ：直减价， decimal
    }
    /**
     *
     * @param totalPrice
     * @return 折扣后的价格
     */
    public float getDiscountPrice(float totalPrice) {

        for (DiscountInfo item : discountInfo) {

            if (!TextUtils.isEmpty(item.supportMaxPrice)) {
                if (totalPrice > Float.valueOf(item.supportMaxPrice)) {
                    return totalPrice;
                }
            }
            if (!TextUtils.isEmpty(item.unsupportMinPrice)) {
                if (totalPrice < Float.valueOf(item.unsupportMinPrice)) {
                    return totalPrice;
                }
            }
            if (!TextUtils.isEmpty(item.supportMinPrice)) {
                if (totalPrice < Float.valueOf(item.supportMinPrice)) {
                    return totalPrice;
                }
            }
            if (!TextUtils.isEmpty(item.supportMaxPrice)) {
                if (totalPrice > Float.valueOf(item.supportMaxPrice)) {
                    return totalPrice;
                }
            }
            if (item.discountType == 0) {
                Cart.subtraction = item.reduce;
                Cart.discount = "1";
                return new BigDecimal(totalPrice).subtract(
                        new BigDecimal(item.reduce)).floatValue();
            } else {
                Cart.subtraction = "0";
                Cart.discount = item.rate;
                return new BigDecimal(totalPrice).multiply(
                        new BigDecimal(item.rate)).floatValue();
            }

        }

        return totalPrice;
    }

}
