package br.unb.cic.witup.expath.util;

import java.nio.file.Path;
import java.nio.file.Paths;

public final class JvmTestCompilationHelper {
    private JvmTestCompilationHelper() {
    }

    public static Path testClassesDir() {
        Path projectRoot = Paths.get(System.getProperty("user.dir"));
        return projectRoot.resolve("target/test-classes");
    }

    public static String testClassesPath() {
        return testClassesDir().toString();
    }
}
