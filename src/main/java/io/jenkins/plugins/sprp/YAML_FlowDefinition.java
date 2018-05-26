/*
 * The MIT License
 *
 * Copyright 2018 Abhishek Gautam (@gautamabhishek46).
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

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
            this.scriptPath = "Jenkinsfile.yaml";
        }
        return this;
    }

    public YAML_FlowDefinition(String scriptPath) {
        this.scriptPath = "Jenkinsfile.yaml";
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
