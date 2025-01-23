package com.rewritetext.model;

public class ProcessingResult {
    private boolean success;
    private String content;
    private String errorMessage;

    public ProcessingResult(boolean success, String content, String errorMessage) {
        this.success = success;
        this.content = content;
        this.errorMessage = errorMessage;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getContent() {
        return content;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}