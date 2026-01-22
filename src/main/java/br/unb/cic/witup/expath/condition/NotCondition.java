package br.unb.cic.witup.expath.condition;

import java.util.Objects;

public final class NotCondition implements PathCondition {
    private final PathCondition operand;

    public NotCondition(final PathCondition operand) {
        this.operand = Objects.requireNonNull(operand, "operand");
    }

    public PathCondition getOperand() {
        return operand;
    }

    @Override
    public String format() {
        return "!(" + operand.format() + ")";
    }
}
