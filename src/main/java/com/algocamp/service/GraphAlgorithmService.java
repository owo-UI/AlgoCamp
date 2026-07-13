package com.algocamp.service;

import com.algocamp.algorithm.AlgorithmStepper;
import com.algocamp.algorithm.AlgorithmType;
import com.algocamp.algorithm.impl.BfsStepper;
import com.algocamp.common.Result;
import com.algocamp.common.ResultCode;
import com.algocamp.domain.Graph;
import com.algocamp.domain.StepState;
import com.algocamp.algorithm.impl.DfsStepper;
import com.algocamp.algorithm.impl.DijkstraStepper;
import com.algocamp.algorithm.impl.TopologicalSortStepper;
import org.springframework.stereotype.Service;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * 图算法工坊的业务逻辑服务。
 * <p>
 * 负责根据算法类型选择对应的 {@link AlgorithmStepper} 实现，
 * 执行图算法并将结果包装为统一的 {@link Result} 返回。
 * Controller 层不直接调用算法实现类，而是通过本 Service 间接调用。
 * </p>
 *
 * @author AlgoCamp
 */
@Service
public class GraphAlgorithmService {

    /**
     * 算法类型 → 算法实现 的注册表。
     * 使用 EnumMap 保证查找效率，并与 AlgorithmType 枚举一一对应。
     */
    private final Map<AlgorithmType, AlgorithmStepper> stepperRegistry;

    /**
     * 构造方法：注册当前已实现的所有算法。
     */
    public GraphAlgorithmService() {
        stepperRegistry = new EnumMap<>(AlgorithmType.class);
        stepperRegistry.put(AlgorithmType.BFS, new BfsStepper());
        stepperRegistry.put(AlgorithmType.DFS, new DfsStepper());
        stepperRegistry.put(AlgorithmType.DIJKSTRA, new DijkstraStepper());
        stepperRegistry.put(AlgorithmType.TOPOLOGICAL_SORT, new TopologicalSortStepper());
    }

    /**
     * 执行图算法，返回节点的访问顺序。
     *
     * @param graph         待遍历的图
     * @param startVertex   起始节点 ID
     * @param algorithmType 算法类型
     * @return 统一结果，成功时 data 为访问顺序列表
     */
    public Result<List<String>> getVisitOrder(Graph graph,
                                              String startVertex,
                                              AlgorithmType algorithmType) {
        try {
            AlgorithmStepper stepper = resolveStepper(algorithmType);
            List<String> visitOrder = stepper.getVisitOrder(graph, startVertex);
            return Result.success(visitOrder);
        } catch (IllegalArgumentException ex) {
            // 参数校验失败（如图为空、起点不存在、算法未实现）
            return Result.fail(ex.getMessage());
        } catch (Exception ex) {
            // 未预期的异常
            return Result.fail(ResultCode.INTERNAL_ERROR, "算法执行失败: " + ex.getMessage());
        }
    }

    /**
     * 以步进方式执行图算法，返回每一步的完整状态快照列表。
     *
     * @param graph         待遍历的图
     * @param startVertex   起始节点 ID
     * @param algorithmType 算法类型
     * @return 统一结果，成功时 data 为步进状态列表
     */
    public Result<List<StepState>> executeStepByStep(Graph graph,
                                                     String startVertex,
                                                     AlgorithmType algorithmType) {
        try {
            AlgorithmStepper stepper = resolveStepper(algorithmType);
            List<StepState> steps = stepper.executeStepByStep(graph, startVertex);
            return Result.success(steps);
        } catch (IllegalArgumentException ex) {
            return Result.fail(ex.getMessage());
        } catch (Exception ex) {
            return Result.fail(ResultCode.INTERNAL_ERROR, "算法执行失败: " + ex.getMessage());
        }
    }

    /**
     * 根据算法类型从注册表中获取对应的算法实现。
     *
     * @param algorithmType 算法类型枚举
     * @return 对应的 AlgorithmStepper 实现
     * @throws IllegalArgumentException 若算法类型为 null 或尚未实现
     */
    private AlgorithmStepper resolveStepper(AlgorithmType algorithmType) {
        if (algorithmType == null) {
            throw new IllegalArgumentException("算法类型不能为空");
        }
        AlgorithmStepper stepper = stepperRegistry.get(algorithmType);
        if (stepper == null) {
            throw new IllegalArgumentException(
                    "算法尚未实现: " + algorithmType.getDisplayName()
            );
        }
        return stepper;
    }
}