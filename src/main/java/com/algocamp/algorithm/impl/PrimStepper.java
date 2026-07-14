package com.algocamp.algorithm.impl;

import com.algocamp.algorithm.AlgorithmStepper;
import com.algocamp.algorithm.AlgorithmType;
import com.algocamp.domain.Graph;
import com.algocamp.domain.MstEdge;
import com.algocamp.domain.StepState;
import com.algocamp.domain.WeightedEdge;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.Collections;
import java.util.Map;

/**
 * Prim 最小生成树算法的步进实现类。
 * <p>
 * 从指定起点出发，每次选择连接「树内节点」与「树外节点」的最小权边扩展 MST。
 * 仅适用于<strong>无向连通带权图</strong>。
 * </p>
 *
 * @author AlgoCamp
 */
public class PrimStepper implements AlgorithmStepper {

    @Override
    public AlgorithmType getAlgorithmType() {
        return AlgorithmType.PRIM;
    }

    /**
     * 执行 Prim 算法，返回节点加入最小生成树的顺序。
     *
     * @param graph       待处理的无向带权图
     * @param startVertex 起始节点 ID
     * @return 节点加入 MST 的先后顺序
     */
    @Override
    public List<String> getVisitOrder(Graph graph, String startVertex) {
        validateInput(graph, startVertex);

        Set<String> verticesInMst = new HashSet<>();
        List<String> joinOrder = new ArrayList<>();
        List<MstEdge> mstEdges = new ArrayList<>();

        // 候选横切边：按权重升序
        PriorityQueue<MstEdge> candidateEdges = new PriorityQueue<>();

        // 起点加入 MST
        verticesInMst.add(startVertex);
        joinOrder.add(startVertex);
        addOutgoingCandidates(graph, startVertex, verticesInMst, candidateEdges);

        while (verticesInMst.size() < graph.getVertexCount() && !candidateEdges.isEmpty()) {
            MstEdge selectedEdge = candidateEdges.poll();
            if (selectedEdge == null) {
                break;
            }

            // 两端都已在树内 → 会成环，跳过
            boolean fromInMst = verticesInMst.contains(selectedEdge.getFrom());
            boolean toInMst = verticesInMst.contains(selectedEdge.getTo());
            if (fromInMst && toInMst) {
                continue;
            }

            // 确定新加入的节点
            String newVertex = fromInMst ? selectedEdge.getTo() : selectedEdge.getFrom();
            verticesInMst.add(newVertex);
            joinOrder.add(newVertex);
            mstEdges.add(selectedEdge);

            // 将新节点连到树外的边加入候选
            addOutgoingCandidates(graph, newVertex, verticesInMst, candidateEdges);
        }

        if (verticesInMst.size() != graph.getVertexCount()) {
            throw new IllegalArgumentException("图不连通，无法生成最小生成树");
        }

        return joinOrder;
    }

    /**
     * 以步进方式执行 Prim，返回每一步的完整内部状态快照。
     * <p>
     * 步进规则：
     * <ol>
     *   <li>第 0 步：起点入树，装入初始候选边</li>
     *   <li>后续每步：选出权重最小的有效横切边 → 新节点入树 → 更新候选边</li>
     *   <li>最后一步：MST 覆盖全部节点时 {@code finished = true}</li>
     * </ol>
     * </p>
     *
     * @param graph       无向带权图
     * @param startVertex 起始节点 ID
     * @return 步进状态列表
     */
    @Override
    public List<StepState> executeStepByStep(Graph graph, String startVertex) {
        validateInput(graph, startVertex);

        List<StepState> steps = new ArrayList<>();

        Set<String> verticesInMst = new HashSet<>();
        List<String> joinOrder = new ArrayList<>();
        List<MstEdge> mstEdges = new ArrayList<>();
        PriorityQueue<MstEdge> candidateEdges = new PriorityQueue<>();

        verticesInMst.add(startVertex);
        joinOrder.add(startVertex);
        addOutgoingCandidates(graph, startVertex, verticesInMst, candidateEdges);

        // 第 0 步：初始状态
        steps.add(buildStepState(
                0,
                startVertex,
                joinOrder,
                mstEdges,
                candidateEdges,
                false
        ));

        int stepNumber = 1;

        while (verticesInMst.size() < graph.getVertexCount() && !candidateEdges.isEmpty()) {
            MstEdge selectedEdge = candidateEdges.poll();
            if (selectedEdge == null) {
                break;
            }

            boolean fromInMst = verticesInMst.contains(selectedEdge.getFrom());
            boolean toInMst = verticesInMst.contains(selectedEdge.getTo());
            if (fromInMst && toInMst) {
                continue; // 过期边，不记步
            }

            String newVertex = fromInMst ? selectedEdge.getTo() : selectedEdge.getFrom();
            verticesInMst.add(newVertex);
            joinOrder.add(newVertex);
            mstEdges.add(selectedEdge);
            addOutgoingCandidates(graph, newVertex, verticesInMst, candidateEdges);

            boolean finished = verticesInMst.size() == graph.getVertexCount();

            steps.add(buildStepState(
                    stepNumber,
                    newVertex,
                    joinOrder,
                    mstEdges,
                    candidateEdges,
                    finished
            ));

            stepNumber++;
        }

        if (verticesInMst.size() != graph.getVertexCount()) {
            throw new IllegalArgumentException("图不连通，无法生成最小生成树");
        }

        return steps;
    }

    /**
     * 将指定节点连向「尚未进入 MST」的邻居边加入候选优先队列。
     *
     * @param graph           图
     * @param vertex          当前树内节点
     * @param verticesInMst   已在 MST 中的节点集合
     * @param candidateEdges  候选边优先队列
     */
    private void addOutgoingCandidates(Graph graph,
                                       String vertex,
                                       Set<String> verticesInMst,
                                       PriorityQueue<MstEdge> candidateEdges) {
        for (WeightedEdge edge : graph.getWeightedNeighbors(vertex)) {
            if (!verticesInMst.contains(edge.getTo())) {
                candidateEdges.offer(new MstEdge(vertex, edge.getTo(), edge.getWeight()));
            }
        }
    }

    /**
     * 构建一步的 {@link StepState} 快照。
     */
    private StepState buildStepState(int stepNumber,
                                     String currentVertex,
                                     List<String> joinOrder,
                                     List<MstEdge> mstEdges,
                                     PriorityQueue<MstEdge> candidateEdges,
                                     boolean finished) {
        return new StepState(
                stepNumber,
                currentVertex,
                new ArrayList<>(joinOrder),
                Collections.emptyList(),   // BFS 队列
                Collections.emptyList(),   // DFS 栈
                Collections.emptyMap(),    // Dijkstra 距离
                Collections.emptyList(),   // Dijkstra 优先队列
                Collections.emptyMap(),    // 拓扑入度
                new ArrayList<>(mstEdges),
                snapshotCandidateEdges(candidateEdges),
                finished
        );
    }

    /**
     * 拷贝候选边优先队列，并按权重升序排列后返回。
     * <p>
     * {@link PriorityQueue} 内部无序，因此先拷贝再排序，便于前端展示。
     * </p>
     *
     * @param candidateEdges 候选边优先队列
     * @return 按权重升序的候选边列表
     */
    private List<MstEdge> snapshotCandidateEdges(PriorityQueue<MstEdge> candidateEdges) {
        List<MstEdge> snapshot = new ArrayList<>(candidateEdges);
        Collections.sort(snapshot);
        return snapshot;
    }

    /**
     * 校验 Prim 算法的输入参数。
     *
     * @param graph       图对象
     * @param startVertex 起始节点 ID
     * @throws IllegalArgumentException 参数不合法时抛出
     */
    private void validateInput(Graph graph, String startVertex) {
        if (graph == null) {
            throw new IllegalArgumentException("图对象不能为 null");
        }
        if (graph.isDirected()) {
            throw new IllegalArgumentException("Prim 算法仅适用于无向图，请设置 directed=false");
        }
        if (startVertex == null || startVertex.isBlank()) {
            throw new IllegalArgumentException("起始节点 ID 不能为空");
        }
        if (!graph.containsVertex(startVertex)) {
            throw new IllegalArgumentException("起始节点不存在于图中: " + startVertex);
        }
        if (graph.getVertexCount() == 0) {
            throw new IllegalArgumentException("图中没有节点");
        }
    }
}