package io.jenkins.plugins.sprp;

import com.cloudbees.plugins.credentials.CredentialsMatchers;
import com.cloudbees.plugins.credentials.CredentialsProvider;
import com.cloudbees.plugins.credentials.common.StandardCredentials;
import com.cloudbees.plugins.credentials.common.StandardUsernameCredentials;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import hudson.EnvVars;
import hudson.Extension;
import hudson.FilePath;
import hudson.model.Queue;
import hudson.model.TaskListener;
import hudson.model.queue.Tasks;
import org.jenkinsci.Symbol;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import org.jenkinsci.plugins.workflow.steps.*;
import org.kohsuke.stapler.DataBoundConstructor;

import java.util.Collections;
import java.util.Set;

public class GitPushStep extends Step {
    private String credentialId;
    private String url;
    private String branch;

    @DataBoundConstructor
    public GitPushStep(String credentialId, String url, String branch) {
        this.credentialId = credentialId;
        this.url = url;
        this.branch = branch;
    }

    public String getCredentialId() {
        return credentialId;
    }

    public String getBranch() {
        return branch;
    }

    public String getUrl() {
        return url;
    }

    public StepExecution start(StepContext context) throws Exception {
        return new GitPushStep.Execution(this.credentialId, this.url, this.branch, context);
    }

    public static class Execution extends SynchronousStepExecution<Void> {
        @SuppressFBWarnings(
                value = {"SE_TRANSIENT_FIELD_NOT_RESTORED"},
                justification = "Only used when starting."
        )
        private final transient String credentialId;
        private final String url;
        private final String branch;
        private static final long serialVersionUID = 1L;

        Execution(String credentialId, String url, String branch, StepContext context) {
            super(context);
            this.credentialId = credentialId;
            this.url = url;
            this.branch = branch;
        }

        protected Void run() throws Exception {
            FilePath ws = getContext().get(FilePath.class);
            TaskListener listener = this.getContext().get(TaskListener.class);
            EnvVars envVars = getContext().get(EnvVars.class);
            WorkflowJob job = getContext().get(WorkflowJob.class);
            GitOperations gitOperations = new GitOperations(ws, listener, envVars, url);
            StandardCredentials c = CredentialsMatchers.firstOrNull(
                    CredentialsProvider.lookupCredentials(
                            StandardCredentials.class,
                            job,
                            Tasks.getAuthenticationOf((Queue.Task)job)),
                    CredentialsMatchers.withId(credentialId));

            gitOperations.setUsernameAndPasswordCredential((StandardUsernameCredentials)c);
            gitOperations.setCurrentBranch(branch);
            gitOperations.push(true);
            return null;
        }
    }

    @Symbol("gitPush")
    @Extension
    public static class DescriptorImpl extends StepDescriptor {
        public DescriptorImpl() {
        }

        public String getFunctionName() {
            return "gitPush";
        }

        public String getDisplayName() {
            return "Git push step";
        }

        public Set<? extends Class<?>> getRequiredContext() {
            return Collections.singleton(TaskListener.class);
        }
    }
}
