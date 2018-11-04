package cn.acewill.mobile.pos.common;

import java.util.ArrayList;
import java.util.List;

import cn.acewill.mobile.pos.model.table.Sections;
import cn.acewill.mobile.pos.model.table.TableData;
import cn.acewill.mobile.pos.model.table.TableInfor;
import cn.acewill.mobile.pos.model.table.TableStatus;


/**
 * Created by aqw on 2016/12/19.
 */
public class TableDataController {

    //桌台区域列表
    public static List<Sections> tableSections;

    //封装区域桌台列表数据，主要是为了实时更新桌台金额、创建时间、桌台状态
    public static List<TableInfor> getTableBySectionId(Long sectionId,List<TableData> result){
        List<TableInfor> allTableInfor = new ArrayList<>();

        for (Sections tableSection : getSectionList(sectionId)) {
                if(tableSection.getTableList()!=null&&tableSection.getTableList().size()>0){
                    for (TableInfor tableInfor : tableSection.getTableList()) {
                        if(result!=null&&result.size()>0){
                            for (TableData tableData : result) {
                                if(tableData==null) continue;
                                if(tableInfor.getId().equals(tableData.getId())){
                                    tableInfor.setMoney(tableData.getTotal());
                                    tableInfor.setCreated_time(tableData.getTime());
                                    tableInfor.setRealNumber(tableData.getNumber());
                                    tableInfor.setStatus(tableData.getStatus());

                                    allTableInfor.add(tableInfor);
                                    break;
                                }
                            }
                        }else {
                            allTableInfor.add(tableInfor);
                        }
                    }
                }
        }

        return allTableInfor;
    }

    //获取区域列表
    public static List<Sections> getSectionList(Long sectionId){
        List<Sections> sectionList = new ArrayList<>();

        if(sectionId==-1){//全部区域
            sectionList.addAll(tableSections);
        }else {//按区域id
            for (Sections tableSection : tableSections) {
                if(tableSection.getId().equals(sectionId)){
                    sectionList.add(tableSection);
                    break;
                }
            }
        }
        return sectionList;
    }

    //封装区域桌台列表数据，获取区域下所有空桌台
    public static List<TableInfor> getEmptyTables(Long sectionId, List<TableData> result){
        List<TableInfor> allTableInfor = new ArrayList<>();
        for (Sections tableSection : getSectionList(sectionId)) {
                if(tableSection.getTableList()!=null&&tableSection.getTableList().size()>0){
                    for (TableInfor tableInfor : tableSection.getTableList()) {
                        if(result!=null&&result.size()>0){
                            for (TableData tableData : result) {
                                if(tableData==null) continue;
                                if(tableInfor.getId().equals(tableData.getId()) && tableData.getStatus() == TableStatus.EMPTY){
                                    tableInfor.setMoney(tableData.getTotal());
                                    tableInfor.setCreated_time(tableData.getTime());
                                    tableInfor.setRealNumber(tableData.getNumber());
                                    tableInfor.setStatus(tableData.getStatus());

                                    allTableInfor.add(tableInfor);
                                    break;
                                }
                            }
                        }
                    }
                }
        }

        return allTableInfor;
    }

    //封装区域桌台列表数据，获取区域下除了指定桌台外的所有使用中的桌台
    public static List<TableInfor> getUseTables(Long sectionId,Long tableId,List<TableData> result){
        List<TableInfor> allTableInfor = new ArrayList<>();

        for (Sections tableSection : getSectionList(sectionId)) {
                if(tableSection.getTableList()!=null&&tableSection.getTableList().size()>0){
                    for (TableInfor tableInfor : tableSection.getTableList()) {
                        if(result!=null&&result.size()>0){
                            for (TableData tableData : result) {
                                if(tableData==null) continue;
                                if(tableInfor.getId().equals(tableData.getId()) && tableData.getStatus() == TableStatus.IN_USE && !tableId.equals(tableInfor.getId()) ){
                                    tableInfor.setMoney(tableData.getTotal());
                                    tableInfor.setCreated_time(tableData.getTime());
                                    tableInfor.setRealNumber(tableData.getNumber());
                                    tableInfor.setStatus(tableData.getStatus());

                                    allTableInfor.add(tableInfor);
                                    break;
                                }
                            }
                        }
                    }
                }
        }

        return allTableInfor;
    }

}
