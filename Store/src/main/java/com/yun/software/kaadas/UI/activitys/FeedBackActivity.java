package com.yun.software.kaadas.UI.activitys;

import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yun.software.kaadas.Http.ApiConstants;
import com.yun.software.kaadas.Http.HttpManager;
import com.yun.software.kaadas.Http.OnIResponseListener;
import com.kaadas.lock.store.R;;import com.kaadas.lock.store.R2;;
import com.yun.software.kaadas.UI.bean.BaseBody;
import com.yun.software.kaadas.UI.bean.Store;
import com.yun.software.kaadas.Utils.UserUtils;
import com.yun.software.kaadas.base.BaseActivity;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import la.xiong.androidquick.tool.ToastUtil;

public class FeedBackActivity extends BaseActivity {

    @BindView(R2.id.edit_text)
    EditText editText;
    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_feedback;
    }

    @Override
    protected boolean isLoadDefaultTitleBar() {
        return true;
    }

    @Override
    protected void initViewsAndEvents() {
        tvTitle.setText("意见反馈");
    }


    @OnClick(R2.id.submit)
    public void onClickView(View view){
        sendData();
    }


    private void sendData() {

        String text = editText.getText().toString().trim();
        if (TextUtils.isEmpty(text)){
            ToastUtil.showShort("请输入内容");
            return;
        }

        Map<String,Object> map = new HashMap<>();
        map.put("token", UserUtils.getToken());
        Map<String,Object> params = new HashMap<>();
        params.put("content",text);
        map.put("params",params);


        HttpManager.getInstance().post(mContext, ApiConstants.FEEDBACK_SAVE, map, new OnIResponseListener() {
            @Override
            public void onSucceed(String result) {
                ToastUtil.showShort("提交成功");
                finish();

            }
            @Override
            public void onFailed(String error) {
                ToastUtil.showShort(error);


            }
        },true);
    }


}


