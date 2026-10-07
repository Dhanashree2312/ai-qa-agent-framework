package ai.tool;

import ai.model.TestCase;
import org.testng.annotations.Test;

import java.util.List;

public class TestCaseGeneratorTest {

    @Test
    public void generateTestCasesFromJira() {
        //Generate requirement from JiraRequirementTool
        JiraRequirementTool jiraTool = new JiraRequirementTool();
        String requirementText = jiraTool.getRequirementText("SCRUM-5");
        System.out.println("========== REQUIREMENT ==========");
        System.out.println(requirementText);


        // Generate test cases using LLM
        TestCaseGenerator generator =
                new TestCaseGenerator();
        List<TestCase> testCases = generator.generateTestCases(requirementText);
        System.out.println("========== GENERATED TEST CASES ==========");

        // Print generated test cases
        System.out.println("========== GENERATED TEST CASES ==========");

        for (TestCase testCase : testCases) {

            System.out.println("Test Case: "
                    + testCase.getTestcaseName());

            System.out.println("Description: "
                    + testCase.getTestcaseDescription());

            System.out.println("Priority: "
                    + testCase.getPriority());

            System.out.println("Type: "
                    + testCase.getType());

            System.out.println("Feature: "
                    + testCase.getFeature());

            System.out.println("Test Data: "
                    + testCase.getTestData());

            System.out.println("Preconditions: "
                    + testCase.getPreConditions());

            System.out.println("Steps:");

            testCase.getTestSteps().forEach(step -> {

                System.out.println(
                        step.getStepNumber()
                                + ". "
                                + step.getAction()
                                + " → "
                                + step.getExpectedResult()
                );
            });

            System.out.println("--------------------------------------");
        }

    }
}
