package com.jldevtech.mactracker.ControllerFolder;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


import androidx.appcompat.app.AppCompatActivity;

import com.jldevtech.mactracker.Database.AppDatabase;
import com.jldevtech.mactracker.R;
import com.jldevtech.mactracker.entities.UserEntity;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;

public class LoginScreen extends AppCompatActivity {
    public static final String EXTRA_MESSAGE = "";
    private EditText username;
    private EditText password;
    private Button newUser;
    private Button loginButton;
    AppDatabase appDB;
    SharedPreferences sp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_login);

        // Insert dummy data
        /**TODO I wonder if this could work by only entering one field of the user class then deleting**/
        appDB = AppDatabase.getInstance(getApplicationContext());
        UserEntity userEntity = new UserEntity();
        userEntity.setUserName("testOne");
        userEntity.setPassword("testOne");
        userEntity.setGender("testOne");
        userEntity.setWeight("testOne");
        userEntity.setHeight("testOne");
        userEntity.setAge("testOne");
        userEntity.setCalories("testOne");
        userEntity.setActivity("testOne");

        appDB.userDao().insertUser(userEntity);
        userEntity = appDB.userDao().getU("testOne");
        appDB.userDao().delete(userEntity);

        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        newUser = (Button) findViewById(R.id.signUp);
        loginButton = (Button) findViewById(R.id.loginButton);
    }


    public void login(View view) throws GeneralSecurityException, IOException {
        loginButton = (Button) findViewById(R.id.loginButton);

        String pwd = password.getText().toString();
        byte[] data = new byte[0];
        try {
            data = pwd.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String base64 = android.util.Base64.encodeToString(data, Base64.DEFAULT);

        Cursor cursor = appDB.query("SELECT * FROM user_table WHERE username = '" + username.getText().toString() + "' AND password ='" + base64 + "';", null);
        if (cursor.moveToFirst()) {
            SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("username", username.getText().toString());
            editor.commit();
            editor.putBoolean("logged", true);
            editor.commit();

            Intent i = new Intent(this, MacroInfoScreen.class);
            startActivity(i);
            finish();

        } else {

            AlertDialog.Builder builder = new AlertDialog.Builder(LoginScreen.this);
            builder.setMessage("No matching account found! Please verify your login information or create an account");
            builder.setTitle("Alert!");
            builder.setCancelable(true);
            AlertDialog alertDialog = builder.create();
            alertDialog.show();

        }
    }

    public void createNewUser(View view) {
            Intent i = new Intent(this, NewUser.class);
            startActivity(i);
            finish();
    }

}