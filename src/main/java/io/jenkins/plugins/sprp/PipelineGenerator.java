package io.jenkins.plugins.sprp;

import hudson.ExtensionList;
import hudson.ExtensionPoint;
import hudson.model.Descriptor;
import io.jenkins.plugins.sprp.impl.AgentGenerator;
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
import org.jenkinsci.plugins.structs.SymbolLookup;
import org.jenkinsci.plugins.workflow.cps.Snippetizer;
import org.jenkinsci.plugins.workflow.steps.StepDescriptor;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public abstract class PipelineGenerator<T> implements ExtensionPoint {
    static private Logger logger = Logger.getLogger(PipelineGenerator.class.getClass().getName());

    @Nonnull
    public abstract List<String> toPipeline(@CheckForNull T object) throws ConversionException;

    public abstract boolean canConvert(@Nonnull Object object);

    public static ExtensionList<PipelineGenerator> all() {
        return ExtensionList.lookup(PipelineGenerator.class);
    }

    @CheckForNull
    public static PipelineGenerator lookupForName(@Nonnull String name) {
        return SymbolLookup.get().find(PipelineGenerator.class, name);
    }

    @CheckForNull
    public static <T extends PipelineGenerator> T lookupConverter(Class<T> clazz) {
        for (PipelineGenerator gen : all()) {
            if (clazz.equals(gen.getClass())) {
                return clazz.cast(gen);
            }
        }
        return null;
    }

    @Nonnull
    public static <T extends PipelineGenerator> T lookupConverterOrFail(Class<T> clazz)
            throws ConversionException {
        T converter = lookupConverter(clazz);
        if (converter == null) {
            throw new ConversionException("Failed to find converter: " + clazz);
        }
        return converter;
    }

    @CheckForNull
    public static PipelineGenerator lookup(@Nonnull Object object) {
        for (PipelineGenerator gen : all()) {
            if (gen.canConvert(object)) {
                return gen;
            }
        }
        return null;
    }

    @Nonnull
    public static List<String> convert(@Nonnull Object object) throws ConversionException {
        PipelineGenerator gen = lookup(object);
        if (gen == null) {
            // TODO: add better diagnostics (field matching)
            throw new ConversionException("Cannot find converter for the object: " + object.getClass());
        }
        //TODO: handle raw type conversion risks
        return gen.toPipeline(object);
    }

    @Nonnull
    public static List<String> convert(@Nonnull String converterName, @CheckForNull Object object) throws ConversionException {
        PipelineGenerator gen = lookupForName(converterName);
        if (gen == null) {
            // TODO: add better diagnostics (field matching)
            throw new ConversionException("Cannot find converter for the type: " + converterName);
        }
        //TODO: handle raw type conversion risks
        return gen.toPipeline(object);
    }

    // TODO: Move all things below outside to Generator extensions

    public List<String> getTools(HashMap<String, String> tools) {
        ArrayList<String> snippetLines = new ArrayList<>();

        if (tools == null) {
            return snippetLines;
        }

        snippetLines.add("tools {");

        for (Map.Entry<String, String> entry : tools.entrySet()) {
            snippetLines.add(entry.getKey() + " '" + entry.getValue() + "'");
        }

        snippetLines.add("}");

        return snippetLines;
    }

    public List<String> getEnvironment(Environment environment) {
        ArrayList<String> snippetLines = new ArrayList<>();

        if (environment == null || (environment.getVariables() == null && environment.getCredentials() == null)) {
            return snippetLines;
        }

        snippetLines.add("environment {");

        for (Map.Entry<String, String> entry : environment.getVariables().entrySet()) {
            snippetLines.add(entry.getKey() + " = '" + entry.getValue() + "'");
        }

        for (Credential credential : environment.getCredentials()) {
            snippetLines.add(credential.getVariable() + " = credentials('" + credential.getCredentialId() + "')");
        }

        snippetLines.add("}");

        return snippetLines;
    }

    public List<String> getPostSection(Post postSection) throws ConfiguratorException,
            InstantiationException, IllegalAccessException, InvocationTargetException, NotSupportedException, NoSuchMethodException {
        ArrayList<String> snippetLines = new ArrayList<>();

        if (postSection == null) {
            return snippetLines;
        }

        snippetLines.add("post {");

        snippetLines.addAll(getPostConditionSnippetIfNonNull("always", postSection.getAlways()));
        snippetLines.addAll(getPostConditionSnippetIfNonNull("changed", postSection.getChanged()));
        snippetLines.addAll(getPostConditionSnippetIfNonNull("fixed", postSection.getFixed()));
        snippetLines.addAll(getPostConditionSnippetIfNonNull("regression", postSection.getRegression()));
        snippetLines.addAll(getPostConditionSnippetIfNonNull("aborted", postSection.getAborted()));
        snippetLines.addAll(getPostConditionSnippetIfNonNull("failure", postSection.getFailure()));
        snippetLines.addAll(getPostConditionSnippetIfNonNull("success", postSection.getSuccess()));
        snippetLines.addAll(getPostConditionSnippetIfNonNull("unstable", postSection.getUnstable()));
        snippetLines.addAll(getPostConditionSnippetIfNonNull("cleanup", postSection.getCleanup()));

        snippetLines.add("}");

        return snippetLines;
    }

    private List<String> getPostConditionSnippetIfNonNull(String postCondition, ArrayList<LinkedHashMap<String, Step>> steps)
            throws IllegalAccessException, ConfiguratorException, InstantiationException, NotSupportedException,
            NoSuchMethodException, InvocationTargetException {
        ArrayList<String> snippetLines = new ArrayList<>();
        if (steps != null) {
            snippetLines.add(postCondition + " {");
            snippetLines.addAll(getSteps(steps));
            snippetLines.add("}");
        }

        return snippetLines;
    }

    public List<String> getArchiveArtifactsStage(ArrayList<String> paths) {
        ArrayList<String> snippetLines = new ArrayList<>();

        if (paths == null) {
            return snippetLines;
        }

        snippetLines.add("stage('Archive artifacts') {");
        snippetLines.add("steps {");

        for (String p : paths) {
            snippetLines.add("archiveArtifacts artifacts: '" + p + "'");
        }

        snippetLines.add("}");
        snippetLines.add("}");

        return snippetLines;
    }

    public List<String> getPublishReportSnippet(ArrayList<String> paths) {
        ArrayList<String> snippetLines = new ArrayList<>();

        for (String p : paths) {
            snippetLines.add("junit '" + p + "'");
        }

        return snippetLines;
    }

    List<String> getSteps(ArrayList<LinkedHashMap<String, Step>> steps) throws InvocationTargetException,
            InstantiationException, ConfiguratorException, IllegalAccessException, NotSupportedException, NoSuchMethodException {
        ArrayList<String> snippetLines = new ArrayList<>();

        for (LinkedHashMap<String, Step> step : steps) {
            for (Map.Entry<String, Step> entry : step.entrySet()) {
                snippetLines.add(stepConfigurator(entry.getValue()));
            }
        }

        return snippetLines;
    }

    private String stepConfigurator(Step step)
            throws IllegalAccessException, InvocationTargetException,
            InstantiationException, ConfiguratorException, NotSupportedException, NoSuchMethodException {
        if (step == null)
            return "\n";

        String snippet;
        Object stepObject = null;
        Descriptor<org.jenkinsci.plugins.workflow.steps.Step> stepDescriptor = StepDescriptor.byFunctionName(step.getStepName());

        if (stepDescriptor == null) {
            throw new RuntimeException(new IllegalStateException("No step exist with the name of " + step.getStepName()));
        }

        Class clazz = stepDescriptor.clazz;

        if (step.getDefaultParameter() != null) {
            Constructor constructor = Configurator.getDataBoundConstructor(clazz);

            if (constructor != null && constructor.getParameterCount() == 1) {
                stepObject = constructor.newInstance(step.getDefaultParameter());
            } else {
                throw new NoSuchMethodException("No suitable constructor found for default parameter of step "
                        + step.getStepName());
            }
        } else {
            Mapping mapping = doMappingForMap(step.getParameters());

            Configurator configurator = Configurator.lookup(clazz);

            if (configurator != null) {
                stepObject = configurator.configure(mapping);
            } else {
                throw new IllegalStateException("No step with name '" + step.getStepName() +
                        "' exist. Have you installed required plugin.");
            }
        }

        snippet = Snippetizer.object2Groovy(stepObject);
        return snippet;
    }

    private Mapping doMappingForMap(Map<String, Object> map) throws NotSupportedException {
        Mapping mapping = new Mapping();

        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (entry.getValue() instanceof Map) {
                mapping.put(entry.getKey(), doMappingForMap((Map<String, Object>) entry.getValue()));
            } else if (entry.getValue() instanceof List) {
                mapping.put(entry.getKey(), doMappingForSequence((List) entry.getValue()));
            } else {
                mapping.put(entry.getKey(), doMappingForScalar(entry.getValue()));
            }
        }

        return mapping;
    }

    private Scalar doMappingForScalar(Object object) throws NotSupportedException {
        Scalar scalar;

        if (object instanceof String) {
            scalar = new Scalar((String) object);
        } else if (object instanceof Number) {
            scalar = new Scalar((Number) object);
        } else if (object instanceof Enum) {
            scalar = new Scalar((Enum) object);
        } else if (object instanceof Boolean) {
            scalar = new Scalar((Boolean) object);
        } else {
            throw new NotSupportedException(object.getClass() + " is not supported.");
        }

        return scalar;
    }

    private Sequence doMappingForSequence(List<Object> objects) throws NotSupportedException {
        Sequence sequence = new Sequence();

        for (Object object : objects) {
            if (object instanceof Map) {
                sequence.add(doMappingForMap((Map<String, Object>) object));
            } else if (object instanceof Sequence) {
                sequence.add(doMappingForSequence((List) object));
            } else {
                sequence.add(doMappingForScalar(object));
            }
        }

        return sequence;
    }

    public List<String> getStage(Stage stage) throws ConversionException, InstantiationException, IllegalAccessException, ConfiguratorException,
            InvocationTargetException, NotSupportedException, NoSuchMethodException {
        String stageName = stage.getName();

        ArrayList<String> snippetLines = new ArrayList<>();
        snippetLines.add("stage('" + stageName + "') {");

        final Agent agent = stage.getAgent();
        if (agent != null && !agent.getAnyOrNone().equals("any")) {
            AgentGenerator gen = lookupConverterOrFail(AgentGenerator.class);
            snippetLines.addAll(gen.toPipeline(agent));
        }

        snippetLines.add("steps {");
        snippetLines.addAll(getSteps(stage.getSteps()));
        snippetLines.add("}");

        snippetLines.addAll(getPostSection(stage.getPost()));

        snippetLines.add("}");

        return snippetLines;
    }

    public List<String> getPublishReportsAndArtifactStage(ArrayList<String> reports, ArtifactPublishingConfig config,
                                                          ArrayList<HashMap<String, String>> publishArtifacts) {
        ArrayList<String> snippetLines = new ArrayList<>();

        if (reports == null && config == null) {
            return snippetLines;
        }

        snippetLines.add("stage('Publish reports & artifacts') {");
        snippetLines.add("steps {");

        if (reports != null) {
            snippetLines.addAll(getPublishReportSnippet(reports));
        }

        if (config != null) {
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

    public List<String> gitPushStage(@Nonnull GitConfig gitConfig) {
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

    public static String autoAddTabs(ArrayList<String> snippetLines) {
        int numOfTabs = 0;
        StringBuilder snippet = new StringBuilder();

        for (String str : snippetLines) {
            if (str.startsWith("}")) {
                numOfTabs--;
            }

            if (numOfTabs != 0) {
                snippet.append(StringUtils.repeat("\t", numOfTabs));
            }

            snippet.append(str).append("\n");

            if (str.endsWith("{")) {
                numOfTabs++;
            }
        }

        return snippet.toString();
    }
}
