package com.yun.software.kaadas.UI.activitys;

import android.content.Intent;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.yun.software.kaadas.Comment.ConfigKeys;
import com.yun.software.kaadas.Comment.Constans;
import com.yun.software.kaadas.Comment.Setting;
import com.yun.software.kaadas.Http.ApiConstants;
import com.yun.software.kaadas.Http.HttpManager;
import com.yun.software.kaadas.Http.OnIResponseListener;
import com.kaadas.lock.R;;import com.kaadas.lock.R2;;
import com.yun.software.kaadas.UI.alipay.IAlPayResultListener;
import com.yun.software.kaadas.UI.alipay.PayAsyncTask;
import com.yun.software.kaadas.UI.wxchat.EcWeChat;
import com.yun.software.kaadas.Utils.OrderStatue;
import com.yun.software.kaadas.Utils.UserUtils;
import com.yun.software.kaadas.base.BaseActivity;

import java.util.HashMap;
import java.util.Map;

import de.greenrobot.event.EventBus;
import la.xiong.androidquick.tool.LogUtils;
import la.xiong.androidquick.tool.StringUtil;
import la.xiong.androidquick.tool.ToastUtil;
import la.xiong.androidquick.ui.eventbus.EventCenter;

public class PayActivity extends BaseActivity implements View.OnClickListener {

    ImageView checkWeichatView;
    ImageView checkZhifubaoView;
    private String price;

    private String orderId;
    private String orderNo;


    private String[] channels = new String[]{"支付宝", "微信支付", "QQ钱包"};
    private String[] channels_key = new String[]{"ali_web", "wx" ,"qq_web"};

    private int current = 0;//0,使用支付宝进行测试，1，使用微信进行测试，2.使用QQ钱包进行测试



    private int type;


    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_pay;
    }

    @Override
    protected void initViewsAndEvents() {
        Setting.getConfigurator().withActivity(this);
        orderId = getIntent().getStringExtra("id");
        price = getIntent().getStringExtra("price");
        orderNo = getIntent().getStringExtra("orderNo");
        type = getIntent().getIntExtra("type",0);

        ImageView closeView = findViewById(R.id.close);
        LinearLayout llWeichatView = findViewById(R.id.ll_weichat);
        checkWeichatView = findViewById(R.id.check_weichat);
        LinearLayout llZhifubaoView = findViewById(R.id.ll_zhifubao);
        checkZhifubaoView = findViewById(R.id.check_zhifubao);
        Button button = findViewById(R.id.button);

        TextView priceView = findViewById(R.id.price);
        priceView.setText(price);

        closeView.setOnClickListener(this);
        llWeichatView.setOnClickListener(this);
        llZhifubaoView.setOnClickListener(this);
        button.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.close) {
            if (type == ShopDetailsNewActivity.TYPE_NORMAL) {
                EventBus.getDefault().post(new EventCenter(Constans.MESSAGE_SUBMIT_ORDER));
            }
            finish();

        } else if (i == R.id.ll_weichat) {
            checkWeichatView.setImageResource(R.drawable.icon_pay_sel);
            checkZhifubaoView.setImageResource(R.drawable.icon_pay_nor);
            current = 1;

        } else if (i == R.id.ll_zhifubao) {
            checkWeichatView.setImageResource(R.drawable.icon_pay_nor);
            checkZhifubaoView.setImageResource(R.drawable.icon_pay_sel);
            current = 0;

        } else if (i == R.id.button) {
            if (current == 0) { //支付宝
                paySend();
            } else if (current == 1) {//微信
                wechatPay();
            }


        }

    }


    /**
     * 微信支付
     */
    private void wechatPay() {
        LogUtils.iTag("WXPayEntryActivity","开启支付了");
        final IWXAPI iwxapi = EcWeChat.getInstance().getWXAPI();
        final String appId = Setting.getConfiguration(ConfigKeys.WE_CHAT_APP_ID);
        Map<String,Object> map = new HashMap<>();
        map.put("token", UserUtils.getToken());
        Map<String,Object> params = new HashMap<>();
        params.put("outTradeNo",orderNo);
        map.put("params",params);
        HttpManager.getInstance().post(mContext, ApiConstants.WXAPP_PAYMENT, map, new OnIResponseListener() {
            @Override
            public void onSucceed(String resonse) {
                final JSONObject result =
                        JSON.parseObject(resonse);
                final String prepayId = result.getString("prepay_id");
                final String partnerId = result.getString("mch_id");
                final String packageValue = result.getString("package");
                final String timestamp = result.getString("timestamp");
                final String nonceStr = result.getString("nonce_str");
                final String paySign = result.getString("sign");
                final PayReq payReq = new PayReq();
                payReq.appId = appId;
                payReq.prepayId = prepayId;
                payReq.partnerId = partnerId;
                payReq.packageValue = packageValue;
                payReq.timeStamp = timestamp;
                payReq.nonceStr = nonceStr;
                payReq.sign = paySign;
                LogUtils.iTag("payparams",JSON.toJSONString(payReq));
                iwxapi.sendReq(payReq);
            }

            @Override
            public void onFailed(String error) {

            }
        },true);

    }


    /**
     * 支付宝支付
     */
    private void paySend(){
        Map<String,Object> map = new HashMap<>();
        map.put("token", UserUtils.getToken());
        Map<String,Object> params = new HashMap<>();
        params.put("outTradeNo",orderNo); //订单号
//        params.put("subject","凯迪仕商城");//商品的标题/交易标题/订单标题/订单关键字等
//        params.put("totalAmount",price);//该笔订单的资金总额
//        params.put("bankType",current);
        map.put("params",params);
        HttpManager.getInstance().post(mContext, ApiConstants.ALLAT_PAY_APP, map, new OnIResponseListener() {
            @Override
            public void onSucceed(String result) {
                //必须是异步的调用客户端支付接口
                final PayAsyncTask payAsyncTask = new PayAsyncTask(PayActivity.this, new IAlPayResultListener() {
                    @Override
                    public void onPaySuccess() {
                       getStatus();
                    }
                    @Override
                    public void onPaying() {
                        ToastUtil.showShort("正在支付");
                        finish();
                    }

                    @Override
                    public void onPayFail() {
                        ToastUtil.showShort("支付失败");
                    }

                    @Override
                    public void onPayCancel() {
                        ToastUtil.showShort("取消支付");
                    }

                    @Override
                    public void onPayConnectError() {
                        ToastUtil.showShort("支付错误");
                    }
                });
                payAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, result);


//                String payInfo = StringUtil.getJsonKeyStr(result,"pay_info");
//                String tradeNo = StringUtil.getJsonKeyStr(result,"trade_no");
//                String outTradeNo = StringUtil.getJsonKeyStr(result,"out_trade_no");

//                //构造支付请求体，准备调用SDK的数据集
//                RequestEntity requestEntity = new RequestEntity.Builder()
//                        .setChannel(channels_key[current]).
//                        setPayInfo(payInfo).
//                        setTradeNo(tradeNo).
//                        setOutTradeNo(outTradeNo).build();
//                //调用SDK进行支付，唤起SDK支付
//                PayImp.pay(PayActivity.this, requestEntity);
            }

            @Override
            public void onFailed(String error) {
                ToastUtil.showShort(error);
                finish();
            }
        },true);
    }


    /**
     *SDK唤起的支付结果在这里返回
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //TODO STEP 4  等待结果返回
//        if (requestCode == PayImp.REQUEST_CODE_PAY) {
//            //收到的是支付请求的回调
//            if (RESULT_OK == resultCode) {
//                Bundle bundle = data.getExtras();
//                if (bundle == null)
//                    return;
//                getStatus();
//            }
//        }
    }

    @Override
    protected void onEventComing(EventCenter eventCenter) {
        super.onEventComing(eventCenter);
        if(eventCenter.getEventCode()==Constans.MESSAGE_WX_PAYSUCCESS){
            getStatus();
        }
    }

    private void getStatus(){
        Map<String,Object> map = new HashMap<>();
        map.put("token", UserUtils.getToken());
        Map<String,Object> params = new HashMap<>();
        params.put("orderNo",orderNo); //订单号
        map.put("params",params);

        HttpManager.getInstance().post(mContext, ApiConstants.INDENT_GETSTATUSBYNO, map, new OnIResponseListener() {
            @Override
            public void onSucceed(String result) {
                String status = StringUtil.getJsonKeyStr(result,"indentStatus");
                if (!TextUtils.isEmpty(status) && !status.equals(OrderStatue.INDENT_STATUS_WAIT_PAY)){
                    ToastUtil.showShort("支付成功");
                    EventBus.getDefault().post(new EventCenter(Constans.MESSAGE_PAY_SUCCESS));
                    finish();
                }else {
                    ToastUtil.showShort("支付失败");
                }
            }

            @Override
            public void onFailed(String error) {

            }
        },true);
    }

    @Override
    protected void onDestroy() {
        Setting.getConfigurator().removeActivity();
        super.onDestroy();
    }
}
