package com.algocamp.algo_camp;

import com.algocamp.algorithm.impl.DijkstraStepper;
import com.algocamp.algorithm.impl.TopologicalSortStepper;
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
        Graph graph = new Graph(true);
        graph.addEdge("A", "B");
        graph.addEdge("A", "C");
        graph.addEdge("B", "D");
        graph.addEdge("C", "D");

        TopologicalSortStepper topo = new TopologicalSortStepper();
        for (StepState step : topo.executeStepByStep(graph, "A")) {
            System.out.println(step);
        }


    }
}
