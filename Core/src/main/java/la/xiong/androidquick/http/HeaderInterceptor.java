package la.xiong.androidquick.http;


import android.provider.SyncStateContract;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import la.xiong.androidquick.tool.LogUtils;
import la.xiong.androidquick.tool.StringUtil;
import la.xiong.androidquick.ui.eventbus.EventCenter;
import okhttp3.*;
import okio.BufferedSource;

import java.io.IOException;
import java.nio.charset.Charset;


/**
 * Created by yanliang
 * 2017/1/3
 */

public class HeaderInterceptor implements Interceptor {
     public static final  String TAG="HeaderInterceptor";
    private final Charset UTF8 = Charset.forName("UTF-8");

    public static final int MESSAGE_LOGINOUT= 21;

    @Override
    public Response intercept(Chain chain) throws IOException {

        final Request request = chain.request();
        Response originalResponse = chain.proceed(request);
        ResponseBody responseBody = originalResponse.body();
        BufferedSource source = responseBody.source();
        source.request(Long.MAX_VALUE); // Buffer the entire body.
        okio.Buffer buffer = source.buffer();
        Charset charset = UTF8;
        MediaType contentType = responseBody.contentType();
        if (contentType != null) {
            charset = contentType.charset(UTF8);
        }
        String bodyString = buffer.clone().readString(charset);
        LogUtils.iTag(TAG,bodyString);
        if (StringUtil.getJsonKeyStr(bodyString,"code").equals("21")) {
            Log.e("商城   ", "商城token过期");
            EventBus.getDefault().post(new EventCenter(MESSAGE_LOGINOUT,"relogin"));
        }
        return originalResponse;
    }
}
