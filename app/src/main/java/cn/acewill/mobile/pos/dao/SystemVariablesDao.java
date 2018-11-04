package cn.acewill.mobile.pos.dao;


import com.raizlabs.android.dbflow.sql.language.Select;

import cn.acewill.mobile.pos.model.SystemVariables;
import cn.acewill.mobile.pos.model.SystemVariables_Table;


/**
 * Created by Acewill on 2016/8/15.
 */
public class SystemVariablesDao {
    private static final  String TAG = "SystemVariablesDao";

    public void save(SystemVariables sv) {
        sv.save();
    }

    //根据key获取变量名
    public SystemVariables getValue(String key) {
        SystemVariables systemVariables = new Select().from(SystemVariables.class).where(SystemVariables_Table.key.eq(key)).querySingle();

        return systemVariables;
    }
}
