package com.kaadas.lock.adapter;

import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.kaadas.lock.R;
import com.kaadas.lock.publiclibrary.bean.WifiLockAlarmRecord;
import com.kaadas.lock.publiclibrary.bean.WifiLockOperationRecord;
import com.kaadas.lock.publiclibrary.ble.BleUtil;
import com.kaadas.lock.utils.DateUtils;
import com.kaadas.lock.utils.LogUtils;

import java.util.List;

/**
 * Created by David on 2019/2/16
 */


public class WifiLockOperationItemRecordAdapter extends BaseQuickAdapter<WifiLockOperationRecord, BaseViewHolder> {
    public WifiLockOperationItemRecordAdapter(int layoutResId, @Nullable List<WifiLockOperationRecord> data) {
        super(layoutResId, data);
    }

    public WifiLockOperationItemRecordAdapter(@Nullable List<WifiLockOperationRecord> data) {
        super(data);
    }

    @Override
    protected void convert(BaseViewHolder helper, WifiLockOperationRecord record) {
        int size = getData().size();
        int position = helper.getPosition();
        TextView tvTime = helper.getView(R.id.tv_time);
        long time = record.getTime();
        String s = DateUtils.currentLong2HourMin(time * 1000);
        tvTime.setText(TextUtils.isEmpty(s) ? "" : s);
        helper.getView(R.id.view_top).setVisibility(position == 0 ? View.INVISIBLE : View.VISIBLE);
        helper.getView(R.id.view_bottom).setVisibility(position == size - 1 ? View.INVISIBLE : View.VISIBLE);


        TextView tvContent = helper.getView(R.id.tv_content);
        String content = BleUtil.getAlarmByType(record.getType(), mContext);
        TextView tvRight = helper.getView(R.id.tv_right);
        int type = record.getType();
        //1开锁 2关锁 3添加密钥 4删除密钥 5修改管理员密码 6自动模式 7手动模式 8安全模式切换 9常用模式切换 10反锁模式 11布防模式
        String left = "";
        String right = "";
        String shareUserNickname;
        int pwdType = record.getPwdType();
        switch (type) {
            case 1: //开锁
                left = record.getUserNickname();
                if (TextUtils.isEmpty(left)) {
                    left = record.getPwdNum() + "";
                }
                switch (pwdType) {
                    //	密码类型：0密码 3卡片 4指纹 8APP用户 9机械钥匙
                    case 0:
                        right = mContext.getString(R.string.password_open);
                        if (record.getPwdNum() == 252) {
                            left = mContext.getString(R.string.offline_password);
                        }
                        break;
                    case 4:
                        right = mContext.getString(R.string.fingerprint_open);
                        break;
                    case 3:
                        right = mContext.getString(R.string.rfid_open);
                        break;
                    case 8:
                        right = mContext.getString(R.string.app_open);
                        break;
                    case 9:
                        right = mContext.getString(R.string.machine_key);
                        break;
                    default:
                        right = mContext.getString(R.string.unknown_open);
                        break;
                }
                break;
            case 2: //关锁
                right = mContext.getString(R.string.lock_already_lock);
                break;
            case 3: //添加秘钥
                right = mContext.getString(R.string.lock_add) + record.getPwdNum();
                switch (pwdType) {
                    //	密码类型：0密码 3卡片 4指纹 8APP用户 9机械钥匙
                    case 0:
                        right = right + mContext.getString(R.string.password);
                        break;
                    case 4:
                        right = right + mContext.getString(R.string.fingerprint);
                        break;
                    case 3:
                        right = right + mContext.getString(R.string.card);
                        break;
                    default:
                        right = right + mContext.getString(R.string.unknown_open);
                        break;
                }
                break;
            case 4: //删除秘钥
                int pwdNum = record.getPwdNum();
                right = mContext.getString(R.string.lock_delete) + ((pwdNum == 255) ? "全部" : pwdNum + "");
                switch (pwdType) {
                    //	密码类型：0密码 3卡片 4指纹 8APP用户 9机械钥匙
                    case 0:
                        right = right + mContext.getString(R.string.password);
                        break;
                    case 4:
                        right = right + mContext.getString(R.string.fingerprint);
                        break;
                    case 3:
                        right = right + mContext.getString(R.string.card);
                        break;
                    default:
                        right = mContext.getString(R.string.unknown_open);
                        break;
                }
                break;
            case 5: //修改管理员密码
                right = mContext.getString(R.string.wifi_lock_modify_admin_password);
                break;
            case 6: //自动模式
                right = mContext.getString(R.string.wifi_lock_auto_model);
                break;
            case 7: //手动模式
                right = mContext.getString(R.string.wifi_lock_hand_model);
                break;
            case 8: //安全模式切换
                right = mContext.getString(R.string.wifi_lock_safe_model);
                break;
            case 9: //常用模式切换
                right = mContext.getString(R.string.wifi_lock_commen_model);
                break;
            case 10: //反锁模式
                right = mContext.getString(R.string.wifi_lock_bacl_lock_model);
                break;
            case 11: //布防模式
                right = mContext.getString(R.string.wifi_lock_bufang_model);
                break;
            // 12修改密码昵称
            case 12:
                right = mContext.getString(R.string.modify) + (record.getPwdNum() > 9 ? ""+record.getPwdNum():"0" + record.getPwdNum());
                switch (pwdType) {
                    //	密码类型：0密码 3卡片 4指纹 8APP用户 9机械钥匙
                    case 0:
                        right = right+mContext.getString(R.string.password);
                        break;
                    case 4:
                        right = right + mContext.getString(R.string.fingerprint);
                        break;
                    case 3:
                        right = right + mContext.getString(R.string.card);
                        break;
                    default:
                        right = right + mContext.getString(R.string.unknown_open);
                        break;
                }
                right = right + mContext.getString(R.string.nick_for) + record.getPwdNickname();
                break;
            //13添加分享用户
            case 13:
                  shareUserNickname = record.getShareUserNickname();
                right = mContext.getString(R.string.wifi_add)+ (!TextUtils.isEmpty(shareUserNickname)?shareUserNickname:record.getShareAccount())+mContext.getString(R.string.wifi_add2);
                break;
            //14删除分享用户
            case 14:
                  shareUserNickname = record.getShareUserNickname();
                right = mContext.getString(R.string.wifi_delete)+ (!TextUtils.isEmpty(shareUserNickname)?shareUserNickname:record.getShareAccount())+mContext.getString(R.string.wifi_delete2);
                break;
            default:
                right = mContext.getString(R.string.unknown_open);
                break;
        }
        LogUtils.e("Adapter 显示的   " + content);
        // 机械开锁/APP开锁/自动开锁/密码开锁/门卡开锁/指纹开锁
        tvContent.setText(left);
        tvRight.setText(right);
    }


}
