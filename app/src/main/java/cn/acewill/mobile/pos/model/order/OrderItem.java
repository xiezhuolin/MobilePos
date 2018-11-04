package cn.acewill.mobile.pos.model.order;

import android.text.TextUtils;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ModelContainer;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import cn.acewill.mobile.pos.factory.AppDatabase;
import cn.acewill.mobile.pos.model.KitchenStall;
import cn.acewill.mobile.pos.model.MarketObject;
import cn.acewill.mobile.pos.model.MarketType;
import cn.acewill.mobile.pos.model.dish.CookingMethod;
import cn.acewill.mobile.pos.model.dish.Dish;
import cn.acewill.mobile.pos.model.dish.DishDiscount;
import cn.acewill.mobile.pos.model.dish.Flavor;
import cn.acewill.mobile.pos.model.dish.Option;
import cn.acewill.mobile.pos.model.dish.OptionCategory;
import cn.acewill.mobile.pos.model.dish.Specification;
import cn.acewill.mobile.pos.utils.Constant;
import cn.acewill.mobile.pos.utils.ToolsUtils;


/**
 * 对应订单里的一项，一项可以对应多份同样的菜
 * <p>
 * 注意： 一下操作会生成2个OrderItem， 也就是每次下单操作就会生成新的OrderItem
 * 1. 点2份红豆粥，下单
 * 2. 通过添加菜品再点一份红豆粥，下单
 * <p>
 * Created by Acewill on 2016/6/2.
 */
@com.raizlabs.android.dbflow.annotation.Table(name = "order_item", database = AppDatabase.class)
@ModelContainer
public class OrderItem extends BaseModel implements Serializable {
	@Column
	@PrimaryKey(autoincrement = true)
	public long id;
	@Column
	public long orderId;

	@SerializedName("dishID")
	public  long       dishId;
	public  String     appId;
	public  String     brandId;
	public  String     storeId;
	public  String     dishName;
	public  BigDecimal price;
	public  BigDecimal memberPrice;
	public  BigDecimal cost;
	public  BigDecimal tempPrice;
	private String     dishUnit;//菜品单位的名称
	private String     imageName;
	/**
	 * 临时菜品price  用来切换外卖价格和菜品原价进行切换使用
	 */
	private BigDecimal temporaryPrice;
	/**
	 * 菜品外卖时的价格
	 */
	private BigDecimal waimaiPrice;
	/**
	 * 菜品临时存储外卖时的价格  用来切换外卖价格和菜品原价进行切换使用
	 */
	private BigDecimal waimaiTempPrice;
	private BigDecimal waiDai_cost = new BigDecimal("0.00");
	public int     quantity;
	public String  createdAt;
	public boolean gift;
	public String  dishKindStr;
	public int     dishUnitID;
	public String  dishKind;//菜品分类
	public String  sku;
	public String  skuStr;
	public int        rejectedQuantity = 0;
	public String     packName         = "";//菜品所属套餐名称
	public String     tempPackName     = "";
	public BigDecimal tempCost         = new BigDecimal("0.00");
	public BigDecimal itemPrice        = new BigDecimal("0.00");

	public OrderItemStatus itemStatus;
	public ServiceStatus   serviceStatus;
	public int             current_select;
	public int             current_refund_select;//退菜选择的份数

	@Expose(serialize = false)
	public  String createdBy; //哪个用户创建的？
	@Expose(serialize = false)
	public  long   rejectedBy; //哪个用户退菜的
	@Expose(serialize = false)
	public  int    rejectedReason; // 拒绝原因
	@Expose(serialize = false)
	public  double orderTime;            //点这个菜的时间
	@Expose(serialize = false)
	public  int    amount;  //点了多少份？需要看这个菜的unit是什么
	@Expose(serialize = false)
	public  int    rejectedAmount;  //退了多少份？
	//    @Expose(serialize = false)
	//    private CookingMethod cookingMethod;
	//    @Expose(serialize = false)
	//    private Flavor flavor;
	@Expose(serialize = false)
	public  String comment; // 在做法和口味之外 需要特殊注意的地方
	/**
	 * 退菜人员名称
	 */
	private String returnUserName;
	/**
	 * 授权退菜人员名称
	 */
	private String authUserName;
	/**
	 * 餐段名称
	 */
	private String menuName;

	/**
	 * 全单打折要减的钱
	 */
	public BigDecimal allOrderDisCountSubtractPrice;

	public int isPackage;//是否是套餐（0普通菜品，1套餐）

	public boolean isPackage() {
		return isPackage == 1;
	}

	/**
	 * discounted 1是参与 0不参与
	 */
	public int discounted = 1;

	public boolean isParticipationDisCount() {
		return discounted == 1;
	}

	public BigDecimal getMemberPrice() {
		return memberPrice;
	}

	public void setMemberPrice(BigDecimal memberPrice) {
		this.memberPrice = memberPrice;
	}

	public int getDiscounted() {
		return discounted;
	}

	public void setDiscounted(int discounted) {
		this.discounted = discounted;
	}

	public ArrayList<CookingMethod> cookList;
	public ArrayList<Flavor>        tasteList;
	public ArrayList<Option>        optionList;
	public ArrayList<Dish.Package>  subItemList;

	public ArrayList<DishDiscount> dishDiscount;// :[ 菜品特价方案，array
	public boolean isSelectDisCount = false;
	public boolean isSelectItem     = false;

	public List<OptionCategory>     optionCategoryList;//定制项数组
	public List<Dish.PackageItem>   packageItems;//   套餐项, array, 当菜品是普通项的时候为空；当菜品是套餐的时候必须有数据
	public ArrayList<Specification> specificationList;

	public BigDecimal kitDishMoney = new BigDecimal("0");//厨房小票需要打印的菜品金额

	public boolean isPackageItem = false;//当前orderItem是否以前是套餐项

	public List<MarketObject> marketList     = new CopyOnWriteArrayList<>(); //这个菜品上使用过的营销方案, 可以是单品的，也可以是全单的，全单的话需要按比例平摊到菜品上
	public List<MarketObject> tempMarketList = new CopyOnWriteArrayList<>(); //这个菜品上使用过的营销方案, 可以是单品的，也可以是全单的，全单的话需要按比例平摊到菜品上

	public BigDecimal getWaiDai_cost() {
		return waiDai_cost;
	}

	public void setWaiDai_cost(BigDecimal waiDai_cost) {
		this.waiDai_cost = waiDai_cost;
	}

	public BigDecimal getKitDishMoney() {
		return kitDishMoney;
	}

	public void setKitDishMoney(BigDecimal kitDishMoney) {
		this.kitDishMoney = kitDishMoney;
	}

	public boolean isPackageItem() {
		return isPackageItem;
	}

	public void setPackageItem(boolean packageItem) {
		isPackageItem = packageItem;
	}

	public boolean isSelectItem() {
		return isSelectItem;
	}

	public void setSelectItem(boolean selectItem) {
		isSelectItem = selectItem;
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

	public boolean haveOptionCategory() {
		if (optionList != null) {
			return true;
		}
		return optionCategoryList != null && optionCategoryList.size() > 0;
	}

	public boolean getPrintState() {
		return isPrint;
	}

	public void setPrintState(boolean print) {
		isPrint = print;
	}

	public boolean isPrint = false;//是否被打印过

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getOrderId() {
		return orderId;
	}

	public void setOrderId(long orderId) {
		this.orderId = orderId;
	}

	public List<KitchenStall> getKitchenStallList() {
		return kitchenStallList;
	}

	public void setKitchenStallList(List<KitchenStall> kitchenStallList) {
		this.kitchenStallList = kitchenStallList;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public OrderItemStatus getItemStatus() {
		return itemStatus;
	}

	public void setItemStatus(OrderItemStatus itemStatus) {
		this.itemStatus = itemStatus;
	}

	public ServiceStatus getServiceStatus() {
		return serviceStatus;
	}

	public void setServiceStatus(ServiceStatus serviceStatus) {
		this.serviceStatus = serviceStatus;
	}


	public double getOrderTime() {
		return orderTime;
	}

	public void setOrderTime(double orderTime) {
		this.orderTime = orderTime;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public int getRejectedAmount() {
		return rejectedAmount;
	}

	public void setRejectedAmount(int rejectedAmount) {
		this.rejectedAmount = rejectedAmount;
	}

	//    public CookingMethod getCookingMethod() {
	//        return cookingMethod;
	//    }
	//
	//    public void setCookingMethod(CookingMethod cookingMethod) {
	//        this.cookingMethod = cookingMethod;
	//    }
	//
	//    public Flavor getFlavor() {
	//        return flavor;
	//    }
	//
	//    public void setFlavor(Flavor flavor) {
	//        this.flavor = flavor;
	//    }

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public long getDishId() {
		return dishId;
	}

	public void setDishId(long dishId) {
		this.dishId = dishId;
	}

	public BigDecimal getPrice() {
		if (price == null) {
			price = new BigDecimal(0);
		}
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public BigDecimal getCost() {
		BigDecimal tempMarketPrice = new BigDecimal("0.000");
		if (!ToolsUtils.isList(getTempMarketList())) {
			for (MarketObject marketObject1 : getTempMarketList()) {
				if (marketObject1.getMarketType() == MarketType.DISCOUNT) {
					tempMarketPrice = marketObject1.getReduceCash();
				}
			}
		}
		if (cost == null) {
			cost = tempMarketPrice = new BigDecimal("0.000");
		}
		return cost.add(tempMarketPrice);
	}

	public void setCost(BigDecimal cost) {
		this.cost = cost;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public boolean isGift() {
		return gift;
	}

	public void setGift(boolean gift) {
		this.gift = gift;
	}

	public String getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}

	public String getDishName() {
		return dishName;
	}

	public void setDishName(String dishName) {
		this.dishName = dishName;
	}

	public ArrayList<Flavor> getTasteList() {
		return tasteList;
	}

	public void setTasteList(ArrayList<Flavor> tasteList) {
		this.tasteList = tasteList;
	}

	public ArrayList<Dish.Package> getSubItemList() {
		return subItemList;
	}

	public void setSubItemList(ArrayList<Dish.Package> subItemList) {
		this.subItemList = subItemList;
	}

	public ArrayList<CookingMethod> getCookList() {
		return cookList;
	}

	public void setCookList(ArrayList<CookingMethod> cookList) {
		this.cookList = cookList;
	}

	public String getDishKindStr() {
		return dishKindStr;
	}

	public void setDishKindStr(String dishKindStr) {
		this.dishKindStr = dishKindStr;
	}

	public int getDishUnitID() {
		return dishUnitID;
	}

	public void setDishUnitID(int dishUnitID) {
		this.dishUnitID = dishUnitID;
	}

	public ArrayList<Option> getOptionList() {
		return optionList;
	}

	public void setOptionList(ArrayList<Option> optionList) {
		this.optionList = optionList;
	}

	public int getRejectedQuantity() {
		return rejectedQuantity;
	}

	public void setRejectedQuantity(int selectQuantity) {
		this.rejectedQuantity = selectQuantity;
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

	public String getSku() {
		return sku;
	}

	public void setSku(String sku) {
		this.sku = sku;
	}

	public int getIsPackage() {
		return isPackage;
	}

	public void setIsPackage(int isPackage) {
		this.isPackage = isPackage;
	}

	public String getSkuStr() {
		return skuStr;
	}

	public void setSkuStr(String skuStr) {
		this.skuStr = skuStr;
	}

	public ArrayList<DishDiscount> getDishDiscount() {
		return dishDiscount;
	}

	public void setDishDiscount(ArrayList<DishDiscount> dishDiscount) {
		this.dishDiscount = dishDiscount;
	}

	public BigDecimal getTempPrice() {
		if (tempPrice == null) {
			return price;
		}
		return tempPrice;
	}

	public void setTempPrice(BigDecimal tempPrice) {

		this.tempPrice = tempPrice;
	}

	public String getDishKind() {
		return dishKind;
	}

	public void setDishKind(String dishKind) {
		this.dishKind = dishKind;
	}

	public ArrayList<Specification> getSpecificationList() {
		return specificationList;
	}

	public void setSpecificationList(ArrayList<Specification> specificationList) {
		this.specificationList = specificationList;
	}

	public List<OptionCategory> getOptionCategoryList() {
		return optionCategoryList;
	}

	public void setOptionCategoryList(List<OptionCategory> optionCategoryList) {
		this.optionCategoryList = optionCategoryList;
	}

	public String getPackName() {
		return packName;
	}

	public void setPackName(String packName) {
		this.packName = packName;
	}

	public OrderItem() {

	}


	public OrderItem(Dish dish) {
		dishId = dish.getDishId();
		memberPrice = dish.getMemberPrice();
		dishName = dish.dishName;
		price = dish.getPrice();
		cost = dish.getPrice();
		quantity = dish.quantity;
		tempPrice = dish.getPrice();
		temporaryPrice = dish.getPrice();
		waimaiPrice = dish.getWaimaiPrice();
		waimaiTempPrice = dish.getWaimaiPrice();
		dishKind = dish.dishKind;
		dishUnit = dish.getDishUnit();
		dishUnitID = dish.getDishUnitID();
		dishDiscount = dish.getDishDiscount();
		specificationList = dish.getSpecificationList();
		optionCategoryList = dish.getOptionCategoryList();
		quantity = dish.quantity;
		optionList = dish.optionList;
		discounted = dish.discounted;
		subItemList = dish.subItemList;
	}

	public String getTempPackName() {
		return tempPackName;
	}

	public void setTempPackName(String tempPackName) {
		this.tempPackName = tempPackName;
	}

	public int getCurrent_select() {
		return current_select;
	}

	public void setCurrent_select(int current_select) {
		this.current_select = current_select;
	}

	public String getReturnUserName() {
		return returnUserName;
	}

	public void setReturnUserName(String returnUserName) {
		this.returnUserName = returnUserName;
	}

	public String getAuthUserName() {
		return authUserName;
	}

	public void setAuthUserName(String authUserName) {
		this.authUserName = authUserName;
	}

	public String getMenuName() {
		return menuName;
	}

	public void setMenuName(String menuName) {
		this.menuName = menuName;
	}

	public List<MarketObject> getMarketList() {
		return marketList;
	}

	public void setMarketList(List<MarketObject> marketList) {
		this.marketList = marketList;
	}

	public void setMarketList(List<MarketObject> marketList, List<MarketObject> tempMarketList) {
		this.marketList = marketList;
		if (!ToolsUtils.isList(tempMarketList)) {
			if (this.marketList == null) {
				this.marketList = new CopyOnWriteArrayList<>();
			}
			this.marketList.addAll(tempMarketList);
		}
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

	public BigDecimal getTemporaryPrice() {
		return temporaryPrice;
	}

	public void setTemporaryPrice(BigDecimal temporaryPrice) {
		this.temporaryPrice = temporaryPrice;
	}

	public void setOnlyCost(BigDecimal cost) {
		this.cost = cost;
	}

	public BigDecimal getItemPrice() {
		return itemPrice;
	}

	public void setItemPrice(BigDecimal itemPrice) {
		this.itemPrice = itemPrice;
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
			for (Dish.Package item : subItemList) {
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

	public BigDecimal getOrderDishCost() {
		if (tempPrice == null) {
			tempPrice = cost;
		}
		BigDecimal dishBigCost = new BigDecimal("0.00");
		//        if(getMarketList() != null && getMarketList().size() >0)//如果有促销打折活动的话 要取打折后的菜品
		//        {
		//            dishBigCost = tempPrice;
		//        }
		//        else{
		//        }
		dishBigCost = tempPrice;
		return dishBigCost;
	}


	private List<KitchenStall> kitchenStallList     = new CopyOnWriteArrayList<>();//菜品被多个档口绑定
	private boolean            isThroughLabelPrint  = false;//该菜品项是否是通过标签打印机打印
	private int                oiRePrintIdentifying = 0;//订单是否是重试补打
	private int                dishHavePrintCount   = 0;//这个菜在这个订单中的分单中一共要打印多少份

	public int getDishHavePrintCount() {
		return dishHavePrintCount;
	}

	public void setDishHavePrintCount(int count) {
		//如果该订单不是重试补打的类型
		if (oiRePrintIdentifying != Constant.EventState.PRINT_RETRY_REPRINT && dishHavePrintCount <= 0) {
			if (isThroughLabelPrint) {
				dishHavePrintCount = quantity;
			} else {
				dishHavePrintCount = count;
			}
		}
	}

	/**
	 * 减去一份
	 */
	public void subtractDishHavePrintCount() {
		this.dishHavePrintCount -= 1;
	}

	public int getOiRePrintIdentifying() {
		return oiRePrintIdentifying;
	}

	public void setOiRePrintIdentifying(int oiRePrintIdentifying) {
		this.oiRePrintIdentifying = oiRePrintIdentifying;
	}

	public boolean isThroughLabelPrint() {
		return isThroughLabelPrint;
	}

	public void setThroughLabelPrint(boolean throughLabelPrint) {
		isThroughLabelPrint = throughLabelPrint;
	}

	public String getDishUnit() {
		return dishUnit;
	}

	public void setDishUnit(String dishUnit) {
		this.dishUnit = dishUnit;
	}

	public String getImageName() {
		return imageName;
	}

	public void setImageName(String imageName) {
		this.imageName = imageName;
	}

	public int itemIndex;

	public int getItemIndex() {
		return itemIndex;
	}

	public void setItemIndex(int itemIndex) {
		this.itemIndex = itemIndex;
	}
}
