package cn.acewill.mobile.pos.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.acewill.mobile.pos.R;
import cn.acewill.mobile.pos.utils.ScreenUtil;

/**
 * Created by aqw on 2016/8/17.
 */
public class TitleView extends RelativeLayout {
    private TextView title_right;
    private RelativeLayout title_group;
    private LinearLayout title_left;
    private TextView title_center;
    private LinearLayout right_left_ll;
    private ImageView right_icon;
    private LinearLayout title_right_icon;
    private ImageView title_icon;

    public TitleView(Context context) {
        super(context,null);
    }

    public TitleView(Context context, AttributeSet attrs) {
        super(context, attrs);

        isInEditMode();

        LayoutInflater.from(context).inflate(R.layout.title_view,this);

        title_group = (RelativeLayout)findViewById(R.id.title_group);
        title_left = (LinearLayout)findViewById(R.id.title_left);
        title_center = (TextView)findViewById(R.id.title_center);
        right_left_ll = (LinearLayout) findViewById(R.id.right_left_ll);
        right_icon = (ImageView)findViewById(R.id.right_icon);
        title_right_icon = (LinearLayout)findViewById(R.id.title_right_icon);
        title_icon = (ImageView)findViewById(R.id.title_icon);
        title_right = (TextView)findViewById(R.id.title_right);

        final TypedArray typedArray = getContext().obtainStyledAttributes(
                attrs, R.styleable.CustomTitleView);


        String titleText = typedArray.getString(R.styleable.CustomTitleView_titleText);
        String rightText = typedArray.getString(R.styleable.CustomTitleView_rightText);
        boolean leftVisible = typedArray.getBoolean(R.styleable.CustomTitleView_leftIconVisible,true);
        boolean rightTextVisible = typedArray.getBoolean(R.styleable.CustomTitleView_rightTextVisible,false);

        boolean rightIconVisible = typedArray.getBoolean(R.styleable.CustomTitleView_rightIconVisible,true);
        int rightIcon = typedArray.getResourceId(R.styleable.CustomTitleView_rightIcon, R.drawable.empty_icon);

        boolean rightLeftVisible = typedArray.getBoolean(R.styleable.CustomTitleView_rightLeftVisible,true);
        int rightLeftIcon = typedArray.getResourceId(R.styleable.CustomTitleView_rightLeftIcon, R.drawable.empty_icon);
        typedArray.recycle();

        int titleHeight = ScreenUtil.getScreenSize(context)[1];
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,titleHeight/9);
        title_group.setLayoutParams(params);

        title_left.setVisibility(leftVisible? View.VISIBLE:View.GONE);

        title_center.setText(titleText);

        right_left_ll.setVisibility(rightLeftVisible?View.VISIBLE:View.GONE);
        right_icon.setBackgroundResource(rightLeftIcon);

        title_right_icon.setVisibility(rightIconVisible?View.VISIBLE:View.GONE);
        title_icon.setBackgroundResource(rightIcon);

        title_right.setVisibility(rightTextVisible?View.VISIBLE:View.GONE);
        title_right.setText(TextUtils.isEmpty(rightText)?"添加":rightText);
    }

    public void setRightText(String text){
        title_right.setText(text);
    }
    public void setRightVisibility(int visibility){
        title_right.setVisibility(visibility);
    }

    public void setRightOnclick(OnClickListener onclick){
        title_right.setOnClickListener(onclick);
    }

}
