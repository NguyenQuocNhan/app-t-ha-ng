package com.example.xyz.Model;

import java.util.List;

public class Order {
    private String ID;
    private String User;
    private String address;
    private float totalMoney;
    List<ItemOrder> order;

    Order() {}

    public Order(String ID, String user, String address, float totalMoney, List<ItemOrder> order) {
        this.ID = ID;
        User = user;
        this.address = address;
        this.totalMoney = totalMoney;
        this.order = order;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getUser() {
        return User;
    }

    public void setUser(String user) {
        User = user;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public float getTotalMoney() {
        return totalMoney;
    }

    public void setTotalMoney(float totalMoney) {
        this.totalMoney = totalMoney;
    }

    public List<ItemOrder> getOrder() {
        return order;
    }

    public void setOrder(List<ItemOrder> order) {
        this.order = order;
    }
}
