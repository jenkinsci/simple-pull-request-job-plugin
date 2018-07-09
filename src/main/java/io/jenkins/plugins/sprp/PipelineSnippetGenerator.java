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
import org.apache.log4j.Logger;
import org.eclipse.jgit.errors.NotSupportedException;
import org.jenkinsci.plugins.casc.Configurator;
import org.jenkinsci.plugins.casc.ConfiguratorException;
import org.jenkinsci.plugins.casc.model.Mapping;
import org.jenkinsci.plugins.casc.model.Scalar;
import org.jenkinsci.plugins.casc.model.Sequence;
import org.jenkinsci.plugins.workflow.cps.Snippetizer;
import org.jenkinsci.plugins.workflow.steps.StepDescriptor;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PipelineSnippetGenerator {
    static private Logger logger = Logger.getLogger(PipelineSnippetGenerator.class.getClass().getName());
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

        if(agent != null) {
            agentLines.addAll(getTools(agent.getTools()));
        }

        return agentLines;
    }

    public List<String> getTools(HashMap<String, String> tools){
        ArrayList<String> snippetLines = new ArrayList<>();

        if(tools == null) {
            return snippetLines;
        }

        snippetLines.add("tools {");

        for(Map.Entry<String, String> entry: tools.entrySet()){
            snippetLines.add(entry.getKey() + " '" + entry.getValue() + "'");
        }

        snippetLines.add("}");

        return snippetLines;
    }

    public List<String> getEnvironment(Environment environment){
        ArrayList<String> snippetLines = new ArrayList<>();

        if(environment == null || (environment.getVariables() == null && environment.getCredentials() == null)) {
            return snippetLines;
        }

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

    public List<String> getPostSection(Post postSection) throws ConfiguratorException,
            InstantiationException, IllegalAccessException, InvocationTargetException, NotSupportedException, NoSuchMethodException {
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

    public List<String> getArchiveArtifactsStage(ArrayList<String> paths){
        ArrayList<String> snippetLines = new ArrayList<>();

        if(paths == null)
            return snippetLines;

        snippetLines.add("stage('Archive artifacts') {");
        snippetLines.add("steps {");

        for(String p: paths)
            snippetLines.add("archiveArtifacts artifacts: '" + p + "'");

        snippetLines.add("}");
        snippetLines.add("}");

        return snippetLines;
    }

    public List<String> getPublishReportSnippet(ArrayList<String> paths){
        ArrayList<String> snippetLines = new ArrayList<>();

        for(String p: paths)
            snippetLines.add("junit '" + p + "'");

        return snippetLines;
    }

    List<String> getSteps(ArrayList<LinkedHashMap<String, Step>> steps) throws InvocationTargetException,
            InstantiationException, ConfiguratorException, IllegalAccessException, NotSupportedException, NoSuchMethodException {
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
            InstantiationException, ConfiguratorException, NotSupportedException, NoSuchMethodException {
        if(step == null)
            return "\n";

        String snippet;
        Object stepObject = null;
        Descriptor<org.jenkinsci.plugins.workflow.steps.Step> stepDescriptor = StepDescriptor.byFunctionName(step.getStepName());

        if(stepDescriptor == null)
            throw new RuntimeException(new IllegalStateException("No step exist with the name of " + step.getStepName()));

        Class clazz = stepDescriptor.clazz;

        if(step.getStepName().equals("sh")){
            if(step.getDefaultParameter() != null) {
                step.setDefaultParameter(completeShellScriptPath((String) step.getDefaultParameter()));
            }
            else{
                step.getParameters().put("script", completeShellScriptPath(step.getParameters().get("script").toString()));
            }
        }

        if(step.getDefaultParameter() != null){
            Constructor constructor = Configurator.getDataBoundConstructor(clazz);

            if(constructor != null && constructor.getParameterCount() == 1){
                stepObject = constructor.newInstance(step.getDefaultParameter());
            }
            else{
                throw new NoSuchMethodException("No suitable constructor found for default parameter of step "
                        + step.getStepName());
            }
        }
        else{
            Mapping mapping = doMappingForMap(step.getParameters());

            Configurator configurator = Configurator.lookup(clazz);

            if (configurator != null) {
                stepObject = configurator.configure(mapping);
            }
            else{
                throw new IllegalStateException("No step with name '" + step.getStepName() +
                        "' exist. Have you installed required plugin.");
            }
        }

        snippet = Snippetizer.object2Groovy(stepObject);
        return snippet;
    }

    private Mapping doMappingForMap(Map<String, Object> map) throws NotSupportedException {
        Mapping mapping = new Mapping();

        for(Map.Entry<String, Object> entry: map.entrySet()){
            if(entry.getValue() instanceof Map){
                mapping.put(entry.getKey(), doMappingForMap((Map<String, Object>) entry.getValue()));
            }
            else if(entry.getValue() instanceof List){
                mapping.put(entry.getKey(), doMappingForSequence((List) entry.getValue()));
            }
            else {
                mapping.put(entry.getKey(), doMappingForScalar(entry.getValue()));
            }
        }

        return mapping;
    }

    private Scalar doMappingForScalar(Object object) throws NotSupportedException {
        Scalar scalar;

        if(object instanceof String)
            scalar = new Scalar((String) object);
        else if(object instanceof Number)
            scalar = new Scalar((Number) object);
        else if(object instanceof Enum)
            scalar = new Scalar((Enum) object);
        else if(object instanceof Boolean)
            scalar = new Scalar((Boolean) object);
        else {
            throw new NotSupportedException(object.getClass() + " is not supported.");
        }

        return scalar;
    }

    private Sequence doMappingForSequence(List<Object> objects) throws NotSupportedException {
        Sequence sequence = new Sequence();

        for(Object object: objects){
            if(object instanceof Map){
                sequence.add(doMappingForMap((Map<String, Object>) object));
            }
            else if(object instanceof Sequence){
                sequence.add(doMappingForSequence((List) object));
            }
            else{
                sequence.add(doMappingForScalar(object));
            }
        }

        return sequence;
    }

    public List<String> getStage(Stage stage) throws InstantiationException, IllegalAccessException, ConfiguratorException,
            InvocationTargetException, NotSupportedException, NoSuchMethodException {
        String stageName = stage.getName();

        ArrayList<String> snippetLines = new ArrayList<>();
        snippetLines.add("stage('" + stageName + "') {");

        if (!stage.getAgent().getAnyOrNone().equals("any")) {
            snippetLines.addAll(getAgent(stage.getAgent()));
        }

        snippetLines.add("steps {");
        snippetLines.addAll(getSteps(stage.getSteps()));
        snippetLines.add("}");

        snippetLines.addAll(getPostSection(stage.getPost()));

        snippetLines.add("}");

        return snippetLines;
    }

    public List<String> getPublishReportsAndArtifactStage(ArrayList<String> reports, ArtifactPublishingConfig config,
                                          ArrayList<HashMap<String, String>> publishArtifacts){
        ArrayList<String> snippetLines = new ArrayList<>();

        if(reports == null && config == null)
            return snippetLines;

        snippetLines.add("stage('Publish reports & artifacts') {");
        snippetLines.add("steps {");
        snippetLines.addAll(getPublishReportSnippet(reports));

        if(config != null) {
            snippetLines.add("" + "withCredentials([file(credentialsId: '" + config.getCredentialId() + "', variable: 'FILE')]) {");

            for (HashMap<String, String> artifact : publishArtifacts) {
                snippetLines.add("sh 'scp -i $FILE " + artifact.get("from") + " " + config.getUser() + "@" + config.getHost() + ":" + artifact.get("to") + "'");
            }

            snippetLines.add("}");
        }

        snippetLines.add("}");
        snippetLines.add("}");

        return snippetLines;
    }

    public List<String> gitPushStage(GitConfig gitConfig){
        ArrayList<String> snippetLines = new ArrayList<>();

        snippetLines.add("stage('Git Push') {");
        snippetLines.add("steps {");
        snippetLines.add("gitPush " +
                "credentialId: \"" + gitConfig.getCredentialsId() + "\"," +
                "url: \"" + gitConfig.getGitUrl() + "\"," +
                "branch: \"" + gitConfig.getGitBranch() + "\"");

        snippetLines.add("}");
        snippetLines.add("}");
        return snippetLines;
    }

    public String autoAddTabs(ArrayList<String> snippetLines){
        int numOfTabs = 0;
        StringBuilder snippet = new StringBuilder();

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
