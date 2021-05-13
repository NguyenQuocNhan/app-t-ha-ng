package com.example.xyz.Model;

public class ItemOrder {
    private int index;
    private int number;
    private String productID;
    private float sumMoney;

    ItemOrder() {}

    public ItemOrder(int index, int number, String productID, float sumMoney) {
        this.index = index;
        this.number = number;
        this.productID = productID;
        this.sumMoney = sumMoney;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getProductID() {
        return productID;
    }

    public void setProductID(String productID) {
        this.productID = productID;
    }

    public float getSumMoney() {
        return sumMoney;
    }

    public void setSumMoney(float sumMoney) {
        this.sumMoney = sumMoney;
    }
}
