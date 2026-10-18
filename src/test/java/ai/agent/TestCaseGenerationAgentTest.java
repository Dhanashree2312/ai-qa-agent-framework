
package ai.agent;

import ai.model.TestCase;
import org.testng.annotations.Test;

import java.util.List;

public class TestCaseGenerationAgentTest {

    @Test(groups = "integration")
    public void generateTestCasesWithAgent() {

        TestCaseGenerationAgent agent =
                new TestCaseGenerationAgent();

        List<TestCase> testCases =
                agent.generateTestCasesFromJira("SCRUM-5");

        // Keep your existing assertions or validation here.
    }
}
