//package cn.acewill.mobile.pos.factory;
//
//import java.io.IOException;
//
//import okhttp3.Interceptor;
//import okhttp3.Request;
//import okhttp3.Response;
//
///**
// * Created by Acewill on 2016/8/8.
// */
//public class HostSelectionInterceptor implements Interceptor {
//    private static String currentUrl;
//    private static boolean urlChanged = false;
//
//    public static void setBaseUrl(String baseUrl) {
//        currentUrl = baseUrl;
//        urlChanged = true;
//    }
//
//    @Override
//    public Response intercept(Chain chain) throws IOException {
//        Request request = chain.request();
//        String query = request.url().encodedQuery();
//        String path = request.url().encodedPath();
//
//       // if (currentUrl != null && urlChanged) {
//            request = request.newBuilder()
//                    .url(currentUrl + path + "?" + query)
//                    .build();
//            urlChanged = false;
//      //  }
//
//        return chain.proceed(request);
//    }
//}
