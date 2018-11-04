package cn.acewill.mobile.pos.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

@SuppressLint("NewApi")
public class ScrolListView extends ListView {
	public ScrolListView(Context context) {
		super(context);
	}

	public ScrolListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ScrolListView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	// public ScrollListView(Context context, AttributeSet attrs, int
	// defStyleAttr, int defStyleRes) {
	// super(context, attrs, defStyleAttr, defStyleRes);
	// }

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);
	}
}
