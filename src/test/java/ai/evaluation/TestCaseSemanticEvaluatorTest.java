package ai.evaluation;

import ai.model.TestCase;
import ai.model.TestStep;
import ai.tool.JiraRequirementTool;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TestCaseSemanticEvaluatorTest{
    TestCaseSemanticEvaluator semanticEvaluator;
    JiraRequirementTool jiraRequirementTool;

    public TestCaseSemanticEvaluatorTest(){
        semanticEvaluator = new TestCaseSemanticEvaluator();
        jiraRequirementTool = new JiraRequirementTool();
    }

    @Test(groups="integration")
    public void evaluateTestCase(){
        List<TestStep> testSteps = new ArrayList<>();
        TestStep testStep1 = new TestStep(1,"Enter valid username and password.","Username and password should be entered.");
        TestStep testStep2 = new TestStep(2,"Click Login.","Login button is clicked");
        TestStep testStep3 = new TestStep(3,"Verify the dashboard or successful login state appears.","User is logged in successfully");
        testSteps.add(testStep1);
        testSteps.add(testStep2);
        testSteps.add(testStep3);
        TestCase testcase2 = new TestCase("Verify login with valid credentials",
                "Verify successful login",
                "Valid credentials are available",testSteps,"Medium","Functional","Login", Map.of(
                "username", "validUser",
                "password", "validPass123"));
        String requirementText = jiraRequirementTool.getRequirementText("SCRUM-5");
        SemanticEvaluationResult result = semanticEvaluator.evaluate(requirementText,testcase2);
        System.out.println("Semantic Evaluation Result:");

        Assert.assertNotNull(result, "Evaluation result should not be null");
        Assert.assertNotNull(result, "Evaluation result should not be null");

        Assert.assertTrue(
                result.getRequirementRelevance() >= 0
                        && result.getRequirementRelevance() <= 5,
                "Requirement relevance must be between 0 and 5"
        );

        Assert.assertTrue(
                result.getExpectedBehaviorCoverage() >= 0
                        && result.getExpectedBehaviorCoverage() <= 5,
                "Expected behavior coverage must be between 0 and 5"
        );

        Assert.assertTrue(
                result.getStepAlignment() >= 0
                        && result.getStepAlignment() <= 5,
                "Step alignment must be between 0 and 5"
        );

        Assert.assertNotNull(result.getReason(), "Evaluation reason should not be null");

        System.out.println("Requirement Relevance: " + result.getRequirementRelevance());
        System.out.println("Expected Behavior Coverage: " + result.getExpectedBehaviorCoverage());
        System.out.println("Step Alignment: " + result.getStepAlignment());
        System.out.println("Reason: " + result.getReason());
        System.out.println(result.getRequirementRelevance());
        System.out.println(result.getStepAlignment());
        System.out.println(result.getReason());
    }

    @AfterClass(alwaysRun = true)
    public void tearDown(){
        semanticEvaluator.close();
    }
}
