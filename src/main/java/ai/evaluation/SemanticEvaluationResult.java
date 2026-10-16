package ai.evaluation;

public class SemanticEvaluationResult{
    private int requirementRelevance;
    private int expectedBehaviorCoverage;
    private int stepAlignment;
    private String reason;

    public SemanticEvaluationResult(){

    }

    public int getRequirementRelevance(){
        return requirementRelevance;
    }

    public void setRequirementRelevance(int requirementRelevance){
        this.requirementRelevance=requirementRelevance;
    }

    public int getExpectedBehaviorCoverage(){
        return expectedBehaviorCoverage;
    }

    public void setExpectedBehaviorCoverage(int expectedBehaviorCoverage){
        this.expectedBehaviorCoverage=expectedBehaviorCoverage;
    }

    public int getStepAlignment(){
        return stepAlignment;
    }

    public void setStepAlignment(int stepAlignment){
        this.stepAlignment=stepAlignment;
    }

    public String getReason(){
        return reason;
    }

    public void setReason(String reason){
        this.reason=reason;
    }
}
