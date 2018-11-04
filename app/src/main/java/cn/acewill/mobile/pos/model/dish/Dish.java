package cn.acewill.mobile.pos.model.dish;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ModelContainer;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import cn.acewill.mobile.pos.common.DishDataController;
import cn.acewill.mobile.pos.factory.AppDatabase;
import cn.acewill.mobile.pos.model.MarketObject;
import cn.acewill.mobile.pos.model.MarketType;
import cn.acewill.mobile.pos.model.order.OrderItem;
import cn.acewill.mobile.pos.utils.ToolsUtils;


/**
 * Created by Acewill on 2016/6/2.
 */
//当把一个菜品添加到订单中时，需要详细信息,比如口味和做法
@com.raizlabs.android.dbflow.annotation.Table(name = "dish", database = AppDatabase.class)
@ModelContainer
public class Dish extends BaseModel implements Serializable {
	@Column
	@PrimaryKey(autoincrement = true)
	/**
	 *菜品ID
	 */
	@SerializedName("dishID")
	public  int        dishId;
	private String     appid;
	private Long       brandid;
	private Long       storeid;
	/**
	 * 菜品名称
	 */
	@Column
	public  String     dishName;
	@Column
	private String     dishUnit;//菜品单位的名称
	/**
	 * 速记码
	 */
	@Column
	private String     sortMark;
	/**
	 * 菜品简称
	 */
	@Column
	private String     Alias;
	/**
	 * 菜品英文名
	 */
	@Column
	private String     dishNameEnglish;
	/**
	 * 菜品会员价价格
	 */
	@Column
	private BigDecimal memberPrice;
	/**
	 * 菜品价格
	 */
	@Column
	private BigDecimal price;
	/**
	 * 临时菜品price  用来切换外卖价格和菜品原价进行切换使用
	 */
	private BigDecimal temporaryPrice;

	/**
	 * 用来临时存储菜品通过优惠活动打折后的菜品价钱
	 */
	private BigDecimal tempPrice;
	/**
	 * 菜品外卖时的价格
	 */
	private BigDecimal waimaiPrice;
	/**
	 * 菜品临时存储外卖时的价格 用来切换外卖价格和菜品原价进行切换使用
	 */
	private BigDecimal waimaiTempPrice;


	public BigDecimal tempCost = new BigDecimal("0.00");

	/**
	 * 全单打折要减的钱
	 */
	public BigDecimal allOrderDisCountSubtractPrice;

	/**
	 * 单品打折要减的钱
	 */
	public  BigDecimal singleOrderDisCountSubtracePrice;
	/**
	 * 是否是套餐（0普通菜品，1套餐）
	 */
	@Column
	private int        isPackage;

	public boolean isPackage() {
		return isPackage == 1;
	}

	/**
	 * ture显示套餐明细，false不显示
	 */
	private boolean showPackageItemsFlag;

	/**
	 * 菜品状态
	 */
	@Column
	private int    status;
	/**
	 * 菜品图片路径
	 */
	@Column
	private String imageName;
	@Column
	public  String comment;//备注
	/**
	 * 是否支持堂食
	 */
	@Column
	private int    isTangShi;
	/**
	 * 是否支持外带
	 */
	@Column
	private int    isWaiDai;
	/**
	 * 是否支持微信支付
	 */
	@Column
	private int    isWeChat;
	/**
	 * 是否支持外卖
	 */
	@Column
	private int    isWaiMai;
	/**
	 * 菜品数量
	 */
	@Column
	public  int    dishCount;
	/**
	 * 菜品库存修改了多少份
	 */
	public  int    dishModifyCount;
	/**
	 * 菜品已点的份数
	 */
	@Column
	public  int    selectDishCount;
	/**
	 * 菜品单位ID
	 */
	private int    dishUnitID;
	/**
	 * 菜品时段Id
	 */
	public  long   dishTimeID;
	/**
	 * 菜品单位名称
	 */
	@Column
	public  String dishUnitStr;
	/**
	 * 菜品说明
	 */
	@Column
	private String dishComment;
	@Column
	public  String dishKind;//菜品分类
	@Column
	/**
	 * 分类名称逗号分开
	 */
	public  String dishKindStr;
	/**
	 * 菜品排序号
	 */
	@Column
	private int    seq;
	/**
	 * 菜品颜色
	 */
	public  String dishKindColor;

	@Column
	private BigDecimal waiDai_cost;
	public  BigDecimal waiDai_costTemp;

	private DishSummary summary;

	/**
	 * 套餐标识  0 为必选 ，1组合多选  //-1 为必选 ，>0组合多选
	 */
	public int                          groupnum;
	/**
	 * 已选套餐
	 */
	public ArrayList<Dish>              setmeals;
	/**
	 * 套餐Group   eg:几选几
	 */
	public ArrayList<SetmealGroupModel> setmealGroup;//
	/**
	 * 价格因素 ？
	 */
	public int                          priceFactor;
	/**
	 * 该套餐项是否能重复 0=不能  1=能
	 */
	public int                          isRequired;
	//在沽清状态中是否被修改过
	public boolean                      isDishCountModify;
	//在沽清状态中是否被检测过
	public boolean                      isDishCountDetection;

	public BigDecimal cost;
	public Integer    dishOptionId;
	public int        quantity;
	public int        tempQuantity;
	public int        itemIndex;

	public int getItemIndex() {
		return itemIndex;
	}

	public void setItemIndex(int itemIndex) {
		this.itemIndex = itemIndex;
	}

	/**
	 * 是否参与促销活动
	 */
	public boolean isJoinMarket = false;

	/**
	 * 购物车在套餐中子项是否已显示
	 */
	public boolean isMealShow = false;

	public boolean isMealShow() {
		return isMealShow;
	}

	public void setMealShow(boolean mealShow) {
		isMealShow = mealShow;
	}

	/**
	 * discounted 1是参与 0不参与
	 */
	public int discounted = 1;

	/**
	 * weighted 1是参与 0不参与
	 */
	public int weighted = 0;

	/**
	 * 菜品是否需要称重
	 *
	 * @return
	 */
	public boolean isWeighted() {
		if (weighted == 1) {
			return true;
		}
		return false;
	}

	private String subItemSth = "";

	public int getWeighted() {
		return weighted;
	}

	public void setWeighted(int weighted) {
		this.weighted = weighted;
	}

	public boolean isShowPackageItemsFlag() {
		return showPackageItemsFlag;
	}

	public void setShowPackageItemsFlag(boolean showPackageItemsFlag) {
		this.showPackageItemsFlag = showPackageItemsFlag;
	}

	public boolean isDishCountModify() {
		return isDishCountModify;
	}

	public void setDishCountModify(boolean dishCountModify) {
		isDishCountModify = dishCountModify;
	}

	public boolean isDishCountDetection() {
		return isDishCountDetection;
	}

	public void setDishCountDetection(boolean isDishCountDetection) {
		this.isDishCountDetection = isDishCountDetection;
	}

	/**
	 * 该菜是否是赠菜
	 */
	public boolean isGiftDish = false;

	public boolean isGiftDish() {
		return isGiftDish;
	}

	public void setGiftDish(boolean giftDish) {
		isGiftDish = giftDish;
	}

	public boolean isParticipationDisCount() {
		return discounted == 1;
	}

	public int getDiscounted() {
		return discounted;
	}

	public void setDiscounted(int discounted) {
		this.discounted = discounted;
	}

	public int getAlreadyCount() {
		return quantity;
	}

	public boolean isJoinMarket() {
		return isJoinMarket;
	}

	public void setJoinMarket(boolean joinMarket) {
		isJoinMarket = joinMarket;
	}

	public String getSubItemSth() {
		return subItemSth;
	}

	public void setSubItemSth(String subItemSth) {
		this.subItemSth = subItemSth;
	}

	public ArrayList<DishDiscount> dishDiscount;// :[ 菜品特价方案，array
	public List<CookingMethod>     cook; //做法, array, 当没有指定做法的时候为空数组
	public List<Flavor>            taste;  //口味, array, 当没有指定口味的时候为空数组
	public List<OptionCategory>    optionCategoryList;//定制项数组
	public List<PackageItem>       packageItems;//   套餐项, array, 当菜品是普通项的时候为空；当菜品是套餐的时候必须有数据
	public List<MarketObject>      marketList;//这个菜品上使用过的营销方案, 可以是单品的，也可以是全单的，全单的话需要按比例平摊到菜品上
	public List<MarketObject>      tempMarketList; //这个菜品上临时使用过的营销方案, 可以是单品的，也可以是全单的，全单的话需要按比例平摊到菜品上


	public ArrayList<Integer>       selectCooker;
	public ArrayList<Integer>       selectTaste;
	public ArrayList<Integer>       selectCategory;
	public ArrayList<CookingMethod> cookList;
	public ArrayList<Flavor>        tasteList;
	public ArrayList<Option>        optionList;
	public ArrayList<Package>       subItemList;
	public ArrayList<Specification> specificationList;

	public int     current_select               = 0;
	public int     current_selectTakeOut        = 0;//打包费
	public int     current_selectSpecifications = -1;//规格
	public boolean isSelectDisCount             = false;
	public boolean isJoinSingleDisCount         = false;//是否参与单品打折

	public boolean isJoinSingleDisCount() {
		return isJoinSingleDisCount;
	}

	public void setJoinSingleDisCount(boolean joinSingleDisCount) {
		isJoinSingleDisCount = joinSingleDisCount;
	}

	public Dish() {

	}

	public List<MarketObject> getMarketList() {
		return marketList;
	}

	public void setMarketList(List<MarketObject> marketList) {
		this.marketList = marketList;
	}

	public ArrayList<Option> getOptionList() {
		return optionList;
	}

	public void setOptionList(ArrayList<Option> optionList) {
		this.optionList = optionList;
	}

	public ArrayList<Package> getSubItemList() {
		return subItemList;
	}

	public void setSubItemList(ArrayList<Package> subItemList) {
		this.subItemList = subItemList;
	}

	public Dish(OrderItem orderItem) {
		dishId = Integer.valueOf(orderItem.getDishId() + "");
		dishName = orderItem.dishName;
		price = orderItem.price;
		cost = orderItem.price;
		quantity = orderItem.quantity;
		imageName = orderItem.getImageName();
		tempPrice = orderItem.price;
		temporaryPrice = orderItem.price;
		waimaiPrice = orderItem.getWaimaiPrice();
		waimaiTempPrice = orderItem.getWaimaiPrice();
		isPackage = orderItem.isPackage;
		dishKind = orderItem.dishKind;
		dishUnit = orderItem.getDishUnit();
		dishUnitID = orderItem.getDishUnitID();
		dishDiscount = orderItem.getDishDiscount();
		specificationList = orderItem.getSpecificationList();
		optionCategoryList = orderItem.getOptionCategoryList();
		if (quantity == 0) {
			quantity = 1;
		}
		subItemList = orderItem.getSubItemList();
		optionList = orderItem.getOptionList();
		dishKind = orderItem.getDishKind();
		discounted = orderItem.discounted;
	}

	public void setCost(BigDecimal cost) {
		this.cost = cost;
		this.tempPrice = cost;
	}

	public void setOnlyCost(BigDecimal cost) {
		this.cost = cost;
	}

	public void setDishWeightPrice(float dishWeight) {
		BigDecimal wPrice = price.multiply(new BigDecimal(String.valueOf(dishWeight)))
				.setScale(2, BigDecimal.ROUND_DOWN);
		price = wPrice;
		cost = wPrice;
		tempPrice = wPrice;
		temporaryPrice = wPrice;
	}


	public Dish(Dish dish) {
		dishId = dish.getDishId();
		dishName = dish.dishName;
		price = dish.price;
		cost = dish.price;
		memberPrice = dish.memberPrice;
		quantity = dish.quantity;
		subItemSth = "";
		tempPrice = dish.price;
		temporaryPrice = dish.price;
		showPackageItemsFlag = dish.isShowPackageItemsFlag();
		weighted = dish.getWeighted();
		waimaiPrice = dish.getWaimaiPrice();
		waimaiTempPrice = dish.getWaimaiPrice();
		isPackage = dish.isPackage;
		dishKind = dish.dishKind;
		dishUnit = dish.getDishUnit();
		dishUnitID = dish.getDishUnitID();
		dishDiscount = dish.getDishDiscount();
		waiDai_cost = dish.getWaiDai_cost();
		waiDai_costTemp = waiDai_cost;
		specificationList = dish.getSpecificationList();
		optionCategoryList = dish.getOptionCategoryList();
		discounted = dish.discounted;
		isGiftDish = dish.isGiftDish;
		imageName = dish.getImageName();
		dishOptionId = dish.getDishOptionId();
		if (quantity == 0) {
			quantity = 1;
		}

		if (dish.isPackage()) {
			subItemList = new ArrayList<Package>();
			if (dish.packageItems != null && dish.packageItems.size() > 0) {
				for (PackageItem ackageItem : dish.packageItems) {
					ackageItem.quantity = 0;
					for (Package choiceDish : ackageItem.options) {
						if (!isShowPackageItemsFlag())//不显示套餐明细直接将菜品加入到购物车
						{
							subItemList.add(new Package(choiceDish));
						} else {
							if (choiceDish.quantity > 0) {
								subItemList.add(new Package(choiceDish));
								choiceDish.quantity = 0;
							}
						}
					}
				}
			}
		} else if (dish.haveCooker() && dish.selectCooker != null) {
			selectCooker = dish.selectCooker;
			cookList = new ArrayList<CookingMethod>();
			for (Integer index : selectCooker) {
				cookList.add(dish.cook.get(index));
			}
			dish.selectCooker.clear();
		} else if (dish.haveTaste() && dish.selectTaste != null) {
			selectTaste = dish.selectTaste;
			tasteList = new ArrayList<Flavor>();
			for (Integer index : selectTaste) {
				tasteList.add(dish.taste.get(index));
			}
			dish.selectTaste.clear();
		} else if (dish.haveOptionCategory()) {
			optionList = dish.optionList;
			//            optionList = new ArrayList<Option>();
			//            int size = Cart.optionList.size();
			//            for (int i = 0; i < size; i++) {
			//                optionList.add(Cart.optionList.get(i));
			//            }
		}
	}


	public class PackageItem implements Serializable {
		public String               itemName;// : 分组名称, String
		public String               itemID;// : 分组ID, String
		public int                  itemCount;// : 分组选择数量, int
		public int                  isMust;// : 是否必须, 0:不是必须；1：必须项
		public ArrayList<Package>   options;// : [ 可选项, array
		public List<OptionCategory> optionCategoryList;//定制项数组
		public int                  quantity;

		public boolean getIsMust() {
			return isMust == 1;
		}

	}

	public class Package extends Dish {
		public Package() {
		}

		//套餐子项价格
		public BigDecimal itemPrice;

		public Package(Package dish) {
			this.setDishId(dish.getDishId());
			this.setDishName(dish.getDishName());
			this.setPrice(dish.getItemPrice());
			this.cost = price;
			this.optionCategoryList = dish.optionCategoryList;
			this.quantity = dish.quantity;
			this.dishKind = dish.dishKind;
			this.setIsPackage(dish.getIsPackage());
			this.optionList = dish.optionList;
			this.extraCost = dish.extraCost;
			this.skuStr = dish.skuStr;
			this.itemPrice = dish.getItemPrice();
			this.dishKindStr = DishDataController.getKindNameById(dish.dishKind);
			if (this.quantity == 0) {
				this.quantity = 1;
			}
		}

		public String skuStr;
		public int    extraCost;
		public int    count;
		public String sku;
		public boolean isPrint = false;

		public BigDecimal getItemPrice() {
			return itemPrice;
		}

		public void setItemPrice(BigDecimal itemPrice) {
			this.itemPrice = itemPrice;
		}
	}

	/**
	 * @return 优惠后的价格  实付
	 */
	public BigDecimal getCost() {
		//        if (dishDiscount != null) {
		//            for (DishDiscount item : dishDiscount) {
		//                if (TimeUtil.isDiscountTime(item.discountTime)) {
		////                    int price = new BigDecimal(item.discountPrice).intValue();
		//                    return new BigDecimal(item.discountPrice);
		////                    return new BigDecimal(price+"");
		//                }
		//            }
		//        }
		return cost;
	}

	/**
	 * @return 优惠价格 +做法价格+套餐项价格+定制项价格  应付
	 */
	public String getTotalPrice() {
		BigDecimal bigDecimal = new BigDecimal(0);
		if (cookList != null) {
			for (CookingMethod item : cookList) {
				if (!TextUtils.isEmpty(item.price)) {
					bigDecimal = bigDecimal.add(new BigDecimal(item.price));
				}
			}
		}
		if (subItemList != null) {
			for (Package item : subItemList) {
				bigDecimal = bigDecimal.add(new BigDecimal(item.extraCost));
				if (item.optionList != null && item.optionList.size() > 0) {
					for (Option option : item.optionList) {
						if (option.getPrice().compareTo(new BigDecimal("0")) != 0) {
							bigDecimal = bigDecimal.add(option.getPrice());
						}
					}
				}
			}
		}
		if (tasteList != null) {
			for (Flavor item : tasteList) {
				if (item.tasteExtraCost != 0) {
					bigDecimal = bigDecimal.add(new BigDecimal(item.tasteExtraCost));
				}
			}
		}

		if (optionList != null) {
			for (Option option : optionList) {
				if (option.getPrice().compareTo(new BigDecimal("0")) != 0) {
					bigDecimal = bigDecimal.add(option.getPrice());
				}
			}
		}
		bigDecimal = new BigDecimal(price.toString()).add(bigDecimal);
		return bigDecimal.toString();
	}

	/**
	 * @return 优惠价格 +做法价格+套餐项价格+定制项价格  应付
	 */
	public String getTotalPriceXtt() {
		BigDecimal bigDecimal = new BigDecimal(0);
		if (cookList != null) {
			for (CookingMethod item : cookList) {
				if (!TextUtils.isEmpty(item.price)) {
					bigDecimal = bigDecimal.add(new BigDecimal(item.price));
				}
			}
		}
		if (subItemList != null) {
			for (Package item : subItemList) {
				bigDecimal = bigDecimal.add(new BigDecimal(item.extraCost));
			}
		}
		if (tasteList != null) {
			for (Flavor item : tasteList) {
				if (item.tasteExtraCost != 0) {
					bigDecimal = bigDecimal.add(new BigDecimal(item.tasteExtraCost));
				}
			}
		}

		if (optionList != null) {
			for (Option option : optionList) {
				if (option.getPrice().compareTo(new BigDecimal("0")) != 0) {
					bigDecimal = bigDecimal.add(option.getPrice());
				}
			}
		}
		if (cost.compareTo(BigDecimal.ZERO) != 0) {
			bigDecimal = cost.add(bigDecimal);
		} else {
			bigDecimal = new BigDecimal("0.00");
		}
		return bigDecimal.toString();
	}


	/**
	 * @return 优惠价格 +做法价格+套餐项价格+定制项价格 最后得到的应付价格 实付
	 */
	public String getCostPrice() {
		BigDecimal bigDecimal = new BigDecimal(0);
		if (cookList != null) {
			for (CookingMethod item : cookList) {
				if (!TextUtils.isEmpty(item.price)) {
					bigDecimal = bigDecimal.add(new BigDecimal(item.price));
				}
			}
		}
		if (subItemList != null) {
			for (Package item : subItemList) {
				bigDecimal = bigDecimal.add(new BigDecimal(item.extraCost));
				if (item.optionList != null && item.optionList.size() > 0) {
					for (Option option : item.optionList) {
						if (option.getPrice().compareTo(new BigDecimal("0")) != 0) {
							bigDecimal = bigDecimal.add(option.getPrice());
						}
					}
				}
			}
		}
		if (tasteList != null) {
			for (Flavor item : tasteList) {
				if (item.tasteExtraCost != 0) {
					bigDecimal = bigDecimal.add(new BigDecimal(item.tasteExtraCost));
				}
			}
		}
		if (optionList != null) {
			for (Option option : optionList) {
				if (option.getPrice().compareTo(new BigDecimal("0")) != 0) {
					bigDecimal = bigDecimal.add(option.getPrice());
				}
			}
		}

		if (tempPrice.compareTo(BigDecimal.ZERO) != 0) {
			bigDecimal = new BigDecimal(tempPrice.toString()).add(bigDecimal);
		} else {
			bigDecimal = new BigDecimal("0.00");
		}
		return bigDecimal.toString();
	}

	/**
	 * 获取菜品cost
	 *
	 * @return
	 */
	public BigDecimal getDishCost() {
		return new BigDecimal(getCostPrice());
	}

	public BigDecimal getDishPrice() {
		return new BigDecimal(getTotalPrice());
	}


	public float sum() {
		return new BigDecimal(getTotalPrice()).multiply(new BigDecimal(quantity)).floatValue();
	}

	public int getDishId() {
		return dishId;
	}

	public void setDishId(int dishId) {
		this.dishId = dishId;
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
		return Alias;
	}

	public void setAlias(String alias) {
		Alias = alias;
	}

	public String getDishNameEnglish() {
		return dishNameEnglish;
	}

	public void setDishNameEnglish(String dishNameEnglish) {
		this.dishNameEnglish = dishNameEnglish;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public BigDecimal getMemberPrice() {
		return memberPrice;
	}

	public void setMemberPrice(BigDecimal memberPrice) {
		this.memberPrice = memberPrice;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}


	public int getIsPackage() {
		return isPackage;
	}

	public void setIsPackage(int isPackage) {
		this.isPackage = isPackage;
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

	public int getDishCount() {
		return dishCount;
	}

	public void setDishCount(int dishCount) {
		this.dishCount = dishCount;
	}

	public BigDecimal getAllOrderDisCountSubtractPrice() {
		if (allOrderDisCountSubtractPrice == null) {
			allOrderDisCountSubtractPrice = new BigDecimal("0.00");
		}
		return allOrderDisCountSubtractPrice;
	}

	public void setAllOrderDisCountSubtractPrice(BigDecimal allOrderDisCountSubtractPrice) {
		this.allOrderDisCountSubtractPrice = allOrderDisCountSubtractPrice;
	}

	public BigDecimal getSingleOrderDisCountSubtracePrice() {
		if (singleOrderDisCountSubtracePrice == null) {
			singleOrderDisCountSubtracePrice = new BigDecimal("0.00");
		}
		return singleOrderDisCountSubtracePrice;
	}

	public void setSingleOrderDisCountSubtracePrice(BigDecimal singleOrderDisCountSubtracePrice) {
		this.singleOrderDisCountSubtracePrice = singleOrderDisCountSubtracePrice;
	}

	public String getDishUnitStr() {
		return dishUnitStr;
	}

	public void setDishUnitStr(String dishUnitStr) {
		this.dishUnitStr = dishUnitStr;
	}


	public String getDishComment() {
		return dishComment;
	}

	public void setDishComment(String dishComment) {
		this.dishComment = dishComment;
	}

	public String getDishKindStr() {
		return dishKindStr;
	}

	public void setDishKindStr(String dishKindStr) {
		this.dishKindStr = dishKindStr;
	}

	public int getSeq() {
		return seq;
	}

	public void setSeq(int seq) {
		this.seq = seq;
	}

	public boolean haveCooker() {
		if (cookList != null) {
			return true;
		}
		return cook != null && cook.size() > 0;
	}

	public boolean haveTaste() {
		if (tasteList != null) {
			return true;
		}
		return taste != null && taste.size() > 0;
	}

	public boolean haveOptionCategory() {
		if (optionList != null) {
			return true;
		}
		return optionCategoryList != null && optionCategoryList.size() > 0;
	}

	public boolean haveMarkList() {
		if (marketList != null && marketList.size() > 0) {
			return true;
		}
		return false;
	}

	public boolean isOptionRequired() {
		boolean isRequired = false;
		if (haveOptionCategory()) {
			for (OptionCategory optionCategory : optionCategoryList) {
				for (Option option : optionCategory.optionList) {
					if (option.required == false) {
						isRequired = true;
						return isRequired;
					}
					if (option.required) {
						isRequired = false;
					}
				}
			}
		}
		return isRequired;
	}

	public String getDishUnit() {
		return dishUnit;
	}

	public void setDishUnit(String dishUnit) {
		this.dishUnit = dishUnit;
	}

	public int getDishUnitID() {
		return dishUnitID;
	}

	public void setDishUnitID(int dishUnitID) {
		this.dishUnitID = dishUnitID;
	}


	public List<OptionCategory> getOptionCategoryList() {
		return optionCategoryList;
	}

	public void setOptionCategoryList(List<OptionCategory> optionCategoryList) {
		this.optionCategoryList = optionCategoryList;
	}

	public ArrayList<DishDiscount> getDishDiscount() {
		if (dishDiscount == null) {
			dishDiscount = new ArrayList<>();
		}
		return dishDiscount;
	}

	public void setDishDiscount(List<DishDiscount> dishDiscount) {
		this.dishDiscount = (ArrayList<DishDiscount>) dishDiscount;
		if (this.dishDiscount == null) {
			this.dishDiscount = new ArrayList<>();
		}
	}

	public BigDecimal getWaiDai_cost() {
		if (current_selectTakeOut != 0) {
			return new BigDecimal("0.00");
		}
		return waiDai_cost;
	}

	public void setWaiDai_cost(BigDecimal waiDai_cost) {
		this.waiDai_cost = waiDai_cost;
	}

	public BigDecimal getTempPrice() {
		BigDecimal waidaiCost     = new BigDecimal("0.00");
		BigDecimal waidaiCostTemp = new BigDecimal("0.00");
		//        if ("TAKE_OUT".equals(PosInfo.getInstance().getOrderType())) {
		//            waidaiCost = getWaiDai_cost();
		//        }
		if (tempPrice.compareTo(BigDecimal.ZERO) != 0) {
			waidaiCostTemp = waidaiCost.add(tempPrice).setScale(2, BigDecimal.ROUND_DOWN);
		} else {
			waidaiCostTemp = new BigDecimal("0.00");
		}
		return waidaiCostTemp;
	}

	public void setTempPrice(BigDecimal tempPrice) {
		this.tempPrice = tempPrice;
	}

	public String getAppid() {
		return appid;
	}

	public void setAppid(String appid) {
		this.appid = appid;
	}

	public Long getBrandid() {
		return brandid;
	}

	public void setBrandid(Long brandid) {
		this.brandid = brandid;
	}

	public Long getStoreid() {
		return storeid;
	}

	public void setStoreid(Long storeid) {
		this.storeid = storeid;
	}

	public String getDishKind() {
		return dishKind;
	}

	public void setDishKind(String dishKind) {
		this.dishKind = dishKind;
	}

	public String getDishKindColor() {
		return dishKindColor;
	}

	public void setDishKindColor(String dishKindColor) {
		this.dishKindColor = dishKindColor;
	}

	public long getDishTimeID() {
		return dishTimeID;
	}

	public void setDishTimeID(long dishTimeID) {
		this.dishTimeID = dishTimeID;
	}

	public ArrayList<Specification> getSpecificationList() {
		return specificationList;
	}

	public void setSpecificationList(ArrayList<Specification> specificationList) {
		this.specificationList = specificationList;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public List<MarketObject> getTempMarketList() {
		return tempMarketList;
	}

	public void setTempMarketList(List<MarketObject> tempMarketList) {
		this.tempMarketList = tempMarketList;
	}

	public BigDecimal getTempCost() {
		return tempCost;
	}

	public void setTempCost(BigDecimal tempCost) {
		this.tempCost = tempCost;
	}

	public Integer getDishOptionId() {
		return dishOptionId;
	}

	public void setDishOptionId(Integer dishOptionId) {
		this.dishOptionId = dishOptionId;
	}

	public int getTempQuantity() {
		return tempQuantity;
	}

	public void setTempQuantity(int tempQuantity) {
		this.tempQuantity = tempQuantity;
	}

	public BigDecimal getTemporaryPrice() {
		return temporaryPrice;
	}

	public void setTemporaryPrice(BigDecimal temporaryPrice) {
		this.temporaryPrice = temporaryPrice;
	}

	public BigDecimal getWaimaiTempPrice() {
		return waimaiTempPrice;
	}

	public void setWaimaiTempPrice(BigDecimal waimaiTempPrice) {
		this.waimaiTempPrice = waimaiTempPrice;
	}

	public BigDecimal getWaimaiPrice() {
		return waimaiPrice;
	}

	public void setWaimaiPrice(BigDecimal waimaiPrice) {
		this.waimaiPrice = waimaiPrice;
	}

	public int getDishModifyCount() {
		return dishModifyCount;
	}

	public void setDishModifyCount(int dishModifyCount) {
		this.dishModifyCount = dishModifyCount;
	}

	public BigDecimal getOrderDishCost() {
		BigDecimal dishBigCost = new BigDecimal("0.00");
		if (getMarketList() != null && getMarketList().size() > 0)//如果有促销打折活动的话 要取打折后的菜品
		{
			boolean isHaveSales = false;
			for (MarketObject marketObject : getMarketList()) {
				if (marketObject.getMarketType() == MarketType.SALES || marketObject
						.getMarketType() == MarketType.MANUAL)//如果有促销打折活动的话 要取打折后的菜品
				{
					isHaveSales = true;
					break;
				}
			}
			if (isHaveSales) {
				dishBigCost = getTempPrice();
			} else {
				dishBigCost = new BigDecimal(getCostPrice());
			}
		} else {
			dishBigCost = new BigDecimal(getCostPrice());
		}
		return dishBigCost;
	}

	public BigDecimal activeMoney() {
		BigDecimal activityMoney = new BigDecimal("0.00");
		if (marketList != null && marketList.size() > 0) {
			int size = marketList.size();
			for (int i = 0; i < size; i++) {
				MarketObject marketOb = marketList.get(i);
				if (marketOb != null) {
					if (marketOb.getMarketType() == MarketType.MANUAL && marketOb
							.getDiscountType() == 1) {
						activityMoney = activityMoney.add(marketOb.getReduceCash()
								.multiply(new BigDecimal(getQuantity())));
					} else {
						activityMoney = activityMoney.add(marketOb.getReduceCash());
					}
				}
			}
		}
		return activityMoney;
	}

	/**
	 * 菜品打折后的价-全单打折-单品打折
	 *
	 * @return
	 */
	public BigDecimal getDishRealCost() {
		return getOrderDishCost().subtract(getAllOrderDisCountSubtractPrice())
				.subtract(getSingleOrderDisCountSubtracePrice());
	}

	/**
	 * 菜品打折后的价-全单打折
	 *
	 * @return
	 */
	public BigDecimal getDishSubAllOrderDisCount() {
		return getOrderDishCost().subtract(getAllOrderDisCountSubtractPrice());
	}


	public boolean equals(Dish obj) {
		return obj instanceof Dish && this.dishId > 0 && this.dishId == (((Dish) obj).getDishId());
	}

	public boolean getUsingMarket() {
		if (!ToolsUtils.isList(marketList)) {
			for (MarketObject market2 : marketList) {
				if (market2.getMarketType() == MarketType.SALES) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean isHava = false;

	public boolean isHava() {
		return isHava;
	}

	public void setHava(boolean hava) {
		isHava = hava;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}
}
