package io.jenkins.plugins.sprp.models;

// TODO: add dockerfile support

public class Agent {
    private String lable;
    private String customWorkspace;
//    Docker docker;

    Agent(){}

    public String getLable() {
        return lable;
    }

    public void setLable(String lable) {
        this.lable = lable;
    }

    public String getCustomWorkspace() {
        return customWorkspace;
    }

    public void setCustomWorkspace(String customWorkspace) {
        this.customWorkspace = customWorkspace;
    }

//    public Docker getDocker() {
//        return docker;
//    }
//
//    public void setDocker(Docker docker) {
//        this.docker = docker;
//    }
}
