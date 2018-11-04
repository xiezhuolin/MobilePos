package cn.acewill.mobile.pos.model.dish;

import android.text.TextUtils;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.io.Serializable;

/**
 * 烹饪方式
 * Created by Acewill on 2016/6/2.
 * useIsForPrivateBooleans -- 对布尔变量不需要getXXX方法，而用isXXX方法
 */
//@com.raizlabs.android.dbflow.annotation.Table(name="cooking_method",database = AppDatabase.class,useIsForPrivateBooleans = true)
//@ModelContainer
public class CookingMethod extends BaseModel implements Serializable {
    @Column
    @PrimaryKey(autoincrement = true)
    public int cookID;// : 做法ID, String
    @Column
    public String cookName;// : 做法名称, String
    @Column
    public String price;	//价格
    public String cookExtraCost;	//价格

    public float getPrice() {
        price = cookExtraCost;
        if (TextUtils.isEmpty(price)) {
            return 0;
        }
        return Float.valueOf(price);
    }
//    @Column
//    private String alias;		//拼音首字母
//    @Column
//    private boolean evaluateValue;  //这种做法是否要另外加钱？
//    @Column
//    private int displayOrder; //显示顺序


}
