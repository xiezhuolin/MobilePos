package com.acewill.paylibrary.epay;

public class AliGoodsItem 
{
	//"goods_id": "apple-01", "goods_name": "ipad", "goods_category": "7788230", "price": "88.88", "quantity": "1"
	private String goods_id = "";
	
	private String goods_name = "";
	
	private String goods_category = "";
	
	private String price = "";
	
	private String quantity = "";

	public String getGoods_id() {
		return goods_id;
	}

	public void setGoods_id(String goods_id) {
		this.goods_id = goods_id;
	}

	public String getGoods_name() {
		return goods_name;
	}

	public void setGoods_name(String goods_name) {
		this.goods_name = goods_name;
	}

	public String getGoods_category() {
		return goods_category;
	}

	public void setGoods_category(String goods_category) {
		this.goods_category = goods_category;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getQuantity() {
		return quantity;
	}

	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}
}
