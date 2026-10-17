package ai.agent;

import ai.agent.TestCaseGenerationAgent;
import ai.evaluation.TestCaseEvaluationDetail;
import ai.evaluation.TestSuiteEvaluationReport;
import ai.evaluation.TestSuiteEvaluationService;
import ai.model.TestCase;
import ai.tool.JiraRequirementTool;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

public class TestSuiteEvaluationServiceTest {

    @Test(groups="integration")
    public void shouldEvaluateAgent1GeneratedTestCases() {

        TestCaseGenerationAgent agent =
                new TestCaseGenerationAgent();

        JiraRequirementTool jiraTool =
                new JiraRequirementTool();

        TestSuiteEvaluationService service =
                new TestSuiteEvaluationService();

        try {
            // Step 1: Fetch the original requirement from Jira
            String requirementText =
                    jiraTool.getRequirementText("SCRUM-5");

            // Step 2: Generate test cases using Agent 1
            List<TestCase> testCases =
                    agent.generateTestCasesFromJira("SCRUM-5");

            Assert.assertNotNull(testCases);
            Assert.assertFalse(
                    testCases.isEmpty(),
                    "Agent 1 should generate at least one test case"
            );

            System.out.println(
                    "Generated test cases: " + testCases.size()
            );

            // Step 3: Evaluate the generated test suite
            TestSuiteEvaluationReport report =
                    service.evaluateSuite(requirementText, testCases);

            // Step 4: Validate the report
            Assert.assertEquals(
                    report.getTotalTestCases(), testCases.size()
            );

            Assert.assertEquals(
                    report.getPassedTestCases()
                            + report.getFailedTestCases(),
                    report.getTotalTestCases()
            );

            Assert.assertTrue(report.getPassRate() >= 0.0);
            Assert.assertTrue(report.getPassRate() <= 100.0);

            // Step 5: Print the evaluation summary
            System.out.println("----- Evaluation Report -----");
            System.out.println(
                    "Total: " + report.getTotalTestCases()
            );
            System.out.println(
                    "Passed: " + report.getPassedTestCases()
            );
            System.out.println(
                    "Failed: " + report.getFailedTestCases()
            );
            System.out.println(
                    "Pass rate: " + report.getPassRate() + "%"
            );

            // Print each test case's evaluation
            for (TestCaseEvaluationDetail detail :
                    report.getEvaluationDetails()) {

                System.out.println(
                        "Test case: "
                                + detail.getTestCase().getTestcaseName()
                );

                System.out.println(
                        "Quality gate passed: "
                                + detail.isPassedQualityGate()
                );

                System.out.println(
                        "Reason: "
                                + detail.getEvaluationResult().getReason()
                );
            }

        } finally {
            service.close();
        }
    }
}