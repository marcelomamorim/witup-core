package br.unb.cic.witup.expath.sootup;

import br.unb.cic.witup.expath.LocalExpath;
import br.unb.cic.witup.expath.LocalExpathExtractor;
import br.unb.cic.witup.expath.condition.AtomicCondition;
import br.unb.cic.witup.expath.condition.PathCondition;
import br.unb.cic.witup.expath.condition.TrueCondition;
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
        List<Stmt> orderedStatements = body.getStmts();
        List<LocalExpath> expaths = new ArrayList<>();

        for (Stmt stmt : orderedStatements) {
            if (!(stmt instanceof JThrowStmt throwStmt)) {
                continue;
            }
            PathCondition pathCondition = findConditionForThrow(orderedStatements, throwStmt);
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

    private PathCondition findConditionForThrow(final List<Stmt> orderedStatements, final JThrowStmt throwStmt) {
        int throwIndex = orderedStatements.indexOf(throwStmt);
        for (int index = throwIndex - 1; index >= 0; index--) {
            Stmt candidate = orderedStatements.get(index);
            if (candidate instanceof JIfStmt ifStmt) {
                return new AtomicCondition(ifStmt.getCondition().toString());
            }
        }
        return new TrueCondition();
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
