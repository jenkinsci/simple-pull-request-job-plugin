package io.jenkins.plugins.sprp.generators;

import hudson.Extension;
import io.jenkins.plugins.sprp.GitConfig;
import io.jenkins.plugins.sprp.PipelineGenerator;
import org.jenkinsci.Symbol;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

@Extension
@Symbol("gitPushStage")
public class GitPushStageGenerator extends PipelineGenerator<GitConfig> {

    @Nonnull
    @Override
    public List<String> toPipeline(GitConfig gitConfig) {
        ArrayList<String> snippetLines = new ArrayList<>();

        if(gitConfig == null){
            return snippetLines;
        }

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

    @Override
    public boolean canConvert(@Nonnull Object object) {
        return object instanceof GitConfig;
    }
}
