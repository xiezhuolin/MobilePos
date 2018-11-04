package cn.acewill.mobile.pos.model.dish;

import java.util.List;

/**
 * Created by DHH on 2016/7/19.
 */
public class KindData {
    private String appid;
    private int brandid;
    private int storeid;
    private int kindid;
    private String kindName;
    private String english;
    private int status;
    private int kindType;
    private String statusStr;
    /**
     * appid : 1dao2
     * brandid : 1
     * storeid : -1
     * dishid : 1
     * dishName : caipin1
     * sortMark : 16541
     * alias : null
     * english :
     * memberPrice : 11
     * price : 11
     * isPackage : 0
     * status : 1
     * imageName :
     * isTangShi : 1
     * isWaiDai : 1
     * isWeChat : 0
     * isWaiMai : 0
     * count : 11
     * unitid : 1
     * comment :
     * seq : 1
     * choiceArr : null
     * priceLevelObjectArr : null
     * dishKindStr : null
     * unitStr : null
     * tasteStr : null
     * cookStr : null
     * timeStr : null
     * timeDetailStr : null
     * tasteStrID : null
     * cookStrID : null
     * dishKindStrID : null
     * timeStrID : null
     * statusStr : null
     * isTangShiStr : null
     * isWaiDaiStr : null
     * isWeChatStr : null
     * isWaiMaiStr : null
     * isPackageStr : null
     */

    private List<DishArr> dishArr;

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public int getBrandid() {
        return brandid;
    }

    public void setBrandid(int brandid) {
        this.brandid = brandid;
    }

    public int getStoreid() {
        return storeid;
    }

    public void setStoreid(int storeid) {
        this.storeid = storeid;
    }

    public int getKindid() {
        return kindid;
    }

    public void setKindid(int kindid) {
        this.kindid = kindid;
    }

    public String getKindName() {
        return kindName;
    }

    public void setKindName(String kindName) {
        this.kindName = kindName;
    }

    public String getEnglish() {
        return english;
    }

    public void setEnglish(String english) {
        this.english = english;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getKindType() {
        return kindType;
    }

    public void setKindType(int kindType) {
        this.kindType = kindType;
    }

    public String getStatusStr() {
        return statusStr;
    }

    public void setStatusStr(String statusStr) {
        this.statusStr = statusStr;
    }

    public List<DishArr> getDishArr() {
        return dishArr;
    }

    public void setDishArr(List<DishArr> dishArr) {
        this.dishArr = dishArr;
    }

    public static class DishArr {
        private int dishid;
        private String dishName;
        private String sortMark;
        private String alias;
        private String english;
        private int memberPrice;
        private int price;
        /**
         * 0:单品  1:套餐
         */
        public int isPackage;
        private int status;
        private String imageName;
        private int isTangShi;
        private int isWaiDai;
        private int isWeChat;
        private int isWaiMai;
        private int count;
        private int unitid;
        private String comment;
        private int seq;

        /**
         * 套餐内容
         */
        private List<ChoiceArr> choiceArr;
        public List<ChoiceArr> getChoiceArr() {
            return choiceArr;
        }

        public void setChoiceArr(List<ChoiceArr> choiceArr) {
            this.choiceArr = choiceArr;
        }

        public static class ChoiceArr
        {
            public int min;
            public int isRepeatable;
            public int priceFactor;
            public String kindID;
            public String kindName;

            private List<Dish> dishIDStr;

            public List<Dish> getDishIDStr() {
                return dishIDStr;
            }

            public void setDishIDStr(List<Dish> dishIDStr) {
                this.dishIDStr = dishIDStr;
            }
        }

        public int getDishid() {
            return dishid;
        }

        public void setDishid(int dishid) {
            this.dishid = dishid;
        }

        public String getDishName() {
            return dishName;
        }

        public void setDishName(String dishName) {
            this.dishName = dishName;
        }

        public String getSortMark() {
            return sortMark;
        }

        public void setSortMark(String sortMark) {
            this.sortMark = sortMark;
        }

        public String getAlias() {
            return alias;
        }

        public void setAlias(String alias) {
            this.alias = alias;
        }

        public String getEnglish() {
            return english;
        }

        public void setEnglish(String english) {
            this.english = english;
        }

        public int getMemberPrice() {
            return memberPrice;
        }

        public void setMemberPrice(int memberPrice) {
            this.memberPrice = memberPrice;
        }

        public int getPrice() {
            return price;
        }

        public void setPrice(int price) {
            this.price = price;
        }

        public int getIsPackage() {
            return isPackage;
        }

        public void setIsPackage(int isPackage) {
            this.isPackage = isPackage;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getImageName() {
            return imageName;
        }

        public void setImageName(String imageName) {
            this.imageName = imageName;
        }

        public int getIsTangShi() {
            return isTangShi;
        }

        public void setIsTangShi(int isTangShi) {
            this.isTangShi = isTangShi;
        }

        public int getIsWaiDai() {
            return isWaiDai;
        }

        public void setIsWaiDai(int isWaiDai) {
            this.isWaiDai = isWaiDai;
        }

        public int getIsWeChat() {
            return isWeChat;
        }

        public void setIsWeChat(int isWeChat) {
            this.isWeChat = isWeChat;
        }

        public int getIsWaiMai() {
            return isWaiMai;
        }

        public void setIsWaiMai(int isWaiMai) {
            this.isWaiMai = isWaiMai;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public int getUnitid() {
            return unitid;
        }

        public void setUnitid(int unitid) {
            this.unitid = unitid;
        }

        public String getComment() {
            return comment;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }

        public int getSeq() {
            return seq;
        }

        public void setSeq(int seq) {
            this.seq = seq;
        }
    }
}
