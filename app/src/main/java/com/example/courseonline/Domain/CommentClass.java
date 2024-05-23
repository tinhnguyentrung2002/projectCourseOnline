package com.example.courseonline.Domain;

import java.util.Date;

public class CommentClass {

    private String course_id;
    private String comment_id;
    private String user_id;
    private String user_name;
    private String user_avatar;
    private int comment_rate;
    private int comment_like;
    private String comment_content;
    private Date comment_upload_time;


    public CommentClass(String course_id, String comment_id, String user_id, String user_name, String user_avatar, int comment_rate, int comment_like, String comment_content, Date comment_upload_time) {
        this.course_id = course_id;
        this.comment_id = comment_id;
        this.user_id = user_id;
        this.user_name = user_name;
        this.user_avatar = user_avatar;
        this.comment_rate = comment_rate;
        this.comment_like = comment_like;
        this.comment_content = comment_content;
        this.comment_upload_time = comment_upload_time;
    }

    public CommentClass() {
    }

    public String getCourse_id() {
        return course_id;
    }

    public void setCourse_id(String course_id) {
        this.course_id = course_id;
    }

    public String getComment_id() {
        return comment_id;
    }

    public void setComment_id(String comment_id) {
        this.comment_id = comment_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_avatar() {
        return user_avatar;
    }

    public void setUser_avatar(String user_avatar) {
        this.user_avatar = user_avatar;
    }

    public int getComment_rate() {
        return comment_rate;
    }

    public void setComment_rate(int comment_rate) {
        this.comment_rate = comment_rate;
    }

    public String getComment_content() {
        return comment_content;
    }

    public void setComment_content(String comment_content) {
        this.comment_content = comment_content;
    }

    public Date getComment_upload_time() {
        return comment_upload_time;
    }

    public void setComment_upload_time(Date comment_upload_time) {
        this.comment_upload_time = comment_upload_time;
    }

    public int getComment_like() {
        return comment_like;
    }

    public void setComment_like(int comment_like) {
        this.comment_like = comment_like;
    }
}
