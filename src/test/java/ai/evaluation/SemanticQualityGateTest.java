package ai.evaluation;

import org.testng.Assert;
import org.testng.annotations.Test;

public class SemanticQualityGateTest {
    SemanticQualityGate semanticQualityGate;

    public SemanticQualityGateTest(){
        semanticQualityGate= new SemanticQualityGate();
    }

    @Test
    public void shouldPassWhenAllScoresMeetThreshold(){
        SemanticEvaluationResult result =
                new SemanticEvaluationResult();
        result.setExpectedBehaviorCoverage(5);
        result.setStepAlignment(5);
        result.setRequirementRelevance(5);

        boolean qualityGate = semanticQualityGate.meetsQualityGate(result);

        Assert.assertTrue(
                semanticQualityGate.meetsQualityGate(result),
                "Quality gate should pass when all scores meet the threshold"
        );
    }

    @Test
    public void shouldPassWhenAllScoresAreAtThreshold(){
        SemanticEvaluationResult result =
                new SemanticEvaluationResult();
        result.setExpectedBehaviorCoverage(4);
        result.setStepAlignment(4);
        result.setRequirementRelevance(4);

        boolean qualityGate = semanticQualityGate.meetsQualityGate(result);

        Assert.assertTrue(
                semanticQualityGate.meetsQualityGate(result),
                "Quality gate should pass when all scores are at the threshold"
        );
    }

    @Test
    public void shouldFailWhenAllScoresAreBelowThreshold(){
        SemanticEvaluationResult result =
                new SemanticEvaluationResult();
        result.setExpectedBehaviorCoverage(3);
        result.setStepAlignment(0);
        result.setRequirementRelevance(2);

        boolean qualityGate = semanticQualityGate.meetsQualityGate(result);

        Assert.assertFalse(
                semanticQualityGate.meetsQualityGate(result),
                "Quality gate should fail when all scores are below the threshold"
        );
    }

    @Test
    public void shouldFailWhenEvenOneScoreFailsThreshold(){
        SemanticEvaluationResult result =
                new SemanticEvaluationResult();
        result.setExpectedBehaviorCoverage(5);
        result.setStepAlignment(2);
        result.setRequirementRelevance(5);

        boolean qualityGate = semanticQualityGate.meetsQualityGate(result);

        Assert.assertFalse(
                semanticQualityGate.meetsQualityGate(result),
                "Quality gate should pass when even one score fails to meet the threshold"
        );
    }

    @Test
    public void shouldFailWhenEvaluationResultIsNull() {
        boolean qualityGate =
                semanticQualityGate.meetsQualityGate(null);

        Assert.assertFalse(
                qualityGate,
                "Quality gate should fail when evaluation result is null"
        );
    }
}
