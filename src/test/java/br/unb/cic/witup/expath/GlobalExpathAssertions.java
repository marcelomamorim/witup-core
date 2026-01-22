package br.unb.cic.witup.expath;

import br.unb.cic.witup.expath.condition.AndCondition;
import br.unb.cic.witup.expath.condition.AtomicCondition;
import br.unb.cic.witup.expath.condition.NotCondition;
import br.unb.cic.witup.expath.condition.OrCondition;
import br.unb.cic.witup.expath.condition.PathCondition;
import br.unb.cic.witup.expath.condition.TrueCondition;
import org.junit.jupiter.api.Assertions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

public final class GlobalExpathAssertions {
    private GlobalExpathAssertions() {
    }

    public static void assertThrows(final GlobalExpath expath, final String exceptionClassSimpleName) {
        String exceptionType = expath.getExceptionType();
        String simpleName = exceptionType.contains(".")
                ? exceptionType.substring(exceptionType.lastIndexOf('.') + 1)
                : exceptionType;
        Assertions.assertEquals(exceptionClassSimpleName, simpleName, "Unexpected exception type");
    }

    public static void assertOriginMethod(final GlobalExpath expath, final String owner, final String methodName) {
        String originMethod = expath.getOriginMethod();
        Assertions.assertTrue(originMethod.contains(owner), "Origin method should include owner");
        Assertions.assertTrue(originMethod.contains(methodName), "Origin method should include method name");
    }

    public static void assertHasCondition(final GlobalExpath expath, final String normalizedCondition) {
        String actual = normalize(expath.getGlobalCondition().getCondition());
        String expected = normalizeExpression(normalizedCondition);
        Assertions.assertTrue(actual.contains(expected),
                () -> "Expected condition to contain '" + expected + "' but was '" + actual + "'");
    }

    public static void assertEquivalentConditions(final GlobalExpath first, final GlobalExpath second) {
        String left = normalize(first.getGlobalCondition().getCondition());
        String right = normalize(second.getGlobalCondition().getCondition());
        Assertions.assertEquals(left, right, "Expected equivalent global conditions");
    }

    public static void assertAllSatOrUnknown(final List<GlobalExpath> expaths) {
        for (GlobalExpath expath : expaths) {
            Assertions.assertTrue(expath.getStatus() == ExpathStatus.SAT
                            || expath.getStatus() == ExpathStatus.UNKNOWN,
                    "Unexpected expath status: " + expath.getStatus());
        }
    }

    public static void assertCount(final List<GlobalExpath> expaths, final int expected) {
        Assertions.assertEquals(expected, expaths.size(), "Unexpected expath count");
    }

    public static void assertEndsWithThrow(final GlobalExpath expath) {
        Assertions.assertFalse(expath.getPathNodes().isEmpty(), "Path nodes should not be empty");
        Object lastNode = expath.getPathNodes().get(expath.getPathNodes().size() - 1);
        Assertions.assertTrue(lastNode.toString().toLowerCase(Locale.ROOT).contains("throw"),
                "Path should end with a throw statement");
    }

    public static String normalize(final PathCondition pathCondition) {
        Objects.requireNonNull(pathCondition, "pathCondition");
        if (pathCondition instanceof AtomicCondition atomic) {
            return normalizeExpression(atomic.getExpression());
        }
        if (pathCondition instanceof NotCondition notCondition) {
            return "!" + normalize(notCondition.getOperand());
        }
        if (pathCondition instanceof AndCondition andCondition) {
            return normalizeComposite(andCondition.getOperands(), "&&");
        }
        if (pathCondition instanceof OrCondition orCondition) {
            return normalizeComposite(orCondition.getOperands(), "||");
        }
        if (pathCondition instanceof TrueCondition) {
            return "true";
        }
        return normalizeExpression(pathCondition.format());
    }

    public static boolean pathConditionImpliesThrow(final PathCondition pathCondition, final MethodUnderTest method) {
        String normalized = normalize(pathCondition);
        return method.expectedConditions().stream()
                .map(GlobalExpathAssertions::normalizeExpression)
                .anyMatch(normalized::contains);
    }

    private static String normalizeComposite(final List<PathCondition> operands, final String operator) {
        List<String> normalizedParts = new ArrayList<>();
        for (PathCondition operand : operands) {
            normalizedParts.add(normalize(operand));
        }
        Collections.sort(normalizedParts);
        return normalizedParts.stream().collect(Collectors.joining(operator));
    }

    private static String normalizeExpression(final String expression) {
        return expression.replace("this.", "")
                .replaceAll("\\s+", "")
                .replace("(", "")
                .replace(")", "");
    }
}
