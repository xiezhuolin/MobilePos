package cn.acewill.mobile.pos.model.dish;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 套餐group内容 eg:几选几的内容
 */
public class SetmealGroupModel implements Serializable{
	/**
	 * 需要选择的数量
	 */
	public int max;
	/**
	 * 已选的数量
	 */
	public int quantity = 0;
	/**
	 * 所属套餐id
	 */
	public String kindID;
	/**
	 * 套餐项
	 */
	public ArrayList<Dish> setmeals;
	/**
	 * 套餐名
	 */
	public String kindName;
	/**
	 * 是否能重复 0=不能  1=能
	 */
	public String isRepeatable;
}
