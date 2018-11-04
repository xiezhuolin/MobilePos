package cn.acewill.mobile.pos.model.dish;

import com.google.gson.annotations.SerializedName;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ModelContainer;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.io.Serializable;
import java.math.BigDecimal;

import cn.acewill.mobile.pos.factory.AppDatabase;


/**
 * 口味
 * Created by Acewill on 2016/6/2.
 */
@com.raizlabs.android.dbflow.annotation.Table(name="option",database = AppDatabase.class)
@ModelContainer
public class Option extends BaseModel implements Serializable {
    @Column
    @PrimaryKey(autoincrement = true)
//    @SerializedName("tasteID")
    public int id;
    @Column
    @SerializedName("optionName")
    public String name;
    @Column
    public BigDecimal price;
    @Column
    public boolean required;
    @Column
    public int categoryId;

    public boolean isSelect = false;

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

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public boolean getSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }
}
