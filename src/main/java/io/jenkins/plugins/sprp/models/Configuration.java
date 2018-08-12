package io.jenkins.plugins.sprp.models;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class Configuration {
    private boolean pushPrOnSuccess;
    private ArrayList<String> prApprovers;

    private String gitCredentialId;
    private ArtifactPublishingConfig artifactPublishingConfig;
    private ArrayList<String> archiveArtifacts;
    private ArrayList<String> reports;
    private ArrayList<HashMap<String, String>> publishArtifacts;
    private String findBugs;

    @CheckForNull
    private ArrayList<CustomPipelineSection> sections;

    public ArrayList<String> getReports() {
        return reports;
    }

    public void setReports(ArrayList<String> reports) {
        this.reports = reports;
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

    public String getFindBugs() {
        return findBugs;
    }

    public void setFindBugs(String findBugs) {
        this.findBugs = findBugs;
    }

    public ArrayList<String> getArchiveArtifacts() {
        return archiveArtifacts;
    }

    public void setArchiveArtifacts(ArrayList<String> archiveArtifacts) {
        this.archiveArtifacts = archiveArtifacts;
    }

    @Nonnull
    public ArrayList<CustomPipelineSection> getSections() {
        return this.sections != null ? this.sections : (ArrayList<CustomPipelineSection>) Collections.<CustomPipelineSection>emptyList();
    }

    public void setSections(@CheckForNull ArrayList<CustomPipelineSection> sections) {
        this.sections = sections;
    }

    public ArrayList<String> getPrApprovers() {
        return prApprovers;
    }

    public void setPrApprovers(ArrayList<String> prApprovers) {
        this.prApprovers = prApprovers;
    }

    public boolean isPushPrOnSuccess() {
        return pushPrOnSuccess;
    }

    public void setPushPrOnSuccess(boolean pushPrOnSuccess) {
        this.pushPrOnSuccess = pushPrOnSuccess;
    }
}
