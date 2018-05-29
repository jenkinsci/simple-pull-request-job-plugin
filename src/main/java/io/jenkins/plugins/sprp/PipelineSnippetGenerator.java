package io.jenkins.plugins.sprp;

import io.jenkins.plugins.sprp.models.Agent;
import io.jenkins.plugins.sprp.models.ArtifactPublishingConfig;
import io.jenkins.plugins.sprp.models.Stage;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;

public class PipelineSnippetGenerator {
    PipelineSnippetGenerator(){
    }

    public String shellScritp(ArrayList<String> paths){
        String snippet = "";
        snippet = "script {\n" +
                "\tif (isUnix()) {\n";

        for(String p: paths)
            snippet += "\t\tsh '"+ p + ".sh" + "'\n";

        snippet += "\t} else {\n";

        for(String p: paths)
            snippet += "\t\tbat '"+ p + ".bat" + "'\n";

        snippet += "\t}\n" +
                "}\n";

        return snippet;

    }

    // This function will add tabs at the beginning of each line
    public String addTabs(String script, int numberOfTabs){
        String tabs = StringUtils.repeat("\t", numberOfTabs);

        script = script.replace("\n", "\n" + tabs);
        script = script.substring(0, script.length() - numberOfTabs);
        return script;
    }

    public String getTabString(int number){
        return StringUtils.repeat("\t", number);
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

    //TODO: Change to support full specs
    public String getAgent(Agent agent){
        String snippet = "";

        if(agent == null){
            snippet = "any\n";
        }
        else {
            if(agent.getDockerImage() != null){
                snippet += "{\n";
                snippet += "\tdocker {\n";
                snippet += "\t\timage '" + agent.getDockerImage() + "'\n";

                if (agent.getArgs() != null)
                    snippet += "\t\targs '" + agent.getArgs() + "'\n";

                snippet += "\t\talwaysPull " + agent.getAlwaysPull() + "\n";
                snippet += "\t\t" + addTabs(getCommonOptionsOfAgent(agent), 2);
                snippet += "\t}\n";
                snippet += "}\n";
            }
            else if(agent.getDockerfile() != null){
                snippet += "{\n";
                snippet += "\tdockerfile {\n";
                snippet += "\t\tfilename '" + agent.getDockerfile() + "'\n";

                if (agent.getDir() != null)
                    snippet += "\t\tdir '" + agent.getDir() + "'\n";

                if (agent.getArgs() != null)
                    snippet += "\t\tadditionalBuildArgs '" + agent.getArgs() + "'\n";

                snippet += "\t\t" + addTabs(getCommonOptionsOfAgent(agent), 2);
                snippet += "\t}\n";
                snippet += "}\n";
            }
            else {
                snippet += "{\n";
                snippet += "\tnode{\n";
                snippet += "\t\t" + addTabs(getCommonOptionsOfAgent(agent), 2);
                snippet += "\t}\n";
                snippet += "}\n";
            }
        }

        return snippet;
    }

    public String getArchiveArtifactsSnippet(ArrayList<String> paths){
        String snippet = "";

        for(String p: paths)
            snippet += "archiveArtifacts artifacts: '" + p + "'\n";

        return snippet;
    }

    public String getPublishReportSnippet(ArrayList<String> paths){
        String snippet = "";

        for(String p: paths)
            snippet += "junit '" + p + "'\n";

        return snippet;
    }

    public String getStage(
            Stage stage,
            ArrayList<String> buildResultPaths,
            ArrayList<String> testResultPaths,
            ArrayList<String> archiveArtifacts
    ){
        String snippet = "stage('" + stage.getName() + "') {\n";

        snippet += "\tsteps {\n";
        snippet += "\t\t" + addTabs(shellScritp(stage.getScripts()), 2);
        snippet += "\t}\n";

        if(stage.getFailure() != null
                || stage.getSuccess() != null
                || stage.getAlways() != null
                || (stage.getName().equals("Build") && (archiveArtifacts != null || buildResultPaths != null))
                || stage.getName().equals("Tests") && testResultPaths != null) {
            snippet += "\tpost {\n";

            if (stage.getSuccess() != null
                    || (stage.getName().equals("Build") && (archiveArtifacts != null || buildResultPaths != null))
                    || stage.getName().equals("Tests") && testResultPaths != null) {
                snippet += "\t\tsuccess {\n";
                if (stage.getName().equals("Build")) {
                    if(archiveArtifacts != null)
                        snippet += "\t\t\t" + addTabs(getArchiveArtifactsSnippet(archiveArtifacts), 3);

                    if(buildResultPaths != null)
                        snippet += "\t\t\t" + addTabs(getPublishReportSnippet(buildResultPaths), 3);
                }
                if (stage.getName().equals("Tests") && testResultPaths != null) {
                    snippet += "\t\t\t" + addTabs(getPublishReportSnippet(testResultPaths), 3);
                }
                if(stage.getSuccess() != null)
                    snippet += "\t\t\t" + addTabs(shellScritp(stage.getSuccess()), 3);
                snippet += "\t\t}\n";
            }
            if (stage.getAlways() != null) {
                snippet += "\t\talways {\n";
                if(stage.getAlways() != null)
                    snippet += "\t\t\t" + addTabs(shellScritp(stage.getAlways()), 3);
                snippet += "\t\t}\n";
            }
            if (stage.getFailure() != null) {
                snippet += "\t\tfailure {\n";
                snippet += "\t\t\t" + addTabs(shellScritp(stage.getFailure()), 3);
                snippet += "\t\t}\n";
            }

            snippet += "\t}\n";
        }
        snippet += "}\n";

        return snippet;
    }

    public String getPublishArtifactStage(ArtifactPublishingConfig config,
                                          ArrayList<HashMap<String, String>> publishArtifacts){
        if(config == null)
            return "";

        String snippet = "stage('Publish Artifact') {\n";

        snippet += "\tsteps {\n";
        snippet += "\t\t" + "withCredentials([file(credentialsId: '" + config.getCredentialId() + "', variable: 'FILE')]) {\n";

        for(HashMap<String, String> artifact: publishArtifacts){
            snippet += "\t\t\tsh 'scp -i $FILE " + artifact.get("from") + " " + config.getUser() + "@" + config.getHost() + ":" + artifact.get("to") + "'\n";
        }

        snippet += "\t\t}\n";
        snippet += "\t}\n";
        snippet += "}\n";

        return snippet;
    }
}
