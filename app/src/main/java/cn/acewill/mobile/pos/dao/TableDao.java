package cn.acewill.mobile.pos.dao;

import com.raizlabs.android.dbflow.sql.language.Select;

import java.util.List;

import cn.acewill.mobile.pos.model.table.Section;
import cn.acewill.mobile.pos.model.table.Section_Table;
import cn.acewill.mobile.pos.model.table.Table;
import cn.acewill.mobile.pos.model.table.Table_Table;


/**
 * Created by Acewill on 2016/6/1.
 *
 */
public class TableDao {
    private static final  String TAG = "TableDao";


    public void saveTable(Table table) {
           table.save();
    }

    public List<Section> getAllSections() {
        List<Section> sectionList = new Select().from(Section.class).queryList();

        return sectionList;
    }

    public Section getSection(long sectionId) {
        Section section =  new Select().from(Section.class).where(Section_Table.id.eq(sectionId)).querySingle();

        //查询该区域的所有桌台

        return section;
    }

    public List<Table> getAllTables() {
        List<Table> tableList = new Select().from(Table.class).queryList();

        return tableList;
    }


    //获取某个区域的所有桌台
    public List<Table> getAllTablesBySection(long sectionId) {
        List<Table> tableList = new Select().from(Table.class).where(Table_Table.sectionId.eq(sectionId)).queryList();

        return tableList;
    }
}
