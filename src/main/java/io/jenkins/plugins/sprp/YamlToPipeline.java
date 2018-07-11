package io.jenkins.plugins.sprp;

import hudson.model.TaskListener;
import io.jenkins.plugins.sprp.models.Stage;
import io.jenkins.plugins.sprp.models.YamlPipeline;
import jenkins.model.Jenkins;
import org.eclipse.jgit.errors.NotSupportedException;
import org.jenkinsci.plugins.casc.ConfiguratorException;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.CustomClassLoaderConstructor;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

public class YamlToPipeline {
    public String generatePipeline(InputStream yamlScriptInputStream, GitConfig gitConfig, TaskListener listener)
            throws InvocationTargetException, InstantiationException, ConfiguratorException,
            IllegalAccessException, NotSupportedException, NoSuchMethodException {
        ArrayList<String> scriptLines = new ArrayList<>();

        YamlPipeline yamlPipeline = loadYaml(yamlScriptInputStream, listener);

        if (yamlPipeline == null) {
            return "";
        }

        // Passing a dummy launcher to detect if the machine is Unix or not
        PipelineSnippetGenerator psg = new PipelineSnippetGenerator(Jenkins.get().createLauncher(listener));

        scriptLines.add("pipeline {");

        // Adding outer agent and tools section
        scriptLines.addAll(psg.getAgent(yamlPipeline.getAgent()));

        // Adding environment
        scriptLines.addAll(psg.getEnvironment(yamlPipeline.getEnvironment()));

        // Stages begin
        scriptLines.add("stages {");

        if (yamlPipeline.getSteps() != null) {
            scriptLines.add("stage('Build') {");
            scriptLines.add("steps {");

            scriptLines.addAll(psg.getSteps(yamlPipeline.getSteps()));

            scriptLines.add("}");
            scriptLines.add("}");
        }

        if (yamlPipeline.getStages() != null) {
            for (Stage stage : yamlPipeline.getStages()) {
                scriptLines.addAll(psg.getStage(stage));
            }
        }

        // Archive artifacts stage
        scriptLines.addAll(psg.getArchiveArtifactsStage(yamlPipeline.getArchiveArtifacts()));

        scriptLines.addAll(psg.getPublishReportsAndArtifactStage(yamlPipeline.getReports(),
                yamlPipeline.getArtifactPublishingConfig(), yamlPipeline.getPublishArtifacts()));

        // This stage will always be generated at last, because if anyone of the above stage fails then we
        // will not push the code to target branch
        if (yamlPipeline.getConfiguration() != null && yamlPipeline.getConfiguration().isPushPrOnSuccess()) {
            scriptLines.addAll(psg.gitPushStage(gitConfig));
        }

        scriptLines.add("}");

        scriptLines.addAll(psg.getPostSection(yamlPipeline.getPost()));

        scriptLines.add("}");

        return psg.autoAddTabs(scriptLines);
    }

    public YamlPipeline loadYaml(InputStream yamlScriptInputStream, TaskListener listener) {
        CustomClassLoaderConstructor constructor = new CustomClassLoaderConstructor(this.getClass().getClassLoader());
        Yaml yaml = new Yaml(constructor);
        YamlPipeline yamlPipeline = yaml.loadAs(yamlScriptInputStream, YamlPipeline.class);

        if (yamlPipeline.getStages() != null && yamlPipeline.getSteps() != null) {
            throw new IllegalStateException("Only one of 'steps' or 'stages' must be present in the YAML file.");
        }

        return yamlPipeline;
    }
}
