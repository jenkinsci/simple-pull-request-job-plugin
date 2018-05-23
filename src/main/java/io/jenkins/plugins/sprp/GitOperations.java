package io.jenkins.plugins.sprp;

import hudson.EnvVars;
import hudson.model.TaskListener;
import hudson.triggers.SCMTrigger;
import org.codehaus.groovy.runtime.callsite.DummyCallSite;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.transport.RefSpec;
import org.eclipse.jgit.transport.URIish;
import org.jenkinsci.plugins.gitclient.*;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class GitOperations {
    File workspace;
    TaskListener listener;
    EnvVars envVars;
    String url;
    GitClient git;

    final String DUMMY_BRANCH_NAME = "DUMMY_8DD2963";   // Just to avoid duplicate branch name

    public GitOperations(File workspace, TaskListener listener, EnvVars envVars, String url) throws IOException, InterruptedException {
        this.workspace = workspace;
        this.envVars = envVars;
        this.listener = listener;
        this.url = url;

        initialiseGitClient();
    }

    private void initialiseGitClient() throws IOException, InterruptedException {
        git = Git.with(listener, envVars)
                .in(workspace)
                .using("git")
                .getClient();
    }

    public void setEnvVars(EnvVars envVars) throws IOException, InterruptedException {
        this.envVars = envVars;
        initialiseGitClient();
    }

    public TaskListener getListener() {
        return listener;
    }

    public void setWorkspace(File workspace) throws IOException, InterruptedException {
        this.workspace = workspace;
        initialiseGitClient();
    }

    public boolean cloneTheRepo(String branch) {
        CloneCommand clone = git.clone_();
        ArrayList<RefSpec> refSpecs = new ArrayList<>();;
        clone.url(url);

//        if (branch != null && !branch.equals("")){
//
//            refSpecs.add(new RefSpec().setSourceDestination(branch, branch));
//            clone.refspecs(refSpecs);
//        }

        try{
            clone.execute();
        }catch (InterruptedException e){
            listener.getLogger().println("Error while cloning branch " + branch + "from " + url);
            listener.getLogger().println(e.getMessage());
            return false;
        }

        listener.getLogger().println("Cloned branch " + branch + " successfully from " + url + ".");
        return checkout(branch);
    }

    public boolean fetch(String branch, List<RefSpec> refSpecs) {
        FetchCommand fetch = git.fetch_();
        ArrayList<RefSpec> rfs = new ArrayList<>();

        if (branch != null && !branch.equals("")){
            rfs.add(new RefSpec().setSourceDestination(branch, branch));
        }



        fetch.prune(true);

        try{
            fetch.from(new URIish(this.url), rfs);
            fetch.execute();
        }catch (InterruptedException e){
            listener.getLogger().println("Error while fetching branch from " + url);
            listener.getLogger().println(e.getMessage());
            return false;
        }catch (URISyntaxException e){
            listener.getLogger().println("Invalid repository url");
            return false;
        }

        listener.getLogger().println("Fetch successful from " + url + ".");
        return checkout(branch);
    }

    public boolean checkout(String branch) {
//        CheckoutCommand checkoutCommand = git.checkout();
//        checkoutCommand.branch(branch);
//
//
//        try {
//            checkoutCommand.execute();
//        }catch (InterruptedException e){
//            listener.getLogger().println("Error while checkout to branch " + branch);
//            listener.getLogger().println(e.getMessage());
//            return false;
//        }
//
//        listener.getLogger().println("Cloned branch " + branch + " successfully.");
        try {
            git.checkout(branch);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return true;
    }

    public boolean pullChangesOfPullrequest(int PR_Number, String branch) throws InterruptedException {
        if(!cloneTheRepo(branch))
            return false;

        FetchCommand fetchCommand = git.fetch_();

        ArrayList<RefSpec> refSpecs = new ArrayList<>();
        refSpecs.add(new RefSpec().setSource("pull/" + PR_Number + "/head"));

        try{
            fetchCommand.from(new URIish(this.url), refSpecs);
            fetchCommand.execute();
        }catch (InterruptedException e){
            listener.getLogger().println("Error while fetching from " + url);
            listener.getLogger().println(e.getMessage());
            return false;
        }catch (URISyntaxException e){
            listener.getLogger().println("Invalid repository url");
            return false;
        }

        if(!merge("FETCH_HEAD"))
            return false;

        return checkout(branch);
    }

    public boolean merge(String rev) throws InterruptedException {

        MergeCommand mergeCommand = git.merge();
        mergeCommand.setRevisionToMerge(ObjectId.fromString(rev));
        mergeCommand.setCommit(false);
        try{
            mergeCommand.execute();
        }catch (InterruptedException e){
            listener.getLogger().println("Error while merging.");
            listener.getLogger().println(e.getMessage());
            return false;
        }

        return true;
    }
}
