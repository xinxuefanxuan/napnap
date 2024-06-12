package com.work37.napnap.entity;

import android.graphics.drawable.Drawable;
import android.media.Image;
import android.widget.ImageView;

import java.util.List;

public class Game {
    private int rank;
    private String name;
    private Drawable picture;

    private String profile;

    private String tags;

    private int score;

    public Game() {
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public Game(int rank, String name,Drawable picture, String profile, String tags, int score) {
        this.rank = rank;
        this.name = name;
        this.picture = picture;
        this.profile = profile;
        this.tags = tags;
        this.score = score;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Drawable getPicture() {
        return picture;
    }

    public void setPicture(Drawable picture) {
        this.picture = picture;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
