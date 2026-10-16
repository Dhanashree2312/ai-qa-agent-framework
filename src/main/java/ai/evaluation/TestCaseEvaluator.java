package ai.evaluation;

import ai.model.TestCase;
import ai.model.TestStep;

import java.util.List;

public class TestCaseEvaluator{

    public TestCaseEvaluator(){
    }

    public boolean hasValidName(TestCase testCase) {
        // Your logic here
        if(testCase!=null){
            String testCaseName = testCase.getTestcaseName();
            if(testCaseName==null || testCaseName.trim().isBlank()){
                return false;
            }
            else{
                return true;
            }
        }
        return false;
    }

    public boolean hasValidSteps(TestCase testcase){
        if(testcase == null){
            return false;
        }
        List<TestStep> testSteps = testcase.getTestSteps();
        if(testSteps == null || testSteps.isEmpty()){
            return false;
        }
        int expectedStepNumber = 1;
        for(TestStep teststep : testSteps){
            if(teststep == null){
                return false;
            }
            else{
            Integer stepNumber = teststep.getStepNumber();

            if(stepNumber==null || stepNumber<=0){
                return false;
            }
            if (stepNumber != expectedStepNumber) {
                return false;
            }
            else if(teststep.getAction()==null || teststep.getAction().trim().isBlank()){
                return false;
            }
            else if(teststep.getExpectedResult()==null || teststep.getExpectedResult().trim().isBlank()){
                return false;
            }}
            expectedStepNumber++;
        }
        return true;
    }

}
