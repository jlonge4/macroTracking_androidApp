package com.jldevtech.mactracker.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "foods_table")
public class FoodEntity {
    @PrimaryKey(autoGenerate = true)
    public int fid;

    @ColumnInfo(name = "username")
    public String username;

    @ColumnInfo(name = "foodName")
    public String foodName;

    @ColumnInfo(name = "date")
    public String date;

    @ColumnInfo(name = "protein")
    public String protein;

    @ColumnInfo(name = "carbs")
    public String carbs;

    @ColumnInfo(name = "fat")
    public String fat;

    public String getProtein() {
        return protein;
    }

    public void setProtein(String protein) {
        this.protein = protein;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCarbs() {
        return carbs;
    }

    public void setCarbs(String carbs) {
        this.carbs = carbs;
    }

    public String getFat() {
        return fat;
    }

    public void setFat(String fat) {
        this.fat = fat;
    }

    public int getFid() {
        return fid;
    }

    public void setFid(int fid) {
        this.fid = fid;
    }

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
