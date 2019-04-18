package com.example.vcanteenvendor;

import com.google.gson.annotations.SerializedName;

public class Review {

    @SerializedName("score")
    double score;

    @SerializedName("orderName")
    String orderName;

    @SerializedName("orderNameExtra")
    String orderNameExtra;

    @SerializedName("comment")
    String comment;

    @SerializedName("createdAt")
    String createdAt;

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public String getOrderName() {
        return orderName;
    }

    public void setOrderName(String orderName) {
        this.orderName = orderName;
    }

    public String getOrderNameExtra() {
        return orderNameExtra;
    }

    public void setOrderNameExtra(String orderNameExtra) {
        this.orderNameExtra = orderNameExtra;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public Review(double score, String orderName, String orderNameExtra, String comment, String createdAt) {
        this.score = score;
        this.orderName = orderName;
        this.orderNameExtra = orderNameExtra;
        this.comment = comment;
        this.createdAt = createdAt;
    }
}
