package io.jenkins.plugins.sprp.models;

import javax.annotation.CheckForNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class Stage {
    private String name;
    @CheckForNull
    private Agent agent;
    private ArrayList<LinkedHashMap<String, Step>> steps;
    private Post post;

    public Stage() {
    }

    static ArrayList<LinkedHashMap<String, Step>> generateSteps(ArrayList<LinkedHashMap<String, Object>> stepsList) {
        ArrayList<LinkedHashMap<String, Step>> generatedSteps = new ArrayList<>();
        for (LinkedHashMap<String, Object> yamlStepObj : stepsList) {
            LinkedHashMap<String, Step> stepObj = new LinkedHashMap<>();
            Step step = new Step();

            // Below for loop will run only once as it will have only one step
            for (Map.Entry<String, Object> entry : yamlStepObj.entrySet()) {
                step.setStepName(entry.getKey());
                if (entry.getValue().getClass() == LinkedHashMap.class) {
                    step.setParameters((HashMap<String, Object>) entry.getValue());
                } else {
                    step.setDefaultParameter(entry.getValue());
                }
            }

            stepObj.put(step.getStepName(), step);
            generatedSteps.add(stepObj);
        }

        return generatedSteps;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public ArrayList<LinkedHashMap<String, Step>> getSteps() {
        return steps;
    }

    public void setSteps(ArrayList<LinkedHashMap<String, Object>> stepsList) {
        this.steps = generateSteps(stepsList);
    }

    @CheckForNull
    public Agent getAgent() {
        return agent;
    }

    public void setAgent(Agent agent) {
        this.agent = agent;

        if (this.agent.getTools() != null) {
            throw new IllegalStateException("\"tools\" is not allowed inside a stage agent.");
        }
    }
}