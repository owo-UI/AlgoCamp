package com.algocamp.common;

/**
 * API 统一返回结果包装类。
 * <p>
 * 所有 Controller 接口必须返回 {@code Result<T>}，不允许直接返回 Map 或裸 List。
 * 前端通过 {@code code} 判断成功与否，通过 {@code data} 获取业务数据。
 * </p>
 *
 * <p>成功示例：</p>
 * <pre>{@code
 * Result<List<StepState>> result = Result.success(steps);
 * // JSON: { "code": 200, "message": "操作成功", "data": [...] }
 * }</pre>
 *
 * @param <T> 业务数据的类型
 * @author AlgoCamp
 */
public class Result<T> {

    /**
     * 状态码，200 表示成功，400 表示参数错误，500 表示服务器错误。
     */
    private final int code;

    /**
     * 提示信息，成功或失败时均可携带说明文字。
     */
    private final String message;

    /**
     * 业务数据载荷，失败时通常为 null。
     */
    private final T data;

    /**
     * 私有构造方法，强制通过静态工厂方法创建实例。
     *
     * @param code    状态码
     * @param message 提示信息
     * @param data    业务数据
     */
    private Result(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    /**
     * 获取状态码。
     *
     * @return 状态码数值
     */
    public int getCode() {
        return code;
    }

    /**
     * 获取提示信息。
     *
     * @return 提示信息
     */
    public String getMessage() {
        return message;
    }

    /**
     * 获取业务数据。
     *
     * @return 业务数据，可能为 null
     */
    public T getData() {
        return data;
    }

    /**
     * 判断本次请求是否处理成功。
     *
     * @return 成功返回 true，否则 false
     */
    public boolean isSuccess() {
        return code == ResultCode.SUCCESS.getCode();
    }

    // ==================== 静态工厂方法 ====================

    /**
     * 返回成功结果，携带业务数据。
     *
     * @param data 业务数据
     * @param <T>  数据类型
     * @return 成功结果对象
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(
                ResultCode.SUCCESS.getCode(),
                ResultCode.SUCCESS.getMessage(),
                data
        );
    }

    /**
     * 返回成功结果，不携带业务数据。
     *
     * @param <T> 数据类型
     * @return 成功结果对象（data 为 null）
     */
    public static <T> Result<T> success() {
        return success(null);
    }

    /**
     * 返回成功结果，携带自定义提示信息。
     *
     * @param message 自定义成功提示
     * @param data    业务数据
     * @param <T>     数据类型
     * @return 成功结果对象
     */
    public static <T> Result<T> success(String message, T data) {
        return new Result<>(ResultCode.SUCCESS.getCode(), message, data);
    }

    /**
     * 返回失败结果，使用默认的「请求参数错误」状态码。
     *
     * @param message 失败原因说明
     * @param <T>     数据类型
     * @return 失败结果对象（data 为 null）
     */
    public static <T> Result<T> fail(String message) {
        return new Result<>(
                ResultCode.BAD_REQUEST.getCode(),
                message,
                null
        );
    }

    /**
     * 返回失败结果，指定状态码枚举和自定义提示信息。
     *
     * @param resultCode 状态码枚举
     * @param message    失败原因说明
     * @param <T>        数据类型
     * @return 失败结果对象（data 为 null）
     */
    public static <T> Result<T> fail(ResultCode resultCode, String message) {
        return new Result<>(resultCode.getCode(), message, null);
    }
}