package cn.acewill.mobile.pos.common;

import android.support.v4.util.ArrayMap;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import cn.acewill.mobile.pos.config.MyApplication;
import cn.acewill.mobile.pos.model.dish.Dish;
import cn.acewill.mobile.pos.model.dish.Option;
import cn.acewill.mobile.pos.model.dish.OptionCategory;
import cn.acewill.mobile.pos.utils.ToolsUtils;


/**
 * Created by DHH on 2017/4/25.
 */

public class DishOptionController<T> {
    private static ArrayMap<Integer, ArrayMap<Integer, ArrayMap<Integer, ArrayMap<Integer, List<Option>>>>> cartlAllDishOptionMap = new ArrayMap<>();

    public static boolean addDishOption(Dish dish, Dish dishSubItem, List<OptionCategory> optionCategoryList, Option option, boolean isDefaultAdd) {
        if (dish != null) {
            //普通菜品
            if (dish.subItemList == null || dish.subItemList.size() < 0) {
                ArrayMap<Integer, ArrayMap<Integer, ArrayMap<Integer, List<Option>>>> cartItemDishOptionMap = cartlAllDishOptionMap.get(dish.getDishOptionId());
                if (cartItemDishOptionMap == null) {
                    cartItemDishOptionMap = new ArrayMap<>();
                    ArrayMap<Integer, ArrayMap<Integer, List<Option>>> dishItemOptionMap = new ArrayMap<>();
                    ArrayMap<Integer, List<Option>> dishCategoryIdMap = new ArrayMap<>();
                    List<Option> optionList = new ArrayList<>();
                    optionList.add(option);
                    dishCategoryIdMap.put(option.getCategoryId(), optionList);
                    dishItemOptionMap.put(dish.getDishId(), dishCategoryIdMap);
                    cartItemDishOptionMap.put(dish.getDishId(), dishItemOptionMap);
                    cartlAllDishOptionMap.put(dish.getDishOptionId(), cartItemDishOptionMap);
                    return true;
                }
                ArrayMap<Integer, ArrayMap<Integer, List<Option>>> dishItemOptionMap = cartItemDishOptionMap.get(dish.getDishId());
                if (dishItemOptionMap == null) {
                    dishItemOptionMap = new ArrayMap<>();
                    ArrayMap<Integer, List<Option>> dishCategoryIdMap = new ArrayMap<>();
                    List<Option> optionList = new ArrayList<>();
                    optionList.add(option);
                    dishCategoryIdMap.put(option.getCategoryId(), optionList);
                    dishItemOptionMap.put(dish.getDishId(), dishCategoryIdMap);
                    cartItemDishOptionMap.put(dish.getDishId(), dishItemOptionMap);
                    cartlAllDishOptionMap.put(dish.getDishOptionId(), cartItemDishOptionMap);
                    return true;
                }
                ArrayMap<Integer, List<Option>> dishCategoryIdMap = dishItemOptionMap.get(dish.getDishId());
                if (dishCategoryIdMap == null) {
                    dishCategoryIdMap = new ArrayMap<>();
                    List<Option> optionList = new ArrayList<>();
                    optionList.add(option);
                    dishCategoryIdMap.put(option.getCategoryId(), optionList);
                    dishItemOptionMap.put(dish.getDishId(), dishCategoryIdMap);
                    cartItemDishOptionMap.put(dish.getDishId(), dishItemOptionMap);
                    cartlAllDishOptionMap.put(dish.getDishOptionId(), cartItemDishOptionMap);
                    return true;
                }
                List<Option> optionList2 = ToolsUtils.cloneTo(dishCategoryIdMap.get(option.getCategoryId()));
                List<Option> optionList = null;
                if (optionList2 != null) {
                    optionList = new CopyOnWriteArrayList<>(optionList2);
                }
                if (optionList == null || optionList.size() < 0) {
                    optionList = new CopyOnWriteArrayList<>();
                    optionList.add(option);
                    dishCategoryIdMap.put(option.getCategoryId(), optionList);
                    dishItemOptionMap.put(dish.getDishId(), dishCategoryIdMap);
                    cartItemDishOptionMap.put(dish.getDishId(), dishItemOptionMap);
                    cartlAllDishOptionMap.put(dish.getDishOptionId(), cartItemDishOptionMap);
                    return true;
                }
                int size = optionCategoryList.size();
                OptionCategory optionCategory = null;
                for (int i = 0; i < size; i++) {
                    if (option.getCategoryId() == optionCategoryList.get(i).getId()) {
                        optionCategory = optionCategoryList.get(i);
                    }
                }
                if (optionCategory != null) {
                    int min = optionCategory.minimalOptions;
                    int max = optionCategory.maximalOptions;
                    if (!option.getSelect()) {
                        if (optionList.size() > max || optionList.size() == max) {
                            MyApplication.getInstance().ShowToast(optionCategory.getName() + ToolsUtils.returnXMLStr("sort_by_most_options") + max +ToolsUtils.returnXMLStr("copies"));
                            return false;
                        }
                    }
                    //已是选中状态 想做删除操作
                    if (isHaveOption(optionList, option)) {
                        List<Option> dishItemTempOptionList = deleteDishItemOption(optionList, option);
                        optionList.clear();
                        optionList = dishItemTempOptionList;
                        dishCategoryIdMap.put(option.getCategoryId(), optionList);
                        dishItemOptionMap.put(dish.getDishId(), dishCategoryIdMap);
                        cartItemDishOptionMap.put(dish.getDishId(), dishItemOptionMap);
                        cartlAllDishOptionMap.put(dish.getDishOptionId(), cartItemDishOptionMap);
                        ToolsUtils.writeUserOperationRecords("取消选择了(" + option.getName() + ")定制项");
                    } else {
                        List<Option> dishItemTempOptionList = deleteDishItemOption(optionList, option);
                        optionList.clear();
                        optionList = dishItemTempOptionList;
                        optionList.add(option);
                        ToolsUtils.writeUserOperationRecords("选择了(" + option.getName() + ")定制项");
                    }
                    if (!ToolsUtils.isList(optionList)) {
                        dishCategoryIdMap.put(option.getCategoryId(), optionList);
                        dishItemOptionMap.put(dish.getDishId(), dishCategoryIdMap);
                        cartItemDishOptionMap.put(dish.getDishId(), dishItemOptionMap);
                        cartlAllDishOptionMap.put(dish.getDishOptionId(), cartItemDishOptionMap);
                    } else {
                        deleteDishOption(dish, null);
                    }
                    return true;
                }
            }
            //套餐
            else {
                ArrayMap<Integer, ArrayMap<Integer, ArrayMap<Integer, List<Option>>>> cartItemDishOptionMap = cartlAllDishOptionMap.get(dish.getDishOptionId());
                if (cartItemDishOptionMap == null) {
                    cartItemDishOptionMap = new ArrayMap<>();
                    ArrayMap<Integer, ArrayMap<Integer, List<Option>>> dishItemOptionMap = new ArrayMap<>();
                    ArrayMap<Integer, List<Option>> dishCategoryIdMap = new ArrayMap<>();
                    List<Option> optionList = new ArrayList<>();
                    optionList.add(option);
                    dishCategoryIdMap.put(option.getCategoryId(), optionList);
                    dishItemOptionMap.put(dishSubItem.getDishId(), dishCategoryIdMap);
                    cartItemDishOptionMap.put(dish.getDishId(), dishItemOptionMap);
                    cartlAllDishOptionMap.put(dish.getDishOptionId(), cartItemDishOptionMap);
                    return true;
                }

                ArrayMap<Integer, ArrayMap<Integer, List<Option>>> dishItemOptionMap = cartItemDishOptionMap.get(dish.getDishId());
                if (dishItemOptionMap == null) {
                    dishItemOptionMap = new ArrayMap<>();
                    ArrayMap<Integer, List<Option>> dishCategoryIdMap = new ArrayMap<>();
                    List<Option> optionList = new ArrayList<>();
                    optionList.add(option);
                    dishCategoryIdMap.put(option.getCategoryId(), optionList);
                    dishItemOptionMap.put(dishSubItem.getDishId(), dishCategoryIdMap);
                    cartItemDishOptionMap.put(dish.getDishId(), dishItemOptionMap);
                    cartlAllDishOptionMap.put(dish.getDishOptionId(), cartItemDishOptionMap);
                    return true;
                }
                ArrayMap<Integer, List<Option>> dishCategoryIdMap = dishItemOptionMap.get(dishSubItem.getDishId());
                if (dishCategoryIdMap == null) {
                    dishCategoryIdMap = new ArrayMap<>();
                    List<Option> optionList = new ArrayList<>();
                    optionList.add(option);
                    dishCategoryIdMap.put(option.getCategoryId(), optionList);
                    dishItemOptionMap.put(dishSubItem.getDishId(), dishCategoryIdMap);
                    cartItemDishOptionMap.put(dish.getDishId(), dishItemOptionMap);
                    cartlAllDishOptionMap.put(dish.getDishOptionId(), cartItemDishOptionMap);
                    return true;
                }
                List<Option> optionList2 = ToolsUtils.cloneTo(dishCategoryIdMap.get(option.getCategoryId()));
                List<Option> optionList = null;
                if (optionList2 != null) {
                    optionList = new CopyOnWriteArrayList<>(optionList2);
                }
                if (optionList == null || optionList.size() < 0) {
                    optionList = new CopyOnWriteArrayList<>();
                    optionList.add(option);
                    dishCategoryIdMap.put(option.getCategoryId(), optionList);
                    dishItemOptionMap.put(dishSubItem.getDishId(), dishCategoryIdMap);
                    cartItemDishOptionMap.put(dish.getDishId(), dishItemOptionMap);
                    cartlAllDishOptionMap.put(dish.getDishOptionId(), cartItemDishOptionMap);
                    return true;
                }
                int size = optionCategoryList.size();
                OptionCategory optionCategory = null;
                for (int i = 0; i < size; i++) {
                    if (option.getCategoryId() == optionCategoryList.get(i).getId()) {
                        optionCategory = optionCategoryList.get(i);
                    }
                }
                if (optionCategory != null) {
                    int min = optionCategory.minimalOptions;
                    int max = optionCategory.maximalOptions;
                    if (!option.getSelect()) {
                        if (optionList.size() > max || optionList.size() == max) {
                            MyApplication.getInstance().ShowToast(optionCategory.getName() + ToolsUtils.returnXMLStr("sort_by_most_options") + max +ToolsUtils.returnXMLStr("copies"));
                            return false;
                        }
                    }
                    //已是选中状态 想做删除操作
                    if (isHaveOption(optionList, option)) {
                        List<Option> dishItemTempOptionList = deleteDishItemOption(optionList, option);
                        optionList.clear();
                        optionList = dishItemTempOptionList;
                        dishCategoryIdMap.put(option.getCategoryId(), optionList);
                        dishItemOptionMap.put(dishSubItem.getDishId(), dishCategoryIdMap);
                        cartItemDishOptionMap.put(dish.getDishId(), dishItemOptionMap);
                        cartlAllDishOptionMap.put(dish.getDishOptionId(), cartItemDishOptionMap);
                        ToolsUtils.writeUserOperationRecords("取消选择了(" + option.getName() + ")定制项");
                    } else {
                        List<Option> dishItemTempOptionList = deleteDishItemOption(optionList, option);
                        optionList.clear();
                        optionList = dishItemTempOptionList;
                        optionList.add(option);
                        ToolsUtils.writeUserOperationRecords("选择了(" + option.getName() + ")定制项");
                    }
                    if (!ToolsUtils.isList(optionList)) {
                        dishCategoryIdMap.put(option.getCategoryId(), optionList);
                        dishItemOptionMap.put(dishSubItem.getDishId(), dishCategoryIdMap);
                        cartItemDishOptionMap.put(dish.getDishId(), dishItemOptionMap);
                        cartlAllDishOptionMap.put(dish.getDishOptionId(), cartItemDishOptionMap);
                    } else {
                        deleteDishOption(dish, dishSubItem);
                    }
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isHaveOption(List<Option> optionList, Option option) {
        if (!ToolsUtils.isList(optionList)) {
            for (Option optionTemp : optionList) {
                if (optionTemp.getId() == option.getId()) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isDishHaveOption(Dish dish, Dish subDish, Option option) {
        if (subDish != null) {
            if (getDishOptionMap(dish.getDishOptionId()) != null && getDishOptionMap(dish.getDishOptionId()).size() > 0) {
                ArrayMap<Integer, ArrayMap<Integer, List<Option>>> dishMap = getDishOptionMap(dish.getDishOptionId()).get(dish.getDishId());
                if (dishMap != null && dishMap.size() > 0) {
                    ArrayMap<Integer, List<Option>> subMap = dishMap.get(subDish.getDishId());
                    if (subMap != null && subMap.size() > 0) {
                        List<Option> dishSelectList = subMap.get(option.getCategoryId());
                        if (isHaveOption(dishSelectList, option)) {
                            return true;
                        }
                    }
                }
            }

        } else {
            if (getDishOptionMap(dish.getDishOptionId()) != null && getDishOptionMap(dish.getDishOptionId()).size() > 0) {
                List<Option> dishSelectList = getDishOptionMap(dish.getDishOptionId()).get(dish.getDishId()).get(dish.getDishId()).get(option.getCategoryId());
                if (isHaveOption(dishSelectList, option)) {
                    return true;
                }
            }
        }

        return false;
    }

    public static List<Option> deleteDishItemOption(List<Option> optionList, Option option) {
        if (!ToolsUtils.isList(optionList)) {
            List<Option> optionListTemp = new CopyOnWriteArrayList<>();
            for (Option optionItem : optionList) {
                if (optionItem.getId() != option.getId()) {
                    optionListTemp.add(optionItem);
                }
            }
            return optionListTemp;
        }
        return null;
    }

    public static void deleteDishOption(Dish dish, Dish dishSubItem) {
        if (dish != null) {
            //套餐子项
            if (dishSubItem != null) {
                cartlAllDishOptionMap.get(dish.getDishOptionId()).get(dish.getDishId()).remove(dishSubItem.getDishId());
            }
            //普通菜品
            else {
                cartlAllDishOptionMap.remove(dish.getDishOptionId());
            }
        }
    }

    public static ArrayMap<Integer, ArrayMap<Integer, ArrayMap<Integer, List<Option>>>> getDishOptionMap(int position) {
        ArrayMap<Integer, ArrayMap<Integer, ArrayMap<Integer, List<Option>>>> dishOptionMap = new ArrayMap<>();
        dishOptionMap.clear();
        if (cartlAllDishOptionMap != null) {
            if (cartlAllDishOptionMap.get(position) != null && cartlAllDishOptionMap.get(position).size() > 0) {
                dishOptionMap = cartlAllDishOptionMap.get(position);
            }
        }
        if(dishOptionMap == null)
        {
            dishOptionMap = new ArrayMap<>();
        }
        return dishOptionMap;
    }

    public static boolean checkSelectOption(Dish dish, Dish dishSubItem) {
        if (dish != null)
            try {
                if (!ToolsUtils.isList(dish.subItemList)) {
                    if (!ToolsUtils.isList(dishSubItem.getOptionCategoryList())) {
                        List<OptionCategory> optionCategoryList = dishSubItem.getOptionCategoryList();
                        final int optionCategorySize = optionCategoryList.size();
                        int loopSize = 0;
                        for (OptionCategory optionCategory : optionCategoryList) {
                            int min = optionCategory.minimalOptions;
                            int max = optionCategory.maximalOptions;
                            try {
                                loopSize++;
                                List<Option> optionList = getDishOptionMap(dish.getDishOptionId()).get(dish.getDishId()).get(dishSubItem.getDishId()).get(optionCategory.getId());
                                if (!ToolsUtils.isList(optionList)) {
                                    List<Option> dishOptionItemSelectList = new CopyOnWriteArrayList<>();
                                    for (Option option : optionList) {
                                        if (option.isSelect) {
                                            dishOptionItemSelectList.add(option);
                                        }
                                    }
                                    int selectOptionSize = dishOptionItemSelectList.size();
                                    if (selectOptionSize < min) {
                                        MyApplication.getInstance().ShowToast(optionCategory.getName() + ToolsUtils.returnXMLStr("cllassification_least_choice") + min +ToolsUtils.returnXMLStr("copies"));
                                        return false;
                                    }
                                    if (selectOptionSize > max) {
                                        MyApplication.getInstance().ShowToast(optionCategory.getName() + ToolsUtils.returnXMLStr("sort_by_most_options") + max +ToolsUtils.returnXMLStr("copies"));
                                        return false;
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                if(min >0)
                                {
                                    MyApplication.getInstance().ShowToast(optionCategory.getName() + ToolsUtils.returnXMLStr("cllassification_least_choice") + min +ToolsUtils.returnXMLStr("copies"));
                                    return false;
                                }
                                else if(optionCategorySize == loopSize){
                                    return true;
                                }
                            }
                        }
                    }
                } else {
                    if (!ToolsUtils.isList(dish.getOptionCategoryList())) {
                        List<OptionCategory> optionCategoryList = dish.getOptionCategoryList();
                        final int optionCategorySize = optionCategoryList.size();
                        int loopSize = 0;
                        for (OptionCategory optionCategory : optionCategoryList) {
                            int min = optionCategory.minimalOptions;
                            int max = optionCategory.maximalOptions;
                            try {
                                loopSize++;
                                List<Option> optionList = getDishOptionMap(dish.getDishOptionId()).get(dish.getDishId()).get(dish.getDishId()).get(optionCategory.getId());
                                if (!ToolsUtils.isList(optionList)) {
                                    List<Option> dishOptionItemSelectList = new CopyOnWriteArrayList<>();
                                    for (Option option : optionList) {
                                        if (option.isSelect) {
                                            dishOptionItemSelectList.add(option);
                                        }
                                    }
                                    int selectOptionSize = dishOptionItemSelectList.size();
                                    if (selectOptionSize < min) {
                                        MyApplication.getInstance().ShowToast(optionCategory.getName() + ToolsUtils.returnXMLStr("cllassification_least_choice") + min +ToolsUtils.returnXMLStr("copies"));
                                        return false;
                                    }
                                    if (selectOptionSize > max) {
                                        MyApplication.getInstance().ShowToast(optionCategory.getName() + ToolsUtils.returnXMLStr("sort_by_most_options") + max +ToolsUtils.returnXMLStr("copies"));
                                        return false;
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                if(min >0)
                                {
                                    MyApplication.getInstance().ShowToast(optionCategory.getName() + ToolsUtils.returnXMLStr("cllassification_least_choice") + min +ToolsUtils.returnXMLStr("copies"));
                                    return false;
                                }
                                else if(optionCategorySize == loopSize){
                                    return true;
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                return true;
            }
        return true;
    }

    public static ArrayList<Option> getDishOption(Dish dish, Dish dishSubItemDish) {
        if (dish != null)
            try {
                {
                    //套餐
                    if (dish.subItemList != null && dish.subItemList.size() > 0) {
                        if (!ToolsUtils.isList(dishSubItemDish.getOptionCategoryList())) {
                            List<Option> optionSelectList = new CopyOnWriteArrayList<>();
                            for (OptionCategory optionCategory : dishSubItemDish.getOptionCategoryList()) {
                                Integer optionCategoryId = optionCategory.getId();
                                List<Option> optionList = getDishOptionMap(dish.getDishOptionId()).get(dish.getDishId()).get(dishSubItemDish.getDishId()).get(optionCategoryId);
                                if (!ToolsUtils.isList(optionList)) {
                                    for (Option option : optionList) {
                                        if (option.isSelect) {
                                            optionSelectList.add(option);
                                        }
                                    }
                                }
                            }
                            return new ArrayList<>(optionSelectList);
                        }
                    }
                    //单项菜品
                    else {
                        if (!ToolsUtils.isList(dish.getOptionCategoryList())) {
                            List<Option> optionSelectList = new CopyOnWriteArrayList<>();
                            for (OptionCategory optionCategory : dish.getOptionCategoryList()) {
                                Integer optionCategoryId = optionCategory.getId();
                                List<Option> optionList = getDishOptionMap(dish.getDishOptionId()).get(dish.getDishId()).get(dish.getDishId()).get(optionCategoryId);
                                if (!ToolsUtils.isList(optionList)) {
                                    for (Option option : optionList) {
                                        if (option.isSelect) {
                                            optionSelectList.add(option);
                                        }
                                    }
                                }
                            }
                            return new ArrayList<>(optionSelectList);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                return new ArrayList<>();
            }
        return null;
    }

    public static void cleanCartDishMap() {
        if (cartlAllDishOptionMap != null) {
            cartlAllDishOptionMap.clear();
        }
    }

}
