package com.algocamp.controller;

import com.algocamp.algorithm.AlgorithmType;
import com.algocamp.common.Result;
import com.algocamp.controller.dto.GraphAlgorithmRequest;
import com.algocamp.domain.Graph;
import com.algocamp.domain.StepState;
import com.algocamp.service.GraphAlgorithmService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 算法工坊的 REST API 控制器。
 * <p>
 * 提供图算法相关的 HTTP 接口，供前端算法工坊模块调用。
 * 所有接口返回统一的 {@link Result} 包装结构。
 * </p>
 *
 * @author AlgoCamp
 */
@RestController
@RequestMapping("/api/workshop/algorithm")
public class GraphAlgorithmController {

    /**
     * 图算法业务服务，通过构造器注入。
     */
    private final GraphAlgorithmService graphAlgorithmService;

    /**
     * 构造器注入 GraphAlgorithmService。
     *
     * @param graphAlgorithmService 图算法业务服务
     */
    public GraphAlgorithmController(GraphAlgorithmService graphAlgorithmService) {
        this.graphAlgorithmService = graphAlgorithmService;
    }

    /**
     * 执行图算法，返回节点的访问顺序。
     * <p>
     * 请求示例：
     * <pre>{@code
     * POST /api/workshop/algorithm/visit-order
     * {
     *   "directed": false,
     *   "edges": [
     *     { "from": "A", "to": "B" },
     *     { "from": "A", "to": "C" },
     *     { "from": "B", "to": "D" }
     *   ],
     *   "startVertex": "A",
     *   "algorithm": "BFS"
     * }
     * }</pre>
     * </p>
     *
     * @param request 图算法执行请求体
     * @return 统一结果，成功时 data 为访问顺序列表
     */
    @PostMapping("/visit-order")
    public Result<List<String>> getVisitOrder(@RequestBody GraphAlgorithmRequest request) {
        Result<AlgorithmType> algorithmTypeResult = parseAlgorithmType(request);
        if (!algorithmTypeResult.isSuccess()) {
            return Result.fail(algorithmTypeResult.getMessage());
        }

        Graph graph = request.buildGraph();
        return graphAlgorithmService.getVisitOrder(
                graph,
                request.getStartVertex(),
                algorithmTypeResult.getData()
        );
    }

    /**
     * 以步进方式执行图算法，返回每一步的完整状态快照。
     * <p>
     * 请求体格式与 {@link #getVisitOrder} 相同。
     * 成功时 data 为 {@link StepState} 列表，前端可逐步播放可视化。
     * </p>
     *
     * @param request 图算法执行请求体
     * @return 统一结果，成功时 data 为步进状态列表
     */
    @PostMapping("/steps")
    public Result<List<StepState>> executeStepByStep(@RequestBody GraphAlgorithmRequest request) {
        Result<AlgorithmType> algorithmTypeResult = parseAlgorithmType(request);
        if (!algorithmTypeResult.isSuccess()) {
            return Result.fail(algorithmTypeResult.getMessage());
        }

        Graph graph = request.buildGraph();
        return graphAlgorithmService.executeStepByStep(
                graph,
                request.getStartVertex(),
                algorithmTypeResult.getData()
        );
    }

    /**
     * 解析并校验请求中的算法类型和起始节点。
     *
     * @param request 图算法执行请求体
     * @return 成功时 data 为 AlgorithmType；失败时 code 为 400
     */
    private Result<AlgorithmType> parseAlgorithmType(GraphAlgorithmRequest request) {
        if (request == null) {
            return Result.fail("请求体不能为空");
        }
        if (request.getStartVertex() == null || request.getStartVertex().isBlank()) {
            return Result.fail("起始节点 startVertex 不能为空");
        }
        if (request.getAlgorithm() == null || request.getAlgorithm().isBlank()) {
            return Result.fail("算法类型 algorithm 不能为空");
        }
        try {
            AlgorithmType algorithmType = AlgorithmType.fromCode(request.getAlgorithm());
            return Result.success(algorithmType);
        } catch (IllegalArgumentException ex) {
            return Result.fail(ex.getMessage());
        }
    }
}