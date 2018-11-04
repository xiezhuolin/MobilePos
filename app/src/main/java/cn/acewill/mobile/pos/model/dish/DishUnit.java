package cn.acewill.mobile.pos.model.dish;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;

/**
 * Created by Acewill on 2016/6/2.
 */
public class DishUnit {
    @Column
    @PrimaryKey(autoincrement = true)
    private long id;

    private String name; // 单位的名字,比如半份

}
