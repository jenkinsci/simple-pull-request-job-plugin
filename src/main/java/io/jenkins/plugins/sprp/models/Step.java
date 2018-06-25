package io.jenkins.plugins.sprp.models;

import java.util.AbstractMap;
import java.util.HashMap;

public class Step {
    private String stepName;
    private String defaultParameter;
    private HashMap<String, Object> parameters;

    Step(){

    }

    Step(String stepName){
        System.out.println("stepname: " + stepName);
        if(!stepName.contains(" "))
            this.stepName = stepName;
        else{
            String[] str = stepName.split(" ", 0);
            this.stepName = str[0];
            this.defaultParameter = str[1];

            if(this.defaultParameter.startsWith("'") || this.defaultParameter.startsWith("\""))
                this.defaultParameter = this.defaultParameter.substring(1, this.defaultParameter.length() - 1);
        }
    }

    Step(AbstractMap.SimpleEntry<String, String> stepNameAndDefaultParameter){
            stepName = stepNameAndDefaultParameter.getKey();
            defaultParameter = stepNameAndDefaultParameter.getValue();
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