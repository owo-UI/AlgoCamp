package com.algocamp.algorithm.impl;

import com.algocamp.algorithm.AlgorithmStepper;
import com.algocamp.algorithm.AlgorithmType;
import com.algocamp.domain.Graph;
import com.algocamp.domain.StepState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Collections;

/**
 * 拓扑排序（Kahn 入度法）的步进实现类。
 * <p>
 * 仅适用于<strong>有向无环图（DAG）</strong>。
 * 使用队列维护入度为 0 的节点，依次输出拓扑有序序列。
 * </p>
 *
 * @author AlgoCamp
 */
public class TopologicalSortStepper implements AlgorithmStepper {

    /**
     * 初始入度值（尚未统计前）。
     */
    private static final int ZERO_IN_DEGREE = 0;

    @Override
    public AlgorithmType getAlgorithmType() {
        return AlgorithmType.TOPOLOGICAL_SORT;
    }

    /**
     * 执行拓扑排序，返回拓扑有序节点序列。
     * <p>
     * 使用 Kahn 算法：反复取出入度为 0 的节点，将其邻居入度减 1。
     * 若最终输出节点数少于图中节点总数，说明存在环。
     * </p>
     *
     * @param graph       待排序的有向图
     * @param startVertex 本算法不使用，仅用于通过接口参数校验
     * @return 拓扑有序节点 ID 列表
     */
    @Override
    public List<String> getVisitOrder(Graph graph, String startVertex) {
        validateInput(graph, startVertex);

        // 统计各节点入度
        Map<String, Integer> inDegrees = computeInDegrees(graph);

        // 拓扑排序结果
        List<String> topologicalOrder = new ArrayList<>();

        // 入度为 0 的节点队列
        Queue<String> zeroInDegreeQueue = new LinkedList<>();

        // 将所有入度为 0 的节点入队
        for (Map.Entry<String, Integer> entry : inDegrees.entrySet()) {
            if (entry.getValue() == ZERO_IN_DEGREE) {
                zeroInDegreeQueue.offer(entry.getKey());
            }
        }

        while (!zeroInDegreeQueue.isEmpty()) {
            String currentVertex = zeroInDegreeQueue.poll();
            topologicalOrder.add(currentVertex);

            // 将邻居入度减 1，若变为 0 则入队
            for (String neighbor : graph.getNeighbors(currentVertex)) {
                int updatedInDegree = inDegrees.get(neighbor) - 1;
                inDegrees.put(neighbor, updatedInDegree);
                if (updatedInDegree == ZERO_IN_DEGREE) {
                    zeroInDegreeQueue.offer(neighbor);
                }
            }
        }

        // 检测环：输出节点数不足说明存在环
        if (topologicalOrder.size() != graph.getVertexCount()) {
            throw new IllegalArgumentException("图中存在环，无法进行拓扑排序");
        }

        return topologicalOrder;
    }

    /**
     * 以步进方式执行拓扑排序，返回每一步的完整内部状态快照。
     * <p>
     * 步进规则：
     * <ol>
     *   <li>第 0 步：统计入度，将入度为 0 的节点入队，记录初始状态</li>
     *   <li>后续每步：出队一个节点 → 加入拓扑序列 → 邻居入度减 1 → 记录入度表和队列快照</li>
     *   <li>最后一步：队列为空时 {@code finished = true}</li>
     * </ol>
     * </p>
     *
     * @param graph       待排序的有向图
     * @param startVertex 本算法不使用，仅用于参数校验
     * @return 按执行顺序排列的步进状态列表
     */
    @Override
    public List<StepState> executeStepByStep(Graph graph, String startVertex) {
        validateInput(graph, startVertex);

        List<StepState> steps = new ArrayList<>();

        // 可变入度表（算法过程中会修改）
        Map<String, Integer> inDegrees = computeInDegrees(graph);

        // 已输出的拓扑序列
        List<String> topologicalOrder = new ArrayList<>();

        // 入度为 0 的节点队列
        Queue<String> zeroInDegreeQueue = new LinkedList<>();

        // 初始化：所有入度为 0 的节点入队
        for (Map.Entry<String, Integer> entry : inDegrees.entrySet()) {
            if (entry.getValue() == ZERO_IN_DEGREE) {
                zeroInDegreeQueue.offer(entry.getKey());
            }
        }

        // 第 0 步：初始状态
        steps.add(buildStepState(
                0,
                null,
                topologicalOrder,
                inDegrees,
                zeroInDegreeQueue,
                false
        ));

        int stepNumber = 1;

        while (!zeroInDegreeQueue.isEmpty()) {
            String currentVertex = zeroInDegreeQueue.poll();
            topologicalOrder.add(currentVertex);

            // 邻居入度减 1
            for (String neighbor : graph.getNeighbors(currentVertex)) {
                int updatedInDegree = inDegrees.get(neighbor) - 1;
                inDegrees.put(neighbor, updatedInDegree);
                if (updatedInDegree == ZERO_IN_DEGREE) {
                    zeroInDegreeQueue.offer(neighbor);
                }
            }

            boolean finished = zeroInDegreeQueue.isEmpty();

            steps.add(buildStepState(
                    stepNumber,
                    currentVertex,
                    topologicalOrder,
                    inDegrees,
                    zeroInDegreeQueue,
                    finished
            ));

            stepNumber++;
        }

        // 检测环
        if (topologicalOrder.size() != graph.getVertexCount()) {
            throw new IllegalArgumentException("图中存在环，无法进行拓扑排序");
        }

        return steps;
    }

    /**
     * 统计图中各节点的入度。
     *
     * @param graph 有向图
     * @return 节点 ID → 入度 的映射表
     */
    private Map<String, Integer> computeInDegrees(Graph graph) {
        Map<String, Integer> inDegrees = new HashMap<>();

        // 初始化所有节点入度为 0
        for (String vertex : graph.getVertices()) {
            inDegrees.put(vertex, ZERO_IN_DEGREE);
        }

        // 遍历所有边，累加目标节点的入度
        for (String vertex : graph.getVertices()) {
            for (String neighbor : graph.getNeighbors(vertex)) {
                inDegrees.put(neighbor, inDegrees.get(neighbor) + 1);
            }
        }

        return inDegrees;
    }

    /**
     * 构建一步的 {@link StepState} 快照。
     *
     * @param stepNumber       步序号
     * @param currentVertex    当前输出节点，可为 null
     * @param topologicalOrder 已输出的拓扑序列
     * @param inDegrees        当前入度表
     * @param queue            入度为 0 的节点队列
     * @param finished         是否为结束步
     * @return 本步状态快照
     */
    private StepState buildStepState(int stepNumber,
                                     String currentVertex,
                                     List<String> topologicalOrder,
                                     Map<String, Integer> inDegrees,
                                     Queue<String> queue,
                                     boolean finished) {
        return new StepState(
                stepNumber,
                currentVertex,
                new ArrayList<>(topologicalOrder),
                snapshotQueue(queue),
                Collections.emptyList(),   // DFS 栈
                Collections.emptyMap(),    // Dijkstra 距离表
                Collections.emptyList(),   // Dijkstra 优先队列
                snapshotInDegrees(inDegrees),
                finished
        );
    }

    /**
     * 拷贝入度表的当前快照。
     *
     * @param inDegrees 入度表
     * @return 入度表副本
     */
    private Map<String, Integer> snapshotInDegrees(Map<String, Integer> inDegrees) {
        return new HashMap<>(inDegrees);
    }

    /**
     * 拷贝入度为 0 的节点队列。
     *
     * @param queue 节点队列
     * @return 队列快照列表
     */
    private List<String> snapshotQueue(Queue<String> queue) {
        return new ArrayList<>(queue);
    }

    /**
     * 校验拓扑排序的输入参数。
     *
     * @param graph       图对象
     * @param startVertex 起始节点（仅校验非空，算法不使用）
     * @throws IllegalArgumentException 参数不合法时抛出
     */
    private void validateInput(Graph graph, String startVertex) {
        if (graph == null) {
            throw new IllegalArgumentException("图对象不能为 null");
        }
        if (!graph.isDirected()) {
            throw new IllegalArgumentException("拓扑排序仅适用于有向图，请设置 directed=true");
        }
        if (startVertex == null || startVertex.isBlank()) {
            throw new IllegalArgumentException("起始节点 ID 不能为空");
        }
        if (!graph.containsVertex(startVertex)) {
            throw new IllegalArgumentException("起始节点不存在于图中: " + startVertex);
        }
    }
}