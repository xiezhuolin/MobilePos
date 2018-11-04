package cn.acewill.mobile.pos.common;

import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import android.util.SparseArray;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import cn.acewill.mobile.pos.model.KitchenStall;
import cn.acewill.mobile.pos.model.dish.Cart;
import cn.acewill.mobile.pos.model.dish.Dish;
import cn.acewill.mobile.pos.model.dish.DishDiscount;
import cn.acewill.mobile.pos.model.dish.DishTime;
import cn.acewill.mobile.pos.model.dish.DishType;
import cn.acewill.mobile.pos.model.dish.Menu;
import cn.acewill.mobile.pos.model.dish.Unit;
import cn.acewill.mobile.pos.model.order.Order;
import cn.acewill.mobile.pos.model.order.OrderDiscount;
import cn.acewill.mobile.pos.model.order.OrderItem;
import cn.acewill.mobile.pos.printer.Printer;
import cn.acewill.mobile.pos.printer.PrinterOutputType;
import cn.acewill.mobile.pos.service.PosInfo;
import cn.acewill.mobile.pos.utils.Constant;
import cn.acewill.mobile.pos.utils.DishCountUtil;
import cn.acewill.mobile.pos.utils.PinyinUtils;
import cn.acewill.mobile.pos.utils.TimeUtil;
import cn.acewill.mobile.pos.utils.ToolsUtils;

import static cn.acewill.mobile.pos.common.PrinterDataController.kitchenStallDishMap;
import static cn.acewill.mobile.pos.common.PrinterDataController.printerMap;


public class DishDataController {
    /**
     * -1:全部;0:堂食;1:外带;2:外卖
     */
    public static final int DISH_TYPE = -1;
    /**
     * 分类列表
     */
    public static List<DishType> dishKindList;
    /**
     * 菜品单位列表
     */
    public static List<Unit> dishUnitList;
    /**
     * 菜品时段列表
     */
    public static List<DishTime> dishTimeList;
    /**
     * 菜品打折方案列表
     */
    public static ArrayMap<Integer, ArrayList<DishDiscount>> dishDiscountList = new ArrayMap<Integer, ArrayList<DishDiscount>>();
    public static List<Menu> menuData;
    public static List<Menu> menuAllData;
    public static ArrayList<OrderDiscount> markets;
    public static List<OrderItem> orderItemList = new CopyOnWriteArrayList<OrderItem>();
    public static ArrayMap<Long, Integer> tablePeopleNumMap = new ArrayMap<>();

    public static void cleanDishDate() {
        dishKindList = null;
        dishUnitList = null;
        dishTimeList = null;
        menuData = null;
        menuAllData = null;
        markets = null;
        orderItemList = null;
        searchDishs = null;
        aliasList = null;
        dishDiscountList.clear();
        dishId2DishMap.clear();
    }

    public static float getDiscountPrice(float totalPrice) {
        if (markets != null && markets.size() > 0) {
            for (OrderDiscount item : markets) {
                if (TimeUtil.isDiscountTime(item.discountTime)) {
                    return item.getDiscountPrice(totalPrice);
                }
            }
        }
        return totalPrice;
    }

    public static Menu getDishs(int dishType) {
        if (menuData != null && menuData.size() > 0) {
            for (Menu item : menuData) {
                Menu menuData = item.getDishData(dishType);
                if (menuData != null) {
                    return menuData;
                }
            }
        }
        return null;
    }

    public static Menu getAllDishs(int dishType) {
        if (menuAllData != null && menuAllData.size() > 0) {
            for (Menu item : menuAllData) {
                Menu menuData = item.getDishData(dishType);
                if (menuData != null) {
                    return menuData;
                }
            }
        }
        return null;
    }

    public static int getKindCount() {
        return dishKindList.size();
    }

    public static List<DishType> getDishKindList() {
        return dishKindList;
    }

    public static void setDishKindList(List<DishType> dishKindList) {
        DishDataController.dishKindList = dishKindList;
    }

    private static List<Dish> searchDishs = new ArrayList<>();

    public static List<Dish> searchDish(String searchSth) {
        searchDishs = new ArrayList<Dish>();
        for (Menu menu : menuData) {
            for (Dish dish : menu.dishData) {
                if (dish.getAlias().indexOf(searchSth) != -1) {
                    addDish(dish);
                }
            }
        }
        return searchDishs;
    }

    public static List<Dish> searchAllDish(String searchSth) {
        searchDishs = new ArrayList<Dish>();
        for (Menu menu : menuAllData) {
            for (Dish dish : menu.dishData) {
                if (dish.getAlias().indexOf(searchSth) != -1) {
                    addDish(dish);
                }
            }
        }
        return searchDishs;
    }

    private static void addDish(Dish dish) {
        int size = searchDishs.size();
        if (size == 0) {
            searchDishs.add(dish);
        } else {
            boolean isHave = false;
            for (int i = 0; i < size; i++) {
                if (searchDishs.get(i).getDishId() == dish.getDishId()) {
                    isHave = true;
                    break;
                }
            }
            if (isHave == false) {
                searchDishs.add(dish);
            }
        }
    }


    public static List<Dish> sortMarkDish(String searchSth) {
        searchDishs = new ArrayList<Dish>();
        for (Menu menu : menuData) {
            for (Dish dish : menu.dishData) {
                if (!TextUtils.isEmpty(dish.getSortMark()) && dish.getSortMark().indexOf(searchSth) != -1) {
                    addDish(dish);
                }
            }
        }
        return searchDishs;
    }

    public static List<Dish> sortAllMarkDish(String searchSth) {
        searchDishs = new ArrayList<Dish>();
        for (Menu menu : menuAllData) {
            for (Dish dish : menu.dishData) {
                if (!TextUtils.isEmpty(dish.getSortMark()) && dish.getSortMark().indexOf(searchSth) != -1) {
                    addDish(dish);
                }
            }
        }
        return searchDishs;
    }

    private static ArrayList<String> aliasList = new ArrayList<>();

    public static void setDishData(List<Menu> menuData) {
        DishDataController.menuData = menuData;
        for (Menu menu : menuData) {
            menu.dishMap = new ArrayMap<String, ArrayList<Dish>>();
            for (Dish dish : menu.dishData) {
                String dishAlias = PinyinUtils.getFirstSpell(dish.getDishName());
                dish.setAlias(dishAlias);
                ArrayList<Dish> arrayList = menu.dishMap.get(dish.dishKind);
                if (arrayList == null) {
                    arrayList = new ArrayList<Dish>();
                    menu.dishMap.put(dish.dishKind, arrayList);
                }
                dishDiscountList.put(dish.getDishId(), dish.dishDiscount);
                DishCountUtil.dishItemList.add(dish);
                arrayList.add(dish);
            }
        }
    }

    public static void setDishAllData(List<Menu> menuData) {
        DishDataController.menuAllData = menuData;
        for (Menu menu : menuData) {
            menu.dishMap = new ArrayMap<String, ArrayList<Dish>>();
            for (Dish dish : menu.dishData) {
                String dishAlias = PinyinUtils.getFirstSpell(dish.getDishName());
                dish.setAlias(dishAlias);
                ArrayList<Dish> arrayList = menu.dishMap.get(dish.dishKind);
                if (arrayList == null) {
                    arrayList = new ArrayList<Dish>();
                    menu.dishMap.put(dish.dishKind, arrayList);
                }
//                dishDiscountList.put(dish.getDishId(), dish.dishDiscount);
//                DishCountUtil.dishItemList.add(dish);
                arrayList.add(dish);
            }
        }
    }

    /**
     * @param position 菜品位置
     * @return 如果是该分类的首项，则返回首项的分类名称，否则返回空
     */
    public static String getKindFirstItemName(int position) {
        // int index = kindFirstPostions.indexOf(Integer.valueOf(position));
        // if (index > -1) {
        // return dishKindList.get(index).kindName;
        // }
        return "";
    }

    /**
     * @param position 菜品位置
     * @return 分类位置
     */
    public static int getSectionForPosition(int position) {

        return 0;
    }

    /**
     * @param section 分类位置
     * @return 该分类的菜品在dishList中的首项
     */
    public static int getPositionForSection(int section) {
        return 0;
    }

    public static String getKindName(int section) {
        return dishKindList.get(section).getName();
    }

    public static Dish getDish(int dishId, int dishType) {
        List<Dish> dishs = getDishs(dishType).dishData;
        int size = dishs.size();
        for (int i = 0; i < size; i++) {
            if (dishs.get(i).getDishId() == (dishId)) {
                return dishs.get(i);
            }
        }
        return null;
    }

    /**
     * @param kindId
     * @param position 分类中的脚标
     * @return
     */
    public static Dish getDishForKind(String kindId, int position) {
        ArrayList<Dish> dishs = getDishs(DISH_TYPE).dishMap.get(kindId);
        if (dishs != null) {
            return dishs.get(position);
        }
        return null;
    }

    public static String kindName(int position) {
        return dishKindList.get(position).getName();
    }

    public static ArrayList<Dish> getdishsForKind(String kindId) {
        if (getDishs(DISH_TYPE) == null) {
            return null;
        }
        if (getDishs(DISH_TYPE).dishMap == null || getDishs(DISH_TYPE).dishMap.size() <= 0) {
            return null;
        }
        return getDishs(DISH_TYPE).dishMap.get(kindId);
    }

    public static List<Dish> getdishsForKind(int position) {
        if (getDishs(DISH_TYPE) == null) {
            return null;
        }
        if (getDishs(DISH_TYPE).dishMap == null || getDishs(DISH_TYPE).dishMap.size() <= 0) {
            return null;
        }
        if(dishKindList == null || dishKindList.size() <= 0)
        {
            return null;
        }
        if(TextUtils.isEmpty(dishKindList.get(position).getId()+""))
        {
            return null;
        }
        return getDishs(DISH_TYPE).dishMap.get(String.valueOf(dishKindList.get(position).getId()));
    }

    public static List<Dish> getAlldishsForKind(int position) {
        if (getAllDishs(DISH_TYPE) == null) {
            return null;
        }
        if (getAllDishs(DISH_TYPE).dishMap == null || getAllDishs(DISH_TYPE).dishMap.size() <= 0) {
            return null;
        }
        if(dishKindList == null || dishKindList.size() <= 0)
        {
            return null;
        }
        if(TextUtils.isEmpty(dishKindList.get(position).getId()+""))
        {
            return null;
        }
        return getAllDishs(DISH_TYPE).dishMap.get(String.valueOf(dishKindList.get(position).getId()));
    }

    public static String getKindNameById(String id) {
        String name = "";
        if (dishKindList != null && dishKindList.size() > 0) {
            for (DishType dishType : dishKindList) {
                if (String.valueOf(dishType.getId()).equals(id)) {
                    name = dishType.getName();
                    break;
                }
            }
        }
        return name;
    }

    public static SparseArray<Integer> dishMarkMap = new SparseArray<>();//已点分类菜品项目份数

    public static SparseArray<Integer> getDishMarkMap() {
        synchronized (dishMarkMap)
        {
            return dishMarkMap;
        }
    }

    public static void addDishMark(String dishKind, Dish dish) {
        PosInfo posInfo = PosInfo.getInstance();
        posInfo.setClickDish(false);
        Integer kindDishQuantity = dishMarkMap.get(Integer.valueOf(dishKind));
        if (kindDishQuantity == null || kindDishQuantity < 0) {
            kindDishQuantity = 0;
        }
        synchronized (dishMarkMap)
        {
            int count = (int) kindDishQuantity + dish.getQuantity() - dish.getTempQuantity();
            dishMarkMap.put(Integer.valueOf(dishKind), count);
            Cart.notifyContentChange();
        }
    }

    public static void reduceDishMark(String dishKind, Dish dish) {
        PosInfo posInfo = PosInfo.getInstance();
        posInfo.setClickDish(false);
        int kindDishQuantity = dishMarkMap.get(Integer.valueOf(dishKind));
        int quantity = 0;
        if (dish.quantity != 0) {
            quantity = dish.getQuantity();
        }
        synchronized (dishMarkMap)
        {
            dishMarkMap.put(Integer.valueOf(dishKind), quantity + kindDishQuantity - dish.getTempQuantity());
            Cart.notifyContentChange();
        }
        ToolsUtils.writeUserOperationRecords("更新了(" + dish.getDishName() + ")菜品的状态" + "==>>" + ToolsUtils.getPrinterSth(dish));
    }

    public static void removeDishMark(String dishKind, Dish dish) {
        PosInfo posInfo = PosInfo.getInstance();
        posInfo.setClickDish(false);
        int kindDishQuantity = dishMarkMap.get(Integer.valueOf(dishKind));
        synchronized (dishMarkMap)
        {
            dishMarkMap.put(Integer.valueOf(dishKind), kindDishQuantity - dish.getQuantity());
            Cart.notifyContentChange();
        }
    }

    public static void cleanDishMarkMap() {
        PosInfo posInfo = PosInfo.getInstance();
        posInfo.setClickDish(true);
        if (dishMarkMap != null) {
            synchronized (dishMarkMap)
            {
                dishMarkMap.clear();
            }
        }
    }

    //给菜品项设置分类id
    public static void handleOrder(final Order order) {
        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        Menu m = getDishs(-1);
                        if (m == null) {
                            return;
                        }
                        List<Dish> dishs = m.getDishData();
                        if (dishs == null) {
                            return;
                        }
                        for (OrderItem orderItem : order.getItemList()) {
                            if (orderItem.getSubItemList() != null && orderItem.getSubItemList().size() > 0) {//套餐
                                List<Dish.Package> subDishs = orderItem.getSubItemList();
                                for (Dish dish : dishs) {
                                    if ((int) orderItem.getDishId() == dish.getDishId()) {//套餐主项
                                        orderItem.setDishKind(dish.dishKind);
                                        orderItem.setDishKindStr(dish.getDishKindStr());
                                    }
                                    for (Dish.Package subDish : subDishs) {//套餐子项
                                        if (subDish.getDishId() == dish.getDishId()) {
                                            subDish.setDishKindStr(dish.getDishKindStr());
                                            subDish.dishKind = dish.dishKind;
                                        }
                                    }

                                }
                            } else {//普通菜品
                                for (Dish dish : dishs) {
                                    if ((int) orderItem.getDishId() == dish.getDishId()) {
                                        orderItem.setDishKind(dish.dishKind);
                                        orderItem.setDishKindStr(dish.getDishKindStr());
                                        break;
                                    }
                                }
                            }
                        }

                    }
                }
        ).start();
    }

    public static ArrayMap<Long, Dish> dishId2DishMap = new ArrayMap<>();

    public static ArrayMap<Long, Dish> getDishIdforDishMap(List<Dish> dishs) {
        if (dishs != null && dishs.size() > 0) {
            for (Dish dish : dishs) {
                dishId2DishMap.put(Long.valueOf(dish.getDishId()), dish);
            }
        }
        return dishId2DishMap;
    }

    public static Order initOrder2(Order o) {
        if (getDishs(DISH_TYPE) != null) {
            List<Dish> dishs = getDishs(DISH_TYPE).getDishData();
            if (dishs == null) {
                return o;
            }
            if (dishId2DishMap == null || dishId2DishMap.size() <= 0) {
                dishId2DishMap = getDishIdforDishMap(dishs);
            }
            List<OrderItem> copyOrderItemList = new CopyOnWriteArrayList<>();
            if (o.getItemList() != null && o.getItemList().size() > 0) {
                //全新的订单菜品list
                int dishItemLaberCount = 0;
                for (OrderItem orderItem : o.getItemList()) {
                    //套餐
                    if (orderItem.getSubItemList() != null && orderItem.getSubItemList().size() > 0) {
                        List<Dish.Package> subDishs = orderItem.getSubItemList();
                        int size = subDishs.size();
                        for (int i = 0; i < size; i++) {
                            OrderItem orderItem1 = new OrderItem(subDishs.get(i));
                            Dish tempDish = dishId2DishMap.get(orderItem1.getDishId());
                            if (tempDish != null) {
                                orderItem1.setId(TimeUtil.getOrderItemId(i));
                                orderItem1.setRejectedQuantity(orderItem.getRejectedQuantity() * orderItem1.getQuantity());
                                orderItem1.setQuantity(orderItem.quantity * orderItem1.quantity);
                                if(logicDishIsLabelPrint(Long.valueOf(tempDish.getDishId()+"")))
                                {
                                    dishItemLaberCount += orderItem1.getQuantity();
                                }
                                orderItem1.setItemPrice(orderItem.getItemPrice());
                                if (ToolsUtils.isPrintPackName()) {
                                    orderItem1.setPackName(orderItem.getDishName());
                                    orderItem1.setTempPackName(orderItem.getDishName());
                                }
                                orderItem1.setDishName("[套]" + tempDish.getDishName());
                                orderItem1.setDishKind(tempDish.getDishKind());
                                //                                //如果是全单退菜模式 退菜份数等于菜品选择的份数
                                //                                if (o.getTableStyle() == Constant.EventState.PRINTER_RETREAT_DISH) {
                                //                                    orderItem1.setRejectedQuantity(orderItem.getQuantity() * orderItem1.getQuantity());
                                //                                }
                                orderItem1.setKitDishMoney(new BigDecimal(orderItem.quantity).multiply(orderItem.getPrice()));
                                orderItem1.setPackageItem(true);
                                copyOrderItemList.add(orderItem1);
                            }
                        }
                    }
                    //普通菜品
                    else {
                        //如果是全单退菜模式 退菜份数等于菜品选择的份数
                        if (o.getTableStyle() == Constant.EventState.PRINTER_RETREAT_DISH) {
                            orderItem.setRejectedQuantity(orderItem.getQuantity());
                        }
                        if(logicDishIsLabelPrint(orderItem.getDishId()))
                        {
                            dishItemLaberCount += orderItem.getQuantity();
                        }
                        orderItem.setKitDishMoney(new BigDecimal(orderItem.quantity).multiply(orderItem.getPrice()));
                        orderItem.setPackageItem(false);
                        copyOrderItemList.add(orderItem);
                    }
                }
                o.setLaberDishCount(dishItemLaberCount);
                o.setItemList(copyOrderItemList);
            }
        }
        return o;
    }


    /**
     * 判断当前菜品是否用通过标签打印机打印的
     * @param dishId
     * @return
     */
    private static boolean  logicDishIsLabelPrint(Long dishId)
    {
        if (kitchenStallDishMap != null && kitchenStallDishMap.size() > 0) {
            List<KitchenStall> kitchenStallList = kitchenStallDishMap.get(dishId);
            if (kitchenStallList != null && kitchenStallList.size() > 0) {
                int kitchenSize = kitchenStallList.size();
                for (int j = 0; j < kitchenSize; j++) {
                    KitchenStall kitchenStall = kitchenStallList.get(j);
                    if (kitchenStall != null && kitchenStall.getDishReceiptCounts() != null && kitchenStall.getDishReceiptCounts() > 0) {
                        //判断门店配置的打印机打印菜品
                        if (printerMap != null && printerMap.size() > 0) {
                            Printer printer = printerMap.get(kitchenStall.getPrinterid());
                            if (printer != null && printer.getOutputType() == PrinterOutputType.LABEL) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }


    public static ArrayList<DishDiscount> getDishDiscount(int dishId) {
        ArrayList<DishDiscount> dishDiscounts = dishDiscountList.get(dishId);
        if (dishDiscounts != null) {
            return dishDiscounts;
        }
        return null;
    }

}
