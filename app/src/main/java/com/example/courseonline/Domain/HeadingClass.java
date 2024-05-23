package com.example.courseonline.Domain;

public class HeadingClass {
    private String heading_title;
    private String heading_id;
    private String course_id;

    public HeadingClass(String heading_title, String heading_id, String course_id) {
        this.heading_title = heading_title;
        this.heading_id = heading_id;
        this.course_id = course_id;
    }

    public HeadingClass() {
    }

    public String getCourse_id() {
        return course_id;
    }

    public void setCourse_id(String course_id) {
        this.course_id = course_id;
    }

    public String getHeading_title() {
        return heading_title;
    }

    public void setHeading_title(String heading_title) {
        this.heading_title = heading_title;
    }

    public String getHeading_id() {
        return heading_id;
    }

    public void setHeading_id(String heading_id) {
        this.heading_id = heading_id;
    }
}
