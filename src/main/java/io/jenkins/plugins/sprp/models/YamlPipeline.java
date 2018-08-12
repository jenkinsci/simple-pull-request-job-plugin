package io.jenkins.plugins.sprp.models;

import javax.annotation.Nonnull;
import java.util.*;

public class YamlPipeline {
    private Agent agent;
    private ArrayList<Stage> stages;
    private Post post;
    private ArrayList<LinkedHashMap<String, Step>> steps;
    private Configuration configuration;
    private Environment environment;

    public YamlPipeline() {
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
        return configuration.getGitCredentialId();
    }

    public ArtifactPublishingConfig getArtifactPublishingConfig() {
        return configuration.getArtifactPublishingConfig();
    }

    public ArrayList<HashMap<String, String>> getPublishArtifacts() {
        return configuration.getPublishArtifacts();
    }

    public ArrayList<String> getArchiveArtifacts() {
        return configuration.getArchiveArtifacts();
    }

    public ArrayList<Stage> getStages() {
        return stages;
    }

    public void setStages(ArrayList<Stage> passedStages) {
        this.stages = passedStages;
    }

    public ArrayList<String> getReports() {
        return configuration.getReports();
    }

    public String getFindBugs() {
        return configuration.getFindBugs();
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

    @Nonnull
    public List<CustomPipelineSection> getSections() {
        return configuration != null ? configuration.getSections() : Collections.<CustomPipelineSection>emptyList();
    }
}
