package ai.agent;

import ai.model.TestCase;
import ai.tool.AgentTool;
import ai.tool.AgentToolRegistry;
import ai.tool.JiraRequirementTool;
import ai.tool.TestCaseExcelWriter;
import ai.tool.TestCaseGenerator;
import ai.tool.TestCaseValidator;

import java.util.List;

public class TestCaseGenerationAgent {

    private final TestCaseGenerator testCaseGenerator;
    private final TestCaseValidator testCaseValidator;
    private final TestCaseExcelWriter excelWriter;
    private final AgentToolRegistry toolRegistry;

    private static final int MAX_RETRIES = 1;

    public TestCaseGenerationAgent() {

        testCaseGenerator =
                new TestCaseGenerator();

        testCaseValidator =
                new TestCaseValidator();

        excelWriter =
                new TestCaseExcelWriter();

        toolRegistry =
                new AgentToolRegistry();

        toolRegistry.register(
                new JiraRequirementTool()
        );
    }

    public List<TestCase> generateTestCasesFromJira(
            String issueKey) {

        AgentTool jiraTool =
                toolRegistry.getTool(
                        "jira_requirement"
                );

        if (jiraTool == null) {

            throw new IllegalStateException(
                    "Jira requirement tool is not registered"
            );
        }

        System.out.println(
                "Fetching requirement from Jira..."
        );

        String requirementText =
                jiraTool.execute(issueKey);

        for (int attempt = 0;
             attempt <= MAX_RETRIES;
             attempt++) {

            System.out.println(
                    "Generating test cases. Attempt: "
                            + (attempt + 1)
            );

            List<TestCase> testCases =
                    testCaseGenerator
                            .generateTestCases(
                                    requirementText
                            );

            if (testCaseValidator
                    .validate(testCases)) {

                System.out.println(
                        "Test cases validated successfully."
                );

                excelWriter.write(
                        testCases,
                        "target/generated-test-cases.xlsx"
                );

                System.out.println(
                        "Excel file generated successfully."
                );

                return testCases;
            }

            System.out.println(
                    "Validation failed. Regenerating..."
            );
        }

        throw new IllegalStateException(
                "Test case generation failed after retry."
        );
    }
}