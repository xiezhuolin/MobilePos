package cn.acewill.mobile.pos.model.cache;

import com.google.gson.annotations.SerializedName;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ModelContainer;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.structure.BaseModel;

import cn.acewill.mobile.pos.factory.AppDatabase;


/**
 * Created by Acewill on 2016/12/8.
 */
@com.raizlabs.android.dbflow.annotation.Table(name="cached_call_number",database =AppDatabase.class)
@ModelContainer
public class CachedCallNumber extends BaseModel {
    @Column
    @PrimaryKey
    @SerializedName( "id" )
    private long id;

    @Column
    @SerializedName( "number" )
    private int number;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }
}
