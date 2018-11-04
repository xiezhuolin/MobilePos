package cn.acewill.mobile.pos.model.dish;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Created by DHH on 2017/1/4.
 */

public class Specification implements Serializable{
    public String name;
    public BigDecimal price;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}
