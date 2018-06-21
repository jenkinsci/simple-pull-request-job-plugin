package io.jenkins.plugins.sprp.models;

import java.util.HashMap;
import java.util.Map;

public class Step {
    private String stepName;
    private String defalutParameter;
    private HashMap<String, String> parameters;

    Step(String stepName){
        this.stepName = stepName;
    }

    Step(HashMap<String, String> stepNameAndDefaultParameter){
        for(Map.Entry<String, String> entry: stepNameAndDefaultParameter.entrySet()){
            stepName = entry.getKey();
            defalutParameter = entry.getValue();
        }
    }

    public String getStepName() {
        return stepName;
    }

    public void setStepName(String stepName) {
        this.stepName = stepName;
    }

    public HashMap<String, String> getParameters() {
        return parameters;
    }

    public void setParameters(HashMap<String, String> parameters) {
        this.parameters = parameters;
    }

    public String getDefalutParameter() {
        return defalutParameter;
    }
}
