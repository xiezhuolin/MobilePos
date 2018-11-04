package cn.acewill.mobile.pos.utils;

import android.annotation.SuppressLint;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * 格式
 * @author aqw
 *
 */
@SuppressLint("NewApi") 
public class FormatUtils {
	
	/**
	 * 保留两位小数 不四舍五入
	 * @param d
	 * @return
	 */
	public static String getDoubleT(double d){
		DecimalFormat formater = new DecimalFormat();
        formater.setMaximumFractionDigits(2);
        formater.setGroupingSize(0);
        formater.setRoundingMode(RoundingMode.FLOOR);
		return formater.format(d);
	}
	/**
	 * 保留两位小数
	 * @param d
	 * @return
	 */
	public static String getDoubleW(double d){
		DecimalFormat priceFormat = new DecimalFormat("######0.00");
		return priceFormat.format(d);
	}

	/**
	 * 保留两位小数 不四舍五入
	 * @param d
	 * @return
	 */
	public static String getDoubleT(float d){
		DecimalFormat formater = new DecimalFormat();
		formater.setMaximumFractionDigits(2);
		formater.setGroupingSize(0);
		formater.setRoundingMode(RoundingMode.FLOOR);
		return formater.format(d);
	}
	/**
	 * 保留两位小数
	 * @param d
	 * @return
	 */
	public static String getDoubleW(float d){
		DecimalFormat priceFormat = new DecimalFormat("######0.00");
		return priceFormat.format(d);
	}

	public static double formatDBig(BigDecimal bigDecimal){
		return bigDecimal.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
	}

	public static float formatFBig(BigDecimal bigDecimal){
		return bigDecimal.setScale(2,BigDecimal.ROUND_HALF_UP).floatValue();
	}

	//计算金额键盘快捷金额
	public static int[] getMoney(int money) {
		int[] temp = new int[3];
		try {
			if(money<100){
				if(money<5){
					temp[0] = 5;
					temp[1] = 10;
					temp[2] = 20;
				}else if(money<10){
					temp[0] = 10;
					temp[1] = 20;
					temp[2] = 50;
				}else{
					int bit = money%10;//个位
					int ten = money/10;//十位

					if(1<=ten&&ten<3){
						if(bit==0){
							temp[0] = (ten+1)*10;
							temp[1] = (ten+2)*10;
							temp[2] = (ten+3)*10;
						}else if(bit<5){//23
							temp[0] = Integer.parseInt(ten+"5");
							temp[1] = (ten+1)*10;
							temp[2] = temp[1] + bit;
						}else{//37
							temp[0] = (ten+1)*10;
							temp[1] = (ten+2)*10;
							temp[2] = Integer.parseInt("50");
						}
					}else{
						if(bit==0){
							temp[0] = Integer.parseInt(ten+"5");
							temp[1] = (ten+2)*10;
							temp[2] = (ten+3)*10;
						}else if(bit<5){//53
							temp[0] = Integer.parseInt(ten+"5");
							temp[1] = (ten+1)*10;
							temp[2] = temp[1] + bit;
						}else{//57
							temp[0] = (ten+1)*10;
							temp[1] = (ten+2)*10;
							temp[2] = Integer.parseInt("100");
						}
					}
				}
			}else{//大于100
				String m = money+"";
				int hund = money/100;

				int bit = Integer.parseInt(m.substring(m.length()-1));//个位
				int ten = Integer.parseInt(m.substring(m.length()-2,m.length()-1));//十位

				if(ten==0){
					if(bit<5){//103
						temp[0] = Integer.parseInt(hund+"05");
						temp[1] = Integer.parseInt(hund+"10");
						temp[2] = temp[1] + bit;
					}else{//107
						temp[0] = Integer.parseInt(hund+"10");
						temp[1] = Integer.parseInt(hund+"20");
						temp[2] = Integer.parseInt(hund+"50");
					}
				}else if(1<=ten&&ten<3){
					if(bit<5){//223
						temp[0] = Integer.parseInt(hund+""+ten+"5");
						temp[1] = Integer.parseInt(hund+""+(ten+1)*10);
						temp[2] = temp[1] + bit;
					}else{//327
						temp[0] = Integer.parseInt(hund+""+(ten+1)*10);
						temp[1] = Integer.parseInt(hund+""+(ten+2)*10);
						temp[2] = Integer.parseInt(hund+"50");
					}
				}else if(3<=ten&&ten<8){
					if(bit<5){//150
						temp[0] = Integer.parseInt(hund+""+ten+"5");
						temp[1] = Integer.parseInt(hund+""+(ten+1)*10);
						temp[2] = temp[1] + bit;
					}else{//486
						temp[0] = Integer.parseInt(hund+""+(ten+1)*10);
						temp[1] = Integer.parseInt(hund+""+(ten+2)*10);
						temp[2] = Integer.parseInt((hund+1)+"00");
					}
				}else{
					if(bit<5){//383
						temp[0] = Integer.parseInt(hund+""+ten+"5");
						temp[1] = Integer.parseInt((hund+1)+"00");
						temp[2] = temp[1] + bit;
					}else{//387
						temp[0] = Integer.parseInt((hund+1)+"00");
						temp[1] = temp[0] + bit;
						temp[2] = Integer.parseInt((hund+1)+"10");
					}
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
			temp[0] = money;
			temp[1] = money*2;
			temp[2] = money*2+50;
		}
		return temp;
	}


}
