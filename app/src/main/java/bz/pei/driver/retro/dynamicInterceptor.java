package bz.pei.driver.retro;

import java.io.IOException;

import javax.inject.Singleton;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by root on 9/25/17.
 */

@Singleton
public class dynamicInterceptor  implements Interceptor {
    private static dynamicInterceptor sInterceptor;
    private String mScheme;
    private String mHost;


    public  dynamicInterceptor get() {
        if (sInterceptor == null) {
            sInterceptor = new dynamicInterceptor();
        }
        return sInterceptor;
    }


    public dynamicInterceptor() {
        // Intentionally blank
    }

    public void setInterceptor(String url) {
        HttpUrl httpUrl = HttpUrl.parse(url);
        mScheme = httpUrl.scheme();
        mHost = httpUrl.host();
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();

        // If new Base URL is properly formatted than replace with old one
        if (mScheme != null && mHost != null) {
            HttpUrl newUrl = original.url().newBuilder()
                    .scheme(mScheme)
                    .host(mHost)
                    .build();
            original = original.newBuilder()
                    .url(newUrl)
                    .build();
        }
        return chain.proceed(original);
    }
}
