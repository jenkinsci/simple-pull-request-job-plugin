package io.jenkins.plugins.sprp.models;

import java.util.ArrayList;
import java.util.HashMap;

public class Environment {
    private ArrayList<Credential> credentials;
    private HashMap<String, String> variables;

    public ArrayList<Credential> getCredentials() {
        return credentials;
    }

    public void setCredentials(ArrayList<Credential> credentials) {
        this.credentials = credentials;
    }

    public HashMap<String, String> getVariables() {
        return variables;
    }

    public void setVariables(HashMap<String, String> variables) {
        this.variables = variables;
    }
}
