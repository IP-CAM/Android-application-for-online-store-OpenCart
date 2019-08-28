package com.example.store.oc;

/**
 * Created by Rost on 05.10.2018.
 */

public class GsonProduct {
    private int product_id;
    private String image;
    private String[] images;
    private float price;
    private String name;
    private String description;
    private int rating;
    private int reviews;
    private AttributeGroups[] attribute_groups;

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

    public String[] getImages() {
        return images;
    }

    public void setImages(String[] images) {
        this.images = images;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public int getReviews() {
        return reviews;
    }

    public void setReviews(int reviews) {
        this.reviews = reviews;
    }

    public AttributeGroups[] getAttribute_groups() {
        return attribute_groups;
    }

    public void setAttribute_groups(AttributeGroups[] attribute_groups) {
        this.attribute_groups = attribute_groups;
    }
}

class AttributeGroups{
    private String name;
    private AttributeItem[] attribute;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AttributeItem[] getAttribute() {
        return attribute;
    }

    public void setAttribute(AttributeItem[] attribute) {
        this.attribute = attribute;
    }

    class AttributeItem{
        private String name;
        private String text;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }
}

