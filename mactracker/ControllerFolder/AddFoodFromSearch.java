package com.jldevtech.mactracker.ControllerFolder;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.jldevtech.mactracker.Database.AppDatabase;
import com.jldevtech.mactracker.R;
import com.jldevtech.mactracker.entities.FoodEntity;
import com.fatsecret.platform.model.Food;
import com.fatsecret.platform.model.Serving;
import com.fatsecret.platform.services.android.Request;
import com.fatsecret.platform.services.android.ResponseListener;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class AddFoodFromSearch extends AppCompatActivity {

    public static final String EXTRA_MESSAGE = "";
    ArrayList<String> servingSizes = new ArrayList();
    HashMap servingsDict = new HashMap();
    AppDatabase appDB;
    EditText newFoodName;
    EditText newFoodFat;
    Spinner servingSpinner;
    Spinner servingsNo;
    EditText newFoodCarbs;
    EditText newFoodProtein;
    EditText newFoodDate;
    String username;
    String description;
    Context context;
    int count = 0;
    int foodId;
    int loaded;
    Double servingAmount;
    String selectedServingSize;
    public Request req;
    public RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        appDB = AppDatabase.getInstance(getApplicationContext());
        setContentView(R.layout.add_from_search);
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        username = pref.getString("username" , "");
        Intent intent = getIntent();
        Long fId = intent.getLongExtra("foodId", 0);
        description = intent.getStringExtra("foodDescription");
        getServings(fId);

        String foodName = intent.getStringExtra("foodName");

        long date = System.currentTimeMillis();
        Locale locale = getResources().getConfiguration().locale;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", locale);
        String foodDate = sdf.format(date);

        newFoodName = findViewById(R.id.newFoodName);
        newFoodCarbs = findViewById(R.id.newFoodCarbs);
        newFoodFat = findViewById(R.id.newFoodFat);
        newFoodProtein = findViewById(R.id.newFoodProtein);
        newFoodDate = findViewById(R.id.newFoodDate);
        servingSpinner = findViewById(R.id.spinner);
        servingsNo = findViewById(R.id.servingsNo);

        newFoodName.setText(foodName);
        newFoodDate.setText(foodDate);
        servingsNo.setSelection(3);

        servingsNo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                servingAmount =  Double.parseDouble((String)parent.getItemAtPosition(position));
                if (loaded>0) {
                    setServingAmount();
                }
            }
            @Override
            public void onNothingSelected(AdapterView <?> parent) {
            }
        });
    }

    /**Saves food from search and drops grams unit*/ //TODO I dont think the grams replacement is needed n/
    //TODO due to the api getting strictly the serving numbers and no longer parsing desc string
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
            AlertDialog.Builder builder = new AlertDialog.Builder(AddFoodFromSearch.this);
            builder.setMessage("Please correctly fill in all of the fields");
            builder.setTitle("Alert !");
            builder.setCancelable(true);
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
    }

    /**Checks all fields of passed in entity are not null or blank*/
    public boolean checkNull(FoodEntity fE) throws NullPointerException, IllegalAccessException {
        for (Field f : fE.getClass().getFields())
            if (f.get(fE).equals("") || f.get(fE) == null)
                return false;
        return true;
    }

    public void getServings(Long id) { //TODO this method provides a food result from the CompactFood id via a second API call
        String key = getString(R.string.KEY);
        String secret = getString(R.string.SECRET);
        requestQueue = Volley.newRequestQueue(this);
        AddFoodFromSearch.Listener listener = new AddFoodFromSearch.Listener();
        req = new Request(key, secret, listener);
        req.getFood(requestQueue, id);
        System.out.println(requestQueue + "\n" + req);
    }

    class Listener implements ResponseListener {
        @Override
        public void onFoodResponse(Food food) { //todo add a list (dict) of servings that the user can choose from
            List<Serving> servings = food.getServings();
            for (Serving s : servings) {
                servingsDict.put(s.getServingDescription() + "F", s.getFat());
                servingsDict.put(s.getServingDescription() + "P", s.getProtein());
                servingsDict.put(s.getServingDescription() + "C", s.getCarbohydrate());
                servingSizes.add(s.getServingDescription());
                if (s.getServingDescription()!=null && description.contains(s.getMeasurementDescription()) && description.contains(String.valueOf(s.getCarbohydrate()))) {
                    String foodCarbs = String.valueOf(s.getCarbohydrate());
                    String foodFat = String.valueOf(s.getFat());
                    String foodProtein = String.valueOf(s.getProtein());

                    newFoodCarbs.setText(foodCarbs);
                    newFoodFat.setText(foodFat);
                    newFoodProtein.setText(foodProtein);
                }
            }
            setAdapter();
        }

        public void setAdapter() {
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(AddFoodFromSearch.this, android.R.layout.simple_spinner_item, servingSizes);
            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            servingSpinner.setAdapter(arrayAdapter);

            servingSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    selectedServingSize = parent.getItemAtPosition(position).toString();

                    if (count <1) {
                        loaded++;
                        servingAmount = 1.0;
                        count ++;
                    }

                    setServingAmount();
                }
                @Override
                public void onNothingSelected(AdapterView <?> parent) {
                }
            });
        }
    }

    public void setServingAmount() {
        String foodCarbs = String.valueOf(servingsDict.get(selectedServingSize +"C"));
        String foodFat = String.valueOf(servingsDict.get(selectedServingSize +"F"));
        String foodProtein = String.valueOf(servingsDict.get(selectedServingSize +"P"));

        newFoodCarbs.setText(String.valueOf(Double.parseDouble(foodCarbs) * servingAmount));
        newFoodFat.setText(String.valueOf(Double.parseDouble(foodFat) * servingAmount));
        newFoodProtein.setText(String.valueOf(Double.parseDouble(foodProtein) * servingAmount));
    }




}