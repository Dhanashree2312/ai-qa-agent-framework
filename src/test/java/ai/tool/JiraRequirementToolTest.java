
package ai.tool;

import com.microsoft.playwright.APIResponse;
import org.testng.Assert;
import org.testng.annotations.Test;

public class JiraRequirementToolTest {

    @Test(groups = "integration")
    public void getJiraRequirement() {

        try (JiraRequirementTool jiraRequirementTool =
                     new JiraRequirementTool()) {

            APIResponse response =
                    jiraRequirementTool.getRequirement("SCRUM-5");

            try {
                System.out.println("Status: " + response.status());
                System.out.println("Response: " + response.text());

                Assert.assertTrue(
                        response.ok(),
                        "Jira request should succeed"
                );
            } finally {
                response.dispose();
            }

            String requirement =
                    jiraRequirementTool.getRequirementText("SCRUM-5");

            Assert.assertNotNull(
                    requirement,
                    "Requirement text should not be null"
            );

            Assert.assertFalse(
                    requirement.isBlank(),
                    "Requirement text should not be blank"
            );
        }
    }
}
