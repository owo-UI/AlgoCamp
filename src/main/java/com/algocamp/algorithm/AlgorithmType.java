//算法类型枚举（避免魔法字符串）
package com.algocamp.algorithm;

/**
 * 算法工坊支持的图算法类型枚举。
 * <p>
 * 用于标识算法实现类、REST 接口路由参数，以及前端算法选择器展示。
 * 避免在代码中散落 "BFS"、"DFS" 等魔法字符串。
 * </p>
 *
 * @author AlgoCamp
 */
public enum AlgorithmType {

    /** 广度优先搜索（Breadth-First Search） */
    BFS("BFS", "广度优先搜索"),

    /** 深度优先搜索（Depth-First Search）—— 预留，后续实现 */
    DFS("DFS", "深度优先搜索"),

    /** Dijkstra 最短路径 —— 预留，后续实现 */
    DIJKSTRA("DIJKSTRA", "Dijkstra 最短路径");

    /**
     * 算法代码，用于 API 传参（如 ?algorithm=BFS）。
     */
    private final String code;

    /**
     * 算法中文显示名称，供前端展示。
     */
    private final String displayName;

    /**
     * 构造算法类型枚举项。
     *
     * @param code        算法代码
     * @param displayName 中文显示名称
     */
    AlgorithmType(String code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }

    /**
     * 获取算法代码。
     *
     * @return 算法代码字符串
     */
    public String getCode() {
        return code;
    }

    /**
     * 获取算法中文显示名称。
     *
     * @return 中文名称
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * 根据算法代码查找对应的枚举值（忽略大小写）。
     *
     * @param code 算法代码，如 "BFS"
     * @return 匹配的 AlgorithmType
     * @throws IllegalArgumentException 若 code 无效或为 null
     */
    public static AlgorithmType fromCode(String code) {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("算法代码不能为空");
        }
        for (AlgorithmType type : values()) {
            if (type.code.equalsIgnoreCase(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("不支持的算法类型: " + code);
    }
}