package com.example.vcanteenvendor;

import com.google.gson.annotations.SerializedName;

public class Menu {

    @SerializedName("foodName")
    private String foodName;

    @SerializedName("price")
    private int foodPrice;

    @SerializedName("foodId")
    private int foodId;





    /////////////////////////////////////  GETTER SETTER CONSTRUCTOR  //////////////////////////////



    @SerializedName("foodImage")
    private String foodImg;

    @SerializedName("foodStatus")
    private String foodStatus;

    @SerializedName("foodType")
    private String foodType;

    public void setFoodImg(String foodImg) {
        this.foodImg = foodImg;
    }




    /////////////////////////////////////  GETTER SETTER CONSTRUCTOR  //////////////////////////////

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


}
