package com.algocamp.algo_camp;

import com.algocamp.algorithm.impl.DijkstraStepper;
import com.algocamp.algorithm.impl.KruskalStepper;
import com.algocamp.algorithm.impl.PrimStepper;
import com.algocamp.algorithm.impl.TopologicalSortStepper;
import com.algocamp.controller.dto.EdgeRequest;
import com.algocamp.controller.dto.GraphAlgorithmRequest;
import com.algocamp.domain.Graph;
import com.algocamp.domain.MstEdge;
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
    void testPrim() {
        Graph graph = new Graph(false);
        graph.addWeightedEdge("A", "B", 1);
        graph.addWeightedEdge("B", "D", 2);
        graph.addWeightedEdge("A", "C", 3);
        graph.addWeightedEdge("C", "D", 4);
        graph.addWeightedEdge("A", "D", 5);

        KruskalStepper kruskal = new KruskalStepper();
        for (StepState step : kruskal.executeStepByStep(graph, "A")) {
            System.out.println(step);
        }

    }
}
