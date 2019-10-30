package com.kaadas.lock.publiclibrary.ota.face;

public interface IOtaListener {
    /**
     * Socket连接成功
     */
    void onConnected();

    /**
     * 进度更新
     *
     * @param currentPackage 为0时  表示    写入配置数据成功  正准备发送数据
     * @param totalPackage   currentPackage 等于 totalPackage时，表示数据写入完成。
     *                       但是还没有  写入finish指令  且返回成功
     */
    void onProgress(int currentPackage, int totalPackage);

    /**
     * ota完成
     * 写入finish指令  且返回成功
     */
    void onComplete();

    /**
     * 出错了
     *
     * @param errorCode 返回ERROR          -1
     *                  inputStream  为空   -2
     *                  读取数据出多       -3
     *                  返回未知数据       -4
     *                  写数据出错        -5
     *                  socket 为空        -6
     *                  连接失败    -7
     *                  文件未找到错误   -8
     *                  读取文件失败     -9
     *                  线程中断错误    -10
     *                  读取数据超时  -11
     *                  读取文件中数据的个数失败   -12
     * @param throwable
     */
    void onError(int errorCode, Throwable throwable);


    void startSendFile();
}
