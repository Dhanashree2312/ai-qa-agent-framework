package ai.evaluation;

import ai.llm.OllamaClient;
import ai.model.TestCase;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.microsoft.playwright.APIResponse;

import java.util.ArrayList;
import java.util.List;

public class TestSuiteEvaluationService {

    private final TestCaseSemanticEvaluator evaluator;
    private final SemanticQualityGate qualityGate;
    private final OllamaClient ollamaClient = new OllamaClient();
    private final Gson gson = new Gson();

    public TestSuiteEvaluationService() {
        this(new TestCaseSemanticEvaluator());
    }

    public TestSuiteEvaluationService(
            TestCaseSemanticEvaluator evaluator) {
        this.evaluator = evaluator;
        this.qualityGate = new SemanticQualityGate();
    }

    public TestSuiteEvaluationReport evaluateSuite(
            String requirementText,
            List<TestCase> testcases) {

        List<TestCaseEvaluationDetail> evaluationResult =
                new ArrayList<>();

        int passTestCases = 0;

        // Step 1: Handle an empty test suite.
        if (testcases == null || testcases.isEmpty()) {
            TestSuiteEvaluationReport report =
                    new TestSuiteEvaluationReport();

            report.setTotalTestCases(0);
            report.setPassedTestCases(0);
            report.setFailedTestCases(0);
            report.setPassRate(0.0);
            report.setEvaluationDetails(evaluationResult);
            report.setRequirementCoveragePercentage(0.0);
            report.setMissingScenarios(new ArrayList<>());

            return report;
        }

        // Step 2: Evaluate each test case.
        for (TestCase testcase : testcases) {

            SemanticEvaluationResult result =
                    evaluator.evaluate(requirementText, testcase);

            boolean passed =
                    qualityGate.meetsQualityGate(result);

            TestCaseEvaluationDetail details =
                    new TestCaseEvaluationDetail();

            details.setEvaluationResult(result);
            details.setTestCase(testcase);
            details.setPassedQualityGate(passed);

            evaluationResult.add(details);

            if (passed) {
                passTestCases++;
            }
        }

        // Step 3: Calculate suite-level metrics.
        int totalTestCases = testcases.size();

        int failedTestCases =
                totalTestCases - passTestCases;

        double passRate =
                totalTestCases == 0
                        ? 0.0
                        : (passTestCases * 100.0)
                        / totalTestCases;

        // Step 4: Build the evaluation report.
        TestSuiteEvaluationReport report =
                new TestSuiteEvaluationReport();

        report.setTotalTestCases(totalTestCases);
        report.setFailedTestCases(failedTestCases);
        report.setPassedTestCases(passTestCases);
        report.setPassRate(passRate);
        report.setEvaluationDetails(evaluationResult);

        // Step 5: Evaluate requirement-level coverage.
        evaluateRequirementCoverage(
                requirementText,
                testcases,
                report
        );

        return report;
    }

    protected void evaluateRequirementCoverage(
            String requirementText,
            List<TestCase> testCases,
            TestSuiteEvaluationReport report) {

        if (testCases == null || testCases.isEmpty()) {
            report.setRequirementCoveragePercentage(0.0);
            report.setMissingScenarios(new ArrayList<>());
            return;
        }

        // Build a readable representation of the test suite.
        StringBuilder testCasesContent =
                new StringBuilder();

        for (TestCase testCase : testCases) {

            testCasesContent
                    .append("Test Case: ")
                    .append(testCase.getTestcaseName())
                    .append("\nDescription: ")
                    .append(testCase.getTestcaseDescription())
                    .append("\n");

            if (testCase.getTestSteps() != null) {
                for (var step : testCase.getTestSteps()) {

                    testCasesContent
                            .append("Action: ")
                            .append(step.getAction())
                            .append("\nExpected Result: ")
                            .append(step.getExpectedResult())
                            .append("\n");
                }
            }

            testCasesContent.append("\n");
        }

        // System prompt: define the coverage evaluation rules.
        String systemPrompt = """
                You are an expert QA requirement coverage evaluator.

                Identify distinct, testable behavior scenarios from the
                original requirement and determine whether the complete
                generated test suite covers each scenario.

                A scenario is covered only when a generated test case
                provides meaningful evidence that it tests that behavior.

                Return only valid JSON in this format:
                {
                  "scenarios": [
                    {
                      "scenario": "Successful login",
                      "covered": true,
                      "supportingTestCases": ["Positive Login Test"],
                      "reason": "The test verifies successful login."
                    },
                    {
                      "scenario": "Blank credentials rejected",
                      "covered": false,
                      "supportingTestCases": [],
                      "reason": "No generated test case checks blank credentials."
                    }
                  ]
                }

                Include all distinct, testable scenarios from the requirement.
                Do not invent requirements.
                Use exact generated test case names in supportingTestCases.
                Return an empty supportingTestCases list when a scenario is missing.
                The covered field must be a boolean, not a string.
                Do not include Markdown code fences.
                """;

        // User prompt: provide the requirement and generated test suite.
        String userPrompt = """
                Original requirement:
                %s

                Complete generated test suite:
                %s
                """.formatted(
                requirementText,
                testCasesContent
        );

        // Call Ollama.
        APIResponse response =
                ollamaClient.ask(systemPrompt, userPrompt);

        if (response.status() < 200
                || response.status() >= 300) {

            throw new RuntimeException(
                    "Coverage evaluation failed. Status: "
                            + response.status()
                            + ", Body: "
                            + response.text()
            );
        }

        // Extract the model's content from the Ollama response.
        JsonObject responseJson =
                gson.fromJson(
                        response.text(),
                        JsonObject.class
                );

        if (responseJson == null
                || !responseJson.has("message")
                || !responseJson.get("message").isJsonObject()
                || !responseJson.getAsJsonObject("message")
                .has("content")) {

            throw new IllegalStateException(
                    "Unexpected Ollama response structure: "
                            + response.text()
            );
        }

        String content = responseJson
                .getAsJsonObject("message")
                .get("content")
                .getAsString()
                .trim();

        // Remove Markdown code fences if the model adds them.
        if (content.startsWith("```")) {
            content = content.replaceFirst(
                    "^```(?:json)?\\s*",
                    ""
            );

            content = content.replaceFirst(
                    "\\s*```$",
                    ""
            ).trim();
        }

        // Parse the content and handle a JSON-encoded string.
        JsonElement parsedContent =
                JsonParser.parseString(content);

        if (parsedContent.isJsonPrimitive()
                && parsedContent
                .getAsJsonPrimitive()
                .isString()) {

            content = parsedContent
                    .getAsString()
                    .trim();

            parsedContent =
                    JsonParser.parseString(content);
        }

        // Validate that the model returned a JSON object.
        if (!parsedContent.isJsonObject()) {
            throw new IllegalStateException(
                    "Expected a JSON object for requirement coverage, "
                            + "but received: "
                            + content
            );
        }

        JsonObject coverageJson =
                parsedContent.getAsJsonObject();

        if (!coverageJson.has("scenarios")
                || !coverageJson.get("scenarios").isJsonArray()) {

            throw new IllegalStateException(
                    "Coverage response must contain a scenarios array: "
                            + content
            );
        }

        JsonArray scenarios =
                coverageJson.getAsJsonArray("scenarios");

        // Calculate requirement coverage and identify missing scenarios.
        int totalScenarios = scenarios.size();
        int coveredScenarios = 0;

        List<String> missingScenarios =
                new ArrayList<>();

        for (int i = 0; i < totalScenarios; i++) {

            JsonElement scenarioElement =
                    scenarios.get(i);

            if (!scenarioElement.isJsonObject()) {
                throw new IllegalStateException(
                        "Invalid scenario at index " + i
                                + ": " + scenarioElement
                );
            }

            JsonObject scenario =
                    scenarioElement.getAsJsonObject();

            if (!scenario.has("scenario")
                    || !scenario.has("covered")
                    || !scenario.get("covered").isJsonPrimitive()
                    || !scenario.get("covered")
                    .getAsJsonPrimitive().isBoolean()) {

                throw new IllegalStateException(
                        "Scenario must contain a scenario name "
                                + "and boolean covered field: "
                                + scenario
                );
            }

            boolean covered =
                    scenario.get("covered").getAsBoolean();

            if (covered) {
                coveredScenarios++;
            } else {
                missingScenarios.add(
                        scenario.get("scenario").getAsString()
                );
            }
        }

        double coveragePercentage =
                totalScenarios == 0
                        ? 0.0
                        : (coveredScenarios * 100.0)
                        / totalScenarios;

        report.setRequirementCoveragePercentage(
                coveragePercentage
        );

        report.setMissingScenarios(missingScenarios);
    }

    public void close() {
        evaluator.close();
        ollamaClient.close();
    }
}