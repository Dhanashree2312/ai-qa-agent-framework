package ai.tool;

import com.microsoft.playwright.APIResponse;
import org.testng.annotations.Test;

public class JiraRequirementToolTest {

    @Test(groups="integration")
    public void getJiraRequirement() {

        JiraRequirementTool jiraRequirementTool =
                new JiraRequirementTool();

        APIResponse response =
                jiraRequirementTool.getRequirement("SCRUM-5");

        System.out.println("Status: " + response.status());
        System.out.println("Response: " + response.text());
        jiraRequirementTool.getRequirementText("SCRUM-5");
    }
}