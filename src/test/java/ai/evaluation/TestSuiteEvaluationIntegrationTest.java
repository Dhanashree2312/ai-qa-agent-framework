package ai.evaluation;

import ai.agent.TestCaseGenerationAgent;
import ai.evaluation.TestCaseEvaluationDetail;
import ai.evaluation.SemanticEvaluationResult;
import ai.evaluation.TestSuiteEvaluationReport;
import ai.evaluation.TestSuiteEvaluationService;
import ai.model.TestCase;
import ai.tool.JiraRequirementTool;
import org.testng.annotations.Test;

import java.util.List;

public class TestSuiteEvaluationIntegrationTest {

    @Test
    public void shouldEvaluateJiraGeneratedTestSuite() {

        TestCaseGenerationAgent agent = new TestCaseGenerationAgent();
        JiraRequirementTool jiraTool = new JiraRequirementTool();
        TestSuiteEvaluationService service =
                new TestSuiteEvaluationService();

        try {
            String requirementText =
                    jiraTool.getRequirementText("SCRUM-5");

            List<TestCase> testCases =
                    agent.generateTestCasesFromJira("SCRUM-5");

            TestSuiteEvaluationReport report =
                    service.evaluateSuite(requirementText, testCases);

            System.out.println(
                    "\n========== AI QA EVALUATION REPORT =========="
            );
            System.out.println("Jira Requirement: SCRUM-5");
            System.out.println("Total Test Cases: "
                    + report.getTotalTestCases());
            System.out.println("Passed Quality Gate: "
                    + report.getPassedTestCases());
            System.out.println("Failed Quality Gate: "
                    + report.getFailedTestCases());

            System.out.printf("Quality Gate Pass Rate: %.2f%%%n",
                    report.getPassRate());

            System.out.printf("Requirement Coverage: %.2f%%%n",
                    report.getRequirementCoveragePercentage());

            System.out.println("\nMissing Scenarios:");
            if (report.getMissingScenarios().isEmpty()) {
                System.out.println("None");
            } else {
                report.getMissingScenarios()
                        .forEach(scenario ->
                                System.out.println("- " + scenario));
            }

            System.out.println("\nIndividual Test Case Results:");

            for (TestCaseEvaluationDetail detail
                    : report.getEvaluationDetails()) {

                SemanticEvaluationResult result =
                        detail.getEvaluationResult();

                System.out.println("----------------------------------");
                System.out.println("Test Case: "
                        + detail.getTestCase().getTestcaseName());
                System.out.println("Requirement Relevance: "
                        + result.getRequirementRelevance() + "/5");
                System.out.println("Expected Behavior Coverage: "
                        + result.getExpectedBehaviorCoverage() + "/5");
                System.out.println("Step Alignment: "
                        + result.getStepAlignment() + "/5");
                System.out.println("Quality Gate: "
                        + (detail.isPassedQualityGate()
                        ? "PASS" : "FAIL"));
                System.out.println("Reason: " + result.getReason());
            }

            System.out.println(
                    "=============================================="
            );

        } finally {
            service.close();
            // Close agent and Jira tool here too if their APIs
            // provide close methods and they own open resources.
        }
    }
}