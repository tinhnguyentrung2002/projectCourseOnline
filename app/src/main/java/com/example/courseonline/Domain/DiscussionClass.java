package com.example.courseonline.Domain;

import java.util.Date;

public class DiscussionClass {
    private String course_id;
    private String discussion_id;
    private String discussion_title;
    private String discussion_thumbnail;
    private Date discussion_upload;

    public DiscussionClass(String course_id, String discussion_id, String discussion_title, String discussion_thumbnail, Date discussion_upload) {
        this.course_id = course_id;
        this.discussion_id = discussion_id;
        this.discussion_title = discussion_title;
        this.discussion_thumbnail = discussion_thumbnail;
        this.discussion_upload = discussion_upload;
    }

    public DiscussionClass() {
    }

    public String getDiscussion_id() {
        return discussion_id;
    }

    public void setDiscussion_id(String discussion_id) {
        this.discussion_id = discussion_id;
    }

    public String getDiscussion_title() {
        return discussion_title;
    }

    public void setDiscussion_title(String discussion_title) {
        this.discussion_title = discussion_title;
    }

    public String getDiscussion_thumbnail() {
        return discussion_thumbnail;
    }

    public void setDiscussion_thumbnail(String discussion_thumbnail) {
        this.discussion_thumbnail = discussion_thumbnail;
    }

    public Date getDiscussion_upload() {
        return discussion_upload;
    }

    public void setDiscussion_upload(Date discussion_upload) {
        this.discussion_upload = discussion_upload;
    }

    public String getCourse_id() {
        return course_id;
    }

    public void setCourse_id(String course_id) {
        this.course_id = course_id;
    }
}
