package com.falabella.assessment.entities;

public class InputSchema {
    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    private String body;

    InputSchema(String body) {
        this.body = body;
    }
}
