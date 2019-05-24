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

import com.kaadas.lock.R;
import com.kaadas.lock.publiclibrary.ota.p6.BluetoothLeService;
import com.kaadas.lock.publiclibrary.ota.p6.CommonUtils.CheckSumUtils;
import com.kaadas.lock.publiclibrary.ota.p6.CommonUtils.Constants;
import com.kaadas.lock.publiclibrary.ota.p6.CommonUtils.ConvertUtils;
import com.kaadas.lock.publiclibrary.ota.p6.CommonUtils.Utils;
import com.kaadas.lock.publiclibrary.ota.p6.IUpdateStatusListener;

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
                    showErrorDialogMessage(getmActivity().getResources().getString(R.string.ota_alert_invalid_file), true);
                }
            }
        }, 1000);
    }

    private void startOTA() {
        // 升级状态   文件读取成功
        if (listener != null) {
            listener.onFileReadComplete();
        }

        Utils.setStringSharedPreference(getmActivity(), Constants.PREF_BOOTLOADER_STATE, "" + BootLoaderCommands_v1.ENTER_BOOTLOADER);
        setFileUpgradeStarted(true);
        generatePendingNotification(getmActivity());

        OTAFlashRowModel_v1.Header headerRow = (OTAFlashRowModel_v1.Header) mFileContents.get(CustomFileReader_v1.KEY_HEADER).get(0);
        this.mCheckSumType = headerRow.mCheckSumType;
        this.mActiveApp = headerRow.mAppId;

        //Send Enter Bootloader command
        mOtaFirmwareWrite.OTAEnterBootLoaderCmd(mCheckSumType, headerRow.mProductId);
        // 升级状态    执行Enter引导加载程序命令
        if (listener != null) {
            listener.otaEnterBootloader();
        }
    }

    @Override
    public void processOTAStatus(String status, Bundle extras) {
        if (extras.containsKey(Constants.EXTRA_ERROR_OTA)) {
            String errorMessage = extras.getString(Constants.EXTRA_ERROR_OTA);
            Log.e(TAG, "升级错误  " + errorMessage);
            showErrorDialogMessage(getmActivity().getResources().getString(R.string.alert_message_ota_error) + errorMessage, false);
        } else if (status.equalsIgnoreCase("" + BootLoaderCommands_v1.ENTER_BOOTLOADER)) {
            if (extras.containsKey(Constants.EXTRA_SILICON_ID) && extras.containsKey(Constants.EXTRA_SILICON_REV)) {
                OTAFlashRowModel_v1.Header headerRow = (OTAFlashRowModel_v1.Header) mFileContents.get(CustomFileReader_v1.KEY_HEADER).get(0);
                byte[] siliconIdReceived = extras.getByteArray(Constants.EXTRA_SILICON_ID);
                byte siliconRevReceived = extras.getByte(Constants.EXTRA_SILICON_REV);

                if (Arrays.equals(headerRow.mSiliconId, siliconIdReceived) && headerRow.mSiliconRev == siliconRevReceived) {
                    //Send Set Application Metadata command
                    OTAFlashRowModel_v1.AppInfo appInfoRow = (OTAFlashRowModel_v1.AppInfo) mFileContents.get(CustomFileReader_v1.KEY_APPINFO).get(0);
                    mOtaFirmwareWrite.OTASetAppMetadataCmd(mCheckSumType, mActiveApp, appInfoRow.mAppStart, appInfoRow.mAppSize);
                    Utils.setStringSharedPreference(getmActivity(), Constants.PREF_BOOTLOADER_STATE, "" + BootLoaderCommands_v1.SET_APP_METADATA);
                    //升级状态   执行Set应用程序元数据命令…
                    if (listener != null) {
                        listener.otaSetApplicationMetadata();
                    }
                } else {
                    //Wrong SiliconId and SiliconRev
                    showErrorDialogMessage(getmActivity().getResources().getString(R.string.alert_message_silicon_id_error), true);
                    //升级状态   错误:SiliconID或SiliconRev不匹配
                }
            }
        } else if (status.equalsIgnoreCase("" + BootLoaderCommands_v1.SET_APP_METADATA)) {
            List<OTAFlashRowModel_v1> dataRows = mFileContents.get(CustomFileReader_v1.KEY_DATA);
            //获取行号
            int rowNum = Utils.getIntSharedPreference(getmActivity(), Constants.PREF_PROGRAM_ROW_NO+ BluetoothLeService.mBluetoothDeviceAddress);
            OTAFlashRowModel_v1 dataRow = dataRows.get(rowNum);
            if (dataRow instanceof OTAFlashRowModel_v1.EIV) {
                Log.e("OTA升级", "发送数据1 writeEiv   " + status);
                writeEiv(rowNum);//Set EIV
            } else if (dataRow instanceof OTAFlashRowModel_v1.Data) {
                Log.e("OTA升级", "发送数据2 writeData   " + status);
                writeData(rowNum);//Program data row
            }
        } else if (status.equalsIgnoreCase("" + BootLoaderCommands_v1.SET_EIV)) {
            //获取行号
            int rowNum = Utils.getIntSharedPreference(getmActivity(), Constants.PREF_PROGRAM_ROW_NO+ BluetoothLeService.mBluetoothDeviceAddress);
            rowNum++;//Increment row number
            List<OTAFlashRowModel_v1> dataRows = mFileContents.get(CustomFileReader_v1.KEY_DATA);
            //Update progress bar
            int totalLines = dataRows.size();
            showProgress(mProgressBarPosition, rowNum, totalLines);
            if (rowNum < totalLines) {//Process next row
                Utils.setIntSharedPreference(getmActivity(), Constants.PREF_PROGRAM_ROW_NO+ BluetoothLeService.mBluetoothDeviceAddress, rowNum);
                Utils.setIntSharedPreference(getmActivity(), Constants.PREF_PROGRAM_ROW_START_POS+ BluetoothLeService.mBluetoothDeviceAddress, 0);
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
                Utils.setIntSharedPreference(getmActivity(), Constants.PREF_PROGRAM_ROW_NO+ BluetoothLeService.mBluetoothDeviceAddress, 0);
                Utils.setIntSharedPreference(getmActivity(), Constants.PREF_PROGRAM_ROW_START_POS+ BluetoothLeService.mBluetoothDeviceAddress, 0);
                //Programming done, send VerifyApplication command
                mOtaFirmwareWrite.OTAVerifyAppCmd(mCheckSumType, mActiveApp);
                Utils.setStringSharedPreference(getmActivity(), Constants.PREF_BOOTLOADER_STATE, "" + BootLoaderCommands_v1.VERIFY_APP);

                //升级状态  执行验证应用程序命令…
                if (listener != null) {
                    listener.otaVerifyApplication();
                }
            }
        } else if (status.equalsIgnoreCase("" + BootLoaderCommands_v1.SEND_DATA)) {
            int rowNum = Utils.getIntSharedPreference(getmActivity(), Constants.PREF_PROGRAM_ROW_NO+ BluetoothLeService.mBluetoothDeviceAddress);
            Log.e("OTA升级", "发送数据5 writeData   " + status);
            writeData(rowNum);//Program data row
        } else if (status.equalsIgnoreCase("" + BootLoaderCommands_v1.SEND_DATA_WITHOUT_RESPONSE)) {
            int rowNum = Utils.getIntSharedPreference(getmActivity(), Constants.PREF_PROGRAM_ROW_NO+ BluetoothLeService.mBluetoothDeviceAddress);
            Log.e("OTA升级", "发送数据6 writeData   " + status);
            writeData(rowNum);//Program data row
        } else if (status.equalsIgnoreCase("" + BootLoaderCommands_v1.PROGRAM_DATA)) {
            int rowNum = Utils.getIntSharedPreference(getmActivity(), Constants.PREF_PROGRAM_ROW_NO+ BluetoothLeService.mBluetoothDeviceAddress);
            rowNum++;//Increment row number
            List<OTAFlashRowModel_v1> dataRows = mFileContents.get(CustomFileReader_v1.KEY_DATA);
            //Update progress bar
            int totalLines = dataRows.size();
            showProgress(mProgressBarPosition, rowNum, totalLines);
            //进度改变
            if (rowNum < totalLines) {//Process next row
                Utils.setIntSharedPreference(getmActivity(), Constants.PREF_PROGRAM_ROW_NO+ BluetoothLeService.mBluetoothDeviceAddress, rowNum);
                Utils.setIntSharedPreference(getmActivity(), Constants.PREF_PROGRAM_ROW_START_POS+ BluetoothLeService.mBluetoothDeviceAddress, 0);
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
                Utils.setIntSharedPreference(getmActivity(), Constants.PREF_PROGRAM_ROW_NO+ BluetoothLeService.mBluetoothDeviceAddress, 0);
                Utils.setIntSharedPreference(getmActivity(), Constants.PREF_PROGRAM_ROW_START_POS+ BluetoothLeService.mBluetoothDeviceAddress, 0);
                //Programming done, send VerifyApplication command
                mOtaFirmwareWrite.OTAVerifyAppCmd(mCheckSumType, mActiveApp);
                Utils.setStringSharedPreference(getmActivity(), Constants.PREF_BOOTLOADER_STATE, "" + BootLoaderCommands_v1.VERIFY_APP);
                //升级状态 执行验证应用程序命令…
                if (listener != null) {
                    listener.otaVerifyApplication();
                }
            }
        } else if (status.equalsIgnoreCase("" + BootLoaderCommands_v1.VERIFY_APP)) {
            if (extras.containsKey(Constants.EXTRA_VERIFY_APP_STATUS)) {
                byte statusReceived = extras.getByte(Constants.EXTRA_VERIFY_APP_STATUS);
                if (statusReceived == 1) {
                    //Send ExitBootloader command
                    mOtaFirmwareWrite.OTAExitBootloaderCmd(mCheckSumType);
                    Utils.setStringSharedPreference(getmActivity(), Constants.PREF_BOOTLOADER_STATE, "" + BootLoaderCommands_v1.EXIT_BOOTLOADER);
                    //  执行退出BootLoader指令
                    if (listener != null) {
                        listener.otaEndBootloader();
                    }

                } else {
                    showErrorDialogMessage(getmActivity().getResources().getString(R.string.alert_message_verify_application_error), false);
                }
            }
        } else if (status.equalsIgnoreCase("" + BootLoaderCommands_v1.EXIT_BOOTLOADER)) {
            BluetoothDevice device = BluetoothLeService.getRemoteDevice();
            //  升级成功
            if (listener != null) {
                listener.upgradeCompleted();
            }

            setFileUpgradeStarted(false);
            storeAndReturnDeviceAddress();
            BluetoothLeService.disconnect();
            BluetoothLeService.unpairDevice(device);
        }
    }

    private void writeData(int rowNum) {
        //开始的position
        int startPosition = Utils.getIntSharedPreference(getmActivity(), Constants.PREF_PROGRAM_ROW_START_POS+ BluetoothLeService.mBluetoothDeviceAddress);
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
            Utils.setStringSharedPreference(getmActivity(), Constants.PREF_BOOTLOADER_STATE, "" + BootLoaderCommands_v1.SEND_DATA);
            Utils.setIntSharedPreference(getmActivity(), Constants.PREF_PROGRAM_ROW_START_POS+ BluetoothLeService.mBluetoothDeviceAddress, startPosition);
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
            Utils.setStringSharedPreference(getmActivity(), Constants.PREF_BOOTLOADER_STATE, "" + BootLoaderCommands_v1.PROGRAM_DATA);
            Utils.setIntSharedPreference(getmActivity(), Constants.PREF_PROGRAM_ROW_START_POS+ BluetoothLeService.mBluetoothDeviceAddress, 0);   //  正在升级
            if (listener != null) {
                listener.onProcessing();
            }
        }
    }

    private void writeEiv(int rowNum) {
        OTAFlashRowModel_v1.EIV eivRow = (OTAFlashRowModel_v1.EIV) mFileContents.get(CustomFileReader_v1.KEY_DATA).get(rowNum);
        //Send SetEiv command
        mOtaFirmwareWrite.OTASetEivCmd(mCheckSumType, eivRow.mEiv);
        Utils.setStringSharedPreference(getmActivity(), Constants.PREF_BOOTLOADER_STATE, "" + BootLoaderCommands_v1.SET_EIV);
        Utils.setIntSharedPreference(getmActivity(), Constants.PREF_PROGRAM_ROW_START_POS+ BluetoothLeService.mBluetoothDeviceAddress, 0);
        if (listener != null) {
            listener.otaSetEiv();
        }
    }
}
