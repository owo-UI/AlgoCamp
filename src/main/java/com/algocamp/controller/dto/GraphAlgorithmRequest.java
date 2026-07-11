package com.algocamp.controller.dto;

import com.algocamp.domain.Graph;

import java.util.ArrayList;
import java.util.List;

/**
 * 图算法执行请求的数据传输对象。
 * <p>
 * 封装前端提交的完整图数据（有向/无向、边列表、起始节点、算法类型），
 * 并提供 {@link #buildGraph()} 方法将其转换为内部 domain 层的 {@link Graph} 对象。
 * </p>
 *
 * @author AlgoCamp
 */
public class GraphAlgorithmRequest {

    /**
     * 是否为有向图。false 表示无向图（默认）。
     */
    private boolean directed;

    /**
     * 图的边列表，每条边包含 from 和 to 两个节点 ID。
     */
    private List<EdgeRequest> edges;

    /**
     * BFS/DFS 等算法的起始节点 ID。
     */
    private String startVertex;

    /**
     * 算法类型代码，如 "BFS"、"DFS"。
     */
    private String algorithm;

    /**
     * 无参构造方法，供 JSON 反序列化使用。
     */
    public GraphAlgorithmRequest() {
    }

    /**
     * 获取是否为有向图。
     *
     * @return 有向图返回 true，无向图返回 false
     */
    public boolean isDirected() {
        return directed;
    }

    /**
     * 设置是否为有向图。
     *
     * @param directed 有向图标志
     */
    public void setDirected(boolean directed) {
        this.directed = directed;
    }

    /**
     * 获取边列表。
     *
     * @return 边列表，可能为 null
     */
    public List<EdgeRequest> getEdges() {
        return edges;
    }

    /**
     * 设置边列表。
     *
     * @param edges 边列表
     */
    public void setEdges(List<EdgeRequest> edges) {
        this.edges = edges;
    }

    /**
     * 获取起始节点 ID。
     *
     * @return 起始节点 ID
     */
    public String getStartVertex() {
        return startVertex;
    }

    /**
     * 设置起始节点 ID。
     *
     * @param startVertex 起始节点 ID
     */
    public void setStartVertex(String startVertex) {
        this.startVertex = startVertex;
    }

    /**
     * 获取算法类型代码。
     *
     * @return 算法代码，如 "BFS"
     */
    public String getAlgorithm() {
        return algorithm;
    }

    /**
     * 设置算法类型代码。
     *
     * @param algorithm 算法代码
     */
    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    /**
     * 将本请求对象转换为 domain 层的 {@link Graph} 对象。
     * <p>
     * 遍历边列表，依次调用 {@link Graph#addEdge} 构建邻接表。
     * 若边列表为 null 或为空，则返回仅含节点（或无节点）的空图。
     * </p>
     *
     * @return 构建好的 Graph 对象
     */
    public Graph buildGraph() {
        Graph graph = new Graph(directed);
        if (edges == null) {
            return graph;
        }
        for (EdgeRequest edge : edges) {
            if (edge != null && edge.getFrom() != null && edge.getTo() != null) {
                graph.addEdge(edge.getFrom(), edge.getTo());
            }
        }
        return graph;
    }
}