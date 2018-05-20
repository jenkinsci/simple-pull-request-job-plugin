/*
 * The MIT License
 *
 * Copyright 2015 CloudBees, Inc.
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

import hudson.Extension;
import jenkins.branch.MultiBranchProjectFactory;
import jenkins.branch.MultiBranchProjectFactoryDescriptor;
import jenkins.scm.api.SCMSource;
import jenkins.scm.api.SCMSourceCriteria;
import org.apache.commons.lang.StringUtils;
//import org.jenkinsci.plugins.workflow.multibranch.AbstractWorkflowBranchProjectFactory;
//import org.jenkinsci.plugins.workflow.multibranch.AbstractWorkflowMultiBranchProjectFactory;
//import org.jenkinsci.plugins.workflow.multibranch.YAML_BranchProjectFactory;
//import org.jenkinsci.plugins.workflow.multibranch.WorkflowMultiBranchProject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

import java.io.IOException;

/**
 * Defines organization folders by {@link YAML_BranchProjectFactory}.
 */
public class WorkflowMultiBranchProjectFactory
        extends org.jenkinsci.plugins.workflow.multibranch.WorkflowMultiBranchProjectFactory {
    private String scriptPath = YAML_BranchProjectFactory.SCRIPT;

    public Object readResolve() {
        if (this.scriptPath == null) {
            this.scriptPath = YAML_BranchProjectFactory.SCRIPT;
        }
        return this;
    }

    @DataBoundSetter
    public void setScriptPath(String scriptPath) {
        if (StringUtils.isEmpty(scriptPath)) {
            this.scriptPath = YAML_BranchProjectFactory.SCRIPT;
        } else {
            this.scriptPath = scriptPath;
        }
    }

    public String getScriptPath() { return scriptPath; }

    @DataBoundConstructor
    public WorkflowMultiBranchProjectFactory() { }

    @Override protected SCMSourceCriteria getSCMSourceCriteria(SCMSource source) {
        return newProjectFactorySCMSourceCriteria(source);
    }

    private org.jenkinsci.plugins.workflow.multibranch.AbstractWorkflowBranchProjectFactory newProjectFactory() {
        YAML_BranchProjectFactory workflowBranchProjectFactory = new YAML_BranchProjectFactory();
        workflowBranchProjectFactory.setScriptPath(scriptPath);
        return workflowBranchProjectFactory;
    }

    private SCMSourceCriteria newProjectFactorySCMSourceCriteria(SCMSource source) {
        YAML_BranchProjectFactory workflowBranchProjectFactory = new YAML_BranchProjectFactory();
        workflowBranchProjectFactory.setScriptPath(scriptPath);
        return workflowBranchProjectFactory.getSCMSourceCriteria(source);
    }

    @Extension
    public static class DescriptorImpl extends MultiBranchProjectFactoryDescriptor {

        @Override public MultiBranchProjectFactory newInstance() {
            return new org.jenkinsci.plugins.workflow.multibranch.WorkflowMultiBranchProjectFactory();
        }

        @Override public String getDisplayName() {
            return "Pipeline " + YAML_BranchProjectFactory.SCRIPT;
        }

    }

    @Override
    protected void customize(org.jenkinsci.plugins.workflow.multibranch.WorkflowMultiBranchProject project)
            throws IOException, InterruptedException {
        project.setProjectFactory(newProjectFactory());
    }
}
