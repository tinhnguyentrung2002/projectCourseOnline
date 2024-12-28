package com.example.courseonline.Domain;

import java.util.Date;
import java.util.List;
import java.util.Objects;

public class MessageClass {
    private String message_id;
    private String topic_id;
    private String message_sender_id;
    private String message_content;
    private int message_like;
    private List<String> message_attach;
    private Date message_upload;
    private boolean message_state;

    public MessageClass(String message_id, String topic_id, String message_sender_id, String message_content, int message_like, List<String> message_attach, Date message_upload, boolean message_state) {
        this.message_id = message_id;
        this.topic_id = topic_id;
        this.message_sender_id = message_sender_id;
        this.message_content = message_content;
        this.message_like = message_like;
        this.message_attach = message_attach;
        this.message_upload = message_upload;
        this.message_state = message_state;
    }

    public MessageClass() {
    }

    public boolean isMessage_state() {
        return message_state;
    }

    public void setMessage_state(boolean message_state) {
        this.message_state = message_state;
    }

    public String getMessage_id() {
        return message_id;
    }

    public void setMessage_id(String message_id) {
        this.message_id = message_id;
    }

    public String getTopic_id() {
        return topic_id;
    }

    public void setTopic_id(String topic_id) {
        this.topic_id = topic_id;
    }

    public String getMessage_sender_id() {
        return message_sender_id;
    }

    public void setMessage_sender_id(String message_sender_id) {
        this.message_sender_id = message_sender_id;
    }

    public String getMessage_content() {
        return message_content;
    }

    public void setMessage_content(String message_content) {
        this.message_content = message_content;
    }

    public int getMessage_like() {
        return message_like;
    }

    public void setMessage_like(int message_like) {
        this.message_like = message_like;
    }

    public List<String> getMessage_attach() {
        return message_attach;
    }

    public void setMessage_attach(List<String> message_attach) {
        this.message_attach = message_attach;
    }

    public Date getMessage_upload() {
        return message_upload;
    }

    public void setMessage_upload(Date message_upload) {
        this.message_upload = message_upload;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        MessageClass that = (MessageClass) obj;
        return Objects.equals(message_id, that.message_id);
    }

    @Override
    public int hashCode() {
        return message_id  != null ? message_id.hashCode() : 0;
    }
}
