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

    public String shellScript(ArrayList<String> paths){
        StringBuilder snippet;
        snippet = new StringBuilder("script {\n" + "\tif (isUnix()) {\n");

        for(String p: paths)
            snippet.append("\t\tsh '").append(p).append(".sh").append("'\n");

        snippet.append("\t} else {\n");

        for(String p: paths)
            snippet.append("\t\tbat '").append(p).append(".bat").append("'\n");

        snippet.append("\t}\n" + "}\n");

        return snippet.toString();

    }

    // This function will add tabs at the beginning of each line
    public String addTabs(String script, int numberOfTabs){
        String tabs = StringUtils.repeat("\t", numberOfTabs);

        script = script.replace("\n", "\n" + tabs);
        if(script.length() > numberOfTabs)
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
        StringBuilder snippet = new StringBuilder();

        for(String p: paths)
            snippet.append("archiveArtifacts artifacts: '").append(p).append("'\n");

        return snippet.toString();
    }

    public String getPublishReportSnippet(ArrayList<String> paths){
        StringBuilder snippet = new StringBuilder();

        for(String p: paths)
            snippet.append("junit testResults: '").append(p).append(", allowEmptyResults: true").append("'\n");

        return snippet.toString();
    }

    public String getStage(
            Stage stage,
            ArrayList<String> buildResultPaths,
            ArrayList<String> testResultPaths,
            ArrayList<String> archiveArtifacts,
            GitConfig gitConfig,
            String findbugs
    ){
        String snippet = "stage('" + stage.getName() + "') {\n";

        snippet += "\tsteps {\n";
        snippet += "\t\t" + addTabs(shellScript(stage.getScripts()), 2);
        snippet += "\t}\n";

        if(stage.getFailure() != null
                || stage.getSuccess() != null
                || stage.getAlways() != null
                || (stage.getName().equals("Build") &&
                        (archiveArtifacts != null || buildResultPaths != null || findbugs != null))
                || stage.getName().equals("Tests") && (testResultPaths != null || gitConfig.getGitUrl() != null)) {
            snippet += "\tpost {\n";

            if (stage.getSuccess() != null
                    || (stage.getName().equals("Build"))
                    || stage.getName().equals("Tests") && (testResultPaths != null)// || gitConfig.getGitUrl() != null)
                    )
            {
                snippet += "\t\tsuccess {\n";
                if (stage.getName().equals("Build")) {
                    snippet += "\t\t\t" + addTabs("archiveArtifacts artifacts: '**/target/*.jar'\n", 3);
                    if(archiveArtifacts != null)
                        snippet += "\t\t\t" + addTabs(getArchiveArtifactsSnippet(archiveArtifacts), 3);

                    if(buildResultPaths != null)
                        snippet += "\t\t\t" + addTabs(getPublishReportSnippet(buildResultPaths), 3);
                }
                if (stage.getName().equals("Tests")) {
                    if(testResultPaths != null)
                        snippet += "\t\t\t" + addTabs(getPublishReportSnippet(testResultPaths), 3);
//                    if(gitConfig.getGitUrl() != null)
//                        snippet += "\t\t\t" + addTabs("gitPush " +
//                                "credentialId: \"" + gitConfig.getCredentialsId() + "\"," +
//                                "url: \"" + gitConfig.getGitUrl() + "\"," +
//                                "branch: \"" + gitConfig.getGitBranch() + "\"" +
//                                "\n", 3);
                }
                if(stage.getSuccess() != null)
                    snippet += "\t\t\t" + addTabs(shellScript(stage.getSuccess()), 3);
                snippet += "\t\t}\n";
            }
            if (stage.getAlways() != null || (findbugs != null && stage.getName().equals("Tests"))) {
                snippet += "\t\talways {\n";
                if(findbugs != null && stage.getName().equals("Tests"))
                    snippet += "\t\t\t" + addTabs("findbugs pattern: '" + findbugs + "'\n", 3);

                if(stage.getAlways() != null)
                    snippet += "\t\t\t" + addTabs(shellScript(stage.getAlways()), 3);
                snippet += "\t\t}\n";
            }
            if (stage.getFailure() != null) {
                snippet += "\t\tfailure {\n";
                snippet += "\t\t\t" + addTabs(shellScript(stage.getFailure()), 3);
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

        StringBuilder snippet = new StringBuilder("stage('Publish Artifact') {\n");

        snippet.append("\tsteps {\n");
        snippet.append("\t\t" + "withCredentials([file(credentialsId: '").append(config.getCredentialId()).append("', variable: 'FILE')]) {\n");

        for(HashMap<String, String> artifact: publishArtifacts){
            snippet.append("\t\t\tsh 'scp -i $FILE ").append(artifact.get("from")).append(" ").append(config.getUser()).append("@").append(config.getHost()).append(":").append(artifact.get("to")).append("'\n");
        }

        snippet.append("\t\t}\n");
        snippet.append("\t}\n");
        snippet.append("}\n");

        return snippet.toString();
    }
}
