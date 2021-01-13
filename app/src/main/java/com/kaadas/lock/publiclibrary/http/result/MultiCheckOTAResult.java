package com.kaadas.lock.publiclibrary.http.result;

import java.io.Serializable;
import java.util.List;

public class MultiCheckOTAResult extends BaseResult implements Serializable {

    /**
    {
    "code": "200",
    "msg": "成功",
    "nowTime": 1587954859,
    "data": {
        "upgradeTask": [
            {
                "fileLen": 42,
                "fileUrl": "47.106.83.60/otaFiles/73c4e82adcd84ab6a337603abfb0ad84?filename=test.txt",
                "devNum": 1,
                "fileMd5": "a5dcbcd00801bc8637c6882560a325a7",
                "fileVersion": "test-1.0"
            },
            {
                "fileLen": 42,
                "fileUrl": "47.106.83.60/otaFiles/73c4e82adcd84ab6a337603abfb0ad84?filename=test.txt",
                "devNum": 2,
                "fileMd5": "a5dcbcd00801bc8637c6882560a325a7",
                "fileVersion": "test-1.0"
            }
        ]
    }
}
     */

    private String msg;
    private String code;
    private long nowTime;
    private Param data;

    public static class Param{
        private List<UpgradeTask> upgradeTask;

        public List<UpgradeTask> getUpgradeTask() {
            return upgradeTask;
        }

        public void setUpgradeTask(List<UpgradeTask> upgradeTask) {
            this.upgradeTask = upgradeTask;
        }

        @Override
        public String toString() {
            return "Param{" +
                    "upgradeTask=" + upgradeTask +
                    '}';
        }
    }


    public static class UpgradeTask {
        private int fileLen;
        private String fileUrl;
        private int devNum;
        private String fileMd5;
        private String fileVersion;

        public int getFileLen() {
            return fileLen;
        }

        public void setFileLen(int fileLen) {
            this.fileLen = fileLen;
        }

        public String getFileUrl() {
            return fileUrl;
        }

        public void setFileUrl(String fileUrl) {
            this.fileUrl = fileUrl;
        }

        public int getDevNum() {
            return devNum;
        }

        public void setDevNum(int devNum) {
            this.devNum = devNum;
        }

        public String getFileMd5() {
            return fileMd5;
        }

        public void setFileMd5(String fileMd5) {
            this.fileMd5 = fileMd5;
        }

        public String getFileVersion() {
            return fileVersion;
        }

        public void setFileVersion(String fileVersion) {
            this.fileVersion = fileVersion;
        }

        @Override
        public String toString() {
            return "UpgradeTask{" +
                    "fileLen=" + fileLen +
                    ", fileUrl='" + fileUrl + '\'' +
                    ", devNum=" + devNum +
                    ", fileMd5='" + fileMd5 + '\'' +
                    ", fileVersion='" + fileVersion + '\'' +
                    '}';
        }
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public long getNowTime() {
        return nowTime;
    }

    public void setNowTime(long nowTime) {
        this.nowTime = nowTime;
    }

    public Param getData() {
        return data;
    }

    public void setData(Param data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "MultiCheckOTAResult{" +
                "msg='" + msg + '\'' +
                ", code='" + code + '\'' +
                ", nowTime=" + nowTime +
                ", data=" + data +
                '}';
    }
}
