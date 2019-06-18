/*
 * Copyright Cypress Semiconductor Corporation, 2014-2018 All rights reserved.
 *
 * This software, associated documentation and materials ("Software") is
 * owned by Cypress Semiconductor Corporation ("Cypress") and is
 * protected by and subject to worldwide patent protection (UnitedStates and foreign), United States copyright laws and international
 * treaty provisions. Therefore, unless otherwise specified in a separate license agreement between you and Cypress, this Software
 * must be treated like any other copyrighted material. Reproduction,
 * modification, translation, compilation, or representation of this
 * Software in any other form (e.g., paper, magnetic, optical, silicon)
 * is prohibited without Cypress's express written permission.
 *
 * Disclaimer: THIS SOFTWARE IS PROVIDED AS-IS, WITH NO WARRANTY OF ANY
 * KIND, EXPRESS OR IMPLIED, INCLUDING, BUT NOT LIMITED TO,
 * NONINFRINGEMENT, IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE. Cypress reserves the right to make changes
 * to the Software without notice. Cypress does not assume any liability
 * arising out of the application or use of Software or any product or
 * circuit described in the Software. Cypress does not authorize its
 * products for use as critical components in any products where a
 * malfunction or failure may reasonably be expected to result in
 * significant injury or death ("High Risk Product"). By including
 * Cypress's product in a High Risk Product, the manufacturer of such
 * system or application assumes all risk of such use and in doing so
 * indemnifies Cypress against all liability.
 *
 * Use of this Software may be limited by and subject to the applicable
 * Cypress software license agreement.
 *
 *
 */

package com.kaadas.lock.publiclibrary.ota.p6.OTAFirmwareUpdate;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.publiclibrary.ota.p6.BluetoothLeService;
import com.kaadas.lock.publiclibrary.ota.p6.CommonUtils.CheckSumUtils;
import com.kaadas.lock.publiclibrary.ota.p6.CommonUtils.Constants;
import com.kaadas.lock.publiclibrary.ota.p6.CommonUtils.ConvertUtils;
import com.kaadas.lock.publiclibrary.ota.p6.CommonUtils.Utils;
import com.kaadas.lock.publiclibrary.ota.p6.IUpdateStatusListener;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.Rsa;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class OTAFUHandler_v1 extends OTAFUHandlerBase {
    private OTAFirmwareWrite_v1 mOtaFirmwareWrite;
    private Map<String, List<OTAFlashRowModel_v1>> mFileContents;
    private byte mCheckSumType;
    private final int mMaxDataSize;
    private String TAG = "OTA升级";
    private Handler handler = new Handler(Looper.getMainLooper());
    private long lastErrorTime = 0;//上次错误的时间
    private int errorTimes = 0;  //错误次数
    private static final long sumErrorTime = 3000; //错误累加的时间
    private OTAFlashRowModel_v1.Header headerRow;


    public OTAFUHandler_v1(Activity activity, IUpdateStatusListener listener,
                           BluetoothGattCharacteristic otaCharacteristic
            , String filepath, OTAFUHandlerCallback callback) {
        super(activity, listener, otaCharacteristic,
                Constants.ACTIVE_APP_NO_CHANGE, Constants.NO_SECURITY_KEY, filepath, callback);

        this.mMaxDataSize = 300;
        Log.e(TAG, "最大个数是  " + mMaxDataSize);
    }

    @Override
    public void prepareFileWrite() {
        mOtaFirmwareWrite = new OTAFirmwareWrite_v1(mOtaCharacteristic);
        File file = new File(mFilepath);
        LogUtils.e("文件时否存在   " + file.exists() + "  文件地址   " + mFilepath);
        final CustomFileReader_v1 customFileReader = new CustomFileReader_v1(mFilepath);
        /**
         * Reads the file content and provides a 1 second delay
         */
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    mFileContents = customFileReader.readLines();
                    startOTA();
                } catch (CustomFileReader_v1.InvalidFileFormatException e) {
                    showErrorDialogMessage(BluetoothLeService.ERROR_TAG_FILE_ERROR, "文件准备失败  " + e.getMessage(), true);
                }
            }
        }, 1000);
    }

    private void startOTA() {
        // 升级状态   文件读取成功
        if (listener != null) {
            listener.onFileReadComplete();
        }
        Utils.setStringSharedPreference(getmActivity(), Constants.PREF_BOOTLOADER_STATE + BluetoothLeService.mBluetoothDeviceAddress, "" + BootLoaderCommands_v1.ENTER_BOOTLOADER);
        setFileUpgradeStarted(true);
        generatePendingNotification(getmActivity());

        headerRow = (OTAFlashRowModel_v1.Header) mFileContents.get(CustomFileReader_v1.KEY_HEADER).get(0);
        this.mCheckSumType = headerRow.mCheckSumType;
        this.mActiveApp = headerRow.mAppId;

        //Send Enter Bootloader command
        LogUtils.e("  文件数据是2    mProductId  " + Rsa.bytesToHexString(headerRow.mProductId) +"    mAppId    "  + Rsa.byteToHexString(mActiveApp)+"   mCheckSumType " +Rsa.byteToHexString(mCheckSumType));
        mOtaFirmwareWrite.OTAEnterBootLoaderCmd(mCheckSumType, headerRow.mProductId);
        postTimeout();
        // 升级状态    执行Enter引导加载程序命令
        if (listener != null) {
            listener.otaEnterBootloader();
        }
    }

    @Override
    public void processOTAStatus(String status, Bundle extras) {
        if (extras.containsKey(Constants.EXTRA_ERROR_OTA)) {
            handler.removeCallbacks(timeOutRunnable);
            Log.e(TAG, "移除数据超时    EXTRA_ERROR_OTA " );
            String errorMessage = extras.getString(Constants.EXTRA_ERROR_OTA);
            Log.e(TAG, "升级错误  " + errorMessage);
            //todo 升级错误时的回调
            if (System.currentTimeMillis() - lastErrorTime > sumErrorTime || System.currentTimeMillis() - lastErrorTime < 0) {
                errorTimes = 1;
            } else {
                errorTimes++;
            }
            lastErrorTime = System.currentTimeMillis();
            if (errorTimes > 3) { //如果三秒内连续超过三次
                showErrorDialogMessage(BluetoothLeService.ERROR_TAG_ERROR_THREE, "连续三次错误" + errorMessage, false);
            } else {
                int oldRowNum = Utils.getIntSharedPreference(getmActivity(), Constants.PREF_PROGRAM_ROW_NO_OLD + BluetoothLeService.mBluetoothDeviceAddress);
                int oldRowPos = Utils.getIntSharedPreference(getmActivity(), Constants.PREF_PROGRAM_ROW_START_POS_OLD + BluetoothLeService.mBluetoothDeviceAddress);
                int nowRowNum = Utils.getIntSharedPreference(getmActivity(), Constants.PREF_PROGRAM_ROW_NO + BluetoothLeService.mBluetoothDeviceAddress);
                int nowRowPos = Utils.getIntSharedPreference(getmActivity(), Constants.PREF_PROGRAM_ROW_START_POS + BluetoothLeService.mBluetoothDeviceAddress);
                if (nowRowPos == 0) {
                    Utils.setIntSharedPreference(getmActivity(), Constants.PREF_PROGRAM_ROW_NO + BluetoothLeService.mBluetoothDeviceAddress, oldRowNum);
                }
                LogUtils.e("新的行   " + nowRowNum + "  新的pos  " + nowRowPos + " 旧的行  " + oldRowNum + "   旧的pos   " + oldRowPos);
                Utils.setIntSharedPreference(getmActivity(), Constants.PREF_PROGRAM_ROW_START_POS + BluetoothLeService.mBluetoothDeviceAddress, 0);
                Utils.setIntSharedPreference(getmActivity(), Constants.PREF_PROGRAM_ROW_NO + BluetoothLeService.mBluetoothDeviceAddress, nowRowNum);
                Utils.setStringSharedPreference(getmActivity(), Constants.PREF_BOOTLOADER_STATE + BluetoothLeService.mBluetoothDeviceAddress, "" + BootLoaderCommands_v1.ENTER_BOOTLOADER);
                LogUtils.e("  文件数据是1    mProductId  " + Rsa.bytesToHexString(headerRow.mProductId) +"    mAppId    "  + Rsa.byteToHexString(mActiveApp)+"   mCheckSumType " +Rsa.byteToHexString(mCheckSumType));
                mOtaFirmwareWrite.OTAEnterBootLoaderCmd(mCheckSumType, headerRow.mProductId);
                postTimeout();
            }
        } else if (status.equalsIgnoreCase("" + BootLoaderCommands_v1.ENTER_BOOTLOADER)) {
            handler.removeCallbacks(timeOutRunnable);
            Log.e(TAG, "移除数据超时    ENTER_BOOTLOADER " );
            if (extras.containsKey(Constants.EXTRA_SILICON_ID) && extras.containsKey(Constants.EXTRA_SILICON_REV)) {
                OTAFlashRowModel_v1.Header headerRow = (OTAFlashRowModel_v1.Header) mFileContents.get(CustomFileReader_v1.KEY_HEADER).get(0);
                byte[] siliconIdReceived = extras.getByteArray(Constants.EXTRA_SILICON_ID);
                byte siliconRevReceived = extras.getByte(Constants.EXTRA_SILICON_REV);

                if (Arrays.equals(headerRow.mSiliconId, siliconIdReceived) && headerRow.mSiliconRev == siliconRevReceived) {
                    //Send Set Application Metadata command
                    OTAFlashRowModel_v1.AppInfo appInfoRow = (OTAFlashRowModel_v1.AppInfo) mFileContents.get(CustomFileReader_v1.KEY_APPINFO).get(0);
                    mOtaFirmwareWrite.OTASetAppMetadataCmd(mCheckSumType, mActiveApp, appInfoRow.mAppStart, appInfoRow.mAppSize);
                    postTimeout();
                    Utils.setStringSharedPreference(getmActivity(), Constants.PREF_BOOTLOADER_STATE + BluetoothLeService.mBluetoothDeviceAddress, "" + BootLoaderCommands_v1.SET_APP_METADATA);
                    //升级状态   执行Set应用程序元数据命令…
                    if (listener != null) {
                        listener.otaSetApplicationMetadata();
                    }
                } else {
                    //Wrong SiliconId and SiliconRev
                    showErrorDialogMessage(BluetoothLeService.ERROR_TAG_START_COMAMND, "开始指令错误", true);
                    //升级状态   错误:SiliconID或SiliconRev不匹配
                }
            }
        } else if (status.equalsIgnoreCase("" + BootLoaderCommands_v1.SET_APP_METADATA)) {
            handler.removeCallbacks(timeOutRunnable);
            Log.e(TAG, "移除数据超时    SET_APP_METADATA " );
            List<OTAFlashRowModel_v1> dataRows = mFileContents.get(CustomFileReader_v1.KEY_DATA);
            //获取行号
            int rowNum = Utils.getIntSharedPreference(getmActivity(), Constants.PREF_PROGRAM_ROW_NO + BluetoothLeService.mBluetoothDeviceAddress);
            OTAFlashRowModel_v1 dataRow = dataRows.get(rowNum);
            if (dataRow instanceof OTAFlashRowModel_v1.EIV) {
                Log.e("OTA升级", "发送数据1 writeEiv   " + status);
                writeEiv(rowNum);//Set EIV
            } else if (dataRow instanceof OTAFlashRowModel_v1.Data) {
                Log.e("OTA升级", "发送数据2 writeData   " + status);
                writeData(rowNum);//Program data row
            }
        } else if (status.equalsIgnoreCase("" + BootLoaderCommands_v1.SET_EIV)) {
            handler.removeCallbacks(timeOutRunnable);
            Log.e(TAG, "移除数据超时    SET_EIV " );
            //获取行号
            int rowNum = Utils.getIntSharedPreference(getmActivity(), Constants.PREF_PROGRAM_ROW_NO + BluetoothLeService.mBluetoothDeviceAddress);
            rowNum++;//Increment row number
            List<OTAFlashRowModel_v1> dataRows = mFileContents.get(CustomFileReader_v1.KEY_DATA);
            //Update progress bar
            int totalLines = dataRows.size();
            showProgress(mProgressBarPosition, rowNum, totalLines);
            if (rowNum < totalLines) {//Process next row
                int oldRowNum = Utils.getIntSharedPreference(getmActivity(), Constants.PREF_PROGRAM_ROW_NO + BluetoothLeService.mBluetoothDeviceAddress);
                Utils.setIntSharedPreference(getmActivity(), Constants.PREF_PROGRAM_ROW_NO_OLD + BluetoothLeService.mBluetoothDeviceAddress, oldRowNum);
                int oldRowPos = Utils.getIntSharedPreference(getmActivity(), Constants.PREF_PROGRAM_ROW_START_POS + BluetoothLeService.mBluetoothDeviceAddress);
                Utils.setIntSharedPreference(getmActivity(), Constants.PREF_PROGRAM_ROW_START_POS_OLD + BluetoothLeService.mBluetoothDeviceAddress, oldRowPos);
                Utils.setIntSharedPreference(getmActivity(), Constants.PREF_PROGRAM_ROW_NO + BluetoothLeService.mBluetoothDeviceAddress, rowNum);
                Utils.setIntSharedPreference(getmActivity(), Constants.PREF_PROGRAM_ROW_START_POS + BluetoothLeService.mBluetoothDeviceAddress, 0);
                OTAFlashRowModel_v1 dataRow = dataRows.get(rowNum);
                if (dataRow instanceof OTAFlashRowModel_v1.EIV) {
                    Log.e("OTA升级", "发送数据3 writeEiv   " + status);
                    writeEiv(rowNum);//Set EIV
                } else if (dataRow instanceof OTAFlashRowModel_v1.Data) {
                    Log.e("OTA升级", "发送数据4 writeData   " + status);
                    writeData(rowNum);//Program data row
                }
            }
            if (rowNum == totalLines) {//All rows have been processed
                //升级完成，将写入数据的位置复位
                Utils.setIntSharedPreference(getmActivity(), Constants.PREF_PROGRAM_ROW_NO + BluetoothLeService.mBluetoothDeviceAddress, 0);
                Utils.setIntSharedPreference(getmActivity(), Constants.PREF_PROGRAM_ROW_START_POS + BluetoothLeService.mBluetoothDeviceAddress, 0);
                //Programming done, send VerifyApplication command
                mOtaFirmwareWrite.OTAVerifyAppCmd(mCheckSumType, mActiveApp);
                postTimeout();
                Utils.setStringSharedPreference(getmActivity(), Constants.PREF_BOOTLOADER_STATE + BluetoothLeService.mBluetoothDeviceAddress, "" + BootLoaderCommands_v1.VERIFY_APP);
                //升级状态  执行验证应用程序命令…
                if (listener != null) {
                    listener.otaVerifyApplication();
                }
            }
        } else if (status.equalsIgnoreCase("" + BootLoaderCommands_v1.SEND_DATA)) {
            handler.removeCallbacks(timeOutRunnable);
            Log.e(TAG, "移除数据超时    SEND_DATA " );
            int rowNum = Utils.getIntSharedPreference(getmActivity(), Constants.PREF_PROGRAM_ROW_NO + BluetoothLeService.mBluetoothDeviceAddress);
            Log.e("OTA升级", "发送数据5 writeData   " + status);
            writeData(rowNum);//Program data row
        } else if (status.equalsIgnoreCase("" + BootLoaderCommands_v1.SEND_DATA_WITHOUT_RESPONSE)) {
            handler.removeCallbacks(timeOutRunnable);
            Log.e(TAG, "移除数据超时    SEND_DATA_WITHOUT_RESPONSE " );
            int rowNum = Utils.getIntSharedPreference(getmActivity(), Constants.PREF_PROGRAM_ROW_NO + BluetoothLeService.mBluetoothDeviceAddress);
            Log.e("OTA升级", "发送数据6 writeData   " + status);
            writeData(rowNum);//Program data row
        } else if (status.equalsIgnoreCase("" + BootLoaderCommands_v1.PROGRAM_DATA)) {
            handler.removeCallbacks(timeOutRunnable);
            Log.e(TAG, "移除数据超时    PROGRAM_DATA " );
            int rowNum = Utils.getIntSharedPreference(getmActivity(), Constants.PREF_PROGRAM_ROW_NO + BluetoothLeService.mBluetoothDeviceAddress);
            rowNum++;//Increment row number
            List<OTAFlashRowModel_v1> dataRows = mFileContents.get(CustomFileReader_v1.KEY_DATA);
            //Update progress bar
            int totalLines = dataRows.size();
            showProgress(mProgressBarPosition, rowNum, totalLines);
            //进度改变
            if (rowNum < totalLines) {//Process next row
                int oldRowNum = Utils.getIntSharedPreference(getmActivity(), Constants.PREF_PROGRAM_ROW_NO + BluetoothLeService.mBluetoothDeviceAddress);
                Utils.setIntSharedPreference(getmActivity(), Constants.PREF_PROGRAM_ROW_NO_OLD + BluetoothLeService.mBluetoothDeviceAddress, oldRowNum);
                int oldRowPos = Utils.getIntSharedPreference(getmActivity(), Constants.PREF_PROGRAM_ROW_START_POS + BluetoothLeService.mBluetoothDeviceAddress);
                Utils.setIntSharedPreference(getmActivity(), Constants.PREF_PROGRAM_ROW_START_POS_OLD + BluetoothLeService.mBluetoothDeviceAddress, oldRowPos);
                Utils.setIntSharedPreference(getmActivity(), Constants.PREF_PROGRAM_ROW_NO + BluetoothLeService.mBluetoothDeviceAddress, rowNum);
                Utils.setIntSharedPreference(getmActivity(), Constants.PREF_PROGRAM_ROW_START_POS + BluetoothLeService.mBluetoothDeviceAddress, 0);
                OTAFlashRowModel_v1 dataRow = dataRows.get(rowNum);
                if (dataRow instanceof OTAFlashRowModel_v1.EIV) {
                    Log.e("OTA升级", "发送数据7 writeEiv   " + status);
                    writeEiv(rowNum);//Set EIV
                } else if (dataRow instanceof OTAFlashRowModel_v1.Data) {
                    Log.e("OTA升级", "发送数据8 writeData   " + status);
                    writeData(rowNum);//Program data row
                }
            }
            if (rowNum == totalLines) {//All rows have been processed   所有数据都已经处理
                //升级完成
                Utils.setIntSharedPreference(getmActivity(), Constants.PREF_PROGRAM_ROW_NO + BluetoothLeService.mBluetoothDeviceAddress, 0);
                Utils.setIntSharedPreference(getmActivity(), Constants.PREF_PROGRAM_ROW_START_POS + BluetoothLeService.mBluetoothDeviceAddress, 0);
                //Programming done, send VerifyApplication command
                mOtaFirmwareWrite.OTAVerifyAppCmd(mCheckSumType, mActiveApp);
                postTimeout();
                Utils.setStringSharedPreference(getmActivity(), Constants.PREF_BOOTLOADER_STATE + BluetoothLeService.mBluetoothDeviceAddress, "" + BootLoaderCommands_v1.VERIFY_APP);
                //升级状态 执行验证应用程序命令…
                if (listener != null) {
                    listener.otaVerifyApplication();
                }
            }
        } else if (status.equalsIgnoreCase("" + BootLoaderCommands_v1.VERIFY_APP)) {
            handler.removeCallbacks(timeOutRunnable);
            Log.e(TAG, "移除数据超时    VERIFY_APP " );
            if (extras.containsKey(Constants.EXTRA_VERIFY_APP_STATUS)) {
                byte statusReceived = extras.getByte(Constants.EXTRA_VERIFY_APP_STATUS);
                if (statusReceived == 1) {
                    //Send ExitBootloader command
                    mOtaFirmwareWrite.OTAExitBootloaderCmd(mCheckSumType);
                    Utils.setStringSharedPreference(getmActivity(), Constants.PREF_BOOTLOADER_STATE + BluetoothLeService.mBluetoothDeviceAddress, "" + BootLoaderCommands_v1.EXIT_BOOTLOADER);
                    //  执行退出BootLoader指令
                    if (listener != null) {
                        listener.otaEndBootloader();
                    }

                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            BluetoothLeService.disconnect();
                        }
                    }, 1000);
                } else {
                    showErrorDialogMessage(BluetoothLeService.ERROR_TAG_APP_CHECK_ERROR, "写入完成校验错误", false);
                }
            }
        } else if (status.equalsIgnoreCase("" + BootLoaderCommands_v1.EXIT_BOOTLOADER)) {  //此处不会走，因为Exit_bootloader没有返回值
            handler.removeCallbacks(timeOutRunnable);
            Log.e(TAG, "移除数据超时    EXIT_BOOTLOADER " );
            Log.e(TAG, "升级成功，退出");
            BluetoothDevice device = BluetoothLeService.getRemoteDevice();
            //  升级成功
            if (listener != null) {
                listener.upgradeCompleted();
            }
            setFileUpgradeStarted(false);
            storeAndReturnDeviceAddress();
            BluetoothLeService.disconnect();
        }
    }

    protected void showErrorDialogMessage(String tag, String errorMessage, final boolean stayOnPage) {
        if (BluetoothLeService.ERROR_TAG_FILE_ERROR.equals(tag)) {
            Log.e(TAG, "准备文件出错  ，删除文件？");
            File file = new File(mFilepath);
            if (file.exists()) { //删除本地的升级文件，本地升级文件有问题
                file.delete();  //此处还没有写入指令
            }
        } else {
            mOtaFirmwareWrite.OTAExitBootloaderCmd(mCheckSumType);  //直接重新开始
            Utils.setStringSharedPreference(MyApplication.getInstance(), Constants.PREF_BOOTLOADER_STATE + BluetoothLeService.mBluetoothDeviceAddress, "Default");
            Utils.setIntSharedPreference(MyApplication.getInstance(), Constants.PREF_PROGRAM_ROW_NO + BluetoothLeService.mBluetoothDeviceAddress, 0);
            Utils.setIntSharedPreference(MyApplication.getInstance(), Constants.PREF_PROGRAM_ROW_START_POS + BluetoothLeService.mBluetoothDeviceAddress, 0);
        }
        BluetoothLeService.disconnect();
        mParent.showErrorDialogMessage(tag, errorMessage, stayOnPage);
    }


    private void writeData(int rowNum) {
        //开始的position
        int startPosition = Utils.getIntSharedPreference(getmActivity(), Constants.PREF_PROGRAM_ROW_START_POS + BluetoothLeService.mBluetoothDeviceAddress);
        OTAFlashRowModel_v1.Data dataRow = (OTAFlashRowModel_v1.Data) mFileContents.get(CustomFileReader_v1.KEY_DATA).get(rowNum);

        int payloadLength = dataRow.mData.length - startPosition;
        boolean isLastPacket = (payloadLength <= mMaxDataSize);
        if (!isLastPacket) {
            payloadLength = mMaxDataSize;
        }
        Log.e("OTA升级", "  行编号  " + rowNum + "  startPosition  " + startPosition + "   isLastPacket  " + isLastPacket);


        final byte[] payload = new byte[payloadLength];
        for (int i = 0; i < payloadLength; i++) {
            byte b = dataRow.mData[startPosition];
            payload[i] = b;
            startPosition++;
        }
        if (!isLastPacket) {
            //Send SendData command
            Log.e("升级控制2", "带有回调");
            mOtaFirmwareWrite.OTASendDataCmd(mCheckSumType, payload);
            postTimeout();
            Utils.setStringSharedPreference(getmActivity(), Constants.PREF_BOOTLOADER_STATE + BluetoothLeService.mBluetoothDeviceAddress, "" + BootLoaderCommands_v1.SEND_DATA);
            int oldRowNum = Utils.getIntSharedPreference(getmActivity(), Constants.PREF_PROGRAM_ROW_NO + BluetoothLeService.mBluetoothDeviceAddress);
            Utils.setIntSharedPreference(getmActivity(), Constants.PREF_PROGRAM_ROW_NO_OLD + BluetoothLeService.mBluetoothDeviceAddress, oldRowNum);
            int oldRowPos = Utils.getIntSharedPreference(getmActivity(), Constants.PREF_PROGRAM_ROW_START_POS + BluetoothLeService.mBluetoothDeviceAddress);
            Utils.setIntSharedPreference(getmActivity(), Constants.PREF_PROGRAM_ROW_START_POS_OLD + BluetoothLeService.mBluetoothDeviceAddress, oldRowPos);
            Utils.setIntSharedPreference(getmActivity(), Constants.PREF_PROGRAM_ROW_START_POS + BluetoothLeService.mBluetoothDeviceAddress, startPosition);
            //  正在升级
            if (listener != null) {
                listener.onProcessing();
            }
        } else {
            //Send ProgramData command
            Log.e("升级控制3", "发送数据 ");
            long crc32 = CheckSumUtils.crc32(dataRow.mData, dataRow.mData.length);
            byte[] baCrc32 = ConvertUtils.intToByteArray((int) crc32);
            mOtaFirmwareWrite.OTAProgramDataCmd(mCheckSumType, dataRow.mAddress, baCrc32, payload);
            postTimeout();
            Utils.setStringSharedPreference(getmActivity(), Constants.PREF_BOOTLOADER_STATE + BluetoothLeService.mBluetoothDeviceAddress, "" + BootLoaderCommands_v1.PROGRAM_DATA);
            int oldRowNum = Utils.getIntSharedPreference(getmActivity(), Constants.PREF_PROGRAM_ROW_NO + BluetoothLeService.mBluetoothDeviceAddress);
            Utils.setIntSharedPreference(getmActivity(), Constants.PREF_PROGRAM_ROW_NO_OLD + BluetoothLeService.mBluetoothDeviceAddress, oldRowNum);
            int oldRowPos = Utils.getIntSharedPreference(getmActivity(), Constants.PREF_PROGRAM_ROW_START_POS + BluetoothLeService.mBluetoothDeviceAddress);
            Utils.setIntSharedPreference(getmActivity(), Constants.PREF_PROGRAM_ROW_START_POS_OLD + BluetoothLeService.mBluetoothDeviceAddress, oldRowPos);
            Utils.setIntSharedPreference(getmActivity(), Constants.PREF_PROGRAM_ROW_START_POS + BluetoothLeService.mBluetoothDeviceAddress, 0);   //  正在升级
            if (listener != null) {
                listener.onProcessing();
            }
        }
    }

    private void writeEiv(int rowNum) {
        OTAFlashRowModel_v1.EIV eivRow = (OTAFlashRowModel_v1.EIV) mFileContents.get(CustomFileReader_v1.KEY_DATA).get(rowNum);
        //Send SetEiv command
        mOtaFirmwareWrite.OTASetEivCmd(mCheckSumType, eivRow.mEiv);
        postTimeout();
        Utils.setStringSharedPreference(getmActivity(), Constants.PREF_BOOTLOADER_STATE + BluetoothLeService.mBluetoothDeviceAddress, "" + BootLoaderCommands_v1.SET_EIV);

        int oldRowNum = Utils.getIntSharedPreference(getmActivity(), Constants.PREF_PROGRAM_ROW_NO + BluetoothLeService.mBluetoothDeviceAddress);
        Utils.setIntSharedPreference(getmActivity(), Constants.PREF_PROGRAM_ROW_NO_OLD + BluetoothLeService.mBluetoothDeviceAddress, oldRowNum);
        int oldRowPos = Utils.getIntSharedPreference(getmActivity(), Constants.PREF_PROGRAM_ROW_START_POS + BluetoothLeService.mBluetoothDeviceAddress);
        Utils.setIntSharedPreference(getmActivity(), Constants.PREF_PROGRAM_ROW_START_POS_OLD + BluetoothLeService.mBluetoothDeviceAddress, oldRowPos);
        Utils.setIntSharedPreference(getmActivity(), Constants.PREF_PROGRAM_ROW_START_POS + BluetoothLeService.mBluetoothDeviceAddress, 0);
        if (listener != null) {
            listener.otaSetEiv();
        }
    }


    public void postTimeout() {
        handler.postDelayed(timeOutRunnable, 2 * 1000);
    }

    private Runnable timeOutRunnable = new Runnable() {
        @Override
        public void run() {
            showErrorDialogMessage(BluetoothLeService.ERROR_TAG_RESPONSE_TIMEOUT, "响应数据超时", false);
        }
    };
}
