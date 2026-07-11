package com.algocamp.controller.dto;

/**
 * 图边的请求数据传输对象。
 * <p>
 * 对应前端拖拽连线时产生的单条边，包含起点和终点节点 ID。
 * </p>
 *
 * @author AlgoCamp
 */
public class EdgeRequest {

    /**
     * 边的起点节点 ID。
     */
    private String from;

    /**
     * 边的终点节点 ID。
     */
    private String to;

    /**
     * 无参构造方法，供 JSON 反序列化使用。
     */
    public EdgeRequest() {
    }

    /**
     * 全参构造方法，便于测试时手动构建。
     *
     * @param from 起点节点 ID
     * @param to   终点节点 ID
     */
    public EdgeRequest(String from, String to) {
        this.from = from;
        this.to = to;
    }

    /**
     * 获取起点节点 ID。
     *
     * @return 起点节点 ID
     */
    public String getFrom() {
        return from;
    }

    /**
     * 设置起点节点 ID。
     *
     * @param from 起点节点 ID
     */
    public void setFrom(String from) {
        this.from = from;
    }

    /**
     * 获取终点节点 ID。
     *
     * @return 终点节点 ID
     */
    public String getTo() {
        return to;
    }

    /**
     * 设置终点节点 ID。
     *
     * @param to 终点节点 ID
     */
    public void setTo(String to) {
        this.to = to;
    }
}