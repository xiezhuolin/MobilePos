package cn.acewill.mobile.pos.factory;

import android.util.Log;

import java.io.IOException;

import cn.acewill.mobile.pos.config.MyApplication;
import cn.acewill.mobile.pos.exception.ErrorCode;
import cn.acewill.mobile.pos.exception.PosServiceException;
import cn.acewill.mobile.pos.utils.Constant;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import rx.exceptions.Exceptions;

/**
 * 检查网络是否连接
 * Created by aqw on 2016/11/10.
 */
public class NetWorkInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        boolean isConnect = MyApplication.isNetConnected(MyApplication.getInstance().getApplicationContext());
        if(isConnect){
            Request request = chain.request();
            String requestStartMessage = "接口调用:" + request.method() + ' ' + request.url();
            Log.i("TAG",requestStartMessage);//此处用于日志记录

            return chain.proceed(chain.request());
        }
        PosServiceException exception = new PosServiceException(ErrorCode.NETWORK_NOT_CONNECT, Constant.NETWORK_NOT_CONNECT);
        throw Exceptions.propagate(exception);
    }
}
