/*
 * Copyright Cypress Semiconductor Corporation, 2014-2018 All rights reserved.
 *
 * This software, associated documentation and materials ("Software") is
 * owned by Cypress Semiconductor Corporation ("Cypress") and is
 * protected by and subject to worldwide patent protection (UnitedStates and foreign),
 * United States copyright laws and international
 * treaty provisions. Therefore, unless otherwise specified in a separate
 * license agreement between you and Cypress, this Software
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

/**
 * Class created for bootloader command constants
 */
class BootLoaderCommands_v1 {
    /* Command identifier for verifying the checksum value of the bootloadable project.
     * 用于验证可引导加载项目的校验和值的ommand标识符*/
    public static final int VERIFY_APP = 0x31;
    /* Command identifier for sending a block of data to the bootloader without doing anything with it yet.
    * 命令标识符，用于在不做任何操作的情况下向引导加载程序发送数据块。*/
    public static final int SEND_DATA = 0x37;
    /* Command identifier for starting the boot loader.  All other commands ignored until this is sent.
    * 启动引导加载程序的命令标识符。在发送此命令之前忽略所有其他命令。*/
    public static final int ENTER_BOOTLOADER = 0x38;
    /* Command identifier for exiting the bootloader and restarting the target program.
    * 用于退出引导加载程序并重新启动目标程序的命令标识符。*/
    public static final int EXIT_BOOTLOADER = 0x3B;
    /* Command identifier for sending a block of data to the bootloader without doing anything with it yet.
    * 命令标识符，用于在不做任何操作的情况下向引导加载程序发送数据块。*/
    public static final int SEND_DATA_WITHOUT_RESPONSE = 0x47;
    /* Command to program data. */
    public static final int PROGRAM_DATA = 0x49;
    /* Command to set application metadata in bootloader SDK
    * 命令在引导加载程序SDK中设置应用程序元数据*/
    public static final int SET_APP_METADATA = 0x4C;
    /* Command to set encryption initial vector
    * 命令设置加密初始向量**/
    public static final int SET_EIV = 0x4D;

    public static final int PACKET_START = 0x01;
    public static final int PACKET_END = 0x17;
    public static final int BASE_CMD_SIZE = 7;//SOP(1) + CmdCode(1) + DataLength(2) + Checksum(2) + EOP(1)
}
