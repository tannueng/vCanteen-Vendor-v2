package com.example.vcanteenvendor;

import com.google.gson.annotations.SerializedName;

import java.util.List;

class ReviewList {

    @SerializedName("vendorScore")
    double vendorScore;

    @SerializedName("reviewList")
    List<Review> reviewList;

    public ReviewList(double vendorScore, List<Review> reviewList) {
        this.vendorScore = vendorScore;
        this.reviewList = reviewList;
    }

    public double getVendorScore() {
        return vendorScore;
    }

    public void setVendorScore(double vendorScore) {
        this.vendorScore = vendorScore;
    }

    public List<Review> getReviewList() {
        return reviewList;
    }

    public void setReviewList(List<Review> reviewList) {
        this.reviewList = reviewList;
    }
}
