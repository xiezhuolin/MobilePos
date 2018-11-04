package cn.acewill.mobile.pos.base.adapter;

import android.content.Context;
import android.content.res.Resources;

import java.util.List;

/**
 * 通用Adapter
 * Created by Sxf on 2016/2/1.
 */
public abstract  class BaseAdapter<T> extends android.widget.BaseAdapter {

    public Context context;
    public List<T> dataList ;
    public Resources res;

    public BaseAdapter(Context context){
        this.context = context;
        res = context.getResources();
    }

    public void setData(List<T> dataList){
//        if (dataList != null){
//        }
        this.dataList = dataList;
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return dataList != null ? dataList.size() : 0;
    }

    @Override
    public T getItem(int position) {
        return dataList != null ? dataList.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public String getStringById(int resId){
        String str = "";
        if (res != null){
            str = res.getString(resId);
        }
        return str;
    }
}
