package io.jenkins.plugins.sprp.models;

import java.util.AbstractMap;
import java.util.HashMap;

public class Step {
    private String stepName;
    private String defalutParameter;
    private HashMap<String, String> parameters;

    Step(){

    }

    Step(String stepName){
        this.stepName = stepName;
    }

    Step(AbstractMap.SimpleEntry<String, String> stepNameAndDefaultParameter){
            stepName = stepNameAndDefaultParameter.getKey();
            defalutParameter = stepNameAndDefaultParameter.getValue();
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

    public void setDefalutParameter(String defalutParameter) {
        this.defalutParameter = defalutParameter;
    }
}