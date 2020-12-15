package com.kaadas.lock.publiclibrary.xm;

import android.content.Context;

import com.kaadas.lock.R;

public class XMP2PConnectJsonError {

    /**
     * 获取JSON节点失败，json key错误
     */
    public static final int XM_JSON_ERROR_JSON_KEY = 1;

    /**
     * JSON节点值超过范围
     */
    public static final int XM_JSON_ERROR_JSON_LIMIT = 2;

    /**
     * 用户名无效
     */
    public static final int XM_JSON_ERROR_INVALID_USER = 3;

    /**
     * 获取参数失败(获取到的参数值无效)
     */
    public static final int XM_JSON_ERROR_FAILED_PARAMETER = 4;

    /**
     * 文件打开失败
     */
    public static final int XM_JSON_ERROR_FILE_OPEN = 5;

    /**
     * 认证失败
     */
    public static final int XM_JSON_ERROR_AUTHENTICATION_FAILED = 6;

    /**
     * 对讲通道已占用
     */
    public static final int XM_JSON_ERROR_TALK_OCCUPIED = 7;

    /**
     * 当前会话未打开打对讲
     */
    public static final int XM_JSON_ERROR_NOT_TALK = 8;

    /**
     * 未找到录像文件
     */
    public static final int XM_JSON_ERROR_NOT_VIDEO = 9;

    /**
     * 删除录像文件失败
     */
    public static final int XM_JSON_ERROR_FAILED_DELETE = 10;

    /**
     * 回放通道已占用
     */
    public static final int XM_JSON_ERROR_PLAYBACK_OCCUPIED = 11;

    /**
     * 回放失败,文件不存在
     */
    public static final int XM_JSON_ERROR_PLAYBACK_NOT_FILE = 12;

    /**
     * 回放控制无效(没有文件处于回放状态)
     */
    public static final int XM_JSON_ERROR_INVALID_PLAYBACK = 13;

    /**
     * 回放控制TOKEN无效
     */
    public static final int XM_JSON_ERROR_INVALID_PLAYBACK_TOKEN= 14;

    /**
     * 摄像机麦克风未打开
     */
    public static final int XM_JSON_ERROR_OFF_CAMERA_MICROPHONE = 15;

    /**
     * 摄像机喇叭未打开
     */
    public static final int XM_JSON_ERROR_OFF_CAMERA_HORN = 16;

    /**
     * 密码超过限制(5~12)
     */
    public static final int XM_JSON_ERROR_PASSWORD_LIMIT = 17;

    /**
     * 无SD卡或SD卡损坏
     */
    public static final int XM_JSON_ERROR_NO_SD_CARD = 18;

    /**
     * 强制停止录像(SD卡损坏或正在升级)
     */
    public static final int XM_JSON_ERROR_FORCED_STOP_RECORDING = 19;

    /**
     * 未检测到录像索引,需要先格式化卡
     */
    public static final int XM_JSON_ERROR_VIDEO_INDEX_NOT_DETECTED = 20;

    /**
     * 格式化SD卡失败
     */
    public static final int XM_JSON_ERROR_FAILED_FORMAT = 21;

    /**
     * 挂载SD卡失败
     */
    public static final int XM_JSON_ERROR_FAILED_MOUNT = 22;

    /**
     * 校时时间设置失败
     */
    public static final int XM_JSON_ERROR_TIME_SETTING_FAILED = 23;

    /**
     * 校时时区设置失败
     */
    public static final int XM_JSON_ERROR_ZONE_SETTING_FAILED = 24;

    /**
     * 摄像机忙(未完成上一次抓图或抓拍录像)
     */
    public static final int XM_JSON_ERROR_CAMERA_BUSY = 25;

    /**
     * 参数检测失败
     */
    public static final int XM_JSON_ERROR_PARAMETER_DETECTION_FAILED = 26;

    /**
     * 文件夹检测失败
     */
    public static final int XM_JSON_ERROR_FOLDER_DETECTION_FAILED = 27;

    /**
     * 外部电源未接入
     */
    public static final int XM_JSON_ERROR_EXTERNAL_POWER_NOT_CONNECTED = 28;

    /**
     * 网关已锁定
     */
    public static final int XM_JSON_ERROR_GATEWAY_LOCKED = 100;

    /**
     * 摄像机已锁定
     */
    public static final int XM_JSON_ERROR_CAMERA_LOCKED = 101;

    /**
     * SDK协议错误
     */
    public static final int XM_JSON_ERROR_SDK_PROTOCOL_ERROR = 110;

    /**
     * 访问密码错误
     */
    public static final int XM_JSON_ERROR_ACCESS_PASSWORD_ERROR = 111;

    /**
     * 通道错误
     */
    public static final int XM_JSON_ERROR_CHANNEL_ERROR = 112;

    /**
     * 数据长度错误
     */
    public static final int XM_JSON_ERROR_DATA_LENGTH_ERROR = 113;

    /**
     * 通道会话占满
     */
    public static final int XM_JSON_ERROR_CHANNEL_SESSION_FULL = 114;

    /**
     *  摄像机关闭，不产生录像等信息
     */
    public static final int XM_JSON_ERROR_CAMERA_OFF = 115;

    /**
     *  某些模块未初始化好 (T21)
     */
    public static final int XM_JSON_ERROR_T21_INITIALIZED = 116;

    /**
     * 负载数据太长
     */
    public static final int XM_JSON_ERROR_DATA_TOO_LONG = 117;

    /**
     * 设备正在升级
     */
    public static final int XM_JSON_ERROR_DEVICE_UPGRADED = 118;

    /**
     * 用户名不正确
     */
    public static final int XM_JSON_ERROR_INCORRECT_USER_NAME = 119;

    /**
     * JSON错误(分配空间等失败)
     */
    public static final int XM_JSON_ERROR_FAILED_ALLOCATE_SPACE = 1000;

    /**
     * SDK库接口调用失败
     */
    public static final int XM_JSON_ERROR_SDK_LIBRARY_INTERFACE_CALL_FAILED = 1001;

    /**
     * 信令通道未建立
     */
    public static final int XM_JSON_ERROR_SIGNALING_CHANNEL_NOT_ESTABLISHED = 1002;

    /**
     * 配置命令发送失败
     */
    public static final int XM_JSON_ERROR_COMMAND_SEND_FAILED = 1003;

    /**
     * 摄像机唤醒超时或失败
     */
    public static final int XM_JSON_ERROR_SCAMERA_WAKEUP_TIMEOUT_FAILURE = 1004;

    /**
     * 配置摄像机参数超时
     */
    public static final int XM_JSON_ERROR_CAMERA_TIMEOUT_PARAMETER_CONFIGURATION = 1005;

    /**
     * 配置摄像机参数失败
     */
    public static final int XM_JSON_ERROR_FAILED_CONFIGURE_CAMERA_PARAMETERS = 1006;

    /**
     * 指定通道无摄像机
     */
    public static final int XM_JSON_ERROR_NO_CAMERA_FOR_SPECIFIED_CHANNEL = 1007;

    /**
     * 指定通道摄像机不在线
     */
    public static final int XM_JSON_ERROR_NO_ONLINE_CAMERA_FOR_SPECIFIED_CHANNEL = 1008;

    /**
     * 线程创建失败
     */
    public static final int XM_JSON_ERROR_THREAD_CREATION_FAILED = 1009;

    /**
     * 当前已处于升级状态
     */
    public static final int XM_JSON_ERROR_CURRENTLY_UPGRADE_STATE = 1010;

    /**
     * 通道输入有误
     */
    public static final int XM_JSON_ERROR_CHANNEL_INPUT_ERROR = 2000;

    /**
     * 获取token失败
     */
    public static final int XM_JSON_ERROR_FAILED_GET_TOKEN = 5100;

    /**
     * 播放key校验失败
     */
    public static final int XM_JSON_ERROR_PLAY_KEY_VERIFICATION = 5101;

    /**
     * 不支持该功能
     */
    public static final int XM_JSON_ERROR_NOT_SUPPORTED = 9999;


    public static String checkP2PJSONErrorStringWithCode(Context context, int paramInt) {
        String errorString = "";
        switch (paramInt){
            case XM_JSON_ERROR_JSON_KEY://1
                errorString = context.getString(R.string.xm_device_error_reason) + context.getString(R.string.xm_json_error_json_key) + "";
                break;
            case XM_JSON_ERROR_JSON_LIMIT://2
                errorString = context.getString(R.string.xm_device_error_reason) + context.getString(R.string.xm_json_error_json_limit) + "";
                break;
            case XM_JSON_ERROR_INVALID_USER://3
                errorString = context.getString(R.string.xm_device_error_reason) + context.getString(R.string.xm_json_error_invalid_user) + "";
                break;
            case XM_JSON_ERROR_FAILED_PARAMETER://4
                errorString = context.getString(R.string.xm_device_error_reason) + context.getString(R.string.xm_json_error_failed_parameter) + "";
                break;
            case XM_JSON_ERROR_FILE_OPEN://5
                errorString = context.getString(R.string.xm_device_error_reason) + context.getString(R.string.xm_json_error_file_open) + "";
                break;
            case XM_JSON_ERROR_AUTHENTICATION_FAILED://6
                errorString = context.getString(R.string.xm_device_error_reason) + context.getString(R.string.xm_json_error_authentication_failed) + "";
                break;
            case XM_JSON_ERROR_TALK_OCCUPIED://7
                errorString = context.getString(R.string.xm_device_error_reason) + context.getString(R.string.xm_json_error_talk_occupied) + "";
                break;
            case XM_JSON_ERROR_NOT_TALK://8
                errorString = context.getString(R.string.xm_device_error_reason) + context.getString(R.string.xm_json_error_not_talk) + "";
                break;
            case XM_JSON_ERROR_NOT_VIDEO://9
                errorString = context.getString(R.string.xm_device_error_reason) + context.getString(R.string.xm_json_error_not_video) + "";
                break;
            case XM_JSON_ERROR_FAILED_DELETE://10
                errorString = context.getString(R.string.xm_device_error_reason) + context.getString(R.string.xm_json_error_failed_delete) + "";
                break;
            case XM_JSON_ERROR_PLAYBACK_OCCUPIED://11
                errorString = context.getString(R.string.xm_device_error_reason) + context.getString(R.string.xm_json_error_playback_occupied) + "";
                break;
            case XM_JSON_ERROR_PLAYBACK_NOT_FILE://12
                errorString = context.getString(R.string.xm_device_error_reason) + context.getString(R.string.xm_json_error_playback_not_file) + "";
                break;
            case XM_JSON_ERROR_INVALID_PLAYBACK://13
                errorString = context.getString(R.string.xm_device_error_reason) + context.getString(R.string.xm_json_error_invalid_playback) + "";
                break;
            case XM_JSON_ERROR_INVALID_PLAYBACK_TOKEN://14
                errorString = context.getString(R.string.xm_device_error_reason) + context.getString(R.string.xm_json_error_invalid_playback_token) + "";
                break;
            case XM_JSON_ERROR_OFF_CAMERA_MICROPHONE://15
                errorString = context.getString(R.string.xm_device_error_reason) + context.getString(R.string.xm_json_error_off_camera_microphone) + "";
                break;
            case XM_JSON_ERROR_OFF_CAMERA_HORN://16
                errorString = context.getString(R.string.xm_device_error_reason) + context.getString(R.string.xm_json_error_off_camera_horn) + "";
                break;
            case XM_JSON_ERROR_PASSWORD_LIMIT://17
                errorString = context.getString(R.string.xm_device_error_reason) + context.getString(R.string.xm_json_error_password_limit) + "";
                break;
            case XM_JSON_ERROR_NO_SD_CARD://18
                errorString = context.getString(R.string.xm_device_error_reason) + context.getString(R.string.xm_json_error_no_sd_card) + "";
                break;
            case XM_JSON_ERROR_FORCED_STOP_RECORDING://19
                errorString = context.getString(R.string.xm_device_error_reason) + context.getString(R.string.xm_json_error_forced_stop_recording) + "";
                break;
            case XM_JSON_ERROR_VIDEO_INDEX_NOT_DETECTED://20
                errorString = context.getString(R.string.xm_device_error_reason) + context.getString(R.string.xm_json_error_video_index_not_detected) + "";
                break;
            case XM_JSON_ERROR_FAILED_FORMAT://21
                errorString = context.getString(R.string.xm_device_error_reason) + context.getString(R.string.xm_json_error_failed_format) + "";
                break;
            case XM_JSON_ERROR_FAILED_MOUNT://22
                errorString = context.getString(R.string.xm_device_error_reason) + context.getString(R.string.xm_json_error_failed_mount) + "";
                break;
            case XM_JSON_ERROR_TIME_SETTING_FAILED://23
                errorString = context.getString(R.string.xm_device_error_reason) + context.getString(R.string.xm_json_error_time_setting_failed) + "";
                break;
            case XM_JSON_ERROR_ZONE_SETTING_FAILED://24
                errorString = context.getString(R.string.xm_device_error_reason) + context.getString(R.string.xm_json_error_zone_setting_failed) + "";
                break;
            case XM_JSON_ERROR_CAMERA_BUSY://25
                errorString = context.getString(R.string.xm_device_error_reason) + context.getString(R.string.xm_json_error_camera_busy) + "";
                break;
            case XM_JSON_ERROR_PARAMETER_DETECTION_FAILED://26
                errorString = context.getString(R.string.xm_device_error_reason) + context.getString(R.string.xm_json_error_parameter_detection_failed) + "";
                break;
            case XM_JSON_ERROR_FOLDER_DETECTION_FAILED://27
                errorString = context.getString(R.string.xm_device_error_reason) + context.getString(R.string.xm_json_error_folder_detection_failed) + "";
                break;
            case XM_JSON_ERROR_EXTERNAL_POWER_NOT_CONNECTED://28
                errorString = context.getString(R.string.xm_device_error_reason) + context.getString(R.string.xm_json_error_external_power_not_connected) + "";
                break;
            case XM_JSON_ERROR_GATEWAY_LOCKED://100
                errorString = context.getString(R.string.xm_device_error_reason) + context.getString(R.string.xm_json_error_gateway_locked) + "";
                break;
            case XM_JSON_ERROR_CAMERA_LOCKED://101
                errorString = context.getString(R.string.xm_device_error_reason) + context.getString(R.string.xm_json_error_camera_locked) + "";
                break;
            case XM_JSON_ERROR_SDK_PROTOCOL_ERROR://110
                errorString = context.getString(R.string.xm_device_error_reason) + context.getString(R.string.xm_json_error_sdk_protocol_error) + "";
                break;
            case XM_JSON_ERROR_ACCESS_PASSWORD_ERROR://111
                errorString = context.getString(R.string.xm_device_error_reason) + context.getString(R.string.xm_json_error_access_password_error) + "";
                break;
            case XM_JSON_ERROR_CHANNEL_ERROR://112
                errorString = context.getString(R.string.xm_device_error_reason) + context.getString(R.string.xm_json_error_channel_error) + "";
                break;
            case XM_JSON_ERROR_DATA_LENGTH_ERROR://113
                errorString = context.getString(R.string.xm_device_error_reason) + context.getString(R.string.xm_json_error_data_length_error) + "";
                break;
            case XM_JSON_ERROR_CHANNEL_SESSION_FULL://114
                errorString = context.getString(R.string.xm_device_error_reason) + context.getString(R.string.xm_json_error_channel_session_full) + "";
                break;
            case XM_JSON_ERROR_CAMERA_OFF://115
                errorString = context.getString(R.string.xm_device_error_reason) + context.getString(R.string.xm_json_error_camera_off) + "";
                break;
            case XM_JSON_ERROR_T21_INITIALIZED://116
                errorString = context.getString(R.string.xm_device_error_reason) + context.getString(R.string.xm_json_error_t21_initialized) + "";
                break;
            case XM_JSON_ERROR_DATA_TOO_LONG://117
                errorString = context.getString(R.string.xm_device_error_reason) + context.getString(R.string.xm_json_error_data_too_long) + "";
                break;
            case XM_JSON_ERROR_DEVICE_UPGRADED://118
                errorString = context.getString(R.string.xm_device_error_reason) + context.getString(R.string.xm_json_error_device_upgraded) + "";
                break;
            case XM_JSON_ERROR_INCORRECT_USER_NAME://119
                errorString = context.getString(R.string.xm_device_error_reason) + context.getString(R.string.xm_json_error_incorrect_user_name) + "";
                break;
            case XM_JSON_ERROR_FAILED_ALLOCATE_SPACE://1000
                errorString = context.getString(R.string.xm_device_error_reason) + context.getString(R.string.xm_json_error_failed_allocate_space) + "";
                break;
            case XM_JSON_ERROR_SDK_LIBRARY_INTERFACE_CALL_FAILED://1001
                errorString = context.getString(R.string.xm_device_error_reason) + context.getString(R.string.xm_json_error_sdk_library_interface_call_failed) + "";
                break;
            case XM_JSON_ERROR_SIGNALING_CHANNEL_NOT_ESTABLISHED://1002
                errorString = context.getString(R.string.xm_device_error_reason) + context.getString(R.string.xm_json_error_signaling_channel_not_established) + "";
                break;
            case XM_JSON_ERROR_COMMAND_SEND_FAILED://1003
                errorString = context.getString(R.string.xm_device_error_reason) + context.getString(R.string.xm_json_error_command_send_failed) + "";
                break;
            case XM_JSON_ERROR_SCAMERA_WAKEUP_TIMEOUT_FAILURE://1004
                errorString = context.getString(R.string.xm_device_error_reason) + context.getString(R.string.xm_json_error_scamera_wakeup_timeout_failure) + "";
                break;
            case XM_JSON_ERROR_CAMERA_TIMEOUT_PARAMETER_CONFIGURATION://1005
                errorString = context.getString(R.string.xm_device_error_reason) + context.getString(R.string.xm_json_error_camera_timeout_parameter_configuration) + "";
                break;
            case XM_JSON_ERROR_FAILED_CONFIGURE_CAMERA_PARAMETERS://1006
                errorString = context.getString(R.string.xm_device_error_reason) + context.getString(R.string.xm_json_error_failed_configure_camera_parameters) + "";
                break;
            case XM_JSON_ERROR_NO_CAMERA_FOR_SPECIFIED_CHANNEL://1007
                errorString = context.getString(R.string.xm_device_error_reason) + context.getString(R.string.xm_json_error_no_camera_for_specified_channel) + "";
                break;
            case XM_JSON_ERROR_NO_ONLINE_CAMERA_FOR_SPECIFIED_CHANNEL://1008
                errorString = context.getString(R.string.xm_device_error_reason) + context.getString(R.string.xm_json_error_no_online_camera_for_specified_channel) + "";
                break;
            case XM_JSON_ERROR_THREAD_CREATION_FAILED://1009
                errorString = context.getString(R.string.xm_device_error_reason) + context.getString(R.string.xm_json_error_thread_creation_failed) + "";
                break;
            case XM_JSON_ERROR_CURRENTLY_UPGRADE_STATE://1010
                errorString = context.getString(R.string.xm_device_error_reason) + context.getString(R.string.xm_json_error_currently_upgrade_state) + "";
                break;
            case XM_JSON_ERROR_CHANNEL_INPUT_ERROR://2000
                errorString = context.getString(R.string.xm_device_error_reason) + context.getString(R.string.xm_json_error_channel_input_error) + "";
                break;
            case XM_JSON_ERROR_FAILED_GET_TOKEN://5100
                errorString = context.getString(R.string.xm_device_error_reason) + context.getString(R.string.xm_json_error_failed_get_token) + "";
                break;
            case XM_JSON_ERROR_PLAY_KEY_VERIFICATION://5101
                errorString = context.getString(R.string.xm_device_error_reason) + context.getString(R.string.xm_json_error_play_key_verification) + "";
                break;
            case XM_JSON_ERROR_NOT_SUPPORTED://9999
                errorString = context.getString(R.string.xm_device_error_reason) + context.getString(R.string.xm_json_error_not_supported) + "";
                break;

            default://视频访问错误
                errorString = context.getString(R.string.xm_device_error_reason) + context.getString(R.string.xm_json_error_other_error) + "";
                break;
        }
        return errorString;
    }
}
