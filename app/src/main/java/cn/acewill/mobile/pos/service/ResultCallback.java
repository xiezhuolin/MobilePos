package cn.acewill.mobile.pos.service;


import cn.acewill.mobile.pos.exception.PosServiceException;

/**
 * Created by Acewill on 2016/6/13.
 */
public interface ResultCallback<T>  {
    public void onResult(T result);
    public void onError(PosServiceException e);
}
