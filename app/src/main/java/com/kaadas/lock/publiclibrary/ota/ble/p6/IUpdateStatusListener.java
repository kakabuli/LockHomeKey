package com.kaadas.lock.publiclibrary.ota.ble.p6;

public interface IUpdateStatusListener {


    //进度改变
    void onProcessChange(float currentLine, float totalLine);

    //文件读取成功  开始OTA
    void onFileReadComplete();

    //加载引导程序
    void otaEnterBootloader();

    //设置应用程序元数据

    void otaSetApplicationMetadata();

    //验证程序
    void otaVerifyApplication();

    //执行退出boot模式指令
    void otaEndBootloader();

    //升级完成
    void upgradeCompleted();

    //正在升级
    void onProcessing();

    //正在执行设置EIV指令
    void otaSetEiv();


}
