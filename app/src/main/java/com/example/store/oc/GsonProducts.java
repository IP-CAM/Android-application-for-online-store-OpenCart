package com.example.store.oc;

/**
 * Created by Rost on 05.10.2018.
 */

public class GsonProducts {
    private int product_id;
    private String image;
    private float price;
    private String name;

    public int getProduct_id() {
        return product_id;
    }

    public String getImage() {
        return image;
    }

    public float getPrice() {
        return price;
    }

    public void setProduct_id(int product_id) {
        this.product_id = product_id;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
