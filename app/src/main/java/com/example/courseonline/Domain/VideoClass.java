package com.example.courseonline.Domain;

public class VideoClass {
    private String course_id;
    private String video_title;
    private String video_url;
    private String video_duration;

    private String video_id;

    public VideoClass(String course_id, String video_title, String video_url, String video_duration, String video_id) {
        this.course_id = course_id;
        this.video_title = video_title;
        this.video_url = video_url;
        this.video_duration = video_duration;
        this.video_id = video_id;
    }

    public VideoClass() {
    }

    public String getVideo_id() {
        return video_id;
    }

    public void setVideo_id(String video_id) {
        this.video_id = video_id;
    }

    public String getVideo_duration() {
        return video_duration;
    }

    public void setVideo_duration(String video_duration) {
        this.video_duration = video_duration;
    }

    public String getVideo_title() {
        return video_title;
    }

    public void setVideo_title(String video_title) {
        this.video_title = video_title;
    }

    public String getVideo_url() {
        return video_url;
    }

    public void setVideo_url(String video_url) {
        this.video_url = video_url;
    }

    public String getCourse_id() {
        return course_id;
    }

    public void setCourse_id(String course_id) {
        this.course_id = course_id;
    }
}
