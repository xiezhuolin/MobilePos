package cn.acewill.mobile.pos.utils;

import java.text.DecimalFormat;

public class PriceUtil {
	public static String formatPrice(String price){
		Double m = Double.parseDouble(price);
		DecimalFormat df = new DecimalFormat("0.000");
		return df.format(m);
	}
	public static String formatPrice(float price){
		return formatPrice(String.valueOf(price));
	}
}
