package br.unb.cic.witup.expath;

import br.unb.cic.witup.graph.node.ThrowStatementNode;
import br.unb.cic.witup.graph.node.WITUpNode;
import br.unb.cic.witup.sootup.SootUpAnalyser;
import br.unb.cic.witup.sootup.SootUpPropertyGraphs;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExPathGeneratorTest {
    private static final String CLASS_NAME = "br.unb.cic.witup.samples.Math";
    private static final String CIRCLE_AREA_SIGNATURE =
            "<br.unb.cic.witup.samples.Math: double circleArea()>";

    private Path testClassesDir;
    private SootUpAnalyser sootUpAnalyser;

    @BeforeEach
    void setUp() {
        Path projectRoot = Paths.get(System.getProperty("user.dir"));
        testClassesDir = projectRoot.resolve("target/test-classes");
        sootUpAnalyser = new SootUpAnalyser();
    }

    @Test
    void generatesLocalExPathsForThrowStatements() {
        HashMap<String, SootUpPropertyGraphs> sootUpGraphs = sootUpAnalyser
                .analyseThrowingMethods(testClassesDir.toString(), CLASS_NAME);
        SootUpPropertyGraphs circleAreaGraphs = sootUpGraphs.get(CIRCLE_AREA_SIGNATURE);

        ExPathGenerator generator = new ExPathGenerator();
        Map<WITUpNode, List<ExPath>> localExPaths = generator.generateLocalExPaths(circleAreaGraphs);

        assertFalse(localExPaths.isEmpty());
        localExPaths.forEach((throwNode, paths) -> {
            assertTrue(throwNode instanceof ThrowStatementNode);
            assertFalse(paths.isEmpty());
            paths.forEach(path -> assertTrue(path.getEndNode().equals(throwNode)));
        });
    }

    @Test
    void generatesGlobalExPathsFromLocalPaths() {
        HashMap<String, Map<WITUpNode, List<ExPath>>> globalExPaths = sootUpAnalyser
                .analyseGlobalExPaths(testClassesDir.toString(), CLASS_NAME);

        assertTrue(globalExPaths.containsKey(CIRCLE_AREA_SIGNATURE));
        assertFalse(globalExPaths.get(CIRCLE_AREA_SIGNATURE).isEmpty());
    }
}
