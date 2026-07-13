package com.algocamp.algorithm.impl;

import com.algocamp.algorithm.AlgorithmStepper;
import com.algocamp.algorithm.AlgorithmType;
import com.algocamp.domain.Graph;
import com.algocamp.domain.StepState;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Collections;

/**
 * 深度优先搜索（DFS）算法的步进实现类。
 * <p>
 * 使用栈（{@link Deque}）实现深度优先遍历，支持：
 * <ul>
 *   <li>{@link #getVisitOrder} — 一次性返回完整访问顺序</li>
 *   <li>{@link #executeStepByStep} — 步进版（下一步实现）</li>
 * </ul>
 * </p>
 *
 * @author AlgoCamp
 */
public class DfsStepper implements AlgorithmStepper {

    @Override
    public AlgorithmType getAlgorithmType() {
        return AlgorithmType.DFS;
    }

    /**
     * 执行 DFS，返回节点访问顺序。
     * <p>
     * 算法过程：从起始节点出发，每次取栈顶节点处理，
     * 并将其未访问的邻居逆序压入栈中（保证先深入第一条路径）。
     * </p>
     *
     * @param graph       待遍历的图
     * @param startVertex 起始节点 ID
     * @return 按 DFS 访问先后顺序排列的节点 ID 列表
     */
    @Override
    public List<String> getVisitOrder(Graph graph, String startVertex) {
        validateInput(graph, startVertex);

        // 访问顺序结果集
        List<String> visitOrder = new ArrayList<>();

        // 已发现节点集合，防止重复入栈
        Set<String> discoveredVertices = new HashSet<>();

        // DFS 核心数据结构：后进先出栈
        Deque<String> stack = new ArrayDeque<>();

        // 起点入栈并标记已发现
        discoveredVertices.add(startVertex);
        stack.push(startVertex);

        while (!stack.isEmpty()) {
            // 弹出栈顶节点作为当前处理节点
            String currentVertex = stack.pop();
            visitOrder.add(currentVertex);

            // 邻居逆序入栈，保证先访问第一个邻居
            List<String> neighbors = graph.getNeighbors(currentVertex);
            for (int i = neighbors.size() - 1; i >= 0; i--) {
                String neighbor = neighbors.get(i);
                if (!discoveredVertices.contains(neighbor)) {
                    discoveredVertices.add(neighbor);
                    stack.push(neighbor);
                }
            }
        }

        return visitOrder;
    }

/**
 * 以步进方式执行 DFS，返回每一步的完整内部状态快照。
 * <p>
 * 步进规则：
 * <ol>
 *   <li>第 0 步：起点入栈，记录初始状态（尚未处理任何节点）</li>
 *   <li>后续每步：弹出栈顶 → 记录当前节点与已访问列表 → 将未访问邻居逆序入栈 → 记录栈快照</li>
 *   <li>最后一步：栈为空时 {@code finished = true}</li>
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

    // 已发现节点集合，防止重复入栈
    Set<String> discoveredVertices = new HashSet<>();

    // 已处理（已弹出）的节点列表，按处理顺序排列
    List<String> processedVertices = new ArrayList<>();

    Deque<String> stack = new ArrayDeque<>();

    // 起点入栈
    discoveredVertices.add(startVertex);
    stack.push(startVertex);

    // 第 0 步：初始状态快照
    steps.add(buildStepState(
            0,
            null,
            processedVertices,
            stack,
            false
    ));

    int stepNumber = 1;

    while (!stack.isEmpty()) {
        // 弹出栈顶节点
        String currentVertex = stack.pop();
        processedVertices.add(currentVertex);

        // 邻居逆序入栈
        List<String> neighbors = graph.getNeighbors(currentVertex);
        for (int i = neighbors.size() - 1; i >= 0; i--) {
            String neighbor = neighbors.get(i);
            if (!discoveredVertices.contains(neighbor)) {
                discoveredVertices.add(neighbor);
                stack.push(neighbor);
            }
        }

        boolean finished = stack.isEmpty();

        steps.add(buildStepState(
                stepNumber,
                currentVertex,
                processedVertices,
                stack,
                finished
        ));

        stepNumber++;
    }

    return steps;
}

/**
 * 构建一步的 {@link StepState} 快照。
 *
 * @param stepNumber        步序号
 * @param currentVertex     当前处理节点，可为 null
 * @param processedVertices 已处理节点列表
 * @param stack             DFS 栈
 * @param finished          是否为结束步
 * @return 本步状态快照
 */
private StepState buildStepState(int stepNumber,
                                 String currentVertex,
                                 List<String> processedVertices,
                                 Deque<String> stack,
                                 boolean finished) {
    return new StepState(
            stepNumber,
            currentVertex,
            new ArrayList<>(processedVertices),
            Collections.emptyList(),   // DFS 不使用队列，传空列表
            snapshotStack(stack),
            finished
    );
}

/**
 * 拷贝 DFS 栈当前内容，栈底在前、栈顶在后。
 * <p>
 * {@link ArrayDeque} 的迭代顺序是栈顶在前，因此使用 {@code descendingIterator()}
 * 从栈底到栈顶遍历，得到符合前端展示习惯的列表。
 * </p>
 *
 * @param stack DFS 栈
 * @return 栈快照列表（栈底在前，栈顶在后）
 */
private List<String> snapshotStack(Deque<String> stack) {
    List<String> snapshot = new ArrayList<>();
    stack.descendingIterator().forEachRemaining(snapshot::add);
    return snapshot;
}

    /**
     * 校验 DFS 执行的输入参数是否合法。
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


// public static void main(String[] args) {
//     Graph graph = new Graph(false);
//     graph.addEdge("A", "B");
//     graph.addEdge("A", "C");
//     graph.addEdge("B", "D");
//     DfsStepper dfs = new DfsStepper();
//     for (StepState step : dfs.executeStepByStep(graph, "A")) {
//         System.out.println(step);
//         }
//     }
}
