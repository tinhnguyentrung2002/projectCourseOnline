package com.example.courseonline.Domain;

public class DocumentClass {
    private String document_id;
    private String document_url;
    private String document_title;

    public DocumentClass(String document_id,String document_url, String document_title) {
        this.document_id = document_id;
        this.document_url = document_url;
        this.document_title = document_title;
    }

    public DocumentClass() {
    }

    public String getDocument_id() {
        return document_id;
    }

    public void setDocument_id(String document_id) {
        this.document_id = document_id;
    }

    public String getDocument_url() {
        return document_url;
    }

    public void setDocument_url(String document_url) {
        this.document_url = document_url;
    }

    public String getDocument_title() {
        return document_title;
    }

    public void setDocument_title(String document_title) {
        this.document_title = document_title;
    }
}

