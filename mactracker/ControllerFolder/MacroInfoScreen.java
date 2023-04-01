package com.jldevtech.mactracker.ControllerFolder;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jldevtech.mactracker.Adapters.FoodAdapter;
import com.jldevtech.mactracker.Database.AppDatabase;
import com.jldevtech.mactracker.R;
import com.jldevtech.mactracker.entities.FoodEntity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MacroInfoScreen extends AppCompatActivity {
    public static final String EXTRA_MESSAGE = "";
    private TextView totalCals;
    private TextView valueP;
    private TextView valueC;
    private TextView valueF;
    private TextView todaysDate;
    private ProgressBar proteinProgress;
    private ProgressBar carbsProgress;
    private ProgressBar fatProgress;
    private Toolbar toolbarT;
    AppDatabase appDB;
    private RecyclerView foodsList;
    String fatLabel;
    String proteinLabel;
    String carbsLabel;
    List<String> loggedProtein;
    List<String> loggedFat;
    List<String> loggedCarbs;
    String username;
    String calories;
    LocalDate currentDate
            = null;
    String weekday = null;
    int day =0;
    String month = null;
    int year=0;
    private ArrayList<FoodEntity> foodList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appDB = AppDatabase.getInstance(getApplicationContext());
        setContentView(R.layout.macro_info);
        todaysDate = findViewById(R.id.todaysDate);
        valueP = findViewById(R.id.valueP);
        valueC = findViewById(R.id.valueC);
        valueF = findViewById(R.id.valueF);
        toolbarT = findViewById(R.id.toolbar);
        proteinProgress = findViewById(R.id.proteinProgress);
        carbsProgress = findViewById(R.id.carbsProgress);
        fatProgress = findViewById(R.id.fatProgress);
        proteinProgress.setVisibility(View.VISIBLE);
        foodsList = findViewById(R.id.foodsList);
        totalCals = findViewById(R.id.totalCals);

        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        username = pref.getString("username" , "");
        calories = appDB.userDao().getUserCalories(username);

        //Set day and month on top of home screen
        long date = System.currentTimeMillis();
        Locale locale = getResources().getConfiguration().locale;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", locale);
        String dateString = sdf.format(date);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            currentDate = LocalDate.parse(dateString);
            weekday = currentDate.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
            day = currentDate.getDayOfMonth();
            year = currentDate.getYear();
            month = currentDate.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
        }

        //Calculate users macronutrients
        double pTarg = appDB.userDao().getPtarget(username);
        double cTarg = appDB.userDao().getCtarget(username);
        double fTarg = appDB.userDao().getFtarget(username);
        int caloriesInfo = Integer.parseInt(calories);
        int protein = (int) (caloriesInfo * (pTarg/100)) / 4;
        int carbs = (int) (caloriesInfo * (cTarg/100)) / 4;
        int fat = (int) (caloriesInfo * (fTarg/100)) / 9;

        //Get each macro value from DB
        loggedProtein = appDB.foodDao().getProtein(dateString, username);
        loggedCarbs = appDB.foodDao().getCarbs(dateString, username);
        loggedFat = appDB.foodDao().getFat(dateString, username);

        /** Logged number of macros to be parsed from database and replace "00"*/
        int totezProtez = getMacs(loggedProtein);
        proteinLabel = totezProtez + "/" + protein;
        int totezFatz = getMacs(loggedFat);
        fatLabel = totezFatz + "/" + fat;
        int totezCarbz = getMacs(loggedCarbs);
        carbsLabel = totezCarbz + "/" + carbs;

        totalCals.setText("Total calories: " + calories);
        todaysDate.setText(weekday + ", " + month + " " +  day);
        valueF.setText(fatLabel);
        valueC.setText(carbsLabel);
        valueP.setText(proteinLabel);
        proteinProgress.setMax(protein);
        proteinProgress.setProgress(totezProtez);
        fatProgress.setMax(fat);
        fatProgress.setProgress(totezFatz);
        carbsProgress.setMax(carbs);
        carbsProgress.setProgress(totezCarbz);
        setSupportActionBar(toolbarT);
        toolbarT.setOverflowIcon(ContextCompat.getDrawable(this, R.drawable.three_dots));
        getSupportActionBar().setTitle(null);

        foodList = new ArrayList<>();
        updateLists();
        setAdapter();
    }

    /** Set toolbar options menu items*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_menu_real, menu);
        return true;
    }

    /**Sets adapter for recycler view to populate results(foods)*/
    private void setAdapter() {
        FoodAdapter adapter = new FoodAdapter(this);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(),1);
        foodsList.setLayoutManager(gridLayoutManager);
        foodsList.setItemAnimator(new DefaultItemAnimator());
        foodsList.setAdapter(adapter);
        adapter.setTerms(foodList);
    }

    /**Uses current (system date) to get the foods for the current day only*/
    private void updateLists() {
        long date = System.currentTimeMillis();
        Locale locale = getResources().getConfiguration().locale;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", locale);
        String dateString = sdf.format(date);
        List<FoodEntity> allFoods = appDB.foodDao().getTodaysFoods(dateString, username);
        foodList.addAll(allFoods);
    }

    /**Case for each toolbar menu item to be selected */
    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                Intent logout = new Intent(MacroInfoScreen.this, LoginScreen.class);
                SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
                SharedPreferences.Editor editor = pref.edit();
                editor.putBoolean("logged", false);
                editor.commit();
                startActivity(logout);
                finish();
                return true;
            case R.id.editSettings:
                Intent editSettings = new Intent(MacroInfoScreen.this, EditUser.class);
                startActivity(editSettings);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**Add food method takes you to search food screen */
    public void addFood(View view) {
        Intent i = new Intent(this, SearchFood.class);
        String message = username;
        i.putExtra(EXTRA_MESSAGE, message);
        startActivity(i);
    }

    /**Calculate macros for each F,C,P **/
    public int getMacs(List<String> loggedMac) {
        int totezFatz = 0;
        if (loggedFat != null) {
            for (String sF : loggedFat) {
                int numericF = Integer.parseInt(sF);
                totezFatz += numericF;
            }
            return totezFatz;
        } else {
            return 0;
        }
    }
}