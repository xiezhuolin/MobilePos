package cn.acewill.mobile.pos.model.dish;

import android.text.TextUtils;

import org.greenrobot.eventbus.EventBus;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import cn.acewill.mobile.pos.common.DishDataController;
import cn.acewill.mobile.pos.common.DishOptionController;
import cn.acewill.mobile.pos.common.MarketDataController;
import cn.acewill.mobile.pos.config.MyApplication;
import cn.acewill.mobile.pos.config.Store;
import cn.acewill.mobile.pos.model.FetchOrder;
import cn.acewill.mobile.pos.model.MarketObject;
import cn.acewill.mobile.pos.model.MarketType;
import cn.acewill.mobile.pos.model.OrderStatus;
import cn.acewill.mobile.pos.model.event.PosEvent;
import cn.acewill.mobile.pos.model.order.Order;
import cn.acewill.mobile.pos.model.order.OrderItem;
import cn.acewill.mobile.pos.model.order.PaymentStatus;
import cn.acewill.mobile.pos.service.PosInfo;
import cn.acewill.mobile.pos.utils.Constant;
import cn.acewill.mobile.pos.utils.PriceUtil;
import cn.acewill.mobile.pos.utils.TimeUtil;
import cn.acewill.mobile.pos.utils.ToolsUtils;

import static cn.acewill.mobile.pos.common.MarketDataController.equalOption;
import static cn.acewill.mobile.pos.utils.ToolsUtils.cloneTo;


/**
 * Created by DHH on 2016/7/21.
 */
public class Cart {
	private static Cart cart = new Cart();

	public static Cart getInstance() {
		return cart;
	}

	private Cart() {
	}

	public static final int ADD_DISH          = 0;//加菜
	public static final int ADD_DISH_COUNT    = 1;//加菜数量
	public static final int REDUCE_DISH       = 2;//减菜
	public static final int REDUCE_DISH_COUNT = 3;//减菜数量
	private static int type;//加菜或减菜
	public static  String               tid              = "";
	public static  float                alreadyPaidMoney = (float) 0.0;// 已经通过团购券，或微信支付的钱
	public static  float                dmValue          = (float) 0.0;// 所使用的代金券金额
	public static  String               subtraction      = "0";
	public static  String               discount         = "1";
	public static  List<DishCount>      dishCountsList   = new ArrayList<DishCount>();
	private static List<ChangeListener> listeners        = new ArrayList<ChangeListener>();

	public static List<Dish> getDishItemList() {
		return dishItemList;
	}

	public static List<Dish> getDishItemMarketActList() {
		dishItemList = MarketDataController.adapterMarket(dishItemList, getPriceSum());
		return dishItemList;
	}

	/**
	 * 切换到外卖时  菜品price要取菜品外卖价格 waimaiPrice
	 */
	public static void switchWaiMaiPrice() {
		if (!ToolsUtils.isList(dishItemList)) {
			int size = dishItemList.size();
			for (int i = 0; i < size; i++) {
				Dish dish = dishItemList.get(i);
				dish.setPrice(dish.getWaimaiTempPrice());
				dish.setCost(dish.getWaimaiTempPrice());
			}
		}
		notifyContentChange();
	}

	/**
	 * 切换到堂食时  菜品price要切换回菜品的原价 price
	 */
	public static void switchEATINPrice() {
		if (!ToolsUtils.isList(dishItemList)) {
			int size = dishItemList.size();
			for (int i = 0; i < size; i++) {
				Dish dish = dishItemList.get(i);
				dish.setPrice(dish.getTemporaryPrice());
				dish.setCost(dish.getTemporaryPrice());
			}
		}
		notifyContentChange();
	}

	/**
	 * 会员结账时，改成会员价
	 */
	public static void changeToMemberPrice() {
		if (!ToolsUtils.isList(dishItemList)) {
			int size = dishItemList.size();
			for (int i = 0; i < size; i++) {
				Dish dish = dishItemList.get(i);
				dish.setPrice(dish.getMemberPrice());
				dish.setCost(dish.getMemberPrice());
			}
		}
		notifyContentChange();
	}
	//    public static void setDishItemTempMarketActList() {
	//        MarketDataController.adapterTempMarket(dishItemList);
	//        notifyContentChange();
	//    }

	public static void setDishItemList(List<Dish> dishItemLists) {
		dishItemList = dishItemLists;
		if (!ToolsUtils.isList(dishItemList)) {
			for (Dish dish : dishItemList) {
				dish.setTempQuantity(0);
				cleanAllOrderMarketState();
				DishDataController.addDishMark(dish.dishKind, dish);
			}
		}
	}

	/**
	 * 已点的菜品
	 */
	public static List<Dish> dishItemList = new CopyOnWriteArrayList<>();

	//挂单中的订单
	public static List<FetchOrder> handDishItemList = new CopyOnWriteArrayList<>();

	/**
	 * 将需要挂单的菜品列表加入到map中
	 *
	 * @param dishList
	 */
	public static void handDishList(List<Dish> dishList) {
		if (dishList != null && dishList.size() > 0) {
			FetchOrder fetchOrder = new FetchOrder();
			fetchOrder.setCreateOrderTime(TimeUtil.getTimeStr(System.currentTimeMillis()));
			fetchOrder.setFetchDishList(cloneTo(dishList));
			handDishItemList.add(fetchOrder);
			dishItemList.clear();
			EventBus.getDefault().post(new PosEvent(Constant.EventState.CLEAN_CART));//清空点菜列表数据
		}
	}

	/**
	 * 清空挂单列表
	 */
	public static void cleanHandDishList() {
		if (handDishItemList != null && handDishItemList.size() > 0) {
			handDishItemList.clear();
			EventBus.getDefault().post(new PosEvent(Constant.EventState.CLEAN_CART));//清空点菜列表数据
		}
	}


	public interface ChangeListener {
		public void contentChanged(int type);
	}

	@SuppressWarnings("unchecked")
	public String getItemNameByDids(ArrayList<DishCount> dids, List<Dish> dishItemList) {
		ArrayList<DishCount> clone = (ArrayList<DishCount>) dids.clone();
		StringBuilder        sb    = new StringBuilder();
		for (DishCount item : clone) {
			for (Dish dish : dishItemList) {
				if (dish.isPackage()) {
					for (Dish.Package aPackage : dish.subItemList) {
						if (item.dishid == aPackage.dishId) {
							sb.append(aPackage.dishName + " /剩 " + item.getCount() + " 份 ,");
							DishDataController.getDish(aPackage.dishId, -1).dishCount = item.count;
						}
					}
				} else {
					if (item.dishid == dish.dishId) {
						sb.append(dish.dishName + " /剩 " + item.getCount() + " 份 ,");
						DishDataController.getDish(dish.dishId, -1).dishCount = item.count;
						break;
					}
				}

			}
		}
		return sb.substring(0, sb.length());
	}

	/**
	 * 通过dishId返回菜品
	 *
	 * @param dishid 菜品Id
	 * @return 菜品
	 */
	public static Dish getItemByDid(int dishid) {
		for (Dish dish : dishItemList) {
			if (dish.getDishId() == dishid) {
				return dish;
			}
		}
		return null;
	}

	public void selectDishPackage(int position, Dish dish) {
		if (dish != null && dish.subItemList != null && dish.subItemList.size() > 0) {
			Dish.Package dishPackage = dish.subItemList.get(position);
			if (dishPackage.optionCategoryList != null && dishPackage.optionCategoryList
					.size() > 0) {
				dishPackage.optionList = (ArrayList) DishOptionController
						.getDishOption(dish, dishPackage);
				//                if (getOptionPackageMap() != null && getOptionPackageMap().size() > 0) {
				//                    ArrayList<Option> options = new ArrayList<Option>();
				//                    int size = optionPackageList.size();
				//                    for (int i = 0; i < size; i++) {
				//                        options.add(Cart.optionPackageList.get(i));
				//                    }
				//                    dishPackage.optionList = options;
				//                }
			}
		}
		//        DishDataController.reduceDishMark(dish.dishKind, dish);
		notifyContentChange();
	}

	public void selectCount(int position, int count, int takeOutCurrent, int current, int current_sp, ArrayList<DishDiscount> dishDiscounts) {
		Dish dish = dishItemList.get(position);
		dish.setTempQuantity(dish.getQuantity());
		dish.quantity = count;
		if (dish.haveOptionCategory()) {
			dish.optionList = (ArrayList) DishOptionController.getDishOption(dish, null);
			//            if (getOptionMap() != null && getOptionMap().size() > 0) {
			//                ArrayList<Option> options = new ArrayList<Option>();
			//                int size = optionList.size();
			//                for (int i = 0; i < size; i++) {
			//                    options.add(Cart.optionList.get(i));
			//                }
			//                dish.optionList = options;
			//            }
		}
		if (current_sp >= 0) {
			if (dish.getSpecificationList() != null && dish.getSpecificationList().size() > 0) {
				dish.current_selectSpecifications = current_sp;
				BigDecimal price = dish.getSpecificationList().get(current_sp).getPrice();
				String     name  = dish.getSpecificationList().get(current_sp).getName();
				dish.setPrice(price);
				dish.setTempPrice(price);
				dish.cost = price;
				dish.setDishName(dish.getDishName() + "( " + name + " )");
			}
		}
		if (takeOutCurrent >= 0) {
			if ("TAKE_OUT".equals(PosInfo.getInstance().getOrderType())) {
				dish.current_selectTakeOut = takeOutCurrent;
			}
		}
		if (current >= 0) {
			dish.setDishDiscount(dishDiscounts);
			if (dish.dishDiscount != null && dish.dishDiscount.size() > 0) {
				BigDecimal cost = dish.getOrderDishCost()
						.subtract(dish.getAllOrderDisCountSubtractPrice());
				if (current == dish.dishDiscount.size()) {
					dish.current_select = current;
					dish.cost = cost;
					dish.setTempPrice(cost);
				} else {
					dish.current_select = current;
					DishDiscount discount     = dish.dishDiscount.get(current);
					BigDecimal   discountCost = new BigDecimal(discount.discountPrice);
					//折扣
					if (discount.getDiscountType() == 0) {
						dish.setOnlyCost(discountCost);
						dish.setTempPrice(discountCost);
						if (dish.getMarketList() == null) {
							dish.marketList = new ArrayList<>();
						} else {
							dish.setSingleOrderDisCountSubtracePrice(new BigDecimal("0.00"));
							ToolsUtils.removeItemForMarkType(dish
									.getMarketList(), MarketType.MANUAL);//如果菜品中已经选择了手动打折
						}
						BigDecimal reduceCash = (cost.subtract(discountCost))
								.multiply(new BigDecimal(dish.getQuantity()))
								.setScale(3, BigDecimal.ROUND_HALF_UP);
						if (reduceCash.compareTo(BigDecimal.ZERO) == 1) {
							dish.setSingleOrderDisCountSubtracePrice(cost.subtract(discountCost));
							MarketObject marketObject = new MarketObject("手动" + discount
									.getName(), (cost.subtract(discountCost))
									.multiply(new BigDecimal(dish.getQuantity()))
									.setScale(3, BigDecimal.ROUND_HALF_UP), MarketType.MANUAL);
							dish.marketList.add(marketObject);
						}
					}
					//金额
					else {
						if (dish != null) {
							reductiMoney(dish, discount, position);
						}
					}
				}
			}
		}
		DishDataController.reduceDishMark(dish.dishKind, dish);
	}

	/**
	 * 过滤掉当前选中的菜品
	 *
	 * @param position
	 * @return
	 */
	private List<Dish> filterDishList(int position) {
		List<Dish> tempDishList = new CopyOnWriteArrayList<>();
		if (dishItemList != null && dishItemList.size() > 0) {
			tempDishList = ToolsUtils.cloneTo(dishItemList);
			synchronized (tempDishList) {
				tempDishList.remove(position);
			}
		}
		return tempDishList;
	}

	/**
	 * 已点的菜品  除开当前正常操作的菜品 后续要将其加到dishItemList里
	 */
	public static List<Dish> dishTempItemList = new CopyOnWriteArrayList<>();

	public void reductiMoney(Dish dishItem, DishDiscount discount, int position) {
		dishTempItemList = filterDishList(position);
		BigDecimal allMoney = dishItem.getOrderDishCost()
				.subtract(dishItem.getAllOrderDisCountSubtractPrice())
				.multiply(new BigDecimal(dishItem.getQuantity()));
		final List<Dish> singleItemList = new ArrayList<Dish>();
		final List<Dish> dishList       = new ArrayList<Dish>();
		if (dishList.size() > 0) {
			dishList.clear();
		}
		if (singleItemList.size() > 0) {
			singleItemList.clear();
		}
		dishList.add(dishItem);
		int itemIndex = 1;
		if (dishList != null && dishList.size() > 0) {
			for (Dish item : dishList) {
				int count = item.getQuantity();
				if (count == 1) {
					item.setItemIndex(itemIndex);
					itemIndex++;
					singleItemList.add(item);
				} else {
					for (int i = 0; i < count; i++) {
						Dish singleItem = cloneTo(item);
						singleItem.setItemIndex(itemIndex);
						singleItem.setQuantity(1);
						singleItem.setTempQuantity(1);
						itemIndex++;
						singleItemList.add(singleItem);
					}
				}
			}
		}

		if (!TextUtils.isEmpty(allMoney + "")) {
			if (discount.getDiscountType() == 1) {
				BigDecimal reduceMoney = discount.getReductiMoney();
				if (reduceMoney.compareTo(new BigDecimal(allMoney + "")) == 1)//如果折扣金额大于订单金额
				{
					return;
				}
			}
		}

		BigDecimal marketCost   = new BigDecimal("0");//参与活动的菜品总金额
		BigDecimal dishPriceSum = new BigDecimal("0.000");//参加促销活动菜品的总price  最后要拿这个钱来算比例
		if (discount != null) {
			if (singleItemList != null && singleItemList.size() > 0) {
				for (Dish dish : singleItemList) {
					BigDecimal dishPrice = dish.getOrderDishCost()
							.multiply(new BigDecimal(dish.getQuantity()));
					dishPriceSum = dishPriceSum.add(dishPrice);
					marketCost = marketCost.add(dishPrice);
				}

				if (dishPriceSum.compareTo(BigDecimal.ZERO) == 0) {
					MyApplication.getInstance()
							.ShowToast(ToolsUtils.returnXMLStr("not_dish_join_activity"));
					return;
				}
				Dish tempDish = null;
				//优惠的总金额
				BigDecimal dishPreferentialSum = new BigDecimal("0.000");
				//用来临时计算的满减优惠总金额
				BigDecimal tempDishPreferentialSum = new BigDecimal("0.00");
				//满减优惠总金额
				BigDecimal dishPreferentialCashSum = new BigDecimal("0.00");
				int        size                    = singleItemList.size();
				//全单满减
				if (discount.getDiscountType() == 1) {
					dishPreferentialSum = discount.getReductiMoney();
					tempDishPreferentialSum = discount.getReductiMoney();
				}
				for (int i = 0; i < size; i++) {
					Dish dish = singleItemList.get(i);
					//全单满减
					if (discount.getDiscountType() == 1) {
						BigDecimal dishTotalCost = dish.getOrderDishCost()
								.multiply(new BigDecimal(dish.getQuantity()));
						//单项菜品优惠金额
						BigDecimal meanDishPreferential = dishTotalCost
								.multiply(dishPreferentialSum)
								.divide(marketCost, 2, BigDecimal.ROUND_HALF_UP);
						BigDecimal dishPartPreferential = meanDishPreferential
								.divide(new BigDecimal(dish
										.getQuantity()), 2, BigDecimal.ROUND_HALF_UP);
						dish.setOnlyCost(dish.getOrderDishCost().subtract(dishPartPreferential));
						dish.setTempPrice(dish.getOrderDishCost().subtract(dishPartPreferential));
						dishPreferentialCashSum = dishPreferentialCashSum
								.add(dish.getCost().multiply(new BigDecimal(dish.getQuantity())));
						if (size - 1 == i)//最后一个菜
						{
							tempDish = dish;
						} else {
							setMarketObjectList(dish, discount, meanDishPreferential);
							tempDishPreferentialSum = tempDishPreferentialSum
									.subtract(meanDishPreferential);
						}
					}
				}
				if (discount.getDiscountType() == 1) {
					BigDecimal price = marketCost.subtract(dishPreferentialSum);//参加完优惠活动后应该是这个价钱
					tempDish.setOnlyCost(tempDish.getCost()
							.add(price.subtract(dishPreferentialCashSum)));
					tempDish.setTempPrice(tempDish.getCost()
							.add(price.subtract(dishPreferentialCashSum)));
					setMarketObjectList(tempDish, discount, tempDishPreferentialSum);
				}

				getDishItemList().clear();
				dishItemList = assemblyList(singleItemList);
			}
		} else {
			MyApplication.getInstance().ShowToast(ToolsUtils.returnXMLStr("discount_is_null"));
		}
	}


	private static void setMarketObjectList(Dish dish, DishDiscount activitys, BigDecimal marketDishCost) {
		if (dish.getMarketList() == null) {
			dish.marketList = new ArrayList<>();
		} else {
			dish.setSingleOrderDisCountSubtracePrice(new BigDecimal("0.00"));
			ToolsUtils
					.removeItemForMarkType(dish.getMarketList(), MarketType.MANUAL);//如果菜品中已经选择了手动打折
		}
		dish.setSingleOrderDisCountSubtracePrice(marketDishCost);
		MarketObject marketObject = new MarketObject("手动" + activitys
				.getName(), marketDishCost, MarketType.MANUAL);
		marketObject.setDiscountType(1);
		dish.marketList.add(marketObject);
	}

	/**
	 * 组装list 将拆开的菜品重新组装起来
	 *
	 * @param singleItemList
	 * @return
	 */
	private static List<Dish> assemblyList(List<Dish> singleItemList) {
		List<Dish> dishItem = new ArrayList<Dish>();
		for (Dish item : singleItemList) {
			Dish               singleItem  = cloneTo(item);
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
						//这是相同项的
						//                        isSame = true;
						//                        bean.setQuantity(bean.getQuantity() + 1);
						break;
					}
				} else {
					if (item.getDishId() == bean.getDishId() && item.getCost()
							.equals(bean.getCost())) {
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
		dishItem.addAll(ToolsUtils.cloneTo(dishTempItemList));
		if (dishTempItemList != null && dishTempItemList.size() > 0)//清空临时菜品列表
		{
			synchronized (dishTempItemList) {
				dishTempItemList.clear();
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


	public int addItem(Dish dish) {
		synchronized (dishItemList) {
			type = ADD_DISH;
			if (dish.isPackage()) {
			} else if (dish.haveOptionCategory()) {
				// TODO 含有定制项
			} else if (dish.haveMarkList()) {
				// TODO 含有打折或者优惠方案
			}
			//            else {
			//                for (Dish item : dishItemList) {
			//                    if (item.getDishId() == dish.getDishId()) {
			//                        item.setTempQuantity(item.getQuantity());
			//                        cleanAllOrderMarketState();
			//                        item.quantity++;
			//                        DishDataController.addDishMark(item.dishKind, item);
			//                        ToolsUtils.writeUserOperationRecords("TIME===>菜品加一份时间:" + TimeUtil.getStringTimeLong(System.currentTimeMillis()) + "增加了(" + item.getDishName() + ")菜品");
			//                        return item.quantity;
			//                    }
			//                }
			//            }
			Dish dishModel = new Dish(dish);
			//dishModel.setTempQuantity(dish.getQuantity());
			dishModel.setTempQuantity(0);
			cleanAllOrderMarketState();
			dishItemList.add(dishModel);
			DishDataController.addDishMark(dishModel.dishKind, dishModel);
			return dishModel.getQuantity();
		}
	}


	/**
	 * 加一份
	 *
	 * @param position
	 * @return
	 */
	public int addItem(int position) {
		synchronized (dishItemList) {
			type = ADD_DISH_COUNT;
			Dish dish = dishItemList.get(position);
			dish.setTempQuantity(dish.getQuantity());
			cleanAllOrderMarketState();
			int i = ++dish.quantity;
			DishDataController.addDishMark(dish.dishKind, dish);
			return i;
		}
	}

	public int reduceItem(int position) {
		int i = 0;
		try {
			synchronized (dishItemList) {
				if (dishItemList != null && dishItemList.size() > 0) {
					if (dishItemList.size() == position) {
						return i;
					}
					type = REDUCE_DISH_COUNT;
					Dish dish = dishItemList.get(position);
					if (dish != null) {
						dish.setTempQuantity(dish.getQuantity());
						cleanAllOrderMarketState();
						i = --dish.quantity;
						if (i == 0) {
							dishItemList.remove(position);
						}
						DishDataController.reduceDishMark(dish.dishKind, dish);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return i;
		}
		return i;
	}

	public int findDishByDid(final int dishid) {
		final int[] d_count = {0};
		if (dishItemList != null && dishItemList.size() > 0) {
			int size = dishItemList.size();
			for (int i = 0; i < size; i++) {
				Dish dishItem = dishItemList.get(i);
				if (dishItem != null) {
					if (dishid == dishItem.getDishId()) {
						d_count[0] += dishItem.getQuantity();
					}
				}
			}
		}
		if (d_count[0] != 0 && d_count[0] > 0) {
		}
		return d_count[0];
	}

	/**
	 * 减一份
	 *
	 * @param dish
	 * @return 剩余数量
	 */
	public int reduceItem(Dish dish) {
		type = REDUCE_DISH;
		int index = 0;
		synchronized (dishItemList) {
			if (dishItemList != null && dishItemList.size() > 0) {
				int size = dishItemList.size();
				for (int i = 0; i < size; i++) {
					if (dishItemList.size() == i) {
						break;
					}
					boolean isDeleteItem = false;
					Dish    dishItem     = dishItemList.get(i);
					if (dishItem != null) {
						if (dish.getDishId() == dishItem.getDishId()) {
							index = reduceItem(i);
							if (index == 0) {
								isDeleteItem = true;
							}
						}
						if (size == 1) {
							break;
						}
						if (isDeleteItem) {
							i -= 1;
						}
					}
				}
			}
		}
		return index;
	}

	/**
	 * 移除菜品
	 *
	 * @param dish
	 */
	public static void removeItem(Dish dish) {
		for (Dish item : dishItemList) {
			cleanAllOrderMarketState();
			if (item.getDishId() == dish.getDishId()) {
				ToolsUtils.writeUserOperationRecords("移除了(" + dish.getDishName() + ")菜品");
				dishItemList.remove(item);
				DishDataController.removeDishMark(dish.dishKind, dish);
				break;
			}
		}
	}

	public static void removeItem(Dish dish, int position) {
		cleanAllOrderMarketState();
		ToolsUtils.writeUserOperationRecords("移除了(" + dish.getDishName() + ")菜品");
		dishItemList.remove(position);
		DishDataController.removeDishMark(dish.dishKind, dish);
	}

	public static void removeItem2(Dish dish) {
		for (Dish item : dishItemList) {
			if (item.getDishId() == dish.getDishId() && item.isGiftDish) {
				ToolsUtils.writeUserOperationRecords("移除了(" + dish.getDishName() + ")菜品");
				dishItemList.remove(item);
				//                DishDataController.removeDishMark(dish.dishKind, dish);
				break;
			}
		}
	}


	public static void addListener(ChangeListener listener) {
		listeners.add(listener);
	}

	public static void removeListener(ChangeListener listener) {
		listeners.remove(listener);
	}

	public static void notifyContentChange() {
		for (ChangeListener listener : listeners) {
			listener.contentChanged(type);
		}
	}

	/**
	 * 获取总份数
	 *
	 * @return
	 */
	public int getQuantity() {
		int count = 0;
		for (int i = 0; i < dishItemList.size(); i++) {
			count += dishItemList.get(i).quantity;
		}
		return count;
	}

	/**
	 * 获取已点菜品个数
	 *
	 * @return
	 */
	public int getDishCount() {
		return dishItemList.size();
	}

	public void remove(Dish dishModel) {
		dishItemList.remove(dishModel);
		notifyContentChange();
	}

	public int add(Dish mDishModel) {
		notifyContentChange();
		return addItem(mDishModel);
	}

	public void deleteItem(int position) {
		dishItemList.remove(position);
		notifyContentChange();
	}

	/**
	 * 获取已点菜品项列表
	 *
	 * @return
	 */
	public static List<Dish> getAllOrderDish() {
		return dishItemList;
	}

	/**
	 * 清空购物车中的数据
	 */
	public static void cleanDishList() {
		if (dishItemList != null) {
			dishItemList.clear();
		}
	}

	//    /**
	//     * @return 总价
	//     */
	//    public String getTotalPrice() {
	//        BigDecimal bigDecimal = new BigDecimal("0.00");
	//        for (Dish goodsModel : dishItemList) {
	////            BigDecimal bigDecimal2 = new BigDecimal(PriceUtil.formatPrice(goodsModel.getCost()) + "");
	//            BigDecimal bigDecimal2 = new BigDecimal(goodsModel.getCost()+"");
	//            BigDecimal bigDecimal3 = new BigDecimal(goodsModel.quantity);
	//            bigDecimal = bigDecimal.add(bigDecimal2.multiply(bigDecimal3));
	//        }
	//        return bigDecimal.toString();
	//    }

	/**
	 * 总价(应付)
	 *
	 * @return
	 */
	public static float getPriceSum() {
		BigDecimal bigDecimal = new BigDecimal("0.000");
		for (Dish goodsModel : dishItemList) {
			BigDecimal bigDecimal2 = new BigDecimal(PriceUtil
					.formatPrice(goodsModel.getTotalPrice()) + "");
			BigDecimal bigDecimal3 = new BigDecimal(goodsModel.quantity);
			bigDecimal = bigDecimal.add(bigDecimal2.multiply(bigDecimal3));

			//计算外带打包费用
			if ("TAKE_OUT".equals(PosInfo.getInstance().getOrderType()) || "SALE_OUT"
					.equals(PosInfo.getInstance().getOrderType())) {
				if (goodsModel.getWaiDai_cost() != null) {
					PosInfo.getInstance().setAddWaiMaiMoney(true);
					bigDecimal = bigDecimal.add(bigDecimal3.multiply(goodsModel.getWaiDai_cost()));
				}
			} else {
				if (goodsModel.getWaiDai_cost() != null && PosInfo.getInstance()
						.isAddWaiMaiMoney()) {
					bigDecimal = bigDecimal
							.subtract(bigDecimal3.multiply(goodsModel.getWaiDai_cost()));
				}
			}
		}
		if (bigDecimal.floatValue() == 0) {
			return 0;
		}

		if ("SALE_OUT".equals(PosInfo.getInstance().getOrderType())) {
			bigDecimal = bigDecimal.add(new BigDecimal(Store.getInstance(MyApplication.getContext())
					.getSaleMoney()));
		}
		float discountPrice = bigDecimal.floatValue();
		//        float discountPrice = DishDataController.getDiscountPrice(bigDecimal.floatValue());
		return discountPrice > 0 ? discountPrice : 0;
	}

	/**
	 * 总价(应付)
	 *
	 * @return
	 */
	public static float getPriceSumXtt() {
		BigDecimal bigDecimal = new BigDecimal("0.000");
		for (Dish goodsModel : dishItemList) {
			BigDecimal bigDecimal2 = new BigDecimal(PriceUtil
					.formatPrice(goodsModel.getTotalPriceXtt()) + "");
			BigDecimal bigDecimal3 = new BigDecimal(goodsModel.quantity);
			bigDecimal = bigDecimal.add(bigDecimal2.multiply(bigDecimal3));
		}
		if (bigDecimal.floatValue() == 0) {
			return 0;
		}

		float discountPrice = DishDataController.getDiscountPrice(bigDecimal.floatValue());
		return discountPrice > 0 ? discountPrice : 0;
	}

	/**
	 * 实付
	 *
	 * @return
	 */
	public static float getCost() {
		BigDecimal bigDecimal                       = new BigDecimal("0.000");
		BigDecimal allOrderDisCountSubtractPrice    = new BigDecimal("0.000");//通过全单打折每份菜品最后需要减掉的钱
		BigDecimal singleOrderDisCountSubtractPrice = new BigDecimal("0.000");//通过单品打折每份菜品最后需要减掉的钱
		for (Dish goodsModel : dishItemList) {
			BigDecimal dishCost = new BigDecimal("0.00");
			allOrderDisCountSubtractPrice = allOrderDisCountSubtractPrice
					.add(goodsModel.getAllOrderDisCountSubtractPrice()
							.multiply(new BigDecimal(goodsModel.getQuantity())));
			singleOrderDisCountSubtractPrice = singleOrderDisCountSubtractPrice
					.add(goodsModel.getSingleOrderDisCountSubtracePrice()
							.multiply(new BigDecimal(goodsModel.getQuantity())));
			dishCost = goodsModel.getOrderDishCost();
			BigDecimal bigDecimal3 = new BigDecimal(goodsModel.quantity);
			bigDecimal = bigDecimal.add(dishCost.multiply(bigDecimal3));
			//计算外带打包费用
			if ("TAKE_OUT".equals(PosInfo.getInstance().getOrderType()) || "SALE_OUT"
					.equals(PosInfo.getInstance().getOrderType())) {
				if (goodsModel.getWaiDai_cost() != null) {
					bigDecimal = bigDecimal.add(bigDecimal3.multiply(goodsModel.getWaiDai_cost()));
				}
			}
			//            else {
			//                if (goodsModel.getWaiDai_cost() != null && PosInfo.getInstance().isAddWaiMaiMoney()) {
			////                    if(goodsModel.isAddIngWaiMaiCost())
			////                    {
			////                        bigDecimal = bigDecimal.subtract(bigDecimal3.multiply(goodsModel.getWaiDai_cost()));
			////                        goodsModel.setAddIngWaiMaiCost(false);
			////                    }
			//                }
			//            }
		}
		if (bigDecimal.floatValue() == 0) {
			return 0;
		}

		if ("SALE_OUT".equals(PosInfo.getInstance().getOrderType())) {
			bigDecimal = bigDecimal.add(new BigDecimal(Store.getInstance(MyApplication.getContext())
					.getSaleMoney()));
		}
		bigDecimal = bigDecimal.subtract(allOrderDisCountSubtractPrice)
				.subtract(singleOrderDisCountSubtractPrice);
		float discountPrice = bigDecimal.floatValue();
		//        float discountPrice = DishDataController.getDiscountPrice(bigDecimal.floatValue());
		//        int price = (int)discountPrice;
		//        discountPrice = price;
		return discountPrice > 0 ? discountPrice : 0;
	}

	/**
	 * 获取打包费
	 *
	 * @return
	 */
	public static BigDecimal getTakeMoney() {
		BigDecimal bigDecimal = new BigDecimal("0.000");
		for (Dish goodsModel : dishItemList) {
			BigDecimal bigDecimal3 = new BigDecimal(goodsModel.quantity);
			//计算外带打包费用
			if ("TAKE_OUT".equals(PosInfo.getInstance().getOrderType()) || "SALE_OUT"
					.equals(PosInfo.getInstance().getOrderType())) {
				if (goodsModel.getWaiDai_cost() != null) {
					bigDecimal = bigDecimal.add(bigDecimal3.multiply(goodsModel.getWaiDai_cost()));
				}
			}
		}
		return bigDecimal;
	}

	public void clear() {
		// for (DishModel iterable_element : items) {
		// iterable_element.count = 0;
		// if(iterable_element.tastes!=null){
		// iterable_element.tastes.clear();
		// }
		// iterable_element.remarks = "";
		// iterable_element.setmealIndex = -1;
		// }
		if (dishItemList != null) {
			dishItemList.clear();
		}
		// orderNo = "";
		tid = "";
		//        this.paymentInfo = null;

		alreadyPaidMoney = 0;
		dmValue = 0;
		discount = "1";
		subtraction = "0";

		this.notifyContentChange();
	}

	public Order getOrderItem(List<Dish> mDishs, Order tableOrder, boolean isTableOrder, OrderStatus orderStatus) {
		Order   order   = new Order();
		PosInfo posInfo = PosInfo.getInstance();
		if (mDishs != null) {
			if (orderStatus != null && orderStatus == OrderStatus.PENDING) {
				order.setStatus(OrderStatus.PENDING);
			}
			order.setTotal(getPriceSum() + "");
			order.setCost("0");
			order.setCreatedAt(System.currentTimeMillis());
			order.setSource(posInfo.getTerminalName());
			order.setDiscount(1);
			order.setCreatedBy(posInfo.getRealname());
			order.setSubtraction(0);
			order.setComment("");
			order.setOrderType(posInfo.getOrderType());
			if (posInfo.getOrderType() == "TAKE_OUT")//如果订单是外卖
			{
				order.setWaimaiType(posInfo.getWaimaiType());
			}
			order.setMenuName(getMenuName());//获取当前菜品餐段名称
			order.setPaymentStatus(PaymentStatus.NOT_PAYED);
			if (tableOrder != null) {
				order.setTableIds(tableOrder.getTableIds());
				order.setTableNames(tableOrder.getTableNames());
				order.setCustomerAmount(Integer.valueOf(tableOrder.getCustomerAmount()));
			}
			int             dishSize = mDishs.size();
			List<OrderItem> itemList = new ArrayList<>();
			for (int i = 0; i < dishSize; i++) {
				OrderItem item = new OrderItem();
				Dish      dish = mDishs.get(i);
				item.setId(TimeUtil.getOrderItemId(i));
				item.setOptionList(dish.optionList);
				item.setDishId(dish.getDishId());
				item.setDishName(dish.getDishName());
				item.setPrice(dish.getPrice());
				item.setDishUnit(dish.getDishUnit());
				item.setImageName(dish.getImageName());
				item.setWaiDai_cost(dish.getWaiDai_cost());
				//                item.setCost(dish.getPrice()*order.getDiscount());
				//                BigDecimal b1 = new BigDecimal(dish.getTotalPrice()+"");
				//                BigDecimal b2 = new BigDecimal(order.getDiscount());
				item.setCost((dish.getDishRealCost()).setScale(2, BigDecimal.ROUND_DOWN));
				item.setQuantity(dish.quantity);
				item.setComment(dish.comment);
				item.setCookList(dish.cookList);
				item.setTasteList(dish.tasteList);
				item.setMenuName(getMenuName());//获取当前菜品餐段名称
				item.setDishUnitID(dish.getDishUnitID());
				item.setSubItemList(dish.subItemList);
				item.setGift(false);
				item.setDishKindStr(DishDataController.getKindNameById(dish.dishKind));
				item.setDishKind(dish.dishKind);
				item.setMarketList(dish.getMarketList());
				item.setTempMarketList(dish.getTempMarketList());
				item.setDiscounted(dish.getDiscounted());
				itemList.add(item);
			}
			order.setItemList(itemList);
		}
		return order;
	}

	public Order getOrderItem(Order tableOrder, List<Dish> mDishs) {
		Order   order   = new Order();
		PosInfo posInfo = PosInfo.getInstance();
		if (mDishs != null) {
			order.setTotal(getPriceSum() + "");
			order.setCost("0");
			order.setCreatedAt(System.currentTimeMillis());
			order.setSource(posInfo.getTerminalName());
			order.setDiscount(1);
			order.setCreatedBy(posInfo.getRealname());
			if (tableOrder != null) {
				order.setTableNames(tableOrder.getTableNames());
				order.setTableIds(tableOrder.getTableIds());
				order.setCustomerAmount(Integer.valueOf(tableOrder.getCustomerAmount()));
			}
			if (posInfo.getOrderType() == "TAKE_OUT")//如果订单是外卖
			{
				order.setWaimaiType(posInfo.getWaimaiType());
			}
			order.setMenuName(getMenuName());//获取当前菜品餐段名称
			order.setSubtraction(0);
			order.setComment("");
			order.setOrderType(posInfo.getOrderType());
			order.setPaymentStatus(PaymentStatus.NOT_PAYED);
			int             dishSize = mDishs.size();
			List<OrderItem> itemList = new ArrayList<>();
			for (int i = 0; i < dishSize; i++) {
				OrderItem item = new OrderItem();
				Dish      dish = mDishs.get(i);
				item.setId(TimeUtil.getOrderItemId(i));
				item.setOptionList(dish.optionList);
				item.setMemberPrice(dish.getMemberPrice());
				item.setDishId(dish.getDishId());
				item.setDishName(dish.getDishName());
				item.setPrice(dish.getPrice());
				item.setDishUnit(dish.getDishUnit());
				item.setImageName(dish.getImageName());
				item.setWaiDai_cost(dish.getWaiDai_cost());
				//                item.setCost(dish.getPrice()*order.getDiscount());
				//                BigDecimal b1 = new BigDecimal(dish.getTotalPrice()+"");
				//                BigDecimal b2 = new BigDecimal(order.getDiscount());
				item.setMenuName(getMenuName());//获取当前菜品餐段名称
				item.setQuantity(dish.quantity);
				item.setComment(dish.comment);
				item.setCookList(dish.cookList);
				item.setTasteList(dish.tasteList);
				item.setDishUnitID(dish.getDishUnitID());
				item.setSubItemList(dish.subItemList);
				item.setIsPackage(dish.getIsPackage());
				item.setGift(false);
				item.setDishKindStr(DishDataController.getKindNameById(dish.dishKind));
				item.setDishKind(dish.dishKind);
				item.setMarketList(dish.getMarketList());
				item.setTempMarketList(dish.getTempMarketList());
				item.setCost((dish.getDishRealCost()).setScale(2, BigDecimal.ROUND_DOWN));
				item.setDiscounted(dish.getDiscounted());
				itemList.add(item);
			}
			order.setItemList(itemList);
		}
		return order;
	}

	public Order getNetOrderItem(List<Dish> mDishs, Order tableOrder) {
		Order   order   = new Order();
		PosInfo posInfo = PosInfo.getInstance();
		if (mDishs != null) {
			order.setTotal(tableOrder.getTotal());
			order.setCost(tableOrder.getCost());
			order.setCreatedAt(System.currentTimeMillis());
			order.setSource(tableOrder.getSource());
			order.setNetOrderid(tableOrder.getNetOrderid());
			order.setDiscount(tableOrder.getDiscount());
			order.setCreatedBy(posInfo.getRealname());
			order.setSubtraction(tableOrder.getSubtraction());
			order.setComment(tableOrder.getComment());
			order.setOrderType(tableOrder.getOrderType());
			if (posInfo.getOrderType() == "TAKE_OUT")//如果订单是外卖
			{
				order.setWaimaiType(posInfo.getWaimaiType());
			}
			order.setMenuName(getMenuName());//获取当前菜品餐段名称
			order.setPaymentStatus(tableOrder.getPaymentStatus());
			order.setCustomerName(tableOrder.getCustomerName());
			order.setCustomerPhoneNumber(tableOrder.getCustomerPhoneNumber());
			order.setCustomerAddress(tableOrder.getCustomerAddress());
			if (tableOrder != null) {
				order.setTableIds(tableOrder.getTableIds());
				order.setTableNames(tableOrder.getTableNames());
				order.setCustomerAmount(Integer.valueOf(tableOrder.getCustomerAmount()));
			}
			int             dishSize = mDishs.size();
			List<OrderItem> itemList = new ArrayList<>();
			for (int i = 0; i < dishSize; i++) {
				OrderItem item = new OrderItem();
				Dish      dish = mDishs.get(i);
				item.setId(TimeUtil.getOrderItemId(i));
				item.setOptionList(dish.optionList);
				item.setDishId(dish.getDishId());
				item.setDishName(dish.getDishName());
				item.setPrice(dish.getPrice());
				item.setDishUnit(dish.getDishUnit());
				item.setImageName(dish.getImageName());
				item.setWaiDai_cost(dish.getWaiDai_cost());
				//                item.setCost(dish.getPrice()*order.getDiscount());
				//                BigDecimal b1 = new BigDecimal(dish.getTotalPrice()+"");
				//                BigDecimal b2 = new BigDecimal(order.getDiscount());
				item.setCost((dish.getDishRealCost()).setScale(2, BigDecimal.ROUND_DOWN));
				item.setQuantity(dish.quantity);
				item.setComment(dish.comment);
				item.setCookList(dish.cookList);
				item.setTasteList(dish.tasteList);
				item.setMenuName(getMenuName());//获取当前菜品餐段名称
				item.setDishUnitID(dish.getDishUnitID());
				item.setSubItemList(dish.subItemList);
				item.setGift(false);
				item.setDishKindStr(DishDataController.getKindNameById(dish.dishKind));
				item.setDishKind(dish.dishKind);
				item.setMarketList(dish.getMarketList());
				item.setTempMarketList(dish.getTempMarketList());
				item.setDiscounted(dish.getDiscounted());
				itemList.add(item);
			}
			order.setItemList(itemList);
		}
		return order;
	}

	public List<OrderItem> getTableOrderItem(List<Dish> mDishs) {
		List<OrderItem> itemList = new ArrayList<>();
		if (mDishs != null) {
			int dishSize = mDishs.size();
			for (int i = 0; i < dishSize; i++) {
				OrderItem item = new OrderItem();
				Dish      dish = mDishs.get(i);
				item.setId(TimeUtil.getOrderItemId(i));
				item.setOptionList(dish.optionList);
				item.setDishId(dish.getDishId());
				item.setDishName(dish.getDishName());
				item.setPrice(dish.getPrice());
				item.setDishUnit(dish.getDishUnit());
				item.setImageName(dish.getImageName());
				item.setCreatedAt(String.valueOf(System.currentTimeMillis()));
				item.setWaiDai_cost(dish.getWaiDai_cost());

				item.setCost((dish.getDishRealCost()).setScale(2, BigDecimal.ROUND_DOWN));
				item.setQuantity(dish.quantity);
				item.setComment(dish.comment);
				item.setCookList(dish.cookList);
				item.setTasteList(dish.tasteList);
				item.setDishUnitID(dish.getDishUnitID());
				item.setSubItemList(dish.subItemList);
				item.setIsPackage(dish.getIsPackage());
				item.setGift(false);
				item.setDishKindStr(DishDataController.getKindNameById(dish.dishKind));
				item.setDishKind(dish.dishKind);
				item.setMarketList(dish.getMarketList());
				item.setTempMarketList(dish.getTempMarketList());
				item.setDiscounted(dish.getDiscounted());
				itemList.add(item);
			}
		}
		return itemList;
	}

	public static List<OrderItem> getRetreatItem(OrderItem orderItem) {
		List<OrderItem> itemList = new ArrayList<>();
		itemList.add(orderItem);
		return itemList;
	}

	/**
	 * 检测购物车中是否有菜品记录
	 *
	 * @return
	 */
	public static boolean isCartDishNull() {
		if (dishItemList != null && dishItemList.size() > 0) {
			return false;
		}
		return true;
	}

	public static List<FetchOrder> getHandDishItemList() {
		return handDishItemList;
	}

	public static void setHandDishItemList(List<FetchOrder> handDishItemList) {
		Cart.handDishItemList = handDishItemList;
	}

	public static void removeHandDishList(int position) {
		if (!isCartDishNull() && handDishItemList.get(position) != null) {
			handDishItemList.remove(position);
			notifyContentChange();
		}
	}

	/**
	 * 清除全单打折状态  清除单个菜打折状态
	 */
	public static void cleanAllOrderMarketState() {
		List<Dish> dishItemList = getDishItemList();
		if (dishItemList != null && dishItemList.size() > 0) {
			for (Dish dish : dishItemList) {
				dish.setAllOrderDisCountSubtractPrice(new BigDecimal("0.00"));
				ToolsUtils.removeItemForMarkType(dish.getMarketList(), MarketType.DISCOUNT);

				dish.setSingleOrderDisCountSubtracePrice(new BigDecimal("0.00"));
				ToolsUtils.removeItemForMarkType(dish.getMarketList(), MarketType.MANUAL);
				dish.current_select = 0;
			}
		}
	}

	private String getMenuName() {
		Menu menu = DishDataController.getDishs(DishDataController.DISH_TYPE);
		if (menu != null && !TextUtils.isEmpty(menu.getTimeName())) {
			return menu.getTimeName();
		}
		return "未知餐段";
	}


}
