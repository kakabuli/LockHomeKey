package com.kaadas.lock.publiclibrary.http.result;

public class OTAResult {

    /**
     * msg : 操作成功
     * code : 200
     * data : {"fileUrl":"http://121.201.57.214/otaUpgradeFile/9211131645b34ef0a62ad8ff70c38393.png","fileMd5":"48d6d2a38e295611aa68959ccd6b3771","fileVersion":"1.5"}
     */

    private String msg;
    private String code;
    private UpdateFileInfo data;

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

    public UpdateFileInfo getData() {
        return data;
    }

    public void setData(UpdateFileInfo data) {
        this.data = data;
    }

    public static class UpdateFileInfo {
        /**
         * fileUrl : http://121.201.57.214/otaUpgradeFile/9211131645b34ef0a62ad8ff70c38393.png
         * fileMd5 : 48d6d2a38e295611aa68959ccd6b3771
         * fileVersion : 1.5
         */

        private String fileUrl;
        private String fileMd5;
        private String fileVersion;

        public String getFileUrl() {
            return fileUrl;
        }

        public void setFileUrl(String fileUrl) {
            this.fileUrl = fileUrl;
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
            return "UpdateFileInfo{" +
                    "fileUrl='" + fileUrl + '\'' +
                    ", fileMd5='" + fileMd5 + '\'' +
                    ", fileVersion='" + fileVersion + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "OTAResult{" +
                "msg='" + msg + '\'' +
                ", code='" + code + '\'' +
                ", data=" + data +
                '}';
    }
}
