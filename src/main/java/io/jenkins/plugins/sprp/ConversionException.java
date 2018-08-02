package io.jenkins.plugins.sprp;

import java.io.IOException;

public class ConversionException extends IOException {

    public ConversionException(String message) {
        super(message);
    }

    public ConversionException(String message, Throwable cause) {
        super(message, cause);
    }
}
