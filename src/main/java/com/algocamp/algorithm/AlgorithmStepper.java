//步进算法统一接口
package com.algocamp.algorithm;

import com.algocamp.domain.Graph;
import com.algocamp.domain.StepState;

import java.util.List;

/**
 * 支持步进可视化的图算法统一接口。
 * <p>
 * 每种算法（BFS、DFS 等）对应一个实现类，放在 {@code algorithm.impl} 包下。
 * Service 层通过本接口调用算法，而不依赖具体实现，便于扩展新算法。
 * </p>
 *
 * @author AlgoCamp
 */
public interface AlgorithmStepper {

    /**
     * 返回本实现类对应的算法类型。
     *
     * @return 算法类型枚举
     */
    AlgorithmType getAlgorithmType();

    /**
     * 一次性执行算法，返回节点的访问顺序。
     * <p>
     * 适用于只需最终结果、不需要步进可视化的场景。
     * </p>
     *
     * @param graph       待遍历的图，不能为 null
     * @param startVertex 起始节点 ID，不能为 null 或空
     * @return 按访问先后顺序排列的节点 ID 列表
     * @throws IllegalArgumentException 若参数无效或起始节点不在图中
     */
    List<String> getVisitOrder(Graph graph, String startVertex);

    /**
     * 以步进方式执行算法，返回每一步的完整内部状态快照列表。
     * <p>
     * 适用于算法工坊的前端步进可视化：前端逐步播放 {@link StepState} 列表，
     * 展示当前节点、已访问集合、队列（或栈）等内容的变化。
     * </p>
     *
     * @param graph       待遍历的图，不能为 null
     * @param startVertex 起始节点 ID，不能为 null 或空
     * @return 按执行顺序排列的步进状态列表；第 0 步为初始状态
     * @throws IllegalArgumentException 若参数无效或起始节点不在图中
     */
    List<StepState> executeStepByStep(Graph graph, String startVertex);
}