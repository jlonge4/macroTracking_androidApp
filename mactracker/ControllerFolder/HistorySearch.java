
package com.jldevtech.mactracker.ControllerFolder;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.jldevtech.mactracker.Adapters.HistorySearchAdapter;
import com.jldevtech.mactracker.Database.AppDatabase;
import com.jldevtech.mactracker.R;
import com.jldevtech.mactracker.entities.FoodEntity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HistorySearch extends AppCompatActivity {

    private ArrayList<FoodEntity> foodList;
    private ArrayList<FoodEntity> searchedFoods;
    RecyclerView foodsList;
    AppDatabase appDB;
    DatePicker spinnerDate;
    List<FoodEntity> allFoods;
    SearchView search;
    String filterSearch;
    String username;
    private RequestQueue queue;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.generate_meal_reports);
        appDB = AppDatabase.getInstance(getApplicationContext());
        foodsList = findViewById(R.id.foodsList);
        spinnerDate = findViewById(R.id.spinnerDate);
        search = findViewById(R.id.search);
        int id = search.getContext()
                .getResources()
                .getIdentifier("android:id/search_src_text", null, null);
        TextView textView = (TextView) search.findViewById(id);
        textView.setTextColor(Color.WHITE);

        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        username = pref.getString("username" , "");

        foodList = new ArrayList<>();
        allFoods = null;
        spinnerDate.setOnDateChangedListener(myDateListener);

        updateLists();
        setAdapter();

        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                allFoods = updateSearchResult();
                foodList.clear();
                foodList.addAll(allFoods);
                setAdapter();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }

        });
    }

    private void setAdapter() {
        HistorySearchAdapter adapter = new HistorySearchAdapter(this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(), 1);
        foodsList.setLayoutManager(gridLayoutManager);
        foodsList.setItemAnimator(new DefaultItemAnimator());
        foodsList.setAdapter(adapter);
        adapter.setTerms(foodList);
    }


    private DatePicker.OnDateChangedListener myDateListener = new DatePicker.OnDateChangedListener() {
        @Override
        public void onDateChanged(DatePicker datePicker, int year, int month, int day) {
            month += 1;
            String unformattedDate = year + "-" + month + "-" + day;
            String date = formatDate(unformattedDate);
            try {
                foodList.clear();
                allFoods = appDB.foodDao().getTodaysFoods(date, username);
                foodList.addAll(allFoods);
                setAdapter();
            } catch (Exception e) {
                AlertDialog.Builder builder = new AlertDialog.Builder(HistorySearch.this);
                builder.setMessage("No foods recorded on this date!");
                builder.setTitle("Alert !");
                builder.setCancelable(true);
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        }
    };

    private void updateLists() {
        long date = System.currentTimeMillis();
        Locale locale = getResources().getConfiguration().locale;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", locale);
        String dateString = sdf.format(date);
        System.out.println(dateString);
        List<FoodEntity> allFoods = appDB.foodDao().getTodaysFoods(dateString, username);
        foodList.addAll(allFoods);
    }

    public List<FoodEntity> updateSearchResult() {
        searchedFoods = new ArrayList<FoodEntity>();
        allFoods = appDB.foodDao().getFoods();
        for(FoodEntity f : allFoods) {
            if(f.getFoodName().toLowerCase().contains(search.getQuery())){
                searchedFoods.add(f);
            }
        }
        allFoods.clear();
        allFoods.addAll(searchedFoods);
        return searchedFoods;
    }

    public String formatDate(String date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date d_date = null;
        try {
            d_date = dateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String strDate = dateFormat.format(d_date);
        return strDate;
    }

}