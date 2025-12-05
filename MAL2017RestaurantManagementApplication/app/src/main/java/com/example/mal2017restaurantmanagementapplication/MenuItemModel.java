package com.example.mal2017restaurantmanagementapplication;

public class MenuItemModel {
    public int imageRes;
    public String tag;
    public String title;
    public String desc;
    public String price;

    public MenuItemModel(int imageRes, String tag, String title, String desc, String price){
        this.imageRes = imageRes;
        this.tag = tag;
        this.title = title;
        this.desc = desc;
        this.price = price;
    }
}
