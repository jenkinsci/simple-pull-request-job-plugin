package io.jenkins.plugins.sprp;

import hudson.Launcher;
import hudson.model.Descriptor;
import io.jenkins.plugins.sprp.models.Agent;
import io.jenkins.plugins.sprp.models.ArtifactPublishingConfig;
import io.jenkins.plugins.sprp.models.Environment;
import io.jenkins.plugins.sprp.models.Credential;
import io.jenkins.plugins.sprp.models.Stage;
import io.jenkins.plugins.sprp.models.Step;
import io.jenkins.plugins.sprp.models.Post;
import org.apache.commons.lang.StringUtils;
import org.jenkinsci.plugins.casc.Configurator;
import org.jenkinsci.plugins.casc.ConfiguratorException;
import org.jenkinsci.plugins.casc.model.Mapping;
import org.jenkinsci.plugins.workflow.cps.Snippetizer;
import org.jenkinsci.plugins.workflow.steps.StepDescriptor;
import org.kohsuke.stapler.DataBoundConstructor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PipelineSnippetGenerator {
    static private Logger logger = java.util.logging.Logger.getLogger(PipelineSnippetGenerator.class.getClass().getName());
    private Launcher launcher;

    PipelineSnippetGenerator(Launcher launcher){
        this.launcher = launcher;
    }

    public String shellScript(ArrayList<String> paths){
        StringBuilder snippet;
        snippet = new StringBuilder("script {\n" + "if (isUnix()) {\n");

        for(String p: paths)
            snippet.append("sh '").append(p).append(".sh").append("'\n");

        snippet.append("} else {\n");

        for(String p: paths)
            snippet.append("bat '").append(p).append(".bat").append("'\n");

        snippet.append("}\n" + "}\n");

        return snippet.toString();

    }

    private String getCommonOptionsOfAgent(Agent agent){
        String snippet = "";

        if (agent.getLabel() != null)
            snippet += "label '" + agent.getLabel() + "'\n";

        if (agent.getCustomWorkspace() != null)
            snippet += "customWorkspace '" + agent.getCustomWorkspace() + "'\n";

        if (agent.getDockerfile() != null || agent.getDockerImage() != null)
            snippet += "reuseNode " + agent.getReuseNode() + "\n";

        return snippet;
    }

    public List<String> getAgent(Agent agent){
        ArrayList<String> agentLines = new ArrayList<>();

        if(agent == null){
            agentLines.add("agent any");
        }
        else if(agent.getAnyOrNone() != null)
            agentLines.add("agent " + agent.getAnyOrNone());
        else {

            if(agent.getDockerImage() != null){
                agentLines.add("agent {");
                agentLines.add("docker {");
                agentLines.add("image '" + agent.getDockerImage() + "'");

                if (agent.getArgs() != null)
                    agentLines.add("args '" + agent.getArgs() + "'");

                agentLines.add("alwaysPull " + agent.getAlwaysPull() + "");
                agentLines.add(getCommonOptionsOfAgent(agent));
                agentLines.add("}");
                agentLines.add("}");
            }
            else if(agent.getDockerfile() != null){
                agentLines.add("agent {");
                agentLines.add("dockerfile {");
                agentLines.add("filename '" + agent.getDockerfile() + "'");

                if (agent.getDir() != null)
                    agentLines.add("dir '" + agent.getDir() + "'");

                if (agent.getArgs() != null)
                    agentLines.add("additionalBuildArgs '" + agent.getArgs() + "'");

                agentLines.add(getCommonOptionsOfAgent(agent));
                agentLines.add("}");
                agentLines.add("}");
            }
            else if(agent.getLabel() != null && agent.getCustomWorkspace() != null){
                agentLines.add("agent {");
                agentLines.add("node{");
                agentLines.add(getCommonOptionsOfAgent(agent));
                agentLines.add("}");
                agentLines.add("}");
            }
            else {
                agentLines.add("agent any");
            }

        }

        if(agent != null)
            agentLines.addAll(getTools(agent.getTools()));

        return agentLines;
    }

    public List<String> getTools(HashMap<String, String> tools){
        ArrayList<String> snippetLines = new ArrayList<>();

        if(tools == null)
            return snippetLines;

        snippetLines.add("tools {");

        for(Map.Entry<String, String> entry: tools.entrySet()){
            snippetLines.add(entry.getKey() + " '" + entry.getValue() + "'");
        }

        snippetLines.add("}");

        return snippetLines;
    }

    public List<String> getEnvironment(Environment environment){
        ArrayList<String> snippetLines = new ArrayList<>();

        if(environment == null || (environment.getVariables() == null && environment.getCredentials() == null))
            return snippetLines;

        snippetLines.add("environment {");

        for(Map.Entry<String, String> entry: environment.getVariables().entrySet()){
            snippetLines.add(entry.getKey() + " = '" + entry.getValue() + "'");
        }

        for(Credential credential: environment.getCredentials()){
            snippetLines.add(credential.getVariable() + " = credentials('" + credential.getCredentialId() + "')");
        }

        snippetLines.add("}");

        return snippetLines;
    }

    public List<String> getPostSection(Post postSection) throws NoSuchMethodException, ConfiguratorException,
            InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchFieldException {
        ArrayList<String> snippetLines = new ArrayList<>();

        if(postSection == null) {
            return snippetLines;
        }

        snippetLines.add("post {");

        if(postSection.getAborted() != null){
            snippetLines.add("aborted {");
            snippetLines.addAll(getSteps(postSection.getAborted()));
            snippetLines.add("}");
        }

        if(postSection.getAlways() != null){
            snippetLines.add("always {");
            snippetLines.addAll(getSteps(postSection.getAlways()));
            snippetLines.add("}");
        }

        if(postSection.getChanged() != null){
            snippetLines.add("changed {");
            snippetLines.addAll(getSteps(postSection.getChanged()));
            snippetLines.add("}");
        }

        if(postSection.getCleanup() != null){
            snippetLines.add("cleanup {");
            snippetLines.addAll(getSteps(postSection.getCleanup()));
            snippetLines.add("}");
        }

        if(postSection.getFailure() != null){
            snippetLines.add("failure {");
            snippetLines.addAll(getSteps(postSection.getFailure()));
            snippetLines.add("}");
        }

        if(postSection.getFixed() != null){
            snippetLines.add("fixed {");
            snippetLines.addAll(getSteps(postSection.getFixed()));
            snippetLines.add("}");
        }

        if(postSection.getRegression() != null){
            snippetLines.add("regression {");
            snippetLines.addAll(getSteps(postSection.getRegression()));
            snippetLines.add("}");
        }

        if(postSection.getSuccess() != null){
            snippetLines.add("success {");
            snippetLines.addAll(getSteps(postSection.getSuccess()));
            snippetLines.add("}");
        }

        if(postSection.getUnstable() != null){
            snippetLines.add("unstable {");
            snippetLines.addAll(getSteps(postSection.getUnstable()));
            snippetLines.add("}");
        }

        snippetLines.add("}");

        return snippetLines;
    }

    public List<String> getArchiveArtifactsSnippet(ArrayList<String> paths){
        ArrayList<String> snippetLines = new ArrayList<>();

        for(String p: paths)
            snippetLines.add("archiveArtifacts artifacts: '" + p + "'");

        return snippetLines;
    }

    public List<String> getPublishReportSnippet(ArrayList<String> paths){
        ArrayList<String> snippetLines = new ArrayList<>();

        for(String p: paths)
            snippetLines.add("junit '" + p + "'");

        return snippetLines;
    }

    List<String> getSteps(ArrayList<LinkedHashMap<String, Step>> steps) throws InvocationTargetException, NoSuchMethodException,
            InstantiationException, ConfiguratorException, IllegalAccessException, NoSuchFieldException {
        ArrayList<String> snippetLines = new ArrayList<>();

        for(LinkedHashMap<String, Step> step: steps)
            for(Map.Entry<String, Step> entry: step.entrySet())
                snippetLines.add(stepConfigurator(entry.getValue()));

        return snippetLines;
    }

    public boolean isUnix(){
        return this.launcher.isUnix();
    }

    private String completeShellScriptPath(String scriptPath){
        if (isUnix()) {
            return scriptPath + ".sh";
        } else {
            return scriptPath + ".bat";
        }
    }

    private String stepConfigurator(Step step)
            throws IllegalAccessException, InvocationTargetException,
            InstantiationException, ConfiguratorException, NoSuchFieldException {
        if(step == null)
            return "\n";

        String snippet;
        Object stepObject = null;
        Descriptor stepDescriptor = StepDescriptor.byFunctionName(step.getStepName());

        if(stepDescriptor == null)
            throw new RuntimeException(new IllegalStateException("No step exist with the name of " + step.getStepName()));

        Class clazz = stepDescriptor.clazz;

        if(step.getStepName().equals("sh")){
            if(step.getDefaultParameter() != null) {
                step.setDefaultParameter(completeShellScriptPath(step.getDefaultParameter()));
            }
            else{
                step.getParameters().put("script", completeShellScriptPath(step.getParameters().get("script").toString()));
            }
        }

        // Right now all the DefaultParameter of a step are considered to be string.
        if(step.getDefaultParameter() != null){
            Constructor[] constructors = clazz.getConstructors();
            boolean foundSuitableConstructor = false;

            for(int i = 0; i < constructors.length && !foundSuitableConstructor; i++){
                Annotation[] annotations = constructors[i].getAnnotations();
                for(int j = 0; j < annotations.length && !foundSuitableConstructor; j++){
                    if(annotations[j].annotationType() == DataBoundConstructor.class && constructors[i].getParameterCount() == 1){
                        foundSuitableConstructor = true;
                        Class parameterClass = constructors[i].getParameters()[0].getType();

                        if(parameterClass == String.class)
                            stepObject = constructors[i].newInstance(step.getDefaultParameter());
                        else if(parameterClass == Boolean.class)
                            stepObject = constructors[i].newInstance(Boolean.parseBoolean(step.getDefaultParameter()));
                        else if(parameterClass == Float.class)
                            stepObject = constructors[i].newInstance(Float.parseFloat(step.getDefaultParameter()));
                        if(parameterClass == Double.class)
                            stepObject = constructors[i].newInstance(Double.parseDouble(step.getDefaultParameter()));
                        else if(parameterClass == Integer.class)
                            stepObject = constructors[i].newInstance(Integer.parseInt(step.getDefaultParameter()));
                        else if(parameterClass == boolean.class)
                            stepObject = constructors[i].newInstance(Boolean.parseBoolean(step.getDefaultParameter()));
                        else if(parameterClass == float.class)
                            stepObject = constructors[i].newInstance(Float.parseFloat(step.getDefaultParameter()));
                        else if(parameterClass == double.class)
                            stepObject = constructors[i].newInstance(Double.parseDouble(step.getDefaultParameter()));
                        else if(parameterClass == int.class)
                            stepObject = constructors[i].newInstance(Integer.parseInt(step.getDefaultParameter()));
                        else
                            logger.log(Level.WARNING, parameterClass.getName() + " is not supported at this time.");
                    }
                }
            }
        }
        else{
            Mapping mapping = new Mapping();

            for(Map.Entry<String, Object> entry: step.getParameters().entrySet()){
                Class stepFieldClass = clazz.getDeclaredField(entry.getKey()).getType();

                if(stepFieldClass == String.class)
                    mapping.put(entry.getKey(), (String) entry.getValue());
                else if(stepFieldClass == Boolean.class)
                    mapping.put(entry.getKey(), (Boolean) entry.getValue());
                else if(stepFieldClass == Float.class)
                    mapping.put(entry.getKey(), (Float) entry.getValue());
                if(stepFieldClass == Double.class)
                    mapping.put(entry.getKey(), (Double) entry.getValue());
                else if(stepFieldClass == Integer.class)
                    mapping.put(entry.getKey(), (Integer) entry.getValue());
                else
                    logger.log(Level.WARNING, stepFieldClass.getName() + " is not supported at this time.");
            }

            Configurator configurator = Configurator.lookup(clazz);
            if (configurator != null) {
                stepObject = configurator.configure(mapping);
            }
            else{
                throw new IllegalStateException("No step with name '" + step.getStepName() +
                        "' exist. Have you installed required plugin.");
            }
        }

        if (stepObject == null)
            throw new IllegalStateException("Cannot find a step named " + step.getStepName() + " with suitable parameters.");

        snippet = Snippetizer.object2Groovy(stepObject);
        return snippet;
    }

    public List<String> getStage(
            Stage stage,
            ArrayList<String> buildResultPaths,
            ArrayList<String> archiveArtifacts,
            GitConfig gitConfig,
            String findbugs
    ) throws NoSuchMethodException, InstantiationException, IllegalAccessException, ConfiguratorException,
            InvocationTargetException, NoSuchFieldException
    {
        String stageName = stage.getName();

        ArrayList<String> snippetLines = new ArrayList<>();
        snippetLines.add("stage('" + stageName + "') {");

        snippetLines.addAll(getAgent(stage.getAgent()));

        snippetLines.add("steps {");
        snippetLines.addAll(getSteps(stage.getSteps()));
        snippetLines.add("}");

        snippetLines.addAll(getPostSection(stage.getPost()));

        snippetLines.add("}");

        return snippetLines;
    }

    public List<String> getPublishReportsAndArtifactStage(ArrayList<String> reports, ArtifactPublishingConfig config,
                                          ArrayList<HashMap<String, String>> publishArtifacts){
        if(config == null)
            return null;

        ArrayList<String> snippetLines = new ArrayList<>();

        snippetLines.add("steps {");
        snippetLines.addAll(getPublishReportSnippet(reports));
        snippetLines.add("" + "withCredentials([file(credentialsId: '" + config.getCredentialId() + "', variable: 'FILE')]) {");

        for(HashMap<String, String> artifact: publishArtifacts){
            snippetLines.add("sh 'scp -i $FILE " + artifact.get("from") + " " + config.getUser() + "@" + config.getHost() + ":" + artifact.get("to") + "'");
        }

        snippetLines.add("}");
        snippetLines.add("}");
        snippetLines.add("}");

        return snippetLines;
    }

    public String autoAddTabs(ArrayList<String> snippetLines){
        int numOfTabs = 0;
        StringBuilder snippet = new StringBuilder();

//        for(int i = 0; i < snippetLines.length(); i++){
//            if(snippetLines.charAt(i) == '{'){
//                numOfTabs++;
//            }
//
//            if(i + 1 != snippetLines.length() &&
//                    snippetLines.charAt(i) == '\n' &&
//                    snippetLines.charAt(i + 1) == '}'){
//                numOfTabs--;
//            }
//
//            if(snippetLines.charAt(i) == '\n'){
//                snippetLines.insert(i + 1, StringUtils.repeat("\t", numOfTabs));
//            }
//        }

        for(String str: snippetLines){
            if(str.startsWith("}")){
                numOfTabs--;
            }

            if(numOfTabs != 0){
                snippet.append(StringUtils.repeat("\t", numOfTabs));
            }

            snippet.append(str).append("\n");

            if(str.endsWith("{")){
                numOfTabs++;
            }
        }

        return snippet.toString();
    }
}
