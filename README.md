# AI Test Case Evaluation Framework

## Overview

The AI Test Case Evaluation Framework assesses the quality of LLM-generated test cases and estimates how effectively a generated test suite covers the original requirement.

The framework integrates with Jira, generates test cases using Agent 1, evaluates each test case using an LLM-based semantic evaluator, applies a configurable quality gate, and produces a suite-level evaluation report with requirement coverage and missing scenarios.

The project uses Java and a locally hosted Ollama model to explore AI-powered quality engineering.

## Evaluation Workflow

1. Retrieve the requirement from Jira.
2. Generate test cases using the test case generation agent.
3. Validate the generated test cases and export them to Excel.
4. Evaluate each test case against the original requirement using an LLM-based semantic evaluator.
5. Apply the semantic quality gate to classify each test case as PASS or FAIL.
6. Evaluate requirement coverage across the complete generated test suite.
7. Generate a report containing semantic scores, quality-gate results, coverage percentage, and missing scenarios.

## Evaluation Metrics

| Metric | Description |
|---|---|
| Requirement Relevance | Measures how closely a test case relates to the original requirement. |
| Expected Behavior Coverage | Assesses how well a test case addresses the expected behavior described in the requirement. |
| Step Alignment | Assesses whether the test steps and expected results align with the intended test scenario. |
| Semantic Quality Gate | Passes a test case when all configured semantic score thresholds are met. |
| Quality Gate Pass Rate | Percentage of evaluated test cases that pass the quality gate. |
| Requirement Coverage | Percentage of identified, testable requirement scenarios classified as covered by the generated test suite. |
| Missing Scenarios | Lists identified requirement scenarios that the generated suite does not adequately cover. |

The individual semantic scores range from 0 to 5. The current quality gate requires a score of at least 4 for requirement relevance, expected behavior coverage, and step alignment.

### Quality Gate Pass Rate

`(Number of passing test cases / Total evaluated test cases) × 100`

### Requirement Coverage

`(Number of covered scenarios / Total identified scenarios) × 100`

For an empty test suite, the current implementation reports a quality-gate pass rate and requirement coverage of 0%.

## Example Evaluation Report

The following results are from a successful integration run using Jira issue `SCRUM-5` for user authentication.

```text
========== AI QA EVALUATION REPORT ==========

Jira Requirement: SCRUM-5
Total Test Cases: 2
Passed Quality Gate: 2
Failed Quality Gate: 0
Quality Gate Pass Rate: 100.00%
Requirement Coverage: 66.67%

Missing Scenarios:
- Blank credentials rejected

Individual Test Case Results:
----------------------------------
Test Case: Positive Login Test
Requirement Relevance: 4/5
Expected Behavior Coverage: 4/5
Step Alignment: 5/5
Quality Gate: PASS
Reason: The test case is relevant to the requirement,
covers most expected behaviors, and the steps align well
with the requirement.

----------------------------------
Test Case: Negative Login Test
Requirement Relevance: 4/5
Expected Behavior Coverage: 4/5
Step Alignment: 4/5
Quality Gate: PASS
Reason: The test case covers invalid username and password
scenarios and aligns well with the requirement.

==============================================
```

### Interpreting the Results

- **100% quality-gate pass rate** means both generated test cases met all three configured semantic score thresholds.
- **66.67% requirement coverage** is the coverage evaluator's estimate based on the scenarios it identified in this run.
- **Blank credentials rejected** was identified as a missing scenario and highlights a gap in the generated suite.

A high quality-gate pass rate does not guarantee complete requirement coverage. The quality gate assesses individual test cases, while coverage evaluates the suite as a whole.

The coverage percentage is model-generated and should be validated against a manually defined scenario list before being treated as a reliable coverage measure.

## Technology Stack

- Java 21
- Maven
- TestNG
- Gson
- Microsoft Playwright `APIRequestContext`
- Ollama with a locally hosted LLM
- Jira REST API integration
- Apache POI for Excel report generation

## Limitations

- **LLM-dependent evaluation:** Semantic scores and scenario coverage are model-generated judgments and may vary across runs.
- **Coverage is an estimate:** A scenario classified as covered is not proof that the test case fully validates the behavior.
- **Quality-gate thresholds are configurable:** Passing the gate does not guarantee that a test case is correct, executable, or sufficient.
- **Requirement interpretation matters:** Ambiguous or incomplete Jira requirements can affect test generation and evaluation.
- **Evaluation is not execution:** This framework evaluates generated test cases; it does not establish that the application passes those tests.
- **Local model constraints:** Response quality, latency, and output formatting depend on the selected Ollama model and its configuration.
- **Scenario identification can be incomplete:** If the evaluator fails to identify a requirement scenario, the calculated coverage may be misleading.

## Future Improvements

- Validate evaluation results against manually defined expected scenarios.
- Add duplicate test-case and hallucinated-assertion detection.
- Track evaluation consistency across repeated runs.
- Improve test generation using missing-scenario feedback.
- Integrate automated tests and evaluation reports into CI/CD.
- Add reproducible test runs and versioned evaluation results.
