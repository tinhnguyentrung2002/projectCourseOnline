package com.example.courseonline.Domain;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class QuestionClass implements Parcelable {
    private String question_id;
    private String question_content;
    private String question_correct_answer;
    private String question_explain;
    private String question_type;
    private List<String> question_answer_option;

    public QuestionClass(String question_id, String question_type, String question_content, String question_correct_answer, String question_explain, List<String> question_answer_option) {
        this.question_id = question_id;
        this.question_content = question_content;
        this.question_correct_answer = question_correct_answer;
        this.question_explain = question_explain;
        this.question_answer_option = question_answer_option;
        this.question_type = question_type;
    }

    public QuestionClass() {
    }

    protected QuestionClass(Parcel in) {
        question_id = in.readString();
        question_content = in.readString();
        question_correct_answer = in.readString();
        question_explain = in.readString();
        question_answer_option = in.createStringArrayList();
        question_type = in.readString();
    }

    public static final Creator<QuestionClass> CREATOR = new Creator<QuestionClass>() {
        @Override
        public QuestionClass createFromParcel(Parcel in) {
            return new QuestionClass(in);
        }

        @Override
        public QuestionClass[] newArray(int size) {
            return new QuestionClass[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(question_id);
        dest.writeString(question_content);
        dest.writeString(question_correct_answer);
        dest.writeString(question_explain);
        dest.writeStringList(question_answer_option);
        dest.writeString(question_type);
    }

    public String getQuestion_explain() {
        return question_explain;
    }

    public void setQuestion_explain(String question_explain) {
        this.question_explain = question_explain;
    }

    public String getQuestion_id() {
        return question_id;
    }

    public void setQuestion_id(String question_id) {
        this.question_id = question_id;
    }

    public String getQuestion_content() {
        return question_content;
    }

    public void setQuestion_content(String question_content) {
        this.question_content = question_content;
    }

    public String getQuestion_correct_answer() {
        return question_correct_answer;
    }

    public void setQuestion_correct_answer(String question_correct_answer) {
        this.question_correct_answer = question_correct_answer;
    }

    public List<String> getQuestion_answer_option() {
        return question_answer_option;
    }

    public void setQuestion_answer_option(List<String> question_answer_option) {
        this.question_answer_option = question_answer_option;
    }

    public String getQuestion_type() {
        return question_type;
    }

    public void setQuestion_type(String question_type) {
        this.question_type = question_type;
    }
}

