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

package com.kaadas.lock.publiclibrary.ota.ble.p6.OTAFirmwareUpdate;

import android.app.Activity;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Context;
import android.util.Log;

import com.kaadas.lock.publiclibrary.ota.ble.p6.BluetoothLeService;
import com.kaadas.lock.publiclibrary.ota.ble.p6.IUpdateStatusListener;

public abstract class OTAFUHandlerBase implements OTAFUHandler {

//    protected final Fragment mFragment;
//    protected final View mView;
//    protected final TextView mProgressText;
//    protected final NotificationHandlerBase mNotificationHandler;

    protected final BluetoothGattCharacteristic mOtaCharacteristic;
    protected final String mFilepath;
    protected final OTAFUHandlerCallback mParent;
    protected boolean mPrepareFileWriteEnabled = true;
    protected int mProgressBarPosition;
    protected byte mActiveApp; //Dual-App Bootloader Active Application ID
    protected long mSecurityKey;
    protected Activity mActivity;
    protected IUpdateStatusListener listener;


    public OTAFUHandlerBase(Activity activity, IUpdateStatusListener listener,
                            BluetoothGattCharacteristic otaCharacteristic,
                            byte activeApp, long securityKey, String filepath, OTAFUHandlerCallback parent) {
        this.mActivity = activity;
        this.mOtaCharacteristic = otaCharacteristic;
        this.mActiveApp = activeApp;
        this.mSecurityKey = securityKey;
        this.mFilepath = filepath;
        this.mParent = parent;
        this.listener = listener;
    }

    @Override
    public void setPrepareFileWriteEnabled(boolean enabled) {
        this.mPrepareFileWriteEnabled = enabled;
    }

    @Override
    public void setProgressBarPosition(int position) {
        this.mProgressBarPosition = position;
    }

    protected Activity getmActivity() {

        return mActivity;
    }


    /**
     * Method to showToast progress bar
     */
    protected void showProgress(int fileStatus, float fileLineNos, float totalLines) {
        Log.e("进度改变  ", "当前进度   " + fileLineNos + "   总数  " + totalLines);
        if (listener != null) {
            listener.onProcessChange((int) fileLineNos, (int) totalLines);
            return;
        }

    }





    protected String storeAndReturnDeviceAddress() {
        return mParent.saveAndReturnDeviceAddress();
    }

    protected void setFileUpgradeStarted(boolean started) {
        mParent.setFileUpgradeStarted(started);
    }

    protected void generatePendingNotification(Context context) {
        mParent.generatePendingNotification(context);
    }
}
