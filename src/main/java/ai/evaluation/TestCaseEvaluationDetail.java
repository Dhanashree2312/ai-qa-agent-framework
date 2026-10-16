package ai.evaluation;

import ai.model.TestCase;

public class TestCaseEvaluationDetail {
    private TestCase testCase; //identifies which generated test case was evaluated.
    private SemanticEvaluationResult evaluationResult; //retains its three scores and the evaluator's reason.
    private boolean passedQualityGate; //records whether the scores met all your thresholds.

    public TestCase getTestCase() {
        return testCase;
    }

    public void setTestCase(TestCase testCase) {
        this.testCase = testCase;
    }

    public SemanticEvaluationResult getEvaluationResult() {
        return evaluationResult;
    }

    public void setEvaluationResult(SemanticEvaluationResult evaluationResult) {
        this.evaluationResult = evaluationResult;
    }

    public boolean isPassedQualityGate() {
        return passedQualityGate;
    }

    public void setPassedQualityGate(boolean passedQualityGate) {
        this.passedQualityGate = passedQualityGate;
    }
}
