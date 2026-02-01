package br.unb.cic.witup.expath;

import br.unb.cic.witup.expath.condition.PathCondition;
import sootup.core.jimple.common.stmt.Stmt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class LocalExpath {
    private final String methodId;
    private final Stmt throwStmt;
    private final List<Stmt> pathNodes;
    private final PathCondition pathCondition;
    private final String exceptionType;

    public LocalExpath(
            final String methodId,
            final Stmt throwStmt,
            final List<Stmt> pathNodes,
            final PathCondition pathCondition,
            final String exceptionType
    ) {
        this.methodId = Objects.requireNonNull(methodId, "methodId");
        this.throwStmt = Objects.requireNonNull(throwStmt, "throwStmt");
        this.pathNodes = Collections.unmodifiableList(new ArrayList<>(Objects.requireNonNull(pathNodes, "pathNodes")));
        this.pathCondition = Objects.requireNonNull(pathCondition, "pathCondition");
        this.exceptionType = Objects.requireNonNull(exceptionType, "exceptionType");
    }

    public String getMethodId() {
        return methodId;
    }

    public Stmt getThrowStmt() {
        return throwStmt;
    }

    public List<Stmt> getPathNodes() {
        return pathNodes;
    }

    public PathCondition getPathCondition() {
        return pathCondition;
    }

    public String getExceptionType() {
        return exceptionType;
    }
}
