package com.example.courseonline.Domain;

public class TypeClass {
    private  String type_id;
    private String category_child_id;
    private String category_id;
    private String course_id;

    public TypeClass(String type_id, String category_child_id, String category_id, String course_id) {
        this.type_id = type_id;
        this.category_id = category_id;
        this.course_id = course_id;
        this.category_child_id = category_child_id;
    }

    public TypeClass() {
    }

    public String getType_id() {
        return type_id;
    }

    public void setType_id(String type_id) {
        this.type_id = type_id;
    }

    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

    public String getCourse_id() {
        return course_id;
    }

    public void setCourse_id(String course_id) {
        this.course_id = course_id;
    }

    public String getCategory_child_id() {
        return category_child_id;
    }

    public void setCategory_child_id(String category_child_id) {
        this.category_child_id = category_child_id;
    }
}
