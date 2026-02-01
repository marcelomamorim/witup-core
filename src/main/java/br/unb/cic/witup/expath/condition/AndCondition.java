package br.unb.cic.witup.expath.condition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class AndCondition implements PathCondition {
    private final List<PathCondition> operands;

    public AndCondition(final List<PathCondition> operands) {
        Objects.requireNonNull(operands, "operands");
        this.operands = Collections.unmodifiableList(new ArrayList<>(operands));
    }

    public List<PathCondition> getOperands() {
        return operands;
    }

    @Override
    public String format() {
        return "(" + operands.stream().map(PathCondition::format).reduce((left, right) -> left + " && " + right).orElse("") + ")";
    }
}
