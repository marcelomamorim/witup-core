package br.unb.cic.witup.expath.condition;

import java.util.Objects;

public final class AtomicCondition implements PathCondition {
    private final String expression;

    public AtomicCondition(final String expression) {
        this.expression = Objects.requireNonNull(expression, "expression");
    }

    public String getExpression() {
        return expression;
    }

    @Override
    public String format() {
        return expression;
    }
}
