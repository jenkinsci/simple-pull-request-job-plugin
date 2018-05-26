package io.jenkins.plugins.sprp;

import com.cloudbees.plugins.credentials.Credentials;
import com.cloudbees.plugins.credentials.CredentialsProvider;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import hudson.*;
import hudson.model.*;
import org.acegisecurity.Authentication;
import org.eclipse.jgit.transport.RefSpec;
import org.jenkinsci.plugins.gitclient.CloneCommand;
import org.jenkinsci.plugins.gitclient.FetchCommand;
import org.jenkinsci.plugins.gitclient.Git;
import org.jenkinsci.plugins.gitclient.GitClient;
import org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition;
import org.jenkinsci.plugins.workflow.cps.CpsFlowExecution;
import org.jenkinsci.plugins.workflow.cps.CpsScmFlowDefinition;
import org.jenkinsci.plugins.workflow.flow.FlowDefinition;
import org.jenkinsci.plugins.workflow.flow.FlowDefinitionDescriptor;
import org.jenkinsci.plugins.workflow.flow.FlowExecution;
import org.jenkinsci.plugins.workflow.flow.FlowExecutionOwner;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import org.jenkinsci.plugins.workflow.job.WorkflowRun;
//import hudson.plugins.xshell.XShellBuilder;
import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class YAML_FlowDefinition extends FlowDefinition {
    private String scriptPath;

    public Object readResolve() {
        if (this.scriptPath == null) {
            this.scriptPath = "Jenkins.yaml";
        }
        return this;
    }

    public YAML_FlowDefinition(String scriptPath) {
        this.scriptPath = "Jenkins.yaml";
    }

    @Override
    public FlowExecution create(FlowExecutionOwner owner, TaskListener listener,
                                          List<? extends Action> actions) throws Exception {
        Queue.Executable exec = owner.getExecutable();
        if (!(exec instanceof WorkflowRun)) {
            throw new IllegalStateException("inappropriat   e context");
        }
        WorkflowRun build = (WorkflowRun) exec;

        File file = new File("/mnt/CC0091D90091CB3A/workspace/OpenSource/jenkinsOrg/simple-pull-request-job-plugin/work/workspace");


        GitOperations gitOperations = new GitOperations(file, listener,
                build.getCharacteristicEnvVars(), "https://github.com/jenkinsci/simple-pull-request-job-plugin");
//        gitOperations.deleteBranch("DUMMY_8DD2963");
//        gitOperations.printRevisions();
        if(gitOperations.pullChangesOfPullrequest(4, "master"))
            listener.getLogger().println("PR is successfully fetched and checkout");
        else
            listener.getLogger().println("PR is not successfully fetched and checkout");

        String script = "pipeline {\n" +
                "    agent any\n" +
                "    stages {\n" +
                "        stage('Example') {\n" +
                "            steps {\n" +
                "                echo 'Hello World'\n" +
                "\n" +
                "            }\n" +
                "        }\n" +
                "    }\n" +
                "}";
        return new CpsFlowExecution(script, false, owner);
    }

    @Extension
    public static class DescriptorImpl extends FlowDefinitionDescriptor {

        @Nonnull
        @Override public String getDisplayName() {
            return Messages.YAML_FlowDefinition_DescriptorImpl_DisplayName();
        }
    }
}
