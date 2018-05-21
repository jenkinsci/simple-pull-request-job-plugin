package io.jenkins.plugins.sprp;

import hudson.Extension;
import hudson.Functions;
import hudson.model.TaskListener;
import hudson.model.Action;
import hudson.scm.NullSCM;
import hudson.scm.SCM;
import jenkins.branch.Branch;
//import jenkins.scm.api.;
import org.eclipse.jgit.transport.RefSpec;
import org.eclipse.jgit.transport.URIish;
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
import org.jenkinsci.plugins.workflow.multibranch.BranchJobProperty;
import org.jenkinsci.plugins.workflow.multibranch.WorkflowBranchProjectFactory;
import org.jenkinsci.plugins.workflow.multibranch.WorkflowMultiBranchProject;

import javax.annotation.Nonnull;
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
        String script = "";

        listener.getLogger().println(script);

        GitClient git = Git.with(listener, ((WorkflowRun) owner.getExecutable()).getParent().getCharacteristicEnvVars())
                .in(owner.getRootDir().getAbsoluteFile())
                .getClient();
        git.addRemoteUrl("origin", "https://github.com/gautamabhishek46/dummy.git");
        FetchCommand gitFetch= git.fetch_();
        ArrayList<RefSpec> refSpecs = new ArrayList<>();
//        refSpecs.add(new RefSpec().setSource("https://github.com/gautamabhishek46/dummy.git"));
        refSpecs.add(new RefSpec().setSourceDestination("master", "master"));
        gitFetch.from(new URIish("https://github.com/gautamabhishek46/dummy.git"), null).execute();
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
