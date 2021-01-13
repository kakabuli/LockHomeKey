package com.kaadas.lock.publiclibrary.bean;

import java.io.Serializable;

public class ClothesHangerMachineMotorBean implements Serializable {
    private int action;
    private int status;/*0x00:正常 0x01:到达上限位 0x02:到达下限位 0x03:遇阻*/

    public int getAction() {
        return action;
    }

    public void setAction(int action) {
        this.action = action;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
