package cn.acewill.mobile.pos.model.table;

import java.math.BigDecimal;

/**
 * Created by aqw on 2016/12/17.
 */
public class TableInfor {
    /**
     * id : 1933
     * appId : jwcj1
     * brandId : 49
     * storeId : 1
     * sectionId : 1055
     * serialNumber : 1
     * name : 1
     * capacity : 4
     * realNumber : 1
     * temStu : null
     * uiPropertiesStr : {"x":6,"y":6,"width":56,"height":56,"type":"circle","screen_width":0,"screen_height":0}
     * status : IN_USE
     * bookingStatus : NOT_BOOKING
     * uiProperties : {"x":6,"y":6,"width":56,"height":56,"type":"circle","screen_width":0,"screen_height":0}
     * money : null
     * created_time : null
     * book : null
     */

    private Long id;
    private String appId;
    private String brandId;
    private String storeId;
    private String sectionId;
    private String serialNumber;
    private String name;
    private int capacity;
    private int realNumber;
    private String temStu;
    private String uiPropertiesStr;
    private TableStatus status;
    private BookingStatus bookingStatus;
    private UiPropertiesBean uiProperties;
    private BigDecimal money;
    private String created_time;
    private Object book;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getBrandId() {
        return brandId;
    }

    public void setBrandId(String brandId) {
        this.brandId = brandId;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public String getSectionId() {
        return sectionId;
    }

    public void setSectionId(String sectionId) {
        this.sectionId = sectionId;
    }

    public void setMoney(BigDecimal money) {
        this.money = money;
    }

    public BigDecimal getMoney() {
        return money;
    }

    public String getCreated_time() {
        return created_time;
    }

    public void setCreated_time(String created_time) {
        this.created_time = created_time;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getRealNumber() {
        return realNumber;
    }

    public void setRealNumber(int realNumber) {
        this.realNumber = realNumber;
    }

    public String getTemStu() {
        return temStu;
    }

    public void setTemStu(String temStu) {
        this.temStu = temStu;
    }

    public String getUiPropertiesStr() {
        return uiPropertiesStr;
    }

    public void setUiPropertiesStr(String uiPropertiesStr) {
        this.uiPropertiesStr = uiPropertiesStr;
    }

    public TableStatus getStatus() {
        return status;
    }

    public void setStatus(TableStatus status) {
        this.status = status;
    }

    public BookingStatus getBookingStatus() {
        return bookingStatus;
    }

    public void setBookingStatus(BookingStatus bookingStatus) {
        this.bookingStatus = bookingStatus;
    }

    public UiPropertiesBean getUiProperties() {
        return uiProperties;
    }

    public void setUiProperties(UiPropertiesBean uiProperties) {
        this.uiProperties = uiProperties;
    }


    public Object getBook() {
        return book;
    }

    public void setBook(Object book) {
        this.book = book;
    }

    public static class UiPropertiesBean {
        /**
         * x : 6
         * y : 6
         * width : 56
         * height : 56
         * type : circle
         * screen_width : 0
         * screen_height : 0
         */

        private int x;
        private int y;
        private int width;
        private int height;
        private TableShap type;
        private int screen_width;
        private int screen_height;

        public int getX() {
            return x;
        }

        public void setX(int x) {
            this.x = x;
        }

        public int getY() {
            return y;
        }

        public void setY(int y) {
            this.y = y;
        }

        public int getWidth() {
            return width;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }

        public TableShap getType() {
            return type;
        }

        public void setType(TableShap type) {
            this.type = type;
        }

        public int getScreen_width() {
            return screen_width;
        }

        public void setScreen_width(int screen_width) {
            this.screen_width = screen_width;
        }

        public int getScreen_height() {
            return screen_height;
        }

        public void setScreen_height(int screen_height) {
            this.screen_height = screen_height;
        }
    }
}
