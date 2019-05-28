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

package com.kaadas.lock.publiclibrary.ota.p6.CommonUtils;

/**
 * Constants used in the project
 */
public class Constants {

    // The value of manifest.package in AndroidManifest.xml
    public static String PACKAGE_NAME;
    public static final String EXTRA_BYTE_VALUE = "com.cypress.cysmart.backgroundservices." + "EXTRA_BYTE_VALUE";
    public static final String EXTRA_CHARACTERISTIC_ERROR_MESSAGE = "com.cypress.cysmart.backgroundservices." + "EXTRA_CHARACTERISTIC_ERROR_MESSAGE";
    /**
     * Descriptor constants
     */
    public static final String EXTRA_SILICON_ID = "com.cypress.cysmart.backgroundservices." + "EXTRA_SILICON_ID";
    public static final String EXTRA_SILICON_REV = "com.cypress.cysmart.backgroundservices." + "EXTRA_SILICON_REV";
    public static final String EXTRA_VERIFY_APP_STATUS = "com.cypress.cysmart.backgroundservices." + "EXTRA_VERIFY_APP_STATUS";
    public static final String EXTRA_ERROR_OTA = "com.cypress.cysmart.backgroundservices." + "EXTRA_ERROR_OTA";

    //CYACD2 constants
    public static final String EXTRA_BTLDR_SDK_VER = "com.cypress.cysmart.backgroundservices." + "EXTRA_BTLDR_SDK_VER";
    /**
     * Shared Preference Status HandShake Status
     */
    public static final String PREF_BOOTLOADER_STATE = "PREF_BOOTLOADER_STATE";
    public static final String PREF_PROGRAM_ROW_NO = "PREF_PROGRAM_ROW_NO";
    public static final String PREF_PROGRAM_ROW_START_POS = "PREF_PROGRAM_ROW_START_POS";
    public static final String PREF_BOOTLOADER_STATE_OLD = "PREF_BOOTLOADER_STATE_OLD";
    public static final String PREF_PROGRAM_ROW_NO_OLD = "PREF_PROGRAM_ROW_NO_OLD";
    public static final String PREF_PROGRAM_ROW_START_POS_OLD = "PREF_PROGRAM_ROW_START_POS_OLD";
    /**
     * OTA File Selection Extras
     */
    public static final byte ACTIVE_APP_NO_CHANGE = -1;
    public static final long NO_SECURITY_KEY = -1;
    /**
     * Shared Preference Status File Status
     */
    public static final String PREF_MTU_NEGOTIATED = "PREF_MTU_NEGOTIATED";
}
