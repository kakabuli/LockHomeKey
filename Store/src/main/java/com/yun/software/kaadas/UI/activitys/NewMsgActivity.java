package com.yun.software.kaadas.UI.activitys;

import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yun.software.kaadas.Http.ApiConstants;
import com.yun.software.kaadas.Http.HttpManager;
import com.yun.software.kaadas.Http.OnIResponseListener;
import com.kaadas.lock.store.R;;import com.kaadas.lock.store.R2;;
import com.yun.software.kaadas.UI.bean.BaseBody;
import com.yun.software.kaadas.UI.bean.HomeListBean;
import com.yun.software.kaadas.UI.bean.NewMsgBean;
import com.yun.software.kaadas.Utils.UserUtils;
import com.yun.software.kaadas.base.BaseActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import la.xiong.androidquick.tool.ToastUtil;

public class NewMsgActivity extends BaseActivity {

    @BindView(R2.id.sys_info)
    TextView sysInfoView;

    @BindView(R2.id.order_info)
    TextView orderInfoView;

    @BindView(R2.id.act_info)
    TextView actInfoView;


    private List<NewMsgBean> listbeans = new ArrayList<>();
    @Override
    protected boolean isLoadDefaultTitleBar() {
        return true;
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_new_msg;
    }

    @Override
    protected void initViewsAndEvents() {
        tvTitle.setText("消息中心");
        getData();
    }


    @OnClick({R2.id.ll_system_info,R2.id.ll_order_info,R2.id.ll_act_info})
    public void onViewClick(View view){
        int i = view.getId();//系统消息
//订单消息
//活动消息
        if (i == R.id.ll_system_info) {
            readyGo(SystemInfoActivity.class);

        } else if (i == R.id.ll_order_info) {
            readyGo(OrderInfoActivity.class);

        } else if (i == R.id.ll_act_info) {
            readyGo(ActInfoActivity.class);

        }
    }

    private void getData() {
        Map<String,Object> map = new HashMap<>();
        map.put("token", UserUtils.getToken());
        HttpManager.getInstance().post(mContext, ApiConstants.SYSMESSAGE_GETSYSMESSAGENUMBER, map, new OnIResponseListener() {
            @Override
            public void onSucceed(String result) {

                Gson gson = new Gson();
                List<NewMsgBean> list = gson.fromJson(result,new TypeToken<List<NewMsgBean>>(){}.getType());
                listbeans.addAll(list);
                setView();

            }
            @Override
            public void onFailed(String error) {
                ToastUtil.showShort(error);

            }
        },false);
    }

    private void setView() {
        for (NewMsgBean bean:listbeans) {
            switch (bean.getMessageType()){
                case "message_type_indent"://订单消息
                    orderInfoView.setText(bean.getNumber());
                    break;
                case "message_type_system"://系统消息
                    sysInfoView.setText(bean.getNumber());
                    break;
                case "message_type_activity"://活动消息
                    actInfoView.setText(bean.getNumber());
                    break;
            }
        }

    }


}
