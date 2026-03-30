package com.pulsedesk.dto;

public class CommentRequest {
    private String text;
    private String source;

    public String getText() { return text; }
    public String getSource() { return source; }
    public void setText(String text) { this.text = text; }
    public void setSource(String source) { this.source = source; }
}
