package cn.acewill.mobile.pos.model;

/**
 * 小票
 * Created by Acewill on 2016/8/23.
 */
public class Receipt {
    private int id;
    private String name;
    private ReceiptType type;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ReceiptType getType() {
        return type;
    }

    public void setType(ReceiptType type) {
        this.type = type;
    }
}
