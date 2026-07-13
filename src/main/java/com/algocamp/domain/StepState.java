//BFS每一步的完整内部状态，供前端步进播放
package com.algocamp.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 图算法步进执行时，某一步的完整内部状态快照。
 * <p>
 * 用于算法工坊的前端步进可视化：前端根据本对象渲染
 * 「当前节点」「已访问节点」「BFS 队列」或「DFS 栈」等信息。
 * BFS 使用 {@link #queueContents}，DFS 使用 {@link #stackContents}，未使用的字段为空列表。
 * </p>
 *
 * @author AlgoCamp
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
     * @param stepNumber      步序号，从 0 开始
     * @param currentVertex   当前节点 ID，可为 null
     * @param visitedVertices 已访问节点列表，可为 null（视为空列表）
     * @param queueContents   BFS 队列内容，可为 null（视为空列表）
     * @param stackContents   DFS 栈内容，可 为 null（视为空列表）
     * @param finished        是否为结束步
     */
    public StepState(int stepNumber,
                 String currentVertex,
                 List<String> visitedVertices,
                 List<String> queueContents,
                 List<String> stackContents,
                 boolean finished) {
        this.stepNumber = stepNumber;
        this.currentVertex = currentVertex;
        this.visitedVertices = copyToUnmodifiableList(visitedVertices);
        this.queueContents = copyToUnmodifiableList(queueContents);
        this.stackContents = copyToUnmodifiableList(stackContents);
        this.finished = finished;
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

    @Override
    public String toString() {
        return "StepState{"
                + "stepNumber=" + stepNumber
                + ", currentVertex='" + currentVertex + '\''
                + ", visitedVertices=" + visitedVertices
                + ", queueContents=" + queueContents
                + ", stackContents=" + stackContents
                + ", finished=" + finished
                + '}';
    }
}
