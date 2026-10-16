package ai.agent;

import ai.llm.OllamaClient;
import ai.model.ReviewResult;
import ai.model.TestCase;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.microsoft.playwright.APIResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class TestCaseReviewAgent {

    private static final Logger logger =
            LogManager.getLogger(TestCaseReviewAgent.class);

    private final OllamaClient ollamaClient;
    private final Gson gson;

    public TestCaseReviewAgent() {
        ollamaClient = new OllamaClient();
        gson = new Gson();
    }

    public ReviewResult review(String requirementText,
                               List<TestCase> testCases) {

        String systemPrompt = """
                You are an expert Senior QA Test Lead.

                Your job is to review AI-generated test cases against the
                original requirement.

                Review the test cases for:

                1. Requirement and acceptance-criteria coverage
                2. Positive scenarios
                3. Negative scenarios
                4. Step clarity
                5. Expected-result quality
                6. Test-data completeness
                7. Duplicate or redundant scenarios
                8. Missing important scenarios

                Return ONLY valid JSON.

                The JSON must have exactly this structure:

                {
                  "overallStatus": "PASS or NEEDS_IMPROVEMENT",
                  "summary": "short summary",
                  "issues": [
                    "issue 1"
                  ],
                  "suggestions": [
                    "suggestion 1"
                  ]
                }

                If the test cases are good enough, use PASS and an empty
                issues array.

                Do not invent requirements that are not present in the
                requirement text.
                """;

        String testCasesJson = gson.toJson(testCases);

        String userPrompt = """
                Review the following generated test cases against the Jira requirement.

                REQUIREMENT:
                %s

                GENERATED TEST CASES:
                %s

                Return only the JSON review result.
                """.formatted(requirementText, testCasesJson);

        logger.info("Starting test case review...");

        APIResponse response = ollamaClient.ask(systemPrompt, userPrompt);
        String responseBody = response.text();

        logger.info("Received review response from Ollama.");

        return parseReviewResult(responseBody);
    }

    private ReviewResult parseReviewResult(String response) {

        JsonObject responseObject =
                JsonParser.parseString(response)
                        .getAsJsonObject();

        JsonObject messageObject =
                responseObject.getAsJsonObject("message");

        String content =
                messageObject
                        .get("content")
                        .getAsString();

        System.out.println("Raw Review Content:");
        System.out.println(content);

        // Remove Markdown code fences
        content = content
                .replace("```json", "")
                .replace("```", "")
                .trim();

        // Extract only the JSON object
        int startIndex = content.indexOf("{");
        int endIndex = content.lastIndexOf("}");

        if (startIndex == -1 || endIndex == -1) {
            throw new RuntimeException(
                    "Could not find JSON object in Ollama review response:\n"
                            + content
            );
        }

        content =
                content.substring(startIndex, endIndex + 1);

        System.out.println("Clean Review JSON:");
        System.out.println(content);

        JsonObject reviewJson =
                JsonParser.parseString(content)
                        .getAsJsonObject();

        return gson.fromJson(
                reviewJson,
                ReviewResult.class
        );
    }

    public void close() {
        ollamaClient.close();
    }
}