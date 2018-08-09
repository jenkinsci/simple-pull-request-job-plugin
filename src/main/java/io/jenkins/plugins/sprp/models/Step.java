package io.jenkins.plugins.sprp.models;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import javax.annotation.Nonnull;
import java.util.HashMap;

public class Step {
    @Nonnull
    private String stepName;
    private Object defaultParameter;
    private HashMap<String, Object> parameters;

    public Step() {
        stepName = "";
    }

    @SuppressFBWarnings("RCN_REDUNDANT_NULLCHECK_OF_NONNULL_VALUE")
    @Nonnull
    public String getStepName() {
        return stepName;
    }

    public void setStepName(@Nonnull String stepName) {
        this.stepName = stepName;
    }

    public HashMap<String, Object> getParameters() {
        return parameters;
    }

    public void setParameters(HashMap<String, Object> parameters) {
        this.parameters = parameters;
    }

    public Object getDefaultParameter() {
        return defaultParameter;
    }

    public void setDefaultParameter(Object defaultParameter) {
        this.defaultParameter = defaultParameter;
    }
}