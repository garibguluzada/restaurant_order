package com.orderme.backend.additional_products;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "additional_products")
public class AdditionalProduct {

    @Id
    private int prodId;
    private int mealid;
    private String products;
    private double price;

    // Getters and Setters
    public int getProdId() {
        return prodId;
    }

    public void setProdId(int prodId) {
        this.prodId = prodId;
    }

    public int getMealid() {
        return mealid;
    }

    public void setMealid(int mealid) {
        this.mealid = mealid;
    }

    public String getProducts() {
        return products;
    }

    public void setProducts(String products) {
        this.products = products;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AdditionalProduct that = (AdditionalProduct) o;
        return prodId == that.prodId &&
               mealid == that.mealid &&
               Double.compare(that.price, price) == 0 &&
               Objects.equals(products, that.products);
    }

    @Override
    public int hashCode() {
        return Objects.hash(prodId, mealid, products, price);
    }
}