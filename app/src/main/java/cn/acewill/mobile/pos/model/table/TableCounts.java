package cn.acewill.mobile.pos.model.table;

/**
 * Created by Acewill on 2016/6/2.
 */
public class TableCounts {
    private int occupiedCounts; //正在使用的桌台数目
    private int freeCounts; //空闲的桌台数目
    private int bookedCounts; //已经预定的桌台数目

    public int getOccupiedCounts() {
        return occupiedCounts;
    }

    public void setOccupiedCounts(int occupiedCounts) {
        this.occupiedCounts = occupiedCounts;
    }

    public int getFreeCounts() {
        return freeCounts;
    }

    public void setFreeCounts(int freeCounts) {
        this.freeCounts = freeCounts;
    }

    public int getBookedCounts() {
        return bookedCounts;
    }

    public void setBookedCounts(int bookedCounts) {
        this.bookedCounts = bookedCounts;
    }
}
