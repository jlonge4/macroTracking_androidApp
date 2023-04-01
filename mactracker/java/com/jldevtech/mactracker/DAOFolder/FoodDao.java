package com.jldevtech.mactracker.DAOFolder;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.jldevtech.mactracker.entities.FoodEntity;

import java.util.List;

@Dao
public interface FoodDao {
    @Query("SELECT protein FROM foods_table WHERE date = :date AND username = :username")
    List<String> getProtein(String date, String username);

    @Query("SELECT fat FROM foods_table WHERE date = :date AND username = :username")
    List<String> getFat(String date, String username);

    @Query("SELECT carbs FROM foods_table WHERE date = :date AND username = :username")
    List<String> getCarbs(String date, String username);

    @Query("SELECT * FROM foods_table WHERE date = :date AND username = :username")
    List<FoodEntity> getTodaysFoods(String date, String username);

    @Query("SELECT * FROM foods_table")
    List<FoodEntity> getFoods();

    @Query("DELETE FROM foods_table WHERE date <= date('now','-21 day')")
    void deleteOldFoods();

    @Update
    void update(FoodEntity foodEntity);

    @Insert
    void insertFood(FoodEntity foodEntity);

    @Delete
    void delete(FoodEntity foodEntity);
}

