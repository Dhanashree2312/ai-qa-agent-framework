package ai.evaluation;

import java.util.ArrayList;
import java.util.List;

public class TestSuiteEvaluationReport {
    private int totalTestCases;
    private int passedTestCases;
    private int failedTestCases;
    private double passRate;
    private List<TestCaseEvaluationDetail> evaluationDetails;

    /*
    requirementCoveragePercentage measures how much of the requirement is covered by the generated suite.
     */
    private double requirementCoveragePercentage;

    /*
    missingScenarios stores behaviors that the generated test cases don't adequately test.
     */
    private List<String> missingScenarios = new ArrayList<>();

    public int getTotalTestCases(){
        return totalTestCases;
    }

    public void setTotalTestCases(int totalTestCases){
        this.totalTestCases=totalTestCases;
    }

    public int getPassedTestCases(){
        return passedTestCases;
    }

    public void setPassedTestCases(int passedTestCases){
        this.passedTestCases=passedTestCases;
    }

    public int getFailedTestCases(){
        return failedTestCases;
    }

    public void setFailedTestCases(int failedTestCases){
        this.failedTestCases=failedTestCases;
    }

    public double getPassRate(){
        return passRate;
    }

    public void setPassRate(double passRate){
        this.passRate=passRate;
    }

    public List<TestCaseEvaluationDetail> getEvaluationDetails() {
        return evaluationDetails;
    }

    public void setEvaluationDetails(
            List<TestCaseEvaluationDetail> evaluationDetails) {
        this.evaluationDetails = evaluationDetails;
    }

    public double getRequirementCoveragePercentage() {
        return requirementCoveragePercentage;
    }

    public void setRequirementCoveragePercentage(
            double requirementCoveragePercentage) {
        this.requirementCoveragePercentage = requirementCoveragePercentage;
    }

    public List<String> getMissingScenarios() {
        return missingScenarios;
    }

    public void setMissingScenarios(List<String> missingScenarios) {
        this.missingScenarios = missingScenarios;
    }

}
