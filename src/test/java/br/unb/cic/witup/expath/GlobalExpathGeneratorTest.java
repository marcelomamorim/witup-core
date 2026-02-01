package br.unb.cic.witup.expath;

import br.unb.cic.witup.expath.sootup.SootUpLocalExpathExtractor;
import br.unb.cic.witup.expath.util.JvmTestCompilationHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

class GlobalExpathGeneratorTest {
    private static final String MATH_CLASS = "br.unb.cic.witup.samples.Math";

    private DefaultGlobalExpathGenerator generator;
    private SootUpLocalExpathExtractor localExtractor;

    @BeforeEach
    void setUp() {
        String classPath = JvmTestCompilationHelper.testClassesPath();
        localExtractor = new SootUpLocalExpathExtractor(classPath);
        generator = new DefaultGlobalExpathGenerator(classPath, localExtractor);
    }

    @Test
    void circleAreaGeneratesSingleGlobalExpath() {
        String signature = "<br.unb.cic.witup.samples.Math: double circleArea()>";

        List<GlobalExpath> globals = generator.generateForMethod(MATH_CLASS, signature);
        GlobalExpathAssertions.assertCount(globals, 1);

        GlobalExpath expath = globals.get(0);
        GlobalExpathAssertions.assertThrows(expath, "RuntimeException");
        GlobalExpathAssertions.assertOriginMethod(expath, MATH_CLASS, "circleArea");
        GlobalExpathAssertions.assertHasCondition(expath, "radius < 0");
        GlobalExpathAssertions.assertEndsWithThrow(expath);
        GlobalExpathAssertions.assertAllSatOrUnknown(globals);

        List<LocalExpath> locals = localExtractor.extract(MATH_CLASS, signature);
        List<GlobalExpath> globalsFromLocal = generator.fromLocalExpaths(locals);
        GlobalExpathAssertions.assertEquivalentConditions(expath, globalsFromLocal.get(0));
    }

    @Test
    void invalidMethodParameterGeneratesSingleGlobalExpath() {
        String signature = "<br.unb.cic.witup.samples.Math: int invalidMethodParameter(int,int)>";

        List<GlobalExpath> globals = generator.generateForMethod(MATH_CLASS, signature);
        GlobalExpathAssertions.assertCount(globals, 1);

        GlobalExpath expath = globals.get(0);
        GlobalExpathAssertions.assertThrows(expath, "RuntimeException");
        GlobalExpathAssertions.assertOriginMethod(expath, MATH_CLASS, "invalidMethodParameter");
        GlobalExpathAssertions.assertHasCondition(expath, "y == 0");
        GlobalExpathAssertions.assertEndsWithThrow(expath);
    }

    @Test
    void invalidMethodParameterInConjunctionExpressionGeneratesOrCondition() {
        String signature = "<br.unb.cic.witup.samples.Math: int invalidMethodParameterInConjunctionExpression(int)>";

        List<GlobalExpath> globals = generator.generateForMethod(MATH_CLASS, signature);
        GlobalExpathAssertions.assertCount(globals, 1);

        GlobalExpath expath = globals.get(0);
        GlobalExpathAssertions.assertThrows(expath, "RuntimeException");
        GlobalExpathAssertions.assertOriginMethod(expath, MATH_CLASS, "invalidMethodParameterInConjunctionExpression");
        GlobalExpathAssertions.assertHasCondition(expath, "p < 0 || p > 1");
        GlobalExpathAssertions.assertEndsWithThrow(expath);
    }
}
