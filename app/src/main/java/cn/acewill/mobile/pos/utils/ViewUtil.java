package cn.acewill.mobile.pos.utils;

import android.app.Activity;
import android.content.Context;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.lang.reflect.Field;

/**
 * 动态计算LitView/gridView高度
 * Created by Dhh on 2016/2/26.
 */
public class ViewUtil {
	/**
	 * 动态计算LitView高度
	 * @param listView
	 */
    public static void setListViewHeight(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }
    
    /**
     * 动态计算GridView高度
     * @param gridView
     */
	public static void setGridViewHeightBasedOnChildren(GridView gridView) {
		// 获取GridView对应的Adapter
		ListAdapter listAdapter = gridView.getAdapter();
		if (listAdapter == null) {
			return;
		}
		int rows;
		int columns = 0;
		int horizontalBorderHeight = 0;
		Class<?> clazz = gridView.getClass();
		try {
			// 利用反射，取得每行显示的个数
			Field column = clazz.getDeclaredField("mRequestedNumColumns");
			column.setAccessible(true);
			columns = (Integer) column.get(gridView);
			// 利用反射，取得横向分割线高度
			Field horizontalSpacing = clazz.getDeclaredField("mRequestedHorizontalSpacing");
			horizontalSpacing.setAccessible(true);
			horizontalBorderHeight = (Integer) horizontalSpacing.get(gridView);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}// 判断数据总数除以每行个数是否整除。不能整除代表有多余，需要加一行
		if (listAdapter.getCount() % columns > 0) {
			rows = listAdapter.getCount() / columns + 1;
		} else {
			rows = listAdapter.getCount() / columns;
		}
		int totalHeight = 0;
		for (int i = 0; i < rows; i++) { // 只计算每项高度*行数
			View listItem = listAdapter.getView(i, null, gridView);
			listItem.measure(0, 0); // 计算子项View 的宽高
			totalHeight += listItem.getMeasuredHeight(); // 统计所有子项的总高度
		}
		ViewGroup.LayoutParams params = gridView.getLayoutParams();
		params.height = totalHeight + horizontalBorderHeight * (rows - 1);// 最后加上分割线总高度
		gridView.setLayoutParams(params);
	}

	/**
	 * 设置页面占屏幕高宽
	 * @param context
     */
	public static void setActivityWindow(Context context, int width, int height){
		Window dialogWindow = ((Activity)context).getWindow();
		WindowManager m = ((Activity)context).getWindowManager();
		Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
		WindowManager.LayoutParams p = dialogWindow.getAttributes(); // 获取对话框当前的参数值

		if(width== LinearLayout.LayoutParams.MATCH_PARENT || width==LinearLayout.LayoutParams.WRAP_CONTENT){
			p.width = width;
		}else {
			p.width = (int) (d.getWidth() * width * 0.1);
		}
		if(height==LinearLayout.LayoutParams.MATCH_PARENT || height==LinearLayout.LayoutParams.WRAP_CONTENT){
			p.height = height;
		}else {
			p.height = (int) (d.getHeight() * height * 0.1);
		}
		dialogWindow.setAttributes(p);
	}

	//显示键盘
	public static void showKey(final EditText view){
		view.postDelayed(new Runnable() {
			@Override
			public void run() {
				view.setFocusable(true); //reg_username为聚焦的EditText

				view.setFocusableInTouchMode(true);

				view.requestFocus();

				InputMethodManager inputManager =(InputMethodManager)view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

				inputManager.showSoftInput(view,0);
			}
		},250);
	}

	//隐藏键盘
	public static void hiddenKey(final EditText view){
		InputMethodManager inputManager =(InputMethodManager)view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		inputManager.hideSoftInputFromInputMethod(view.getWindowToken(), 0);
	}

}
