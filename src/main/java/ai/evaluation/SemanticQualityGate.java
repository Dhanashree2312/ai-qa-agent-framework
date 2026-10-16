package ai.evaluation;

public class SemanticQualityGate {
    private final int minimumRelevance=4;
    private final int minimumCoverage=4;
    private final int minimumStepAlignment=4;

    public boolean meetsQualityGate(SemanticEvaluationResult result){
        if (result == null) {
            return false;
        }
        return result.getRequirementRelevance()>=4
                && result.getStepAlignment()>=4
                && result.getExpectedBehaviorCoverage()>=4;
    }
}
