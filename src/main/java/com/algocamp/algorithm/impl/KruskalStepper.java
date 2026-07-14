package com.algocamp.algorithm.impl;

import com.algocamp.algorithm.AlgorithmStepper;
import com.algocamp.algorithm.AlgorithmType;
import com.algocamp.domain.Graph;
import com.algocamp.domain.MstEdge;
import com.algocamp.domain.StepState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Kruskal 最小生成树算法的步进实现类。
 * <p>
 * 将所有边按权重升序排序，借助并查集避免成环，依次选入 MST。
 * 仅适用于<strong>无向连通带权图</strong>。
 * </p>
 *
 * @author AlgoCamp
 */
public class KruskalStepper implements AlgorithmStepper {

    @Override
    public AlgorithmType getAlgorithmType() {
        return AlgorithmType.KRUSKAL;
    }

    /**
     * 执行 Kruskal 算法，返回选入 MST 的边展示串列表。
     * <p>
     * 每项形如 {@code "A-B"}，顺序为边被选中的先后顺序。
     * {@code startVertex} 本算法不使用，仅用于通过接口参数校验。
     * </p>
     *
     * @param graph       待处理的无向带权图
     * @param startVertex 起始节点（不使用）
     * @return 选中边的展示字符串列表
     */
    @Override
    public List<String> getVisitOrder(Graph graph, String startVertex) {
        validateInput(graph, startVertex);

        List<MstEdge> allEdges = new ArrayList<>(graph.getAllUndirectedEdges());
        Collections.sort(allEdges);

        UnionFind unionFind = new UnionFind(graph.getVertices());
        List<MstEdge> mstEdges = new ArrayList<>();
        List<String> selectedEdgeDisplays = new ArrayList<>();

        int targetEdgeCount = graph.getVertexCount() - 1;

        for (MstEdge edge : allEdges) {
            if (mstEdges.size() >= targetEdgeCount) {
                break;
            }
            if (unionFind.find(edge.getFrom()).equals(unionFind.find(edge.getTo()))) {
                continue; // 两端已连通，加入会成环
            }
            unionFind.union(edge.getFrom(), edge.getTo());
            mstEdges.add(edge);
            selectedEdgeDisplays.add(edge.toDisplayString());
        }

        if (mstEdges.size() != targetEdgeCount) {
            throw new IllegalArgumentException("图不连通，无法生成最小生成树");
        }

        return selectedEdgeDisplays;
    }

    /**
     * 以步进方式执行 Kruskal，返回每一步的完整内部状态快照。
     * <p>
     * 步进规则：
     * <ol>
     *   <li>第 0 步：边按权排序完毕，MST 为空</li>
     *   <li>后续每步：考察下一条边；不成环则加入 MST，成环则跳过但仍记录本步</li>
     *   <li>选满 n-1 条边时 {@code finished = true}</li>
     * </ol>
     * 本步的 {@code currentVertex} 存放「当前考察边」的展示串（如 A-B）。
     * </p>
     *
     * @param graph       无向带权图
     * @param startVertex 本算法不使用，仅用于参数校验
     * @return 步进状态列表
     */
    @Override
    public List<StepState> executeStepByStep(Graph graph, String startVertex) {
        validateInput(graph, startVertex);

        List<StepState> steps = new ArrayList<>();

        List<MstEdge> remainingEdges = new ArrayList<>(graph.getAllUndirectedEdges());
        Collections.sort(remainingEdges);

        UnionFind unionFind = new UnionFind(graph.getVertices());
        List<MstEdge> mstEdges = new ArrayList<>();
        List<String> selectedEdgeDisplays = new ArrayList<>();

        int targetEdgeCount = graph.getVertexCount() - 1;

        // 第 0 步：初始状态
        steps.add(buildStepState(
                0,
                null,
                selectedEdgeDisplays,
                mstEdges,
                remainingEdges,
                false
        ));

        int stepNumber = 1;

        while (mstEdges.size() < targetEdgeCount && !remainingEdges.isEmpty()) {
            MstEdge currentEdge = remainingEdges.remove(0);

            boolean formsCycle = unionFind.find(currentEdge.getFrom())
                    .equals(unionFind.find(currentEdge.getTo()));

            if (!formsCycle) {
                unionFind.union(currentEdge.getFrom(), currentEdge.getTo());
                mstEdges.add(currentEdge);
                selectedEdgeDisplays.add(currentEdge.toDisplayString());
            }

            boolean finished = mstEdges.size() == targetEdgeCount;

            steps.add(buildStepState(
                    stepNumber,
                    currentEdge.toDisplayString(),
                    selectedEdgeDisplays,
                    mstEdges,
                    remainingEdges,
                    finished
            ));

            stepNumber++;
        }

        if (mstEdges.size() != targetEdgeCount) {
            throw new IllegalArgumentException("图不连通，无法生成最小生成树");
        }

        return steps;
    }

    /**
     * 构建一步的 {@link StepState} 快照。
     *
     * @param stepNumber            步序号
     * @param currentEdgeDisplay    当前考察边展示串，可为 null
     * @param selectedEdgeDisplays  已选边展示串列表
     * @param mstEdges              已选 MST 边
     * @param remainingEdges        尚未考察的边
     * @param finished              是否结束
     * @return 本步状态快照
     */
    private StepState buildStepState(int stepNumber,
                                     String currentEdgeDisplay,
                                     List<String> selectedEdgeDisplays,
                                     List<MstEdge> mstEdges,
                                     List<MstEdge> remainingEdges,
                                     boolean finished) {
        return new StepState(
                stepNumber,
                currentEdgeDisplay,
                new ArrayList<>(selectedEdgeDisplays),
                Collections.emptyList(),   // BFS 队列
                Collections.emptyList(),   // DFS 栈
                Collections.emptyMap(),    // Dijkstra 距离
                Collections.emptyList(),   // Dijkstra 优先队列
                Collections.emptyMap(),    // 拓扑入度
                new ArrayList<>(mstEdges),
                new ArrayList<>(remainingEdges),
                finished
        );
    }

    /**
     * 校验 Kruskal 算法的输入参数。
     *
     * @param graph       图对象
     * @param startVertex 起始节点（仅校验存在性）
     */
    private void validateInput(Graph graph, String startVertex) {
        if (graph == null) {
            throw new IllegalArgumentException("图对象不能为 null");
        }
        if (graph.isDirected()) {
            throw new IllegalArgumentException("Kruskal 算法仅适用于无向图，请设置 directed=false");
        }
        if (startVertex == null || startVertex.isBlank()) {
            throw new IllegalArgumentException("起始节点 ID 不能为空");
        }
        if (!graph.containsVertex(startVertex)) {
            throw new IllegalArgumentException("起始节点不存在于图中: " + startVertex);
        }
        if (graph.getVertexCount() < 1) {
            throw new IllegalArgumentException("图中没有节点");
        }
    }

    /**
     * 并查集，用于判断两点是否已在同一连通分量中。
     */
    private static class UnionFind {

        private final Map<String, String> parent;

        /**
         * 为每个节点初始化父节点为自己。
         *
         * @param vertices 图中所有节点
         */
        UnionFind(Iterable<String> vertices) {
            parent = new HashMap<>();
            for (String vertex : vertices) {
                parent.put(vertex, vertex);
            }
        }

        /**
         * 查找节点所属集合的代表元（带路径压缩）。
         *
         * @param vertex 节点 ID
         * @return 代表元
         */
        String find(String vertex) {
            String root = parent.get(vertex);
            if (!root.equals(vertex)) {
                root = find(root);
                parent.put(vertex, root);
            }
            return root;
        }

        /**
         * 合并两个节点所在的集合。
         *
         * @param left  一端节点
         * @param right 另一端节点
         */
        void union(String left, String right) {
            String leftRoot = find(left);
            String rightRoot = find(right);
            if (!leftRoot.equals(rightRoot)) {
                parent.put(leftRoot, rightRoot);
            }
        }
    }
}