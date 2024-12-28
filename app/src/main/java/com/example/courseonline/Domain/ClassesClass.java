package com.example.courseonline.Domain;

import java.util.Date;
import java.util.List;

public class ClassesClass {
    private String course_id;
    private String course_title;
    private String course_owner_id;
    private List<String> course_member;
    private String course_img;
    private String course_private_key;
    private Date course_start_time;
    private Date course_end_time;
    private String course_description;
    private String course_type;
    private List<String> course_recommend;
    private Date course_upload;


    public ClassesClass(String course_id, String course_title, String course_owner_id, List<String> course_member, String course_img, String course_private_key, Date course_start_time, Date course_end_time, String course_description, String course_type, List<String> course_recommend, Date course_upload) {
        this.course_id = course_id;
        this.course_title = course_title;
        this.course_owner_id = course_owner_id;
        this.course_member = course_member;
        this.course_img = course_img;
        this.course_private_key = course_private_key;
        this.course_start_time = course_start_time;
        this.course_end_time = course_end_time;
        this.course_description = course_description;
        this.course_type = course_type;
        this.course_recommend = course_recommend;
        this.course_upload = course_upload;
    }

    public ClassesClass() {
    }

    public String getCourse_id() {
        return course_id;
    }

    public void setCourse_id(String course_id) {
        this.course_id = course_id;
    }

    public String getCourse_title() {
        return course_title;
    }

    public void setCourse_title(String course_title) {
        this.course_title = course_title;
    }

    public String getCourse_owner_id() {
        return course_owner_id;
    }

    public void setCourse_owner_id(String course_owner_id) {
        this.course_owner_id = course_owner_id;
    }

    public List<String> getCourse_member() {
        return course_member;
    }

    public void setCourse_member(List<String> course_member) {
        this.course_member = course_member;
    }

    public String getCourse_img() {
        return course_img;
    }

    public void setCourse_img(String course_img) {
        this.course_img = course_img;
    }

    public String getCourse_private_key() {
        return course_private_key;
    }

    public void setCourse_private_key(String course_private_key) {
        this.course_private_key = course_private_key;
    }

    public Date getCourse_start_time() {
        return course_start_time;
    }

    public void setCourse_start_time(Date course_start_time) {
        this.course_start_time = course_start_time;
    }

    public Date getCourse_end_time() {
        return course_end_time;
    }

    public void setCourse_end_time(Date course_end_time) {
        this.course_end_time = course_end_time;
    }

    public String getCourse_description() {
        return course_description;
    }

    public void setCourse_description(String course_description) {
        this.course_description = course_description;
    }

    public String getCourse_type() {
        return course_type;
    }

    public void setCourse_type(String course_type) {
        this.course_type = course_type;
    }

    public List<String> getCourse_recommend() {
        return course_recommend;
    }

    public void setCourse_recommend(List<String> course_recommend) {
        this.course_recommend = course_recommend;
    }

    public Date getCourse_upload() {
        return course_upload;
    }

    public void setCourse_upload(Date course_upload) {
        this.course_upload = course_upload;
    }
}
