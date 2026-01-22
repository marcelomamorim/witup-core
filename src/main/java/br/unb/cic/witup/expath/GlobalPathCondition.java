package br.unb.cic.witup.expath;

import br.unb.cic.witup.expath.condition.PathCondition;

import java.util.Objects;

public final class GlobalPathCondition {
    private final PathCondition condition;

    public GlobalPathCondition(final PathCondition condition) {
        this.condition = Objects.requireNonNull(condition, "condition");
    }

    public PathCondition getCondition() {
        return condition;
    }

    @Override
    public String toString() {
        return condition.format();
    }
}
