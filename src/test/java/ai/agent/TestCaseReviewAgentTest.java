package ai.agent;

import ai.model.ReviewResult;
import ai.model.TestCase;
import ai.tool.JiraRequirementTool;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

public class TestCaseReviewAgentTest {

    @Test(groups="integration")
    public void reviewGeneratedTestCases() {

        // Agent 1
        TestCaseGenerationAgent generationAgent =
                new TestCaseGenerationAgent();

        // Jira requirement
        JiraRequirementTool jiraRequirementTool =
                new JiraRequirementTool();

        // Agent 2
        TestCaseReviewAgent reviewAgent =
                new TestCaseReviewAgent();

        try {

            // Step 1: Get requirement from Jira
            String requirementText =
                    jiraRequirementTool.getRequirementText("SCRUM-5");

            // Step 2: Agent 1 generates test cases
            List<TestCase> testCases =
                    generationAgent.generateTestCasesFromJira("SCRUM-5");

            Assert.assertNotNull(testCases);
            Assert.assertFalse(testCases.isEmpty());

            // Step 3: Agent 2 reviews the generated test cases
            ReviewResult result =
                    reviewAgent.review(
                            requirementText,
                            testCases
                    );

            // Step 4: Print review result
            System.out.println(
                    "\n========== AGENT 2 REVIEW =========="
            );

            System.out.println(
                    "Status: " + result.getOverallStatus()
            );

            System.out.println(
                    "Summary: " + result.getSummary()
            );

            System.out.println("\nIssues:");

            if (result.getIssues() != null) {
                result.getIssues()
                        .forEach(issue ->
                                System.out.println("- " + issue)
                        );
            }

            System.out.println("\nSuggestions:");

            if (result.getSuggestions() != null) {
                result.getSuggestions()
                        .forEach(suggestion ->
                                System.out.println("- " + suggestion)
                        );
            }

            System.out.println(
                    "====================================\n"
            );

            // Step 5: Basic validation
            Assert.assertNotNull(result);
            Assert.assertNotNull(result.getOverallStatus());
            Assert.assertNotNull(result.getSummary());

        } finally {

            reviewAgent.close();
        }
    }
}