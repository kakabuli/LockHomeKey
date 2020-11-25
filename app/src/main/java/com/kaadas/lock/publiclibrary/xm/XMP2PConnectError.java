package com.kaadas.lock.publiclibrary.xm;

import android.content.Context;

import com.kaadas.lock.R;

public class XMP2PConnectError {

    /**
     * 动态库未初始化
     */
    public static final int XM_DYNAMIC_LIBRARY_NOT_INITIALIZED = -1;
    /**
     * 重复初始化
     */
    public static final int XM_REPEATED_INITIALIZATION = -2;
    /**
     * 连接超时
     */
    public static final int XM_CONNECTION_TIMED_OUT = -3;
    /**
     * 无效DID
     */
    public static final int XM_INVALID_DID = -4;
    /**
     * 无效参数
     */
    public static final int XM_INVALID_PARAMETER = -5;
    /**
     * 设备不在线
     */
    public static final int XM_DEVICE_NOT_ONLINE = -6;
    /**
     * 无法解析名称
     */
    public static final int XM_UNABLE_TO_RESOLVE_NAME = -7;
    /**
     * 无效DID前缀
     */
    public static final int XM_INVALID_DID_PREFIX = -8;
    /**
     * ID过期
     */
    public static final int XM_ID_EXPIRED = -9;
    /**
     * 没有转发服务器可用
     */
    public static final int XM_NO_FORWARDING_SERVER_AVAILABLE = -10;
    /**
     * 无效session句柄
     */
    public static final int XM_INVALID_SESSION_HANDLE = -11;
    /**
     * 客户端主动断开当前设备P2P session
     */
    public static final int XM_CLIENT_DISCONNECTS_DEIVCE_P2P = -12;
    /**
     * 设备主动断开当前客户端P2P session
     */
    public static final int XM_DEVICE_DISCONNECTS_CLIENT_P2P = -13;
    /**
     * 当前P2P session会话已关闭
     */
    public static final int XM_CURRENT_P2P_SESSION_CLOSED = -14;
    /**
     * 远程站点缓冲区已满
     */
    public static final int XM_THE_REMOTE_BUFFER_IS_FULL = -15;
    /**
     * 用户监听中断
     */
    public static final int XM_USER_MONITORING_INTERRUPT = -16;
    /**
     * 超出session最大连接
     */
    public static final int XM_SESSION_MAXIMUM_CONNECTION_EXCEEDED = -17;
    /**
     * UDP端口绑定失败
     */
    public static final int XM_UDP_PORT_BINDING_FAILED = -18;
    /**
     * 用户连接中退出
     */
    public static final int XM_EXIT_FROM_USER_CONNECTION = -19;
    /**
     * 会话关闭的内存不足
     */
    public static final int XM_INSUFFICIENT_MEMORY_FOR_SESSION_CLOSURE = -20;
    /**
     * 无效的API许可
     */
    public static final int XM_INVALID_API_LICENSE = -21;

    public static String checkP2PErrorStringWithCode(Context context,int paramInt) {
        String errorString = "";
        switch (paramInt){
            case XMP2PConnectError.XM_DYNAMIC_LIBRARY_NOT_INITIALIZED://-1
                errorString = context.getString(R.string.xm_device_error_reason) + context.getString(R.string.xm_dynamic_library_not_initialized) + "";
                break;
            case XMP2PConnectError.XM_REPEATED_INITIALIZATION://-2
                errorString = context.getString(R.string.xm_device_error_reason) + context.getString(R.string.xm_repeated_initialization) + "";
                break;
            case XMP2PConnectError.XM_CONNECTION_TIMED_OUT://-3
//                errorString = getString(R.string.xm_connection_timed_out) + "";
                errorString = context.getString(R.string.video_lock_xm_connect_time_out) + "";
                break;
            case XMP2PConnectError.XM_INVALID_DID://-4
                errorString = context.getString(R.string.xm_device_error_reason) + context.getString(R.string.xm_invalid_did) + "";
                break;
            case XMP2PConnectError.XM_INVALID_PARAMETER://-5
                errorString = context.getString(R.string.xm_device_error_reason) + context.getString(R.string.xm_invalid_parameter) + "";
                break;
            case XMP2PConnectError.XM_DEVICE_NOT_ONLINE://-6
                errorString = context.getString(R.string.xm_device_error_reason) + context.getString(R.string.xm_device_not_online) + "";
                break;
            case XMP2PConnectError.XM_UNABLE_TO_RESOLVE_NAME://-7
                errorString = context.getString(R.string.xm_device_error_reason) + context.getString(R.string.xm_unable_to_resolve_name) + "";
                break;
            case XMP2PConnectError.XM_INVALID_DID_PREFIX://-8
                errorString = context.getString(R.string.xm_device_error_reason) + context.getString(R.string.xm_invalid_did_prefix) + "";
                break;
            case XMP2PConnectError.XM_ID_EXPIRED://-9
                errorString = context.getString(R.string.xm_device_error_reason) + context.getString(R.string.xm_id_expired) + "";
                break;
            case XMP2PConnectError.XM_NO_FORWARDING_SERVER_AVAILABLE://-10
                errorString = context.getString(R.string.xm_device_error_reason) + context.getString(R.string.xm_no_forwarding_server_available) + "";
                break;
            case XMP2PConnectError.XM_INVALID_SESSION_HANDLE://-11
                errorString = context.getString(R.string.xm_device_error_reason) + context.getString(R.string.xm_invalid_session_handle) + "";
                break;
            case XMP2PConnectError.XM_CLIENT_DISCONNECTS_DEIVCE_P2P://-12
                errorString = context.getString(R.string.xm_device_error_reason) + context.getString(R.string.xm_client_disconnects_deivce_p2p) + "";
                break;
            case XMP2PConnectError.XM_DEVICE_DISCONNECTS_CLIENT_P2P://-13
                errorString = context.getString(R.string.xm_device_error_reason) + context.getString(R.string.xm_device_disconnects_client_p2p) + "";
                break;
            case XMP2PConnectError.XM_CURRENT_P2P_SESSION_CLOSED://-14
                errorString = context.getString(R.string.xm_device_error_reason) + context.getString(R.string.xm_current_p2p_session_closed) + "";
                break;
            case XMP2PConnectError.XM_THE_REMOTE_BUFFER_IS_FULL://-15
                errorString = context.getString(R.string.xm_device_error_reason) + context.getString(R.string.xm_the_remote_buffer_is_full) + "";
                break;
            case XMP2PConnectError.XM_USER_MONITORING_INTERRUPT://-16
                errorString = context.getString(R.string.xm_device_error_reason) + context.getString(R.string.xm_user_monitoring_interrupt) + "";
                break;
            case XMP2PConnectError.XM_SESSION_MAXIMUM_CONNECTION_EXCEEDED://-17
                errorString = context.getString(R.string.xm_device_error_reason) + context.getString(R.string.xm_session_maximum_connection_exceeded) + "";
                break;
            case XMP2PConnectError.XM_UDP_PORT_BINDING_FAILED://-18
                errorString = context.getString(R.string.xm_device_error_reason) + context.getString(R.string.xm_udp_port_binding_failed) + "";
                break;
            case XMP2PConnectError.XM_EXIT_FROM_USER_CONNECTION://-19
                errorString = context.getString(R.string.xm_device_error_reason) + context.getString(R.string.xm_exit_from_user_connection) + "";
                break;
            case XMP2PConnectError.XM_INSUFFICIENT_MEMORY_FOR_SESSION_CLOSURE://-20
                errorString = context.getString(R.string.xm_device_error_reason) + context.getString(R.string.xm_insufficient_memory_for_session_closure) + "";
                break;
            case XMP2PConnectError.XM_INVALID_API_LICENSE://-21
                errorString = context.getString(R.string.xm_device_error_reason) + context.getString(R.string.xm_invalid_api_license) + "";
                break;

            default:
                errorString = context.getString(R.string.video_lock_xm_connect_failed) + "";
                break;
        }
        return errorString;
    }
}
