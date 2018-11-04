package cn.acewill.mobile.pos.interfices;

import java.util.List;

/**
 * Created by DHH on 2016/9/5.
 */
public interface WSHListener<T>  {
    public void refrush(List<T> dataList);
}
