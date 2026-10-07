package ai.model;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TestCaseTest {
    List<TestStep> testSteps = new ArrayList<>();
    TestStep testStep1 = new TestStep(1,"Click on Auth link","Auth page should be opened, showing fields to enter username and password");
    TestStep testStep2 = new TestStep(2,"Enter valid username and password","User should be able to add username and password successfully");
    TestStep testStep3 = new TestStep(3,"Click on login","Login API should throw success and user should be logged in successfully");
    TestStep testStep4 = new TestStep(4, "Click on logout","User should be able to log out successfully");
    TestStep testStep5 = new TestStep(5, "Click on login through guest","Login through guest must also be successfull");

    @Test
    public void createTestCase(){
        testSteps.add(testStep1);
        testSteps.add(testStep2);
        testSteps.add(testStep3);
        testSteps.add(testStep4);
        testSteps.add(testStep5);

        TestCase testcase1 = new TestCase(
                "Login feature test",
                "Verify if user is able to login through Auth page successfully",
                "User is on right url",
                testSteps,
                "High",
                "Positive Functional",
                "Login Feature",
                Map.of(
                        "username", "validUser",
                        "password", "validPass123"
                )
        );        Assert.assertEquals(testcase1.getFeature(),"Login Feature");
        Assert.assertEquals(testcase1.getTestSteps().size(),5);
    }

}
