package com.jldevtech.mactracker.ControllerFolder;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.jldevtech.mactracker.Database.AppDatabase;
import com.jldevtech.mactracker.R;

public class SplashScreen extends AppCompatActivity {

    public static final String EXTRA_MESSAGE = "";
    AppDatabase appDB;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        appDB = AppDatabase.getInstance(getApplicationContext());
        checkFoods();

        HandlerThread handlerThread = new HandlerThread("background-thread");
        handlerThread.start();
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                checkLogin();
                handlerThread.quitSafely();
                finish();
            }

        }, 2000);

    }

        public Runnable checkLogin () {
            SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
            if (pref.getString("username", "") != null && pref.getBoolean("logged", false)) {
                goToMain();
            } else {
                goToLogin();
            }
            return null;
        }

        public void goToMain () {
            Intent i = new Intent(this, MacroInfoScreen.class);
            startActivity(i);
        }

        public void goToLogin () {
            Intent i = new Intent(this, LoginScreen.class);
            startActivity(i);
        }

        public void checkFoods() {
            appDB.foodDao().deleteOldFoods();
        }
}


