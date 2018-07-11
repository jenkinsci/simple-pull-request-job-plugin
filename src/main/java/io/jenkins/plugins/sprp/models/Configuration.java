package io.jenkins.plugins.sprp.models;

import java.util.ArrayList;

public class Configuration {
    private boolean pushPrOnSuccess;
    private ArrayList<String> prApprovers;

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
