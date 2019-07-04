package com.netproj.moneycalculator;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;


import androidx.appcompat.app.AppCompatActivity;



public class ScreenLoader extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sPref = getSharedPreferences("appPref", MODE_PRIVATE);
        if (sPref.getBoolean("first_start", true)){
            Intent startUp = new Intent(this, StartUp.class);
            startActivity(startUp);
        } else {
            Intent mainActivity = new Intent(this, MainActivity.class);
            startActivity(mainActivity);

        }


        finish();
    }
}
