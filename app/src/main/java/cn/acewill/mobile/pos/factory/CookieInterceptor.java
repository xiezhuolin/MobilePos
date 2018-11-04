package cn.acewill.mobile.pos.factory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Acewill on 2016/6/6.
 */
public class CookieInterceptor implements Interceptor {
    private static List<String> cookieList = new ArrayList<>();
    private static String token;

    public static void addCookie(String s)
    {
        cookieList.add(s);
    }

    public static void setToken(String t)
    {
        token = t;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        HttpUrl url = null;
        if (token != null && token.length() > 0) {
            HttpUrl.Builder urlBuilder = chain.request().url().newBuilder();
            //自动在每个请求后面加上token
            urlBuilder.addQueryParameter("token", token);
            url = urlBuilder.build();
        }


        Request.Builder builder = chain.request().newBuilder();
        for (String c : cookieList) {
            builder.addHeader("Cookie", c);
        }

        if (url != null) {
            return chain.proceed(builder.url(url).build());
        } else {
            return chain.proceed(builder.build());
        }
    }
}
