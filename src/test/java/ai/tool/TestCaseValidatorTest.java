package ai.tool;

import ai.model.TestCase;
import ai.model.TestStep;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

public class TestCaseValidatorTest {

    @Test
    public void validTestCaseShouldPassValidation() {

        TestStep step =
                new TestStep(
                        1,
                        "Enter valid username",
                        "Username is accepted"
                );

        TestCase testCase =
                new TestCase(
                        "Valid Login",
                        "Verify valid login",
                        "User is registered",
                        List.of(step),
                        "High",
                        "Functional",
                        "Authentication",
                        null
                );

        TestCaseValidator validator =
                new TestCaseValidator();

        Assert.assertTrue(
                validator.validate(
                        List.of(testCase)
                )
        );
    }

    @Test
    public void testCaseWithoutStepsShouldFailValidation() {

        TestCase testCase =
                new TestCase(
                        "Invalid Test",
                        "Test without steps",
                        "None",
                        List.of(),
                        "Medium",
                        "Functional",
                        "Authentication",
                        null
                );

        TestCaseValidator validator =
                new TestCaseValidator();

        Assert.assertFalse(
                validator.validate(
                        List.of(testCase)
                )
        );
    }
}