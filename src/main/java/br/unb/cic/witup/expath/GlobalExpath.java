package br.unb.cic.witup.expath;

import sootup.core.jimple.common.stmt.Stmt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class GlobalExpath {
    private final List<String> callStack;
    private final List<Stmt> pathNodes;
    private final GlobalPathCondition globalCondition;
    private final String exceptionType;
    private final String originMethod;
    private final ExpathStatus status;

    public GlobalExpath(
            final List<String> callStack,
            final List<Stmt> pathNodes,
            final GlobalPathCondition globalCondition,
            final String exceptionType,
            final String originMethod,
            final ExpathStatus status
    ) {
        this.callStack = Collections.unmodifiableList(new ArrayList<>(Objects.requireNonNull(callStack, "callStack")));
        this.pathNodes = Collections.unmodifiableList(new ArrayList<>(Objects.requireNonNull(pathNodes, "pathNodes")));
        this.globalCondition = Objects.requireNonNull(globalCondition, "globalCondition");
        this.exceptionType = Objects.requireNonNull(exceptionType, "exceptionType");
        this.originMethod = Objects.requireNonNull(originMethod, "originMethod");
        this.status = Objects.requireNonNull(status, "status");
    }

    public List<String> getCallStack() {
        return callStack;
    }

    public List<Stmt> getPathNodes() {
        return pathNodes;
    }

    public GlobalPathCondition getGlobalCondition() {
        return globalCondition;
    }

    public String getExceptionType() {
        return exceptionType;
    }

    public String getOriginMethod() {
        return originMethod;
    }

    public ExpathStatus getStatus() {
        return status;
    }
}
