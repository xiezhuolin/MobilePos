package cn.acewill.mobile.pos.dao;

import com.raizlabs.android.dbflow.sql.language.Select;

import java.util.List;

import cn.acewill.mobile.pos.model.WorkShift;
import cn.acewill.mobile.pos.model.WorkShift_Table;


/**
 * Created by Acewill on 2016/8/18.
 */
public class WorkShiftDao {
    public void saveDishType(WorkShift w) {
        w.save();
    }

    public List<WorkShift> getAllOfflineWorkShift() {
        List<WorkShift> sectionList = new Select().from(WorkShift.class).queryList();
        return sectionList;
    }

    //获取尚未交班的记录 -- 只可能存在一个未交班记录
    public WorkShift getOpenWorkShift() {
        WorkShift workShift = new Select().from(WorkShift.class).where(WorkShift_Table.endTime.lessThan(1l)).querySingle();
        return workShift;
    }
}
