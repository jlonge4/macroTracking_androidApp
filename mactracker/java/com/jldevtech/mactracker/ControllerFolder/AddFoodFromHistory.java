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

public class AddFoodFromHistory extends AppCompatActivity {

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
        setContentView(R.layout.add_from_history);
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        username = pref.getString("username" , "");
        Intent intent = getIntent();
        String foodName = intent.getStringExtra("foodName");
        String foodCarbs = intent.getStringExtra("foodCarbs");
        String foodFat = intent.getStringExtra("foodFat");
        String foodProtein = intent.getStringExtra("foodProtein");

        long date = System.currentTimeMillis();
        Locale locale = getResources().getConfiguration().locale;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", locale);
        String foodDate = sdf.format(date);

        foodId = intent.getIntExtra("foodId",0);
        newFoodName = findViewById(R.id.newFoodName);
        newFoodCarbs = findViewById(R.id.newFoodCarbs);
        newFoodFat = findViewById(R.id.newFoodFat);
        newFoodProtein = findViewById(R.id.newFoodProtein);
        newFoodDate = findViewById(R.id.newFoodDate);

        newFoodName.setText(foodName);
        newFoodCarbs.setText(foodCarbs);
        newFoodFat.setText(foodFat);
        newFoodProtein.setText(foodProtein);
        newFoodDate.setText(foodDate);
    }

    public void saveEditedFood(View view) throws Exception {
        FoodEntity foodEntity = new FoodEntity();
        foodEntity.setFoodName(String.valueOf(newFoodName.getText()));
        String protein = String.valueOf(newFoodProtein.getText());
        protein = protein.replace("g","");
        Double p2 = Double.parseDouble(protein);
        foodEntity.setProtein(String.valueOf(p2.intValue()));
        String fat = String.valueOf(newFoodFat.getText()).replace("g" , "");
        Double f2 = Double.parseDouble(fat);
        foodEntity.setFat(String.valueOf(f2.intValue()));
        String carbs = String.valueOf(newFoodCarbs.getText()).replace("g" , "");
        Double c2 = Double.parseDouble(carbs);
        foodEntity.setCarbs(String.valueOf(c2.intValue()));
        foodEntity.setDate(String.valueOf(newFoodDate.getText()));
        foodEntity.setUsername(username);
        if (checkNull(foodEntity)) {
            appDB.foodDao().insertFood(foodEntity);
            Intent i = new Intent(this, MacroInfoScreen.class);
            startActivity(i);
            finish();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(AddFoodFromHistory.this);
            builder.setMessage("Please correctly fill in all of the fields");
            builder.setTitle("Alert !");
            builder.setCancelable(true);
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
    }


    public boolean checkNull(FoodEntity fE) throws NullPointerException, IllegalAccessException {
        for (Field f : fE.getClass().getFields())
            if (f.get(fE).equals("") || f.get(fE) == null)
                return false;
        return true;
    }

}
