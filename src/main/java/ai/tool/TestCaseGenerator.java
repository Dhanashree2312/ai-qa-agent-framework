package ai.tool;

import ai.llm.OllamaClient;
import ai.model.TestCase;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.microsoft.playwright.APIResponse;

import java.lang.reflect.Type;
import java.util.List;

public class TestCaseGenerator {

    private final OllamaClient ollamaClient;

    public TestCaseGenerator() {

        ollamaClient =
                new OllamaClient();
    }

    public APIResponse ask(
            String requirementText) {



        String systemPrompt =
                """
                You are a QA engineer.
                Return only valid JSON.
                Do not use markdown or code fences.
        
                Use this structure:
                {
                  "testCases": [
                    {
                      "testcaseName": "string",
                      "testcaseDescription": "string",
                      "preConditions": "string",
                      "testSteps": [
                        {
                          "stepNumber": 1,
                          "action": "string",
                          "expectedResult": "string"
                        }
                      ],
                      "priority": "High",
                      "type": "Functional",
                      "feature": "Authentication",
                      "testData": {
                        "username": "test-user",
                        "password": "test-password"
                      }
                    }
                  ]
                }
                """;


        String userPrompt = """
Generate exactly 2 test cases for the following requirement:
%s

Return test cases in this JSON structure:
{
  "testCases": [
    {
      "testcaseName": "",
      "testcaseDescription": "",
      "preConditions": "",
      "testSteps": [
        {
          "stepNumber": 1,
          "action": "",
          "expectedResult": ""
        }
      ],
      "priority": "",
      "type": "",
      "feature": "",
      "testData": {}
    }
  ]
}

TEST DATA RULES:
1. For the positive login test case, use the keys "username" and "password".
2. For the negative login test case, include all four keys:
   "invalidUsername": "invalid-user"
   "validPassword": "test-password"
   "validUsername": "test-user"
   "invalidPassword": "wrong-password"
3. The negative test case must contain two steps:
   - Attempt login with invalidUsername and validPassword.
   - Attempt login with validUsername and invalidPassword.
4. Each step must clearly identify which test data values it uses.
5. Return valid JSON matching the existing TestCase model.

""".formatted(requirementText);


        return ollamaClient.ask(
                systemPrompt,
                userPrompt
        );
    }

    public List<TestCase> generateTestCases(
            String requirementText) {

        APIResponse response =
                ask(requirementText);

        if (!response.ok()) {

            throw new RuntimeException(
                    "Ollama request failed. Status: "
                            + response.status()
            );
        }

        Gson gson =
                new Gson();

        JsonObject ollamaResponse =
                gson.fromJson(
                        response.text(),
                        JsonObject.class
                );

        String content =
                ollamaResponse
                        .getAsJsonObject("message")
                        .get("content")
                        .getAsString();

        System.out.println(
                "LLM Content:"
        );

        System.out.println(content);

        content =
                content
                        .replace(
                                "```json",
                                ""
                        )
                        .replace(
                                "```",
                                ""
                        )
                        .trim();

        /*
         * LLM returns:
         *
         * {
         *   "testCases": [...]
         * }
         */

        JsonObject jsonObject =
                gson.fromJson(
                        content,
                        JsonObject.class
                );

        JsonArray testCaseArray =
                jsonObject.getAsJsonArray(
                        "testCases"
                );

        if (testCaseArray == null) {

            throw new RuntimeException(
                    "LLM response does not contain 'testCases'"
            );
        }

        Type type =
                new TypeToken<List<TestCase>>() {
                }.getType();

        List<TestCase> testCases =
                gson.fromJson(
                        testCaseArray.toString(),
                        type
                );

        return testCases;
    }

    public void close() {

        ollamaClient.close();
    }
}