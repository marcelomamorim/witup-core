package br.unb.cic.witup.expath;

import br.unb.cic.witup.graph.node.WITUpNode;

import java.util.List;
import java.util.Objects;

/**
 * Represents a single exception path as an ordered list of nodes.
 */
public final class ExPath {
    private final List<WITUpNode> nodes;

    public ExPath(final List<WITUpNode> nodes) {
        this.nodes = List.copyOf(nodes);
    }

    public List<WITUpNode> getNodes() {
        return nodes;
    }

    public WITUpNode getEndNode() {
        return nodes.get(nodes.size() - 1);
    }

    public int size() {
        return nodes.size();
    }

    @Override
    public String toString() {
        return nodes.stream()
                .map(node -> node.getNode().toString())
                .reduce((left, right) -> left + " -> " + right)
                .orElse("");
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ExPath exPath = (ExPath) o;
        return Objects.equals(nodes, exPath.nodes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nodes);
    }
}
