package com.algocamp.algorithm.impl;

import com.algocamp.algorithm.AlgorithmStepper;
import com.algocamp.algorithm.AlgorithmType;
import com.algocamp.domain.Graph;
import com.algocamp.domain.StepState;
import com.algocamp.domain.WeightedEdge;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.Collections;

/**
 * Dijkstra 最短路径算法的步进实现类。
 * <p>
 * 使用优先队列（按距离排序）求单源最短路径，支持：
 * <ul>
 *   <li>{@link #getVisitOrder} — 返回节点确定最短路径的顺序</li>
 *   <li>{@link #executeStepByStep} — 步进版（下一步实现）</li>
 * </ul>
 * </p>
 *
 * @author AlgoCamp
 */
public class DijkstraStepper implements AlgorithmStepper {

    /**
     * 表示尚未可达节点的距离上界。
     */
    private static final int INFINITY = Integer.MAX_VALUE;

    @Override
    public AlgorithmType getAlgorithmType() {
        return AlgorithmType.DIJKSTRA;
    }

    /**
     * 执行 Dijkstra 算法，返回节点确定最短路径的顺序。
     * <p>
     * 每次从优先队列取出距离最小的节点并「确定」，
     * 然后松弛其邻居。返回的列表即为节点被确定的先后顺序。
     * </p>
     *
     * @param graph       待遍历的带权图
     * @param startVertex 起始节点 ID
     * @return 按确定先后顺序排列的节点 ID 列表
     */
    @Override
    public List<String> getVisitOrder(Graph graph, String startVertex) {
        validateInput(graph, startVertex);

        // 各节点当前已知的最短距离
        Map<String, Integer> distances = initializeDistances(graph, startVertex);

        // 已确定最短路径的节点集合
        Set<String> settledVertices = new HashSet<>();

        // 确定顺序结果集
        List<String> visitOrder = new ArrayList<>();

        // 按距离升序排列的优先队列
        PriorityQueue<String> priorityQueue = createPriorityQueue(distances);

        distances.put(startVertex, 0);
        priorityQueue.offer(startVertex);

        while (!priorityQueue.isEmpty()) {
            String currentVertex = priorityQueue.poll();

            // 懒删除：队列中可能有过期的重复条目
            if (settledVertices.contains(currentVertex)) {
                continue;
            }

            settledVertices.add(currentVertex);
            visitOrder.add(currentVertex);

            // 松弛当前节点的所有邻居
            for (WeightedEdge edge : graph.getWeightedNeighbors(currentVertex)) {
                String neighbor = edge.getTo();
                if (settledVertices.contains(neighbor)) {
                    continue;
                }

                int candidateDistance = distances.get(currentVertex) + edge.getWeight();
                if (candidateDistance < distances.get(neighbor)) {
                    distances.put(neighbor, candidateDistance);
                    priorityQueue.offer(neighbor);
                }
            }
        }

        return visitOrder;
    }

    /**
     * 以步进方式执行 Dijkstra，返回每一步的完整内部状态快照。
     * <p>
     * 步进规则：
     * <ol>
     *   <li>第 0 步：起点入优先队列，记录初始状态</li>
     *   <li>后续每步：取出最小距离节点并确定 → 松弛邻居 → 记录距离表和队列快照</li>
     *   <li>最后一步：队列为空时 {@code finished = true}</li>
     * </ol>
     * </p>
     *
     * @param graph       待遍历的带权图
     * @param startVertex 起始节点 ID
     * @return 按执行顺序排列的步进状态列表
     */
    @Override
    public List<StepState> executeStepByStep(Graph graph, String startVertex) {
        validateInput(graph, startVertex);

        List<StepState> steps = new ArrayList<>();

        Map<String, Integer> distances = initializeDistances(graph, startVertex);
        List<String> settledVertices = new ArrayList<>();
        PriorityQueue<String> priorityQueue = createPriorityQueue(distances);

        distances.put(startVertex, 0);
        priorityQueue.offer(startVertex);

        // 第 0 步：初始状态
        steps.add(buildStepState(
                0,
                null,
                settledVertices,
                distances,
                priorityQueue,
                false
        ));

        int stepNumber = 1;

        while (!priorityQueue.isEmpty()) {
            String currentVertex = priorityQueue.poll();

            // 懒删除：跳过已过期的队列条目，不记录步骤
            if (settledVertices.contains(currentVertex)) {
                continue;
            }

            settledVertices.add(currentVertex);

            // 松弛邻居
            for (WeightedEdge edge : graph.getWeightedNeighbors(currentVertex)) {
                String neighbor = edge.getTo();
                if (settledVertices.contains(neighbor)) {
                    continue;
                }
                int candidateDistance = distances.get(currentVertex) + edge.getWeight();
                if (candidateDistance < distances.get(neighbor)) {
                    distances.put(neighbor, candidateDistance);
                    priorityQueue.offer(neighbor);
                }
            }

            boolean finished = priorityQueue.isEmpty();

            steps.add(buildStepState(
                    stepNumber,
                    currentVertex,
                    settledVertices,
                    distances,
                    priorityQueue,
                    finished
            ));

            stepNumber++;
        }

        return steps;
    }

    /**
     * 创建按距离升序排列的优先队列。
     * 距离相同时按节点 ID 字典序，保证结果稳定。
     *
     * @param distances 当前距离表
     * @return 优先队列
     */
    private PriorityQueue<String> createPriorityQueue(Map<String, Integer> distances) {
        return new PriorityQueue<>(
                (vertexA, vertexB) -> {
                    int distanceCompare = Integer.compare(
                            distances.get(vertexA),
                            distances.get(vertexB)
                    );
                    if (distanceCompare != 0) {
                        return distanceCompare;
                    }
                    return vertexA.compareTo(vertexB);
                }
        );
    }



    /**
     * 初始化所有节点距离为无穷大，起点距离为 0。
     *
     * @param graph       图对象
     * @param startVertex 起始节点 ID
     * @return 距离映射表
     */
    private Map<String, Integer> initializeDistances(Graph graph, String startVertex) {
        Map<String, Integer> distances = new HashMap<>();
        for (String vertex : graph.getVertices()) {
            distances.put(vertex, INFINITY);
        }
        distances.put(startVertex, 0);
        return distances;
    }

    /**
     * 构建一步的 {@link StepState} 快照。
     *
     * @param stepNumber     步序号
     * @param currentVertex  当前确定的节点，可为 null
     * @param settledVertices 已确定节点列表
     * @param distances      当前距离表
     * @param priorityQueue  优先队列
     * @param finished       是否为结束步
     * @return 本步状态快照
     */
    private StepState buildStepState(int stepNumber,
                                     String currentVertex,
                                     List<String> settledVertices,
                                     Map<String, Integer> distances,
                                     PriorityQueue<String> priorityQueue,
                                     boolean finished) {
        return new StepState(
                stepNumber,
                currentVertex,
                new ArrayList<>(settledVertices),
                Collections.emptyList(),   // BFS 队列
                Collections.emptyList(),   // DFS 栈
                snapshotDistances(distances),
                snapshotPriorityQueue(priorityQueue, distances),
                Collections.emptyMap(),    // 拓扑排序入度表
                finished
        );
    }

    /**
     * 拷贝距离表，只保留已可达节点（距离 &lt; INFINITY）。
     *
     * @param distances 原始距离表
     * @return 距离快照
     */
    private Map<String, Integer> snapshotDistances(Map<String, Integer> distances) {
        Map<String, Integer> snapshot = new HashMap<>();
        for (Map.Entry<String, Integer> entry : distances.entrySet()) {
            if (entry.getValue() < INFINITY) {
                snapshot.put(entry.getKey(), entry.getValue());
            }
        }
        return snapshot;
    }

    /**
     * 拷贝优先队列内容，按距离从小到大排序后返回。
     * <p>
     * {@link PriorityQueue} 内部无序，需取出所有元素重新排序，
     * 以便前端按距离从小到大的顺序展示。
     * </p>
     *
     * @param priorityQueue 优先队列
     * @param distances     当前距离表
     * @return 排序后的节点列表
     */
    private List<String> snapshotPriorityQueue(PriorityQueue<String> priorityQueue,
                                               Map<String, Integer> distances) {
        List<String> vertices = new ArrayList<>(priorityQueue);
        vertices.sort((vertexA, vertexB) -> {
            int distanceCompare = Integer.compare(
                    distances.get(vertexA),
                    distances.get(vertexB)
            );
            if (distanceCompare != 0) {
                return distanceCompare;
            }
            return vertexA.compareTo(vertexB);
        });
        return vertices;
    }

    /**
     * 校验 Dijkstra 执行的输入参数是否合法。
     *
     * @param graph       图对象
     * @param startVertex 起始节点 ID
     * @throws IllegalArgumentException 参数不合法时抛出
     */
    private void validateInput(Graph graph, String startVertex) {
        if (graph == null) {
            throw new IllegalArgumentException("图对象不能为 null");
        }
        if (startVertex == null || startVertex.isBlank()) {
            throw new IllegalArgumentException("起始节点 ID 不能为空");
        }
        if (!graph.containsVertex(startVertex)) {
            throw new IllegalArgumentException("起始节点不存在于图中: " + startVertex);
        }
    }
}