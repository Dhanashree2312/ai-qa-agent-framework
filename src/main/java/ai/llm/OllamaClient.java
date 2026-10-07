package ai.llm;

import com.google.gson.Gson;
import com.microsoft.playwright.APIRequest;
import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.RequestOptions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OllamaClient {

    private final APIRequestContext request;
    private final Gson gson;
    private final Playwright playwright;

    public OllamaClient() {

        playwright = Playwright.create();

        gson = new Gson();

        request = playwright.request().newContext(
                new APIRequest.NewContextOptions()
                        .setBaseURL("http://localhost:11434")
                        .setExtraHTTPHeaders(
                                Map.of(
                                        "Content-Type",
                                        "application/json"
                                )
                        )
        );
    }

    public APIResponse ask(
            String systemPrompt,
            String userPrompt) {

        Map<String, Object> systemMessage =
                new HashMap<>();

        systemMessage.put("role", "system");
        systemMessage.put("content", systemPrompt);

        Map<String, Object> userMessage =
                new HashMap<>();

        userMessage.put("role", "user");
        userMessage.put("content", userPrompt);

        Map<String, Object> requestBody =
                new HashMap<>();

        requestBody.put(
                "model",
                "qwen2.5-coder:7b"
        );

        requestBody.put(
                "messages",
                List.of(systemMessage, userMessage)
        );

        requestBody.put("stream", false);

        String jsonRequestBody =
                gson.toJson(requestBody);

        return request.post(
                "/api/chat",
                RequestOptions.create()
                        .setHeader(
                                "Content-Type",
                                "application/json"
                        )
                        .setData(jsonRequestBody)
                        .setTimeout(180000)
        );
    }

    public void close() {

        if (request != null) {
            request.dispose();
        }

        if (playwright != null) {
            playwright.close();
        }
    }
}