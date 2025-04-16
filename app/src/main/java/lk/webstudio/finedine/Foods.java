package lk.webstudio.finedine;

import java.util.Date;

public class Foods {
    public Foods(String foodName, Double halfPrice, Double fullPrice, String description, String category, Date addedDate, String imgUrl, Boolean vegetarian, String foodId) {
        this.foodName = foodName;
        this.halfPrice = halfPrice;
        this.fullPrice = fullPrice;
        this.description = description;
        this.category = category;
        this.addedDate = addedDate;
        this.imgUrl = imgUrl;
        this.vegetarian = vegetarian;
        this.foodId = foodId;
    }
    String foodName;
    Double halfPrice;
    Double fullPrice;
    String description;
    String category;
    Date addedDate;
    String imgUrl;
    Boolean vegetarian;
    String foodId;

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public Double getHalfPrice() {
        return halfPrice;
    }

    public void setHalfPrice(Double halfPrice) {
        this.halfPrice = halfPrice;
    }

    public Double getFullPrice() {
        return fullPrice;
    }

    public void setFullPrice(Double fullPrice) {
        this.fullPrice = fullPrice;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Date getAddedDate() {
        return addedDate;
    }

    public void setAddedDate(Date addedDate) {
        this.addedDate = addedDate;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public Boolean getVegetarian() {
        return vegetarian;
    }

    public void setVegetarian(Boolean vegetarian) {
        this.vegetarian = vegetarian;
    }

    public String getFoodId() {
        return foodId;
    }

    public void setFoodId(String foodId) {
        this.foodId = foodId;
    }


}
