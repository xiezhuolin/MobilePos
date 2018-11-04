package cn.acewill.mobile.pos.common;

import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import android.util.Log;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import cn.acewill.mobile.pos.model.Discount;
import cn.acewill.mobile.pos.model.Market;
import cn.acewill.mobile.pos.model.MarketObject;
import cn.acewill.mobile.pos.model.MarketType;
import cn.acewill.mobile.pos.model.dish.Dish;
import cn.acewill.mobile.pos.model.dish.DishDiscount;
import cn.acewill.mobile.pos.model.dish.Menu;
import cn.acewill.mobile.pos.model.dish.Option;
import cn.acewill.mobile.pos.model.dish.OptionCategory;
import cn.acewill.mobile.pos.model.order.MarketingActivity;
import cn.acewill.mobile.pos.service.PosInfo;
import cn.acewill.mobile.pos.utils.TimeUtil;
import cn.acewill.mobile.pos.utils.ToolsUtils;

import static cn.acewill.mobile.pos.common.DishDataController.DISH_TYPE;
import static cn.acewill.mobile.pos.common.DishDataController.getDishs;


/**
 * Created by DHH on 2017/3/14.
 */

public class MarketDataController {
	/**
	 * 门店的营销方案列表
	 */
	public static List<Market>   marketList   = new ArrayList<>();
	/**
	 * 门店
	 */
	public static List<Discount> disCountList = new ArrayList<>();

	public static List<Discount> getDisCountList() {
		return disCountList;
	}

	public static void setDisCountList(List<Discount> disCountList) {
		MarketDataController.disCountList = disCountList;
	}

	public static void setMarketList(List<Market> marketList) {
		cleanStoreMarket();
		MarketDataController.marketList = ToolsUtils.cloneTo(marketList);
	}

	public static void cleanStoreMarket() {
		if (marketList != null && marketList.size() > 0) {
			marketList.clear();
		}
	}

	public static ArrayMap<Integer, Dish> dishId2DishMap = new ArrayMap<>();

	public static ArrayMap<Integer, Dish> getDishIdforDishMap(List<Dish> dishs) {

		if (dishs != null && dishs.size() > 0) {
			for (Dish dish : dishs) {
				try {
					dishId2DishMap.put(dish.getDishId(), dish);
				} catch (Exception e) {
					Log.i("MarketDataController", "获取菜品异常");
					break;
				}
			}
		}


		return dishId2DishMap;
	}

	/**
	 * 初始化菜品marketList,并将已经生效的活动名和优惠了多少价格加入到list中
	 *
	 * @param dish
	 */
	private static Dish addMarketList(Dish dish, Market market, BigDecimal costTemp) {
		dish.marketList = null;
		if (dish.getMarketList() == null) {
			dish.marketList = new ArrayList<>();
		}
		MarketObject marketObject = new MarketObject(market.getMarketName(), dish.getDishPrice()
				.subtract(costTemp).setScale(3, BigDecimal.ROUND_DOWN), MarketType.SALES);
		dish.marketList.add(marketObject);
		return dish;
	}

	/**
	 * 有买有送
	 *
	 * @param dish
	 * @param market
	 */
	private static void addMarketList(Dish dish, Market market, BigDecimal dishPreferentialSum, BigDecimal dishPriceSum) {
		dish.marketList = null;
		if (dish.getPrice() != null) {
			if (dish.getMarketList() == null) {
				dish.marketList = new ArrayList<>();
			}
			//优惠的百分比
			BigDecimal disCountPercent = new BigDecimal("100")
					.divide(dishPriceSum, 3, BigDecimal.ROUND_DOWN);
			//单项菜品优惠金额
			BigDecimal meanDishPreferential = disCountPercent
					.multiply(dish.getPrice().multiply(new BigDecimal(dish.getQuantity())))
					.divide(new BigDecimal("100"), 3, BigDecimal.ROUND_HALF_UP)
					.multiply(dishPreferentialSum);

			MarketObject marketObject = new MarketObject(market
					.getMarketName(), meanDishPreferential
					.multiply(new BigDecimal(dish.getQuantity()))
					.setScale(3, BigDecimal.ROUND_HALF_UP), MarketType.SALES);
			dish.marketList.add(marketObject);
		}
	}


	private static Dish addMarketList(List<Dish> dishList, Market market, List<Dish> joinMarketDishList, BigDecimal dishPriceSum, BigDecimal marketCost) {
		try {

			Dish tempDish = null;
			//优惠的总金额
			BigDecimal dishPreferentialSum = new BigDecimal("0.000");
			//用来临时计算的满减优惠总金额
			BigDecimal tempDishPreferentialSum = new BigDecimal("0.00");
			//满减优惠总金额
			BigDecimal dishPreferentialCashSum = new BigDecimal("0.00");
			//0-折扣，
			if (market.getMarketType() == 0) {
				dishPreferentialSum = marketCost
						.subtract(marketCost.multiply(new BigDecimal(market.getRate())));
			}
			//1-满减
			else if (market.getMarketType() == 1) {
				dishPreferentialSum = market.getCash();
				tempDishPreferentialSum = market.getCash();
			}
			boolean isHave = false;
			int     size   = dishList.size();
			for (int i = 0; i < size; i++) {
				Dish dish = dishList.get(i);
				if (dish != null && dish.isJoinMarket()) {
					List<MarketObject> ishLists = dish.marketList;
					if (ishLists != null && ishLists.size() > 0) {
						int sizel = ishLists.size();
						for (int j = 0; j < sizel; j++) {
							MarketObject moo = ishLists.get(j);
							if (moo.getMarketName().equals(market.getMarketName())) {
								isHave = true;
								break;
							}
						}
					}
					if (!isHave) {
						//0-折扣，
						if (market.getMarketType() == 0) {
							BigDecimal dishTotalCost = dish.getOrderDishCost()
									.multiply(new BigDecimal(dish.getQuantity()));
							//单项菜品优惠金额
							BigDecimal meanDishPreferential = dishTotalCost
									.multiply(dishPreferentialSum)
									.divide(marketCost, 3, BigDecimal.ROUND_HALF_UP);
							dish.setCost(dish.getOrderDishCost().subtract(meanDishPreferential
									.divide(new BigDecimal(dish
											.getQuantity()), 3, BigDecimal.ROUND_HALF_UP)));
							setMarketObjectList(dish, market, meanDishPreferential);
							dish.setHava(true);
						}
						//1-满减
						else if (market.getMarketType() == 1) {
							// 单项菜品的价格
							BigDecimal dishTotalCost = dish.getOrderDishCost()
									.multiply(new BigDecimal(dish.getQuantity()));
							//单项菜品的优惠金额
							BigDecimal meanDishPreferential = dishTotalCost
									.multiply(dishPreferentialSum)
									.divide(marketCost, 2, BigDecimal.ROUND_HALF_UP);
							dish.setCost(dish.getOrderDishCost().subtract(meanDishPreferential
									.divide(new BigDecimal(dish
											.getQuantity()), 2, BigDecimal.ROUND_HALF_UP)));
							dishPreferentialCashSum = dishPreferentialCashSum.add(dish.getCost()
									.multiply(new BigDecimal(dish.getQuantity())));
							dish.setHava(true);
							if (size - 1 == i)//最后一个菜
							{
								tempDish = dish;
							} else {
								setMarketObjectList(dish, market, meanDishPreferential);
								tempDishPreferentialSum = tempDishPreferentialSum
										.subtract(meanDishPreferential);
							}
						}
					}

				}
			}
			if (!isHave && market.getMarketType() == 1) {
				BigDecimal price = marketCost.subtract(dishPreferentialSum);//参加完优惠活动后应该是这个价钱
				tempDish.setCost(tempDish.getCost().add(price.subtract(dishPreferentialCashSum)));
				setMarketObjectList(tempDish, market, tempDishPreferentialSum);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private static void setMarketObjectList(Dish dish, Market market, BigDecimal marketDishCost) {
		if (dish.getMarketList() == null) {
			dish.marketList = new ArrayList<>();
		}
		boolean isHave = false;
		MarketObject marketObject = new MarketObject(market
				.getMarketName(), marketDishCost, MarketType.SALES);
		List<MarketObject> dishList = dish.marketList;
		int                size     = dishList.size();
		for (int i = 0; i < size; i++) {
			MarketObject mo = dishList.get(i);
			if (mo.getMarketName().equals(market.getMarketName())) {
				isHave = true;
				break;
			}
		}
		//如果添加过该促销方案则不添加  避免重复添加
		if (!isHave) {
			dish.marketList.add(marketObject);
		}
	}

	private static void setDishIdList(List<Dish> dishList) {
		try {
			if (dishList != null && dishList.size() > 0) {
				dishIdList.clear();
				int          size            = dishList.size();
				MarketObject marketObjectAll = null;
				for (int i = 0; i < size; i++) {
					Dish dish = dishList.get(i);
					dish.setHava(false);
					if (dish != null) {
						synchronized (dish) {
							if (dish.getMarketList() != null && dish.getMarketList().size() > 0) {
								List<MarketObject> dishMarketList = dish.getMarketList();
								synchronized (dishMarketList) {
									if (dishMarketList != null && dishMarketList.size() > 0) {
										ToolsUtils
												.removeItemForMarkType(dishMarketList, MarketType.SALES);
										dish.setCost(dish.getDishPrice());
										dish.setTempPrice(dish.getDishPrice());
										dishIdList.add(dishList.get(i).getDishId());
									}
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 适配营销活动
	 *
	 * @param dishList
	 * @param orderMoney
	 * @return
	 */
	private static List<Integer> dishIdList = new CopyOnWriteArrayList<>();

	/**
	 * @param dishList   菜品列表
	 * @param orderMoney 菜品的金额
	 * @return
	 */
	public static List<Dish> adapterMarket(List<Dish> dishList, float orderMoney) {
		Menu         dishMenu       = getDishs(DISH_TYPE);
		boolean      isSpiltDish    = false;
		List<Dish>   singleItemList = new ArrayList<Dish>();
		List<Market> marketLists    = new CopyOnWriteArrayList<>();
		if (dishMenu != null) {
			List<Dish> dishs = new ArrayList<>();
			if (dishMenu != null) {
				dishs = dishMenu.getDishData();//获取当前时段全部菜品
				if (dishs == null) {
					return dishList;
				}
				if (dishId2DishMap == null || dishId2DishMap.size() <= 0) {
					dishId2DishMap = getDishIdforDishMap(dishs);
				}
				boolean isMemberOnly = false;//是否只限会员
				setDishIdList(dishList);
				if (!isListNull(dishList) && !isListNull(marketList))//必须有菜品并且必须有优惠方案
				{
					marketLists = handleList(marketList);//筛选可执行的优惠方案
					//遍历可执行的优惠方案列表
					isSpiltDish = true;
					int itemIndex = 1;
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
					for (Market market : marketLists) {
						//该优惠方案必须是开启状态
						if (market.isStatus()) {
							BigDecimal marketCost         = new BigDecimal("0");//参与活动的菜品总金额
							List<Dish> joinMarketDishList = new CopyOnWriteArrayList<>();//参与活动的菜品List
							joinMarketDishList.clear();//先将参与活动的菜品清空
							//单品打折 或者是单品优惠特价
							if (market.getTriggerType() == 2) {
								//单品折扣
								if (market.getMarketType() == 0) {
									updateCostDisCount(singleItemList, market);
								}
								//单品优惠售价
								else if (market.getMarketType() == 1) {
									updateCostSpecial(singleItemList, market);
								}
								//有买有送
								else if (market.getMarketType() == 2) {
									updateCostGift(singleItemList, market);
								}
								//单品第二杯活动
								else if (market.getMarketType() == 3) {
									//单品第二杯折扣
									if (market.getTheSecondType() == 0) {
										updateCostSecondDisCount(singleItemList, market);
									}
									//单品第二杯特价
									else if (market.getTheSecondType() == 1) {
										updateCostSecondSpecial(singleItemList, market);
									}
								}
							}
							if (market.getTriggerType() == 0)//满减活动
							{
								updateCostFullReductionl(singleItemList, market);
							}
						}
					}
				}
			}
		}
		if (isSpiltDish) {
			dishList = ToolsUtils.cloneTo(singleItemList);
			for (Dish item : singleItemList) {
				if (item.getMarketList() == null || item.getMarketList().isEmpty()) {
					item.setCost(item.getCost().multiply(new BigDecimal(item.getQuantity())));
				}
			}

			List<Dish> dishItem = new ArrayList<Dish>();
			for (Dish item : singleItemList) {
				Dish               singleItem  = ToolsUtils.cloneTo(item);
				BigDecimal         itemCost    = item.getCost();
				boolean            isSame      = false;
				boolean            sameOption  = true;//默认是相同的
				List<Option>       options     = item.getOptionList();
				List<Dish.Package> itemSubList = item.getSubItemList();
				for (Dish bean : dishItem) {
					// 判断是不是套餐，如果是套餐比较套餐项；
					// 普通菜品就比较定制项
					List<Dish.Package> beanSubList = bean.getSubItemList();
					if (beanSubList == null || beanSubList.isEmpty()) {
						List<Option> list1 = bean.getOptionList();
						if (options != null && list1 != null)
							sameOption = equalOption(options, list1);
						if (item.getDishId() == bean.getDishId() && item.getCost()
								.equals(bean.getCost()) && item.getAllOrderDisCountSubtractPrice()
								.equals(bean.getAllOrderDisCountSubtractPrice()) && item
								.getDishSubAllOrderDisCount()
								.equals(bean.getDishSubAllOrderDisCount()) && sameOption) {
							//这是相同项的    这里隐藏的意思是点击菜品项的时候
							//                            isSame = true;
							//                            bean.setQuantity(bean.getQuantity() + 1);
							break;
						}
					} else {
						if (item.getDishId() == bean.getDishId() && item.getCost()
								.equals(bean.getCost())) {
							if (itemSubList != null && !itemSubList.isEmpty()) {
								// 需要比较两个套餐的套餐项是否都一样
								if (sameItemList(itemSubList, beanSubList)) {
									//这是相同项的
									//                                    isSame = true;
									//                                   bean.setQuantity(bean.getQuantity() + 1);
									//                                    bean.setTempQuantity(bean.getTempQuantity() + 1);
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
			for (Dish d : dishItem) {
				List<MarketObject> marklist = d.getMarketList();
				if (!ToolsUtils.isList(marklist)) {
					for (MarketObject mo : marklist) {
						if (mo.getMarketType() == MarketType.SALES || mo
								.getMarketType() == MarketType.DISCOUNT) {
							mo.setReduceCash(mo.getReduceCash()
									.multiply(new BigDecimal(d.getQuantity())));
						}
					}
				}
			}
			dishList = ToolsUtils.cloneTo(dishItem);
		}
		return dishList;
	}

	public static boolean equalOption(List<Option> list1, List<Option> list2) {
		if (list1.size() != list2.size())
			return false;
		for (Option object : list1) {
			if (!list2.contains(object))
				return false;
		}
		return true;
	}

	private static boolean sameItemList(List<Dish.Package> list1, List<Dish.Package> list2) {
		if (list1.size() != list2.size())
			return false;
		for (Dish.Package item1 : list1) {
			boolean exist = false;
			for (Dish.Package item2 : list2) {
				if (item1.getDishId() == item2.getDishId() && item1.getItemPrice() != null && item2
						.getItemPrice() != null && item1.getItemPrice()
						.equals(item2.getItemPrice())) {
					exist = true;
				}
			}
			if (!exist)
				return false;
		}

		return true;
	}


	/**
	 * 单品折扣
	 *
	 * @param itemList
	 * @param market
	 */
	private static void updateCostDisCount(List<Dish> itemList, Market market) {
		// 之前已经把所有的订单项进行了拆分，这里需要重新统计是否符合条件中的菜品数量
		ArrayMap<Long, List<Dish>> itemMap = new ArrayMap<Long, List<Dish>>();
		for (Dish item : itemList) {
			if (!item.getUsingMarket() && market.getTriggerDishList().contains(item.getDishId())) {
				// 该品项符合条件，进入统计
				Long dishId = Long.valueOf(item.getDishId() + "");
				if (itemMap.containsKey(dishId)) {
					itemMap.get(dishId).add(item);
				} else {
					List<Dish> list = new ArrayList<Dish>();
					list.add(item);
					itemMap.put(dishId, list);
				}
			}
		}

		if (market.getTriggerDishList() != null && market.getTriggerDishList().size() > 0) {
			// 活动条件中的数量
			int triggerDishCount = market.getTriggerDishCount();
			if (triggerDishCount < 1)
				return;

			for (Long key : itemMap.keySet()) {
				List<Dish> items = itemMap.get(key);
				if (items.size() >= triggerDishCount) {
					// 里面的品项都满足方案的条件，可以进行促销处理
					for (Dish item : items) {
						BigDecimal marketDishCost = item.getDishPrice()
								.multiply(market.getRateBig())
								.setScale(2, BigDecimal.ROUND_HALF_UP);
						BigDecimal marketPrice = item.getDishPrice().subtract(marketDishCost);
						item.setCost(marketDishCost);
						setMarketObjectList(item, market, marketPrice);
					}
				}
			}
		}
	}

	/**
	 * 单品第二杯特价
	 *
	 * @param itemList
	 * @param market
	 */
	private static void updateCostSecondSpecial(List<Dish> itemList, Market market) {
		// 实现的思路如下：之前已经把各个菜品项拆分为一个一个独立项目；然后先建立条件列表和可执行列表；
		// 在条件列表中找出满足方案条件的品项；在可执行列表中找出可执行项；进行方案的处理；
		// 然后递归，重新来一遍，直到没有可以再次执行的情况为止。

		// 符合条件的品项列表
		List<Dish> triggerItemList = new ArrayList<Dish>();
		// 可以执行方案的品项列表
		List<Dish> executeItemList = new ArrayList<Dish>();

		// 分别生成条件和执行列表
		for (Dish item : itemList) {
			if (item.getUsingMarket() || item.isHava()) {
				// 品项已经参与过其他的方案，不在参与该方案
				continue;
			}

			if (market.getTriggerDishList().contains(item.getDishId()))
				triggerItemList.add(item);
			if (market.getMarketDishList().contains(item.getDishId()))
				executeItemList.add(item);
		}

		// 对两个列表进行排序！默认排序是价格由高到低
		Collections.sort(triggerItemList, new Comparator<Dish>() {
			@Override
			public int compare(Dish o1, Dish o2) {
				return o2.getDishPrice().compareTo(o1.getDishPrice());
			}
		});
		Collections.sort(executeItemList, new Comparator<Dish>() {
			@Override
			public int compare(Dish o1, Dish o2) {
				return o1.getDishPrice().compareTo(o2.getDishPrice());
			}
		});

		// 找到符合条件的菜品品项；因为有数量上的要求，所以需要使用列表来控制！
		int        triggerDishCount    = market.getTriggerDishCount();
		List<Dish> realTriggerDishList = new ArrayList<Dish>();
		for (Dish item : triggerItemList) {
			if (realTriggerDishList.isEmpty())
				realTriggerDishList.add(item);
			else {
				// 判断品项与之前的是否相同，如果不同，说明之前的品项已经不和要求
				boolean isSame = true;
				for (Dish triggerItem : realTriggerDishList) {
					if (triggerItem.getDishId() != item.getDishId()) {
						isSame = false;
						break;
					}
				}
				if (!isSame)
					realTriggerDishList.clear();
				realTriggerDishList.add(item);
			}

			if (realTriggerDishList.size() == triggerDishCount)
				break;
		}

		if (realTriggerDishList.size() == triggerDishCount) {
			// 找到符合条件的品项了，下面查找可以执行的品项
			Dish executeItem = null;
			for (Dish canExecuteItem : executeItemList) {
				// 可以执行的品项，不能与条件中的品项是同一个
				boolean isInTrigger = false;
				for (Dish triggerItem : realTriggerDishList) {
					if (triggerItem.getItemIndex() == canExecuteItem.getItemIndex()) {
						isInTrigger = true;
						break;
					}
				}
				if (isInTrigger)
					continue; // 这个包含在了条件之中，不能执行方案，继续找
				else {
					executeItem = canExecuteItem;
					break;
				}
			}

			if (executeItem == null) {
				// 没有可以执行的品项了， 直接返回
				return;
			} else {
				// 对可以执行的品项进行方案的处理
				BigDecimal marketDishCost = executeItem.getDishPrice()
						.subtract(market.getTheSecondPrice());
				executeItem.setCost(market.getTheSecondPrice());
				setMarketObjectList(executeItem, market, marketDishCost);

				for (Dish triggerItem : realTriggerDishList)
					triggerItem.setHava(true);

				// 然后递归
				updateCostSecondSpecial(itemList, market);
			}
		} else {
			// 没有符合条件的品项，直接返回
			return;
		}
	}

	/**
	 * 单品买赠
	 *
	 * @param itemList
	 * @param market
	 */
	private static void updateCostGift(List<Dish> itemList, Market market) {
		// 实现的思路如下：之前已经把各个菜品项拆分为一个一个独立项目；然后先建立条件列表和可执行列表；
		// 在调价列表中找出满足方案条件的品项；在可执行列表中找出可执行项；进行方案的处理；
		// 然后递归，重新来一遍，直到没有可以再次执行的情况为止。

		// 符合条件的品项列表
		List<Dish> triggerItemList = new ArrayList<Dish>();
		// 可以执行方案的品项列表
		List<Dish> executeItemList = new ArrayList<Dish>();

		// 分别生成条件和执行列表
		for (Dish item : itemList) {
			if (item.getUsingMarket() || item.isHava()) {
				// 品项已经参与过其他的方案，不在参与该方案
				continue;
			}

			if (market.getTriggerDishList().contains(item.getDishId()))
				triggerItemList.add(item);
			if (market.getMarketDishList().contains(item.getDishId()))
				executeItemList.add(item);
		}

		// 对两个列表进行排序！默认排序是价格由高到低
		Collections.sort(triggerItemList, new Comparator<Dish>() {
			@Override
			public int compare(Dish o1, Dish o2) {
				return o2.getDishPrice().compareTo(o1.getDishPrice());
			}
		});
		Collections.sort(executeItemList, new Comparator<Dish>() {
			@Override
			public int compare(Dish o1, Dish o2) {
				return o1.getDishPrice().compareTo(o2.getDishPrice());
			}
		});

		// 找到符合条件的菜品品项；因为有数量上的要求，所以需要使用列表来控制！
		int        triggerDishCount    = market.getTriggerDishCount();
		List<Dish> realTriggerDishList = new ArrayList<Dish>();
		for (Dish item : triggerItemList) {
			if (realTriggerDishList.isEmpty())
				realTriggerDishList.add(item);
			else {
				// 判断品项与之前的是否相同，如果不同，说明之前的品项已经不和要求
				boolean isSame = true;
				for (Dish triggerItem : realTriggerDishList) {
					if (triggerItem.getDishId() != item.getDishId()) {
						isSame = false;
						break;
					}
				}
				if (!isSame)
					realTriggerDishList.clear();
				realTriggerDishList.add(item);
			}

			if (realTriggerDishList.size() == triggerDishCount)
				break;
		}

		if (realTriggerDishList.size() == triggerDishCount) {
			// 找到符合条件的品项了，下面查找可以执行的品项
			List<Dish> executeItem = null;
			executeItem = new ArrayList<>();
			for (Dish canExecuteItem : executeItemList) {
				// 可以执行的品项，不能与条件中的品项是同一个
				boolean isInTrigger = false;
				for (Dish triggerItem : realTriggerDishList) {
					if (triggerItem.getItemIndex() == canExecuteItem.getItemIndex()) {
						isInTrigger = true;
						break;
					}
				}
				if (isInTrigger)
					continue; // 这个包含在了条件之中，不能执行方案，继续找
				else {
					if (executeItem != null && !executeItem.isEmpty() && executeItem
							.size() == market.getGiftDishCount()) {
						break;
					}
					executeItem.add(canExecuteItem);
					continue;
				}
			}

			if (executeItem == null || executeItem.isEmpty()) {
				// 没有可以执行的品项了， 直接返回
				return;
			} else {
				for (Dish Dish : executeItem) {
					// 对可以执行的品项进行方案的处理
					BigDecimal marketDishCost = new BigDecimal(0); // 赠送了，就是价格为0
					BigDecimal marketPrice    = Dish.getDishPrice().subtract(marketDishCost);
					Dish.setCost(marketDishCost);
					setMarketObjectList(Dish, market, marketPrice);
					for (Dish triggerItem : realTriggerDishList)
						triggerItem.setHava(true);
					// 然后递归
					updateCostGift(itemList, market);
				}
			}
		} else {
			// 没有符合条件的品项，直接返回
			return;
		}
	}

	/**
	 * 单品第二杯打折
	 *
	 * @param itemList
	 * @param market
	 */
	private static void updateCostSecondDisCount(List<Dish> itemList, Market market) {
		// 实现的思路如下：之前已经把各个菜品项拆分为一个一个独立项目；然后先建立条件列表和可执行列表；
		// 在调价列表中找出满足方案条件的品项；在可执行列表中找出可执行项；进行方案的处理；
		// 然后递归，重新来一遍，直到没有可以再次执行的情况为止。

		// 符合条件的品项列表
		List<Dish> triggerItemList = new ArrayList<Dish>();
		// 可以执行方案的品项列表
		List<Dish> executeItemList = new ArrayList<Dish>();

		// 分别生成条件和执行列表
		for (Dish item : itemList) {
			if (item.getUsingMarket() || item.isHava()) {
				// 品项已经参与过其他的方案，不在参与该方案
				continue;
			}

			if (market.getTriggerDishList().contains(item.getDishId()))
				triggerItemList.add(item);
			if (market.getMarketDishList().contains(item.getDishId()))
				executeItemList.add(item);
		}

		// 对两个列表进行排序！默认排序是价格由高到低
		Collections.sort(triggerItemList, new Comparator<Dish>() {
			@Override
			public int compare(Dish o1, Dish o2) {
				return o2.getDishPrice().compareTo(o1.getDishPrice());
			}
		});
		Collections.sort(executeItemList, new Comparator<Dish>() {
			@Override
			public int compare(Dish o1, Dish o2) {
				return o1.getDishPrice().compareTo(o2.getDishPrice());
			}
		});

		// 找到符合条件的菜品品项；因为有数量上的要求，所以需要使用列表来控制！
		int        triggerDishCount    = market.getTriggerDishCount();
		List<Dish> realTriggerDishList = new ArrayList<Dish>();
		for (Dish item : triggerItemList) {
			if (realTriggerDishList.isEmpty())
				realTriggerDishList.add(item);
			else {
				// 判断品项与之前的是否相同，如果不同，说明之前的品项已经不和要求
				boolean isSame = true;
				for (Dish triggerItem : realTriggerDishList) {
					if (triggerItem.getDishId() != item.getDishId()) {
						isSame = false;
						break;
					}
				}
				if (!isSame)
					realTriggerDishList.clear();
				realTriggerDishList.add(item);
			}

			if (realTriggerDishList.size() == triggerDishCount)
				break;
		}

		if (realTriggerDishList.size() == triggerDishCount) {
			// 找到符合条件的品项了，下面查找可以执行的品项
			Dish executeItem = null;
			for (Dish canExecuteItem : executeItemList) {
				// 可以执行的品项，不能与条件中的品项是同一个
				boolean isInTrigger = false;
				for (Dish triggerItem : realTriggerDishList) {
					if (triggerItem.getItemIndex() == canExecuteItem.getItemIndex()) {
						isInTrigger = true;
						break;
					}
				}
				if (isInTrigger)
					continue; // 这个包含在了条件之中，不能执行方案，继续找
				else {
					executeItem = canExecuteItem;
					break;
				}
			}

			if (executeItem == null) {
				// 没有可以执行的品项了， 直接返回
				return;
			} else {
				// 对可以执行的品项进行方案的处理
				BigDecimal marketDishCost = executeItem.getDishPrice()
						.multiply(market.getTheSecondRate()).setScale(2, BigDecimal.ROUND_HALF_UP);
				BigDecimal marketPrice = executeItem.getDishPrice().subtract(marketDishCost);
				executeItem.setCost(marketDishCost);
				setMarketObjectList(executeItem, market, marketPrice);

				for (Dish triggerItem : realTriggerDishList)
					triggerItem.setHava(true);

				// 然后递归
				updateCostSecondDisCount(itemList, market);
			}
		} else {
			// 没有符合条件的品项，直接返回
			return;
		}
	}

	/**
	 * 单品特价
	 *
	 * @param itemList
	 * @param market
	 */
	private static void updateCostSpecial(List<Dish> itemList, Market market) {
		// 之前已经把所有的订单项进行了拆分，这里需要重新统计是否符合条件中的菜品数量
		Map<Long, List<Dish>> itemMap = new ArrayMap<Long, List<Dish>>();
		for (Dish item : itemList) {
			if (!item.getUsingMarket() && market.getTriggerDishList().contains(item.getDishId())) {
				// 该品项符合条件，进入统计
				Long dishId = Long.valueOf(item.getDishId() + "");
				if (itemMap.containsKey(dishId)) {
					itemMap.get(dishId).add(item);
				} else {
					List<Dish> list = new ArrayList<Dish>();
					list.add(item);
					itemMap.put(dishId, list);
				}
			}
		}

		if (market.getTriggerDishList() != null && market.getTriggerDishList().size() > 0) {
			// 活动条件中的数量
			int triggerDishCount = market.getTriggerDishCount();
			if (triggerDishCount < 1)
				return;

			for (Long key : itemMap.keySet()) {
				List<Dish> items = itemMap.get(key);
				if (items.size() >= triggerDishCount) {
					// 里面的品项都满足方案的条件，可以进行促销处理
					for (Dish item : items) {
						BigDecimal marketDishCost = item.getDishPrice().subtract(market.getCash());
						item.setCost(market.getCash());
						setMarketObjectList(item, market, marketDishCost);
					}
				}
			}
		}
	}

	/**
	 * 全单满减
	 *
	 * @param itemList
	 * @param market
	 */
	private static void updateCostFullReductionl(List<Dish> itemList, Market market) {
		BigDecimal marketCost         = new BigDecimal("0");//参与活动的菜品总金额
		List<Dish> joinMarketDishList = new CopyOnWriteArrayList<>();//参与活动的菜品List
		joinMarketDishList.clear();//先将参与活动的菜品清空
		List<Integer>           marketDishList = market.getMarketDishList();//参与方案的菜品Id
		ArrayMap<Integer, Dish> marketDishMap  = new ArrayMap<>();
		for (Integer dishId : marketDishList) {
			if (dishId != null && dishId2DishMap.get(dishId) != null) {
				marketDishMap.put(dishId, dishId2DishMap.get(dishId));//先把参与方案的菜品用map存起来做一个映射,方便下面使用
			}
		}

		BigDecimal dishPriceSum = new BigDecimal("0.000");//参加促销活动菜品的总price  最后要拿这个钱来算比例
		int        dishQuantity = 0;
		for (Dish dish : itemList) {
			dish.setJoinMarket(false);//先将全部菜品设为不参与活动
			if (marketDishMap.get(dish.getDishId()) != null)//如果当前菜品在marketDishMap内则为参与打折的菜品
			{
				BigDecimal dishPrice = dish.getOrderDishCost()
						.multiply(new BigDecimal(dish.getQuantity()));
				dishPriceSum = dishPriceSum.add(dishPrice);
				marketCost = marketCost.add(dishPrice);
				dish.setJoinMarket(true);
				dishQuantity += dish.getQuantity();
				joinMarketDishList.add(dish);
			}
		}
		//参与活动的菜品总金额大于或等于后台设置的满减额度
		if (marketCost.compareTo(new BigDecimal(market.getFullCash())) != -1 && joinMarketDishList
				.size() > 0) {
			addMarketList(itemList, market, joinMarketDishList, dishPriceSum, marketCost);
		}
	}


	private static boolean isAddParticipationMarketDish(List<Integer> participationActionDishIdList, Dish dish) {
		boolean isHave = false;
		if (participationActionDishIdList != null && participationActionDishIdList.size() > 0) {
			for (Integer participationDishId : participationActionDishIdList) {
				if ((int) participationDishId == dish.getDishId()) {
					isHave = true;
				}
			}
		}
		return isHave;
	}

	/**
	 * 根据菜品Id在购物车中寻找菜品对象
	 *
	 * @param dishList
	 * @param dishId
	 * @return
	 */
	private static Dish getDishListForDish(List<Dish> dishList, Integer dishId) {
		if (!isListNull(dishList)) {
			for (Dish dish : dishList) {
				if (dish.getDishId() == (int) dishId) {
					return dish;
				}
			}
		}
		return null;
	}


	/**
	 * @return true 空
	 */
	public static boolean isListNull(List list) {
		if (list == null || list.size() == 0) {
			return true;
		} else {
			return false;
		}
	}

	//检出在有效时间段内的活动
	private static List<Market> handleList(List<Market> list) {
		List<Market> actlist = new ArrayList<>();

		for (Market market : list) {

			boolean dateFlag = true;
			boolean weekFlag = true;
			boolean timeFlag = true;

			if ((!market.isDateAvailable()) && (!market.isTimeAvailable()) && (!market
					.isWeekAvailable())) {
				actlist.add(market);//永久特价不用判断时间段
			} else {
				long   curTime = System.currentTimeMillis();
				long   sdate   = market.getStartDate();
				long   edate   = market.getEndDate();
				String hours   = TimeUtil.getHour();//当前小时
				String shours  = market.getStartTime();
				String ehours  = market.getEndTime();

				if (market.isDateAvailable()) {
					if (curTime < sdate || curTime > edate) //不在日期时间段内
						dateFlag = false;
				}

				if (market.isWeekAvailable() && dateFlag) {
					String week = market.getWeek();
					if (!week.contains(TimeUtil.getWeekOfDate())) //如果当天不在设置的时间段内
						weekFlag = false;
				}

				// FIXME 后台不判断时间,留给POS或者点餐机判断
				if (market.isTimeAvailable() && dateFlag && timeFlag) {
					if (TimeUtil.compareData(hours, shours) == -1 || TimeUtil
							.compareData(ehours, hours) == -1) //不在营销时间内
						timeFlag = false;
				}
				if (dateFlag && weekFlag && timeFlag)
					actlist.add(market);// 时间检测通过 , 符合要求
			}
		}
		PosInfo      posInfo      = PosInfo.getInstance();
		List<Market> memberMarket = new ArrayList<>();
		if (!ToolsUtils.isList(actlist)) {
			for (Market market : actlist) {
				if (posInfo.getAccountMember() != null) {
					if (market.isCommonExecute()) {
						if (market.isGrade()) {
							long       grade = posInfo.getAccountMember().getGrade();
							List<Long> list1 = market.getGradeList();
							if (!list1.contains(grade)) {
								continue;
							}
							memberMarket.add(market);
						}
					} else {
						continue;
					}
				} else {
					if (market.isGrade()) {
						continue;
					}
					memberMarket.add(market);
				}
			}
		}
		actlist = ToolsUtils.cloneTo(memberMarket);
		sortMarket(actlist);
		Log.i("门店启用的营销方案:========>>", ToolsUtils.getPrinterSth(actlist));
		return actlist;
	}


	private static void sortMarket(List<Market> marketList) {
		Collections.sort(marketList, new Comparator<Market>() {
			@Override
			public int compare(Market a1, Market a2) {
				Market m1 = a1;
				Market m2 = a2;

				int triggerType1 = m1.getTriggerType();
				int triggerType2 = m2.getTriggerType();

				if (triggerType1 == 2 && triggerType2 == 2) {
					//如果单品的促销类型相同，按照 第二份特价>有买有送>优惠金额>折扣 优先第二份特价的方案
					if (m1.getMarketType() == m2.getMarketType()) {
						//第二份特价
						if (m1.getMarketType() == 3 && m2.getMarketType() == 3) {
							//如果第二份类型相同，选择最大额度的
							if (m1.getTheSecondType() == m2.getTheSecondType() && m1
									.getTheSecondType() == 1) {
								return m1.getTheSecondPrice().compareTo(m2.getTheSecondPrice());
							}
							if (m1.getTheSecondType() == m2.getTheSecondType() && m1
									.getTheSecondType() == 0) {
								return m1.getTheSecondRate().compareTo(m2.getTheSecondRate());
							}
							return m2.getTheSecondType() - m1.getTheSecondType();
						}
						//买赠
						if (m1.getMarketType() == 2 && m2.getMarketType() == 2) {
							return m2.getGiftDishCount() - m1.getGiftDishCount();
						}
						//优惠价
						if (m1.getMarketType() == 1 && m2.getMarketType() == 1) {
							return m1.getCash().compareTo(m2.getCash());
						}
						//折扣
						if (m1.getMarketType() == 0 && m2.getMarketType() == 0) {
							return compareNum(m1.getRate(), m2.getRate());
						}
					}
					// 两个都是单品的优惠方案，那么按照 marketType 排序
					return m2.getMarketType() - m1.getMarketType();
				}
				if (triggerType1 == 0 && triggerType2 == 0) {
					// 两个都是全单方案，按照 全单条件中金额大的优先
					BigDecimal fullCash1 = new BigDecimal(m1.getFullCash() + "");
					BigDecimal fullCash2 = new BigDecimal(m2.getFullCash() + "");
					//满减
					if (m1.getMarketType() == 1 && m2.getMarketType() == 1) {
						return m2.getCash().compareTo(m1.getCash());
					}
					//折扣
					if (m1.getMarketType() == 0 && m2.getMarketType() == 0) {
						return compareNum(m1.getRate(), m2.getRate());
					}
					return fullCash2.compareTo(fullCash1);
				}

				// 单品的优惠方案排在前面
				return triggerType2 - triggerType1;
			}
		});
	}

	private static int compareNum(float c1, float c2) {
		int num = 0;
		if (c1 == c2) {
			num = 0;
		} else if (c1 > c2) {
			num = 1;
		} else if (c1 < c2) {
			num = -1;
		}
		return num;
	}


	public static List<DishDiscount> getDishDiscount(Dish dish, List<MarketingActivity> marketingActivities) {
		List<DishDiscount> dishDiscount = new ArrayList<>();
		if (dish != null) {
			if (marketingActivities != null && marketingActivities.size() > 0) {
				//折扣
				for (MarketingActivity activitys : marketingActivities) {
					BigDecimal dishCost = new BigDecimal("0.00");
					BigDecimal dishPrice = dish.getOrderDishCost()
							.subtract(dish.getAllOrderDisCountSubtractPrice());
					DishDiscount discount = null;
					boolean      isEnable = true;
					if (activitys.getDiscountType() == 0) {
						dishCost = dishPrice.multiply(activitys.getDiscountRate())
								.setScale(2, BigDecimal.ROUND_HALF_UP);
						discount = new DishDiscount(activitys.getDiscountName(), dishCost
								.toString(), activitys.getDiscountType(), isEnable);
					} else if (activitys.getDiscountType() == 1) {
						if (dishPrice.compareTo(activitys.getDiscountAmount()) == -1) {
							isEnable = false;
							dishCost = dishPrice;
							discount = new DishDiscount(activitys.getDiscountName(), dishCost
									.toString(), activitys.getDiscountType(), isEnable);
							discount.setReductiMoney(dishPrice);
							dishDiscount.add(discount);
							continue;
						}
						dishCost = dishPrice.subtract(activitys.getDiscountAmount());
						discount = new DishDiscount(activitys.getDiscountName(), dishCost
								.toString(), activitys.getDiscountType(), isEnable);
						discount.setReductiMoney(activitys.getDiscountAmount());
					}
					dishDiscount.add(discount);
				}
			}
		}
		return dishDiscount;
	}

	public static List<Dish> scanQuickOrder(String qrCodeDishStr) {
		List<Dish> scanDishList = null;
		try {
			Menu dishMenu = getDishs(DISH_TYPE);
			if (dishMenu != null) {
				List<Dish> dishs = new ArrayList<>();
				scanDishList = new ArrayList<>();
				if (dishMenu != null) {
					dishs = dishMenu.getDishData();//获取当前时段全部菜品
					if (dishs == null) {
						return scanDishList;
					}
					if (dishId2DishMap == null || dishId2DishMap.size() <= 0) {
						dishId2DishMap = getDishIdforDishMap(dishs);
					}
					if (!TextUtils.isEmpty(qrCodeDishStr)) {
						int bPosition = qrCodeDishStr.indexOf("b");
						int ePosition = qrCodeDishStr.indexOf("e");
						if (bPosition != -1 || ePosition != -1) {
							qrCodeDishStr = qrCodeDishStr.replace("b", "").replace("e", "");
							String dishStr[] = qrCodeDishStr.split("a");
							for (String dishInfoArr : dishStr) {
								//1|6*1#(1379+1378)
								String dishInfo[] = dishInfoArr.split("\\|");
								//6*1#(1379+1378)
								String itemDish = dishInfo[1];
								//6
								String dishId = itemDish.split("x")[0];
								switch (Integer.valueOf(dishInfo[0])) {
									//单项菜品
									case 0:
										//1
										String dishCount0 = itemDish.split("x")[1];
										Dish dish = dishId2DishMap.get(Integer.valueOf(dishId));
										if (dish != null) {
											dish.setQuantity(Integer.valueOf(dishCount0));
										}
										scanDishList.add(dish);
										break;
									//单项菜品后面带有定制项
									case 1:
										//1#(1379+1378)
										String dishDetailInfo = itemDish.split("x")[1];
										//1
										String dishCount = dishDetailInfo.split("c")[0];
										//1379+1378
										String dishOption = dishDetailInfo.split("c")[1]
												.replace("q", "").replace("w", "");
										String dishOptionArr[] = dishOption.split("r");
										List<String> dishOptionList = Arrays.asList(dishOptionArr);
										Dish dish1 = dishId2DishMap.get(Integer.valueOf(dishId));
										if (dish1 != null) {
											dish1.setQuantity(Integer.valueOf(dishCount));
											dish1.optionList = new ArrayList<>();
											if (dish1.getOptionCategoryList() != null && dish1
													.getOptionCategoryList() != null) {
												for (OptionCategory optionCategory : dish1
														.getOptionCategoryList()) {
													if (optionCategory
															.getOptionList() != null && optionCategory
															.getOptionList().size() > 0) {
														int size = optionCategory.getOptionList()
																.size();
														for (int i = 0; i < size; i++) {
															Option option = optionCategory
																	.getOptionList().get(i);
															if (dishOptionList.contains(option
																	.getId() + "")) {
																Integer dishOptionId = TimeUtil
																		.getStringData() + i;
																dish1.setDishOptionId(dishOptionId);
																option.setSelect(true);
																DishOptionController
																		.addDishOption(dish1, null, dish1
																				.getOptionCategoryList(), option, true);
																dish1.optionList.add(option);
															}

														}
													}
												}
											}
										}
										scanDishList.add(dish1);
										break;
									//菜品是个套餐
									case 2:
										//2#[28+40+41+46+47+20]
										String dishPackageInfo = itemDish.split("x")[1];
										//2
										String dishPackageCount = dishPackageInfo.split("c")[0];
										//28+40+41+46+47+20
										String dishPackageItem = dishPackageCount.split("c")[1]
												.replace("q", "").replace("w", "");
										String dishPackageArr[] = dishPackageItem.split("r");
										List<String> dishPackageList = Arrays
												.asList(dishPackageArr);
										Dish dish2 = dishId2DishMap.get(Integer.valueOf(dishId));
										if (dish2 != null) {
											dish2.setQuantity(Integer.valueOf(dishPackageCount));
											if (dish2.packageItems != null && dish2.packageItems
													.size() > 0) {
												for (Dish.PackageItem packageItem : dish2.packageItems) {
													if (packageItem.options != null && packageItem.options
															.size() > 0) {
														for (Dish.Package package1 : packageItem.options) {
															if (dishPackageList
																	.contains(package1 + "")) {
																package1.quantity++;
																packageItem.quantity++;
															}
														}
													}
												}
											}
										}
										scanDishList.add(dish2);
										break;
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return scanDishList;
	}


}
