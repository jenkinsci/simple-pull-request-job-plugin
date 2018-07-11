package io.jenkins.plugins.sprp.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class YamlPipeline {
    private Agent agent;
    private Configuration configuration;
    private ArrayList<Stage> stages;
    private String gitCredentialId;
    private ArtifactPublishingConfig artifactPublishingConfig;
    private ArrayList<String> archiveArtifacts;
    private ArrayList<String> reports;
    private ArrayList<HashMap<String, String>> publishArtifacts;
    private String findBugs;
    private Environment environment;
    private Post post;
    private ArrayList<LinkedHashMap<String, Step>> steps;

    YamlPipeline() {
    }

    public Agent getAgent() {
        return agent;
    }

    public void setAgent(Agent agent) {
        this.agent = agent;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
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

    public void setStages(ArrayList<Stage> passedStages) {
        this.stages = passedStages;
    }

    public ArrayList<String> getReports() {
        return reports;
    }

    public void setReports(ArrayList<String> reports) {
        this.reports = reports;
    }

    public String getFindBugs() {
        return findBugs;
    }

    public void setFindBugs(String findBugs) {
        this.findBugs = findBugs;
    }

    public Environment getEnvironment() {
        return environment;
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public ArrayList<LinkedHashMap<String, Step>> getSteps() {
        return steps;
    }

    public void setSteps(ArrayList<LinkedHashMap<String, Object>> steps) {
        this.steps = Stage.generateSteps(steps);
    }
}
