package cn.acewill.mobile.pos.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;

/**
 * Created by aqw on 2016/9/14.
 */
public class CommonEditText extends EditText {

    private Context context;

    public CommonEditText(Context context) {
        super(context);
        this.context = context;
    }

    public CommonEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public CommonEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
    }

}
