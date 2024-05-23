package com.example.courseonline.Domain;

public class CategoryDisplayClass {

    private String category_id;
    private int category_order;
    private String category_title;
    private int category_layer;

    public CategoryDisplayClass(String category_id, int category_order, String category_title, int category_layer) {
        this.category_id = category_id;
        this.category_order = category_order;
        this.category_title = category_title;
        this.category_layer = category_layer;
    }

    public CategoryDisplayClass(){

    }

    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

    public int getCategory_order() {
        return category_order;
    }

    public void setCategory_order(int category_order) {
        this.category_order = category_order;
    }

    public String getCategory_title() {
        return category_title;
    }

    public void setCategory_title(String category_title) {
        this.category_title = category_title;
    }

    public int getCategory_layer() {
        return category_layer;
    }

    public void setCategory_layer(int category_layer) {
        this.category_layer = category_layer;
    }
}
