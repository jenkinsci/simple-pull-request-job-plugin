package io.jenkins.plugins.sprp.models;

import javax.annotation.Nonnull;
import java.util.AbstractMap;
import java.util.HashMap;

public class Step {
    @Nonnull
    private String stepName;
    private String defaultParameter;
    private HashMap<String, Object> parameters;

    Step(){
        stepName = "";
    }

    Step(String stepName){
        if(!stepName.contains(" "))
            this.stepName = stepName;
        else{
            String[] str = stepName.split(" ", 0);
            this.stepName = str[0];

            boolean foundSecond = false;

            for(int i = 1; i < str.length && !foundSecond; i++){
                if(!str[i].equals(" ")) {
                    foundSecond = true;
                    this.defaultParameter = str[i];
                }
            }

            if(!foundSecond)
                throw new IllegalStateException("No parameter provided for step " + this.stepName);

            // If below condition is failed, assuming there are now quotes. If the input is illegal then it
            // will be caught in PipelineSnippetGenerator class
            if((this.defaultParameter.startsWith("'") && this.defaultParameter.endsWith("'"))
                    || (this.defaultParameter.startsWith("\"") && this.defaultParameter.endsWith("\"")))
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