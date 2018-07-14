package io.jenkins.plugins.sprp;

import hudson.Extension;
import hudson.model.UnprotectedRootAction;
import hudson.security.csrf.CrumbExclusion;
import jenkins.model.Jenkins;
import org.jenkinsci.plugins.workflow.multibranch.WorkflowMultiBranchProject;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author Honza Brázdil jbrazdil@redhat.com
 */
@Extension
public class YamlRootAction implements UnprotectedRootAction {

    static final String URL = "yamlhook";
    static final String BUILD_CONFIGURATION_MODE = "by Jenkinsfile.yaml";

    private static final Logger LOGGER = Logger.getLogger(YamlRootAction.class.getName());

    public String getIconFileName() {
        return null;
    }

    public String getDisplayName() {
        return null;
    }

    public String getUrlName() {
        return URL;
    }

    public void doIndex(StaplerRequest req,
                        StaplerResponse resp) {
        List<WorkflowMultiBranchProject> projects = Jenkins.get().getItems(WorkflowMultiBranchProject.class);

        // Can't find a way to get repository and repository url from the project variable
        // Hence triggering branch indexing of all projects, it will create jobs for new branches and pull requests and
        // delete jobs of closed pull requests.
        for(WorkflowMultiBranchProject project: projects){
            if(project.getProjectFactory().getDescriptor().getDisplayName().equals(YamlRootAction.BUILD_CONFIGURATION_MODE)) {
                project.getIndexing().run();
            }
        }
    }

    @Extension
    public static class GhprbRootActionCrumbExclusion extends CrumbExclusion {

        @Override
        public boolean process(HttpServletRequest req,
                               HttpServletResponse resp,
                               FilterChain chain) throws IOException, ServletException {
            String pathInfo = req.getPathInfo();
            if (pathInfo != null && pathInfo.equals(getExclusionPath())) {
                chain.doFilter(req, resp);
                return true;
            }
            return false;
        }

        public String getExclusionPath() {
            return "/" + URL + "/";
        }
    }
}
