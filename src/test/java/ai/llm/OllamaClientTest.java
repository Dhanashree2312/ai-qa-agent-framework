package ai.llm;

import com.microsoft.playwright.APIResponse;
import org.testng.annotations.Test;

public class OllamaClientTest {
    @Test
    public void askOllama(){
        OllamaClient ollamaClient = new OllamaClient();

        String systemPrompt = """
                You are a senior QA architect with relevant years of experience designing test cases.
                Give conscise and relevant test cases.
                """;
        //String userPrompt = "Please generate test cases for testing website https://www.automation-bible.com/";
        String userPrompt = "Say hello in one sentence.";
        APIResponse response = ollamaClient.ask(systemPrompt,userPrompt);
        System.out.println("Status: " + response.status());
        System.out.println(response.text());
        //System.out.println(response.text());
        ollamaClient.close();
    }
}
