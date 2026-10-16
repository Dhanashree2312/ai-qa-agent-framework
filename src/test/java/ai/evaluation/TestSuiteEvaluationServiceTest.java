package ai.evaluation;

import ai.model.TestCase;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TestSuiteEvaluationServiceTest {

    @Test
    public void shouldCalculateSuiteReportCorrectly() {

        // Arrange: mock the individual test-case evaluator
        TestCaseSemanticEvaluator evaluator =
                mock(TestCaseSemanticEvaluator.class);

        TestSuiteEvaluationService service =
                new TestableTestSuiteEvaluationService(evaluator);

        List<TestCase> testCases = Arrays.asList(
                mock(TestCase.class),
                mock(TestCase.class),
                mock(TestCase.class),
                mock(TestCase.class),
                mock(TestCase.class)
        );

        // Four passing results and one failing result
        when(evaluator.evaluate(anyString(), any(TestCase.class)))
                .thenReturn(
                        createResult(5, 5, 5),
                        createResult(4, 4, 4),
                        createResult(5, 4, 5),
                        createResult(4, 5, 4),
                        createResult(3, 5, 5)
                );

        // Act
        TestSuiteEvaluationReport report =
                service.evaluateSuite(
                        "User login requirement",
                        testCases
                );

        // Assert: individual test-case metrics
        Assert.assertEquals(report.getTotalTestCases(), 5);
        Assert.assertEquals(report.getPassedTestCases(), 4);
        Assert.assertEquals(report.getFailedTestCases(), 1);
        Assert.assertEquals(report.getPassRate(), 80.0);
        Assert.assertEquals(report.getEvaluationDetails().size(), 5);

        verify(evaluator, times(5))
                .evaluate(anyString(), any(TestCase.class));

        service.close();
    }

    @Test
    public void shouldHandleEmptyTestSuite() {

        TestCaseSemanticEvaluator evaluator =
                mock(TestCaseSemanticEvaluator.class);

        TestSuiteEvaluationService service =
                new TestableTestSuiteEvaluationService(evaluator);

        try {
            TestSuiteEvaluationReport report =
                    service.evaluateSuite(
                            "User login requirement",
                            Collections.emptyList()
                    );

            Assert.assertEquals(report.getTotalTestCases(), 0);
            Assert.assertEquals(report.getPassedTestCases(), 0);
            Assert.assertEquals(report.getFailedTestCases(), 0);
            Assert.assertEquals(report.getPassRate(), 0.0);
            Assert.assertTrue(report.getEvaluationDetails().isEmpty());

            // Empty suite should not have any test-case evaluations
            verify(evaluator, times(0))
                    .evaluate(anyString(), any(TestCase.class));

        } finally {
            service.close();
        }
    }

    private SemanticEvaluationResult createResult(
            int relevance,
            int coverage,
            int alignment) {

        SemanticEvaluationResult result =
                new SemanticEvaluationResult();

        result.setRequirementRelevance(relevance);
        result.setExpectedBehaviorCoverage(coverage);
        result.setStepAlignment(alignment);
        result.setReason("Test evaluation result");

        return result;
    }
    private static class TestableTestSuiteEvaluationService
            extends TestSuiteEvaluationService {

        public TestableTestSuiteEvaluationService(
                TestCaseSemanticEvaluator evaluator) {
            super(evaluator);
        }

        @Override
        protected void evaluateRequirementCoverage(
                String requirementText,
                List<TestCase> testCases,
                TestSuiteEvaluationReport report) {

            // Coverage is tested separately from suite metrics.
            report.setRequirementCoveragePercentage(0.0);
            report.setMissingScenarios(Collections.emptyList());
        }
    }
}