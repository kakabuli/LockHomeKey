package com.kaadas.lock.publiclibrary.http.util;

import android.text.TextUtils;


import com.kaadas.lock.BuildConfig;
import com.kaadas.lock.MyApplication;
import com.kaadas.lock.publiclibrary.http.temp.HttpConstants;
import com.kaadas.lock.utils.LogUtils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;


/**
 * @author Administrator
 * @date 2018/3/24
 */

public class RetrofitServiceManager {
    private static final int DEFAULT_TIME_OUT = 15;//超时时间
    private static final int DEFAULT_READ_TIME_OUT = 15;//读取时间
    private static final int DEFAULT_WRITE_TIME_OUT = 15;//读取时间
    private static Retrofit mRetrofit;  //还有token
    private static Retrofit noRetrofit;
    private static OkHttpClient.Builder builder;
    private static OkHttpClient.Builder noBuilder;


    public static Retrofit getInstance() {
        if (mRetrofit == null) {
            builder = new OkHttpClient.Builder();
            builder.connectTimeout(DEFAULT_TIME_OUT, TimeUnit.SECONDS);
            builder.readTimeout(DEFAULT_READ_TIME_OUT, TimeUnit.SECONDS);
            builder.writeTimeout(DEFAULT_WRITE_TIME_OUT, TimeUnit.SECONDS);

            //设置支持所有https请求
            HttpsUtils.SSLParams sslParams = null;
            try {
                sslParams = HttpsUtils.getSslSocketFactory(null, new BufferedInputStream(MyApplication.getInstance().getAssets().open("server.cer")), null);
            } catch (IOException e) {
                e.printStackTrace();
            }
            builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    if (hostname.equals("app-kaadas.juziwulian.com")) {
                        return true;
                    } else {
                        //在这里做断开连接
                        return true;
                    }
                }
            });
            builder.sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager);
            addInterceptor(builder, true);
            mRetrofit = new Retrofit.Builder()
                    .client(builder.build())
                    .baseUrl(HttpConstants.BASE_URL)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();
        }
        return mRetrofit;
    }

    public static Retrofit getNoTokenInstance() {
        if (noRetrofit == null) {
            noBuilder = new OkHttpClient.Builder();
            noBuilder.connectTimeout(DEFAULT_TIME_OUT, TimeUnit.SECONDS);
            noBuilder.readTimeout(DEFAULT_READ_TIME_OUT, TimeUnit.SECONDS);
            noBuilder.writeTimeout(DEFAULT_WRITE_TIME_OUT, TimeUnit.SECONDS);

            //设置支持所有https请求
            HttpsUtils.SSLParams sslParams = null;
            try {
                sslParams = HttpsUtils.getSslSocketFactory(null, new BufferedInputStream(MyApplication.getInstance().getAssets().open("server.cer")), null);
            } catch (IOException e) {
                e.printStackTrace();
            }
            noBuilder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    if (hostname.equals("app-kaadas.juziwulian.com")) {
                        return true;
                    } else {
                        //在这里做断开连接
                        return true;
                    }
                }
            });
//            noBuilder.sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager);
            addInterceptor(noBuilder, false);
            noRetrofit = new Retrofit.Builder()
                    .client(noBuilder.build())
                    .baseUrl(HttpConstants.BASE_URL)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();
        }
        return noRetrofit;
    }

    public static class HttpLogger implements HttpLoggingInterceptor.Logger {
        @Override
        public void log(String message) {
            if(BuildConfig.DEBUG)
                LogUtils.e("网络请求体   ", message);
        }
    }

    public static class HttpHeadLogger implements HttpLoggingInterceptor.Logger {
        @Override
        public void log(String message) {
            if(BuildConfig.DEBUG)
                LogUtils.e("网络请求头   ", message);
        }
    }


    /**
     * 添加各种拦截器
     *
     * @param builder
     */
    private static void addInterceptor(OkHttpClient.Builder builder, boolean isToken) {
        // 添加日志拦截器，非debug模式不打印任何日志
//        LoggingInterceptor loggingInterceptor = new LoggingInterceptor.Builder()
//                .loggable(true)
//                .request()
//                .requestTag("网络请求 Request")
//                .response()
//                .responseTag("网络请求 response")
//                .build();
        HttpLoggingInterceptor logInterceptor1 = new HttpLoggingInterceptor(new HttpHeadLogger());
        logInterceptor1.setLevel(HttpLoggingInterceptor.Level.HEADERS);

        HttpLoggingInterceptor logInterceptor = new HttpLoggingInterceptor(new HttpLogger());
        logInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        if (isToken) {
            HttpHeaderInterceptor httpHeaderInterceptor = null;
            if (!TextUtils.isEmpty(MyApplication.getInstance().getToken())) {
                httpHeaderInterceptor = new HttpHeaderInterceptor.Builder()
                        .addHeaderParams("token", MyApplication.getInstance().getToken())
                        .build();
                builder.addInterceptor(httpHeaderInterceptor);
            }
        } else {
            HttpHeaderInterceptor  httpHeaderInterceptor = new HttpHeaderInterceptor.Builder()
                    .build();
            builder.addInterceptor(httpHeaderInterceptor);
        }

        builder.addInterceptor(logInterceptor1);
        builder.addInterceptor(logInterceptor);
    }

    public static void updateToken() {
//        if (mRetrofit!=null&&builder!=null){
//            if (!TextUtils.isEmpty(MyApplication.getInstance().getToken())) {
//                HttpHeaderInterceptor httpHeaderInterceptor = new HttpHeaderInterceptor.Builder()
//                        .addHeaderParams("token", MyApplication.getInstance().getToken())
//                        .build();
//                builder.addInterceptor(httpHeaderInterceptor);
//            }
//        }else {
        builder = new OkHttpClient.Builder();
        builder.connectTimeout(DEFAULT_TIME_OUT, TimeUnit.SECONDS);
        builder.readTimeout(DEFAULT_READ_TIME_OUT, TimeUnit.SECONDS);
        builder.writeTimeout(DEFAULT_WRITE_TIME_OUT, TimeUnit.SECONDS);

        //设置支持所有https请求
        HttpsUtils.SSLParams sslParams = null;
        try {
            sslParams = HttpsUtils.getSslSocketFactory(null, new BufferedInputStream(MyApplication.getInstance().getAssets().open("server.cer")), null);
//             sslParams = HttpsUtils.getSslSocketFactory(new InputStream[]{MyApplication.getInstance().getAssets().open("server.cer")}, null, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
//        builder.hostnameVerifier((hostname, session) -> true).sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager);
        builder.hostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                if (hostname.equals("app-kaadas.juziwulian.com")) {
                    return true;
                } else {
                    //在这里做断开连接
                    return true;
                }
            }
        });
        LogUtils.d("davi builder "+builder+" sslParams.sSLSocketFactory "+sslParams.sSLSocketFactory+" sslParams.trustManager "+sslParams.trustManager);
        builder.sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager);
        addInterceptor(builder, true);
        mRetrofit = new Retrofit.Builder()
                .client(builder.build())
                .baseUrl(HttpConstants.BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
//        }
    }

    public <T> T creat(Class<T> tClass) {
        return mRetrofit.create(tClass);
    }

}
