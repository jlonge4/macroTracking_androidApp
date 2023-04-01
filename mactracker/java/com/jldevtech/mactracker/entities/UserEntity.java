package com.jldevtech.mactracker.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "user_table")
public class UserEntity {
    @PrimaryKey(autoGenerate = true)
    public int uid;

    @ColumnInfo(name = "username")
    public String username;

    @ColumnInfo(name = "password")
    public String password;

    @ColumnInfo(name = "gender")
    public String gender;

    @ColumnInfo(name = "weight")
    public String weight;

    @ColumnInfo(name = "height")
    public String height;

    @ColumnInfo(name = "age")
    public String age;

    @ColumnInfo(name = "calories")
    public String calories;

    @ColumnInfo(name = "activity")
    public String activity;

    @ColumnInfo(name = "pTarget")
    public double pTarget;

    @ColumnInfo(name = "cTarget")
    public double cTarget;

    @ColumnInfo(name = "fTarget")
    public double fTarget;

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getUserName() {
        return username;
    }

    public void setUserName(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getCalories() {
        return calories;
    }

    public void setCalories(String calories) {
        this.calories = calories;
    }

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public double getpTarget() {
        return pTarget;
    }

    public void setpTarget(double pTarget) {
        this.pTarget = pTarget;
    }

    public double getcTarget() {
        return cTarget;
    }

    public void setcTarget(double cTarget) {
        this.cTarget = cTarget;
    }

    public double getfTarget() {
        return fTarget;
    }

    public void setfTarget(double fTarget) {
        this.fTarget = fTarget;
    }
}
