package com.github.ylfjm.common;

public class YlfjmException extends RuntimeException {

    public YlfjmException(String message) {
        super(message);
    }

    public YlfjmException(String message, Throwable cause) {
        super(message, cause);
    }
}
