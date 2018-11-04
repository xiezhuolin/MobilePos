package cn.acewill.mobile.pos.dao.cache;


import com.raizlabs.android.dbflow.sql.language.Delete;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.util.ArrayList;
import java.util.List;

import cn.acewill.mobile.pos.model.Definition;
import cn.acewill.mobile.pos.model.KDS;
import cn.acewill.mobile.pos.model.KitchenStall;
import cn.acewill.mobile.pos.model.StoreBusinessInformation;
import cn.acewill.mobile.pos.model.StoreConfiguration;
import cn.acewill.mobile.pos.model.TerminalInfo;
import cn.acewill.mobile.pos.model.WorkShift;
import cn.acewill.mobile.pos.model.cache.CachedAllMenu;
import cn.acewill.mobile.pos.model.cache.CachedBindTerminalInfo;
import cn.acewill.mobile.pos.model.cache.CachedCallNumber;
import cn.acewill.mobile.pos.model.cache.CachedCallNumber_Table;
import cn.acewill.mobile.pos.model.cache.CachedDishCount;
import cn.acewill.mobile.pos.model.cache.CachedDishType;
import cn.acewill.mobile.pos.model.cache.CachedKdsList;
import cn.acewill.mobile.pos.model.cache.CachedKitchenStallList;
import cn.acewill.mobile.pos.model.cache.CachedMenu;
import cn.acewill.mobile.pos.model.cache.CachedOrder;
import cn.acewill.mobile.pos.model.cache.CachedOrderNumber;
import cn.acewill.mobile.pos.model.cache.CachedOrderNumber_Table;
import cn.acewill.mobile.pos.model.cache.CachedOrder_Table;
import cn.acewill.mobile.pos.model.cache.CachedPaymentType;
import cn.acewill.mobile.pos.model.cache.CachedPrinterList;
import cn.acewill.mobile.pos.model.cache.CachedPrinterTemplatesList;
import cn.acewill.mobile.pos.model.cache.CachedScreen;
import cn.acewill.mobile.pos.model.cache.CachedStoreBusinessInformation;
import cn.acewill.mobile.pos.model.cache.CachedStoreConfiguration;
import cn.acewill.mobile.pos.model.cache.CachedTerminalInfo;
import cn.acewill.mobile.pos.model.cache.CachedUser;
import cn.acewill.mobile.pos.model.cache.CachedUser_Table;
import cn.acewill.mobile.pos.model.cache.CachedWorkshift;
import cn.acewill.mobile.pos.model.cache.CachedWorkshiftDefinition;
import cn.acewill.mobile.pos.model.dish.DishCount;
import cn.acewill.mobile.pos.model.dish.DishType;
import cn.acewill.mobile.pos.model.dish.Menu;
import cn.acewill.mobile.pos.model.order.Order;
import cn.acewill.mobile.pos.model.payment.Payment;
import cn.acewill.mobile.pos.model.user.User;
import cn.acewill.mobile.pos.printer.Printer;
import cn.acewill.mobile.pos.printer.PrinterTemplates;
import cn.acewill.mobile.pos.service.UploadOrderThread;
import cn.acewill.mobile.pos.service.retrofit.response.ScreenResponse;
import cn.acewill.mobile.pos.utils.ToolsUtils;


/**
 * Created by Acewill on 2016/12/1.
 */

public class CachedDao {
    private static final String TAG = "DishDao";

    //把一个菜品对象保存到缓存中
    public void saveDishType(List<DishType> dishList) {
        Delete.table(CachedDishType.class);
        List<CachedDishType> cachedDishList = new Select().from(CachedDishType.class).queryList();
        for (DishType dish : dishList) {
            new CachedDishType(dish).save();
        }
    }

    public List<DishType> getAllDish() {
        List<CachedDishType> cachedDishList = new Select().from(CachedDishType.class).queryList();
        List<DishType> dishList = new ArrayList<>();
        for (CachedDishType cd : cachedDishList) {
            dishList.add(cd.toDish());
        }

        return dishList;
    }

    //把一个菜品对象保存到缓存中
    public void saveMenu(List<Menu> menuList) {
        //先清空
        Delete.table(CachedMenu.class);
        for (Menu menu : menuList) {
            new CachedMenu(menu).save();
        }
    }

    public List<Menu> getAllMenu() {
        List<CachedMenu> cachedDishList = new Select().from(CachedMenu.class).queryList();
        List<Menu> dishList = new ArrayList<>();
        for (CachedMenu cd : cachedDishList) {
            Menu menu = cd.toMenu();
            dishList.add(menu);
        }

        return dishList;
    }

    //把一个菜品对象保存到缓存中
    public void saveAllMenu(List<Menu> menuList) {
        //先清空
        Delete.table(CachedAllMenu.class);
        for (Menu menu : menuList) {
            new CachedAllMenu(menu).save();
        }
    }

    public List<Menu> getAllDishMenu() {
        List<CachedAllMenu> cachedDishList = new Select().from(CachedAllMenu.class).queryList();
        List<Menu> dishList = new ArrayList<>();
        for (CachedAllMenu cd : cachedDishList) {
            Menu menu = cd.toMenu();
            dishList.add(menu);
        }
        return dishList;
    }

    public void savePaymentType(List<Payment> menuList) {
        //先清空
        Delete.table(CachedPaymentType.class);
        for (Payment menu : menuList) {
            new CachedPaymentType(menu).save();
        }
    }

    public List<Payment> getAllPaymentType() {
        List<CachedPaymentType> cachedDishList = new Select().from(CachedPaymentType.class).queryList();
        List<Payment> dishList = new ArrayList<>();
        for (CachedPaymentType cd : cachedDishList) {
            Payment menu = cd.toPaymentType();
            dishList.add(menu);
        }
        return dishList;
    }


    public void savePrinterList(List<Printer> menuList) {
        //先清空
        Delete.table(CachedPrinterList.class);
        for (Printer menu : menuList) {
            new CachedPrinterList(menu).save();
        }
    }

    public List<Printer> getAllPrinter() {
        List<CachedPrinterList> cachedDishList = new Select().from(CachedPrinterList.class).queryList();
        List<Printer> dishList = new ArrayList<>();
        for (CachedPrinterList cd : cachedDishList) {
            Printer menu = cd.toPrinter();
            dishList.add(menu);
        }
        return dishList;
    }

    public void saveKDSList(List<KDS> menuList) {
        //先清空
        Delete.table(CachedKdsList.class);
        for (KDS menu : menuList) {
            new CachedKdsList(menu).save();
        }
    }

    public List<KDS> getAllKDS() {
        List<CachedKdsList> cachedDishList = new Select().from(CachedKdsList.class).queryList();
        List<KDS> dishList = new ArrayList<>();
        for (CachedKdsList cd : cachedDishList) {
            KDS menu = cd.toKDS();
            dishList.add(menu);
        }
        return dishList;
    }

    public List<KitchenStall> getAllKitchenStall() {
        List<CachedKitchenStallList> cachedDishList = new Select().from(CachedKitchenStallList.class).queryList();
        List<KitchenStall> dishList = new ArrayList<>();
        for (CachedKitchenStallList cd : cachedDishList) {
            KitchenStall menu = cd.toKitchenStall();
            dishList.add(menu);
        }
        return dishList;
    }

    public void saveKitchenStallList(List<KitchenStall> menuList) {
        //先清空
        Delete.table(CachedKitchenStallList.class);
        for (KitchenStall menu : menuList) {
            new CachedKitchenStallList(menu).save();
        }
    }


    public void savePrinterTemplatesList(List<PrinterTemplates> menuList) {
        //先清空
        Delete.table(CachedPrinterTemplatesList.class);
        for (PrinterTemplates menu : menuList) {
            new CachedPrinterTemplatesList(menu).save();
        }
    }

    public List<PrinterTemplates> getAllPrinterTemplates() {
        List<CachedPrinterTemplatesList> cachedDishList = new Select().from(CachedPrinterTemplatesList.class).queryList();
        List<PrinterTemplates> dishList = new ArrayList<>();
        for (CachedPrinterTemplatesList cd : cachedDishList) {
            PrinterTemplates menu = cd.toPrinterTemplates();
            dishList.add(menu);
        }
        return dishList;
    }


    public void saveOrder(Order order) {
        //注意，本地缓存的订单只在上传服务器后才会从本地删除
        new CachedOrder(order).save();
        //通知上传线程有新订单
        UploadOrderThread.notifyOrderCreated();
    }

    public Order findOrderById(long id) {
        //注意，本地缓存的订单只在上传服务器后才会从本地删除

        CachedOrder order = new Select().from(CachedOrder.class).where(CachedOrder_Table.id.eq(id)).querySingle();
        if (order != null) {
            return order.toOrder();
        }
        return null;
    }

    public void deleteOrder(Order order) {
        new CachedOrder(order).delete();
    }

    public List<Order> getAllOrders() {
        List<CachedOrder> cachedDishList = new Select().from(CachedOrder.class).queryList();
        List<Order> dishList = new ArrayList<>();
        for (CachedOrder cd : cachedDishList) {
            Order menu = cd.toOrder();
            dishList.add(menu);
        }
        return dishList;
    }

    public List<User> getAllUser() {
        List<CachedUser> cachedDishList = new Select().from(CachedUser.class).queryList();
        List<User> dishList = new ArrayList<>();
        for (CachedUser cd : cachedDishList) {
            User menu = cd.toUser();
            dishList.add(menu);
        }
        return dishList;
    }


    public User findUserByName(String name) {
        CachedUser oldCachedUser = new Select().from(CachedUser.class).where(CachedUser_Table.name.eq(name)).querySingle();
        if (oldCachedUser != null) {
            return oldCachedUser.toUser();
        }
        return null;
    }

    public void saveUser(User user) {
        //先找到本地的同名的用户
        CachedUser oldCachedUser = new Select().from(CachedUser.class).where(CachedUser_Table.name.eq(user.getUserRet().getUsername())).querySingle();
        if (oldCachedUser != null) {
            User oldUser = oldCachedUser.toUser();
            oldUser.setPassword(user.getPassword());
            CachedUser cacheUser = new CachedUser(oldUser);
            cacheUser.setId(oldCachedUser.getId());
            cacheUser.update();
        } else {
            new CachedUser(user).insert();
        }
    }

    public void saveWorkshift(WorkShift workShift) {
        new CachedWorkshift(workShift).save();
    }

    public List<WorkShift> getAllWorkshift() {
        List<CachedWorkshift> cachedDishList = new Select().from(CachedWorkshift.class).queryList();
        List<WorkShift> dishList = new ArrayList<>();
        for (CachedWorkshift cd : cachedDishList) {
            WorkShift menu = cd.toWorkShift();
            dishList.add(menu);
        }

        return dishList;
    }

    public long getNextOrderId()
    {
        CachedOrderNumber cachedOrderNumber = new Select().from(CachedOrderNumber.class).where(CachedOrderNumber_Table.id.eq(1)).querySingle();
        if (cachedOrderNumber == null) {
            cachedOrderNumber = new CachedOrderNumber();
            cachedOrderNumber.setId(1);
            cachedOrderNumber.setOrderId(1);
            cachedOrderNumber.insert();
            return 1;
        }
        else{
            long currentOrderNumber = cachedOrderNumber.getOrderId();
            if (currentOrderNumber < 200) {
                cachedOrderNumber.setOrderId(currentOrderNumber + 1);
            } else {
                cachedOrderNumber.setOrderId(1);
            }
            cachedOrderNumber.update();
            return cachedOrderNumber.getOrderId();
        }
    }


    public int getNextCallNumber() {
        CachedCallNumber cachedCallNumber = new Select().from(CachedCallNumber.class).where(CachedCallNumber_Table.id.eq(1)).querySingle();
        if (cachedCallNumber == null) {
            cachedCallNumber = new CachedCallNumber();
            cachedCallNumber.setId(1l);
            cachedCallNumber.setNumber(1);
            cachedCallNumber.insert();
            return 1;
        } else {
            //取餐号可以重复使用，超过100从1开始
            int currentCallNumber = cachedCallNumber.getNumber();
            if (currentCallNumber < 100) {
                cachedCallNumber.setNumber(currentCallNumber + 1);
            } else {
                cachedCallNumber.setNumber(1);
            }
            cachedCallNumber.update();
            return cachedCallNumber.getNumber();
        }
    }

    //把一个菜品对象保存到缓存中
    public void saveWorkshiftDefinition(List<Definition> dishList) {
        Delete.table(CachedWorkshiftDefinition.class);
        List<CachedWorkshiftDefinition> cachedDishList = new Select().from(CachedWorkshiftDefinition.class).queryList();
        for (Definition dish : dishList) {
            new CachedWorkshiftDefinition(dish).save();
        }
    }

    public List<Definition> getAllWorkshiftDefinition() {
        List<CachedWorkshiftDefinition> cachedDishList = new Select().from(CachedWorkshiftDefinition.class).queryList();
        List<Definition> dishList = new ArrayList<>();
        for (CachedWorkshiftDefinition cd : cachedDishList) {
            dishList.add(cd.toDefinition());
        }

        return dishList;
    }

    //把后台系统返回的参数
    public void saveStoreBusinessInformation(StoreBusinessInformation dishList) {
        Delete.table(CachedStoreBusinessInformation.class);
        new CachedStoreBusinessInformation(dishList).save();
    }

    public StoreBusinessInformation getStoreBusinessInformation() {
        CachedStoreBusinessInformation cachedStoreBusinessInformation = new Select().from(CachedStoreBusinessInformation.class).querySingle();
        if (cachedStoreBusinessInformation != null) {
            return cachedStoreBusinessInformation.toStoreBusinessInformation();
        }
        return null;
    }


    //把后台系统返回的参数
    public void saveStoreConfiguration(StoreConfiguration dishList) {
        Delete.table(CachedStoreConfiguration.class);
        new CachedStoreConfiguration(dishList).save();
    }

    public StoreConfiguration getStoreConfiguration() {
        CachedStoreConfiguration cachedStoreConfiguration = new Select().from(CachedStoreConfiguration.class).querySingle();
        if (cachedStoreConfiguration != null) {
            return cachedStoreConfiguration.toStoreConfiguration();
        }
        return null;
    }

    //把后台系统返回的参数
    public void saveScreenInfo(ScreenResponse dishList) {
        Delete.table(CachedScreen.class);
        new CachedScreen(dishList).save();
    }

    public ScreenResponse getScreenInfo() {
        CachedScreen cachedStoreConfiguration = new Select().from(CachedScreen.class).querySingle();
        if (cachedStoreConfiguration != null) {
            return cachedStoreConfiguration.toScreenResponse();
        }
        return null;
    }

    //把后台系统返回的参数
    public void saveBindTerminalInfo(TerminalInfo terminalInfo) {
        Delete.table(CachedBindTerminalInfo.class);
        new CachedBindTerminalInfo(terminalInfo).save();
    }

    public TerminalInfo getBindTerminalInfo() {
        CachedBindTerminalInfo cachedStoreConfiguration = new Select().from(CachedBindTerminalInfo.class).querySingle();
        if (cachedStoreConfiguration != null) {
            return cachedStoreConfiguration.toStoreConfiguration();
        }
        return null;
    }

    //把后台系统返回的参数
    public void saveTerminalInfo(TerminalInfo terminalInfo) {
        Delete.table(CachedTerminalInfo.class);
        new CachedTerminalInfo(terminalInfo).save();
    }

    public TerminalInfo getTerminalInfo() {
        CachedTerminalInfo cachedStoreConfiguration = new Select().from(CachedTerminalInfo.class).querySingle();
        if (cachedStoreConfiguration != null) {
            return cachedStoreConfiguration.toStoreConfiguration();
        }
        return null;
    }





    //把沽清列表返回
    public void saveDishCount(List<DishCount> dishList) {
        Delete.table(CachedDishCount.class);
        for (DishCount dish : dishList) {
            new CachedDishCount(dish).save();
        }
    }

    public List<DishCount> getAllDishCount() {
        List<CachedDishCount> cachedDishList = new Select().from(CachedDishCount.class).queryList();
        List<DishCount> dishList = new ArrayList<>();
        for (CachedDishCount cd : cachedDishList) {
            dishList.add(cd.toDishCount());
        }
        return dishList;
    }

    public void modifyDishCount(List<Integer> dishList, int dishCount) {
        List<CachedDishCount> cachedDishList = new Select().from(CachedDishCount.class).queryList();
        for (CachedDishCount cdc : cachedDishList) {
            DishCount dc = cdc.toDishCount();
            if (dishList.contains(Integer.valueOf(String.valueOf(cdc.getId())))) {
                dc.setCount(dishCount);
                cdc.setJsonObject(ToolsUtils.getPrinterSth(dc));
                cdc.update();
            }
        }
    }


}
