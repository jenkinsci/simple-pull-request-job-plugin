package io.jenkins.plugins.sprp.models;

import javax.annotation.CheckForNull;

/**
 * @author Oleg Nenashev
 */
public class CustomPipelineSection {
    private String name;
    @CheckForNull
    private Object data;

    public void setName(String name) {
        this.name = name;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getName() {
        return name;
    }

    @CheckForNull
    public Object getData() {
        return data;
    }
}
