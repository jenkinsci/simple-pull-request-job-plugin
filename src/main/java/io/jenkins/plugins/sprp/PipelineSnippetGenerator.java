package io.jenkins.plugins.sprp;

import hudson.Launcher;
import hudson.model.Descriptor;
import io.jenkins.plugins.sprp.models.Agent;
import io.jenkins.plugins.sprp.models.ArtifactPublishingConfig;
import io.jenkins.plugins.sprp.models.Stage;
import io.jenkins.plugins.sprp.models.Step;
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

    public String getAgent(Agent agent){
        String snippet = "";

        if(agent == null){
            snippet = "any\n";
        }
        else if(agent.getAnyOrNone() != null)
            snippet = agent.getAnyOrNone() + "\n";
        else {
            if(agent.getDockerImage() != null){
                snippet += "{\n";
                snippet += "docker {\n";
                snippet += "image '" + agent.getDockerImage() + "'\n";

                if (agent.getArgs() != null)
                    snippet += "args '" + agent.getArgs() + "'\n";

                snippet += "alwaysPull " + agent.getAlwaysPull() + "\n";
                snippet += getCommonOptionsOfAgent(agent);
                snippet += "}\n";
                snippet += "}\n";
            }
            else if(agent.getDockerfile() != null){
                snippet += "{\n";
                snippet += "dockerfile {\n";
                snippet += "filename '" + agent.getDockerfile() + "'\n";

                if (agent.getDir() != null)
                    snippet += "dir '" + agent.getDir() + "'\n";

                if (agent.getArgs() != null)
                    snippet += "additionalBuildArgs '" + agent.getArgs() + "'\n";

                snippet += getCommonOptionsOfAgent(agent);
                snippet += "}\n";
                snippet += "}\n";
            }
            else {
                snippet += "{\n";
                snippet += "node{\n";
                snippet += getCommonOptionsOfAgent(agent);
                snippet += "}\n";
                snippet += "}\n";
            }
        }

        return snippet;
    }

    public String getArchiveArtifactsSnippet(ArrayList<String> paths){
        StringBuilder snippet = new StringBuilder();

        for(String p: paths)
            snippet.append("archiveArtifacts artifacts: '").append(p).append("'\n");

        return snippet.toString();
    }

    public String getPublishReportSnippet(ArrayList<String> paths){
        StringBuilder snippet = new StringBuilder();

        for(String p: paths)
            snippet.append("junit '").append(p).append("'\n");

        return snippet.toString();
    }

    private String getSteps(ArrayList<Step> steps) throws InvocationTargetException, NoSuchMethodException, InstantiationException, ConfiguratorException, IllegalAccessException, NoSuchFieldException {
        StringBuilder snippet = new StringBuilder();

        for(Step step: steps)
            snippet.append(stepConfigurator(step));

        return snippet.toString();
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
                            logger.log(Level.WARNING, parameterClass.getName() + "is not supported at this time.");
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
                    logger.log(Level.WARNING, stepFieldClass.getName() + "is not supported at this time.");
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

        snippet = Snippetizer.object2Groovy(stepObject) + "\n";
        return snippet;
    }

    public String getStage(
            Stage stage,
            ArrayList<String> buildResultPaths,
            ArrayList<String> testResultPaths,
            ArrayList<String> archiveArtifacts,
            GitConfig gitConfig,
            String findbugs
    ) throws NoSuchMethodException, InstantiationException, IllegalAccessException, ConfiguratorException,
            InvocationTargetException, NoSuchFieldException
    {
        String snippet = "stage('" + stage.getName() + "') {\n";

        snippet += "steps {\n";
        snippet += getSteps(stage.getSteps());
        snippet += "}\n";

        // This condition is to generate Success, Failure and Always steps after steps finished executing.
        if(stage.getFailure() != null
                || stage.getSuccess() != null
                || stage.getAlways() != null
                || (stage.getName().equals("Build") &&
                        (archiveArtifacts != null || buildResultPaths != null || findbugs != null))
                || stage.getName().equals("Tests") && (testResultPaths != null)) {
            snippet += "post {\n";

            if (stage.getSuccess() != null
                    || (stage.getName().equals("Build"))
                    || stage.getName().equals("Tests") && (testResultPaths != null)// || gitConfig.getGitUrl() != null)
                    )
            {
                snippet += "success {\n";
                if (stage.getName().equals("Build")) {
                    snippet += "archiveArtifacts artifacts: '**/target/*.jar'\n";
                    if(archiveArtifacts != null)
                        snippet += getArchiveArtifactsSnippet(archiveArtifacts);

                    if(buildResultPaths != null)
                        snippet += getPublishReportSnippet(buildResultPaths);
                }
                if (stage.getName().equals("Tests")) {
                    if(testResultPaths != null)
                        snippet += getPublishReportSnippet(testResultPaths);
//                    TODO Abhishek: code is commented out for testing purposes, it will be reinstated later
//                    if(gitConfig.getGitUrl() != null)
//                        snippet += "" + addTabs("gitPush " +
//                                "credentialId: \"" + gitConfig.getCredentialsId() + "\"," +
//                                "url: \"" + gitConfig.getGitUrl() + "\"," +
//                                "branch: \"" + gitConfig.getGitBranch() + "\"" +
//                                "\n", 3);
                }
                if(stage.getSuccess() != null)
                    snippet += shellScript(stage.getSuccess());
                snippet += "}\n";
            }
            if (stage.getAlways() != null || (findbugs != null && stage.getName().equals("Tests"))) {
                snippet += "always {\n";
                if(findbugs != null && stage.getName().equals("Tests"))
                    snippet += "findbugs pattern: '" + findbugs + "'\n";

                if(stage.getAlways() != null)
                    snippet += shellScript(stage.getAlways());
                snippet += "}\n";
            }
            if (stage.getFailure() != null) {
                snippet += "failure {\n";
                snippet += shellScript(stage.getFailure());
                snippet += "}\n";
            }

            snippet += "}\n";
        }
        snippet += "}\n";

        return snippet;
    }

    public String getPublishArtifactStage(ArtifactPublishingConfig config,
                                          ArrayList<HashMap<String, String>> publishArtifacts){
        if(config == null)
            return "";

        StringBuilder snippet = new StringBuilder("stage('Publish Artifact') {\n");

        snippet.append("steps {\n");
        snippet.append("" + "withCredentials([file(credentialsId: '").append(config.getCredentialId()).append("', variable: 'FILE')]) {\n");

        for(HashMap<String, String> artifact: publishArtifacts){
            snippet.append("sh 'scp -i $FILE ").append(artifact.get("from")).append(" ").append(config.getUser()).append("@").append(config.getHost()).append(":").append(artifact.get("to")).append("'\n");
        }

        snippet.append("}\n");
        snippet.append("}\n");
        snippet.append("}\n");

        return snippet.toString();
    }

    public StringBuilder autoAddTabs(StringBuilder pipelineScript){
        int numOfTabs = 0;

        for(int i = 0; i < pipelineScript.length(); i++){
            if(pipelineScript.charAt(i) == '{'){
                numOfTabs++;
            }

            if(i + 1 != pipelineScript.length() &&
                    pipelineScript.charAt(i) == '\n' &&
                    pipelineScript.charAt(i + 1) == '}'){
                numOfTabs--;
            }

            if(pipelineScript.charAt(i) == '\n'){
                pipelineScript.insert(i + 1, StringUtils.repeat("\t", numOfTabs));
            }
        }

        return pipelineScript;
    }
}
