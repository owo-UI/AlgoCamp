package com.algocamp.domain;

import java.util.Objects;

/**
 * 最小生成树算法中使用的无向带权边。
 * <p>
 * 与 {@link WeightedEdge} 不同：本类同时保存两端节点 ID，
 * 用于 Prim / Kruskal 记录已选树边、候选边，以及前端可视化。
 * A—B 与 B—A 视为同一条边。
 * </p>
 *
 * @author AlgoCamp
 */
public class MstEdge implements Comparable<MstEdge> {

    /**
     * 边的一端节点 ID。
     */
    private final String from;

    /**
     * 边的另一端节点 ID。
     */
    private final String to;

    /**
     * 边权重，必须为正数。
     */
    private final int weight;

    /**
     * 构造一条无向带权边。
     *
     * @param from   一端节点 ID
     * @param to     另一端节点 ID
     * @param weight 边权重，必须大于 0
     * @throws IllegalArgumentException 若节点 ID 无效或权重 ≤ 0
     */
    public MstEdge(String from, String to, int weight) {
        if (from == null || from.isBlank()) {
            throw new IllegalArgumentException("边的一端节点 ID 不能为空");
        }
        if (to == null || to.isBlank()) {
            throw new IllegalArgumentException("边的另一端节点 ID 不能为空");
        }
        if (weight <= 0) {
            throw new IllegalArgumentException("边权重必须为正数: " + weight);
        }
        this.from = from;
        this.to = to;
        this.weight = weight;
    }

    /**
     * 获取边的一端节点 ID。
     *
     * @return 节点 ID
     */
    public String getFrom() {
        return from;
    }

    /**
     * 获取边的另一端节点 ID。
     *
     * @return 节点 ID
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

    /**
     * 生成展示用字符串，两端按字典序排列，保证无向边展示稳定。
     * <p>
     * 例如 from=B、to=A 时返回 {@code "A-B"}。
     * </p>
     *
     * @return 形如 "A-B" 的展示字符串
     */
    public String toDisplayString() {
        if (from.compareTo(to) <= 0) {
            return from + "-" + to;
        }
        return to + "-" + from;
    }

    /**
     * 按权重升序比较；权重相同时按展示字符串比较，保证排序稳定。
     *
     * @param other 另一条边
     * @return 比较结果
     */
    @Override
    public int compareTo(MstEdge other) {
        int weightCompare = Integer.compare(this.weight, other.weight);
        if (weightCompare != 0) {
            return weightCompare;
        }
        return this.toDisplayString().compareTo(other.toDisplayString());
    }

    /**
     * 无向边相等性：两端集合相同且权重相同即视为相等。
     *
     * @param obj 比较对象
     * @return 相等返回 true
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof MstEdge other)) {
            return false;
        }
        boolean sameEndpoints =
                (from.equals(other.from) && to.equals(other.to))
                        || (from.equals(other.to) && to.equals(other.from));
        return sameEndpoints && weight == other.weight;
    }

    @Override
    public int hashCode() {
        // 无向：两端顺序无关
        int endpointHash = from.hashCode() ^ to.hashCode();
        return Objects.hash(endpointHash, weight);
    }

    @Override
    public String toString() {
        return toDisplayString() + "(" + weight + ")";
    }
}