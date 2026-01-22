package br.unb.cic.witup.expath;

import java.util.List;

public record MethodUnderTest(String className, String methodSignature, List<String> expectedConditions) {
}
