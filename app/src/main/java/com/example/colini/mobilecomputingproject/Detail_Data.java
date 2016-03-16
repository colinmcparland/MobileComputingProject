package com.example.colini.mobilecomputingproject;

import java.io.Serializable;

/**
 * Created by Yang on 2016/3/15.
 */
public class Detail_Data implements Serializable{
    private String name;
    private String category;
    private String price;
    private String description;
    private String barcode;

    public Detail_Data (String name,String category, String price, String description, String barcode){
        this.name = name;
        this.category = category;
        this.price = price;
        this.description = description;
        this.barcode = barcode;
    }

    public String getBarcode() {
        return barcode;
    }

    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }

    public String getName() {
        return name;
    }

    public String getPrice() {
        return price;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(String price) {
        this.price = price;
    }


}
