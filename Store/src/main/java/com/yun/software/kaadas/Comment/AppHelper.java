package com.yun.software.kaadas.Comment;

import android.app.Application;
import android.content.Context;
import android.view.Gravity;

import com.yun.software.kaadas.Http.ApiConstants;
import com.yun.software.kaadas.Http.CustomSignInterceptor;
import com.kaadas.lock.store.R;;import com.kaadas.lock.store.R2;;
import com.zhouyou.http.EasyHttp;
import com.zhouyou.http.cache.converter.SerializableDiskConverter;
import com.zhouyou.http.model.HttpHeaders;

import la.xiong.androidquick.http.HeaderInterceptor;
import la.xiong.androidquick.tool.ToastUtil;
import la.xiong.androidquick.tool.Utils;
import spa.lyh.cn.statusbarlightmode.ImmersionConfiguration;
import spa.lyh.cn.statusbarlightmode.ImmersionMode;

/**
 * Created by yanliang
 * on 2019/1/4 14:12
 */
public class AppHelper {
    private static AppHelper instance = null;
    public Context appContext;
    public synchronized static AppHelper getInstance() {
        if (instance == null) {
            instance = new AppHelper();
        }
        return instance;
    }
    public void init(Application context) {
        appContext=context;
        stateBarinit();
        QizhiConfig();
        Utils.init(appContext);
        setDefalutNewWorkRequest();

        //沉浸式标题栏
    }

    //正式环境
//    private void QizhiConfig(){
//        Setting.init(appContext)
//                .withWeChatAppId("wx496a49943fabac18")
//                .withWeChatAppSecret("e59ede27c5b4bc97a78179a566609a4b")
//                .configure();
//    }

    //测试环境
    private void QizhiConfig(){
        Setting.init(appContext)
                .withWeChatAppId("wxd1cccfeb16cd07d1")
                .withWeChatAppSecret("5d3d1005f5fc485c65adfcf64320b6d9")
                .configure();
    }

    private  void stateBarinit(){
        ImmersionConfiguration configuration = new ImmersionConfiguration.Builder(appContext)
                .enableImmersionMode(ImmersionConfiguration.ENABLE)
                .setColor(R.color.bg_state_color)//默认标题栏颜色
                .build();
        ImmersionMode.getInstance().init(configuration);
        ToastUtil.setBgColor(appContext.getResources().getColor(R.color.black));
        ToastUtil.setGravity(Gravity.CENTER, 0, 0);
        ToastUtil.setMsgColor(appContext.getResources().getColor(R.color.white));
    }
    public String getString(int id){
     return appContext.getString(id);
    }
    public void setDefalutNewWorkRequest() {
        EasyHttp.init((Application) appContext);
        //这里涉及到安全我把url去掉了，demo都是调试通的
        String Url = ApiConstants.BASE_URL;
        //设置请求头
         HttpHeaders headers = new HttpHeaders();
         headers.put("Content-Type", "application/json;charset=UTF-8");
        // 设置请求参数
        // HttpParams params = new HttpParams();
        // params.put("appId", AppConstant.APPID);
        EasyHttp.getInstance()
                .debug("zry_http:", true)
                .setReadTimeOut(6 * 1000)
                .setWriteTimeOut(6 * 1000)
                .setConnectTimeout(6 * 1000)
                .setRetryCount(3)//默认网络不好自动重试3次
                .setRetryDelay(500)//每次延时500ms重试
                //.setRetryIncreaseDelay(500)//每次延时叠加500ms
                .setBaseUrl(Url)
                .addCommonHeaders(headers)
                .setCacheDiskConverter(new SerializableDiskConverter())//默认缓存使用序列化转化
                .setCacheMaxSize(50 * 1024 * 1024)//设置缓存大小为50M
                .setCacheVersion(1)//缓存版本为1
 //             .setHostnameVerifier(new UnSafeHostnameVerifier(Url))//全局访问规则
                .setCertificates()
                //添加参数访问规则
                .addInterceptor(new CustomSignInterceptor())
                .addInterceptor(new HeaderInterceptor());
    }


}
