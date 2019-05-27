package com.kaadas.lock.utils.greenDao.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;

import androidx.annotation.IntDef;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Index;

/**
 * 服务器的网关信息
 */
@Entity
public class GatewayServiceInfo {
    @Id
    private String deviceIdUid;
    private String deviceSN;
    private String deviceNickName;
    private String adminuid;
    private String adminName;
    private String adminNickname;
    private int isAdmin;
    private String meUsername;
    private String mePwd;
    private int meBindState;
    private String uid;

    @Generated(hash = 546772347)
    public GatewayServiceInfo(String deviceIdUid, String deviceSN,
            String deviceNickName, String adminuid, String adminName,
            String adminNickname, int isAdmin, String meUsername, String mePwd,
            int meBindState, String uid) {
        this.deviceIdUid = deviceIdUid;
        this.deviceSN = deviceSN;
        this.deviceNickName = deviceNickName;
        this.adminuid = adminuid;
        this.adminName = adminName;
        this.adminNickname = adminNickname;
        this.isAdmin = isAdmin;
        this.meUsername = meUsername;
        this.mePwd = mePwd;
        this.meBindState = meBindState;
        this.uid = uid;
    }

    @Generated(hash = 426171648)
    public GatewayServiceInfo() {
    }

    public String getDeviceIdUid() {
        return deviceIdUid;
    }

    public void setDeviceIdUid(String deviceIdUid) {
        this.deviceIdUid = deviceIdUid;
    }

    public String getDeviceSN() {
        return deviceSN;
    }

    public void setDeviceSN(String deviceSN) {
        this.deviceSN = deviceSN;
    }

    public String getDeviceNickName() {
        return deviceNickName;
    }

    public void setDeviceNickName(String deviceNickName) {
        this.deviceNickName = deviceNickName;
    }

    public String getAdminuid() {
        return adminuid;
    }

    public void setAdminuid(String adminuid) {
        this.adminuid = adminuid;
    }

    public String getAdminName() {
        return adminName;
    }

    public void setAdminName(String adminName) {
        this.adminName = adminName;
    }

    public String getAdminNickname() {
        return adminNickname;
    }

    public void setAdminNickname(String adminNickname) {
        this.adminNickname = adminNickname;
    }

    public int getIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(int isAdmin) {
        this.isAdmin = isAdmin;
    }

    public String getMeUsername() {
        return meUsername;
    }

    public void setMeUsername(String meUsername) {
        this.meUsername = meUsername;
    }

    public String getMePwd() {
        return mePwd;
    }

    public void setMePwd(String mePwd) {
        this.mePwd = mePwd;
    }

    public int getMeBindState() {
        return meBindState;
    }

    public void setMeBindState(int meBindState) {
        this.meBindState = meBindState;
    }

    public String getUid() {
        return this.uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
