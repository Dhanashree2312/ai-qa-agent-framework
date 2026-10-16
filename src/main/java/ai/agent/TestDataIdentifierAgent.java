package ai.agent;

import ai.llm.OllamaClient;
import ai.model.TestCase;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.microsoft.playwright.APIResponse;
import net.datafaker.Faker;

import java.util.List;

public class TestDataIdentifierAgent {

    private OllamaClient ollamaClient;
    private final Gson gson;
    private final Faker faker;

    public TestDataIdentifierAgent() {

        gson = new Gson();
        ollamaClient = new OllamaClient();
        faker = new Faker();
    }

    // ---------------------------------------------------------
    // STEP 1: Identify required test data using LLM
    // ---------------------------------------------------------

    public String generateData(List<TestCase> testCases) {

        String testCaseListToString = gson.toJson(testCases);

        System.out.println("Input Test Cases:");
        System.out.println(testCaseListToString);

        String systemPrompt =
                """
                Act as a QA test data analyst.

                Review the provided test cases and identify the test data
                required for each test case.

                Do NOT return the complete test case.

                Return only the test case name and the test data requirements.

                IMPORTANT RULES:

                1. Identify data based on the actual test scenario.

                2. For positive scenarios:
                   - Use "validUsername" for a valid existing username.
                   - Use "validPassword" for the valid password corresponding
                     to that username.

                3. For negative login scenarios:
                   - If the username must be invalid, use "invalidUsername".
                   - If the password must be invalid, use "invalidPassword".
                   - If the scenario requires a valid username with an invalid
                     password, use both "validUsername" and "invalidPassword".
                   - If the scenario requires an invalid username with a valid
                     password, use both "invalidUsername" and "validPassword".

                4. Do not use generic field names such as "username" or
                   "password" when a more specific field name such as
                   "validUsername" or "invalidPassword" can be used.

                5. Do not return actual test data values.

                6. Describe what kind of data is required.

                7. Keep the original test case name.

                Use exactly this JSON structure:

                [
                  {
                    "testcaseName": "Test case name",
                    "dataRequirements": {
                      "validUsername": "Description",
                      "validPassword": "Description"
                    }
                  }
                ]

                Return one object for every test case.

                Return ONLY valid JSON.
                Do not use Markdown.
                Do not use ```json.
                Do not include explanations.
                """;

        String userPrompt =
                """
                Identify the test data requirements for these test cases.

                Test cases:
                """ + testCaseListToString;

        APIResponse response =
                ollamaClient.ask(systemPrompt, userPrompt);

        System.out.println("Raw Ollama Response:");
        System.out.println(response.text());

        JsonObject ollamaResponse =
                gson.fromJson(response.text(), JsonObject.class);

        String content =
                ollamaResponse
                        .getAsJsonObject("message")
                        .get("content")
                        .getAsString();

        System.out.println("LLM Data Requirements:");
        System.out.println(content);

        return content;
    }


    // ---------------------------------------------------------
    // STEP 2: Generate actual synthetic test data
    // ---------------------------------------------------------

    public String generateSyntheticData(String content) {

        // Remove Markdown code fences if the LLM still returns them
        content = content
                .replace("```json", "")
                .replace("```", "")
                .trim();

        // Find the JSON array inside the response
        int startIndex = content.indexOf("[");
        int endIndex = content.lastIndexOf("]");

        if (startIndex == -1 || endIndex == -1) {

            throw new RuntimeException(
                    "Could not find JSON array in LLM response:\n"
                            + content
            );
        }

        // Keep only the JSON array
        content =
                content.substring(startIndex, endIndex + 1);

        System.out.println("Clean JSON:");
        System.out.println(content);

        JsonArray dataArray =
                gson.fromJson(content, JsonArray.class);

        JsonArray resultArray =
                new JsonArray();

        // Process every test case
        for (JsonElement element : dataArray) {

            JsonObject object =
                    element.getAsJsonObject();

            String testcaseName =
                    object
                            .get("testcaseName")
                            .getAsString();

            JsonObject dataRequirements =
                    object
                            .getAsJsonObject("dataRequirements");

            JsonObject testData =
                    new JsonObject();

            // Process every required data field
            for (String fieldName :
                    dataRequirements.keySet()) {

                String generatedValue = null;

                // -----------------------------------------
                // Invalid username
                // -----------------------------------------

                if (fieldName.equals("invalidUsername")) {

                    generatedValue =
                            faker.name().username();
                }

                // -----------------------------------------
                // Invalid password
                // -----------------------------------------

                else if (fieldName.equals("invalidPassword")) {

                    generatedValue =
                            faker.internet().password();
                }

                // -----------------------------------------
                // Valid username
                // -----------------------------------------

                else if (fieldName.equals("validUsername")) {

                    /*
                     * Faker cannot guarantee that a generated
                     * username actually exists in the application.
                     *
                     * Therefore, for now we use a placeholder.
                     *
                     * Later this can come from:
                     * - test configuration
                     * - API
                     * - database
                     * - environment variables
                     */

                    generatedValue =
                            "EXISTING_VALID_USER";
                }

                // -----------------------------------------
                // Valid password
                // -----------------------------------------

                else if (fieldName.equals("validPassword")) {

                    /*
                     * Same reason as validUsername.
                     */

                    generatedValue =
                            "EXISTING_VALID_PASSWORD";
                }

                // -----------------------------------------
                // Fallback
                // -----------------------------------------

                else {

                    /*
                     * If the LLM returns an unexpected field,
                     * generate a generic username.
                     */

                    generatedValue =
                            faker.name().username();
                }

                testData.addProperty(
                        fieldName,
                        generatedValue
                );
            }

            // Create final object for this test case
            JsonObject resultObject =
                    new JsonObject();

            resultObject.addProperty(
                    "testcaseName",
                    testcaseName
            );

            resultObject.add(
                    "testData",
                    testData
            );

            resultArray.add(resultObject);
        }

        String finalResult =
                gson.toJson(resultArray);

        System.out.println(
                "Generated Synthetic Test Data:"
        );

        System.out.println(finalResult);

        return finalResult;
    }


    // ---------------------------------------------------------
    // STEP 3: Close Ollama client
    // ---------------------------------------------------------

    public void close() {

        if (ollamaClient != null) {

            ollamaClient.close();
        }
    }
}