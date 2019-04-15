package com.example.vcanteenvendor;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CombinationAlacarteList {

    @SerializedName("combinationList")
    public List<Menu> combinationList;

    @SerializedName("alacarteList")
    public List<Menu> alacarteList;

    /////////////////////////////////////  GETTER SETTER CONSTRUCTOR  //////////////////////////////

    public List<Menu> getCombinationList() {
        return combinationList;
    }

    public void setCombinationList(List<Menu> combinationList) {
        this.combinationList = combinationList;
    }

    public List<Menu> getAlacarteList() {
        return alacarteList;
    }

    public void setAlacarteList(List<Menu> alacarteList) {
        this.alacarteList = alacarteList;
    }

    public CombinationAlacarteList(List<Menu> combinationList, List<Menu> alacarteList) {
        this.combinationList = combinationList;
        this.alacarteList = alacarteList;
    }
}
