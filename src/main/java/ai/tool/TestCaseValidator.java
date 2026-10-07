package ai.tool;

import ai.model.TestCase;
import ai.model.TestStep;

import java.util.List;

public class TestCaseValidator {

    public boolean validate(
            List<TestCase> testCases) {

        if (testCases == null ||
                testCases.isEmpty()) {

            return false;
        }

        for (TestCase testCase :
                testCases) {

            if (testCase == null) {
                return false;
            }

            if (testCase.getTestcaseName() == null ||
                    testCase.getTestcaseName()
                            .isBlank()) {

                return false;
            }

            if (testCase
                    .getTestcaseDescription() == null ||
                    testCase
                            .getTestcaseDescription()
                            .isBlank()) {

                return false;
            }

            if (testCase.getTestSteps() == null ||
                    testCase.getTestSteps().isEmpty()) {

                return false;
            }

            for (TestStep step :
                    testCase.getTestSteps()) {

                if (step == null) {
                    return false;
                }

                if (step.getAction() == null ||
                        step.getAction().isBlank()) {

                    return false;
                }

                if (step.getExpectedResult() == null ||
                        step.getExpectedResult().isBlank()) {

                    return false;
                }
            }
        }

        return true;
    }
}