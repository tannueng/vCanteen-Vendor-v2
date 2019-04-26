package com.example.vcanteenvendor;

import com.google.gson.annotations.SerializedName;

public class Menu {

    @SerializedName("foodName")
    private String foodName;

    @SerializedName("foodPrice")
    private int foodPrice;

    @SerializedName("foodId")
    private int foodId;

    @SerializedName("foodImage")
    private String foodImg;

    @SerializedName("foodStatus")
    private String foodStatus;

    @SerializedName("foodType")
    private String foodType;

    @SerializedName("categoryName")
    private String categoryName;

    @SerializedName("prepareDuration")
    private int prepareDuration;


    /////////////////////////////////////  GETTER SETTER CONSTRUCTOR  //////////////////////////////


    public Menu(String foodName, int foodPrice, int foodId, String foodImg, String foodStatus, String foodType, String categoryName, int prepareDuration) {
        this.foodName = foodName;
        this.foodPrice = foodPrice;
        this.foodId = foodId;
        this.foodImg = foodImg;
        this.foodStatus = foodStatus;
        this.foodType = foodType;
        this.categoryName = categoryName;
        this.prepareDuration = prepareDuration;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public int getPrepareDuration() {
        return prepareDuration;
    }

    public void setPrepareDuration(int prepareDuration) {
        this.prepareDuration = prepareDuration;
    }

    public void setFoodImg(String foodImg) {
        this.foodImg = foodImg;
    }


    public String getFoodImg() {
        return foodImg;
    }

    public String getFoodStatus() {
        return foodStatus;
    }

    public void setFoodStatus(String foodStatus) {
        this.foodStatus = foodStatus;
    }

    public Menu(int foodId, String foodName, int foodPrice, String foodStatus,  String foodImg, String foodType) {
        this.foodName = foodName;
        this.foodPrice = foodPrice;
        this.foodId = foodId;
        this.foodImg = foodImg;
        this.foodStatus = foodStatus;
        this.foodType = foodType;
    }

    public String getFoodType() {
        return foodType;
    }

    public void setFoodType(String foodType) {
        this.foodType = foodType;
    }

    public Menu(){

    }





    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public int getFoodPrice() {
        return foodPrice;
    }

    public void setFoodPrice(int foodPrice) {
        this.foodPrice = foodPrice;
    }

    public int getFoodId() {
        return foodId;
    }

    public void setFoodId(int foodId) {
        this.foodId = foodId;
    }

    @Override
    public String toString() {
        return "Menu{" +
                "foodName='" + foodName + '\'' +
                ", foodPrice=" + foodPrice +
                ", foodId=" + foodId +
                ", foodImg='" + foodImg + '\'' +
                ", foodStatus='" + foodStatus + '\'' +
                ", foodType='" + foodType + '\'' +
                ", categoryName='" + categoryName + '\'' +
                ", prepareDuration='" + prepareDuration + '\'' +
                '}';
    }
}
