package br.unb.cic.witup.expath.sootup;

import br.unb.cic.witup.expath.LocalExpath;
import br.unb.cic.witup.expath.LocalExpathExtractor;
import br.unb.cic.witup.expath.condition.AtomicCondition;
import br.unb.cic.witup.expath.condition.NotCondition;
import br.unb.cic.witup.expath.condition.OrCondition;
import br.unb.cic.witup.expath.condition.PathCondition;
import br.unb.cic.witup.expath.condition.TrueCondition;
import sootup.core.graph.StmtGraph;
import sootup.core.inputlocation.AnalysisInputLocation;
import sootup.core.jimple.common.stmt.JIfStmt;
import sootup.core.jimple.common.stmt.JThrowStmt;
import sootup.core.jimple.common.stmt.Stmt;
import sootup.core.model.Body;
import sootup.java.bytecode.frontend.inputlocation.JavaClassPathAnalysisInputLocation;
import sootup.java.core.JavaSootClass;
import sootup.java.core.JavaSootMethod;
import sootup.java.core.types.JavaClassType;
import sootup.java.core.views.JavaView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public final class SootUpLocalExpathExtractor implements LocalExpathExtractor {
    private final String classPath;

    public SootUpLocalExpathExtractor(final String classPath) {
        this.classPath = Objects.requireNonNull(classPath, "classPath");
    }

    @Override
    public List<LocalExpath> extract(final String className, final String methodSignature) {
        AnalysisInputLocation inputLocation = new JavaClassPathAnalysisInputLocation(classPath);
        JavaView view = new JavaView(inputLocation);
        JavaClassType classType = view.getIdentifierFactory().getClassType(className);
        JavaSootClass sootClass = view.getClass(classType)
                .orElseThrow(() -> new RuntimeException("Soot class not found: " + classType));

        Optional<JavaSootMethod> targetMethod = sootClass.getMethods().stream()
                .filter(method -> methodMatches(method, methodSignature))
                .findFirst();

        JavaSootMethod sootMethod = targetMethod
                .orElseThrow(() -> new RuntimeException("Method not found: " + methodSignature));

        return extractFromMethod(sootMethod);
    }

    private boolean methodMatches(final JavaSootMethod method, final String methodSignature) {
        if (method.getSignature().toString().equals(methodSignature)) {
            return true;
        }
        return method.getName().equals(methodSignature);
    }

    private List<LocalExpath> extractFromMethod(final JavaSootMethod sootMethod) {
        Body body = sootMethod.getBody();
        StmtGraph<?> graph = body.getStmtGraph();
        List<Stmt> orderedStatements = body.getStmts();
        List<LocalExpath> expaths = new ArrayList<>();

        for (Stmt stmt : graph) {
            if (!(stmt instanceof JThrowStmt throwStmt)) {
                continue;
            }
            List<PathCondition> incomingConditions = new ArrayList<>();
            for (Stmt pred : graph.getPredsOf(stmt)) {
                if (pred instanceof JIfStmt ifStmt) {
                    PathCondition condition = new AtomicCondition(ifStmt.getCondition().toString());
                    if (!isThrowOnTrueBranch(ifStmt, throwStmt, orderedStatements)) {
                        condition = new NotCondition(condition);
                    }
                    incomingConditions.add(condition);
                }
            }

            PathCondition pathCondition = buildPathCondition(incomingConditions);
            List<Stmt> pathNodes = buildPathNodes(orderedStatements, throwStmt);
            String exceptionType = throwStmt.getOp().getType().toString();

            expaths.add(new LocalExpath(
                    sootMethod.getSignature().toString(),
                    throwStmt,
                    pathNodes,
                    pathCondition,
                    exceptionType
            ));
        }

        return expaths;
    }

    private boolean isThrowOnTrueBranch(final JIfStmt ifStmt, final JThrowStmt throwStmt, final List<Stmt> orderedStatements) {
        Stmt target = ifStmt.getTarget();
        if (target.equals(throwStmt)) {
            return true;
        }
        int ifIndex = orderedStatements.indexOf(ifStmt);
        if (ifIndex >= 0 && ifIndex + 1 < orderedStatements.size()) {
            return orderedStatements.get(ifIndex + 1).equals(throwStmt);
        }
        return false;
    }

    private PathCondition buildPathCondition(final List<PathCondition> incomingConditions) {
        if (incomingConditions.isEmpty()) {
            return new TrueCondition();
        }
        if (incomingConditions.size() == 1) {
            return incomingConditions.get(0);
        }
        return new OrCondition(incomingConditions);
    }

    private List<Stmt> buildPathNodes(final List<Stmt> orderedStatements, final JThrowStmt throwStmt) {
        List<Stmt> pathNodes = orderedStatements.stream()
                .filter(stmt -> stmt instanceof JIfStmt || stmt.equals(throwStmt))
                .collect(Collectors.toList());
        if (!pathNodes.contains(throwStmt)) {
            pathNodes.add(throwStmt);
        }
        return pathNodes;
    }
}
