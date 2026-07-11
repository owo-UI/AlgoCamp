//响应状态码枚举
package com.algocamp.common;

/**
 * API 统一响应状态码枚举。
 * <p>
 * 用于 {@link Result} 中标识请求处理结果，避免在代码中使用 200、400 等魔法数字。
 * </p>
 *
 * @author AlgoCamp
 */
public enum ResultCode {

    /** 请求处理成功 */
    SUCCESS(200, "操作成功"),

    /** 客户端请求参数错误（如缺少必填字段、节点不存在等） */
    BAD_REQUEST(400, "请求参数错误"),

    /** 服务器内部错误（未预期的异常） */
    INTERNAL_ERROR(500, "服务器内部错误");

    /**
     * HTTP 风格的状态码。
     */
    private final int code;

    /**
     * 默认提示信息。
     */
    private final String message;

    /**
     * 构造状态码枚举项。
     *
     * @param code    状态码数值
     * @param message 默认提示信息
     */
    ResultCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * 获取状态码数值。
     *
     * @return 状态码
     */
    public int getCode() {
        return code;
    }

    /**
     * 获取默认提示信息。
     *
     * @return 提示信息
     */
    public String getMessage() {
        return message;
    }
}