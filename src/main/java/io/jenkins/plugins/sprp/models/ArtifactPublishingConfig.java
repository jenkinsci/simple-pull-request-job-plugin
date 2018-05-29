package io.jenkins.plugins.sprp.models;

public class ArtifactPublishingConfig {
    private String host;
    private String user;
    private String credentialId;

    ArtifactPublishingConfig(){}

    public String getCredentialId() {
        return credentialId;
    }

    public void setCredentialId(String credentialId) {
        this.credentialId = credentialId;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }
}
