package com.jldevtech.mactracker.DAOFolder;



import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.jldevtech.mactracker.entities.UserEntity;


@Dao
public interface UserDao {
    @Query("SELECT calories FROM user_table WHERE username = :un")
    String getUserCalories(String un);

    @Query("SELECT password FROM user_table WHERE username = :un")
    String getPassword(String un);

    @Query("SELECT uid FROM user_table WHERE username = :un")
    int getUid(String un);

    @Query("SELECT * FROM user_table WHERE username = :un")
    UserEntity getU(String un);

    @Query("SELECT pTarget FROM user_table WHERE username = :un")
    double getPtarget(String un);

    @Query("SELECT cTarget FROM user_table WHERE username = :un")
    double getCtarget(String un);

    @Query("SELECT fTarget FROM user_table WHERE username = :un")
    double getFtarget(String un);

    @Insert
    void insertUser(UserEntity userEntity);

    @Delete
    void delete(UserEntity userEntity);

    @Update
    void update(UserEntity userEntity);
}
