package cn.acewill.mobile.pos.interfices;

import android.renderscript.Type;
import android.text.Annotation;

import retrofit2.Retrofit;

/**
 * Created by DHH on 2016/9/8.
 */
public interface Converter<F, T> {
    abstract class Factory {
        //返回一个满足条件的不限制类型的 Converter
        public Converter<?, ?> arbitraryConverter(Type originalType,
                                                  Type convertedType, Annotation[] parameterAnnotations, Annotation[] methodAnnotations, Retrofit retrofit){
            return null;
        }
    }
}