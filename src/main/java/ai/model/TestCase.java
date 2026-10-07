package ai.model;

import java.util.List;
import java.util.Map;

public class TestCase {

    private String testcaseName;
    private String testcaseDescription;
    private String preConditions;
    private List<TestStep> testSteps;
    private String priority;
    private String type;
    private String feature;
    private Map<String, String> testData;

    public TestCase() {
    }

    public TestCase(
            String testcaseName,
            String testcaseDescription,
            String preConditions,
            List<TestStep> testSteps,
            String priority,
            String type,
            String feature,
            Map<String, String> testData) {

        this.testcaseName = testcaseName;
        this.testcaseDescription = testcaseDescription;
        this.preConditions = preConditions;
        this.testSteps = testSteps;
        this.priority = priority;
        this.type = type;
        this.feature = feature;
        this.testData = testData;
    }

    public String getTestcaseName() {
        return testcaseName;
    }

    public void setTestcaseName(String testcaseName) {
        this.testcaseName = testcaseName;
    }

    public String getTestcaseDescription() {
        return testcaseDescription;
    }

    public void setTestcaseDescription(String testcaseDescription) {
        this.testcaseDescription = testcaseDescription;
    }

    public String getPreConditions() {
        return preConditions;
    }

    public void setPreConditions(String preConditions) {
        this.preConditions = preConditions;
    }

    public List<TestStep> getTestSteps() {
        return testSteps;
    }

    public void setTestSteps(List<TestStep> testSteps) {
        this.testSteps = testSteps;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFeature() {
        return feature;
    }

    public void setFeature(String feature) {
        this.feature = feature;
    }

    public Map<String, String> getTestData() {
        return testData;
    }

    public void setTestData(Map<String, String> testData) {
        this.testData = testData;
    }

    @Override
    public String toString() {
        return "TestCase{" +
                "testcaseName='" + testcaseName + '\'' +
                ", testcaseDescription='" + testcaseDescription + '\'' +
                ", preConditions='" + preConditions + '\'' +
                ", testSteps=" + testSteps +
                ", priority='" + priority + '\'' +
                ", type='" + type + '\'' +
                ", feature='" + feature + '\'' +
                ", testData=" + testData +
                '}';
    }
}