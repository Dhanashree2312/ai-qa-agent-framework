package ai.model;

public class TestStep {

    private int stepNumber;
    private String action;
    private String expectedResult;

    public TestStep() {
    }

    public TestStep(int stepNumber, String action, String expectedResult) {
        this.stepNumber = stepNumber;
        this.action = action;
        this.expectedResult = expectedResult;
    }

    public int getStepNumber() {
        return stepNumber;
    }

    public void setStepNumber(int stepNumber) {
        this.stepNumber = stepNumber;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getExpectedResult() {
        return expectedResult;
    }

    public void setExpectedResult(String expectedResult) {
        this.expectedResult = expectedResult;
    }

    @Override
    public String toString() {
        return "TestStep{" +
                "stepNumber=" + stepNumber +
                ", action='" + action + '\'' +
                ", expectedResult='" + expectedResult + '\'' +
                '}';
    }
}