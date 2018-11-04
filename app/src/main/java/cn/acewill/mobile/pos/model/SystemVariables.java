package cn.acewill.mobile.pos.model;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ModelContainer;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.structure.BaseModel;

import cn.acewill.mobile.pos.factory.AppDatabase;


/**
 * Created by Acewill on 2016/8/16.
 */
@com.raizlabs.android.dbflow.annotation.Table( name = "system_variables", database = AppDatabase.class )
@ModelContainer
public class SystemVariables extends BaseModel {

    @PrimaryKey
    @Column
    private String key;
    @Column
    private String value;

    public SystemVariables() {

    }
    public SystemVariables(String key, String value) {
        this.key = key;
        this.value = value;
    }


    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
