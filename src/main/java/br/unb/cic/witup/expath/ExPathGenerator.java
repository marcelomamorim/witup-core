package br.unb.cic.witup.expath;

import br.unb.cic.witup.graph.WITUpGraph;
import br.unb.cic.witup.graph.edge.WITUpEdge;
import br.unb.cic.witup.graph.node.ThrowStatementNode;
import br.unb.cic.witup.graph.node.WITUpNode;
import br.unb.cic.witup.sootup.SootUpPropertyGraphs;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Generates local exception paths over a CFG-only graph.
 */
public class ExPathGenerator {

    public Map<WITUpNode, List<ExPath>> generateLocalExPaths(final SootUpPropertyGraphs graphs) {
        WITUpGraph cfgGraph = WITUpGraph.fromPropertyGraph(graphs.getCFG());
        Set<WITUpNode> entryNodes = cfgGraph.vertexSet().stream()
                .filter(node -> cfgGraph.inDegreeOf(node) == 0)
                .collect(Collectors.toSet());

        System.out.println("Generating local expaths for method: " + graphs.getMethodSignature());
        System.out.println("Entry nodes: " + entryNodes.size());

        Map<WITUpNode, List<ExPath>> pathsByThrow = new HashMap<>();
        for (WITUpNode node : cfgGraph.vertexSet()) {
            if (node instanceof ThrowStatementNode) {
                List<ExPath> paths = new ArrayList<>();
                for (WITUpNode entry : entryNodes) {
                    Deque<WITUpNode> currentPath = new ArrayDeque<>();
                    currentPath.add(entry);
                    Set<WITUpNode> visited = new HashSet<>();
                    visited.add(entry);
                    collectPaths(cfgGraph, entry, node, currentPath, visited, paths);
                }
                pathsByThrow.put(node, paths);
                System.out.println("Local expaths for throw node " + node.getNode() + ":");
                paths.forEach(path -> System.out.println("  " + path));
            }
        }
        System.out.println("Local expaths generated: " + pathsByThrow.size());
        return pathsByThrow;
    }

    private void collectPaths(
            final WITUpGraph graph,
            final WITUpNode current,
            final WITUpNode target,
            final Deque<WITUpNode> currentPath,
            final Set<WITUpNode> visited,
            final List<ExPath> paths) {
        if (current.equals(target)) {
            paths.add(new ExPath(new ArrayList<>(currentPath)));
            return;
        }
        for (WITUpEdge edge : graph.outgoingEdgesOf(current)) {
            WITUpNode next = graph.getEdgeTarget(edge);
            if (visited.contains(next)) {
                continue;
            }
            visited.add(next);
            currentPath.addLast(next);
            collectPaths(graph, next, target, currentPath, visited, paths);
            currentPath.removeLast();
            visited.remove(next);
        }
    }
}
