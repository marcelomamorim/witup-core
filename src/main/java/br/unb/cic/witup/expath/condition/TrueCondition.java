package br.unb.cic.witup.expath.condition;

public final class TrueCondition implements PathCondition {
    @Override
    public String format() {
        return "true";
    }
}
