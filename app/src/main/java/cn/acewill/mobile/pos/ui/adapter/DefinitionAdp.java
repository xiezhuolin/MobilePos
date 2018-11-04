package cn.acewill.mobile.pos.ui.adapter;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.TextView;

import cn.acewill.mobile.pos.R;
import cn.acewill.mobile.pos.base.adapter.BaseAdapter;
import cn.acewill.mobile.pos.config.MyApplication;
import cn.acewill.mobile.pos.model.Definition;


/**
 * Created by DHH on 2016/6/17.
 */
public class DefinitionAdp<T> extends BaseAdapter {
    private int width;
    private int height;
    private int position;
    public DefinitionAdp(Context context) {
        super(context);
        int sc_width = MyApplication.getInstance().getScreenWidth();
        width = sc_width / 4 + 10;
        height = width / 3 + 60;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Definition definition = (Definition)getItem(position);
        final TextView textView = new TextView(context);
        textView.setText(definition.getName());
        textView.setTextSize(16);
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(0xff000000);
        AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(
                width, height);
        textView.setPadding(5,5,5,5);
        textView.setLayoutParams(layoutParams);
        if(this.position == position )
        {
            textView.setBackgroundResource(R.drawable.border_green);
        }
        else
        {
            textView.setBackgroundResource(R.drawable.border_gray1_xb);
        }
        return textView;
    }

    public void setPosition(int position)
    {
        this.position = position;
        this.notifyDataSetChanged();
    }

}
