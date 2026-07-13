package com.algocamp.algo_camp;

import com.algocamp.algorithm.impl.DijkstraStepper;
import com.algocamp.controller.dto.EdgeRequest;
import com.algocamp.controller.dto.GraphAlgorithmRequest;
import com.algocamp.domain.Graph;
import com.algocamp.domain.StepState;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class AlgoCampApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    void testBuildGraph() {
        Graph graph = new Graph(false);
        graph.addWeightedEdge("A", "B", 4);
        graph.addWeightedEdge("A", "C", 2);
        graph.addWeightedEdge("B", "D", 5);
        graph.addWeightedEdge("C", "D", 1);

        DijkstraStepper dijkstra = new DijkstraStepper();
        for (StepState step : dijkstra.executeStepByStep(graph, "A")) {
            System.out.println(step);
        }
    }
}
