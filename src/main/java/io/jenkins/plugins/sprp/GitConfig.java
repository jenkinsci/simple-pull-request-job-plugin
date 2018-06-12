package io.jenkins.plugins.sprp;

public class GitConfig {
    private String gitUrl;
    private String gitBranch;
    private String credentialsId;

    GitConfig(){}

    public void setCredentialsId(String credentialsId) {
        this.credentialsId = credentialsId;
    }

    public String getCredentialsId() {
        return credentialsId;
    }

    public void setGitBranch(String gitBranch) {
        this.gitBranch = gitBranch;
    }

    public String getGitBranch() {
        return gitBranch;
    }

    public void setGitUrl(String gitUrl) {
        this.gitUrl = gitUrl;
    }

    public String getGitUrl() {
        return gitUrl;
    }
}
