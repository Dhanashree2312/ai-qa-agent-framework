package ai.evaluation;

import ai.llm.OllamaClient;
import ai.model.TestCase;
import ai.model.TestStep;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.microsoft.playwright.APIResponse;

import java.util.List;

public class TestCaseSemanticEvaluator {

    private final OllamaClient ollamaClient;
    private final Gson gson;

    public TestCaseSemanticEvaluator() {
        this.ollamaClient = new OllamaClient();
        this.gson = new Gson();
    }

    public SemanticEvaluationResult evaluate(
            String requirement,
            TestCase testCase) {

        String systemPrompt = buildSystemPrompt();
        String userPrompt = buildUserPrompt(requirement, testCase);

        APIResponse response =
                ollamaClient.ask(systemPrompt, userPrompt);

        if (response.status() < 200
                || response.status() >= 300) {

            throw new RuntimeException(
                    "Ollama request failed. Status: "
                            + response.status()
                            + ", Body: " + response.text()
            );
        }

        // Step 1: Parse the outer Ollama response.
        JsonObject responseJson =
                gson.fromJson(response.text(), JsonObject.class);

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

        // Step 2: Extract the LLM-generated content.
        String content = responseJson
                .getAsJsonObject("message")
                .get("content")
                .getAsString()
                .trim();

        // Step 3: Remove Markdown code fences if present.
        if (content.startsWith("```")) {
            content = content.replaceFirst(
                    "^```(?:json)?\\s*", ""
            );

            content = content.replaceFirst(
                    "\\s*```$", ""
            ).trim();
        }

        // Step 4: Parse the generated content.
        JsonElement parsedContent =
                JsonParser.parseString(content);

        // Handle JSON returned as a quoted JSON string.
        if (parsedContent.isJsonPrimitive()
                && parsedContent.getAsJsonPrimitive().isString()) {

            content = parsedContent.getAsString().trim();

            parsedContent =
                    JsonParser.parseString(content);
        }

        // Step 5: Validate the expected JSON structure.
        if (!parsedContent.isJsonObject()) {
            throw new IllegalStateException(
                    "Expected a JSON object for semantic evaluation, "
                            + "but received: " + content
            );
        }

        JsonObject evaluationJson =
                parsedContent.getAsJsonObject();

        String[] requiredFields = {
                "requirementRelevance",
                "expectedBehaviorCoverage",
                "stepAlignment",
                "reason"
        };

        for (String field : requiredFields) {
            if (!evaluationJson.has(field)
                    || evaluationJson.get(field).isJsonNull()) {

                throw new IllegalStateException(
                        "Semantic evaluation response is missing field '"
                                + field + "': " + content
                );
            }
        }

        // Step 6: Convert JSON into the evaluation result model.
        SemanticEvaluationResult result =
                gson.fromJson(
                        evaluationJson,
                        SemanticEvaluationResult.class
                );

        // Step 7: Validate score ranges.
        validateScore(
                "requirementRelevance",
                result.getRequirementRelevance()
        );

        validateScore(
                "expectedBehaviorCoverage",
                result.getExpectedBehaviorCoverage()
        );

        validateScore(
                "stepAlignment",
                result.getStepAlignment()
        );

        return result;
    }

    private void validateScore(String field, int score) {
        if (score < 0 || score > 5) {
            throw new IllegalStateException(
                    "Invalid score for " + field
                            + ": " + score
                            + ". Expected a value between 0 and 5."
            );
        }
    }

    public String buildSystemPrompt() {
        return """
                You are an expert QA evaluator.
                Evaluate a generated test case against the given requirement.

                Assess these criteria:
                1. Requirement relevance: score from 0 to 5.
                2. Expected behavior coverage: score from 0 to 5.
                3. Step alignment: score from 0 to 5.

                Return only valid JSON with these fields:
                {
                  "requirementRelevance": 0,
                  "expectedBehaviorCoverage": 0,
                  "stepAlignment": 0,
                  "reason": "Explanation"
                }

                Do not include Markdown code fences.
                Do not return the JSON as a quoted string.

                Scoring guide:
                - 0: Completely missing or irrelevant.
                - 1: Very poor alignment.
                - 2: Significant gaps.
                - 3: Partially meets expectations.
                - 4: Mostly meets expectations, with minor gaps.
                - 5: Fully meets expectations.
                """;
    }

    public String buildUserPrompt(
            String requirement,
            TestCase testCase) {

        StringBuilder testStepContent =
                new StringBuilder();

        List<TestStep> teststeps =
                testCase.getTestSteps();

        if (teststeps != null) {
            for (TestStep teststep : teststeps) {
                testStepContent.append("""
                        Step: %d
                        Action: %s
                        Expected Result: %s
                        """.formatted(
                        teststep.getStepNumber(),
                        teststep.getAction(),
                        teststep.getExpectedResult()
                ));
            }
        }

        String testcaseContent = """
                Test Case Name: %s
                Description: %s
                Preconditions: %s
                Priority: %s
                Type: %s
                Feature: %s
                """.formatted(
                testCase.getTestcaseName(),
                testCase.getTestcaseDescription(),
                testCase.getPreConditions(),
                testCase.getPriority(),
                testCase.getType(),
                testCase.getFeature()
        );

        return """
                Evaluate the generated test case against the original requirement.
                Treat the original requirement as the source of truth.

                Original Requirement:
                %s

                Generated Test Case:
                %s

                Test Steps:
                %s
                """.formatted(
                requirement,
                testcaseContent,
                testStepContent
        );
    }

    public void close() {
        ollamaClient.close();
    }
}
