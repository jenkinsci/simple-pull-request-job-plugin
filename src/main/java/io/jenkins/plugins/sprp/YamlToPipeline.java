package io.jenkins.plugins.sprp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import hudson.model.TaskListener;
import io.jenkins.plugins.sprp.models.Stage;
import io.jenkins.plugins.sprp.models.YamlPipeline;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class YamlToPipeline {
    public String generatePipeline(String yamlScriptPath, TaskListener listener){
        StringBuilder script;
        final String newLine = "\n";
        int numberOfTabs = 0;

        YamlPipeline yamlPipeline = loadYaml(yamlScriptPath, listener);

        if(yamlPipeline == null)
            return "";

        PipelineSnippetGenerator psg = new PipelineSnippetGenerator();

        script = new StringBuilder("pipeline {\n");
        numberOfTabs++;

        // Adding outer agent
        script.append(psg.getTabString(numberOfTabs)).append("agent ").append(psg.addTabs(psg.getAgent(yamlPipeline.getAgent()), numberOfTabs));

        // Stages begin
        script.append("\tstages {" + newLine);
        numberOfTabs++;

        for(Stage stage: yamlPipeline.getStages()){
            script.append(psg.getTabString(numberOfTabs)).append(psg.addTabs(psg.getStage(stage,
                    yamlPipeline.getBuildResultPaths(),
                    yamlPipeline.getTestResultPaths(),
                    yamlPipeline.getArchiveArtifacts()), numberOfTabs));
        }

        script.append(psg.getTabString(numberOfTabs)).append(psg.addTabs(psg.getPublishArtifactStage(yamlPipeline.getArtifactPublishingConfig(),
                yamlPipeline.getPublishArtifacts()), numberOfTabs));

//        Below code is for stage generation
//        script += psg.getTabString(numberOfTabs) + "stage(Example) {" + newLine;
//        numberOfTabs++;
//
//        script += psg.addTabs(psg.shellScritp("./scripts/hello"), numberOfTabs);
//
//        numberOfTabs--;
//        script += psg.getTabString(numberOfTabs) + "}" + newLine;

        // Stages end
        numberOfTabs--;
        script.append("\t}\n");
        numberOfTabs--;
        script.append("}");

        return script.toString();
    }

    public YamlPipeline loadYaml(String yamlScriptPath, TaskListener listener){
        Yaml yaml = new Yaml();
        try (InputStream in = new FileInputStream(yamlScriptPath)) {
            YamlPipeline yamlPipeline = yaml.loadAs(in, YamlPipeline.class);

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
