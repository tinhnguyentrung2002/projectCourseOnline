package com.example.courseonline.Domain;

import java.util.Date;

public class CourseDisplayClass {
    private String course_id;
    private String course_title;
    private String course_owner_id;
    private double course_rate;
    private int course_member;
    private String course_img;
    private float course_total_time;
    private String course_description;
    private String course_type;
    private double course_price;
    private int course_grade;

    public int getCourse_grade() {
        return course_grade;
    }

    public void setCourse_grade(int course_grade) {
        this.course_grade = course_grade;
    }

    private Date course_upload;


    public CourseDisplayClass(String course_id, String course_title, String course_type,String course_owner_id, double course_rate, int course_member, String course_img, float course_total_time, String course_description, double course_price, Date course_upload) {
        this.course_id = course_id;
        this.course_title = course_title;
        this.course_owner_id = course_owner_id;
        this.course_rate = course_rate;
        this.course_member = course_member;
        this.course_img = course_img;
        this.course_type = course_type;
        this.course_total_time = course_total_time;
        this.course_description = course_description;
        this.course_price = course_price;
        this.course_upload = course_upload;
    }

    public CourseDisplayClass() {
    }

    public double getCourse_price() {
        return course_price;
    }

    public void setCourse_price(double course_price) {
        this.course_price = course_price;
    }

    public Date getCourse_upload() {
        return course_upload;
    }

    public void setCourse_upload(Date course_upload) {
        this.course_upload = course_upload;
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

    public String getCourse_id() {
        return course_id;
    }

    public void setCourse_id(String course_id) {
        this.course_id = course_id;
    }

    public double getCourse_rate() {
        return course_rate;
    }

    public void setCourse_rate(double course_rate) {
        this.course_rate = course_rate;
    }

    public int getCourse_member() {
        return course_member;
    }

    public void setCourse_member(int course_member) {
        this.course_member = course_member;
    }

    public String getCourse_img() {
        return course_img;
    }

    public void setCourse_img(String course_img) {
        this.course_img = course_img;
    }

    public float getCourse_total_time() {
        return course_total_time;
    }

    public void setCourse_total_time(float course_total_time) {
        this.course_total_time = course_total_time;
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
}
