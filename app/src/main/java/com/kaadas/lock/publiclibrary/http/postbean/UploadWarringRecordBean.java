package com.kaadas.lock.publiclibrary.http.postbean;


import com.kaadas.lock.publiclibrary.ble.bean.WarringRecord;

import java.util.List;

/**
 * Create By lxj  on 2019/3/3
 * Describe
 */
public class UploadWarringRecordBean {

    /**
     * devName : GI132231004
     * warningList : [{"warningType":2,"warningTime":1561557361639,"content":"预警内容"},{"warningType":3,"warningTime":1561557361640,"content":"预警内容"}]
     */

    private String devName;
    private List<WarringRecord> warningList;

    public UploadWarringRecordBean(String devName, List<WarringRecord> warningList) {
        this.devName = devName;
        this.warningList = warningList;
    }

    public String getDevName() {
        return devName;
    }

    public void setDevName(String devName) {
        this.devName = devName;
    }

    public List<WarringRecord> getWarningList() {
        return warningList;
    }

    public void setWarningList(List<WarringRecord> warningList) {
        this.warningList = warningList;
    }


}
