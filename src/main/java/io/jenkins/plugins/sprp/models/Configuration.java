package io.jenkins.plugins.sprp.models;

import java.util.ArrayList;

public class Configuration {
    private boolean buildPROnly;
    private boolean pushPROnSuccess;
    private ArrayList<String> prApprovers;

    public ArrayList<String> getPrApprovers() {
        return prApprovers;
    }

    public void setPrApprovers(ArrayList<String> prApprovers) {
        this.prApprovers = prApprovers;
    }

    public boolean isBuildPROnly() {
        return buildPROnly;
    }

    public void setBuildPROnly(boolean buildPROnly) {
        this.buildPROnly = buildPROnly;
    }

    public boolean isPushPROnSuccess() {
        return pushPROnSuccess;
    }

    public void setPushPROnSuccess(boolean pushPROnSuccess) {
        this.pushPROnSuccess = pushPROnSuccess;
    }
}
