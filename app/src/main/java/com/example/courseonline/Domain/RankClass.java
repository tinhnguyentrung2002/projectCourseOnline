package com.example.courseonline.Domain;

import com.google.firebase.Timestamp;

public class RankClass {
    private String rank_user_id;
    private long rank_user_points;
    private int rank_position;
    private Timestamp last_claimed;

    public RankClass(String rank_user_id, long rank_user_points, int rank_position, Timestamp last_claimed) {
        this.rank_user_id = rank_user_id;
        this.rank_user_points = rank_user_points;
        this.rank_position = rank_position;
        this.last_claimed = last_claimed;;
    }

    public RankClass() {
    }

    public String getRank_user_id() {
        return rank_user_id;
    }

    public void setRank_user_id(String rank_user_id) {
        this.rank_user_id = rank_user_id;
    }

    public long getRank_user_points() {
        return rank_user_points;
    }

    public void setRank_user_points(long rank_user_points) {
        this.rank_user_points = rank_user_points;
    }

    public int getRank_position() {
        return rank_position;
    }

    public void setRank_position(int rank_position) {
        this.rank_position = rank_position;
    }

    public Timestamp getLast_claimed() {
        return last_claimed;
    }

    public void setLast_claimed(Timestamp last_claimed) {
        this.last_claimed = last_claimed;
    }
}
