package cn.acewill.mobile.pos.utils;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import cn.acewill.mobile.pos.R;
import cn.acewill.mobile.pos.common.StoreInfor;
import cn.acewill.mobile.pos.config.MyApplication;
import cn.acewill.mobile.pos.interfices.DialogActiveCall;
import cn.acewill.mobile.pos.model.MarketObject;
import cn.acewill.mobile.pos.model.MarketType;
import cn.acewill.mobile.pos.model.dish.Cart;
import cn.acewill.mobile.pos.model.dish.Dish;
import cn.acewill.mobile.pos.model.dish.Option;
import cn.acewill.mobile.pos.model.order.MarketingActivity;
import cn.acewill.mobile.pos.model.order.Order;
import cn.acewill.mobile.pos.model.order.OrderItem;
import cn.acewill.mobile.pos.service.PosInfo;
import cn.acewill.mobile.pos.ui.adapter.ActivityListAdapter;

import static cn.acewill.mobile.pos.common.MarketDataController.equalOption;

/**
 * 结账使用
 * Created by DHH on 2017/12/13.
 */

public class PayDialogUtil {

    /**
     * 营销活动
     *
     * @param context
     * @param allMoney 订单总金额
     * @param callBack
     */
    public static void getActiveDialog(final Context context, final BigDecimal[] allMoney, final Order order, final List<Dish> dishList, final DialogActiveCall callBack) {
        final Dialog dialog = DialogUtil.createDialog(context, R.layout.dialog_active, 9, 8);
        final List<Dish> singleItemList = new ArrayList<Dish>();
        int itemIndex = 1;
        final BigDecimal[] takeOutMoney = {new BigDecimal("0.00")};//打包费
        //先将菜品拆分出来 最后再进行菜品组装
        if(dishList != null && dishList.size() >0)
        {
            for (Dish item : dishList) {
                int count = item.getQuantity();
                if (count == 1) {
                    item.setItemIndex(itemIndex);
                    itemIndex++;
                    singleItemList.add(item);
                } else {
                    for (int i = 0; i < count; i++) {
                        Dish singleItem = ToolsUtils.cloneTo(item);
                        singleItem.setItemIndex(itemIndex);
                        singleItem.setQuantity(1);
                        singleItem.setTempQuantity(1);
                        itemIndex++;
                        singleItemList.add(singleItem);
                    }
                }
            }
        }


        TextView active_close = (TextView) dialog.findViewById(R.id.active_close);
        final ListView active_list = (ListView) dialog.findViewById(R.id.active_list);
        final TextView none = (TextView) dialog.findViewById(R.id.none);
        final LinearLayout none_ll = (LinearLayout) dialog.findViewById(R.id.none_ll);
        final ProgressBar none_pro = (ProgressBar) dialog.findViewById(R.id.none_pro);

        final BigDecimal[] reduceMoney = {new BigDecimal(0)};//优惠金额

        final BigDecimal[] notJobMoney = {new BigDecimal(0)};//不参与打折金额

        if (order != null) {
            BigDecimal tempMarketPrice = new BigDecimal("0.000");
            List<OrderItem> orderItemList = order.getItemList();
            int size = orderItemList.size();
            if (!ToolsUtils.isList(orderItemList)) {
                for (int i = 0; i < size; i++) {
                    OrderItem oi = orderItemList.get(i);
                    if (!ToolsUtils.isList(oi.getTempMarketList())) {
                        for (MarketObject marketObject1 : oi.getTempMarketList()) {
                            if (marketObject1.getMarketType() == MarketType.DISCOUNT) {
                                tempMarketPrice = marketObject1.getReduceCash();
                            }
                        }
                    }
                }
            }
            allMoney[0] = allMoney[0].add(tempMarketPrice);
        }

        final ActivityListAdapter adapter = new ActivityListAdapter(context);
        adapter.setAllMoney(allMoney[0] + "");
        active_list.setAdapter(adapter);

        if (StoreInfor.marketingActivities != null && StoreInfor.marketingActivities.size() > 0) {
            none_ll.setVisibility(View.GONE);
            active_list.setVisibility(View.VISIBLE);
            adapter.setData(StoreInfor.marketingActivities);
        } else {
            none_pro.setVisibility(View.GONE);
            none.setText(ToolsUtils.returnXMLStr("activity_is_null"));
        }

        active_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final MarketingActivity activitys = (MarketingActivity) adapter.getItem(position);
                if(!TextUtils.isEmpty(allMoney[0]+""))
                {
                    if (activitys.getDiscountType() == 1) {
                        BigDecimal reduceMoney = activitys.getDiscountAmount();
                        if (reduceMoney.compareTo(new BigDecimal(allMoney[0]+"")) == 1)//如果折扣金额大于订单金额
                        {
                            return;
                        }
                    }
                }
                BigDecimal marketCost = new BigDecimal("0");//参与活动的菜品总金额
                BigDecimal dishPriceSum = new BigDecimal("0.000");//参加促销活动菜品的总price  最后要拿这个钱来算比例
                if (activitys != null) {
                    if (singleItemList != null && singleItemList.size() > 0) {
                        for (Dish dish : singleItemList) {
                            if (!dish.isParticipationDisCount())//不参与打折的菜品
                            {
                                notJobMoney[0] = notJobMoney[0].add(dish.getOrderDishCost().multiply(new BigDecimal(dish.getQuantity())));
                            } else {
                                BigDecimal dishPrice = dish.getOrderDishCost().multiply(new BigDecimal(dish.getQuantity()));
                                dishPriceSum = dishPriceSum.add(dishPrice);
                                marketCost = marketCost.add(dishPrice);
                            }
                        }

                        if(dishPriceSum.compareTo(BigDecimal.ZERO) == 0)
                        {
                            MyApplication.getInstance().ShowToast(ToolsUtils.returnXMLStr("not_dish_join_activity"));
                            return;
                        }
                        Dish tempDish  = null ;
                        //优惠的总金额
                        BigDecimal dishPreferentialSum = new BigDecimal("0.000");
                        //用来临时计算的满减优惠总金额
                        BigDecimal tempDishPreferentialSum = new BigDecimal("0.00");
                        //满减优惠总金额
                        BigDecimal dishPreferentialCashSum = new BigDecimal("0.00");
                        int size = singleItemList.size();
                        //全单满减
                        if (activitys.getDiscountType() == 1) {
                            dishPreferentialSum = activitys.getDiscountAmount();
                            tempDishPreferentialSum = activitys.getDiscountAmount();
                        }
                        else if(activitys.getDiscountType() == 0)
                        {
                            dishPreferentialSum = marketCost.subtract(marketCost.multiply(activitys.getDiscountRate()));
                        }
                        for (int i = 0; i < size; i++) {
                            Dish dish = singleItemList.get(i);
                            if (dish.isParticipationDisCount())
                            {
                                //计算外带打包费用
                                if ("TAKE_OUT".equals(PosInfo.getInstance().getOrderType()) || "SALE_OUT".equals(PosInfo.getInstance().getOrderType())) {
                                    if (dish.getWaiDai_cost() != null) {
                                        BigDecimal bigDecimal3 = new BigDecimal(dish.quantity);
                                        takeOutMoney[0] = takeOutMoney[0].add(bigDecimal3.multiply(dish.getWaiDai_cost()));
                                    }
                                }
                                //全单满减
                                if (activitys.getDiscountType() == 1) {
                                    BigDecimal dishTotalCost = dish.getOrderDishCost().multiply(new BigDecimal(dish.getQuantity()));
                                    //单项菜品优惠金额
                                    BigDecimal meanDishPreferential = dishTotalCost.multiply(dishPreferentialSum).divide(marketCost, 2, BigDecimal.ROUND_HALF_UP);
                                    BigDecimal dishPartPreferential = meanDishPreferential.divide(new BigDecimal(dish.getQuantity()), 2, BigDecimal.ROUND_HALF_UP);
                                    dish.setAllOrderDisCountSubtractPrice(dishPartPreferential);
                                    dish.setOnlyCost(dish.getOrderDishCost().subtract(dishPartPreferential));
                                    dishPreferentialCashSum = dishPreferentialCashSum.add(dish.getCost().multiply(new BigDecimal(dish.getQuantity())));
                                    if(size -1 == i)//最后一个菜
                                    {
                                        tempDish = dish;
                                    }
                                    else{
                                        setMarketObjectList(dish, activitys, meanDishPreferential);
                                        tempDishPreferentialSum = tempDishPreferentialSum.subtract(meanDishPreferential);
                                    }
                                }
                                //全单折扣
                                else if (activitys.getDiscountType() == 0) {
                                    //                                    //单项菜品优惠金额
                                    BigDecimal dishTotalCost = dish.getOrderDishCost().multiply(new BigDecimal(dish.getQuantity()));
                                    //单项菜品优惠金额
                                    BigDecimal meanDishPreferential = dishTotalCost.multiply(dishPreferentialSum).divide(marketCost, 3, BigDecimal.ROUND_HALF_UP);
                                    BigDecimal dishPartPreferential = meanDishPreferential.divide(new BigDecimal(dish.getQuantity()), 3, BigDecimal.ROUND_HALF_UP);
                                    dish.setAllOrderDisCountSubtractPrice(dishPartPreferential);
                                    dish.setOnlyCost(dish.getOrderDishCost().subtract(dishPartPreferential));
                                    setMarketObjectList(dish, activitys, meanDishPreferential);
                                }
                            }
                        }
                        if (activitys.getDiscountType() == 1) {
                            BigDecimal price = marketCost.subtract(dishPreferentialSum);//参加完优惠活动后应该是这个价钱
                            tempDish.setOnlyCost(tempDish.getCost().add(price.subtract(dishPreferentialCashSum)));
                            tempDish.setAllOrderDisCountSubtractPrice(tempDish.getAllOrderDisCountSubtractPrice().add(dishPreferentialCashSum.subtract(price)));
                            setMarketObjectList(tempDish, activitys, tempDishPreferentialSum);
                        }

                        Cart.getDishItemList().clear();
                        Cart.dishItemList = assemblyList(singleItemList);

                        allMoney[0] = marketCost.add(notJobMoney[0]).subtract(dishPreferentialSum);//计算完优惠后，再加上之前减去的不参与打折的金额,然后减去优惠的金额，返回最终要支付的总金额 再加上打包费
                        callBack.onOk(allMoney[0].add(takeOutMoney[0]).setScale(2, BigDecimal.ROUND_HALF_UP), dishPreferentialSum, activitys.getDiscountName());
                        dialog.dismiss();
                    } else if (order != null) {
                        List<OrderItem> itemList = breakUpList(order.getItemList());
                        for (OrderItem orderItem : itemList) {
                            if (!orderItem.isParticipationDisCount())//不参与打折的菜品
                            {
                                notJobMoney[0] = notJobMoney[0].add(orderItem.getOrderDishCost().multiply(new BigDecimal(orderItem.getQuantity())));
                            }
                            else{
                                BigDecimal dishPrice = orderItem.getOrderDishCost().multiply(new BigDecimal(orderItem.getQuantity()));
                                dishPriceSum = dishPriceSum.add(dishPrice);
                                marketCost = marketCost.add(dishPrice);
                            }
                        }

                        if(dishPriceSum.compareTo(BigDecimal.ZERO) == 0)
                        {
                            MyApplication.getInstance().ShowToast(ToolsUtils.returnXMLStr("not_dish_join_activity"));
                            return;
                        }

                        OrderItem tempOrderItem = null;
                        //优惠的总金额
                        BigDecimal dishPreferentialSum = new BigDecimal("0.000");
                        //用来临时计算的满减优惠总金额
                        BigDecimal tempDishPreferentialSum = new BigDecimal("0.00");
                        //满减优惠总金额
                        BigDecimal dishPreferentialCashSum = new BigDecimal("0.00");
                        //全单满减
                        if (activitys.getDiscountType() == 1) {
                            dishPreferentialSum = activitys.getDiscountAmount();
                            tempDishPreferentialSum = activitys.getDiscountAmount();
                        }
                        else if(activitys.getDiscountType() == 0)
                        {
                            dishPreferentialSum = marketCost.subtract(marketCost.multiply(activitys.getDiscountRate()));
                        }
                        int size = itemList.size();
                        for (int i = 0; i < size; i++) {
                            OrderItem orderItem  = itemList.get(i);
                            if (orderItem.isParticipationDisCount())
                            {
                                //计算外带打包费用
                                if ("TAKE_OUT".equals(PosInfo.getInstance().getOrderType()) || "SALE_OUT".equals(PosInfo.getInstance().getOrderType())) {
                                    if (orderItem.getWaiDai_cost() != null) {
                                        BigDecimal bigDecimal3 = new BigDecimal(orderItem.quantity);
                                        takeOutMoney[0] = takeOutMoney[0].add(bigDecimal3.multiply(orderItem.getWaiDai_cost()));
                                    }
                                }
                                //全单满减
                                if (activitys.getDiscountType() == 1) {
                                    BigDecimal dishTotalCost = orderItem.getOrderDishCost().multiply(new BigDecimal(orderItem.getQuantity()));
                                    //单项菜品优惠金额
                                    BigDecimal meanDishPreferential = dishTotalCost.multiply(dishPreferentialSum).divide(marketCost, 2, BigDecimal.ROUND_HALF_UP);
                                    BigDecimal dishPartPreferential = meanDishPreferential.divide(new BigDecimal(orderItem.getQuantity()), 2, BigDecimal.ROUND_HALF_UP);
                                    orderItem.setAllOrderDisCountSubtractPrice(dishPartPreferential);
                                    orderItem.setOnlyCost(orderItem.getOrderDishCost().subtract(dishPartPreferential));

                                    dishPreferentialCashSum = dishPreferentialCashSum.add(orderItem.getCost().multiply(new BigDecimal(orderItem.getQuantity())));
                                    if(size -1 == i)//最后一个菜
                                    {
                                        tempOrderItem = orderItem;
                                    }
                                    else{
                                        setMarketObjectList(orderItem, activitys, meanDishPreferential);
                                        tempDishPreferentialSum = tempDishPreferentialSum.subtract(meanDishPreferential);
                                    }
                                }
                                //全单折扣
                                else if (activitys.getDiscountType() == 0) {
                                    BigDecimal dishTotalCost = orderItem.getOrderDishCost().multiply(new BigDecimal(orderItem.getQuantity()));
                                    //单项菜品优惠金额
                                    BigDecimal meanDishPreferential = dishTotalCost.multiply(dishPreferentialSum).divide(marketCost, 3, BigDecimal.ROUND_HALF_UP);
                                    BigDecimal dishPartPreferential = meanDishPreferential.divide(new BigDecimal(orderItem.getQuantity()), 3, BigDecimal.ROUND_HALF_UP);
                                    orderItem.setAllOrderDisCountSubtractPrice(dishPartPreferential);
                                    orderItem.setOnlyCost(orderItem.getOrderDishCost().subtract(dishPartPreferential));
                                    setMarketObjectList(orderItem, activitys, meanDishPreferential);
                                }
                            }
                        }
                        if (activitys.getDiscountType() == 1) {
                            BigDecimal price = marketCost.subtract(dishPreferentialSum);//参加完优惠活动后应该是这个价钱
                            tempOrderItem.setOnlyCost(tempOrderItem.getCost().add(price.subtract(dishPreferentialCashSum)));
                            tempOrderItem.setAllOrderDisCountSubtractPrice(tempOrderItem.getAllOrderDisCountSubtractPrice().add(dishPreferentialCashSum.subtract(price)));
                            setMarketObjectList(tempOrderItem, activitys, tempDishPreferentialSum);
                        }

                        order.getItemList().clear();
                        order.setItemList(assemblyOrderItemList(itemList));

                        allMoney[0] = marketCost.add(notJobMoney[0]).subtract(dishPreferentialSum);//计算完优惠后，再加上之前减去的不参与打折的金额,然后减去优惠的金额，返回最终要支付的总金额
                        callBack.onOk(allMoney[0].add(takeOutMoney[0]).setScale(2, BigDecimal.ROUND_HALF_UP), dishPreferentialSum, activitys.getDiscountName());
                        dialog.dismiss();
                    }
                } else {
                    MyApplication.getInstance().ShowToast(ToolsUtils.returnXMLStr("discount_is_null"));
                }
            }
        });

        active_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    private static void setMarketObjectList(Dish dish, MarketingActivity activitys, BigDecimal marketDishCost) {
        if (dish.getMarketList() == null) {
            dish.marketList = new ArrayList<>();
        }
        else{
            ToolsUtils.removeItemForMarkType(dish.getMarketList(),MarketType.DISCOUNT);
            //            for(MarketObject marketObject:dish.getMarketList())
            //            {
            //                if(marketObject.getMarketType() == MarketType.DISCOUNT)
            //                {
            //                    dish.getMarketList().remove(marketObject);
            //                }
            //            }
        }
        MarketObject marketObject = new MarketObject(activitys.getDiscountName(), marketDishCost, MarketType.DISCOUNT);
        dish.marketList.add(marketObject);
    }

    private static void setMarketObjectList(OrderItem dish, MarketingActivity activitys, BigDecimal marketDishCost) {
        if (dish.getMarketList() == null) {
            dish.marketList = new ArrayList<>();
        }
        else{
            ToolsUtils.removeItemForMarkType(dish.getMarketList(),MarketType.DISCOUNT);
            //            for(MarketObject marketObject:dish.getMarketList())
            //            {
            //                if(marketObject.getMarketType() == MarketType.DISCOUNT)
            //                {
            //                    dish.getMarketList().remove(marketObject);
            //                }
            //            }
        }
        MarketObject marketObject = new MarketObject(activitys.getDiscountName(), marketDishCost, MarketType.DISCOUNT);
        dish.marketList.add(marketObject);
    }

    /**
     * 组装list 将拆开的菜品重新组装起来
     * @param singleItemList
     * @return
     */
    private static List<Dish> assemblyList(List<Dish> singleItemList)
    {
        List<Dish> dishItem = new ArrayList<Dish>();
        for (Dish item : singleItemList) {
            Dish singleItem = ToolsUtils.cloneTo(item);
            BigDecimal itemCost = item.getCost();
            boolean isSame = false;
            boolean sameOption = true;//默认是相同的
            List<Option> options = item.getOptionList();
            List<Dish.Package> itemSubList = item.getSubItemList();
            for (Dish bean : dishItem) {
                // 判断是不是套餐，如果是套餐比较套餐项；
                // 普通菜品就比较定制项
                List<Dish.Package> beanSubList = bean.getSubItemList();
                if (beanSubList == null || beanSubList.isEmpty()) {
                    List<Option> list1 = bean.getOptionList();
                    if (options != null && list1 != null)
                        sameOption = equalOption(options, list1);
                    if (item.getDishId() == bean.getDishId() && item.getCost().equals(bean.getCost()) && item.getAllOrderDisCountSubtractPrice().equals(bean.getAllOrderDisCountSubtractPrice()) && item.getDishSubAllOrderDisCount().equals(bean.getDishSubAllOrderDisCount())  && sameOption) {
                        //这是相同项的
//                        isSame = true;
//                        bean.setQuantity(bean.getQuantity() + 1);
                        break;
                    }
                } else {
                    if (item.getDishId() == bean.getDishId() && item.getCost().equals(bean.getCost())) {
                        if (itemSubList != null && !itemSubList.isEmpty()) {
                            // 需要比较两个套餐的套餐项是否都一样
                            if (sameItemList(itemSubList, beanSubList)) {
                                //这是相同项的
//                                isSame = true;
//                                bean.setQuantity(bean.getQuantity() + 1);
//                                bean.setTempQuantity(bean.getTempQuantity() + 1);
                                break;
                            }
                        }
                    }
                }
            }
            if (!isSame) {
                dishItem.add(singleItem);
            }
        }
        return dishItem;
    }

    private static boolean sameItemList(List<Dish.Package> list1, List<Dish.Package> list2) {
        if (list1.size() != list2.size())
            return false;
        for (Dish.Package item1 : list1) {
            boolean exist = false;
            for (Dish.Package item2 : list2) {
                if (item1.getDishId() == item2.getDishId() && item1.getItemPrice() != null && item2.getItemPrice() != null && item1.getItemPrice().equals(item2.getItemPrice())) {
                    exist = true;
                }
            }
            if (!exist)
                return false;
        }
        return true;
    }

    /**
     * 拆分菜品列表
     * @param dishList
     * @return
     */
    private static List<OrderItem> breakUpList(List<OrderItem> dishList)
    {
        int itemIndex = 1;
        List<OrderItem> singleItemList = new ArrayList<OrderItem>();
        for (OrderItem item : dishList) {
            int count = item.getQuantity();
            if (count == 1) {
                item.setItemIndex(itemIndex);
                itemIndex++;
                singleItemList.add(item);
            } else {
                for (int i = 0; i < count; i++) {
                    OrderItem singleItem = ToolsUtils.cloneTo(item);
                    singleItem.setItemIndex(itemIndex);
                    singleItem.setQuantity(1);
                    itemIndex++;
                    singleItemList.add(singleItem);
                }
            }
        }
        return singleItemList;
    }

    /**
     * 组装订单中的菜品列表
     * @param singleItemList
     * @return
     */
    private static List<OrderItem> assemblyOrderItemList(List<OrderItem> singleItemList)
    {
        List<OrderItem> dishItem = new ArrayList<OrderItem>();
        for (OrderItem item : singleItemList) {
            OrderItem singleItem = ToolsUtils.cloneTo(item);
            BigDecimal itemCost = item.getCost();
            boolean isSame = false;
            boolean sameOption = true;//默认是相同的
            List<Option> options = item.getOptionList();
            List<Dish.Package> itemSubList = item.getSubItemList();
            for (OrderItem bean : dishItem) {
                // 判断是不是套餐，如果是套餐比较套餐项；
                // 普通菜品就比较定制项
                List<Dish.Package> beanSubList = bean.getSubItemList();
                if (beanSubList == null || beanSubList.isEmpty()) {
                    List<Option> list1 = bean.getOptionList();
                    if (options != null && list1 != null)
                        sameOption = equalOption(options, list1);
                    if (item.getDishId() == bean.getDishId() && item.getCost().equals(bean.getCost()) && sameOption) {
                        //这是相同项的
//                        isSame = true;
//                        bean.setQuantity(bean.getQuantity() + 1);
                        break;
                    }
                } else {
                    if (item.getDishId() == bean.getDishId() && item.getCost().equals(bean.getCost())) {
                        if (itemSubList != null && !itemSubList.isEmpty()) {
                            // 需要比较两个套餐的套餐项是否都一样
                            if (sameItemList(itemSubList, beanSubList)) {
                                //这是相同项的
//                                isSame = true;
//                                bean.setQuantity(bean.getQuantity() + 1);
                                break;
                            }
                        }
                    }
                }
            }
            if (!isSame) {
                dishItem.add(singleItem);
            }
        }
        return dishItem;
    }
}
