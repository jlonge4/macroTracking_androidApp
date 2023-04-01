package com.jldevtech.mactracker.ControllerFolder;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.jldevtech.mactracker.Database.AppDatabase;
import com.jldevtech.mactracker.R;
import com.jldevtech.mactracker.entities.UserEntity;

import java.lang.reflect.Field;

public class EditUser extends AppCompatActivity {
    private EditText weight;
    private EditText height;
    private EditText age;
    private EditText pTarget;
    private EditText cTarget;
    private EditText fTarget;
    String username;
    boolean success;
    private TextView activityText;
    private RadioButton male;
    private RadioButton female;
    private SeekBar seekBar;
    private Button submit;
    public static String userSex = "";
    public static double userActivity;
    AppDatabase appDB;
    int userCalories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appDB = AppDatabase.getInstance(getApplicationContext());
        setContentView(R.layout.edit_settings);
        weight = (EditText) findViewById(R.id.weight);
        height = (EditText) findViewById(R.id.height);
        age = (EditText) findViewById(R.id.age);
        pTarget = (EditText) findViewById(R.id.pTarget);
        fTarget = (EditText) findViewById(R.id.fTarget);
        cTarget = (EditText) findViewById(R.id.cTarget);
        activityText = (TextView)findViewById(R.id.activityText);
        seekBar = (SeekBar)findViewById(R.id.seekBar);
        submit = (Button)findViewById(R.id.submitSettings);
        male = (RadioButton)findViewById(R.id.male);
        female = (RadioButton)findViewById(R.id.female);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                int selection = seekBar.getProgress();
                switch (selection) {
                    case 0:
                        activityText.setText("Little to no activity");
                        userActivity=1.2;
                        break;
                    case 1:
                        activityText.setText("Light: a few activities per week");
                        userActivity=1.375;
                        break;
                    case 2:
                        activityText.setText("Moderate: 3-5 activities per week");
                        userActivity=1.55;
                        break;
                    case 3:
                        activityText.setText("Heavy: 6-7 activities per week");
                        userActivity=1.725;
                        break;
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    public void calculateMacros() throws Exception {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        username = pref.getString("username" , "");

        String userWeightString = weight.getText().toString();
        int userWeight = Integer.parseInt(userWeightString);

        String userHeightString = height.getText().toString();
        int userHeight = Integer.parseInt(userHeightString);

        String userAgeString = age.getText().toString();
        int userAge = Integer.parseInt(userAgeString);
        UserEntity userEntity = new UserEntity();
        if (male.isChecked()) {
            userSex = "male";
            userEntity.setGender(userSex);
            userCalories = (int) (655 + (((4.35 * userWeight) + (4.7 * userHeight)) - (4.7 * userAge)) * userActivity);
        } else if (female.isChecked()) {
            userSex = "female";
            userEntity.setGender(userSex);
            userCalories = (int) ((((10 * (userWeight / 2.2)) + (6.25 * (userHeight * 2.54)) - (5 * userAge)) - 161) * userActivity);
        }

        double pTarg = Double.parseDouble(pTarget.getText().toString());
        double cTarg = Double.parseDouble(cTarget.getText().toString());
        double fTarg = Double.parseDouble(fTarget.getText().toString());
        double checkTargs = pTarg + fTarg + cTarg;

        userEntity.setUserName(username);
        userEntity.setPassword(appDB.userDao().getPassword(username));
        userEntity.setUid(appDB.userDao().getUid(username));
        userEntity.setWeight(userWeightString);
        userEntity.setHeight(userHeightString);
        userEntity.setAge(userAgeString);
        userEntity.setGender(userSex);
        userEntity.setCalories(String.valueOf(userCalories));
        userEntity.setActivity(String.valueOf(userActivity));
        userEntity.setcTarget(cTarg);
        userEntity.setfTarget(fTarg);
        userEntity.setpTarget(pTarg);
        if (checkNull(userEntity) && checkTargs== 100) {
            appDB.userDao().update(userEntity);
            success = true;
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(EditUser.this);
            builder.setMessage("Please correctly fill in all of the fields");
            builder.setTitle("Alert !");
            builder.setCancelable(true);
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
    }

    public void handleSubmission(View view) throws Exception {
            try {
                calculateMacros();
            } catch (Exception e) {
               success = false;
            }
        if (success) {
            Intent i = new Intent(this, MacroInfoScreen.class);
            startActivity(i);
        }
    }

    /**My method to check each field of a class is not an empty string or null */
    public boolean checkNull(UserEntity uE) throws Exception {
        for (Field f : uE.getClass().getFields())
            if (f.get(uE).equals("") || f.get(uE) == null)
                return false;
        return true;
    }
}