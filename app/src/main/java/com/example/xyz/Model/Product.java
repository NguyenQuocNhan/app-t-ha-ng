package com.example.xyz.Model;

public class Product {
    private String ID;
    private String name;
    private float price;
    private String species;
    private int number;
    private String description;
    private String image;
    private float rating;

    Product() {}

    public Product(String ID, String name, float price, String species, int number, String description, String image, float rating) {
        this.ID = ID;
        this.name = name;
        this.price = price;
        this.species = species;
        this.number = number;
        this.description = description;
        this.image = image;
        this.rating = rating;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        this.species = species;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }
}