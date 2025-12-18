package com.example.mal2017restaurantmanagementapplication;

public class MenuItem {
    private int id;
    private String name;
    private String price;
    private String description;
    private String category;
    private String imagePath;

    public MenuItem() {}

    public MenuItem(int id, String name, String price, String description, String category, String imagePath) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.description = description;
        this.category = category;
        this.imagePath = imagePath;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPrice() { return price; }
    public void setPrice(String price) { this.price = price; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }
}