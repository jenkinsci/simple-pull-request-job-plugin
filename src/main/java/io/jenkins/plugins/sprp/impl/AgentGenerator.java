package io.jenkins.plugins.sprp.impl;

import hudson.Extension;
import io.jenkins.plugins.sprp.PipelineGenerator;
import io.jenkins.plugins.sprp.models.Agent;
import org.jenkinsci.Symbol;

import java.util.ArrayList;
import java.util.List;

@Extension
@Symbol("agent")
public class AgentGenerator extends PipelineGenerator<Agent> {

    @Override
    public List<String> toPipeline(Agent agent) {
        ArrayList<String> agentLines = new ArrayList<>();

        if (agent == null) {
            agentLines.add("agent any");
        } else if (agent.getAnyOrNone() != null)
            agentLines.add("agent " + agent.getAnyOrNone());
        else if(agent.isNone()) {
            agentLines.add("agent none");
        }
        else {

            if (agent.getDockerImage() != null) {
                agentLines.add("agent {");
                agentLines.add("docker {");
                agentLines.add("image '" + agent.getDockerImage() + "'");

                if (agent.getArgs() != null) {
                    agentLines.add("args '" + agent.getArgs() + "'");
                }

                agentLines.add("alwaysPull " + agent.getAlwaysPull() + "");
                agentLines.addAll(getCommonOptionsOfAgent(agent));
                agentLines.add("}");
                agentLines.add("}");
            } else if (agent.getDockerfile() != null) {
                agentLines.add("agent {");
                agentLines.add("dockerfile {");
                agentLines.add("filename '" + agent.getDockerfile() + "'");

                if (agent.getDir() != null) {
                    agentLines.add("dir '" + agent.getDir() + "'");
                }

                if (agent.getArgs() != null) {
                    agentLines.add("additionalBuildArgs '" + agent.getArgs() + "'");
                }

                agentLines.addAll(getCommonOptionsOfAgent(agent));
                agentLines.add("}");
                agentLines.add("}");
            } else if (agent.getLabel() != null && agent.getCustomWorkspace() != null) {
                agentLines.add("agent {");
                agentLines.add("node {");
                agentLines.addAll(getCommonOptionsOfAgent(agent));
                agentLines.add("}");
                agentLines.add("}");
            } else {
                agentLines.add("agent any");
            }

        }

        if (agent != null) {
            agentLines.addAll(getTools(agent.getTools()));
        }

        return agentLines;
    }

    @Override
    public boolean canConvert(Object object) {
        return object instanceof Agent;
    }

    private List<String> getCommonOptionsOfAgent(Agent agent) {
        ArrayList<String> snippetLines = new ArrayList<>();

        if (agent.getLabel() != null) {
            snippetLines.add("label '" + agent.getLabel() + "'");
        }

        if (agent.getCustomWorkspace() != null) {
            snippetLines.add("customWorkspace '" + agent.getCustomWorkspace() + "'");
        }

        if (agent.getDockerfile() != null || agent.getDockerImage() != null) {
            snippetLines.add("reuseNode " + agent.getReuseNode() + "");
        }

        return snippetLines;
    }
}
