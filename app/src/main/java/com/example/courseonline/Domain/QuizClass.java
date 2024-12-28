package com.example.courseonline.Domain;

public class QuizClass {
    private String quiz_id;
    private String quiz_title;
    private String quiz_subtitle;
    private String quiz_time;
    private String quiz_require;
    private String course_id;
    private String heading_id;

    public QuizClass(String quiz_id, String quiz_title, String quiz_subtitle, String quiz_time, String quiz_require, String course_id, String heading_id) {
        this.quiz_id = quiz_id;
        this.quiz_title = quiz_title;
        this.quiz_subtitle = quiz_subtitle;
        this.quiz_time = quiz_time;
        this.quiz_require = quiz_require;
        this.course_id = course_id;
        this.heading_id = heading_id;
    }

    public QuizClass() {
    }

    public String getQuiz_id() {
        return quiz_id;
    }

    public void setQuiz_id(String quiz_id) {
        this.quiz_id = quiz_id;
    }

    public String getQuiz_title() {
        return quiz_title;
    }

    public void setQuiz_title(String quiz_title) {
        this.quiz_title = quiz_title;
    }

    public String getQuiz_subtitle() {
        return quiz_subtitle;
    }

    public void setQuiz_subtitle(String quiz_subtitle) {
        this.quiz_subtitle = quiz_subtitle;
    }

    public String getQuiz_time() {
        return quiz_time;
    }

    public void setQuiz_time(String quiz_time) {
        this.quiz_time = quiz_time;
    }

    public String getQuiz_require() {
        return quiz_require;
    }

    public void setQuiz_require(String quiz_require) {
        this.quiz_require = quiz_require;
    }

    public String getCourse_id() {
        return course_id;
    }

    public void setCourse_id(String course_id) {
        this.course_id = course_id;
    }

    public String getHeading_id() {
        return heading_id;
    }

    public void setHeading_id(String heading_id) {
        this.heading_id = heading_id;
    }

}
