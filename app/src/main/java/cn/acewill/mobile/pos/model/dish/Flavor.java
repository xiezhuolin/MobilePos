package cn.acewill.mobile.pos.model.dish;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ModelContainer;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.io.Serializable;

import cn.acewill.mobile.pos.factory.AppDatabase;


/**
 * 口味
 * Created by Acewill on 2016/6/2.
 */
@com.raizlabs.android.dbflow.annotation.Table(name="flavor",database = AppDatabase.class)
@ModelContainer
public class Flavor extends BaseModel implements Serializable {
    @Column
    @PrimaryKey(autoincrement = true)
//    @SerializedName("tasteID")
    public int tasteID;				//品项口味ID
    @Column
    public float tasteExtraCost;// 口味价格, decimal
    @Column
    public String tasteName;// :口味名称, String

    public int getTasteID() {
        return tasteID;
    }

    public void setTasteID(int tasteID) {
        this.tasteID = tasteID;
    }

    public float getTasteExtraCost() {
        return tasteExtraCost;
    }

    public void setTasteExtraCost(float tasteExtraCost) {
        this.tasteExtraCost = tasteExtraCost;
    }

    public String getTasteName() {
        return tasteName;
    }

    public void setTasteName(String tasteName) {
        this.tasteName = tasteName;
    }
}
