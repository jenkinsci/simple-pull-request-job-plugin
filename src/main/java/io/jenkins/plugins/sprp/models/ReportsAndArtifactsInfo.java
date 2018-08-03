package io.jenkins.plugins.sprp.models;

import java.util.ArrayList;
import java.util.HashMap;

public class ReportsAndArtifactsInfo {

    private ArrayList<String> reports;
    private ArtifactPublishingConfig artifactPublishingConfig;
    private ArrayList<HashMap<String, String>> publishArtifacts;

    public ArrayList<HashMap<String, String>> getPublishArtifacts() {
        return publishArtifacts;
    }

    public void setPublishArtifacts(ArrayList<HashMap<String, String>> publishArtifacts) {
        this.publishArtifacts = publishArtifacts;
    }

    public ArrayList<String> getReports() {
        return reports;
    }

    public void setReports(ArrayList<String> reports) {
        this.reports = reports;
    }

    public ArtifactPublishingConfig getArtifactPublishingConfig() {
        return artifactPublishingConfig;
    }

    public void setArtifactPublishingConfig(ArtifactPublishingConfig config) {
        this.artifactPublishingConfig = config;
    }
}
