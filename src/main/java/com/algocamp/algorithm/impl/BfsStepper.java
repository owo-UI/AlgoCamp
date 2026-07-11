/**BFS:
 * 起点入队，并标记为已访问
 * 队列非空时循环：
   a. 出队 → 加入访问顺序
   b. 遍历邻居，未访问的入队并标记
   返回访问顺序列表
 */
package com.algocamp.algorithm.impl;

import com.algocamp.algorithm.AlgorithmStepper;
import com.algocamp.algorithm.AlgorithmType;
import com.algocamp.domain.Graph;
import com.algocamp.domain.StepState;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;

/**
 * 广度优先搜索（BFS）算法的步进实现类。
 * <p>
 * 使用队列实现层序遍历，支持：
 * <ul>
 *   <li>{@link #getVisitOrder} — 一次性返回完整访问顺序</li>
 *   <li>{@link #executeStepByStep} — 步进版（下一步实现）</li>
 * </ul>
 * </p>
 *
 * @author AlgoCamp
 */
public class BfsStepper implements AlgorithmStepper {

    @Override
    public AlgorithmType getAlgorithmType() {
        return AlgorithmType.BFS;
    }

    /**
     * 执行 BFS，返回节点访问顺序。
     * <p>
     * 算法过程：从起始节点出发，按「层序」依次访问节点。
     * 每次从队头取出一个节点进行处理，并将其未访问的邻居加入队尾。
     * </p>
     *
     * @param graph       待遍历的图
     * @param startVertex 起始节点 ID
     * @return 按 BFS 访问先后顺序排列的节点 ID 列表
     */
    @Override
    public List<String> getVisitOrder(Graph graph, String startVertex) {
        validateInput(graph, startVertex);

        // 访问顺序结果集
        List<String> visitOrder = new ArrayList<>();

        // 已访问节点集合，用于 O(1) 去重判断
        Set<String> visitedVertices = new HashSet<>();

        // BFS 核心数据结构：先进先出队列
        Queue<String> queue = new LinkedList<>();

        // 起点入队并标记已访问
        visitedVertices.add(startVertex);
        queue.offer(startVertex);

        while (!queue.isEmpty()) {
            // 取出队头节点作为当前处理节点
            String currentVertex = queue.poll();
            visitOrder.add(currentVertex);

            // 将当前节点的所有未访问邻居入队
            for (String neighbor : graph.getNeighbors(currentVertex)) {
                if (!visitedVertices.contains(neighbor)) {
                    visitedVertices.add(neighbor);
                    queue.offer(neighbor);
                }
            }
        }

        return visitOrder;
    }

/**
 * 以步进方式执行 BFS，返回每一步的完整内部状态快照。
 * <p>
 * 步进规则：
 * <ol>
 *   <li>第 0 步：起点入队，记录初始状态（尚未处理任何节点）</li>
 *   <li>后续每步：出队一个节点 → 记录当前节点与已访问列表 → 将未访问邻居入队 → 记录队列快照</li>
 *   <li>最后一步：队列为空时 {@code finished = true}</li>
 * </ol>
 * </p>
 *
 * @param graph       待遍历的图
 * @param startVertex 起始节点 ID
 * @return 按执行顺序排列的步进状态列表
 */
@Override
public List<StepState> executeStepByStep(Graph graph, String startVertex) {
    validateInput(graph, startVertex);

    List<StepState> steps = new ArrayList<>();

    // 已入队（已发现）的节点集合，防止重复入队
    Set<String> discoveredVertices = new HashSet<>();

    // 已处理（已出队）的节点列表，按处理顺序排列
    List<String> processedVertices = new ArrayList<>();

    Queue<String> queue = new LinkedList<>();

    // 起点入队
    discoveredVertices.add(startVertex);
    queue.offer(startVertex);

    // 第 0 步：初始状态快照
    steps.add(buildStepState(
            0,
            null,
            processedVertices,
            queue,
            false
    ));

    int stepNumber = 1;

    while (!queue.isEmpty()) {
        // 出队当前要处理的节点
        String currentVertex = queue.poll();
        processedVertices.add(currentVertex);

        // 将当前节点的未访问邻居入队
        for (String neighbor : graph.getNeighbors(currentVertex)) {
            if (!discoveredVertices.contains(neighbor)) {
                discoveredVertices.add(neighbor);
                queue.offer(neighbor);
            }
        }

        // 判断是否为最后一步（队列已空）
        boolean finished = queue.isEmpty();

        steps.add(buildStepState(
                stepNumber,
                currentVertex,
                processedVertices,
                queue,
                finished
        ));

        stepNumber++;
    }

    return steps;
}


/**
 * 构建一步的 {@link StepState} 快照。
 * 内部会对队列和已访问列表做拷贝，避免后续修改影响已记录的状态。
 *
 * @param stepNumber        步序号
 * @param currentVertex     当前处理节点，可为 null
 * @param processedVertices 已处理节点列表
 * @param queue             BFS 队列
 * @param finished          是否为结束步
 * @return 本步状态快照
 */
private StepState buildStepState(int stepNumber,
                                 String currentVertex,
                                 List<String> processedVertices,
                                 Queue<String> queue,
                                 boolean finished) {
    return new StepState(
            stepNumber,
            currentVertex,
            new ArrayList<>(processedVertices),
            snapshotQueue(queue),
            finished
    );
}

/**
 * 拷贝 BFS 队列当前内容，队头在前、队尾在后。
 *
 * @param queue BFS 队列
 * @return 队列快照列表
 */
private List<String> snapshotQueue(Queue<String> queue) {
    return new ArrayList<>(queue);
}

    /**
     * 校验 BFS 执行的输入参数是否合法。
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

//     /**
//      * main类
//      */
//     public static void main(String[] args) {
//        Graph graph = new Graph(false);
// graph.addEdge("A", "B");
// graph.addEdge("A", "C");
// graph.addEdge("B", "D");
// BfsStepper bfs = new BfsStepper();
// List<StepState> steps = bfs.executeStepByStep(graph, "A");
// for (StepState step : steps) {
//     System.out.println(step);
// }
//     }


}