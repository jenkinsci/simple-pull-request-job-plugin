package io.jenkins.plugins.sprp.models;

import java.util.ArrayList;
import java.util.HashMap;

public class YamlPipeline {
    private Agent agent;
    private ArrayList<Stage> stages;
    private String gitCredentialId;
    private ArtifactPublishingConfig artifactPublishingConfig;
    private ArrayList<String> archiveArtifacts;
    private ArrayList<String> buildResultPaths;
    private ArrayList<String> testResultPaths;
    private ArrayList<HashMap<String, String>> publishArtifacts;
    private String findBugs;

    YamlPipeline(){}


    public Agent getAgent() {
        return agent;
    }

    public void setAgent(Agent agent) {
        this.agent = agent;
    }

    public String getGitCredentialId() {
        return gitCredentialId;
    }

    public void setGitCredentialId(String gitCredentialId) {
        this.gitCredentialId = gitCredentialId;
    }

    public ArtifactPublishingConfig getArtifactPublishingConfig() {
        return artifactPublishingConfig;
    }

    public void setArtifactPublishingConfig(ArtifactPublishingConfig artifactPublishingConfig) {
        this.artifactPublishingConfig = artifactPublishingConfig;
    }

    public ArrayList<HashMap<String, String>> getPublishArtifacts() {
        return publishArtifacts;
    }

    public void setPublishArtifacts(ArrayList<HashMap<String, String>> publishArtifacts) {
        this.publishArtifacts = publishArtifacts;
    }

    public ArrayList<String> getArchiveArtifacts() {
        return archiveArtifacts;
    }

    public void setArchiveArtifacts(ArrayList<String> archiveArtifacts) {
        this.archiveArtifacts = archiveArtifacts;
    }

    public ArrayList<Stage> getStages() {
        return stages;
    }

    public void setStages(ArrayList<Stage> stages) {
        this.stages = stages;
    }

    public ArrayList<String> getBuildResultPaths() {
        return buildResultPaths;
    }

    public void setBuildResultPaths(ArrayList<String> buildResultPaths) {
        this.buildResultPaths = buildResultPaths;
    }

    public ArrayList<String> getTestResultPaths() {
        return testResultPaths;
    }

    public void setTestResultPaths(ArrayList<String> testResultPaths) {
        this.testResultPaths = testResultPaths;
    }

    public String getFindBugs() {
        return findBugs;
    }

    public void setFindBugs(String findBugs) {
        this.findBugs = findBugs;
    }
}
