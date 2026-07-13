package com.algocamp.domain;

/**
 * 带权边，表示从某节点出发的一条邻接边。
 * <p>
 * 用于 Dijkstra 等需要边权重的算法。
 * 不可变对象，创建后权重和目标节点不可修改。
 * </p>
 *
 * @author AlgoCamp
 */
public class WeightedEdge {

    /**
     * 边的目标节点 ID。
     */
    private final String to;

    /**
     * 边的权重（距离），必须为正数。
     */
    private final int weight;

    /**
     * 构造一条带权边。
     *
     * @param to     目标节点 ID
     * @param weight 边权重，必须大于 0
     * @throws IllegalArgumentException 若 to 为空或 weight ≤ 0
     */
    public WeightedEdge(String to, int weight) {
        if (to == null || to.isBlank()) {
            throw new IllegalArgumentException("目标节点 ID 不能为空");
        }
        if (weight <= 0) {
            throw new IllegalArgumentException("边权重必须为正数: " + weight);
        }
        this.to = to;
        this.weight = weight;
    }

    /**
     * 获取目标节点 ID。
     *
     * @return 目标节点 ID
     */
    public String getTo() {
        return to;
    }

    /**
     * 获取边权重。
     *
     * @return 边权重
     */
    public int getWeight() {
        return weight;
    }

    @Override
    public String toString() {
        return to + "(" + weight + ")";
    }
}