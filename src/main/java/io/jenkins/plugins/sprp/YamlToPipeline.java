package io.jenkins.plugins.sprp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import hudson.model.TaskListener;
import io.jenkins.plugins.sprp.models.Stage;
import io.jenkins.plugins.sprp.models.YamlPipeline;
import jenkins.model.Jenkins;
import org.jenkinsci.plugins.casc.ConfiguratorException;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.CustomClassLoaderConstructor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

public class YamlToPipeline {
    public String generatePipeline(InputStream yamlScriptInputStream, GitConfig gitConfig, TaskListener listener)
            throws InvocationTargetException, NoSuchMethodException, InstantiationException, ConfiguratorException,
            IllegalAccessException, NoSuchFieldException {
        ArrayList<String> scriptLines = new ArrayList<>();

        YamlPipeline yamlPipeline = loadYaml(yamlScriptInputStream, listener);

        if(yamlPipeline == null)
            return "";

        // Passing a dummy launcher to detect if the machine is Unix or not
        PipelineSnippetGenerator psg = new PipelineSnippetGenerator(Jenkins.get().createLauncher(listener));

        scriptLines.add("pipeline {");

        // Adding outer agent and tools section
        scriptLines.addAll(psg.getAgent(yamlPipeline.getAgent()));

        // Adding environment
        scriptLines.addAll(psg.getEnvironment(yamlPipeline.getEnvironment()));

        // Stages begin
        scriptLines.add("stages {");

        for(Stage stage: yamlPipeline.getStages()) {
            scriptLines.addAll(psg.getStage(
                    stage,
                    yamlPipeline.getReports(),
                    yamlPipeline.getArchiveArtifacts(),
                    gitConfig,
                    yamlPipeline.getFindBugs()));
        }

//        scriptLines.addAll(psg.getPublishReportsAndArtifactStage(yamlPipeline.getReports(),
//                yamlPipeline.getArtifactPublishingConfig(), yamlPipeline.getPublishArtifacts()));

        scriptLines.add("}");

        scriptLines.addAll(psg.getPostSection(yamlPipeline.getPost()));

        scriptLines.add("}");

        return psg.autoAddTabs(scriptLines);
    }

    public YamlPipeline loadYaml(InputStream yamlScriptInputStream, TaskListener listener){
        CustomClassLoaderConstructor constr = new CustomClassLoaderConstructor(this.getClass().getClassLoader());
        Yaml yaml = new Yaml(constr);
        try {
            YamlPipeline yamlPipeline = yaml.loadAs(yamlScriptInputStream, io.jenkins.plugins.sprp.models.YamlPipeline.class);

            // TODO: Just for testing purpose, needs to be removed before release
            ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
            System.out.println(ow.writeValueAsString(yamlPipeline));

            return yamlPipeline;
        }
        catch (IOException e){
            listener.getLogger().println("Error while loading YAML");
            listener.getLogger().println(e.getMessage());

            return null;
        }
    }
}
