package io.jenkins.plugins.sprp.models;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class Post {
    private ArrayList<LinkedHashMap<String, Step>> always;
    private ArrayList<LinkedHashMap<String, Step>> changed;
    private ArrayList<LinkedHashMap<String, Step>> fixed;
    private ArrayList<LinkedHashMap<String, Step>> regression;
    private ArrayList<LinkedHashMap<String, Step>> aborted;
    private ArrayList<LinkedHashMap<String, Step>> failure;
    private ArrayList<LinkedHashMap<String, Step>> success;
    private ArrayList<LinkedHashMap<String, Step>> unstable;
    private ArrayList<LinkedHashMap<String, Step>> cleanup;

    public ArrayList<LinkedHashMap<String, Step>> getAborted() {
        return aborted;
    }

    public void setAborted(ArrayList<LinkedHashMap<String, Object>> aborted) {
        this.aborted = Stage.generateSteps(aborted);
    }

    public ArrayList<LinkedHashMap<String, Step>> getAlways() {
        return always;
    }

    public void setAlways(ArrayList<LinkedHashMap<String, Object>> always) {
        this.always = Stage.generateSteps(always);
    }

    public ArrayList<LinkedHashMap<String, Step>> getChanged() {
        return changed;
    }

    public void setChanged(ArrayList<LinkedHashMap<String, Object>> changed) {
        this.changed = Stage.generateSteps(changed);
    }

    public ArrayList<LinkedHashMap<String, Step>> getCleanup() {
        return cleanup;
    }


    public void setCleanup(ArrayList<LinkedHashMap<String, Object>> cleanup) {
        this.cleanup = Stage.generateSteps(cleanup);
    }

    public ArrayList<LinkedHashMap<String, Step>> getFailure() {
        return failure;
    }

    public void setFailure(ArrayList<LinkedHashMap<String, Object>> failure) {
        this.failure = Stage.generateSteps(failure);
    }

    public ArrayList<LinkedHashMap<String, Step>> getFixed() {
        return fixed;
    }

    public void setFixed(ArrayList<LinkedHashMap<String, Object>> fixed) {
        this.fixed = Stage.generateSteps(fixed);
    }

    public ArrayList<LinkedHashMap<String, Step>> getRegression() {
        return regression;
    }

    public void setRegression(ArrayList<LinkedHashMap<String, Object>> regression) {
        this.regression = Stage.generateSteps(regression);
    }

    public ArrayList<LinkedHashMap<String, Step>> getSuccess() {
        return success;
    }

    public void setSuccess(ArrayList<LinkedHashMap<String, Object>> success) {
        this.success = Stage.generateSteps(success);
    }

    public ArrayList<LinkedHashMap<String, Step>> getUnstable() {
        return unstable;
    }

    public void setUnstable(ArrayList<LinkedHashMap<String, Object>> unstable) {
        this.unstable = Stage.generateSteps(unstable);
    }
}
