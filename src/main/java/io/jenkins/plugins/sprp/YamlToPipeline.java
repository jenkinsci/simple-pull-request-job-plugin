package io.jenkins.plugins.sprp;

import java.nio.channels.Pipe;

public class YamlToPipeline {
    public String generatepipeline(){
        String script;
        final String newLine = "\n";
        int numberOfTabs = 0;

        PipelineSnippetGenerator psg = new PipelineSnippetGenerator();

        script = "pipeline {";
        numberOfTabs++;

        // Adding outer agent
        script += psg.getTabString(numberOfTabs)
                + "agent " + psg.addTabs(psg.getAgent(), numberOfTabs);

        // Stages begin
        script += psg.getTabString(numberOfTabs) + "stages {" + newLine;
        numberOfTabs++;

        script += psg.getTabString(numberOfTabs) + "stage(Example) {" + newLine;

        script += psg.addTabs(psg.shellScritp("./scripts/hello"), numberOfTabs);

        numberOfTabs--;
        script += psg.getTabString(numberOfTabs) + "}" + newLine;

        // Stages end
        script += psg.getTabString(numberOfTabs) + "}";
        script += "}";
        numberOfTabs--;

        return script;
    }
}
