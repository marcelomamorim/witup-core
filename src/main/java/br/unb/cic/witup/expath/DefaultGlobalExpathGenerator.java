package br.unb.cic.witup.expath;

import br.unb.cic.witup.expath.sootup.SootUpLocalExpathExtractor;
import sootup.core.inputlocation.AnalysisInputLocation;
import sootup.java.bytecode.frontend.inputlocation.JavaClassPathAnalysisInputLocation;
import sootup.java.core.JavaSootClass;
import sootup.java.core.JavaSootMethod;
import sootup.java.core.types.JavaClassType;
import sootup.java.core.views.JavaView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public final class DefaultGlobalExpathGenerator implements GlobalExpathGenerator {
    private final String classPath;
    private final LocalExpathExtractor localExpathExtractor;

    public DefaultGlobalExpathGenerator(final String classPath) {
        this(classPath, new SootUpLocalExpathExtractor(classPath));
    }

    public DefaultGlobalExpathGenerator(final String classPath, final LocalExpathExtractor localExpathExtractor) {
        this.classPath = Objects.requireNonNull(classPath, "classPath");
        this.localExpathExtractor = Objects.requireNonNull(localExpathExtractor, "localExpathExtractor");
    }

    @Override
    public List<GlobalExpath> generateForClass(final String className) {
        AnalysisInputLocation inputLocation = new JavaClassPathAnalysisInputLocation(classPath);
        JavaView view = new JavaView(inputLocation);
        JavaClassType classType = view.getIdentifierFactory().getClassType(className);
        JavaSootClass sootClass = view.getClass(classType)
                .orElseThrow(() -> new RuntimeException("Soot class not found: " + classType));

        return sootClass.getMethods().stream()
                .flatMap(method -> generateForMethod(className, method.getSignature().toString()).stream())
                .collect(Collectors.toList());
    }

    @Override
    public List<GlobalExpath> generateForMethod(final String className, final String methodSignature) {
        List<LocalExpath> locals = localExpathExtractor.extract(className, methodSignature);
        return fromLocalExpaths(locals);
    }

    @Override
    public List<GlobalExpath> fromLocalExpaths(final List<LocalExpath> locals) {
        List<GlobalExpath> globals = new ArrayList<>();
        for (LocalExpath local : locals) {
            List<String> callStack = List.of(local.getMethodId());
            GlobalPathCondition condition = new GlobalPathCondition(local.getPathCondition());
            globals.add(new GlobalExpath(
                    callStack,
                    local.getPathNodes(),
                    condition,
                    local.getExceptionType(),
                    local.getMethodId(),
                    ExpathStatus.UNKNOWN
            ));
        }
        return globals;
    }
}
