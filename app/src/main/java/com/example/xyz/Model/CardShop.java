package com.example.xyz.Model;

public class CardShop {
    private String ID;
    private String productID;
    private int number;
    private float sumMoney;

    CardShop(){}

    public CardShop(String ID, String productID, int number, float sumMoney) {
        this.ID = ID;
        this.productID = productID;
        this.number = number;
        this.sumMoney = sumMoney;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getProductID() {
        return productID;
    }

    public void setProductID(String productID) {
        this.productID = productID;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public float getSumMoney() {
        return sumMoney;
    }

    public void setSumMoney(float sumMoney) {
        this.sumMoney = sumMoney;
    }
}

