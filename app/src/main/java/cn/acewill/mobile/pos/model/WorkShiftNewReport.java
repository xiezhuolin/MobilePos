package cn.acewill.mobile.pos.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by DHH on 2017/7/11.
 */

public class WorkShiftNewReport implements Serializable {
    private String personName;
    private String workShiftName;
    private String startTime;
    private String endTime;
    private float startWorkShiftCash;
    private float endWorkShiftCash;
    private float submitCash;
    private String differenceCash;
    private List<WorkShiftCategoryDataList> workShiftCategoryDataList;

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    public List<WorkShiftCategoryDataList> getWorkShiftCategoryDataList() {
        return workShiftCategoryDataList;
    }

    public void setWorkShiftCategoryDataList(List<WorkShiftCategoryDataList> workShiftCategoryDataList) {
        this.workShiftCategoryDataList = workShiftCategoryDataList;
    }

    public float getSubmitCash() {
        return submitCash;
    }

    public void setSubmitCash(float submitCash) {
        this.submitCash = submitCash;
    }

    public float getEndWorkShiftCash() {
        return endWorkShiftCash;
    }

    public void setEndWorkShiftCash(float endWorkShiftCash) {
        this.endWorkShiftCash = endWorkShiftCash;
    }

    public float getStartWorkShiftCash() {
        return startWorkShiftCash;
    }

    public void setStartWorkShiftCash(float startWorkShiftCash) {
        this.startWorkShiftCash = startWorkShiftCash;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getWorkShiftName() {
        return workShiftName;
    }

    public void setWorkShiftName(String workShiftName) {
        this.workShiftName = workShiftName;
    }

    public String getDifferenceCash() {
        return differenceCash;
    }

    public void setDifferenceCash(String differenceCash) {
        this.differenceCash = differenceCash;
    }

    public static class WorkShiftCategoryDataList implements Serializable{
        private String name;
        private List<WorkShiftItemDatas> workShiftItemDatas;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<WorkShiftItemDatas> getWorkShiftItemDatas() {
            return workShiftItemDatas;
        }

        public void setWorkShiftItemDatas(List<WorkShiftItemDatas> workShiftItemDatas) {
            this.workShiftItemDatas = workShiftItemDatas;
        }

        public static class WorkShiftItemDatas implements Serializable{
            private String name;
            private String itemCounts;
            private float total;

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public float getTotal() {
                return total;
            }

            public void setTotal(float total) {
                this.total = total;
            }

            public String getItemCounts() {
                return itemCounts;
            }

            public void setItemCounts(String itemCounts) {
                this.itemCounts = itemCounts;
            }
        }
    }
}
