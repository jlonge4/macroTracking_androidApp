package com.jldevtech.mactracker.ControllerFolder;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.jldevtech.mactracker.Database.AppDatabase;
import com.jldevtech.mactracker.R;
import com.jldevtech.mactracker.entities.FoodEntity;

import java.lang.reflect.Field;

public class EditFood extends AppCompatActivity {

    public static final String EXTRA_MESSAGE = "";
    AppDatabase appDB;
    EditText newFoodName;
    EditText newFoodFat;
    EditText newFoodCarbs;
    EditText newFoodProtein;
    EditText newFoodDate;
    String username;
    int foodId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appDB = AppDatabase.getInstance(getApplicationContext());
        setContentView(R.layout.edit_food);
        newFoodName = findViewById(R.id.newFoodName);
        newFoodCarbs = findViewById(R.id.newFoodCarbs);
        newFoodFat = findViewById(R.id.newFoodFat);
        newFoodProtein = findViewById(R.id.newFoodProtein);
        newFoodDate = findViewById(R.id.newFoodDate);

        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        username = pref.getString("username" , "");
        Intent intent = getIntent();
        String foodName = intent.getStringExtra("foodName");
        String foodCarbs = intent.getStringExtra("foodCarbs");
        String foodFat = intent.getStringExtra("foodFat");
        String foodProtein = intent.getStringExtra("foodProtein");
        String foodDate = intent.getStringExtra("foodDate");
        foodId = intent.getIntExtra("foodId",0);

        newFoodName.setText(foodName);
        newFoodCarbs.setText(foodCarbs);
        newFoodFat.setText(foodFat);
        newFoodProtein.setText(foodProtein);
        newFoodDate.setText(foodDate);
    }

    public void saveEditedFood(View view) throws Exception {
        FoodEntity foodEntity = new FoodEntity();
        foodEntity.setFid(foodId);
        foodEntity.setFoodName(String.valueOf(newFoodName.getText()));
        foodEntity.setProtein(String.valueOf(newFoodProtein.getText()));
        foodEntity.setFat(String.valueOf(newFoodFat.getText()));
        foodEntity.setCarbs(String.valueOf(newFoodCarbs.getText()));
        foodEntity.setDate(String.valueOf(newFoodDate.getText()));
        foodEntity.setUsername(username);
        if (checkNull(foodEntity)) {
            appDB.foodDao().update(foodEntity);
            Intent i = new Intent(this, MacroInfoScreen.class);
            startActivity(i);
            finish();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(EditFood.this);
            builder.setMessage("Please correctly fill in all of the fields");
            builder.setTitle("Alert !");
            builder.setCancelable(true);
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
    }

    public void deleteFoodRecord(View view) {
        FoodEntity foodEntity = new FoodEntity();
        foodEntity.setFid(foodId);
        foodEntity.setFoodName(String.valueOf(newFoodName.getText()));
        foodEntity.setProtein(String.valueOf(newFoodProtein.getText()));
        foodEntity.setFat(String.valueOf(newFoodFat.getText()));
        foodEntity.setCarbs(String.valueOf(newFoodCarbs.getText()));
        foodEntity.setDate(String.valueOf(newFoodDate.getText()));
        appDB.foodDao().delete(foodEntity);

        Intent i = new Intent(this, MacroInfoScreen.class);
        startActivity(i);
        finish();
    }

    public boolean checkNull(FoodEntity fE) throws NullPointerException, IllegalAccessException {
        for (Field f : fE.getClass().getFields())
            if (f.get(fE).equals("") || f.get(fE) == null)
                return false;
        return true;
    }
}
