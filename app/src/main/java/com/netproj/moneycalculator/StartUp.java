package com.netproj.moneycalculator;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class StartUp extends Activity implements View.OnClickListener {

    Button confirmBalance;
    Button skip;
    Button start;
    EditText balance;
    DBHelper dbHelper;
    boolean addedBalance = false;

    @Override
    protected void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_up);
        dbHelper = new DBHelper(this);
        balance = findViewById(R.id.startUpBalance);
        start = findViewById(R.id.buttonStartUpStart);
        confirmBalance = findViewById(R.id.buttonStartupAddBalance);
        skip = findViewById(R.id.buttonSkip);
        confirmBalance.setOnClickListener(this);
        skip.setOnClickListener(this);
        start.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.buttonStartupAddBalance:{
                if (!addedBalance) {
                    if (balance.getText().length() <= 2 || balance.getText().charAt(0) == '0') {
                        Toast.makeText(this, "Остаток должен быть не меньше 100", Toast.LENGTH_LONG).show();
                        return;
                    }
                    balance.setEnabled(false);
                    confirmBalance.setText(R.string.edit);
                    addedBalance = true;
                    start.setVisibility(View.VISIBLE);
                    skip.setVisibility(View.GONE);
                } else {
                    balance.setEnabled(true);
                    confirmBalance.setText(R.string.confirm);
                    addedBalance = false;
                    start.setVisibility(View.GONE);
                    skip.setVisibility(View.VISIBLE);
                }
                break;
            }
            case R.id.buttonSkip :{
                Intent intent = new Intent(this, MainActivity.class);
                SharedPreferences sPref = getSharedPreferences("appPref", MODE_PRIVATE);
                SharedPreferences.Editor edit = sPref.edit();
                edit.putBoolean("first_start", false);
                edit.apply();
                startActivity(intent);
                finish();
                break;
            }
            case R.id.buttonStartUpStart: {
                if (balance.getText().length() == 0){
                    Toast.makeText(this, "Необходимо ввести остаток!", Toast.LENGTH_LONG).show();
                    return;
                } else if (balance.getText().length() <= 2 || balance.getText().charAt(0) == '0') {
                    Toast.makeText(this, "Остаток должен быть не меньше 100", Toast.LENGTH_LONG).show();
                    return;
                }


                dbHelper.addValueToMonth(1, DBHelper.INCOME, 4, "", Integer.parseInt(balance.getText().toString()));
                Intent intent = new Intent(this, MainActivity.class);
                SharedPreferences sPref = getSharedPreferences("appPref", MODE_PRIVATE);
                SharedPreferences.Editor edit = sPref.edit();
                edit.putBoolean("first_start", false);
                edit.apply();
                startActivity(intent);
                finish();
            }
            default:break;
        }
    }
}
