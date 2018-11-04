package cn.acewill.mobile.pos.model.table;

import java.math.BigDecimal;

/**
 * Created by aqw on 2016/12/19.
 */
public class TableData {

    /**
     * id : 943
     * number : 2
     * status : 2
     * total : 21
     * time : 2016-11-08 18:25:01.0
     */
    private Long id;
    private int number;
    private String status;
    private BigDecimal total;
    private String time;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public TableStatus getStatus() {
        if("1".equals(status)){
            return TableStatus.EMPTY;
        }else if("2".equals(status)){
            return TableStatus.IN_USE;
        }else{
            return TableStatus.DIRTY;
        }
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
