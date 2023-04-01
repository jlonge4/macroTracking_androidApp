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
import java.text.SimpleDateFormat;
import java.util.Locale;

public class AddFood extends AppCompatActivity {
    AppDatabase appDB;
    EditText newFoodName;
    EditText newFoodFat;
    EditText newFoodCarbs;
    EditText newFoodProtein;
    EditText newFoodDate;
    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appDB = AppDatabase.getInstance(getApplicationContext());
        setContentView(R.layout.add_food);
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        username = pref.getString("username" , "");
        newFoodName = findViewById(R.id.newFoodName);
        newFoodCarbs = findViewById(R.id.newFoodCarbs);
        newFoodFat = findViewById(R.id.newFoodFat);
        newFoodProtein = findViewById(R.id.newFoodProtein);
        newFoodDate = findViewById(R.id.newFoodDate);

        long date = System.currentTimeMillis();
        Locale locale = getResources().getConfiguration().locale;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", locale);
        String dateString = sdf.format(date);
        newFoodDate.setText(dateString);
    }

    public void addFood(View view) throws Exception {
        FoodEntity foodEntity = new FoodEntity();
        foodEntity.setFoodName(String.valueOf(newFoodName.getText()));
        foodEntity.setDate(String.valueOf(newFoodDate.getText()));
        foodEntity.setProtein(String.valueOf(newFoodProtein.getText()));
        foodEntity.setCarbs(String.valueOf(newFoodCarbs.getText()));
        foodEntity.setFat(String.valueOf(newFoodFat.getText()));
        foodEntity.setUsername(username);
        if (checkNull(foodEntity)) {
            appDB.foodDao().insertFood(foodEntity);
            Intent i = new Intent(this, MacroInfoScreen.class);
            startActivity(i);
            finish();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(AddFood.this);
            builder.setMessage("Please correctly fill in all of the fields");
            builder.setTitle("Alert!");
            builder.setCancelable(true);
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
    }

    public boolean checkNull(FoodEntity fE) throws Exception {
        for (Field f : fE.getClass().getFields())
            if (f.get(fE).equals("") || f.get(fE) == null)
                return false;
        return true;
    }

}
