package ai.evaluation;

import ai.model.TestCase;
import ai.model.TestStep;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TestCaseEvaluationTest{
    TestCaseEvaluator testCaseEvaluator;
    public TestCaseEvaluationTest(){
        testCaseEvaluator = new TestCaseEvaluator();
    }

    @Test
    public void shouldIdentifyTestCaseWithMissingName(){
        List<TestStep> testSteps = new ArrayList<>();
        TestStep testStep1 = new TestStep(1,"Click on Auth link","Auth page should be opened, showing fields to enter username and password");
        TestStep testStep2 = new TestStep(2,"Enter valid username and password","User should be able to add username and password successfully");
        TestStep testStep3 = new TestStep(3,"Click on login","Login API should throw success and user should be logged in successfully");
        TestStep testStep4 = new TestStep(4, "Click on logout","User should be able to log out successfully");
        TestStep testStep5 = new TestStep(5, "Click on login through guest","Login through guest must also be successfull");
        testSteps.add(testStep1);
        testSteps.add(testStep2);
        testSteps.add(testStep3);
        testSteps.add(testStep4);
        testSteps.add(testStep5);

        TestCase testcase1 = new TestCase(null,"","preconditions",testSteps,"Low","functional","",Map.of(
                "username", "validUser",
                "password", "validPass123"));

        boolean result = testCaseEvaluator.hasValidName(testcase1);

        Assert.assertFalse(result);
    }

    @Test
    public void shouldAcceptTestCaseWithValidName(){
        List<TestStep> testSteps = new ArrayList<>();
        TestStep testStep1 = new TestStep(1,"Click on Auth link","Auth page should be opened, showing fields to enter username and password");
        TestStep testStep2 = new TestStep(2,"Enter valid username and password","User should be able to add username and password successfully");
        TestStep testStep3 = new TestStep(3,"Click on login","Login API should throw success and user should be logged in successfully");
        TestStep testStep4 = new TestStep(4, "Click on logout","User should be able to log out successfully");
        TestStep testStep5 = new TestStep(5, "Click on login through guest","Login through guest must also be successfull");

        testSteps.add(testStep1);
        testSteps.add(testStep2);
        testSteps.add(testStep3);
        testSteps.add(testStep4);
        testSteps.add(testStep5);

        TestCase testcase2 = new TestCase("Verify login with valid credentials",
                "Verify successful login",
                "Valid credentials are available",testSteps,"Medium","Functional","Login",Map.of(
                "username", "validUser",
                "password", "validPass123"));

        boolean resultTestcase2 = testCaseEvaluator.hasValidName(testcase2);
        Assert.assertTrue(resultTestcase2);
    }

    @Test
    public void shouldRejectTestCaseWithWhitespaceName(){
        List<TestStep> testSteps = new ArrayList<>();
        TestStep testStep1 = new TestStep(1,"Click on Auth link","Auth page should be opened, showing fields to enter username and password");
        TestStep testStep2 = new TestStep(2,"Enter valid username and password","User should be able to add username and password successfully");
        TestStep testStep3 = new TestStep(3,"Click on login","Login API should throw success and user should be logged in successfully");
        TestStep testStep4 = new TestStep(4, "Click on logout","User should be able to log out successfully");
        TestStep testStep5 = new TestStep(5, "Click on login through guest","Login through guest must also be successfull");

        testSteps.add(testStep1);
        testSteps.add(testStep2);
        testSteps.add(testStep3);
        testSteps.add(testStep4);
        testSteps.add(testStep5);

        TestCase testcase3 = new TestCase("",
                "Verify successful login",
                "Valid credentials are available",testSteps,"Medium","Functional","Login",Map.of(
                "username", "validUser",
                "password", "validPass123"));

        boolean resultTestcase3 = testCaseEvaluator.hasValidName(testcase3);
        Assert.assertFalse(resultTestcase3);
    }

    @Test
    public void shouldAcceptTestCaseWithValidSteps(){
        List<TestStep> testSteps = new ArrayList<>();
        TestStep testStep1 = new TestStep(1,"Click Login","Login page opens");
        TestStep testStep2 = new TestStep(2,"Enter valid credentials","Credentials are entered");
        TestStep testStep3 = new TestStep(3,"Submit the form","User is logged in successfully");
        testSteps.add(testStep1);
        testSteps.add(testStep2);
        testSteps.add(testStep3);
        TestCase testcase2 = new TestCase("Verify login with valid credentials",
                "Verify successful login",
                "Valid credentials are available",testSteps,"Medium","Functional","Login",Map.of(
                "username", "validUser",
                "password", "validPass123"));
        boolean result = testCaseEvaluator.hasValidSteps(testcase2);
        Assert.assertTrue(result);
    }
    @Test
    public void shouldRejectTestCaseWithBlankExpectedResult(){
        List<TestStep> testSteps = new ArrayList<>();
        TestStep testStep1 = new TestStep(1,"Click Login","Login page opens");
        TestStep testStep2 = new TestStep(2,"Enter valid credentials","Credentials are entered");
        TestStep testStep3 = new TestStep(3,"Submit the form","");
        testSteps.add(testStep1);
        testSteps.add(testStep2);
        testSteps.add(testStep3);
        TestCase testcase3 = new TestCase("Verify login with valid credentials",
                "Verify successful login",
                "Valid credentials are available",testSteps,"Medium","Functional","Login",Map.of(
                "username", "validUser",
                "password", "validPass123"));
        boolean result = testCaseEvaluator.hasValidSteps(testcase3);
        Assert.assertFalse(result);
    }
}
