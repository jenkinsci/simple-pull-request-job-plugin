package io.jenkins.plugins.sprp.models;

import java.util.ArrayList;

public class Stage {
    private String name;
    private ArrayList<Step> steps;
    private ArrayList<String> failure;
    private ArrayList<String> success;
    private ArrayList<String> always;

    Stage(){}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<String> getFailure() {
        return failure;
    }

    public void setFailure(ArrayList<String> failure) {
        this.failure = failure;
    }

    public ArrayList<String> getAlways() {
        return always;
    }

    public void setAlways(ArrayList<String> always) {
        this.always = always;
    }

    public ArrayList<Step> getSteps() {
        return steps;
    }

    public void setSteps(ArrayList<Step> steps) {
        this.steps = steps;
    }

    public ArrayList<String> getSuccess() {
        return success;
    }

    public void setSuccess(ArrayList<String> success) {
        this.success = success;
    }
}