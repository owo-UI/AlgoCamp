//BFS每一步的完整内部状态，供前端步进播放
package com.algocamp.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * 图算法步进执行时，某一步的完整内部状态快照。
 * <p>
 * 不同算法使用不同字段：
 * <ul>
 *   <li>BFS — {@link #queueContents}</li>
 *   <li>DFS — {@link #stackContents}</li>
 *   <li>Dijkstra — {@link #distances} + {@link #priorityQueueContents}</li>
 *   <li>拓扑排序 — {@link #inDegrees} + {@link #queueContents}</li>
 *   <li>Prim / Kruskal — {@link #mstEdges} + {@link #candidateEdges}</li>
 * </ul>
 * 未使用的字段为空列表或空 Map。
 * </p>
 */
public class StepState {

    /**
     * 步序号，从 0 开始。
     * 0 通常表示初始状态（起点已入队、尚未出队处理）。
     */
    private final int stepNumber;

    /**
     * 当前正在处理的节点 ID。
     * 初始步或算法已结束时为 {@code null}。
     */
    private final String currentVertex;

    /**
     * 截至本步已访问的节点 ID 列表，按访问先后顺序排列。
     */
    private final List<String> visitedVertices;

    /**
     * BFS 队列在本步的快照，队头在列表最前，队尾在最后。
     */
    private final List<String> queueContents;

    /**
     * DFS 栈在本步的快照，栈底在列表最前，栈顶在最后。
     * BFS 算法不使用本字段，值为空列表。
     */
    private final List<String> stackContents;

    /**
     * 本步之后算法是否已结束（队列/栈为空且无可处理节点）。
     */
    private final boolean finished;

    /**
     * 构造一步的完整状态快照。
     *
     * @param stepNumber             步序号，从 0 开始
     * @param currentVertex          当前节点 ID，可为 null
     * @param visitedVertices        已访问节点列表，可为 null
     * @param queueContents          BFS 队列内容，可为 null
     * @param stackContents          DFS 栈内容，可为 null
     * @param distances              Dijkstra 距离表，可为 null
     * @param priorityQueueContents  Dijkstra 优先队列内容，可为 null
     * @param inDegrees              拓扑排序入度表，可为 null
     * @param finished               是否为结束步
     */
    public StepState(int stepNumber,
                     String currentVertex,
                     List<String> visitedVertices,
                     List<String> queueContents,
                     List<String> stackContents,
                     Map<String, Integer> distances,
                     List<String> priorityQueueContents,
                     Map<String, Integer> inDegrees,
                     List<MstEdge> mstEdges,
                     List<MstEdge> candidateEdges,
                     boolean finished) {
        this.stepNumber = stepNumber;
        this.currentVertex = currentVertex;
        this.visitedVertices = copyToUnmodifiableList(visitedVertices);
        this.queueContents = copyToUnmodifiableList(queueContents);
        this.stackContents = copyToUnmodifiableList(stackContents);
        this.distances = copyToUnmodifiableMap(distances);
        this.priorityQueueContents = copyToUnmodifiableList(priorityQueueContents);
        this.inDegrees = copyToUnmodifiableMap(inDegrees);
        this.mstEdges = copyToUnmodifiableMstEdgeList(mstEdges);
        this.candidateEdges = copyToUnmodifiableMstEdgeList(candidateEdges);
        this.finished = finished;
    }

    /**
     * 获取 Dijkstra 距离表快照。
     *
     * @return 不可变的距离映射（节点 ID → 最短距离）
     */
    public Map<String, Integer> getDistances() {
        return distances;
    }

    /**
     * 获取 Dijkstra 优先队列快照（距离小的在前）。
     *
     * @return 不可变的优先队列节点列表
     */
    public List<String> getPriorityQueueContents() {
        return priorityQueueContents;
    }

    /**
     * 获取步序号。
     *
     * @return 步序号，从 0 开始
     */
    public int getStepNumber() {
        return stepNumber;
    }

    /**
     * 获取当前正在处理的节点 ID。
     *
     * @return 当前节点 ID；无当前节点时返回 null
     */
    public String getCurrentVertex() {
        return currentVertex;
    }

    /**
     * 获取已访问节点列表（按访问顺序）。
     *
     * @return 不可变的已访问节点列表
     */
    public List<String> getVisitedVertices() {
        return visitedVertices;
    }

    /**
     * 获取 BFS 队列快照（队头在前）。
     *
     * @return 不可变的队列内容列表
     */
    public List<String> getQueueContents() {
        return queueContents;
    }
    /**
     * 获取 DFS 栈快照（栈底在前，栈顶在后）。
     *
     * @return 不可变的栈内容列表
     */
    public List<String> getStackContents() {
        return stackContents;
    }

    /**
     * 获取拓扑排序入度表快照。
     *
     * @return 不可变的入度映射（节点 ID → 入度）
     */
    public Map<String, Integer> getInDegrees() {
        return inDegrees;
    }

    /**
     * 获取已选入最小生成树的边列表。
     *
     * @return 不可变的 MST 边列表
     */
    public List<MstEdge> getMstEdges() {
        return mstEdges;
    }

    /**
     * 获取候选边列表快照。
     *
     * @return 不可变的候选边列表
     */
    public List<MstEdge> getCandidateEdges() {
        return candidateEdges;
    }


    /**
     * Dijkstra 算法中，各节点截至本步的已知最短距离。
     * key 为节点 ID，value 为距离值。
     * BFS、DFS 不使用本字段，值为空 Map。
     */
    private final Map<String, Integer> distances;

    /**
     * Dijkstra 优先队列在本步的快照，按距离从小到大排列。
     * 列表元素为节点 ID，距离可通过 {@link #distances} 查询。
     * BFS、DFS 不使用本字段，值为空列表。
     */
    private final List<String> priorityQueueContents;


    /**
     * 拓扑排序中，各节点截至本步的入度。
     * key 为节点 ID，value 为入度值。
     * 其他算法不使用本字段，值为空 Map。
     */
    private final Map<String, Integer> inDegrees;

    /**
     * 最小生成树算法中，截至本步已选入 MST 的边列表。
     * 其他算法不使用本字段，值为空列表。
     */
    private final List<MstEdge> mstEdges;

    /**
     * Prim 算法中候选边（横切边）快照，按权重从小到大排列。
     * Kruskal 可用来展示尚未处理的边；其他算法为空列表。
     */
    private final List<MstEdge> candidateEdges;

    /**
     * 判断本步是否为算法的最后一步。
     *
     * @return 已结束返回 true，否则 false
     */
    public boolean isFinished() {
        return finished;
    }

    /**
     * 将传入列表拷贝为不可变列表，防止外部修改内部状态。
     *
     * @param source 源列表，可为 null
     * @return 不可变列表副本；source 为 null 时返回空列表
     */
    private static List<String> copyToUnmodifiableList(List<String> source) {
        if (source == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(new ArrayList<>(source));
    }

    /**
     * 将传入 Map 拷贝为不可变 Map，防止外部修改内部状态。
     *
     * @param source 源 Map，可为 null
     * @return 不可变 Map 副本；source 为 null 时返回空 Map
     */
    private static Map<String, Integer> copyToUnmodifiableMap(Map<String, Integer> source) {
        if (source == null) {
            return Collections.emptyMap();
        }
        return Collections.unmodifiableMap(new HashMap<>(source));
    }

    /**
     * 将传入的 MST 边列表拷贝为不可变列表。
     *
     * @param source 源列表，可为 null
     * @return 不可变列表副本；source 为 null 时返回空列表
     */
    private static List<MstEdge> copyToUnmodifiableMstEdgeList(List<MstEdge> source) {
        if (source == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(new ArrayList<>(source));
    }

    @Override
    public String toString() {
        return "StepState{"
                + "stepNumber=" + stepNumber
                + ", currentVertex='" + currentVertex + '\''
                + ", visitedVertices=" + visitedVertices
                + ", queueContents=" + queueContents
                + ", stackContents=" + stackContents
                + ", distances=" + distances
                + ", priorityQueueContents=" + priorityQueueContents
                + ", inDegrees=" + inDegrees
                + ", mstEdges=" + mstEdges
                + ", candidateEdges=" + candidateEdges
                + ", finished=" + finished
                + '}';
    }
}


