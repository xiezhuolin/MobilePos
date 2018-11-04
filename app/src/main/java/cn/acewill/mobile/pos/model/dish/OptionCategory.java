package cn.acewill.mobile.pos.model.dish;

import com.google.gson.annotations.SerializedName;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ModelContainer;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.io.Serializable;
import java.util.List;

import cn.acewill.mobile.pos.factory.AppDatabase;


/**
 * 口味
 * Created by Acewill on 2016/6/2.
 */
@com.raizlabs.android.dbflow.annotation.Table(name="OptionCategory",database = AppDatabase.class)
@ModelContainer
public class OptionCategory extends BaseModel implements Serializable {
    @Column
    @PrimaryKey(autoincrement = true)
//    @SerializedName("tasteID")
    public int id;				//品项口味ID
    public List<Option> optionList;
    @SerializedName("optionCategoryName")
    public String name;
    public int minimalOptions;//该分类最少选择几个选项
    public boolean multipleOptions;
    public int maximalOptions;//该分类最多可选择几个选项
    public int seq;//该分类显示时的排序号

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<Option> getOptionList() {
        return optionList;
    }

    public void setOptionList(List<Option> optionList) {
        this.optionList = optionList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isMultipleOptions() {
        return multipleOptions;
    }

    public void setMultipleOptions(boolean multipleOptions) {
        this.multipleOptions = multipleOptions;
    }

    public int getMinimalOptions() {
        return minimalOptions;
    }

    public void setMinimalOptions(int minimalOptions) {
        this.minimalOptions = minimalOptions;
    }

    public int getMaximalOptions() {
        return maximalOptions;
    }

    public void setMaximalOptions(int maximalOptions) {
        this.maximalOptions = maximalOptions;
    }

    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }
}
