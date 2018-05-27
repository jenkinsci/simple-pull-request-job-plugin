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

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import hudson.Extension;
import hudson.model.Action;
import hudson.model.Queue;
import hudson.model.TaskListener;
import java.io.File;
import java.util.List;
import javax.annotation.Nonnull;
import org.jenkinsci.plugins.workflow.cps.CpsFlowExecution;
import org.jenkinsci.plugins.workflow.flow.FlowDefinition;
import org.jenkinsci.plugins.workflow.flow.FlowDefinitionDescriptor;
import org.jenkinsci.plugins.workflow.flow.FlowExecution;
import org.jenkinsci.plugins.workflow.flow.FlowExecutionOwner;
import org.jenkinsci.plugins.workflow.job.WorkflowRun;

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

//        WorkflowRun build = (WorkflowRun) exec;

//        File file = new File("/mnt/CC0091D90091CB3A/workspace/OpenSource/jenkinsOrg/" +
//                "simple-pull-request-job-plugin/work/workspacece");
//
//
//        GitOperations gitOperations = new GitOperations(file, listener,
//                build.getCharacteristicEnvVars(), "https://github.com/gautamabhishek46/dummy");
//        gitOperations.deleteBranch("DUMMY_8DD2963");
//        gitOperations.printRevisions();
//        if(gitOperations.pullChangesOfPullrequest(4, "master"))
//            listener.getLogger().println("PR is successfully fetched and checkout");
//        else
//            listener.getLogger().println("PR is not successfully fetched and checkout");
//        if(gitOperations.push())
//            System.out.println("Push successful.");
//        else
//            System.out.println("Cannot push");


        String script = ""; // +
//                "pipeline {\n" +
//                "\tagent any\n" +
//                "\tstages {\n" +
//                "\t\tstage('Example') {\n" +
//                "\t\t\tsteps {\n" +
//                "\t\t\t\tcheckout scm \n" +
//                "\t\t\t\techo 'Hello World'\n" +
//                "\t\t\t\tscript {\n" +
//                "\t\t\t\t\tif (isUnix()) {\n" +
//                "\t\t\t\t\t\tsh 'echo \"This is UNIX\"'\n" +
//                "\t\t\t\t\t} else {\n" +
//                "\t\t\t\t\t\tsh 'echo \"This is not UNIX\"'\n" +
//                "\t\t\t\t\t}\n" +
//                "\t\t\t\t}\n" +
//                "\t\t\t}\n" +
//                "\t\t}\n" +
//                "\t}\n" +
//                "}";
//        script = new YamlToPipeline().generatepipeline();
        listener.getLogger().println(script);
        return new CpsFlowExecution(script, false, owner);
    }

    @Extension
    public static class DescriptorImpl extends FlowDefinitionDescriptor {

        @SuppressFBWarnings("NP_NONNULL_RETURN_VIOLATION")
        @Nonnull
        @Override public String getDisplayName() {
            return Messages.YAML_FlowDefinition_DescriptorImpl_DisplayName();
        }
    }
}
