package io.jenkins.plugins.sprp.models;

import javax.annotation.Nonnull;
import java.util.HashMap;

public class Step {
    @Nonnull
    private String stepName;
    private String defaultParameter;
    private HashMap<String, Object> parameters;

    Step(){
        stepName = "";
    }

    public String getStepName() {
        return stepName;
    }

    public void setStepName(String stepName) {
        this.stepName = stepName;
    }

    public HashMap<String, Object> getParameters() {
        return parameters;
    }

    public void setParameters(HashMap<String, Object> parameters) {
        this.parameters = parameters;
    }

    public String getDefaultParameter() {
        return defaultParameter;
    }

    public void setDefaultParameter(String defaultParameter) {
        this.defaultParameter = defaultParameter;
    }
}