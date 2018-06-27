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

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;

public class YamlToPipeline {
    public String generatePipeline(InputStream yamlScriptInputStream, GitConfig gitConfig, TaskListener listener)
            throws InvocationTargetException, NoSuchMethodException, InstantiationException, ConfiguratorException,
            IllegalAccessException, NoSuchFieldException {
        StringBuilder script;
        final String newLine = "\n";
        int numberOfTabs = 0;

        YamlPipeline yamlPipeline = loadYaml(yamlScriptInputStream, listener);

        if(yamlPipeline == null)
            return "";

        // Passing a dummy launcher to detect if the machine is Unix or not
        PipelineSnippetGenerator psg = new PipelineSnippetGenerator(Jenkins.get().createLauncher(listener));

        script = new StringBuilder("pipeline {\n");
        numberOfTabs++;

        // Adding outer agent
        script.append(psg.getTabString(numberOfTabs)).append("agent ").append(psg.addTabs(psg.getAgent(yamlPipeline.getAgent()), numberOfTabs));

        // Stages begin
        script.append("\tstages {" + newLine);
        numberOfTabs++;

        for(Stage stage: yamlPipeline.getStages()){
            script.append(psg.getTabString(numberOfTabs)).append(psg.addTabs(psg.getStage(
                    stage,
                    yamlPipeline.getBuildResultPaths(),
                    yamlPipeline.getTestResultPaths(),
                    yamlPipeline.getArchiveArtifacts(),
                    gitConfig,
                    yamlPipeline.getFindBugs()), numberOfTabs));
        }

//        script.append(psg.getTabString(numberOfTabs)).append(psg.addTabs(psg.getPublishArtifactStage(yamlPipeline.getArtifactPublishingConfig(),
//                yamlPipeline.getPublishArtifacts()), numberOfTabs));

        numberOfTabs--;
        script.append(psg.getTabString(numberOfTabs)).append("}\n");
        numberOfTabs--;
        script.append(psg.getTabString(numberOfTabs)).append("}\n");

        return script.toString();
    }

    public YamlPipeline loadYaml(InputStream yamlScriptInputStream, TaskListener listener){
        CustomClassLoaderConstructor constr = new CustomClassLoaderConstructor(this.getClass().getClassLoader());
        Yaml yaml = new Yaml(constr);
        try {
            YamlPipeline yamlPipeline = yaml.loadAs(yamlScriptInputStream, io.jenkins.plugins.sprp.models.YamlPipeline.class);

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
