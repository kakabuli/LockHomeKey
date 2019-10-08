package com.yun.software.kaadas.Utils;

public class OrderStatue {

    /**
     * 订单状态
     */
    public static final String INDENT_STATUS = "indent_status";
    /**
     * 订单状态：待支付
     */
    public static final String INDENT_STATUS_WAIT_PAY = "indent_status_wait_pay";
    /**
     * 订单状态：待安装
     */
    public static final String INDENT_STATUS_WAIT_INSTALL = "indent_status_wait_install";
    /**
     * 订单状态：待评价
     */
    public static final String INDENT_STATUS_WAIT_COMMENT = "indent_status_wait_comment";
    ///** 订单状态：售后/退款 */
    /**
     * 订单状态：待退款
     */
    public static final String INDENT_STATUS_WAIT_REFUND = "indent_status_wait_refund";
    /**
     * 订单状态：已退款
     */
    public static final String INDENT_STATUS_REFUNDED = "indent_status_refunded";
    /**
     * 订单状态：已评价
     */
    public static final String INDENT_STATUS_COMPLETED = "indent_status_completed";

    /**
     * 订单状态：已取消
     */
    public static final String INDENT_STATUS_CANCELDE = "indent_status_canceled";
    /**
     * 订单状态：拼团中
     */
    public static final String INDENT_STATUS_IN_GROUP = "indent_status_in_group";

    /**
     * 拼团成功
     */
    public static final String GROUP_TYPE_COMPLETED = "group_type_completed";

    /**
     * 拼团结束
     */
    public static final String GROUP_TYPE_OVER = "group_type_over";
    /**
     * 订单状态：众筹中
     */
    public static final String INDENT_STATUS_IN_CROWD = "indent_status_in_crowd";
    /** 订单状态：正则 */
    public static final String INDENT_STATUS_REG_EX = "^(" + INDENT_STATUS_WAIT_PAY + ")|(" + INDENT_STATUS_WAIT_INSTALL
            + ")|(" + INDENT_STATUS_WAIT_COMMENT + ")|(" + INDENT_STATUS_WAIT_REFUND + ")|(" + INDENT_STATUS_REFUNDED
            + ")|(" + INDENT_STATUS_COMPLETED + ")|(" + INDENT_STATUS_CANCELDE + ")|(" + INDENT_STATUS_IN_GROUP + ")|("
            + INDENT_STATUS_IN_CROWD + ")|()$";
    public static final String INDENT_STATUS_ERR_MSG = "参数有误：订单状态有误";



    /**
     * 订单类型
     */
    public static final String INDENT_TYPE = "indent_type";
    /**
     * 订单类型：普通订单
     */
    public static final String INDENT_TYPE_BASE = "indent_type_base";
    /**
     * 订单类型：秒杀订单
     */
    public static final String INDENT_TYPE_SECKILL = "product_type_seckill";
    /**
     * 订单类型：众筹订单
     */
    public static final String INDENT_TYPE_CROWD = "product_type_crowd";
    /**
     * 订单类型：砍价订单
     */
    public static final String INDENT_TYPE_BARGAIN = "product_type_bargain";
    /**
     * 订单类型：团购订单
     */
    public static final String INDENT_TYPE_GROUP = "product_type_group";



    /**
     * 订单项状态
     */
    public static final String INDENT_INFO_STATUS = "indent_info_status";

    /**
     * 售后处理中
     */
    public static final String INDENT_INFO_STATUS_AFTER_UNDERWAY = "indent_info_status_after_underway";

    /**
     * 售后完成
     */
    public static final String INDENT_INFO_STATUS_AFTER_COMPLETE = "indent_info_status_after_complete";

    /**
     * 退款中
     */
    public static final String INDENT_INFO_STATUS_REFUND_OF = "indent_info_status_refund_of";
    /**
     * 退款完成
     */
    public static final String INDENT_INFO_STATUS_REFUND_TO_COMPLETE = "indent_info_status_refund_to_complete";

}
