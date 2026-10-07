package ai.agent;

import ai.model.TestCase;
import org.testng.annotations.Test;

import java.util.List;

public class TestCaseGenerationAgentTest {

    private TestCaseGenerationAgent agent;

    public TestCaseGenerationAgentTest() {

        agent =
                new TestCaseGenerationAgent();
    }

    @Test
    public void generateTestCasesWithAgent() {

        List<TestCase> testCases =
                agent.generateTestCasesFromJira(
                        "SCRUM-5"
                );

        System.out.println(
                "\n===== GENERATED TEST CASES ====="
        );

        for (TestCase testCase :
                testCases) {

            System.out.println(
                    testCase
            );
        }

        System.out.println(
                "================================"
        );
    }
}