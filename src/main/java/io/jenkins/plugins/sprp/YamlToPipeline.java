package io.jenkins.plugins.sprp;

import hudson.model.TaskListener;
import io.jenkins.plugins.sprp.models.*;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.CustomClassLoaderConstructor;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class YamlToPipeline {
    public String generatePipeline(@Nonnull InputStream yamlScriptInputStream,
                                     @CheckForNull GitConfig gitConfig,
                                     @Nonnull TaskListener listener)
            throws ConversionException {
        ArrayList<String> scriptLines = new ArrayList<>();

        YamlPipeline yamlPipeline = loadYaml(yamlScriptInputStream, listener);

        scriptLines.add("pipeline {");

        // Adding outer agent and tools section
        scriptLines.addAll(PipelineGenerator.convert("agent", yamlPipeline.getAgent()));

        // Adding environment
        scriptLines.addAll(PipelineGenerator.convert("environment", yamlPipeline.getEnvironment()));

        // Stages begin
        scriptLines.add("stages {");

        if (yamlPipeline.getSteps() != null) {
            scriptLines.add("stage('Build') {");
            scriptLines.add("steps {");

            for (LinkedHashMap<String, Step> step : yamlPipeline.getSteps()) {
                for (Map.Entry<String, Step> entry : step.entrySet()) {
                    scriptLines.addAll(PipelineGenerator.convert("step", entry.getValue()));
                }
            }

            scriptLines.add("}");
            scriptLines.add("}");
        }

        if (yamlPipeline.getStages() != null) {
            for (Stage stage : yamlPipeline.getStages()) {
                scriptLines.addAll(PipelineGenerator.convert("stage", stage));
            }
        }

        // Archive artifacts stage
        scriptLines.addAll(PipelineGenerator.convert("archiveArtifactStage", yamlPipeline.getArchiveArtifacts()));

        ReportsAndArtifactsInfo reportsAndArtifactsInfo = new ReportsAndArtifactsInfo();
        reportsAndArtifactsInfo.setArtifactPublishingConfig(yamlPipeline.getArtifactPublishingConfig());
        reportsAndArtifactsInfo.setReports(yamlPipeline.getReports());
        reportsAndArtifactsInfo.setPublishArtifacts(yamlPipeline.getPublishArtifacts());

        scriptLines.addAll(PipelineGenerator.convert("publishReportsAndArtifactsStage", reportsAndArtifactsInfo));

        // This stage will always be generated at last, because if anyone of the above stage fails then we
        // will not push the code to target branch
        if (yamlPipeline.getConfiguration() != null && yamlPipeline.getConfiguration().isPushPrOnSuccess()) {
            if (gitConfig == null) {
                throw new ConversionException("Git Configuration is not defined, but it is required for the Git Push");
            }
            scriptLines.addAll(PipelineGenerator.convert("gitPushStage", gitConfig));
        }

        scriptLines.add("}");

        scriptLines.addAll(PipelineGenerator.convert("post", yamlPipeline.getPost()));

        for (CustomPipelineSection section : yamlPipeline.getSections()) {
            scriptLines.addAll(PipelineGenerator.convert(section));
        }

        scriptLines.add("}");
        return PipelineGenerator.autoAddTabs(scriptLines);
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
