package com.algocamp.controller.dto;

/**
 * 图边的请求数据传输对象。
 * <p>
 * 对应前端拖拽连线时产生的单条边，包含起点和终点节点 ID可选的权重。
 * Dijkstra 算法需要 weight 字段；BFS、DFS 可不传（默认 1）。
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
     * 边的权重（距离）。
     * 为 null 时视为默认权重 1，供 BFS、DFS 等无权图算法使用。
     */
    private Integer weight;

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
     * 全参构造方法，便于测试时手动构建带权边。
     *
     * @param from   起点节点 ID
     * @param to     终点节点 ID
     * @param weight 边权重
     */
    public EdgeRequest(String from, String to, Integer weight) {
        this.from = from;
        this.to = to;
        this.weight = weight;
    }

    /**
     * 获取边权重。
     *
     * @return 边权重；未设置时返回 null
     */
    public Integer getWeight() {
        return weight;
    }

    /**
     * 设置边权重。
     *
     * @param weight 边权重，必须为正数
     */
    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    /**
     * 获取有效权重：若未设置则返回默认权重 1。
     *
     * @return 有效边权重
     */
    public int getEffectiveWeight() {
        if (weight == null) {
            return 1;
        }
        if (weight <= 0) {
            throw new IllegalArgumentException("边权重必须为正数: " + weight);
        }
        return weight;
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