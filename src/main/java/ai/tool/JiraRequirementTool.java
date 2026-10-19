
package ai.tool;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.microsoft.playwright.APIRequest;
import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.RequestOptions;
import framework.ConfigReader;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

public class JiraRequirementTool implements AgentTool, AutoCloseable {

    private final APIRequestContext requestContext;
    private final Playwright playwright;
    private final Gson gson;

    public JiraRequirementTool() {

        playwright = Playwright.create();

        requestContext =
                playwright.request().newContext(
                        new APIRequest.NewContextOptions()
                                .setBaseURL(
                                        ConfigReader.get("JIRA_BASE_URL")
                                )
                                .setExtraHTTPHeaders(
                                        Map.of(
                                                "Content-Type",
                                                "application/json"
                                        )
                                )
                );

        gson = new Gson();
    }

    public APIResponse getRequirement(String issueKey) {

        String email = ConfigReader.get("JIRA_EMAIL");
        String token = ConfigReader.get("JIRA_API_TOKEN");

        String credentials = email + ":" + token;

        String encodedCredentials =
                Base64.getEncoder()
                        .encodeToString(
                                credentials.getBytes(StandardCharsets.UTF_8)
                        );

        return requestContext.get(
                "/rest/api/3/issue/" + issueKey,
                RequestOptions.create()
                        .setHeader(
                                "Authorization",
                                "Basic " + encodedCredentials
                        )
        );
    }

    public String getRequirementText(String issueKey) {

        APIResponse response = getRequirement(issueKey);

        try {
            if (!response.ok()) {
                throw new RuntimeException(
                        "Failed to retrieve Jira issue. Status: "
                                + response.status()
                                + ", Response: "
                                + response.text()
                );
            }

            JsonObject issue =
                    gson.fromJson(response.text(), JsonObject.class);

            JsonObject fields = issue.getAsJsonObject("fields");

            String title = fields.get("summary").getAsString();

            JsonObject description =
                    fields.getAsJsonObject("description");

            String requirementText = extractText(description);

            System.out.println("Title: " + title);
            System.out.println("Requirement: " + requirementText);
            System.out.println("JIRA_ISSUE_KEY = " + issueKey);

            return requirementText;

        } finally {
            response.dispose();
        }
    }

    private String extractText(JsonObject description) {

        StringBuilder result = new StringBuilder();

        extractText((JsonElement) description, result);

        return result.toString().trim();
    }

    private void extractText(
            JsonElement element,
            StringBuilder result) {

        if (element == null || element.isJsonNull()) {
            return;
        }

        if (element.isJsonObject()) {

            JsonObject object = element.getAsJsonObject();

            if (object.has("text")
                    && !object.get("text").isJsonNull()) {

                result.append(
                        object.get("text").getAsString()
                );

                result.append(" ");
            }

            if (object.has("content")
                    && object.get("content").isJsonArray()) {

                JsonArray content = object.getAsJsonArray("content");

                for (JsonElement child : content) {
                    extractText(child, result);
                }
            }

        } else if (element.isJsonArray()) {

            for (JsonElement child : element.getAsJsonArray()) {
                extractText(child, result);
            }
        }
    }

    @Override
    public String getName() {
        return "jira_requirement";
    }

    @Override
    public String getDescription() {
        return "Retrieves the requirement text from a Jira issue.";
    }

    @Override
    public String execute(String input) {
        return getRequirementText(input);
    }


    @Override
    public void close() {
        try {
            requestContext.dispose();
        } finally {
            playwright.close();
        }
    }

}
