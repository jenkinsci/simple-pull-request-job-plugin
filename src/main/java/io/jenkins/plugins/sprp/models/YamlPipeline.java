package io.jenkins.plugins.sprp.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class YamlPipeline {
    private Agent agent;
    private Configuration configuration;
    private LinkedHashMap<String, ArrayList<Step>> stages;
    private String gitCredentialId;
    private ArtifactPublishingConfig artifactPublishingConfig;
    private ArrayList<String> archiveArtifacts;
    private ArrayList<String> reports;
    private ArrayList<HashMap<String, String>> publishArtifacts;
    private String findBugs;
    private Environment environment;

    YamlPipeline(){}

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

    public LinkedHashMap<String, ArrayList<Step>> getStages() {
        return stages;
    }

    public void setStages(LinkedHashMap<String, ArrayList<Object>> passedStages) {
        this.stages = new LinkedHashMap<String, ArrayList<Step>>();

        for(Map.Entry<String, ArrayList<Object>> stage: passedStages.entrySet()){
            ArrayList<Step> stepList = new ArrayList<>();

            for(Object obj: stage.getValue()){
                LinkedHashMap<String, Object> stepObj = (LinkedHashMap<String, Object>) obj;
                Step step = new Step();

                for(Map.Entry<String, Object> entry: stepObj.entrySet()){
                    step.setStepName(entry.getKey());
                    if(entry.getValue().getClass() == LinkedHashMap.class){
                        step.setParameters((HashMap<String, Object>) entry.getValue());
                    }
                    else {
                        step.setDefaultParameter(entry.getValue().toString());
                    }
                }

                stepList.add(step);
            }

            stages.put(stage.getKey(), stepList);
        }

        System.out.println("asdfasdf");
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
}
