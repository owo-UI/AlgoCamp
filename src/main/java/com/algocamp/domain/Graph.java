
package com.algocamp.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 图的邻接表表示。
 * <p>
 * 用于算法工坊中存储用户创建的图结构，供 BFS、DFS 等图算法读取。
 * 节点以字符串 ID 标识（如 "A"、"B"），便于前端可视化展示。
 * </p>
 *
 * @author AlgoCamp
 */
public class Graph {

    /**
     * 邻接表：key 为节点 ID，value 为该节点的所有邻居节点 ID 列表。
     * 使用 HashMap 保证 O(1) 查找节点，邻居列表保持插入顺序便于展示。
     */
    private final Map<String, List<WeightedEdge>> adjacencyList;

    /**
     * 是否为有向图。
     * true：边 A→B 只记录在 A 的邻居列表中；
     * false：边 A—B 双向记录。
     */
    private final boolean directed;

    /**
     * 无权边的默认权重（用于 BFS、DFS 等算法）。
     */
    private static final int DEFAULT_EDGE_WEIGHT = 1;

    /**
     * 构造一个空图。
     *
     * @param directed 是否为有向图
     */
    public Graph(boolean directed) {
        this.adjacencyList = new HashMap<>();
        this.directed = directed;
    }

    /**
     * 向图中添加一个节点。
     * 若节点已存在，则不重复添加。
     *
     * @param vertexId 节点 ID，不能为空
     * @throws IllegalArgumentException 若 vertexId 为 null 或空字符串
     */
    public void addVertex(String vertexId) {
        validateVertexId(vertexId);
        adjacencyList.putIfAbsent(vertexId, new ArrayList<>());
    }

    /**
     * 向图中添加一条无权边（默认权重为 1）。
     * 供 BFS、DFS 等无权图算法使用。
     *
     * @param from 起点节点 ID
     * @param to   终点节点 ID
     */
    public void addEdge(String from, String to) {
        addWeightedEdge(from, to, DEFAULT_EDGE_WEIGHT);
    }

    /**
     * 向图中添加一条带权边。
     * 若端点不存在，会自动创建节点。
     *
     * @param from   起点节点 ID
     * @param to     终点节点 ID
     * @param weight 边权重，必须大于 0
     * @throws IllegalArgumentException 若参数无效或权重 ≤ 0
     */
    public void addWeightedEdge(String from, String to, int weight) {
        validateVertexId(from);
        validateVertexId(to);
        addVertex(from);
        addVertex(to);
        adjacencyList.get(from).add(new WeightedEdge(to, weight));
        if (!directed) {
            adjacencyList.get(to).add(new WeightedEdge(from, weight));
        }
    }
    /**
     * 获取指定节点的所有邻居节点 ID。
     * 返回不可变列表，调用方无法修改内部邻接表。
     *
     * @param vertexId 节点 ID
     * @return 邻居节点 ID 列表；若节点不存在则返回空列表
     */
    public List<String> getNeighbors(String vertexId) {
        validateVertexId(vertexId);
        List<WeightedEdge> edges = adjacencyList.get(vertexId);
        if (edges == null) {
            return Collections.emptyList();
        }
        List<String> neighbors = new ArrayList<>();
        for (WeightedEdge edge : edges) {
            neighbors.add(edge.getTo());
        }
        return Collections.unmodifiableList(neighbors);
    }

    /**
     * 获取指定节点的所有带权出边。
     *
     * @param vertexId 节点 ID
     * @return 不可变的带权边列表；若节点不存在则返回空列表
     */
    public List<WeightedEdge> getWeightedNeighbors(String vertexId) {
        validateVertexId(vertexId);
        List<WeightedEdge> edges = adjacencyList.get(vertexId);
        if (edges == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(new ArrayList<>(edges));
    }

    /**
     * 获取两点之间的边权重。
     *
     * @param from 起点节点 ID
     * @param to   终点节点 ID
     * @return 边权重；若边不存在则返回 -1
     */
    public int getEdgeWeight(String from, String to) {
        validateVertexId(from);
        validateVertexId(to);
        List<WeightedEdge> edges = adjacencyList.get(from);
        if (edges == null) {
            return -1;
        }
        for (WeightedEdge edge : edges) {
            if (edge.getTo().equals(to)) {
                return edge.getWeight();
            }
        }
        return -1;
    }

    /**
     * 获取图中所有节点 ID。
     *
     * @return 不可变的节点 ID 集合
     */
    public Set<String> getVertices() {
        return Collections.unmodifiableSet(adjacencyList.keySet());
    }

    /**
     * 判断图中是否包含指定节点。
     *
     * @param vertexId 节点 ID
     * @return 存在返回 true，否则 false
     */
    public boolean containsVertex(String vertexId) {
        if (vertexId == null || vertexId.isBlank()) {
            return false;
        }
        return adjacencyList.containsKey(vertexId);
    }

    /**
     * 获取图中节点总数。
     *
     * @return 节点数量
     */
    public int getVertexCount() {
        return adjacencyList.size();
    }

    /**
     * 判断是否为有向图。
     *
     * @return 有向图返回 true，无向图返回 false
     */
    public boolean isDirected() {
        return directed;
    }

    /**
     * 获取邻接表的只读副本，供调试或序列化使用。
     * 外层 Map 与内层 List 均为不可变视图。
     *
     * @return 不可变的邻接表映射
     */
    public Map<String, List<String>> getAdjacencyListSnapshot() {
        Map<String, List<String>> snapshot = new HashMap<>();
        for (Map.Entry<String, List<WeightedEdge>> entry : adjacencyList.entrySet()) {
            List<String> neighbors = new ArrayList<>();
            for (WeightedEdge edge : entry.getValue()) {
                neighbors.add(edge.getTo());
            }
            snapshot.put(entry.getKey(), Collections.unmodifiableList(neighbors));
        }
        return Collections.unmodifiableMap(snapshot);
    }

    /**
     * 校验节点 ID 是否合法。
     *
     * @param vertexId 待校验的节点 ID
     * @throws IllegalArgumentException 若 ID 为 null 或空白
     */
    private void validateVertexId(String vertexId) {
        if (vertexId == null || vertexId.isBlank()) {
            throw new IllegalArgumentException("节点 ID 不能为空");
        }
    }



//    /**
//     * 测试的main函数
//     * @param args
//     */
//     public static void main(String[] args) {
//         Graph graph = new Graph(false);
//         graph.addWeightedEdge("A", "B", 4);
//         graph.addWeightedEdge("A", "C", 2);
//         graph.addWeightedEdge("B", "D", 5);
//         graph.addEdge("C", "D");  // 默认权重 1
//
//         System.out.println(graph.getNeighbors("A"));              // [B, C]
//         System.out.println(graph.getWeightedNeighbors("A"));      // [B(4), C(2)]
//         System.out.println(graph.getEdgeWeight("A", "B"));          // 4
//         System.out.println(graph.getEdgeWeight("C", "D"));          // 1
//    }

}