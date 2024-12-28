package com.example.courseonline.Domain;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;

public class UserClass {
    private FirebaseFirestore db;
    private String user_avatar;
    private String user_email;
    private String user_grade;
    private Timestamp user_lastLogin;
    private String user_level;
    private String user_name;
    private String user_permission;
    private long user_points;
    private long user_best_points;
    private long user_streakLogin;
    private String user_uid;


    public UserClass(String user_avatar, String user_email, String user_grade, Timestamp user_lastLogin, String user_level, String user_name, String user_permission, long user_points, long user_best_points, long user_streakLogin, String user_uid) {
        this.user_avatar = user_avatar;
        this.user_email = user_email;
        this.user_grade = user_grade;
        this.user_lastLogin = user_lastLogin;
        this.user_level = user_level;
        this.user_name = user_name;
        this.user_permission = user_permission;
        this.user_points = user_points;
        this.user_best_points = user_best_points;
        this.user_streakLogin = user_streakLogin;
        this.user_uid = user_uid;
    }

    public UserClass() {
    }

    public String getUser_avatar() {
        return user_avatar;
    }

    public void setUser_avatar(String user_avatar) {
        this.user_avatar = user_avatar;
    }

    public String getUser_email() {
        return user_email;
    }

    public void setUser_email(String user_email) {
        this.user_email = user_email;
    }

    public String getUser_grade() {
        return user_grade;
    }

    public void setUser_grade(String user_grade) {
        this.user_grade = user_grade;
    }

    public Timestamp getUser_lastLogin() {
        return user_lastLogin;
    }

    public void setUser_lastLogin(Timestamp user_lastLogin) {
        this.user_lastLogin = user_lastLogin;
    }

    public String getUser_level() {
        return user_level;
    }

    public void setUser_level(String user_level) {
        this.user_level = user_level;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_permission() {
        return user_permission;
    }

    public void setUser_permission(String user_permission) {
        this.user_permission = user_permission;
    }

    public long getUser_points() {
        return user_points;
    }

    public void setUser_points(long user_points) {
        this.user_points = user_points;
    }

    public long getUser_streakLogin() {
        return user_streakLogin;
    }

    public void setUser_streakLogin(long user_streakLogin) {
        this.user_streakLogin = user_streakLogin;
    }

    public String getUser_uid() {
        return user_uid;
    }

    public void setUser_uid(String user_uid) {
        this.user_uid = user_uid;
    }

    public long getUser_best_points() {
        return user_best_points;
    }

    public void setUser_best_points(long user_best_points) {
        this.user_best_points = user_best_points;
    }

}
