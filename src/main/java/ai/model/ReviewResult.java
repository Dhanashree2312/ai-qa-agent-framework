package ai.model;

import java.util.List;

public class ReviewResult {

    private String overallStatus;
    private String summary;
    private List<String> issues;
    private List<String> suggestions;

    public ReviewResult() {
    }

    public ReviewResult(String overallStatus,
                        String summary,
                        List<String> issues,
                        List<String> suggestions) {
        this.overallStatus = overallStatus;
        this.summary = summary;
        this.issues = issues;
        this.suggestions = suggestions;
    }

    public String getOverallStatus() {
        return overallStatus;
    }

    public void setOverallStatus(String overallStatus) {
        this.overallStatus = overallStatus;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public List<String> getIssues() {
        return issues;
    }

    public void setIssues(List<String> issues) {
        this.issues = issues;
    }

    public List<String> getSuggestions() {
        return suggestions;
    }

    public void setSuggestions(List<String> suggestions) {
        this.suggestions = suggestions;
    }

    @Override
    public String toString() {
        return "ReviewResult{" +
                "overallStatus='" + overallStatus + '\'' +
                ", summary='" + summary + '\'' +
                ", issues=" + issues +
                ", suggestions=" + suggestions +
                '}';
    }
}