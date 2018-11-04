package cn.acewill.mobile.pos.config;

import android.app.Activity;
import android.util.DisplayMetrics;

import java.util.HashMap;

public class Configure {
	public static int screenHeight=0;
	public static int screenWidth=0;
	public static float screenDensity=0;

	public static HashMap<String,String> hashMap = new HashMap<>();
	
	public static void init(Activity context) {
		if(screenDensity==0||screenWidth==0||screenHeight==0){
			DisplayMetrics dm = new DisplayMetrics();
			context.getWindowManager().getDefaultDisplay().getMetrics(dm);
			Configure.screenDensity = dm.density;
			Configure.screenHeight = dm.heightPixels;
			Configure.screenWidth = dm.widthPixels;
		}
	}

	public static HashMap<String, String> getHashMap() {
		if(hashMap.isEmpty()){
			hashMap.put("Big Bang Theory", "http://szfileserver.419174855.mtmssdn.com/common/fileupload/20170713211927_5836.jpg");
//			hashMap.put("House of Cards", "http://cdn3.nflximg.net/images/3093/2043093.jpg");
//			hashMap.put("Game of Thrones", "http://images.boomsbeat.com/data/images/full/19640/game-of-thrones-season-4-jpg.jpg");
//			hashMap.put("Game of Thrones", "http://www.hdwallpapers.in/walls/helix_nebula_5k-wide.jpg");
		}
		return hashMap;
	}

	public static void setHashMap(HashMap<String, String> hashMap) {
		Configure.hashMap = hashMap;
	}
}
