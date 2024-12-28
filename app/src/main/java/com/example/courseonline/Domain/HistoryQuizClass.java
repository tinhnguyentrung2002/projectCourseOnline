package com.example.courseonline.Domain;


import com.google.firebase.Timestamp;

public class HistoryQuizClass {

    private String history_quiz_id;
    private String history_quiz_user_id;
    private boolean history_quiz_state;
    private String history_quiz_time_taken;
    private String history_quiz_score;
    private int history_quiz_total_correct;
    private int history_quiz_total_skip;
    private int history_quiz_total_wrong;
    private Timestamp history_quiz_upload;


    public HistoryQuizClass(String history_quiz_id, String history_quiz_user_id, boolean history_quiz_state, String history_quiz_time_taken, String history_quiz_score, int history_quiz_total_correct, int history_quiz_total_skip, int history_quiz_total_wrong, Timestamp history_quiz_upload) {
        this.history_quiz_id = history_quiz_id;
        this.history_quiz_user_id = history_quiz_user_id;
        this.history_quiz_state = history_quiz_state;
        this.history_quiz_time_taken = history_quiz_time_taken;
        this.history_quiz_score = history_quiz_score;
        this.history_quiz_total_correct = history_quiz_total_correct;
        this.history_quiz_total_skip = history_quiz_total_skip;
        this.history_quiz_total_wrong = history_quiz_total_wrong;
        this.history_quiz_upload = history_quiz_upload;
    }

    public HistoryQuizClass() {
    }

    public boolean isHistory_quiz_state() {
        return history_quiz_state;
    }

    public String getHistory_quiz_score() {
        return history_quiz_score;
    }

    public void setHistory_quiz_score(String history_quiz_score) {
        this.history_quiz_score = history_quiz_score;
    }

    public String getHistory_quiz_user_id() {
        return history_quiz_user_id;
    }

    public void setHistory_quiz_user_id(String history_quiz_user_id) {
        this.history_quiz_user_id = history_quiz_user_id;
    }

    public String getHistory_quiz_id() {
        return history_quiz_id;
    }

    public void setHistory_quiz_id(String history_quiz_id) {
        this.history_quiz_id = history_quiz_id;
    }
    public String getHistory_quiz_time_taken() {
        return history_quiz_time_taken;
    }

    public void setHistory_quiz_time_taken(String history_quiz_time_taken) {
        this.history_quiz_time_taken = history_quiz_time_taken;
    }

    public int getHistory_quiz_total_correct() {
        return history_quiz_total_correct;
    }

    public void setHistory_quiz_total_correct(int history_quiz_total_correct) {
        this.history_quiz_total_correct = history_quiz_total_correct;
    }

    public int getHistory_quiz_total_skip() {
        return history_quiz_total_skip;
    }

    public void setHistory_quiz_total_skip(int history_quiz_total_skip) {
        this.history_quiz_total_skip = history_quiz_total_skip;
    }

    public int getHistory_quiz_total_wrong() {
        return history_quiz_total_wrong;
    }

    public void setHistory_quiz_total_wrong(int history_quiz_total_wrong) {
        this.history_quiz_total_wrong = history_quiz_total_wrong;
    }

    public Timestamp getHistory_quiz_upload() {
        return history_quiz_upload;
    }

    public void setHistory_quiz_upload(Timestamp history_quiz_upload) {
        this.history_quiz_upload = history_quiz_upload;
    }
}

