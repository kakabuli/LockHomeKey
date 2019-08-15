package com.kaadas.lock.adapter;

import android.widget.TextView;

import com.chad.library.adapter.base.BaseSectionQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.kaadas.lock.R;
import com.kaadas.lock.bean.OperationSection;
import com.kaadas.lock.publiclibrary.bean.ForeverPassword;
import com.kaadas.lock.publiclibrary.ble.BleUtil;
import com.kaadas.lock.publiclibrary.http.result.GetPasswordResult;

import java.util.List;

public class OperationRecordAdapter extends BaseSectionQuickAdapter<OperationSection, BaseViewHolder> {

    private GetPasswordResult passwordResults;

    public OperationRecordAdapter(int layoutResId, int sectionHeadResId, List data, GetPasswordResult passwordResult) {
        super(layoutResId, sectionHeadResId, data);
        this.passwordResults = passwordResult;
    }

    @Override
    protected void convert(BaseViewHolder helper, OperationSection item) {
//        helper.setImageUrl(R.id.iv, (String) item.t);
        TextView tvTime = helper.getView(R.id.tv_time);
        TextView tvName = helper.getView(R.id.tv_num);
        TextView tvType = helper.getView(R.id.tv_type);
        int eventType = item.t.getEventType();
        int eventSource = item.t.getEventSource();
        int eventCode = item.t.getEventCode();
        String userNum = item.t.getUserNum()+"";
        if (1==eventType){
            tvTime.setText(item.t.getEventTime());
            String openLockEventSourceContent = BleUtil.getOpenLockEventSourceContent(eventSource, item.t.getUserNum());
            tvType.setText(openLockEventSourceContent);
            tvName.setText("103".equals(userNum)?"App":userNum);
            if (passwordResults != null) {
                switch (eventType) {
                    case 0:
                        List<ForeverPassword> pwdList = passwordResults.getData().getPwdList();
                        for (ForeverPassword password : pwdList) {
                            if (Integer.parseInt(password.getNum()) == Integer.parseInt(userNum)) {
                                tvName.setText(password.getNickName());
                            }
                        }
                        List<GetPasswordResult.DataBean.TempPassword> tempPwdList = passwordResults.getData().getTempPwdList();
                        for (GetPasswordResult.DataBean.TempPassword password : tempPwdList) {
                            if (Integer.parseInt(password.getNum()) == Integer.parseInt(userNum)) {
                                tvName.setText(password.getNickName());
                            }
                        }
                        break;
                    case 4:
                        List<GetPasswordResult.DataBean.Fingerprint> fingerprints = passwordResults.getData().getFingerprintList();
                        for (GetPasswordResult.DataBean.Fingerprint password : fingerprints) {
                            if (Integer.parseInt(password.getNum()) == Integer.parseInt(userNum)) {
                                tvName.setText(password.getNickName());
                            }
                        }
                        break;
                    case 3:  //卡片
                        List<GetPasswordResult.DataBean.Card> cards = passwordResults.getData().getCardList();
                        for (GetPasswordResult.DataBean.Card password : cards) {
                            if (Integer.parseInt(password.getNum()) == Integer.parseInt(userNum)) {
                                tvName.setText(password.getNickName());
                            }
                        }
                        break;
                }
            }
        }else if (2==eventType){
            tvTime.setText(item.t.getEventTime());
            //todo 操作记录类型
            String operationProgramContent = BleUtil.getOperationProgramContent(eventCode);
            tvName.setText(operationProgramContent);
            tvType.setText(operationProgramContent);
            int userId = Integer.parseInt(userNum);
            switch (eventCode){
                case 2:
                    switch (userId){
                        case 0:
                        case 1:
                        case 2:
                        case 3:
                        case 4:
                            tvType.setText(mContext.getString(R.string.add_fowever_pwd));
                            break;
                        case 5:
                        case 6:
                        case 7:
                        case 8:
                            tvType.setText(mContext.getString(R.string.add_temp_pwd));
                            break;
                        case 9:
                            tvType.setText(mContext.getString(R.string.add_force_pwd));
                            break;
                    }
                    break;
                case 3:
                    switch (userId){
                        case 0:
                        case 1:
                        case 2:
                        case 3:
                        case 4:
                            tvType.setText(mContext.getString(R.string.del_fowever_pwd));
                            break;
                        case 5:
                        case 6:
                        case 7:
                        case 8:
                            tvType.setText(mContext.getString(R.string.del_temp_pwd));
                            break;
                        case 9:
                            tvType.setText(mContext.getString(R.string.del_force_pwd));
                            break;
                    }
                    break;
                case 4:
                    switch (userId){
                        case 0:
                        case 1:
                        case 2:
                        case 3:
                        case 4:
                            tvType.setText(mContext.getString(R.string.modify_fowever_pwd));
                            break;
                        case 5:
                        case 6:
                        case 7:
                        case 8:
                            tvType.setText(mContext.getString(R.string.modify_temp_pwd));
                            break;
                        case 9:
                            tvType.setText(mContext.getString(R.string.modify_force_pwd));
                            break;
                    }
                    break;
            }

        }
    }

    @Override
    protected void convertHead(BaseViewHolder helper, final OperationSection item) {
        TextView tvHead = helper.getView(R.id.tv_head);
        tvHead.setText(item.header);
    }

}
