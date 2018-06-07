package io.jenkins.plugins.sprp.models;

public class Agent {
    private String label;
    private String customWorkspace;
    private String dockerImage;
    private boolean alwaysPull;
    private String args;
    private String dockerfile;
    private String dir;
    private boolean reuseNode;

    Agent(){}

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getCustomWorkspace() {
        return customWorkspace;
    }

    public void setCustomWorkspace(String customWorkspace) {
        this.customWorkspace = customWorkspace;
    }

    public String getArgs() {
        return args;
    }

    public void setArgs(String args) {
        this.args = args;
    }

    public String getDockerfile() {
        return dockerfile;
    }

    public void setDockerfile(String dockerfile) {
        this.dockerfile = dockerfile;
    }

    public String getDir() {
        return dir;
    }

    public void setDir(String dir) {
        this.dir = dir;
    }

    public String getDockerImage() {
        return this.dockerImage;
    }

    public void setDockerImage(String dockerImage) {
        this.dockerImage = dockerImage;
    }

    public boolean getAlwaysPull() {
        return alwaysPull;
    }

    public void setAlwaysPull(boolean alwaysPull) {
        this.alwaysPull = alwaysPull;
    }

    public boolean getReuseNode() {
        return this.reuseNode;
    }

    public void setReuseNode(boolean reuseNode) {
        this.reuseNode = reuseNode;
    }
}
